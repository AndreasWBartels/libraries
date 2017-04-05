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

import java.util.List;

public class ObjectListTableModel<T> extends AbstractObjectTableModel<T> {

  private final List<IColumnValueProvider<T>> providers;
  private final List<IColumnValueAdaptor<T>> adaptors;

  public ObjectListTableModel(
    final List<T> objects,
    final List<IColumnValueProvider<T>> providers,
    final List<IColumnValueAdaptor<T>> recievers) {
    super(objects);
    this.providers = providers;
    this.adaptors = recievers;
  }

  private static final long serialVersionUID = 1L;
  private boolean isChangeable = true;

  @Override
  public int getColumnCount() {
    return this.providers.size();
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final T object = get(rowIndex);
    final IColumnValueProvider<T> valueProvider = this.providers.get(columnIndex);
    return valueProvider.getValue(object);
  }

  @Override
  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
    if (this.adaptors.get(columnIndex) == null) {
      super.setValueAt(aValue, rowIndex, columnIndex);
      return;
    }
    final T object = get(rowIndex);
    T adapted = this.adaptors.get(columnIndex).adapt(object, aValue);
    set(rowIndex, adapted);
  }

  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    if (this.adaptors.get(columnIndex) == null) {
      return false;
    }
    return this.isChangeable;
  }

  public void setChangeable(final boolean isChangeable) {
    this.isChangeable = isChangeable;
  }

}
