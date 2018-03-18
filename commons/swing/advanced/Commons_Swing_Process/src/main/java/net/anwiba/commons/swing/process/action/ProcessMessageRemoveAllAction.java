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

import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.process.ProcessMessageContextTableModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public final class ProcessMessageRemoveAllAction extends AbstractAction {
  public static final class RemoveAllRunner implements Runnable {
    private final ProcessMessageContextTableModel contextTableModel;

    public RemoveAllRunner(final ProcessMessageContextTableModel contextTableModel) {
      this.contextTableModel = contextTableModel;
    }

    @Override
    public void run() {
      this.contextTableModel.removeAll();
    }
  }

  private static final long serialVersionUID = 1L;
  private final ProcessMessageContextTableModel tableModel;

  public ProcessMessageRemoveAllAction(final ProcessMessageContextTableModel tableModel) {
    super(DialogMessages.REMOVE_ALL, GuiIcons.EDIT_CLEAR_LIST.getSmallIcon());
    this.tableModel = tableModel;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    final ProcessMessageContextTableModel contextTableModel = this.tableModel;
    GuiUtilities.invokeLater(new RemoveAllRunner(contextTableModel));
  }
}