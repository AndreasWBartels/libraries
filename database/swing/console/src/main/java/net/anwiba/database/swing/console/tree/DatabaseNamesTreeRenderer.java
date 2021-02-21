/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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

package net.anwiba.database.swing.console.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;

public final class DatabaseNamesTreeRenderer extends DefaultTreeCellRenderer {

  private static final long serialVersionUID = 1L;

  public DatabaseNamesTreeRenderer() {
  }

  @Override
  public Component getTreeCellRendererComponent(
      final JTree tree,
      final Object value,
      final boolean isSelected,
      final boolean isExpanded,
      final boolean isLeaf,
      final int row,
      @SuppressWarnings("hiding") final boolean hasFocus) {
    super.getTreeCellRendererComponent(
        tree,
        getText(value, isLeaf, isExpanded),
        isSelected,
        isExpanded,
        isLeaf,
        row,
        hasFocus);
    setIcon(getIcon(value, isLeaf, isExpanded));
    return this;
  }

  private String getText(
      final Object value,
      @SuppressWarnings("unused") final boolean isLeaf,
      @SuppressWarnings("unused") final boolean isExpanded) {
    if (value instanceof DefaultMutableTreeNode) {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      final Object userObject = node.getUserObject();
      if (userObject instanceof IDatabaseTableName) {
        final IDatabaseTableName description = (IDatabaseTableName) userObject;
        return description.getTableName();
      }
      if (userObject instanceof IDatabaseSequenceName) {
        final IDatabaseSequenceName description = (IDatabaseSequenceName) userObject;
        return description.getSequenceName();
      }
      if (userObject instanceof IDatabaseTriggerName) {
        final IDatabaseTriggerName description = (IDatabaseTriggerName) userObject;
        return description.getTriggerName();
      }
      if (userObject instanceof IDatabaseIndexName) {
        final IDatabaseIndexName description = (IDatabaseIndexName) userObject;
        return description.getIndexName();
      }
      if (userObject instanceof IDatabaseConstraintName) {
        final IDatabaseConstraintName description = (IDatabaseConstraintName) userObject;
        return description.getConstraintName();
      }
      if (userObject != null) {
        return userObject.toString();
      }
      return "---"; //$NON-NLS-1$
    }
    return value.toString();
  }

  private Icon getIcon(final Object value, final boolean isLeaf, final boolean isExpanded) {
    if (value instanceof DefaultMutableTreeNode) {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      final Object userObject = node.getUserObject();
      if (userObject instanceof IDatabaseTableName) {
        return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.VIEW_LIST_TEXT.getSmallIcon();
      }
      if (userObject instanceof IDatabaseSequenceName) {
        return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.IMAGE_LOADING.getSmallIcon();
      }
      if (userObject instanceof IDatabaseTriggerName) {
        return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.RADIO_CHECKED.getSmallIcon();
      }
      if (userObject instanceof IDatabaseIndexName) {
        return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.VIEW_LIST_TREE.getSmallIcon();
      }
      if (userObject instanceof IDatabaseConstraintName) {
        return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.TASQUE.getSmallIcon();
      }
    }
    if (!isLeaf) {
      if (isExpanded) {
        return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.FOLDER_OPEN.getSmallIcon();
      }
      return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.FOLDER.getSmallIcon();
    }
    return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.FOLDER_DOCUMENTS.getSmallIcon();
  }
}
