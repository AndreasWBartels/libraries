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
package net.anwiba.commons.swing.transferable;

import java.awt.datatransfer.Transferable;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TransferableFactory {

  private final ITransferableFactories transferableFactories;

  public TransferableFactory(final Collection<ITransferableFactory> transferableFactories) {
    this(new TransferableFactories(transferableFactories));
  }

  public TransferableFactory(final ITransferableFactories transferableFactories) {
    this.transferableFactories = transferableFactories;
  }

  public Transferable create(final JComponent c) {
    final Object userObject = getObject(c);
    return this.transferableFactories.getApplicable(userObject).convert(f -> f.create(userObject)).getOr(() -> null);
  }

  private Object getObject(final JComponent c) {
    if (c instanceof JTree) {
      final JTree sourceTree = (JTree) c;
      final TreePath[] paths = sourceTree.getSelectionPaths();
      if (paths == null || paths.length != 1) {
        return null;
      }
      return getValue(paths[0].getLastPathComponent());
    } else if (c instanceof JList) {
      @SuppressWarnings("rawtypes")
      final JList list = (JList) c;
      return getValue(list.getSelectedValue());
    }
    return null;
  }

  private Object getValue(final Object value) {
    if (value instanceof DefaultMutableTreeNode) {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      return node.getUserObject();
    }
    return value;
  }

}
