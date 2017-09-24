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

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;

import net.anwiba.commons.swing.table.action.ITableActionFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;

public class ObjectTableBuilder<T> {

  final ObjectListTableConfigurationBuilder<T> builder = new ObjectListTableConfigurationBuilder<>();
  private final List<T> values = new ArrayList<>();

  public ObjectTableBuilder<T> setKeyListenerFactory(final IKeyListenerFactory<T> keyListenerFactory) {
    this.builder.setKeyListenerFactory(keyListenerFactory);
    return this;
  }

  public ObjectTableBuilder<T> setSelectionMode(final int selectionMode) {
    this.builder.setSelectionMode(selectionMode);
    return this;
  }

  public ObjectTableBuilder<T> addColumnConfiguration(final IObjectListColumnConfiguration<T> columnConfiguration) {
    this.builder.addColumnConfiguration(columnConfiguration);
    return this;
  }

  public ObjectTableBuilder<T> addActionFactory(final ITableActionFactory<T> factory) {
    this.builder.addActionFactory(factory);
    return this;
  }

  public ObjectTableBuilder<T> setPreferredVisibleRowCount(final int preferredVisibleRowCount) {
    this.builder.setPreferredVisibleRowCount(preferredVisibleRowCount);
    return this;
  }

  public ObjectTableBuilder<T> setMouseListenerFactory(final IMouseListenerFactory<T> mouseListenerFactory) {
    this.builder.setMouseListenerFactory(mouseListenerFactory);
    return this;
  }

  public ObjectTableBuilder<T> addAddObjectAction(final IColumnObjectFactory<T, T, RuntimeException> factory) {
    this.builder.addAddObjectAction(factory);
    return this;
  }

  public ObjectTableBuilder<T> addEditObjectAction(final IColumnObjectFactory<T, T, RuntimeException> factory) {
    this.builder.addEditObjectAction(factory);
    return this;
  }

  public ObjectTableBuilder<T> addRemoveObjectsAction() {
    this.builder.addRemoveObjectsAction();
    return this;
  }

  public ObjectTableBuilder<T> addMoveObjectUpAction() {
    this.builder.addMoveObjectUpAction();
    return this;
  }

  public ObjectTableBuilder<T> addMoveObjectDownAction() {
    this.builder.addMoveObjectDownAction();
    return this;
  }

  public ObjectTableBuilder<T> setFilterToStringConverter(final IColumToStringConverter columnToStringConverter) {
    this.builder.setFilterToStringConverter(columnToStringConverter);
    return this;
  }

  public ObjectTableBuilder<T> setValues(final List<T> values) {
    this.values.clear();
    this.values.addAll(values);
    return this;
  }

  public ObjectTableBuilder<T> addValue(final T value) {
    this.values.add(value);
    return this;
  }

  public ObjectListTable<T> build() {
    final IObjectListTableConfiguration<T> configuration = this.builder.build();
    return new ObjectListTable<>(configuration, this.values);
  }

  public ObjectTableBuilder<T> setSingleSelectionMode() {
    this.builder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    return this;
  }

  public ObjectTableBuilder<T> setAutoResizeModeOff() {
    this.builder.setAutoResizeModeOff();
    return this;
  }
}
