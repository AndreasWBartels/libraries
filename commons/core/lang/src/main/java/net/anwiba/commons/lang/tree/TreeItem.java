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

class TreeItem<K, V> implements ITreeItem<K, V> {

  public final static int LEFT = -1;
  public final static int BALANCED = 0;
  public final static int RIGHT = 1;

  private K key = null;
  private V element = null;

  TreeItem<K, V> prev;
  TreeItem<K, V> next;

  TreeItem<K, V> parent = null;
  TreeItem<K, V> left = null;
  TreeItem<K, V> right = null;
  int balanced = TreeItem.BALANCED;

  TreeItem() {
  }

  TreeItem(final K key, final V element) {
    if (key == null) {
      throw new NullPointerException();
    }
    this.key = key;
    this.element = element;
  }

  @Override
  public int getBalanced() {
    return this.balanced;
  }

  @Override
  public ITreeItem<K, V> getParent() {
    return this.parent;
  }

  @Override
  public ITreeItem<K, V> getLeft() {
    return this.left;
  }

  @Override
  public ITreeItem<K, V> getRight() {
    return this.right;
  }

  @Override
  public K getKey() {
    return this.key;
  }

  @Override
  public V getElement() {
    return this.element;
  }

  public void setElement(final V element) {
    this.element = element;
  }

  @Override
  public TreeItem<K, V> clone() {
    TreeItem<K, V> object = null;
    object = new TreeItem<>(this.key, this.element);
    object.parent = this.parent;
    object.left = this.left;
    object.right = this.right;
    object.balanced = this.balanced;
    return object;
  }

  @Override
  public ITreeItem<K, V> getNext() {
    return this.next;
  }

  @Override
  public ITreeItem<K, V> getPrevious() {
    return this.prev;
  }

}
