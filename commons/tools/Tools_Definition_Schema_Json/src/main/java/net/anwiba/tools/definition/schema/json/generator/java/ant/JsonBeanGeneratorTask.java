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
package net.anwiba.tools.definition.schema.json.generator.java.ant;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.tools.definition.schema.json.generator.java.bean.IOutput;
import net.anwiba.tools.definition.schema.json.generator.java.bean.JsonBeanGeneratorExecutor;

public class JsonBeanGeneratorTask extends Task {

  private File destination = null;
  private File source = null;
  private String pakkage;
  private String comment;

  public void setPackage(final String source) {
    Ensure.ensureArgumentIsNotEmpty(source);
    this.pakkage = source;
  }

  public void setComment(final String source) {
    Ensure.ensureArgumentIsNotEmpty(source);
    this.comment = source;
  }

  public void setSource(final String source) throws IOException {
    if (source == null || source.trim().length() == 0) {
      this.source = null;
      return;
    }
    this.source = getFile(source);
  }

  public void setDestination(final String destination) throws IOException {
    if (destination == null || destination.trim().length() == 0) {
      this.destination = null;
      return;
    }
    this.destination = getFile(destination);
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
    final String basedirString = getProject().getProperty("basedir"); //$NON-NLS-1$
    final File projectPath = new File(basedirString);
    return projectPath;
  }

  @Override
  public void execute() throws BuildException {
    if (this.destination == null) {
      throw new BuildException("missing target path value"); //$NON-NLS-1$
    }
    if (this.source == null) {
      throw new BuildException("missing source folder value"); //$NON-NLS-1$
    }
    if (this.pakkage == null) {
      throw new BuildException("missing packge name"); //$NON-NLS-1$
    }
    try {
      final JsonBeanGeneratorExecutor excecutor = new JsonBeanGeneratorExecutor(
          this.source,
          this.pakkage,
          this.comment,
          new IOutput() {

            @Override
            public void warn(final String message) {
              getProject().log(message, Project.MSG_WARN);
            }

            @Override
            public void info(final String message) {
              getProject().log(message, Project.MSG_INFO);
            }

            @Override
            public void error(final String message, final Throwable throwable) {
              getProject().log(message, throwable, Project.MSG_ERR);
              throwable.printStackTrace();
            }
          });
      excecutor.excecute(this.destination);
    } catch (final Throwable exception) {
      exception.printStackTrace();
      throw new BuildException(exception);
    }
  }
}
