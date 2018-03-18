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

import net.anwiba.commons.swing.table.action.ITableTextFieldActionConfiguration;
import net.anwiba.commons.swing.table.action.ITableTextFieldKeyListenerFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;

public interface IObjectListTableConfiguration<T> extends IObjectTableConfiguration<T> {

  public List<IColumnValueProvider<T>> getColumnValueProviders();

  public List<IColumnValueAdaptor<T>> getColumnValueAdaptors();

  public boolean isFilterable();

  public IColumToStringConverter getRowFilterToStringConverter();

  public IColumnClassProvider getColumnClassProvider();

  public ITableTextFieldActionConfiguration<T> getTextFieldActionConfiguration();

  public ITableTextFieldKeyListenerFactory<T> getTextFieldKeyListenerFactory();

  default boolean isTextFieldEnable() {
    return isFilterable() || getTextFieldKeyListenerFactory() != null || !getTextFieldActionConfiguration().isEmpty();
  }

}
