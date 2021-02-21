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
package net.anwiba.database.swing.console.result;

import java.sql.ResultSet;

import javax.swing.table.AbstractTableModel;

import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public final class ResultSetTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;
  private final IObjectModel<String> statusModel;
  private ResultSetAdapter result;

  public ResultSetTableModel(final IObjectModel<String> statusModel, final IObjectModel<ResultSet> resultSetModel) {
    this.statusModel = statusModel;
    initialize(resultSetModel.get());
    this.result = ResultSetAdapter.create(this.statusModel, null);
    resultSetModel.addChangeListener(() -> initialize(resultSetModel.get()));
  }

  private void initialize(final ResultSet set) {
    synchronized (this) {
      this.result = ResultSetAdapter.create(this.statusModel, set);
    }
    GuiUtilities.invokeLater(() -> {
      fireTableStructureChanged();
      fireTableDataChanged();
    });
  }

  @Override
  public int getRowCount() {
    synchronized (this) {
      return this.result.getRowCount();
    }
  }

  @Override
  public int getColumnCount() {
    synchronized (this) {
      return this.result.getColumnCount();
    }
  }

  @Override
  public String getColumnName(final int columnIndex) {
    synchronized (this) {
      return this.result.getColumnName(columnIndex);
    }
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    synchronized (this) {
      return this.result.getColumnClass(columnIndex);
    }
  }

  public String getColumnTypeName(final int columnIndex) {
    synchronized (this) {
      return this.result.getColumnTypeName(columnIndex);
    }
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    synchronized (this) {
      return this.result.getValueAt(rowIndex, columnIndex);
    }
  }
}
