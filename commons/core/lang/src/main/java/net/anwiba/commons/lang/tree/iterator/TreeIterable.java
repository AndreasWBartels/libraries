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

import net.anwiba.commons.lang.collection.IObjectIterable;
import net.anwiba.commons.lang.collection.IObjectIterator;
import net.anwiba.commons.lang.collection.IObjectIteratorFactory;
import net.anwiba.commons.lang.tree.ITreeItem;

public class TreeIterable<K, V, O> implements IObjectIterable<O> {

  private ITreeItem<K, V> item = null;
  private final IObjectIteratorFactory<ITreeItem<K, V>, O> factory;

  public TreeIterable(final IObjectIteratorFactory<ITreeItem<K, V>, O> factory, final ITreeItem<K, V> item) {
    this.factory = factory;
    this.item = item;
  }

  @Override
  public IObjectIterator<O> iterator() {
    return this.factory.create(this.item);
  }
}
