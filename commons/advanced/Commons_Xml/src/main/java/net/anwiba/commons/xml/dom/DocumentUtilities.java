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
package net.anwiba.commons.xml.dom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

public class DocumentUtilities {

  public static String getDefaultEncoding() {
    return "UTF-8"; //$NON-NLS-1$
  }

  private DocumentUtilities() {
    throw new UnreachableCodeReachedException();
  }

  public static void save(final Document document, final File file) throws IOException {
    try (OutputStream outputStream = new FileOutputStream(file);) {
      save(document, outputStream);
    }
  }

  public static String toString(final Document document) {
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      save(document, new XMLWriter(outputStream, createTrimedOutputFormat(getDefaultEncoding())));
      return outputStream.toString();
    } catch (final IOException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  private static OutputFormat createTrimedOutputFormat(final String encoding) {
    final OutputFormat format = OutputFormat.createCompactFormat();
    format.setEncoding(encoding);
    return format;
  }

  public static void save(final Document document, final OutputStream outputStream) throws IOException {
    save(document, new XMLWriter(outputStream, createOutputFormat()));
  }

  private static void save(final Document document, final XMLWriter writer) throws IOException {
    try {
      writer.write(document);
      writer.flush();
    } catch (final UnsupportedEncodingException e) {
      throw new IOException(e.getMessage());
    }
  }

  private static OutputFormat createOutputFormat() {
    return createOutputFormat(getDefaultEncoding());
  }

  private static OutputFormat createOutputFormat(final String encoding) {
    final OutputFormat format = OutputFormat.createPrettyPrint();
    format.setEncoding(encoding);
    return format;
  }

  private static Document read(final InputSource source) throws IOException {
    try {
      final SAXReader saxReader = new SAXReader();
      return saxReader.read(source);
    } catch (final DocumentException exception) {
      throw new IOException(exception);
    }
  }

  public static Document read(final File file) throws IOException {
    try (InputStream inputStream = new FileInputStream(file);) {
      final InputSource inputSource = new InputSource(inputStream);
      inputSource.setEncoding(getDefaultEncoding());
      return read(inputSource);
    } catch (final FileNotFoundException e) {
      throw e;
    } catch (final Exception e) {
      throw new IOException("Error reading xml document from file '" + file.getAbsolutePath() + "'", e); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static Document read(final InputStream in) throws IOException {
    return read(in, null);
  }

  public static Document read(final InputStream inputStream, final String systemId) throws IOException {
    try {
      return new SAXReader().read(inputStream, systemId);
    } catch (final DocumentException exception) {
      throw new IOException(exception);
    }
  }
}