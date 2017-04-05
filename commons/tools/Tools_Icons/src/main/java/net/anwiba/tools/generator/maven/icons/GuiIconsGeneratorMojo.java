/*
 * #%L anwiba commons tools %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.tools.generator.maven.icons;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.anwiba.tools.icons.configuration.GuiIconConfigurationsReader;
import net.anwiba.tools.icons.configuration.IImageExistsValidator;
import net.anwiba.tools.icons.configuration.IOutput;
import net.anwiba.tools.icons.configuration.IconResource;
import net.anwiba.tools.icons.configuration.ImageExistsValidator;
import net.anwiba.tools.icons.configuration.generated.Class;
import net.anwiba.tools.icons.generator.GuiIconsClassWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

@SuppressWarnings("nls")
@Mojo(name = "generate")
public class GuiIconsGeneratorMojo extends AbstractMojo {

  @Parameter(defaultValue = "${basedir}/src/main/resources/icons/icons.xml")
  private File sourceFile = null;

  @Parameter(defaultValue = "${basedir}/target/generated/java")
  private File outputDirectory = null;

  @Parameter(defaultValue = "true")
  private boolean force;

  @Parameter(defaultValue = "net.anwiba.commons.swing.icon.GuiIcon")
  private String iconClass;

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

  public void setSource(final String source) throws IOException {
    if (source == null || source.trim().length() == 0) {
      this.sourceFile = null;
      return;
    }
    this.sourceFile = getFile(source);
  }

  public void setOutputDirectory(final String outputDirectory) throws IOException {
    if (outputDirectory == null || outputDirectory.trim().length() == 0) {
      this.outputDirectory = null;
      return;
    }
    this.outputDirectory = getFile(outputDirectory);
    if (!this.outputDirectory.exists()) {
      this.outputDirectory.mkdirs();
    }
  }

  @Parameter
  private MavenProject project;

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
      final GuiIconConfigurationsReader reader = new GuiIconConfigurationsReader(
          imageExistsValidator,
          output,
          this.force);
      getLog().info(MessageFormat.format("add file: {0}", this.sourceFile.getCanonicalPath())); //$NON-NLS-1$
      reader.add(this.sourceFile);
      final Class targetClazz = reader.getClazz();
      getLog().info("class:   " + targetClazz.getName());
      final File targetFile = createTargetFile(targetClazz);
      if (!targetFile.exists()) {
        getLog().info(MessageFormat.format("create file: {0}", targetFile.getCanonicalPath())); //$NON-NLS-1$
        targetFile.getParentFile().mkdirs();
      }
      getLog().info(MessageFormat.format("create class: {0}", this.iconClass));
      targetFile.createNewFile();
      try (FileWriter fileWriter = new FileWriter(targetFile)) {
        try (GuiIconsClassWriter writer = new GuiIconsClassWriter(fileWriter, getClass(this.iconClass == null
            ? "net.anwiba.commons.swing.icon.GuiIcon"
            : this.iconClass), targetClazz, output)) {
          final Map<String, IconResource> iconConfigurations = reader.getIconConfigurations();
          final Map<String, String> folders = reader.getFolders();
          writer.write(folders, iconConfigurations);
        }
      }
    } catch (final Exception exception) {
      getLog().warn(exception);
      throw new MojoExecutionException("Internal exception", exception); //$NON-NLS-1$
    }
  }

  private Class getClass(final String iconClassName) {
    try {
      final String packageName = extractPackageName(iconClassName);
      final String className = extractClassName(iconClassName);
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

  private File createTargetFile(final Class targetClazz) {
    final String fileName = getFileName(targetClazz);
    if (fileName == null) {
      throw new IllegalArgumentException("no file target");
    }
    if (this.outputDirectory == null) {
      return new File(new File(getBasePath(), "target/generated/java"), fileName);
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
