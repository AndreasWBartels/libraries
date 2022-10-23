/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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

import java.io.Serializable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class WrappedTableModel implements TableModel, Serializable {

  private final EventListenerList listenerList = new EventListenerList();
  private final IObjectModel<TableModel> model = new ObjectModel<>();
  private final TableModelListener delegatingTableModelListener = new TableModelListener(){

    @Override
    public void tableChanged(final TableModelEvent e) {
      fireTableChanged(new TableModelEvent(WrappedTableModel.this,
          e.getFirstRow(),
          e.getLastRow(),
          e.getColumn(),
          e.getType()));
    }
  };

  public WrappedTableModel(final TableModel tableModel) {
    this.model.set(tableModel);
    tableModel.addTableModelListener(this.delegatingTableModelListener);
  }

  public void wrap(final TableModel tableModel) {
    synchronized (this.model) {
      if (this.model.get() == tableModel) {
        return;
      }
      this.model
          .optional()
          .consume(o -> o.removeTableModelListener(this.delegatingTableModelListener));
      this.model.set(tableModel);
      this.model
          .optional()
          .consume(o -> o.addTableModelListener(this.delegatingTableModelListener));
    }
    GuiUtilities.invokeLater(() -> {
      fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
      fireTableChanged(new TableModelEvent(this));
    });
  }

  public <T extends TableModel> T unwrap(final java.lang.Class<T> clazz) {
    return (T) this.model
        .optional()
        .accept(clazz::isInstance)
        .getOrThrow(() -> new IllegalArgumentException());
  }

  public boolean isWrapperFor(final java.lang.Class<? extends TableModel> clazz) {
    return this.model
        .optional()
        .accept(o -> clazz.isInstance(o))
        .isAccepted();
  }

  @Override
  public int getRowCount() {
    return this.model
        .optional()
        .convert(TableModel::getRowCount)
        .getOr(() -> 0);
  }

  @Override
  public int getColumnCount() {
    return this.model
        .optional()
        .convert(TableModel::getColumnCount)
        .getOr(() -> 1);
  }

  @Override
  public String getColumnName(final int columnIndex) {
    return this.model
        .optional()
        .convert(o -> o.getColumnName(columnIndex))
        .getOr(() -> columnIndex == 0 ? "empty" : generateColumnName(columnIndex));
  }

  private String generateColumnName(int columnIndex) {
    String result = "";
    for (; columnIndex >= 0; columnIndex = columnIndex / 26 - 1) {
      result = (char) ((char) (columnIndex % 26) + 'A') + result;
    }
    return result;
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    return this.model
        .optional()
        .convert(o -> o.getColumnClass(columnIndex))
        .getOr(() -> (Class) Object.class);
  }

  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    return this.model
        .optional()
        .accept(o -> o.isCellEditable(rowIndex, columnIndex))
        .isAccepted();
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    return this.model
        .optional()
        .convert(o -> o.getValueAt(rowIndex, columnIndex))
        .getOr(() -> null);
  }

  @Override
  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
    this.model
        .optional()
        .consume(o -> o.setValueAt(aValue, rowIndex, columnIndex));
  }

  @Override
  public void addTableModelListener(final TableModelListener l) {
    this.listenerList.add(TableModelListener.class, l);
  }

  @Override
  public void removeTableModelListener(final TableModelListener l) {
    this.listenerList.remove(TableModelListener.class, l);
  }

  public void fireTableChanged(final TableModelEvent e) {
    final Object[] listeners = this.listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      try {
        if (listeners[i] == TableModelListener.class) {
          ((TableModelListener) listeners[i + 1]).tableChanged(e);
        }
      } catch (ArrayIndexOutOfBoundsException exception) {
        throw exception;
      }
    }
  }
}
