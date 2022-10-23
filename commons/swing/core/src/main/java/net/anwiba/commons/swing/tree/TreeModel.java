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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
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
    if (parent instanceof ReloadableFolderTreeNode reloadable && !reloadable.isInitialized()) {
      return 0;
    }
    return ((T) parent).getChildCount();
  }

  @SuppressWarnings({ "rawtypes" })
  @Override
  public boolean isLeaf(final Object node) {
    if (node == null) {
      throw new IllegalArgumentException("node is null"); //$NON-NLS-1$
    }
    if (!(node instanceof ITreeNode)) {
      throw new IllegalArgumentException("unsupported instance"); //$NON-NLS-1$
    }
    return ((ITreeNode) node).isLeaf();
  }

  @Override
  public void valueForPathChanged(final TreePath path, final Object newValue) {
    throw new UnsupportedOperationException("not yet implemented"); //$NON-NLS-1$
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public int getIndexOfChild(final Object parent, final Object child) {
    if (!(parent instanceof ITreeNode)) {
      throw new IllegalArgumentException("unsupported instance"); //$NON-NLS-1$
    }
    if (!(child instanceof ITreeNode)) {
      throw new IllegalArgumentException("unsupported instance"); //$NON-NLS-1$
    }
    if (parent instanceof ReloadableFolderTreeNode reloadable && !reloadable.isInitialized()) {
      throw new IllegalStateException();
    }
    return ((ITreeNode) parent).getIndex((ITreeNode) child);
  }

  private final List<TreeModelListener> listeners = new ArrayList<>();

  protected synchronized void fireTreeStructureChanged(final T node) {
    final TreeModelEvent treeModelEvent = new TreeModelEvent(this, new TreePath(node));
    final List<TreeModelListener> currentListeners = new ArrayList<>(this.listeners);
    for (final TreeModelListener listener : currentListeners) {
      listener.treeStructureChanged(treeModelEvent);
    }
  }

  protected void fireTreeNodesRemoved(
      final Object source,
      final Object[] path,
      final int[] childIndices,
      final Object[] children) {
    final TreeModelEvent treeModelEvent = new TreeModelEvent(source, path, childIndices, children);
    final List<TreeModelListener> currentListeners = new ArrayList<>(this.listeners);
    for (final TreeModelListener listener : currentListeners) {
      listener.treeStructureChanged(treeModelEvent);
    }
  }

  @Override
  public synchronized void addTreeModelListener(final TreeModelListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public synchronized void removeTreeModelListener(final TreeModelListener listener) {
    this.listeners.remove(listener);
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
    if (parent instanceof ReloadableFolderTreeNode reloadable && !reloadable.isInitialized()) {
      throw new IllegalStateException();
    }
    return (T) ((T) parent).getChildAt(index);
  }

}
