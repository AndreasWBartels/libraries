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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;

public class FilteredDefaultTreeModel extends DefaultTreeModel {

  private static final long serialVersionUID = 1L;
  private final NeutralDefaultTreeNodeFilter neutralTreeNodeFilter = new NeutralDefaultTreeNodeFilter();
  private final IObjectModel<IDefaultTreeNodeFilter> filterModel = new ObjectModel<>(
      this.neutralTreeNodeFilter);

  public FilteredDefaultTreeModel(final TreeNode root) {
    super(root);
    this.filterModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        nodeStructureChanged(root);
      }
    });
  }

  @Override
  public TreeNode getChild(final Object parent, final int index) {
    return getFilter().getChild((TreeNode) parent, index);
  }

  @Override
  public int getChildCount(final Object parent) {
    final int childCount = getFilter().getChildCount((TreeNode) parent);
    return childCount;
  }

  private IDefaultTreeNodeFilter getFilter() {
    final IDefaultTreeNodeFilter object = this.filterModel.get();
    return object == null ? this.neutralTreeNodeFilter : object;
  }

  public IObjectReceiver<IDefaultTreeNodeFilter> getFilterReceiver() {
    return this.filterModel;
  }
}
