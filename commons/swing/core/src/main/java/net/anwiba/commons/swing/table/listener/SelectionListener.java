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
package net.anwiba.commons.swing.table.listener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;

import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISortedRowMapper;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public final class SelectionListener<T> implements ISelectionListener<T> {
  private final IObjectTableModel<T> tableModel;
  private final ListSelectionModel tableSelectionModel;
  private final ISelectionModel<T> objectSelectionModel;
  private final ISortedRowMapper sortedRowMapper;

  public SelectionListener(
      final IObjectTableModel<T> tableModel,
      final ListSelectionModel tableSelectionModel,
      final ISelectionModel<T> objectSelectionModel,
      final ISortedRowMapper sortedRowMapper) {
    this.tableModel = tableModel;
    this.tableSelectionModel = tableSelectionModel;
    this.objectSelectionModel = objectSelectionModel;
    this.sortedRowMapper = sortedRowMapper;
  }

  @Override
  public void selectionChanged(final SelectionEvent<T> event) {
    if (this.objectSelectionModel.isEmpty() && this.tableSelectionModel.isSelectionEmpty()) {
      return;
    }
    if (this.objectSelectionModel.isEmpty()) {
      this.tableSelectionModel.clearSelection();
      return;
    }
    final List<T> objects = getObjects(this.tableModel, this.tableSelectionModel);
    final List<T> selectedObjects = IterableUtilities.asList(this.objectSelectionModel.getSelectedObjects());
    if (objects.size() == this.objectSelectionModel.size() && objects.containsAll(selectedObjects)) {
      return;
    }
    this.tableSelectionModel.setValueIsAdjusting(true);
    this.tableSelectionModel.clearSelection();
    final int[] indexes = this.tableModel.indices(selectedObjects);
    for (final int index : indexes) {
      final int row = this.sortedRowMapper.getSortedRow(index);
      this.tableSelectionModel.addSelectionInterval(row, row);
    }
    this.tableSelectionModel.setValueIsAdjusting(false);
  }

  private List<T> getObjects(
      @SuppressWarnings("hiding") final IObjectTableModel<T> tableModel,
      @SuppressWarnings("hiding") final ListSelectionModel tableSelectionModel) {
    final List<T> objects = new ArrayList<>();
    if (tableSelectionModel.isSelectionEmpty()) {
      return objects;
    }
    for (int i = tableSelectionModel.getMinSelectionIndex(); i <= tableSelectionModel.getMaxSelectionIndex(); i++) {
      if (tableSelectionModel.isSelectedIndex(i)) {
        objects.add(tableModel.get(this.sortedRowMapper.getModelIndex(i)));
      }
    }
    return objects;
  }
}