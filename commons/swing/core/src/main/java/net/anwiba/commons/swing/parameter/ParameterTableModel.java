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
package net.anwiba.commons.swing.parameter;

import javax.swing.table.AbstractTableModel;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.parameter.Parameter;
import net.anwiba.commons.utilities.parameter.Parameters;

public class ParameterTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;
  private IParameters parameters = Parameters.empty();
  private final boolean isEditable;

  public ParameterTableModel() {
    this(false);
  }

  public ParameterTableModel(final boolean isEditable) {
    this.isEditable = isEditable;
  }

  public void setParameters(final IParameters parameters) {
    this.parameters = parameters == null ? Parameters.empty() : parameters;
    fireTableDataChanged();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public int getRowCount() {
    return this.parameters.getNumberOfParameter();
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    switch (columnIndex) {
      case 0:
      case 1:
        return String.class;
      default:
        throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    if (!this.isEditable && !(-1 < rowIndex && rowIndex < this.parameters.getNumberOfParameter())) {
      return false;
    }
    switch (columnIndex) {
      case 0:
        return false;
      case 1:
        return true;
      default:
        throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public String getColumnName(final int column) {
    switch (column) {
      case 0:
        return DialogMessages.NAME;
      case 1:
        return DialogMessages.VALUE;
      default:
        throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
    if (!this.isEditable && !(-1 < rowIndex && rowIndex < this.parameters.getNumberOfParameter())) {
      throw new IllegalArgumentException();
    }
    final IParameter parameter = this.parameters.getParameter(rowIndex);
    this.parameters = this.parameters
        .adapt(rowIndex, Parameter.of(parameter.getName(), ObjectUtilities.toString(value)));
    fireTableCellUpdated(rowIndex, columnIndex);
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    switch (columnIndex) {
      case 0:
        return this.parameters.getParameter(rowIndex).getName();
      case 1:
        return this.parameters.getParameter(rowIndex).getValue();
      default:
        throw new UnreachableCodeReachedException();
    }
  }

  public IParameters getParameters() {
    return this.parameters;
  }

}
