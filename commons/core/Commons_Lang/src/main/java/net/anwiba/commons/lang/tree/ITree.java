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
package net.anwiba.commons.lang.tree;

import net.anwiba.commons.lang.tree.walker.ITreeWalker;

public interface ITree<K, V> {

  public abstract void insert(K key, V element);

  public abstract void removeAll();

  public abstract V get(K key);

  public abstract void remove(K key);

  public abstract int size();

  public abstract boolean isEmpty();

  public abstract Iterable<V> getValues();

  public abstract Iterable<K> getKeys();

  public abstract Iterable<V> getDeepSearchFirstValues();

  public abstract Iterable<V> getBreadthSearchFirstValues();

  public abstract ITreeWalker<K, V> getTreeWalker();

}