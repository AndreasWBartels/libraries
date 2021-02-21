/*
 * #%L
 * anwiba commons advanced
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.tools.icons.generator.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.tools.icons.configuration.GuiIconConfigurationsReader;
import net.anwiba.tools.icons.configuration.IImageExistsValidator;
import net.anwiba.tools.icons.configuration.IOutput;
import net.anwiba.tools.icons.configuration.IconResource;
import net.anwiba.tools.icons.configuration.ImageExistsValidator;
import net.anwiba.tools.icons.generator.GuiIconsClassWriter;
import net.anwiba.tools.icons.schema.configuration.Class;

@SuppressWarnings("nls")
@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    requiresProject = true,
    threadSafe = true)
public class GuiIconsGeneratorMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}",
      required = true,
      readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "${basedir}/src/main/resources/icons.xml",
      property = "guiicons.sourceFile",
      required = true)
  private File sourceFile = null;

  @Parameter(defaultValue = "${project.build.directory}/generated-sources/java-icons",
      property = "guiicons.outputDirectory",
      required = true)
  private File outputDirectory = null;

  @Parameter(defaultValue = "true", property = "guiicons.addGeneratedSourceToCompileSourceRoot", required = false)
  private boolean addGeneratedSourceToCompileSourceRoot = true;

  @Parameter(defaultValue = "true")
  private boolean force;

  @Parameter(defaultValue = "net.anwiba.commons.swing.icon.GuiIcon",
      property = "guiicons.iconClass",
      required = true)
  private String iconClass;

  @Parameter(defaultValue = "")
  private String comment;

  @Component
  private BuildContext buildContext;

  public void setForce(final boolean force) {
    this.force = force;
  }

  public void setIconClass(final String iconClass) {
    if (iconClass == null || iconClass.trim().length() == 0) {
      this.iconClass = null;
      return;
    }
    this.iconClass = iconClass;
  }

  public void setSourceFile(final File sourceFile) throws IOException {
    this.sourceFile = sourceFile;
  }

  public void setComment(final String comment) {
    Ensure.ensureArgumentNotNull(comment);
    this.comment = comment;
  }

  public void setOutputDirectory(final File outputDirectory) throws IOException {
    this.outputDirectory = outputDirectory;
  }

  public void setAddGeneratedSourceToCompileSourceRoot(final boolean addCompileSourceRoot) {
    this.addGeneratedSourceToCompileSourceRoot = addCompileSourceRoot;
  }

  public boolean isAddGeneratedSourceToCompileSourceRootEnabled() {
    return this.addGeneratedSourceToCompileSourceRoot;
  }

  public MavenProject getProject() {
    return this.project;
  }

  public void setProject(final MavenProject project) {
    this.project = project;
  }

  private File getFile(final String fileName) throws IOException {
    final File file = new File(fileName);
    if (file.isAbsolute()) {
      return file.getCanonicalFile();
    }
    final File projectPath = getBasePath();
    return new File(projectPath, fileName).getCanonicalFile();
  }

  private File getBasePath() {
    if (this.project != null) {
      return this.project.getBasedir();
    }
    if (getPluginContext() == null) {
      throw new RuntimeException("missing plugin context");
    }
    if (getPluginContext().get("project") == null) {
      throw new RuntimeException("missing project info in plugin context");
    }
    final String basedirString = getPluginContext().get("project").toString(); //$NON-NLS-1$
    final File projectPath = new File(basedirString);
    return projectPath;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Executing ..."); //$NON-NLS-1$
    if (this.outputDirectory == null) {
      throw new MojoExecutionException("missing target path value"); //$NON-NLS-1$
    }
    if (this.sourceFile == null) {
      throw new MojoExecutionException("missing source file value"); //$NON-NLS-1$
    }
    if (!this.sourceFile.exists()) {
      getLog().info("Nothing to do"); //$NON-NLS-1$
      return;
    }

    if (!this.outputDirectory.exists()) {
      this.outputDirectory.mkdirs();
    }
    if (getProject() != null && isAddGeneratedSourceToCompileSourceRootEnabled()
        && !getProject().getCompileSourceRoots().contains(this.outputDirectory.getPath())) {
      getProject().addCompileSourceRoot(this.outputDirectory.getPath());
    }

    final List<File> sources = getChildren(this.sourceFile.getParentFile());
    if (checkUpToDate(sources)) {
      getLog().info("Everything is up to date"); //$NON-NLS-1$
      return;
    }
    try {
      final IOutput output = new IOutput() {

        @Override
        public void warn(final String message) {
          getLog().warn(message);
        }

        @Override
        public void info(final String message) {
          getLog().info(message);
        }

        @Override
        public void error(final String message, final Throwable throwable) {
          getLog().error(message, throwable);
        }

        @Override
        public void error(final String message) {
          getLog().error(message);
        }
      };
      final ArrayList<File> resources = new ArrayList<>();
      final IImageExistsValidator imageExistsValidator = new ImageExistsValidator(resources, output);
      final GuiIconConfigurationsReader reader =
          new GuiIconConfigurationsReader(imageExistsValidator, output, this.force);
      getLog().info(MessageFormat.format("add file: {0}", this.sourceFile.getCanonicalPath())); //$NON-NLS-1$
      reader.add(this.sourceFile);
      final Class targetClazz = reader.getClazz();
      getLog().info("class:   " + targetClazz.getName());
      getLog().info("comment: " + this.comment);
      final File targetFile = createTargetFile(targetClazz);
      if (!targetFile.exists()) {
        getLog().info(MessageFormat.format("create file: {0}", targetFile.getCanonicalPath())); //$NON-NLS-1$
        targetFile.getParentFile().mkdirs();
      }
      getLog().info(MessageFormat.format("create class: {0}", this.iconClass));
      targetFile.createNewFile();
      try (FileWriter fileWriter = new FileWriter(targetFile)) {
        try (GuiIconsClassWriter writer = new GuiIconsClassWriter(
            fileWriter,
            getClass(
                this.iconClass == null
                    ? "net.anwiba.commons.swing.icon.GuiIcon"
                    : this.iconClass),
            targetClazz,
            this.comment,
            output)) {
          final Map<String, IconResource> iconConfigurations = reader.getIconConfigurations();
          final Map<String, String> folders = reader.getFolders();
          writer.write(folders, iconConfigurations);
        }
      }
    } catch (final Exception exception) {
      getLog().error(exception);
      throw new MojoExecutionException("Internal exception", exception); //$NON-NLS-1$
    }
  }

  private Class getClass(final String iconClassName) {
    try {
      final String packageName = extractPackageName(iconClassName);
      final String className = extractClassName(iconClassName);
      @SuppressWarnings("hiding")
      final Class iconClass = new Class();
      iconClass.setPackage(packageName);
      iconClass.setName(className);
      return iconClass;
    } catch (final RuntimeException throwable) {
      throwable.printStackTrace();
      throw throwable;
    }
  }

  private String extractClassName(final String absoluteClassName) {
    final int index = absoluteClassName.lastIndexOf('.');
    if (index == -1) {
      return absoluteClassName;
    }
    return absoluteClassName.substring(index + 1);
  }

  private String extractPackageName(final String absoluteClassName) {
    final int index = absoluteClassName.lastIndexOf('.');
    if (index == -1) {
      throw new IllegalArgumentException("illegal classname, icon class name without package"); //$NON-NLS-1$
    }
    return absoluteClassName.substring(0, index);
  }

  private File createTargetFile(final Class targetClazz) throws IOException {
    final String fileName = getFileName(targetClazz);
    if (fileName == null) {
      throw new IllegalArgumentException("no file target");
    }
    if (this.outputDirectory == null) {
      this.outputDirectory = getFile("target/generated/java");
    }
    if (!(this.outputDirectory.exists() || this.outputDirectory.mkdirs())) {
      throw new IllegalStateException("missing output directory"); //$NON-NLS-1$
    }
    return new File(this.outputDirectory, fileName);
  }

  private String getFileName(final Class clazz) {
    if (clazz == null) {
      return null;
    }
    return MessageFormat.format("{0}{1}{2}.java", clazz.getPackage().replace('.', '/'), "/", clazz.getName()); //$NON-NLS-1$//$NON-NLS-2$
  }

  private boolean checkUpToDate(final List<File> sourceFiles) {
    boolean uptodate = true;
    for (final File file : sourceFiles) {
      if (this.buildContext.hasDelta(file)) {
        uptodate = false;
        break;
      }
    }
    return uptodate;
  }

  private List<File> getChildren(final File directory) {
    final List<File> children = new ArrayList<>();
    final File[] sourceFiles = directory.listFiles();
    if (sourceFiles == null) {
      return children;
    }
    for (final File file : sourceFiles) {
      if (file.isDirectory()) {
        children.addAll(getChildren(file));
      } else {
        children.add(file);
      }
    }
    return children;
  }
}
