/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.tree;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

public class TreeModel<T extends ITreeNode<?>> implements ITreeModel<T> {

  private final T rootNode;

  public TreeModel(final T rootNode) {
    this.rootNode = rootNode;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int getChildCount(final Object parent) {
    if (parent == null) {
      throw new IllegalArgumentException("parent is null"); //$NON-NLS-1$
    }
    if (parent instanceof LazyFolderTreeNode && !((LazyFolderTreeNode) parent).isInitialize()) {
      return 0;
    }
    return ((T) parent).getChildCount();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean isLeaf(final Object node) {
    if (node == null) {
      throw new IllegalArgumentException("node is null"); //$NON-NLS-1$
    }
    return ((T) node).isLeaf();
  }

  @Override
  public void valueForPathChanged(final TreePath path, final Object newValue) {
    throw new UnsupportedOperationException("not yet implemented"); //$NON-NLS-1$
  }

  @Override
  public int getIndexOfChild(final Object parent, final Object child) {
    return 0;
  }

  @Override
  public void addTreeModelListener(final TreeModelListener l) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeTreeModelListener(final TreeModelListener l) {
    // TODO Auto-generated method stub

  }

  @Override
  public T getRoot() {
    return this.rootNode;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T getChild(final Object parent, final int index) {
    if (parent == null) {
      throw new IllegalArgumentException("parent is null"); //$NON-NLS-1$
    }
    if (parent instanceof LazyFolderTreeNode && !((LazyFolderTreeNode) parent).isInitialize()) {
      throw new IllegalStateException();
    }
    return (T) ((T) parent).getChildAt(index);
  }

}
