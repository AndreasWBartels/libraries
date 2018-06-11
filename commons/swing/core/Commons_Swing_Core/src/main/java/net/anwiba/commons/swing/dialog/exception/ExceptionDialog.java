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
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.anwiba.commons.message.ExceptionMessage;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.icon.GuiIcons;

public class ExceptionDialog extends MessageDialog {

  private static final long serialVersionUID = 1L;
  final ExceptionMessage exceptionMessage;
  JScrollPane detailsPanel;

  public ExceptionDialog(final Window owner, final Throwable throwable) {
    this(owner, new ExceptionMessage(throwable.getClass().getSimpleName(), throwable.getLocalizedMessage(), throwable));
  }

  public ExceptionDialog(final Window owner, final ExceptionMessage message) {
    super(owner, DialogMessages.ERROR, message, GuiIcons.EMPTY_ICON.getLargeIcon(), DialogType.CLOSE_DETIALS);
    this.exceptionMessage = message;
    //    setPreferredSize(new Dimension(300, 120));
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
      final StringWriter stringWriter = new StringWriter();
      ExceptionDialog.this.exceptionMessage.getThrowable().printStackTrace(new PrintWriter(stringWriter));
      textArea.append(stringWriter.getBuffer().toString());
    }
    return this.detailsPanel;
  }

  public static void show(final Window owner, final ExceptionMessage message) {
    final ExceptionDialog exceptionDialog = new ExceptionDialog(owner, message);
    exceptionDialog.setVisible(true);
  }

  public static void show(final Window owner, final Throwable e) {
    final ExceptionDialog exceptionDialog = new ExceptionDialog(owner, e);
    exceptionDialog.setVisible(true);
  }
}
