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

import java.awt.Component;
import java.awt.event.ActionEvent;

import net.anwiba.commons.swing.action.AbstractCustomizedAction;
import net.anwiba.commons.swing.action.IActionCustomization;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;

@SuppressWarnings("serial")
public final class TableActionClosureAction<T> extends AbstractCustomizedAction {
  private final IObjectTableModel<T> tableModel;
  private final ISelectionIndexModel<T> selectionIndexModel;
  private final ITableActionClosure<T> closure;

  public TableActionClosureAction(
    final IActionCustomization customization,
    final IObjectTableModel<T> tableModel,
    final ISelectionIndexModel<T> selectionIndexModel,
    final ITableActionClosure<T> closure) {
    super(customization);
    this.tableModel = tableModel;
    this.selectionIndexModel = selectionIndexModel;
    this.closure = closure;
  }

  @Override
  public void execute(final Component component, final ActionEvent event) {
    this.closure.execute(component, this.tableModel, this.selectionIndexModel);
  }
}