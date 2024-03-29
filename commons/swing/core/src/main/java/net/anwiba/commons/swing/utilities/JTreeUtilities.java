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
package net.anwiba.commons.swing.utilities;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.anwiba.commons.swing.tree.ReloadableFolderTreeNode;

public class JTreeUtilities {

  public static void expandAll(final JTree tree, final DefaultMutableTreeNode node) {
    GuiUtilities.invokeLater(() -> internalExpandAll(tree, node));
  }

  static void internalExpandAll(final JTree tree, final DefaultMutableTreeNode node) {
    if (node instanceof ReloadableFolderTreeNode reloadableNode && !reloadableNode.isInitialized()) {
      return;
    }
    tree.expandPath(new TreePath(node.getPath()));
    for (int i = 0; i < node.getChildCount(); ++i) {
      internalExpandAll(tree, (DefaultMutableTreeNode) node.getChildAt(i));
    }
  }

  public static void expandAll(final JTree tree) {
    GuiUtilities.invokeLater(() -> internalExpandAll(tree));
  }

  static void internalExpandAll(final JTree tree) {
    synchronized (tree) {
      int row = 0;
      while (row < tree.getRowCount()) {
        TreePath path = tree.getPathForRow(row);
        if (path.getLastPathComponent() instanceof ReloadableFolderTreeNode node
            && !node.isInitialized()) {
          row++;
          continue;
        };
        tree.expandRow(row);
        row++;
      }
    }
  }

  public static void collapseAll(final JTree tree) {
    GuiUtilities.invokeLater(() -> internalCollapseAll(tree));
  }

  static void internalCollapseAll(final JTree tree) {
    synchronized (tree) {
      int row = tree.getRowCount() - 1;
      while (row >= 0) {
        tree.collapseRow(row);
        row--;
      }
    }
  }

  public static void selectFirstLeaf(final JTree tree, final DefaultMutableTreeNode node) {
    if (node.getChildCount() == 0) {
      return;
    }
    final TreePath treePath = new TreePath(node.getFirstLeaf().getPath());
    selectPath(tree, treePath);
  }

  private static void selectPath(final JTree tree, final TreePath treePath) {
    GuiUtilities.invokeLater(() -> {
      tree.getSelectionModel().setSelectionPath(treePath);
      tree.scrollPathToVisible(treePath);
    });
  }

  public static void selectFirstNode(final JTree tree, final DefaultMutableTreeNode node) {
    if (node.getChildCount() == 0) {
      return;
    }
    final TreeNode childNode = node.getFirstChild();
    if (childNode instanceof DefaultMutableTreeNode) {
      final TreePath treePath = new TreePath(((DefaultMutableTreeNode) childNode).getPath());
      selectPath(tree, treePath);
    }
  }

  public static void insertNode(
      final DefaultTreeModel model,
      final JTree tree,
      final DefaultMutableTreeNode root,
      final DefaultMutableTreeNode node,
      final int index) {
    internalInsertNode(model, tree, root, node, index);
  }

  static void internalInsertNode(
      final DefaultTreeModel model,
      final JTree tree,
      final DefaultMutableTreeNode root,
      final DefaultMutableTreeNode node,
      final int index) {
    synchronized (tree) {
      if (index < 0) {
        model.insertNodeInto(node, root, root.getChildCount());
      } else if (index >= root.getChildCount()) {
        model.insertNodeInto(node, root, index);
      } else {
        model.insertNodeInto(node, root, index);
      }
      selectPath(tree, new TreePath(node.getPath()));
    }
  }
}