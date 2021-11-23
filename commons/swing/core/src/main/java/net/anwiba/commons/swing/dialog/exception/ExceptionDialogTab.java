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

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.dialog.tabbed.AbstractDialogTab;
import net.anwiba.commons.swing.icons.GuiIcons;

public class ExceptionDialogTab extends AbstractDialogTab {

  public ExceptionDialogTab(final Throwable throwable) {
    this(Message.builder()
        .setText(throwable.getClass().getSimpleName())
        .setDescription(throwable.getMessage())
        .setThrowable(throwable)
        .setError()
        .build());
  }

  public ExceptionDialogTab(final IMessage message) {
    super(DialogMessages.ERROR, message, GuiIcons.EMPTY_ICON.getLargeIcon());
    final JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setLineWrap(false);
    textArea.setRows(15);
    textArea.setColumns(25);
    textArea.append(Optional.of(message.getThrowable())
        .convert(Message::toDetailInfo)
        .getOr(() -> ""));
    setComponent(new JScrollPane(textArea));
  }

  @Override
  public void checkFieldValues() {
    // nothing to do
  }

  @Override
  public boolean apply() {
    return true;
  }

  @Override
  public void updateView() {
    // nothing to do
  }
}