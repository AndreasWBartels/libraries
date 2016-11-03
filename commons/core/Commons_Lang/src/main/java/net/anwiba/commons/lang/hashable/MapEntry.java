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

import java.text.MessageFormat;
import java.util.Map;

public class MapEntry<K, V> implements Map.Entry<K, V> {
  final K key;
  V value;

  public MapEntry(final K k, final V v) {
    this.value = v;
    this.key = k;
  }

  @Override
  public final K getKey() {
    return this.key;
  }

  @Override
  public final V getValue() {
    return this.value;
  }

  @Override
  public final V setValue(final V newValue) {
    final V oldValue = this.value;
    this.value = newValue;
    return oldValue;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final boolean equals(final Object object) {
    if (!(object instanceof Map.Entry)) {
      return false;
    }
    final Map.Entry<K, V> other = (Map.Entry<K, V>) object;
    return equals(this.getKey(), other.getKey()) && equals(getValue(), other.getValue());
  }

  private boolean equals(@SuppressWarnings("hiding") final Object value, final Object other) {
    return value == other || (value != null && value.equals(other));
  }

  @Override
  public final int hashCode() {
    return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
  }

  @Override
  public final String toString() {
    return MessageFormat.format("{0}={1}", getKey(), getValue()); //$NON-NLS-1$
  }
}