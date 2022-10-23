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

import net.anwiba.commons.model.IObjectModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetMetaDataAdapter {

  public static ResultSetMetaDataAdapter create(
      final ResultSetAdapter resultSetAdapter) {
    if (resultSetAdapter == null || resultSetAdapter == ResultSetAdapter.empty) {
      return new ResultSetMetaDataAdapter(
          0,
          List.of(), //$NON-NLS-1$
          List.of(), //$NON-NLS-1$
          List.of(), //$NON-NLS-1$
          List.of());
    }
    return new ResultSetMetaDataAdapter(
        resultSetAdapter.getColumnCount(),
        resultSetAdapter.getColumnNames(),
        resultSetAdapter.getColumnClasses(),
        resultSetAdapter.getColumnTypeNames(),
        resultSetAdapter.getColumnTypes());
  }

  public static ResultSetMetaDataAdapter create(final IObjectModel<String> statusModel,
      final ResultSetMetaData metaData) {

    if (metaData == null) {
      return new ResultSetMetaDataAdapter(
          0,
          List.of(), //$NON-NLS-1$
          List.of(), //$NON-NLS-1$
          List.of(), //$NON-NLS-1$
          List.of());
    }

    try {

      final List<String> columnNames = new ArrayList<>();
      @SuppressWarnings("rawtypes")
      final List<Class> columnClasses = new ArrayList<>();
      final List<Integer> columnTypes = new ArrayList<>();
      final List<String> columnTypeNames = new ArrayList<>();

      final int columnCount = metaData.getColumnCount();
      for (int i = 1; i < columnCount + 1; i++) {
        columnNames.add(metaData.getColumnName(i));
        columnClasses.add(getColumnClassForName(metaData.getColumnClassName(i)));
        columnTypes.add(metaData.getColumnType(i));
        columnTypeNames.add(metaData.getColumnTypeName(i));
      }

      return new ResultSetMetaDataAdapter(
          columnCount,
          columnNames,
          columnClasses,
          columnTypeNames,
          columnTypes);

    } catch (final Exception exception) {
      statusModel.set(exception.getMessage());
      return new ResultSetMetaDataAdapter(
          0,
          List.of(), //$NON-NLS-1$
          List.of(), //$NON-NLS-1$
          List.of(), //$NON-NLS-1$
          List.of());
    }
  }

  protected static List<List<Object>> getRows(final List<Integer> columnTypes,
      final List<String> columnTypeNames,
      final int columnCount,
      final ResultSet set) throws SQLException {
    final List<List<Object>> rows = new ArrayList<>();
    final ResultSetToRowConverter converter = new ResultSetToRowConverter(
        columnTypes,
        columnTypeNames,
        columnCount);
    while (set.next()) {
      rows.add(converter.convert(set));
    }
    return rows;
  }

  private static Class<?> getColumnClassForName(final String columnClassName) {
    try {
      return Class.forName(columnClassName);
    } catch (final ClassNotFoundException exception) {
      return Object.class;
    }
  }

  private final int rowCount;
  private final int columnCount;
  private final List<String> columnNames;
  @SuppressWarnings("rawtypes")
  private final List<Class> columnClasses;
  private final List<String> columnTypeNames;
  private final List<Integer> columnTypes;

  public ResultSetMetaDataAdapter(
      final int rowCount,
      final List<String> columnNames,
      @SuppressWarnings("rawtypes") final List<Class> columnClasses,
      final List<String> columnTypeNames,
      final List<Integer> columnTypes) {
    this.rowCount = rowCount;
    this.columnCount = 4;
    this.columnTypes = columnTypes;
    this.columnNames = columnNames;
    this.columnClasses = columnClasses;
    this.columnTypeNames = columnTypeNames;
  }

  public int getRowCount() {
    return this.rowCount;
  }

  public int getColumnCount() {
    return this.columnCount;
  }

  public String getColumnName(final int columnIndex) {
    return switch (columnIndex) {
      case 0 -> "column name";
      case 1 -> "type";
      case 2 -> "type name";
      case 3 -> "class";
      default -> null;
    };
  }

  public Class<?> getColumnClass(final int columnIndex) {
    return switch (columnIndex) {
      case 0 -> String.class;
      case 1 -> Integer.class;
      case 2 -> String.class;
      case 3 -> Class.class;
      default -> Object.class;
    };
  }

  public Object getValueAt(final int rowIndex, final int columnIndex) {
    return switch (columnIndex) {
      case 0 -> rowIndex >= this.columnNames.size() ? null : this.columnNames.get(rowIndex);
      case 1 -> rowIndex >= this.columnTypes.size() ? null : this.columnTypes.get(rowIndex);
      case 2 -> rowIndex >= this.columnTypeNames.size() ? null : this.columnTypeNames.get(rowIndex);
      case 3 -> rowIndex >= this.columnClasses.size() ? null : this.columnClasses.get(rowIndex);
      default -> null;
    };
  }
}
