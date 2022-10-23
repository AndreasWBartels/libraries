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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.utilities.collection.ListUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class StringDefaultNodeTreeFilter implements IDefaultTreeNodeFilter {

  IAcceptor<TreeNode> acceptor;

  public StringDefaultNodeTreeFilter(final String string, final IToStringConverter... converters) {
    this.acceptor = new IAcceptor<TreeNode>() {

      @Override
      public boolean accept(final TreeNode value) {
        if (StringUtilities.isNullOrEmpty(string)) {
          return true;
        }
        return evaluate(value);
      }

      private boolean evaluate(final TreeNode node) {
        final String upperCase = string.toUpperCase();
        if (node instanceof DefaultMutableTreeNode objectNode) {
          boolean flag = false;
          for (final IToStringConverter toStringConverter : converters) {
            Object userObject = objectNode.getUserObject();
            if (toStringConverter.isApplicable(userObject)) {
              flag |= contains(toStringConverter.toString(userObject), upperCase);
            }
          }
          return flag || evaluate(iterable(node));
        }
        return contains(node.toString(), upperCase) || evaluate(iterable(node));
      }

      private boolean evaluate(final Iterable<TreeNode> nodes) {
        for (final TreeNode node : nodes) {
          if (evaluate(node)) {
            return true;
          }
        }
        return false;
      }

      private boolean contains(final String value, final String upperCase) {
        return value == null ? false : value.toUpperCase().contains(upperCase);
      }
    };
  }

  @Override
  public TreeNode getChild(final TreeNode parent, final int index) {
    final List<TreeNode> list = ListUtilities.filter(iterable(parent), this.acceptor);
    return list.get(index);
  }

  Iterable<TreeNode> iterable(final TreeNode parent) {
    if (parent instanceof ReloadableFolderTreeNode node && (!node.isInitialized() || node.isLoading() )) {
      return () -> List.<TreeNode>of().iterator();
    }
    return new Iterable<TreeNode>() {

      @Override
      public Iterator<TreeNode> iterator() {

    	final Enumeration<? extends TreeNode> children = parent.children();
        return new Iterator<TreeNode>() {

          @Override
          public boolean hasNext() {
            return children.hasMoreElements();
          }

          @Override
          public TreeNode next() {
            return children.nextElement();
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }

  @Override
  public int getIndexOfChild(final TreeNode parent, final TreeNode child) {
    final List<TreeNode> list = ListUtilities.filter(iterable(parent), this.acceptor);
    return list.indexOf(child);
  }

  @Override
  public int getChildCount(final TreeNode parent) {
    final List<TreeNode> list = ListUtilities.filter(iterable(parent), this.acceptor);
    return list.size();
  }
}