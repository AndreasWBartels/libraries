/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels 
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

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.anwiba.commons.lang.functional.IClosure;

public class LazyFolderTreeNode extends DefaultMutableTreeNode {

  private static final long serialVersionUID = 1L;
  private boolean isInitialize = false;
  private final IClosure<List<DefaultMutableTreeNode>, RuntimeException> initializer;

  public LazyFolderTreeNode(
      final String string,
      final IClosure<List<DefaultMutableTreeNode>, RuntimeException> initializer) {
    super(string);
    this.initializer = initializer;
  }

  public boolean isInitialize() {
    return this.isInitialize;
  }

  private void initialize() {
    if (this.isInitialize) {
      return;
    }
    final List<DefaultMutableTreeNode> list = this.initializer.execute();
    this.children = new Vector<>(list);
    this.isInitialize = true;
  }

  @Override
  public TreeNode getChildAt(final int index) {
    initialize();
    return super.getChildAt(index);
  }

  @Override
  public int getChildCount() {
    initialize();
    return super.getChildCount();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Enumeration<DefaultMutableTreeNode> children() {
    initialize();
    return super.children();
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

}
