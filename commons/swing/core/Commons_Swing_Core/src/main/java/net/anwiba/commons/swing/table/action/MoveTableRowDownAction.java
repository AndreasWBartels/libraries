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

import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public final class MoveTableRowDownAction<T> extends AbstractAction {
  private final IObjectTableModel<T> tableModel;
  private final ISelectionIndexModel<T> selectionIndexModel;
  private static final long serialVersionUID = 1L;

  public MoveTableRowDownAction(
    final String name,
    final Icon icon,
    final String tooltipText,
    final IObjectTableModel<T> tableModel,
    final ISelectionIndexModel<T> selectionIndexModel) {
    super(name, icon);
    putValue(Action.SHORT_DESCRIPTION, tooltipText);
    this.tableModel = tableModel;
    this.selectionIndexModel = selectionIndexModel;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    final int index = this.selectionIndexModel.getMinimum();
    if (index < 0 && !(index < this.tableModel.getRowCount() - 1)) {
      return;
    }
    final T object = this.tableModel.get(index);
    this.tableModel.set(index, this.tableModel.get(index + 1));
    this.tableModel.set(index + 1, object);
    this.selectionIndexModel.set(index + 1);
  }
}