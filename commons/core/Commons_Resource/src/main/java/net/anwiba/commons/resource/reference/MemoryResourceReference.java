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
package net.anwiba.commons.resource.reference;

public class MemoryResourceReference implements IResourceReference {

  private final byte[] buffer;
  private final String mimeType;

  public MemoryResourceReference(final byte[] buffer, final String mimeType) {
    this.buffer = buffer;
    this.mimeType = mimeType;
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

  public byte[] getBuffer() {
    return this.buffer;
  }

  public String getMimeType() {
    return this.mimeType;
  }

}