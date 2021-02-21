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
package net.anwiba.commons.swing.process.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.process.IProcessMessageContext;
import net.anwiba.commons.swing.process.ProcessMessageContextTableModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public final class ProcessMessageRemoveAction extends AbstractAction {
  public static final class RemoveRunner implements Runnable {
    private final ProcessMessageContextTableModel contextTableModel;
    private final IProcessMessageContext[] contexts;

    public RemoveRunner(final ProcessMessageContextTableModel contextTableModel, final IProcessMessageContext[] contexts) {
      this.contextTableModel = contextTableModel;
      this.contexts = contexts;
    }

    @Override
    public void run() {
      this.contextTableModel.remove(this.contexts);
    }
  }

  private static final long serialVersionUID = 1L;
  private final ProcessMessageContextTableModel tableModel;
  private final ISelectionModel<IProcessMessageContext> selectionModel;

  public ProcessMessageRemoveAction(
      final ProcessMessageContextTableModel tableModel,
      final ISelectionModel<IProcessMessageContext> selectionModel) {
    super(DialogMessages.REMOVE, GuiIcons.DELETE_ICON.getSmallIcon());
    this.tableModel = tableModel;
    this.selectionModel = selectionModel;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    final IProcessMessageContext[] contexts = IterableUtilities.toArray(
        this.selectionModel.getSelectedObjects(),
        IProcessMessageContext.class);
    final ProcessMessageContextTableModel contextTableModel = this.tableModel;
    GuiUtilities.invokeLater(new RemoveRunner(contextTableModel, contexts));
  }
}