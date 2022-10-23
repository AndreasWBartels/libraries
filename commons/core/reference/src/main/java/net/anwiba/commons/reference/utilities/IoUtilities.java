/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.reference.utilities;

import net.anwiba.commons.lang.exception.Throwables;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class IoUtilities {

  private static final int BUFFER_SIZE = 4096;
  private static ILogger logger = Logging.getLogger(IoUtilities.class.getName());

  public static ByteArrayInputStream copy(final InputStream inputStream) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    pipe(inputStream, outputStream);
    return new ByteArrayInputStream(outputStream.toByteArray());
  }

  public static void pipe(final InputStream in, final OutputStream out) throws IOException {
    pipe(in, out, 4096);
  }

  public static void pipe(final InputStream in, final OutputStream out, final int bufferSize) throws IOException {
    pipe(in, out, bufferSize, -1);
  }

  public static void pipe(final InputStream in, final OutputStream out, final int bufferSize, final long numberOfBytes)
      throws IOException {
    final int arraysize = numberOfBytes > -1 && bufferSize > numberOfBytes ? (int) numberOfBytes : bufferSize;
    byte[] buffer = new byte[arraysize];
    long readed = 0;
    int numChars;
    while ((numChars = in.read(buffer)) > 0) {
      readed += numChars;
      out.write(buffer, 0, numChars);
      if (numberOfBytes > -1 && readed + bufferSize > numberOfBytes) {
        buffer = new byte[(int) (numberOfBytes - readed)];
      }
    }
  }

  public static void pipe(final File sourceFile, final File targetFile)
      throws IOException,
      FileNotFoundException {
    try (FileInputStream inputStream = new FileInputStream(sourceFile)) {
      try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
        IoUtilities.pipe(inputStream, outputStream);
      }
    }
  }

  public static void pipe(final Readable reader, final Writer writer) throws IOException {
    pipe(reader, writer, BUFFER_SIZE);
  }

  public static void pipe(final Readable in, final Writer out, final int bufferSize) throws IOException {
    final CharBuffer buffer = CharBuffer.allocate(bufferSize);
    while ((in.read(buffer)) > 0) {
      buffer.flip();
      while (buffer.hasRemaining()) {
        final char c = buffer.get();
        out.append(c);
      }
      out.flush();
      buffer.clear();
    }
  }

  public static void pipe(final Readable in, final Writer out, final int bufferSize, final long numberOfCharacters)
      throws IOException {
    CharBuffer buffer = CharBuffer.allocate(bufferSize);
    long readed = 0;
    int numChars;
    while ((numChars = in.read(buffer)) > 0) {
      readed += numChars;
      buffer.flip();
      while (buffer.hasRemaining()) {
        final char c = buffer.get();
        out.append(c);
      }
      out.flush();
      if (numberOfCharacters > -1 && readed + bufferSize > numberOfCharacters) {
        buffer = CharBuffer.allocate((int) (numberOfCharacters - readed));
      }
      buffer.clear();
    }
  }

  public static String toString(final Reader reader) throws IOException {
    final StringWriter writer = new StringWriter();
    pipe(reader, writer);
    return writer.toString();
  }

  public static String toString(final Reader reader, final int numberOfCharacters) throws IOException {
    final StringWriter writer = new StringWriter();
    pipe(reader, writer, BUFFER_SIZE, numberOfCharacters);
    return writer.toString();
  }

  public static IOException close(final AutoCloseable closeable) {
    try {
      Optional.of(Exception.class, closeable).consume(AutoCloseable::close).get();
      return null;
    } catch (final Exception e) {
      return asIOException(e);
    }
  }

  public static IOException close(final Closeable closeable, final Closeable other, final Closeable... others) {
    List<Throwable> throwables = new LinkedList<>();
    Optional.of(close(closeable)).consume(throwables::add);
    Optional.of(close(other)).consume(throwables::add);
    for (Closeable value : others) {
      Optional.of(close(value)).consume(throwables::add);
    }
    return Throwables.concat(IoUtilities::asIOException, throwables);
  }

  public static void closeAndThrow(final Closeable closeable, final Closeable other, final Closeable... others)
      throws IOException {
    IOException exception = close(closeable, other, others);
    throwIfNotNull(exception);
  }

  public static IOException close(final IOException exception, final AutoCloseable closeable) {
    return Throwables.concat(IoUtilities::asIOException, exception, close(closeable));
  }

  public static void throwIfNotNull(final Throwable throwable) throws IOException {
    Throwables.throwIfNotNull(IoUtilities::asIOException, throwable);
  }

  public static void throwIfNotEmpty(final List<Throwable> throwables) throws IOException {
    Throwables.throwIfNotEmpty(t -> asIOException(t), throwables);
  }

  public static IOException asIOException(final Throwable throwable) {
    return Optional.of(throwable)
        .convert(e -> (e instanceof IOException ioe) ? ioe : new IOException(e.getMessage(), e))
        .get();
  }

  public static IOException execute(final IOException exception, final IBlock<IOException> block) {
    if (block == null) {
      return exception;
    }
    try {
      block.execute();
      return exception;
    } catch (final IOException ioException) {
      return Throwables.concat(t -> asIOException(t), exception, ioException);
    }
  }

  public static String toString(final InputStream inputStream, final String contentEncoding, final long numberOfBytes)
      throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    IoUtilities.pipe(inputStream, out, BUFFER_SIZE, numberOfBytes);
    return out.toString(contentEncoding);
  }

  public static String toString(final InputStream inputStream, final String contentEncoding) throws IOException {
    return toString(inputStream, contentEncoding, -1);
  }

  public static byte[] toByteArray(final InputStream inputStream) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    IoUtilities.pipe(inputStream, out, BUFFER_SIZE, -1);
    return out.toByteArray();
  }

  public static int maximumLimitOfBytes(final long contentLength) {
    if (contentLength > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    return maximumLimitOfBytes((int) contentLength);
  }

  public static int maximumLimitOfBytes(final int contentLength) {
    if (contentLength < 0) {
      return Integer.MAX_VALUE;
    }
    return contentLength + 1;
  }

  public static void pipe(final InputStream inputStream, final File file) throws IOException {
    try (final OutputStream outputStream = new FileOutputStream(file)) {
      pipe(inputStream, outputStream);
    }
  }

  public static boolean contentEquals(final File file, final File other) throws FileNotFoundException, IOException {
    if (Objects.equals(file, other)) {
      return true;
    }
    if (file == null || other == null) {
      return false;
    }
    if (file.isDirectory() || other.isDirectory()) {
      return false;
    }
    if (file.exists() && other.exists()) {
      if (file.length() != other.length()) {
        return false;
      }
      try (InputStream inputStream = new FileInputStream(file)) {
        try (InputStream otherInputStream = new FileInputStream(other)) {
          if (inputStream.read() != otherInputStream.read()) {
            return false;
          }
        }
      }
      return true;
    } else {
      return false;
    }
  }
}
