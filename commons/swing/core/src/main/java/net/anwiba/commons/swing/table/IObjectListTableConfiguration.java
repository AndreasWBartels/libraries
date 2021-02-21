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

import javax.swing.JComponent;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.swing.table.action.ITableTextFieldActionConfiguration;
import net.anwiba.commons.swing.table.action.ITableTextFieldKeyListenerFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;

public interface IObjectListTableConfiguration<T> extends IObjectTableConfiguration<T> {

  List<IColumnValueProvider<T>> getColumnValueProviders();

  List<IColumnValueAdaptor<T>> getColumnValueAdaptors();

  boolean isFilterable();

  IColumToStringConverter getRowFilterToStringConverter();

  IColumnClassProvider getColumnClassProvider();

  ITableTextFieldActionConfiguration<T> getTextFieldActionConfiguration();

  ITableTextFieldKeyListenerFactory<T> getTextFieldKeyListenerFactory();

  default boolean isTextFieldEnable() {
    return getRowFilterToStringConverter() != null || getTextFieldKeyListenerFactory() != null
        || !getTextFieldActionConfiguration().isEmpty();
  }

  IObjectDistributor<IAcceptor<T>> getRowFilterDistributor();

  IFactory<IObjectTableModel<T>, JComponent, RuntimeException> getAccessoryHeaderPanelFactory();

}
