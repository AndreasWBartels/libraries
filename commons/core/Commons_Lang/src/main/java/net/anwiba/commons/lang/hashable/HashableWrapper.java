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
package net.anwiba.commons.lang.hashable;

class HashableWrapper<V extends IHashable> {

  private final V value;

  public HashableWrapper(final V value) {
    this.value = value;
  }

  @Override
  public final boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof HashableWrapper)) {
      // return this.value == object || (this.value != null && this.value.equals(object));
      return false;
    }
    final HashableWrapper<?> other = (HashableWrapper<?>) object;
    return this.value == other.value || (this.value != null && this.value.identical(other));
  }

  @Override
  public final int hashCode() {
    return this.value == null
        ? 0
        : this.value.hashValue();
  }

  public V getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value == null
        ? super.toString()
        : this.value.toString();
  }
}