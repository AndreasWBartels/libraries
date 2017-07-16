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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import net.anwiba.commons.lang.collection.IObjectIterable;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.swing.table.filter.NeutralFilter;
import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public class FilterableObjectTableModel<T> extends AbstractTableModel implements IObjectTableModel<T> {

  private static final long serialVersionUID = -4870849336713774402L;
  private final IObjectTableModel<T> objectTableModel;
  private IRowFilter filter;
  private IRowMapper mapper;
  private boolean updateFlag = false;
  private final Object semaphor = new Object();

  public FilterableObjectTableModel(final IObjectTableModel<T> objectTableModel) {
    this.objectTableModel = objectTableModel;
    this.objectTableModel.addTableModelListener(new TableModelListener() {

      @Override
      public void tableChanged(final TableModelEvent event) {
        if (event.getType() == TableModelEvent.HEADER_ROW) {
          fireTableStructureChanged();
          return;
        }
        update();
      }
    });
    setRowFilter(new NeutralFilter());
  }

  IObjectTableModel<T> getObjectTableModel() {
    return this.objectTableModel;
  }

  public synchronized void setRowFilter(final IRowFilter filter) {
    this.filter = filter == null ? new NeutralFilter() : filter;
    update();
  }

  void update() {
    synchronized (this.semaphor) {
      try {
        final IRowMapper mapper = this.filter.filter(this.objectTableModel);
        this.mapper = mapper;

      } finally {
        this.updateFlag = false;
      }
    }
    fireTableDataChanged();
  }

  @Override
  public T get(final int rowIndex) {
    return this.objectTableModel.get(getMapper().getRowIndex(rowIndex));
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    return this.objectTableModel.getColumnClass(columnIndex);
  }

  @Override
  public int getColumnCount() {
    return this.objectTableModel.getColumnCount();
  }

  @Override
  public String getColumnName(final int columnIndex) {
    return this.objectTableModel.getColumnName(columnIndex);
  }

  @Override
  public int getRowCount() {
    return getMapper().getRowCount();
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    return this.objectTableModel.getValueAt(getMapper().getRowIndex(rowIndex), columnIndex);
  }

  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    return this.objectTableModel.isCellEditable(getMapper().getRowIndex(rowIndex), columnIndex);
  }

  @Override
  public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
    this.objectTableModel.setValueAt(value, getMapper().getRowIndex(rowIndex), columnIndex);
  }

  @Override
  public int[] indices(final Iterable<T> objects) {
    final List<Integer> results = new ArrayList<>();
    synchronized (this.semaphor) {
      final int[] indexes = this.objectTableModel.indices(objects);
      for (final int index : indexes) {
        final int result = getMapper().getRowIndex(index);
        if (result == -1) {
          continue;
        }
        results.add(Integer.valueOf(result));
      }
    }
    return ArrayUtilities.primitives(results.toArray(new Integer[results.size()]));
  }

  @Override
  public void addListModelListener(final IChangeableListListener<T> listener) {
    this.objectTableModel.addListModelListener(listener);
  }

  @Override
  public void removeListModelListener(final IChangeableListListener<T> listener) {
    this.objectTableModel.removeListModelListener(listener);
  }

  @Override
  public void add(@SuppressWarnings("unchecked") final T... objects) {
    this.updateFlag = true;
    this.objectTableModel.add(objects);
  }

  @Override
  public void add(final Iterable<T> objects) {
    this.updateFlag = true;
    this.objectTableModel.add(objects);
  }

  @Override
  public T set(final int rowIndex, final T object) {
    this.updateFlag = true;
    return this.objectTableModel.set(getMapper().getRowIndex(rowIndex), object);
  }

  private IRowMapper getMapper() {
    if (this.updateFlag) {
      update();
    }
    return this.mapper;
  }

  @Override
  public void remove(@SuppressWarnings("unchecked") final T... objects) {
    this.updateFlag = true;
    this.objectTableModel.remove(objects);
  }

  @Override
  public void remove(final Iterable<T> objects) {
    this.updateFlag = true;
    this.objectTableModel.remove(objects);
  }

  @Override
  public void removeAll() {
    this.updateFlag = true;
    this.objectTableModel.removeAll();
  }

  @Override
  public void remove(final int... indices) {
    final int[] values = new int[indices.length];
    synchronized (this.semaphor) {
      for (int i = 0; i < indices.length; i++) {
        values[i] = getMapper().getRowIndex(indices[i]);
      }
    }
    this.updateFlag = true;
    this.objectTableModel.remove(values);
  }

  @Override
  public int size() {
    return this.objectTableModel.size();
  }

  @Override
  public void set(@SuppressWarnings("unchecked") final T... objects) {
    this.updateFlag = true;
    this.objectTableModel.set(objects);
  }

  @Override
  public void set(final Iterable<T> objects) {
    this.updateFlag = true;
    this.objectTableModel.set(objects);
  }

  @Override
  public IObjectIterable<T> values() {
    final List<T> values = new ArrayList<>();
    synchronized (this.semaphor) {
      final IRowMapper mapper = getMapper();
      final Iterable<Integer> indeces = mapper.indeces();
      final List<Integer> asList = IterableUtilities.asList(indeces);
      final int[] array = ArrayUtilities.primitives(asList.toArray(new Integer[asList.size()]));
      if (array.length > 0) {
        values.addAll(this.objectTableModel.get(array));
      }
    }
    return new ObjectList<>(Collections.unmodifiableList(IterableUtilities.asList(values)));
  }

  @Override
  public Collection<T> get(final int... indices) {
    final List<T> values = new ArrayList<>();
    synchronized (this.semaphor) {
      final IRowMapper mapper = getMapper();
      final int[] result = new int[indices.length];
      for (int i = 0; i < result.length; i++) {
        result[i] = mapper.getRowIndex(indices[i]);
      }
      values.addAll(this.objectTableModel.get(result));
    }
    return values;
  }

  @Override
  public boolean isEmpty() {
    return getMapper().getRowCount() == 0;
  }
}
