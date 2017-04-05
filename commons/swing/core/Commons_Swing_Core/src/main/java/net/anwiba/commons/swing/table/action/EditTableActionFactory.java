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

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.anwiba.commons.model.IBooleanProvider;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.swing.action.ActionCustomization;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;

public class EditTableActionFactory<T> extends AbstractTableActionFactory<T> {
  private final ITableActionClosure<T> closure;

  public EditTableActionFactory(final ITableActionClosure<T> factory) {
    this.closure = factory;
  }

  @Override
  protected final AbstractAction createAction(
      final IObjectTableModel<T> tableModel,
      final ISelectionModel<T> selectionModel,
      final ISelectionIndexModel<T> selectionIndexModel) {
    return new TableActionClosureAction<>(
        new ActionCustomization(null, GuiIcons.EDIT_ICON, "edit"),
        tableModel,
        selectionIndexModel,
        this.closure);
  }

  @Override
  protected void checkEnabled(
      final Action action,
      final IObjectTableModel<T> tableModel,
      final ISelectionIndexModel<T> selectionIndexModel,
      final ISelectionModel<T> selectionModel,
      final IBooleanProvider sortStateProvider) {
    action.setEnabled(!sortStateProvider.get() && selectionIndexModel.size() == 1);
  }
}