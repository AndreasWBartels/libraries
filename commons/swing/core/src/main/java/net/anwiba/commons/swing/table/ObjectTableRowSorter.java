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
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;

public class ObjectTableRowSorter<T> extends TableRowSorter<IObjectTableModel<T>> {

  private final BooleanModel sortStateModel = new BooleanModel(false);

  public ObjectTableRowSorter(final IObjectTableModel<T> tableModel) {
    super(tableModel);
  }

  private void checkColumn(final int column) {
    if (column < 0 || column >= getModelWrapper().getColumnCount()) {
      throw new IndexOutOfBoundsException("Column out of range"); //$NON-NLS-1$
    }
  }

  @Override
  public void toggleSortOrder(final int column) {
    checkColumn(column);
    if (isSortable(column)) {
      List<SortKey> keys = new ArrayList<>(getSortKeys());
      SortKey sortKey;
      int sortIndex;
      for (sortIndex = keys.size() - 1; sortIndex >= 0; sortIndex--) {
        if (keys.get(sortIndex).getColumn() == column) {
          break;
        }
      }
      if (sortIndex == -1) {
        sortKey = new SortKey(column, SortOrder.ASCENDING);
        keys.add(0, sortKey);
      } else if (sortIndex == 0) {
        keys.set(0, toggle(keys.get(0)));
      } else {
        keys.remove(sortIndex);
        keys.add(0, new SortKey(column, SortOrder.ASCENDING));
      }
      if (keys.size() > getMaxSortKeys()) {
        keys = keys.subList(0, getMaxSortKeys());
      }
      setSortKeys(keys);
      checkSortState();
    }
  }

  private void checkSortState() {
    boolean state = false;
    final List<SortKey> keys = new ArrayList<>(getSortKeys());
    for (final SortKey sortKey : keys) {
      state |= !sortKey.getSortOrder().equals(SortOrder.UNSORTED);
    }
    this.sortStateModel.set(state);
  }

  private SortKey toggle(final SortKey key) {
    switch (key.getSortOrder()) {
      case ASCENDING: {
        return new SortKey(key.getColumn(), SortOrder.DESCENDING);
      }
      case DESCENDING: {
        return new SortKey(key.getColumn(), SortOrder.UNSORTED);
      }
      case UNSORTED: {
        return new SortKey(key.getColumn(), SortOrder.ASCENDING);
      }
    }
    throw new UnreachableCodeReachedException();
  }

  public IBooleanDistributor getSortStateModel() {
    return this.sortStateModel;
  }
}
