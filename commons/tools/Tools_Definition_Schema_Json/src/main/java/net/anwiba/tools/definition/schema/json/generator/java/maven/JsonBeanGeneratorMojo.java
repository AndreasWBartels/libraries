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
package net.anwiba.tools.definition.schema.json.generator.java.maven;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.reference.utilities.FileUtilities;
import net.anwiba.tools.definition.schema.json.generator.java.bean.IOutput;
import net.anwiba.tools.definition.schema.json.generator.java.bean.JsonBeanGeneratorExecutor;

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
public class JsonBeanGeneratorMojo extends AbstractMojo {

  @Parameter(defaultValue = "${basedir}/src/main/schema")
  private File sourceDirectory = null;

  @Parameter(defaultValue = "${basedir}/target/generated/java")
  private File outputDirectory = null;

  @Parameter
  private String pakkage;

  @Parameter
  private String comment;

  @Component
  private BuildContext buildContext;

  @Parameter
  private MavenProject project;

  public MavenProject getProject() {
    return this.project;
  }

  public void setProject(final MavenProject project) {
    this.project = project;
  }

  public void setPackage(final String pakkage) {
    Ensure.ensureArgumentNotNull(pakkage);
    this.pakkage = pakkage;
  }

  public void setComment(final String comment) {
    Ensure.ensureArgumentNotNull(comment);
    this.comment = "//" + comment;
  }

  public void setSourceDirectory(final String sourceDirectory) throws IOException {
    if (sourceDirectory == null || sourceDirectory.trim().length() == 0) {
      this.sourceDirectory = null;
      return;
    }
    this.sourceDirectory = getFile(sourceDirectory);
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
    if (this.sourceDirectory == null) {
      throw new MojoExecutionException("missing source folder value"); //$NON-NLS-1$
    }
    if (this.pakkage == null) {
      throw new MojoExecutionException("missing packge name"); //$NON-NLS-1$equals
    }
    final List<File> sources = getChildren(this.sourceDirectory);

    if (!checkUpToDate(sources)) {
      try {
        final JsonBeanGeneratorExecutor excecutor = new JsonBeanGeneratorExecutor(this.sourceDirectory, this.pakkage, this.comment, new IOutput() {

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
        });
        excecutor.excecute(this.outputDirectory);
        this.buildContext.refresh(this.outputDirectory);
      } catch (final Exception e) {
        throw new MojoExecutionException("Internal exception", e); //$NON-NLS-1$
      }
    } else {
      getLog().info("Everything is up to date"); //$NON-NLS-1$
    }

  }

  private List<File> getChildren(final File directory) {
    final List<File> children = new ArrayList<>();
    final File[] sourceFiles = directory.listFiles(new FileFilter() {

      @Override
      public boolean accept(final File pathname) {
        if (pathname.isDirectory()) {
          return true;
        }
        return "jssd".equalsIgnoreCase(FileUtilities.getExtension(pathname)); //$NON-NLS-1$
      }
    });
    for (final File file : sourceFiles) {
      if (file.isDirectory()) {
        children.addAll(getChildren(file));
      } else {
        children.add(file);
      }
    }
    return children;
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

}
