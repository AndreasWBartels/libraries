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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.anwiba.commons.lang.primativ.IBooleanProvider;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.swing.table.action.AbstractTableActionFactory;
import net.anwiba.commons.swing.table.action.AddTableRowActionFactory;
import net.anwiba.commons.swing.table.action.EditTableActionFactory;
import net.anwiba.commons.swing.table.action.ITableActionClosure;
import net.anwiba.commons.swing.table.action.ITableActionConfiguration;
import net.anwiba.commons.swing.table.action.ITableActionFactory;
import net.anwiba.commons.swing.table.action.ITableCheckActionEnabledValidator;
import net.anwiba.commons.swing.table.action.ITableTextFieldActionConfiguration;
import net.anwiba.commons.swing.table.action.ITableTextFieldActionFactory;
import net.anwiba.commons.swing.table.action.ITableTextFieldKeyListenerFactory;
import net.anwiba.commons.swing.table.action.MoveTableRowDownActionFactory;
import net.anwiba.commons.swing.table.action.MoveTableRowUpActionFactory;
import net.anwiba.commons.swing.table.action.RemoveTableRowActionFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.utilities.string.IStringSubstituter;
import net.anwiba.commons.utilities.string.StringUtilities;

public class ObjectListTableConfigurationBuilder<T> {

  private int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  private final List<IObjectListColumnConfiguration<T>> columnConfigurations = new ArrayList<>();
  private final List<ITableActionFactory<T>> actionFactories = new ArrayList<>();
  private final List<ITableTextFieldActionFactory<T>> textFieldActionFactories = new ArrayList<>();
  private ITableTextFieldKeyListenerFactory<T> textFieldKeyListenerFactory;
  private int preferredVisibleRowCount = 10;
  private IMouseListenerFactory<T> mouseListenerFactory;
  private IKeyListenerFactory<T> keyListenerFactory;
  private IColumToStringConverter columnToStringConverter;
  private int autoRizeMode = -1;
  private IStringSubstituter toolTipSubstituter = s -> StringUtilities.substitute(s, 8, 120);

