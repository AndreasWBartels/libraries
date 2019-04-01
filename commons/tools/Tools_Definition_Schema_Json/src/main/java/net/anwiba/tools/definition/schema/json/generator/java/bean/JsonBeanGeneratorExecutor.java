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
package net.anwiba.tools.definition.schema.json.generator.java.bean;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.reference.utilities.FileUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.tools.definition.schema.json.gramma.parser.JssdParserException;

@SuppressWarnings("nls")
public class JsonBeanGeneratorExecutor {

  private final String pakkage;
  private final String comment;
  private final File source;
  private final IOutput output;

  public JsonBeanGeneratorExecutor(
      final File source,
      final String pakkage,
      final String comment,
      final IOutput output) {
    this.output = output;
    output.info("source: " + source);
    this.source = source;
    output.info("package: " + pakkage);
    this.pakkage = pakkage;
    output.info("comment: " + comment);
    this.comment = comment;
  }

  public void excecute(final File target) throws IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(this.pakkage, this.comment, false);
    final String path = this.pakkage.replace('.', File.separatorChar);
    final File packageFolder = new File(this.source, path);
    this.output.info("package folder: " + packageFolder);
    final File[] files = packageFolder.listFiles(new FileFilter() {

      @Override
      public boolean accept(final File pathname) {
        if (pathname.getName().startsWith(".")) {
          return false;
        }
        final String extension = FileUtilities.getExtension(pathname);
        if (StringUtilities.isNullOrTrimmedEmpty(extension)) {
          return false;
        }
        return extension.equalsIgnoreCase("jssd"); //$NON-NLS-1$
      }
    });
    if (files == null || files.length == 0) {
      this.output.warn("Found no schema definition files: " + packageFolder);
      return;
    }
    final List<Throwable> throwables = new ArrayList<>();
    for (final File file : files) {
      try (final FileInputStream inputStream = new FileInputStream(file)) {
        this.output.info("schema definition: " + file);
        generator.add(inputStream, FileUtilities.getFileWithoutExtension(file).getName());
      } catch (final FileNotFoundException exception) {
        final String message = "Couldn't find file: " + file;
        this.output.error(message, exception);
        throwables.add(new IOException(message, exception));
      } catch (final SecurityException exception) {
        final String message = "Couldn't read file: " + file + ", because " + exception.getMessage();
        this.output.error(message, exception);
        throwables.add(new IOException(message, exception));
      } catch (final JssdParserException exception) {
        final String message = exception.getMessage() + ", in file: " + file;
        this.output.error(message, exception);
        throwables.add(new IOException(message, exception));
      } catch (final CreationException exception) {
        throwables.add(exception);
        final String message = "Couldn't create bean description from file: "
            + file
            + ", because: "
            + exception.getMessage();
        this.output.error(message, exception);
        throwables.add(new CreationException(message, exception));
      }
    }
    this.output.info("target: " + target);
    try {
      generator.generate(target);
    } catch (final IOException | SecurityException exception) {
      this.output.error("Error writing target to: " + target + ", because: " + exception.getMessage(), exception);
      throwables.add(exception);
    }
    throwException(throwables);
  }

  public void throwException(final List<Throwable> throwables) throws IOException {
    final IOException exception = (IOException) throwables.stream().reduce((i, e) -> {
      if (i instanceof IOException) {
        i.addSuppressed(e);
        return i;
      }
      final IOException ex = new IOException(i.getMessage(), i);
      ex.addSuppressed(e);
      return ex;
    }).orElse(null);
    if (exception == null) {
      return;
    }
    throw exception;
  }
}
