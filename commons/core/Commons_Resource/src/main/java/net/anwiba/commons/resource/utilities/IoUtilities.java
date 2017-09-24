/*
 * #%L anwiba commons core %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.commons.resource.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.CharBuffer;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

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

  public static void pipe(final Readable reader, final Appendable writer) throws IOException {
    pipe(reader, writer, BUFFER_SIZE);
  }

  public static void pipe(final Readable in, final Appendable out, final int bufferSize) throws IOException {
    final CharBuffer buffer = CharBuffer.allocate(bufferSize);
    int numChars;
    while ((numChars = in.read(buffer)) > -1) {
      out.append(buffer, 0, numChars);
    }
  }

  public static void close(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      }
    }
  }

  public static IOException close(final Closeable closeable, final IOException exception) {
    if (closeable == null) {
      return exception;
    }
    try {
      closeable.close();
      return exception;
    } catch (final IOException ioException) {
      if (exception == null) {
        return ioException;
      }
      exception.addSuppressed(ioException);
      return exception;
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

  public static String toString(final Reader reader) throws IOException {
    final StringBuffer buffer = new StringBuffer();
    pipe(reader, buffer);
    return buffer.toString();
  }

  public static void pipe(final InputStream inputStream, final File file) throws IOException {
    try (final OutputStream outputStream = new FileOutputStream(file)) {
      pipe(inputStream, outputStream);
    }
  }
}
