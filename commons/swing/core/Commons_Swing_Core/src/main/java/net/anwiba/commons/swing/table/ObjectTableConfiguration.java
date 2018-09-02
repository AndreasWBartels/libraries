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
package net.anwiba.commons.swing.table;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;

import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.swing.table.action.ITableActionConfiguration;
import net.anwiba.commons.utilities.string.IStringSubstituter;

public class ObjectTableConfiguration<T> implements IObjectTableConfiguration<T> {

  private final List<? extends IColumnConfiguration> columnConfigurations;
  private final int selectionMode;
  private final ITableActionConfiguration<T> actionConfiguration;
  private final int preferredVisibleRowCount;
  private final IMouseListenerFactory<T> mouseListenerFactory;
  private final IKeyListenerFactory<T> keyListenerFactory;
  private final int autoResizeMode;
  private final IStringSubstituter toolTipSubstituter;

  ObjectTableConfiguration(
      final int autoizeMode,
      final int selectionMode,
      final int preferredVisibleRowCount,
      final IStringSubstituter toolTipSubstituter,
      final List<? extends IColumnConfiguration> columnConfigurations,
      final IMouseListenerFactory<T> mouseListenerFactory,
      final IKeyListenerFactory<T> keyListenerFactory,
      final ITableActionConfiguration<T> actionConfiguration) {
    this.autoResizeMode = autoizeMode;
    this.selectionMode = selectionMode;
    this.preferredVisibleRowCount = preferredVisibleRowCount;
    this.toolTipSubstituter = toolTipSubstituter;
    this.columnConfigurations = columnConfigurations;
    this.keyListenerFactory = keyListenerFactory == null ? new IKeyListenerFactory<T>() {

      @Override
      public KeyListener create(
          final IObjectTableModel<T> tableModel,
          final ISelectionIndexModel<T> selectionIndicesProvider,
          final ISelectionModel<T> selectionModel,
          final IBooleanDistributor sortStateModel) {
        return null;
      }
    } : keyListenerFactory;
    this.mouseListenerFactory = mouseListenerFactory == null ? new IMouseListenerFactory<T>() {

      @Override
      public MouseListener create(
          final IObjectTableModel<T> tableModel,
          final ISelectionIndexModel<T> selectionIndicesProvider,
          final ISelectionModel<T> selectionModel,
          final IBooleanDistributor sortStateModel) {
        return null;
      }
    } : mouseListenerFactory;
    this.actionConfiguration = actionConfiguration;
  }

  @Override
  public IColumnConfiguration getColumnConfiguration(final int columnIndex) {
    if (columnIndex < 0 || this.columnConfigurations.size() <= columnIndex) {
      return null;
    }
    return this.columnConfigurations.get(columnIndex);
  }

  @Override
  public int getAutoResizeMode() {
    return this.autoResizeMode;
  }

  @Override
  public int getSelectionMode() {
    return this.selectionMode;
  }

  @Override
  public ObjectTableRowSorter<T> getRowSorter(final IObjectTableModel<T> tableModel) {
    boolean flag = false;
    final ObjectTableRowSorter<T> tableRowSorter = new ObjectTableRowSorter<>(tableModel);
    for (int i = 0; i < this.columnConfigurations.size(); i++) {
      final IColumnConfiguration columnConfiguration = this.columnConfigurations.get(i);
      final boolean isSortable = columnConfiguration.isSortable();
      tableRowSorter.setSortable(i, isSortable);
      if (columnConfiguration.getComparator() != null) {
        tableRowSorter.setComparator(i, columnConfiguration.getComparator());
      }
      flag |= isSortable;
    }
    return flag ? tableRowSorter : null;
  }

  @Override
  public IStringSubstituter getToolTipSubstituter() {
    return this.toolTipSubstituter;
  }

  @Override
  public ITableActionConfiguration<T> getTableActionConfiguration() {
    return this.actionConfiguration;
  }

  @Override
  public int getPreferredVisibleRowCount() {
    return this.preferredVisibleRowCount;
  }

  @Override
  public IMouseListenerFactory<T> getMouseListenerFactory() {
    return this.mouseListenerFactory;
  }

  @Override
  public IKeyListenerFactory<T> getKeyListenerFactory() {
    return this.keyListenerFactory;
  }
}
