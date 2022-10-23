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
package net.anwiba.commons.swing.dialog.exception;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.icons.GuiIcons;

public class ExceptionDialog extends MessageDialog {

  private static final long serialVersionUID = 1L;
  private final IMessage exceptionMessage;
  private JScrollPane detailsPanel;

  public ExceptionDialog(final Window owner, final Throwable throwable) {
    this(owner,
        Message.error(throwable.getClass().getSimpleName())
            .description(throwable.getMessage())
            .throwable(throwable)
            .build());
  }

  public ExceptionDialog(final Window owner, final IMessage message) {
    this(owner, DialogMessages.ERROR, message);
  }

  public ExceptionDialog(final Window owner, final String title, final IMessage message) {
    super(owner, title, message, GuiIcons.EMPTY_ICON.getLargeIcon(), DialogType.CLOSE_DETIALS);
    this.exceptionMessage = message;
  }

  @Override
  protected Component getDetailsComponent() {
    if (this.detailsPanel == null) {
      final JTextArea textArea = new JTextArea();
      this.detailsPanel = new JScrollPane(textArea);
      textArea.setEditable(false);
      textArea.setLineWrap(false);
      textArea.setRows(15);
      textArea.setColumns(40);
      textArea.append(Message.toDetailInfo(ExceptionDialog.this.exceptionMessage.getThrowable()));
    }
    return this.detailsPanel;
  }

}
