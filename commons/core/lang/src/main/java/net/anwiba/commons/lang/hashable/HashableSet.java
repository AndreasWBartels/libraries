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

public final class HashableSet<V extends IHashable> extends AbstractSet<V> implements Serializable, Cloneable {

  private static final long serialVersionUID = -7023287624783829656L;
  private final HashSet<HashableWrapper<V>> set;

  public HashableSet() {
    this.set = new HashSet<>();
  }

  public HashableSet(final int initialCapacity) {
    this.set = new HashSet<>(initialCapacity);
  }

  public HashableSet(final int initialCapacity, final float loadFactor) {
    this.set = new HashSet<>(initialCapacity, loadFactor);
  }

  HashableSet(final HashSet<HashableWrapper<V>> clone) {
    this.set = clone;
  }

  @Override
  public int size() {
    return this.set.size();
  }

  @Override
  public boolean contains(final Object object) {
    if (object != null && !(object instanceof IHashable)) {
      return false;
    }
    final IHashable hashable = (IHashable) object;
    return this.set.contains(new HashableWrapper<>(hashable));
  }

  @Override
  public Iterator<V> iterator() {
    final Iterator<HashableWrapper<V>> iterator = this.set.iterator();
    return new Iterator<V>() {

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public V next() {
        return iterator.next().getValue();
      }

      @Override
      public void remove() {
        iterator.remove();
      }
    };
  }

  @Override
  public boolean add(final V e) {
    return this.set.add(new HashableWrapper<>(e));
  }

  @Override
  public boolean remove(final Object object) {
    if (object != null && !(object instanceof IHashable)) {
      return false;
    }
    final IHashable hashable = (IHashable) object;
    return this.set.remove(new HashableWrapper<>(hashable));
  }

  @Override
  public void clear() {
    this.set.clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public HashableSet<V> clone() {
    return new HashableSet<>((HashSet<HashableWrapper<V>>) this.set.clone());
  }
}