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
package net.anwiba.tools.icons.generator.ant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import net.anwiba.tools.icons.configuration.GuiIconConfigurationsReader;
import net.anwiba.tools.icons.configuration.IImageExistsValidator;
import net.anwiba.tools.icons.configuration.IOutput;
import net.anwiba.tools.icons.configuration.IconRecourceSearcher;
import net.anwiba.tools.icons.configuration.IconResource;
import net.anwiba.tools.icons.configuration.ImageExistsValidator;
import net.anwiba.tools.icons.generator.GuiIconsClassWriter;
import net.anwiba.tools.icons.schema.configuration.Class;

@SuppressWarnings("nls")
public class GuiIconsGeneratorTask extends Task {

  private File source = null;
  private File targetFile = null;

  private boolean force = true;
  private boolean aggregate = false;
  private Class iconClass;

  public void setIconclass(final String iconClassName) {
    try {
      System.out.println(iconClassName);
      final String packageName = extractPackageName(iconClassName);
      final String className = extractClassName(iconClassName);
      this.iconClass = new Class();
      this.iconClass.setPackage(packageName);
      this.iconClass.setName(className);
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

  public void setForce(final boolean force) {
    this.force = force;
  }

  public void setAggregate(final boolean aggregate) {
    this.aggregate = aggregate;
  }

  public void setSource(final String source) throws IOException {
    if (source == null || source.trim().length() == 0) {
      this.source = null;
      return;
    }
    this.source = getFile(source);
  }

  public void setTarget(final String target) throws IOException {
    if (target == null || target.trim().length() == 0) {
      this.targetFile = null;
      return;
    }
    this.targetFile = getFile(target);
  }

  private File getFile(final String fileName) throws IOException {
    final File file = new File(fileName);
    if (file.isAbsolute()) {
      return file.getCanonicalFile();
    }
    final String basedirString = getProject().getProperty("basedir"); //$NON-NLS-1$
    final File projectPath = new File(basedirString);
    return new File(projectPath, fileName).getCanonicalFile();
  }

  final IOutput output = new IOutput() {

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
    }

    @Override
    public void error(final String message) {
      getProject().log(message, Project.MSG_ERR);
    }
  };

  @Override
  public void execute() throws BuildException {
    if (this.iconClass == null) {
      throw new BuildException("missing icon class name"); //$NON-NLS-1$
    }
    try {
      this.output.info("source: " + this.source);
      this.output.info("target: " + this.targetFile);
      final String basedirString = getProject().getProperty("basedir"); //$NON-NLS-1$
      this.output.info("path:   " + basedirString);
      final File projectPath = new File(basedirString);
      final List<File> list = getFileList(projectPath);
      final List<File> imageResources = new ArrayList<>();
      final IImageExistsValidator imageExistsValidator = new ImageExistsValidator(imageResources, this.output);
      final GuiIconConfigurationsReader reader = new GuiIconConfigurationsReader(
          imageExistsValidator,
          this.output,
          this.force);
      for (final File file : list) {
        this.output.info(MessageFormat.format("add file: {0}", file.getCanonicalPath())); //$NON-NLS-1$
        reader.add(file);
      }
      final Class targetClazz = reader.getClazz();
      this.output.info("class:   " + targetClazz.getName());
      @SuppressWarnings("hiding")
      final File targetFile = createTargetFile(projectPath, targetClazz);
      if (!targetFile.exists()) {
        this.output.info(MessageFormat.format("create file: {0}", targetFile.getCanonicalPath())); //$NON-NLS-1$
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();
      }
      try (FileWriter fileWriter = new FileWriter(targetFile)) {
        this.output.info(MessageFormat.format("write file: {0}", targetFile.getCanonicalPath())); //$NON-NLS-1$
        try (GuiIconsClassWriter writer = new GuiIconsClassWriter(
            fileWriter,
            this.iconClass,
            targetClazz,
            "// Copyright (c) ${year} by Andreas W. Bartels (bartels@anwiba.net)\n", //$NON-NLS-1$
            this.output)) {
          final Map<String, IconResource> iconConfigurations = reader.getIconConfigurations();
          final Map<String, String> folders = reader.getFolders();
          writer.write(folders, iconConfigurations);
        }
      }
      this.output.info(MessageFormat.format("wrote file: {0}", targetFile.getCanonicalPath())); //$NON-NLS-1$
    } catch (final Throwable exception) {
      this.output.error(exception.getMessage());
      exception.printStackTrace();
      throw new BuildException(exception);
    }
  }

  private File createTargetFile(final File projectPath, final Class targetClazz) {
    final String fileName = getFileName(targetClazz);
    if (fileName == null) {
      throw new IllegalArgumentException("no file target");
    }
    if (this.targetFile == null) {
      return new File(new File(projectPath, "generated"), fileName);
    }
    return new File(this.targetFile, fileName);
  }

  private String getFileName(final Class clazz) {
    if (clazz == null) {
      return null;
    }
    return MessageFormat.format("{0}{1}{2}.java", clazz.getPackage().replace('.', '/'), "/", clazz.getName()); //$NON-NLS-1$//$NON-NLS-2$
  }

  private List<File> getFileList(final File projectPath) throws IOException {
    if (this.source == null || this.source.isDirectory()) {
      final IconRecourceSearcher searcher = new IconRecourceSearcher(this.output, this.aggregate);
      final File sourcePath = this.source == null ? projectPath : this.source;
      this.output.info(MessageFormat.format("search file: {0}", sourcePath.getCanonicalPath())); //$NON-NLS-1$
      return searcher.search(sourcePath);
    }
    return Arrays.asList(new File[] { this.source });
  }
}
