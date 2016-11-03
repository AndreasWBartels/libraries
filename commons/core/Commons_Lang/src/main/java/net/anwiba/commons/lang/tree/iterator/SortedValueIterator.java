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
package net.anwiba.commons.lang.tree.iterator;

import net.anwiba.commons.lang.object.IObjectIterator;
import net.anwiba.commons.lang.tree.ITreeItem;

public final class SortedValueIterator<K, V> implements IObjectIterator<V> {

  private ITreeItem<K, V> item = null;

  public SortedValueIterator(final ITreeItem<K, V> item) {
    this.item = new InitalIteratorItem<>(item);
  }

  @Override
  public boolean hasNext() {
    return this.item.getNext() != null;
  }

  @Override
  public V next() {
    this.item = this.item.getNext();
    return this.item == null
        ? null
        : this.item.getElement();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasPrevious() {
    return this.item.getPrevious() != null;
  }

  @Override
  public V previous() {
    this.item = this.item.getPrevious();
    return this.item == null
        ? null
        : this.item.getElement();
  }
}