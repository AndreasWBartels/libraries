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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISortedRowMapper;

public final class TableSelectionListener<T> implements ListSelectionListener {

  private final IObjectTableModel<T> tableModel;
  private final ListSelectionModel tableSelectionModel;
  private final ISelectionModel<T> objectSelectionModel;
  private final ISortedRowMapper sortedRowMapper;

  public TableSelectionListener(
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
  public void valueChanged(final ListSelectionEvent event) {
    if (event.getValueIsAdjusting()) {
      return;
    }
    if (this.tableSelectionModel.isSelectionEmpty()) {
      this.objectSelectionModel.removeAllSelectedObjects();
      return;
    }
    final Set<Integer> indeces = new HashSet<>();
    final List<T> objects = new ArrayList<>();
    for (int i = this.tableSelectionModel.getMinSelectionIndex(); i <= this.tableSelectionModel.getMaxSelectionIndex(); i++) {
      if (this.tableSelectionModel.isSelectedIndex(i)) {
        indeces.add(Integer.valueOf(i));
        // objects.add(this.tableModel.get(this.sortedRowMapper.getModelIndex(i)));
      }
    }
    for (final Integer index : indeces) {
      objects.add(this.tableModel.get(this.sortedRowMapper.getModelIndex(index.intValue())));
    }
    this.objectSelectionModel.setSelectedObjects(objects);
  }
}