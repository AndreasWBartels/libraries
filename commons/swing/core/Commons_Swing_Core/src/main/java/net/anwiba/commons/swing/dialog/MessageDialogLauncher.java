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

package net.anwiba.commons.swing.dialog;

import java.awt.Component;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import net.anwiba.commons.message.ExceptionMessage;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.MessageBuilder;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.dialog.exception.ExceptionDialog;
import net.anwiba.commons.swing.icon.GuiIcon;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class MessageDialogLauncher {

  private final MessageBuilder builder = new MessageBuilder();
  private String title = "Title"; //$NON-NLS-1$
  GuiIcon icon = GuiIcons.EMPTY_ICON;
  DialogType dialogType = DialogType.CLOSE;

  public MessageDialogLauncher icon(final GuiIcon icon) {
    this.icon = icon;
    return this;
  }

  public MessageDialogLauncher closeButtonDialog() {
    this.dialogType = DialogType.CLOSE;
    return this;
  }

  public MessageDialogLauncher cancleOkButtonDialog() {
    this.dialogType = DialogType.CANCEL_OK;
    return this;
  }

  public MessageDialogLauncher dialogType(final DialogType dialogType) {
    this.dialogType = dialogType;
    return this;
  }

  public MessageDialogLauncher title(final String title) {
    this.title = title;
    return this;
  }

  public MessageDialogLauncher text(final String text) {
    this.builder.setText(text);
    return this;
  }

  public MessageDialogLauncher description(final String description) {
    this.builder.setDescription(description);
    return this;
  }

  public MessageDialogLauncher throwable(final Throwable throwable) {
    this.builder.setThrowable(throwable);
    return this;
  }

  public MessageDialogLauncher type(final MessageType messageType) {
    this.builder.setType(messageType);
    return this;
  }

  public MessageDialogLauncher error() {
    this.builder.setError();
    return this;
  }

  public MessageDialogLauncher info() {
    this.builder.setInfo();
    return this;
  }

  public MessageDialogLauncher warning() {
    this.builder.setWarning();
    return this;
  }

  public IDialogResult launch(final Component component) {
    return launch(component == null ? (Window) null : SwingUtilities.windowForComponent(component));
  }

  public IDialogResult launch(final Window owner) {
    final IObjectModel<IDialogResult> model = new ObjectModel<>();
    final IMessage message = this.builder.build();
    GuiUtilities.invokeAndWait(() -> {
      final MessageDialog dialog = create(owner, this.title, message, this.icon.getLargeIcon(), this.dialogType);
      dialog.setVisible(true);
      model.set(dialog.getResult());
    });
    return model.get();
  }

  private static MessageDialog create(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType) {
    if (message instanceof ExceptionMessage) {
      return new ExceptionDialog(owner, (ExceptionMessage) message);
    }
    final MessageDialog dialog = new MessageDialog(owner, title, message, icon, dialogType);
    dialog.locate();
    return dialog;
  }

}
