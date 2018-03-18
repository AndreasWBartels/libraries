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
package net.anwiba.commons.swing.table;

import java.util.List;

import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.swing.table.action.ITableActionFactory;
import net.anwiba.commons.swing.table.action.ITableTextFieldActionFactory;
import net.anwiba.commons.swing.table.action.ITableTextFieldKeyListenerFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;

public interface IObjectTableBuilder<T> {

  IObjectTableBuilder<T> setKeyListenerFactory(IKeyListenerFactory<T> keyListenerFactory);

  IObjectTableBuilder<T> setSelectionMode(int selectionMode);

  IObjectTableBuilder<T> addColumnConfiguration(IObjectListColumnConfiguration<T> columnConfiguration);

  IObjectTableBuilder<T> addTextFieldActionFactory(ITableTextFieldActionFactory<T> factory);

  IObjectTableBuilder<T> addActionFactory(ITableActionFactory<T> factory);

  IObjectTableBuilder<T> setPreferredVisibleRowCount(int preferredVisibleRowCount);

  IObjectTableBuilder<T> setMouseListenerFactory(IMouseListenerFactory<T> mouseListenerFactory);

  IObjectTableBuilder<T> addAddObjectAction(IColumnObjectFactory<T, T, RuntimeException> factory);

  IObjectTableBuilder<T> addEditObjectAction(IColumnObjectFactory<T, T, RuntimeException> factory);

  IObjectTableBuilder<T> addRemoveObjectsAction();

  IObjectTableBuilder<T> addMoveObjectUpAction();

  IObjectTableBuilder<T> addMoveObjectDownAction();

  IObjectTableBuilder<T> setFilterToStringConverter(IColumToStringConverter columnToStringConverter);

  IObjectTableBuilder<T> setValues(List<T> values);

  IObjectTableBuilder<T> addValue(T value);

  ObjectListTable<T> build();

  IObjectTableBuilder<T> setSingleSelectionMode();

  IObjectTableBuilder<T> setAutoResizeModeOff();

  IObjectTableBuilder<T> addSortableStringConfiguration(
      String title,
      IFunction<T, String, RuntimeException> provider,
      int size);

  IObjectTableBuilder<T> addSortableDoubleConfiguration(
      String title,
      IFunction<T, Double, RuntimeException> provider,
      int size);

  IObjectTableBuilder<T> addSortableBooleanConfiguration(
      String title,
      IFunction<T, Boolean, RuntimeException> provider,
      int size);

  IObjectTableBuilder<T> addSortableStringConfiguration(
      String title,
      IFunction<T, String, RuntimeException> provider,
      IAggregator<T, String, T, RuntimeException> adaptor,
      int size);

  IObjectTableBuilder<T> setTextFieldKeyListenerFactory(
      ITableTextFieldKeyListenerFactory<T> textFieldKeyListenerFactory);

}
