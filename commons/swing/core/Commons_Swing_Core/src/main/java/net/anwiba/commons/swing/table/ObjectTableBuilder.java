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

import net.anwiba.commons.lang.comparable.NumberComparator;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.swing.table.action.ITableActionFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.swing.table.renderer.BooleanRenderer;
import net.anwiba.commons.swing.table.renderer.NumberTableCellRenderer;
import net.anwiba.commons.swing.table.renderer.ObjectTableCellRenderer;

public class ObjectTableBuilder<T> implements IObjectTableBuilder<T> {

  final ObjectListTableConfigurationBuilder<T> builder = new ObjectListTableConfigurationBuilder<>();
  private final List<T> values = new ArrayList<>();

  @Override
  public IObjectTableBuilder<T> setKeyListenerFactory(final IKeyListenerFactory<T> keyListenerFactory) {
    this.builder.setKeyListenerFactory(keyListenerFactory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setSelectionMode(final int selectionMode) {
    this.builder.setSelectionMode(selectionMode);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addColumnConfiguration(final IObjectListColumnConfiguration<T> columnConfiguration) {
    this.builder.addColumnConfiguration(columnConfiguration);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableStringConfiguration(

      final String title,
      final IFunction<T, String, RuntimeException> provider,
      final int size) {

    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, new IColumnValueProvider<T>() {

      @Override
      public Object getValue(final T object) {
        if (object == null) {
          return null;
        }
        return provider.execute(object);
      }
    }, new ObjectTableCellRenderer(), size, String.class, true, null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableDoubleConfiguration(
      final String title,
      final IFunction<T, Double, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, new IColumnValueProvider<T>() {

      @Override
      public Object getValue(final T object) {
        if (object == null) {
          return null;
        }
        return provider.execute(object);
      }
    }, new NumberTableCellRenderer("0.0000"), size, Double.class, true, new NumberComparator())); //$NON-NLS-1$
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableBooleanConfiguration(
      final String title,
      final IFunction<T, Boolean, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, new IColumnValueProvider<T>() {

      @Override
      public Object getValue(final T object) {
        if (object == null) {
          return null;
        }
        return provider.execute(object);
      }
    }, new BooleanRenderer(), size, Boolean.class, true, null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addActionFactory(final ITableActionFactory<T> factory) {
    this.builder.addActionFactory(factory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setPreferredVisibleRowCount(final int preferredVisibleRowCount) {
    this.builder.setPreferredVisibleRowCount(preferredVisibleRowCount);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setMouseListenerFactory(final IMouseListenerFactory<T> mouseListenerFactory) {
    this.builder.setMouseListenerFactory(mouseListenerFactory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addAddObjectAction(final IColumnObjectFactory<T, T, RuntimeException> factory) {
    this.builder.addAddObjectAction(factory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addEditObjectAction(final IColumnObjectFactory<T, T, RuntimeException> factory) {
    this.builder.addEditObjectAction(factory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addRemoveObjectsAction() {
    this.builder.addRemoveObjectsAction();
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addMoveObjectUpAction() {
    this.builder.addMoveObjectUpAction();
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addMoveObjectDownAction() {
    this.builder.addMoveObjectDownAction();
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setFilterToStringConverter(final IColumToStringConverter columnToStringConverter) {
    this.builder.setFilterToStringConverter(columnToStringConverter);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setValues(final List<T> values) {
    this.values.clear();
    this.values.addAll(values);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addValue(final T value) {
    this.values.add(value);
    return this;
  }

  @Override
  public ObjectListTable<T> build() {
    final IObjectListTableConfiguration<T> configuration = this.builder.build();
    return new ObjectListTable<>(configuration, this.values);
  }

  @Override
  public IObjectTableBuilder<T> setSingleSelectionMode() {
    this.builder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setAutoResizeModeOff() {
    this.builder.setAutoResizeModeOff();
    return this;
  }
}
