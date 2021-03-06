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

import java.util.LinkedList;

import net.anwiba.commons.lang.collection.IObjectIterator;
import net.anwiba.commons.lang.tree.ITreeItem;

public class BreadthFirstSearchValueIterator<K, V> implements IObjectIterator<V> {

  private ITreeItem<K, V> item = null;
  private final LinkedList<ITreeItem<K, V>> queue = new LinkedList<>();

  public BreadthFirstSearchValueIterator(final ITreeItem<K, V> item) {
    initalize(item);
  }

  private void initalize(@SuppressWarnings("hiding") final ITreeItem<K, V> item) {
    final LinkedList<ITreeItem<K, V>> internal = new LinkedList<>();
    internal.add(item);
    while (!internal.isEmpty()) {
      final ITreeItem<K, V> parent = internal.removeFirst();
      this.queue.add(parent);
      if (parent.getLeft() != null) {
        internal.add(parent.getLeft());
      }
      if (parent.getRight() != null) {
        internal.add(parent.getRight());
      }
    }
  }

  @Override
  public boolean hasNext() {
    if (this.item != null) {
      return true;
    }
    if (this.queue.isEmpty()) {
      return false;
    }
    this.item = this.queue.removeFirst();
    return this.item != null;
  }

  @Override
  public V next() {
    if (this.item == null || !hasNext()) {
      return null;
    }
    try {
      return this.item == null ? null : this.item.getElement();
    } finally {
      this.item = null;
    }
  }
}
