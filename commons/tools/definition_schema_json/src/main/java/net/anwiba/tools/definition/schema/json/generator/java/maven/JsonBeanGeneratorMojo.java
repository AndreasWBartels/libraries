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
package net.anwiba.tools.definition.schema.json.generator.java.maven;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.reference.utilities.FileUtilities;
import net.anwiba.tools.definition.schema.json.generator.java.bean.IOutput;
import net.anwiba.tools.definition.schema.json.generator.java.bean.JsonBeanGeneratorExecutor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    requiresProject = true,
    threadSafe = true)
public class JsonBeanGeneratorMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}",
      required = true,
      readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "${basedir}/src/main/jssd", property = "jssd.sourceDirectory", required = true)
  private File sourceDirectory = null;

  @Parameter(defaultValue = "${project.build.directory}/generated-sources/java-jssd",
      property = "jssd.outputDirectory",
      required = true)
  private File outputDirectory = null;

  @Parameter(defaultValue = "true", property = "jssd.addGeneratedSourceToCompileSourceRoot", required = false)
  private boolean addGeneratedSourceToCompileSourceRoot = true;

  @Parameter(defaultValue = "false", property = "jssd.addJSSDFilesToCompileSourceRoot", required = false)
  private boolean addJSSDFilesToCompileSourceRoot = false;

  @Parameter(required = true)
  private String pakkage;

  @Parameter(required = true)
  private String comment;

  @Component
  private BuildContext buildContext;

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

  public void setSourceDirectory(final File sourceDirectory) throws IOException {
    this.sourceDirectory = sourceDirectory;
    if (isAddJSSDFilesToCompileSourceRootEnabled()
        && !getProject().getCompileSourceRoots().contains(this.sourceDirectory.getPath())) {
      getProject().addCompileSourceRoot(this.sourceDirectory.getPath());
    }
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

  public void setAddJSSDFilesToCompileSourceRoot(final boolean addJSSDFilesToCompileSourceRoot) {
    this.addJSSDFilesToCompileSourceRoot = addJSSDFilesToCompileSourceRoot;
  }

  public boolean isAddJSSDFilesToCompileSourceRootEnabled() {
    return this.addJSSDFilesToCompileSourceRoot;
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
      throw new MojoExecutionException("missing packge name"); //$NON-NLS-1$ equals
    }
    if (!this.sourceDirectory.exists()) {
      getLog().info("Nothing to do"); //$NON-NLS-1$
      return;
    }

    final List<File> sources = getChildren(this.sourceDirectory);
    if (sources.isEmpty()) {
      getLog().info("Nothing to do"); //$NON-NLS-1$
      return;
    }

    if (!this.outputDirectory.exists()) {
      this.outputDirectory.mkdirs();
    }

    if (getProject() != null) {
      if (isAddJSSDFilesToCompileSourceRootEnabled()
          && !getProject().getCompileSourceRoots().contains(this.sourceDirectory.getPath())) {
        getProject().addCompileSourceRoot(this.sourceDirectory.getPath());
      }
      if (isAddGeneratedSourceToCompileSourceRootEnabled()
          && !getProject().getCompileSourceRoots().contains(this.outputDirectory.getPath())) {
        getProject().addCompileSourceRoot(this.outputDirectory.getPath());
      }
    }

    if (!checkUpToDate(sources)) {
      try {
        final JsonBeanGeneratorExecutor excecutor =
            new JsonBeanGeneratorExecutor(this.sourceDirectory, this.pakkage, this.comment, new IOutput() {

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
        excecutor.execute(this.outputDirectory);
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