  public ObjectListTableConfigurationBuilder<T> setKeyListenerFactory(final IKeyListenerFactory<T> keyListenerFactory) {
    this.keyListenerFactory = keyListenerFactory;
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> setSelectionMode(final int selectionMode) {
    this.selectionMode = selectionMode;
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> addColumnConfiguration(
      final IObjectListColumnConfiguration<T> columnConfiguration) {
    this.columnConfigurations.add(columnConfiguration);
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> addActionFactory(final ITableActionFactory<T> factory) {
    this.actionFactories.add(factory);
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> addActionFactory(
      final ITableActionFactory<T> factory,
      final ITableCheckActionEnabledValidator<T> validator) {
    this.actionFactories.add(new AbstractTableActionFactory<T>() {

      @Override
      protected boolean checkEnabled(
          final IObjectTableModel<T> tableModel,
          final ISelectionIndexModel<T> selectionIndexModel,
          final ISelectionModel<T> selectionModel,
          final IBooleanProvider sortStateProvider) {
        return validator.checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateProvider);
      }

      @Override
      protected Action createAction(
          final IObjectTableModel<T> tableModel,
          final ISelectionIndexModel<T> selectionIndexModel,
          final ISelectionModel<T> selectionModel,
          final IBooleanDistributor sortStateProvider) {
        return factory.create(tableModel, selectionIndexModel, selectionModel, sortStateProvider);
      }
    });
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> addTextFieldActionFactory(
      final ITableTextFieldActionFactory<T> factory) {
    this.textFieldActionFactories.add(factory);
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> setPreferredVisibleRowCount(final int preferredVisibleRowCount) {
    this.preferredVisibleRowCount = preferredVisibleRowCount;
    return this;
  }

  public IObjectListTableConfiguration<T> build() {
    final ITableActionConfiguration<T> actionConfiguration = new TableActionConfiguration<>(this.actionFactories);
    final ITableTextFieldActionConfiguration<T> textFieldActionConfiguration = new TableTextFieldActionConfiguration<>(
        this.textFieldActionFactories);
    return new ObjectListTableConfiguration<>(
        this.toolTipSubstituter,
        this.columnToStringConverter,
        this.autoRizeMode,
        this.selectionMode,
        this.preferredVisibleRowCount,
        this.columnConfigurations,
        this.mouseListenerFactory,
        this.keyListenerFactory,
        actionConfiguration,
        textFieldActionConfiguration,
        this.textFieldKeyListenerFactory);
  }

  public ObjectListTableConfigurationBuilder<T> setMouseListenerFactory(
      final IMouseListenerFactory<T> mouseListenerFactory) {
    this.mouseListenerFactory = mouseListenerFactory;
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> addAddObjectAction(
      final IColumnObjectFactory<T, T, RuntimeException> factory) {
    return addActionFactory(new AddTableRowActionFactory<>(new ITableActionClosure<T>() {

      @SuppressWarnings("unchecked")
      @Override
      public void execute(
          final Component component,
          final IObjectTableModel<T> tableModel,
          final ISelectionIndexModel<T> selectionIndexModel) {
        final T current = getCurrentObject(tableModel, selectionIndexModel);
        final T object = factory.create(component, current);
        if (object == null) {
          return;
        }
        tableModel.add(object);
        selectionIndexModel.set(tableModel.size() - 1);
      }

      private T getCurrentObject(
          final IObjectTableModel<T> tableModel,
          final ISelectionIndexModel<T> selectionIndexModel) {
        if (selectionIndexModel.size() != 1) {
          return null;
        }
        final int index = selectionIndexModel.getMinimum();
        return tableModel.get(index);
      }
    }));
  }

  public ObjectListTableConfigurationBuilder<T> addEditObjectAction(
      final IColumnObjectFactory<T, T, RuntimeException> factory) {
    return addActionFactory(new EditTableActionFactory<>(new ITableActionClosure<T>() {

      @Override
      public void execute(
          final Component component,
          final IObjectTableModel<T> tableModel,
          final ISelectionIndexModel<T> selectionIndexModel) {
        if (selectionIndexModel.size() != 1) {
          return;
        }
        final int index = selectionIndexModel.getMinimum();
        final T current = tableModel.get(index);
        final T object = factory.create(component, current);
        tableModel.set(index, object);
        selectionIndexModel.set(index);
      }
    }));
  }

  public ObjectListTableConfigurationBuilder<T> addRemoveObjectsAction() {
    return addActionFactory(new RemoveTableRowActionFactory<T>());
  }

  public ObjectListTableConfigurationBuilder<T> addMoveObjectUpAction() {
    return addActionFactory(new MoveTableRowUpActionFactory<T>());
  }

  public ObjectListTableConfigurationBuilder<T> addMoveObjectDownAction() {
    return addActionFactory(new MoveTableRowDownActionFactory<T>());
  }

  public ObjectListTableConfigurationBuilder<T> setFilterToStringConverter(
      final IColumToStringConverter columnToStringConverter) {
    this.columnToStringConverter = columnToStringConverter;
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> setAutoResizeModeOff() {
    this.autoRizeMode = JTable.AUTO_RESIZE_OFF;
    return this;
  }

  final ObjectListTableConfigurationBuilder<T> setToolTipSubstituter(final IStringSubstituter stringSubstituter) {
    this.toolTipSubstituter = stringSubstituter;
    return this;
  }

  public ObjectListTableConfigurationBuilder<T> setTextFieldKeyListenerFactory(
      final ITableTextFieldKeyListenerFactory<T> textFieldKeyListenerFactory) {
    this.textFieldKeyListenerFactory = textFieldKeyListenerFactory;
    return this;
  }

}
