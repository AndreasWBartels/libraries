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

import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.anwiba.commons.swing.ui.IObjectUi;
import net.anwiba.commons.swing.ui.ObjectUiTableCellRenderer;

public class ObjectListColumnConfiguration<T> extends ColumnConfiguration implements IObjectListColumnConfiguration<T> {

  private final IColumnValueProvider<T> columnValueProvider;
  private IColumnValueAdaptor<T> columnValueAdaptor;
  private Class<?> clazz;

  public ObjectListColumnConfiguration(
      final Object headerValue,
      final IColumnValueProvider<T> columnValueProvider,
      final IObjectUi<T> objectUi,
      final int preferredWidth,
      final Class<?> clazz,
      final boolean isSortable,
      @SuppressWarnings("rawtypes") final Comparator comparator) {
    this(
        headerValue,
        columnValueProvider,
        new ObjectUiTableCellRenderer<>(objectUi),
        null,
        null,
        preferredWidth,
        clazz,
        isSortable,
        comparator);
  }

  public ObjectListColumnConfiguration(
      final Object headerValue,
      final IColumnValueProvider<T> columnValueProvider,
      final TableCellRenderer cellRenderer,
      final int preferredWidth,
      final Class<?> clazz,
      final boolean isSortable,
      @SuppressWarnings("rawtypes") final Comparator comparator) {
    this(headerValue, columnValueProvider, cellRenderer, null, null, preferredWidth, clazz, isSortable, comparator);
  }

  public ObjectListColumnConfiguration(
      final Object headerValue,
      final IColumnValueProvider<T> columnValueProvider,
      final IObjectUi<T> objectUi,
      final IColumnValueAdaptor<T> columnValueAdaptor,
      final TableCellEditor cellEditor,
      final int preferredWidth,
      final Class<?> clazz,
      final boolean isSortable,
      @SuppressWarnings("rawtypes") final Comparator comparator) {
    this(
        headerValue,
        columnValueProvider,
        new ObjectUiTableCellRenderer<>(objectUi),
        columnValueAdaptor,
        cellEditor,
        preferredWidth,
        clazz,
        isSortable,
        comparator);
  }

  public ObjectListColumnConfiguration(
      final Object headerValue,
      final IColumnValueProvider<T> columnValueProvider,
      final TableCellRenderer cellRenderer,
      final IColumnValueAdaptor<T> columnValueAdaptor,
      final TableCellEditor cellEditor,
      final int preferredWidth,
      final Class<?> clazz,
      final boolean isSortable,
      @SuppressWarnings("rawtypes") final Comparator comparator) {
    super(headerValue, cellRenderer, cellEditor, preferredWidth, isSortable, comparator);
    this.columnValueProvider = columnValueProvider;
    this.columnValueAdaptor = columnValueAdaptor;
    this.clazz = clazz;
  }

  public ObjectListColumnConfiguration(
      final Object headerValue,
      final IColumnValueProvider<T> columnValueProvider,
      final TableCellRenderer cellRenderer,
      final int preferredWidth,
      final boolean isSortable,
      @SuppressWarnings("rawtypes") final Comparator comparator) {
    this(headerValue, columnValueProvider, cellRenderer, null, null, preferredWidth, isSortable, comparator);
  }

  public ObjectListColumnConfiguration(
      final Object headerValue,
      final IColumnValueProvider<T> columnValueProvider,
      final TableCellRenderer cellRenderer,
      final IColumnValueAdaptor<T> columnValueAdaptor,
      final TableCellEditor cellEditor,
      final int preferredWidth,
      final boolean isSortable,
      @SuppressWarnings("rawtypes") final Comparator comparator) {
    this(
        headerValue,
        columnValueProvider,
        cellRenderer,
        columnValueAdaptor,
        cellEditor,
        preferredWidth,
        Object.class,
        isSortable,
        comparator);
  }

  @Override
  public IColumnValueProvider<T> getColumnValueProvider() {
    return this.columnValueProvider;
  }

  @Override
  public IColumnValueAdaptor<T> getColumnValueAdaptor() {
    return this.columnValueAdaptor;
  }

  @Override
  public Class<?> getColumnClass() {
    return this.clazz;
  }

}
