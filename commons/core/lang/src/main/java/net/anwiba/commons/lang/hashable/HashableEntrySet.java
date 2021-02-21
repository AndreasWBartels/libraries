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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public final class HashableEntrySet<K extends IHashable, V> extends AbstractSet<Entry<K, V>>
    implements
    Serializable,
    Cloneable {

  private static final long serialVersionUID = -7023287624783829656L;
  private final HashSet<Entry<HashableWrapper<K>, V>> set;

  HashableEntrySet(final HashSet<Entry<HashableWrapper<K>, V>> clone) {
    this.set = clone;
  }

  public HashableEntrySet() {
    this.set = new HashSet<>();
  }

  @Override
  public int size() {
    return this.set.size();
  }

  @Override
  public boolean contains(final Object object) {
    if (object == null) {
      return this.set.contains(null);
    }
    if (!(object instanceof Entry)) {
      return false;
    }
    @SuppressWarnings("rawtypes")
    final Entry entry = (Entry) object;
    if (!(entry.getKey() instanceof IHashable)) {
      return false;
    }
    final IHashable hashable = (IHashable) entry.getKey();
    final MapEntry<HashableWrapper<IHashable>, Object> o = new MapEntry<>(
        new HashableWrapper<>(hashable),
        entry.getValue());
    return this.set.contains(o);
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    final Iterator<Entry<HashableWrapper<K>, V>> iterator = this.set.iterator();
    return new Iterator<Entry<K, V>>() {

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public Entry<K, V> next() {
        final Entry<HashableWrapper<K>, V> next = iterator.next();
        return new MapEntry<>(next.getKey().getValue(), next.getValue());
      }

      @Override
      public void remove() {
        iterator.remove();
      }
    };
  }

  @Override
  public boolean add(final Entry<K, V> entry) {
    if (entry == null) {
      return this.set.add(null);
    }
    return this.set.add(new MapEntry<>(new HashableWrapper<>(entry.getKey()), entry.getValue()));
  }

  @Override
  public boolean remove(final Object object) {
    if (object == null) {
      return this.set.remove(null);
    }
    if (!(object instanceof Entry)) {
      return false;
    }
    @SuppressWarnings("rawtypes")
    final Entry entry = (Entry) object;
    if (!(entry.getKey() instanceof IHashable)) {
      return false;
    }
    final IHashable hashable = (IHashable) entry.getKey();
    final MapEntry<HashableWrapper<IHashable>, Object> o = new MapEntry<>(
        new HashableWrapper<>(hashable),
        entry.getValue());
    return this.set.remove(o);
  }

  @Override
  public void clear() {
    this.set.clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public HashableEntrySet<K, V> clone() {
    return new HashableEntrySet<>((HashSet<Entry<HashableWrapper<K>, V>>) this.set.clone());
  }
}
