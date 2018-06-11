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
package net.anwiba.commons.swing.utilities;

import java.awt.Window;

import javax.swing.Icon;

import net.anwiba.commons.message.ExceptionMessage;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.IDialogResult;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;
import net.anwiba.commons.swing.dialog.exception.ExceptionDialog;
import net.anwiba.commons.swing.icon.GuiIcons;

public class MessageDialogUtilities {

  public static final IDialogResult show(final Window owner, final IMessage message) {
    return show(owner, message.getText(), message);
  }

  public static final IDialogResult show(final Window owner, final String title, final IMessage message) {
    return show(owner, title, message, DialogType.CLOSE);
  }

  public static IDialogResult show(
      final Window owner,
      final String title,
      final IMessage message,
      final DialogType dialogType) {
    final Icon icon = null;
    return show(owner, title, message, icon, dialogType);
  }

  private static IDialogResult show(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType) {
    final MessageDialog dialog = create(owner, title, message, icon, dialogType);
    dialog.setVisible(true);
    return dialog.getResult();
  }

  public static IDialogResult showUnsupportedOperationDialog(final Window owner) {
    return show(
        owner,
        DialogMessages.ERROR,
        Message.create("Unsupported operation", "Not yet implemented"), //$NON-NLS-1$ //$NON-NLS-2$
        GuiIcons.EMPTY_ICON.getLargeIcon(),
        DialogType.CLOSE);
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
    return new MessageDialog(owner, title, message, icon, dialogType);
  }

  public static MessageDialogLauncher setTitle(final String text) {
    final MessageDialogLauncher launcher = new MessageDialogLauncher();
    return launcher.title(text);
  }

}
