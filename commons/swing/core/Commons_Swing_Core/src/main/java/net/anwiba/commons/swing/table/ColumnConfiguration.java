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

@SuppressWarnings("rawtypes")
public class ColumnConfiguration implements IColumnConfiguration {
  private final int preferredWidth;
  private final TableCellRenderer cellRenderer;
  private final boolean isSortable;
  private final Object headerValue;
  private final TableCellEditor cellEditor;
  private final Comparator comparator;

  public ColumnConfiguration(
      final Object headerValue,
      final TableCellRenderer cellRenderer,
      final int preferredWidth,
      final boolean isSortable,
      final Comparator comparator) {
    this(headerValue, cellRenderer, null, preferredWidth, isSortable, comparator);
  }

  public ColumnConfiguration(
      final Object headerValue,
      final TableCellRenderer cellRenderer,
      final TableCellEditor cellEditor,
      final int preferredWidth,
      final boolean isSortable,
      final Comparator comparator) {
    this.headerValue = headerValue;
    this.cellRenderer = cellRenderer;
    this.cellEditor = cellEditor;
    this.preferredWidth = preferredWidth;
    this.isSortable = isSortable;
    this.comparator = comparator;
  }

  @Override
  public int getPreferredWidth() {
    return this.preferredWidth;
  }

  @Override
  public TableCellRenderer getCellRenderer() {
    return this.cellRenderer;
  }

  @Override
  public boolean isSortable() {
    return this.isSortable;
  }

  @Override
  public Object getHeaderValue() {
    return this.headerValue;
  }

  @Override
  public TableCellEditor getCellEditor() {
    return this.cellEditor;
  }

  @Override
  public Comparator getComparator() {
    return this.comparator;
  }
}
