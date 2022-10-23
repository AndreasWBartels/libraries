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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResultSetAdapter {

  static ResultSetAdapter empty = new ResultSetAdapter(
      0,
      1,
      Arrays.asList("empty"), //$NON-NLS-1$
      Arrays.asList(Object.class),
      Arrays.asList("object"),
      Collections.emptyList(),
      null,
      Collections.emptyList());

  public static ResultSetAdapter create(final IObjectModel<String> statusModel, final ResultSet set) {

    if (set == null) {
      return empty;
    }

    try {

      int rowCount = 0;
      final List<String> columnNames = new ArrayList<>();
      @SuppressWarnings("rawtypes")
      final List<Class> columnClasses = new ArrayList<>();
      final List<Integer> columnTypes = new ArrayList<>();
      final List<String> columnTypeNames = new ArrayList<>();
      ResultSet resultSet = null;
      List<List<Object>> resultList = null;

      final ResultSetMetaData metaData = set.getMetaData();
      final int columnCount = metaData.getColumnCount();
      for (int i = 1; i < columnCount + 1; i++) {
        columnNames.add(metaData.getColumnName(i));
        columnClasses.add(getColumnClassForName(metaData.getColumnClassName(i)));
        columnTypes.add(metaData.getColumnType(i));
        columnTypeNames.add(getColumnTypeName(metaData, i));
      }

      final int scrollType = set.getType();
      final int concurrency = set.getConcurrency();
      if ((scrollType == ResultSet.TYPE_SCROLL_INSENSITIVE || scrollType == ResultSet.TYPE_SCROLL_SENSITIVE)
          && (concurrency == ResultSet.CONCUR_READ_ONLY || concurrency == ResultSet.CONCUR_UPDATABLE)) {
        set.beforeFirst();
        int start = set.getRow();
        set.last();
        rowCount = set.getRow();
        resultSet = set;
        if (set.absolute(start) && start == set.getRow()) {
          resultSet = set;
        } else {
          set.beforeFirst();
          if (start == set.getRow()) {
            resultSet = set;
          } else {
            resultList = getRows(columnTypes, columnTypeNames, columnCount, set);
            rowCount = resultList.size();
          }
        }
      } else {
        resultList = getRows(columnTypes, columnTypeNames, columnCount, set);
        rowCount = resultList.size();
      }

      return new ResultSetAdapter(
          rowCount,
          columnCount,
          columnNames,
          columnClasses,
          columnTypeNames,
          columnTypes,
          resultSet,
          resultList);

    } catch (final Exception exception) {
      statusModel.set(exception.getMessage());
      return empty;
    }
  }

  private static String getColumnTypeName(final ResultSetMetaData metaData, final int i) throws SQLException {
    long precision = metaData.getPrecision(i);
    long scale = metaData.getScale(i);
    final String columnTypeName = metaData.getColumnTypeName(i);
    return columnTypeName + typeNameConstaints(precision, scale);
  }

  private static String typeNameConstaints(final long precision, final long scale) {
    if (precision == 0) {
      return "";
    }
    if (scale == 0) {
      return "(" + precision + ")";
    }
    return "(" + precision + "," + scale + ")";
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
  private final ResultSet resultSet;
  private final List<List<Object>> resultList;
  private final List<Integer> columnTypes;

  public ResultSetAdapter(
      final int rowCount,
      final int columnCount,
      final List<String> columnNames,
      @SuppressWarnings("rawtypes") final List<Class> columnClasses,
      final List<String> columnTypeNames,
      final List<Integer> columnTypes,
      final ResultSet resultSet,
      final List<List<Object>> resultList) {
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    this.columnNames = columnNames;
    this.columnClasses = columnClasses;
    this.columnTypeNames = columnTypeNames;
    this.columnTypes = columnTypes;
    this.resultSet = resultSet;
    this.resultList = resultList;
  }

  public int getRowCount() {
    return this.rowCount;
  }

  public int getColumnCount() {
    return this.columnCount;
  }

  public List<String> getColumnNames() {
    return this.columnNames;
  }

  public List<String> getColumnTypeNames() {
    return this.columnTypeNames;
  }

  public List<Integer> getColumnTypes() {
    return this.columnTypes;
  }

  public List<Class> getColumnClasses() {
    return this.columnClasses;
  }

  public String getColumnName(final int columnIndex) {
    return columnIndex < 0 || columnIndex >= this.columnNames.size() ? null : this.columnNames.get(columnIndex);
  }

  public Class<?> getColumnClass(final int columnIndex) {
    synchronized (this) {
      return columnIndex < 0 || columnIndex >= this.columnClasses.size()
          ? Object.class
          : this.columnClasses.get(columnIndex);
    }
  }

  public String getColumnTypeName(final int columnIndex) {
    return columnIndex < 0 || columnIndex >= this.columnTypeNames.size() ? null : this.columnTypeNames.get(columnIndex);
  }

  public Object getValueAt(final int rowIndex, final int columnIndex) {
    if (this.resultList != null) {
      if (rowIndex >= this.resultList.size()) {
        return null;
      }
      final List<Object> row = this.resultList.get(rowIndex);
      if (columnIndex >= row.size()) {
        return null;
      }
      return row.get(columnIndex);
    }
    if (this.resultSet == null) {
      return null;
    }
    try {
      if (this.resultSet.absolute(rowIndex + 1)) {
        return this.resultSet.getObject(columnIndex + 1);
      }
      this.resultSet.beforeFirst();
      this.resultSet.relative(rowIndex + 1);
      return this.resultSet.getObject(columnIndex + 1);
    } catch (final SQLException exception) {
      return null;
    }
  }
}
