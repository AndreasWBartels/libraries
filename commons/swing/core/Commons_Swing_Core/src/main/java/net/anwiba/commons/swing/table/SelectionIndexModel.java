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

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ListenerList;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.model.SelectionModel;
import net.anwiba.commons.utilities.interval.IntegerInterval;
import net.anwiba.commons.utilities.interval.IntegerIterator;

import java.util.Iterator;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class SelectionIndexModel<T> implements ISelectionIndexModel<T> {
  private final ListSelectionModel tableSelectionModel;
  private final ISortedRowMapper rowMapper;
  private final SelectionModel<T> selectionModel;
  private final ListenerList<ISelectionListener<T>> listeners = new ListenerList<>();

  public SelectionIndexModel(
    final ListSelectionModel tableSelectionModel,
    final ISortedRowMapper rowMapper,
    final SelectionModel<T> selectionModel) {
    this.tableSelectionModel = tableSelectionModel;
    this.rowMapper = rowMapper;
    this.selectionModel = selectionModel;
    selectionModel.addSelectionListener(new ISelectionListener<T>() {

      @Override
      public void selectionChanged(final SelectionEvent<T> event) {
        fireSelectionChanged();
      }
    });
    tableSelectionModel.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(final ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
          return;
        }
        fireSelectionChanged();
      }
    });
  }

  public IntegerInterval getInterval() {
    final int minimum = this.tableSelectionModel.getMinSelectionIndex();
    final int maximum = this.tableSelectionModel.getMaxSelectionIndex();
    if (minimum < 0 || maximum < 0) {
      return new IntegerInterval(-1, -1);
    }
    return new IntegerInterval(this.rowMapper.getModelIndex(minimum), this.rowMapper.getModelIndex(maximum));
  }

  @Override
  public int getMinimum() {
    return getInterval().getMinValue();
  }

  @Override
  public int getMaximum() {
    return getInterval().getMaxValue();
  }

  @Override
  public void set(final int index) {
    this.tableSelectionModel.setSelectionInterval(
        this.rowMapper.getSortedRow(index),
        this.rowMapper.getSortedRow(index));
  }

  @Override
  public Iterator<Integer> iterator() {
    final ISortedRowMapper rowMapper = this.rowMapper;
    final ListSelectionModel tableSelectionModel = this.tableSelectionModel;
    final IntegerIterator integerIterator =
        new IntegerIterator(
            tableSelectionModel.getMinSelectionIndex(),
            tableSelectionModel.getMaxSelectionIndex(),
            new IAcceptor<Integer>() {

              @Override
              public boolean accept(final Integer value) {
                return tableSelectionModel.isSelectedIndex(value.intValue());
              }
            });
    return new Iterator<Integer>() {

      private Integer value = null;

      @Override
      public boolean hasNext() {
        if (this.value != null) {
          return true;
        }
        if (integerIterator.hasNext()) {
          this.value = Integer.valueOf(rowMapper.getModelIndex(integerIterator.next().intValue()));
          return true;
        }
        return false;
      }

      @Override
      public Integer next() {
        try {
          return this.value;
        } finally {
          this.value = null;
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  void fireSelectionChanged() {
    final SelectionModel<T> model = this.selectionModel;
    this.listeners.forAllDo(new IProcedure<ISelectionListener<T>, RuntimeException>() {

      @Override
      public void execute(final ISelectionListener<T> value) throws RuntimeException {
        value.selectionChanged(new SelectionEvent<>(model));
      }
    });
  }

  @Override
  public int size() {
    return this.selectionModel.size();
  }

  @Override
  public boolean isEmpty() {
    return this.tableSelectionModel.isSelectionEmpty();
  }

  @Override
  public void addSelectionListener(final ISelectionListener<T> listener) {
    this.listeners.add(listener);
  }

  @Override
  public void removeSelectionListener(final ISelectionListener<T> listener) {
    this.listeners.remove(listener);
  }

  @Override
  public void clear() {
    this.selectionModel.removeAllSelectedObjects();
  }
}