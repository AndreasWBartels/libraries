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
package net.anwiba.commons.swing.table.action;

import javax.swing.Action;

import net.anwiba.commons.lang.primativ.IBooleanProvider;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public abstract class AbstractTableActionFactory<T> implements ITableActionFactory<T> {
  @Override
  public Action create(
      final IObjectTableModel<T> tableModel,
      final ISelectionIndexModel<T> selectionIndexModel,
      final ISelectionModel<T> selectionModel,
      final IBooleanDistributor sortStateModel) {
    final Action action = createAction(tableModel, selectionIndexModel, selectionModel, sortStateModel);
    action.setEnabled(checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateModel));
    tableModel.addListModelListener(new IChangeableListListener<T>() {

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<T> object) {
        GuiUtilities.invokeLater(
            () -> action.setEnabled(checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateModel)));
      }

      @Override
      public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<T> object) {
        GuiUtilities.invokeLater(
            () -> action.setEnabled(checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateModel)));
      }

      @Override
      public void objectsUpdated(
          final Iterable<Integer> indeces,
          final Iterable<T> oldObjects,
          final Iterable<T> newObjects) {
        GuiUtilities.invokeLater(
            () -> action.setEnabled(checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateModel)));
      }

      @Override
      public void objectsChanged(final Iterable<T> oldObjects, final Iterable<T> newObjects) {
        GuiUtilities.invokeLater(
            () -> action.setEnabled(checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateModel)));
      }
    });
    selectionIndexModel.addSelectionListener(new ISelectionListener<T>() {

      @Override
      public void selectionChanged(final SelectionEvent<T> event) {
        GuiUtilities.invokeLater(
            () -> action.setEnabled(checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateModel)));
      }
    });
    sortStateModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        GuiUtilities.invokeLater(
            () -> action.setEnabled(checkEnabled(tableModel, selectionIndexModel, selectionModel, sortStateModel)));
      }
    });
    return action;
  }

  protected abstract boolean checkEnabled(
      final IObjectTableModel<T> tableModel,
      final ISelectionIndexModel<T> selectionIndexModel,
      final ISelectionModel<T> selectionModel,
      final IBooleanProvider sortStateProvider);

  protected abstract Action createAction(
      final IObjectTableModel<T> tableModel,
      final ISelectionIndexModel<T> selectionIndexModel,
      final ISelectionModel<T> selectionModel,
      IBooleanDistributor sortStateProvider);

}
