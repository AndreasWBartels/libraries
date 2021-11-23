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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;
import net.anwiba.commons.utilities.collection.IterableUtilities;

@SuppressWarnings("serial")
public final class RemoveTableRowAction<T> extends AbstractAction {
  private final ISelectionIndexModel<T> selectionIndexModel;
  private final IObjectTableModel<T> tableModel;

  public RemoveTableRowAction(
    final String name,
    final Icon icon,
    final String tooltipText,
    final ISelectionIndexModel<T> selectionIndexModel,
    final IObjectTableModel<T> tableModel) {
    super(name, icon);
    putValue(Action.SHORT_DESCRIPTION, tooltipText);
    this.selectionIndexModel = selectionIndexModel;
    this.tableModel = tableModel;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    if (this.selectionIndexModel.isEmpty()) {
      return;
    }
    final List<Integer> indeces = new ArrayList<>();
    for (final Integer index : this.selectionIndexModel) {
      indeces.add(index);
    }
    this.tableModel.remove(IterableUtilities.toPrimativArray(indeces));
    this.selectionIndexModel.clear();
  }
}