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

import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.swing.action.ActionCustomization;
import net.anwiba.commons.swing.action.IActionCustomization;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;
import net.anwiba.commons.swing.table.ObjectListTableMessages;

public final class AddTableRowActionFactory<T> implements ITableActionFactory<T> {
  private final ITableActionClosure<T> closure;
  private IActionCustomization customization;

  public AddTableRowActionFactory(final ITableActionClosure<T> closure) {
    this(new ActionCustomization(null, GuiIcons.LIST_ADD, ObjectListTableMessages.add), closure);
  }

  public AddTableRowActionFactory(final IActionCustomization customization, final ITableActionClosure<T> closure) {
    this.customization = customization;
    this.closure = closure;
  }

  @Override
  public Action create(
      final IObjectTableModel<T> tableModel,
      final ISelectionIndexModel<T> selectionIndexModel,
      final ISelectionModel<T> selectionModel,
      final IBooleanDistributor sortStateModel) {
    return new TableActionClosureAction<>(this.customization, tableModel, selectionIndexModel, this.closure);
  }
}
