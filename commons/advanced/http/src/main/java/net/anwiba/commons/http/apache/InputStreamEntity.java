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
package net.anwiba.commons.http.apache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;

import net.anwiba.commons.lang.functional.IClosure;

@SuppressWarnings("nls")
public class InputStreamEntity extends AbstractHttpEntity {

  private final IClosure<InputStream, IOException> content;
  private final long length;

  public InputStreamEntity(
      final IClosure<InputStream, IOException> contentClosure,
      final long length,
      final ContentType contentType) {
    super();
    this.content = Objects.requireNonNull(contentClosure, "no content");
    this.length = length;
    if (contentType != null) {
      setContentType(contentType.toString());
    }
  }

  @Override
  public boolean isRepeatable() {
    return false;
  }

  /**
   * @return the content length or {@code -1} if unknown
   */
  @Override
  public long getContentLength() {
    return this.length;
  }

  @Override
  public InputStream getContent() throws IOException {
    return this.content.execute();
  }

  @Override
  public void writeTo(final OutputStream outstream) throws IOException {
    Objects.requireNonNull(outstream, "no content");
    try (final InputStream instream = this.content.execute();) {
      final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
      int size;
      if (this.length < 0) {
        while ((size = instream.read(buffer)) != -1) {
          outstream.write(buffer, 0, size);
        }
      } else {
        long remaining = this.length;
        while (remaining > 0) {
          size = instream.read(buffer, 0, (int) Math.min(OUTPUT_BUFFER_SIZE, remaining));
          if (size == -1) {
            break;
          }
          outstream.write(buffer, 0, size);
          remaining -= size;
        }
      }
    }
  }

  @Override
  public boolean isStreaming() {
    return true;
  }

}
