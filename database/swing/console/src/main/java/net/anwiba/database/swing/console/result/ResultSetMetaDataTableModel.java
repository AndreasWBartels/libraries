/*
 * #%L
 * anwiba database
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
package net.anwiba.database.swing.console.result;

import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

import javax.swing.table.AbstractTableModel;

public class ResultSetMetaDataTableModel extends AbstractTableModel {

  private ResultSetMetaDataAdapter result;

  public ResultSetMetaDataTableModel(final IObjectModel<ResultSetAdapter> resultSetModel) {
    initialize(resultSetModel.get());
    this.result = ResultSetMetaDataAdapter.create(null);
    resultSetModel.addChangeListener(() -> initialize(resultSetModel.get()));
  }

  private void initialize(final ResultSetAdapter metaData) {
    synchronized (this) {
      this.result = ResultSetMetaDataAdapter.create(metaData);
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

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    synchronized (this) {
      return this.result.getValueAt(rowIndex, columnIndex);
    }
  }

}
