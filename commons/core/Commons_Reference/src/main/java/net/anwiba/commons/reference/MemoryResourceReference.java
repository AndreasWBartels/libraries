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
package net.anwiba.commons.reference;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Base64;

import net.anwiba.commons.reference.utilities.StringUtilities;

public class MemoryResourceReference implements IResourceReference {

  private static final long serialVersionUID = 1L;
  private final ZonedDateTime timeStamp;
  private final byte[] buffer;
  private final String mimeType;
  private final String encoding;

  public MemoryResourceReference(final byte[] buffer, final String mimeType, final String encoding) {
    this.timeStamp = ZonedDateTime.now();
    this.buffer = buffer;
    this.mimeType = mimeType;
    this.encoding = encoding;
  }

  @Override
  public <O, E extends Exception> O accept(final IResourceReferenceVisitor<O, E> visitor) throws E {
    return visitor.visitMemoryResource(this);
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this.buffer);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MemoryResourceReference)) {
      return false;
    }
    final MemoryResourceReference other = (MemoryResourceReference) obj;
    return this.buffer == other.buffer;
  }

  public ZonedDateTime getTimeStamp() {
    return this.timeStamp;
  }

  public byte[] getBuffer() {
    return this.buffer;
  }

  public String getContentType() {
    return this.mimeType;
  }

  public String getEncoding() {
    return this.encoding;
  }

  @SuppressWarnings("nls")
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("data:");
    builder.append(this.mimeType);
    if (StringUtilities.isNullOrEmpty(this.encoding)) {
      builder.append(";charset=");
      builder.append(this.encoding);
    }
    if (this.mimeType.toLowerCase().startsWith("text")) {
      builder.append(",");
      try {
        builder.append(new String(this.buffer, this.encoding));
      } catch (final UnsupportedEncodingException exception) {
        builder.append(new String(this.buffer, StandardCharsets.UTF_8));
      }
    } else {
      builder.append(";base64,");
      builder.append(Base64.getEncoder().encodeToString(this.buffer));
    }
    return builder.toString();
  }
}
