/*
 * #%L
 * *
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
package net.anwiba.commons.swing.database.console.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.anwiba.commons.model.IObjectModel;

public class ResultSetAdapter {

  public static ResultSetAdapter create(final IObjectModel<String> statusModel, final ResultSet set) {

    if (set == null) {
      return new ResultSetAdapter(
          0,
          1,
          Arrays.asList("empty"), //$NON-NLS-1$
          Arrays.asList(Object.class),
          Collections.emptyList(),
          null,
          Collections.emptyList());
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
        columnTypeNames.add(metaData.getColumnTypeName(i));
      }

      if ((set.getType() == ResultSet.TYPE_SCROLL_INSENSITIVE || set.getType() == ResultSet.TYPE_SCROLL_SENSITIVE)
          && (set.getConcurrency() == ResultSet.CONCUR_READ_ONLY
              || set.getConcurrency() == ResultSet.CONCUR_UPDATABLE)) {
        set.beforeFirst();
        set.last();
        rowCount = set.getRow();
        set.beforeFirst();
        resultSet = set;
      } else {
        int counter = 0;
        final List<List<Object>> rows = new ArrayList<>();
        final ReaultSetToRowConverter converter = new ReaultSetToRowConverter(
            columnTypes,
            columnTypeNames,
            columnCount);
        while (set.next()) {
          rows.add(converter.convert(set));
          counter++;
        }
        resultList = rows;
        rowCount = counter;
      }

      return new ResultSetAdapter(
          rowCount,
          columnCount,
          columnNames,
          columnClasses,
          columnTypeNames,
          resultSet,
          resultList);

    } catch (final Exception exception) {
      statusModel.set(exception.getMessage());
      return new ResultSetAdapter(
          0,
          1,
          Arrays.asList("empty"), //$NON-NLS-1$
          Arrays.asList(Object.class),
          Collections.emptyList(),
          null,
          Collections.emptyList());
    }
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

  public ResultSetAdapter(
      final int rowCount,
      final int columnCount,
      final List<String> columnNames,
      @SuppressWarnings("rawtypes") final List<Class> columnClasses,
      final List<String> columnTypeNames,
      final ResultSet resultSet,
      final List<List<Object>> resultList) {
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    this.columnNames = columnNames;
    this.columnClasses = columnClasses;
    this.columnTypeNames = columnTypeNames;
    this.resultSet = resultSet;
    this.resultList = resultList;
  }

  public int getRowCount() {
    return this.rowCount;
  }

  public int getColumnCount() {
    return this.columnCount;
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
      this.resultSet.absolute(rowIndex + 1);
      return this.resultSet.getObject(columnIndex + 1);
    } catch (final SQLException exception) {
      return null;
    }
  }
}
