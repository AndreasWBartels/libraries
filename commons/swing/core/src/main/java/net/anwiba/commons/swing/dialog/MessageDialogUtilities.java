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

import net.anwiba.commons.message.ExceptionMessage;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class MessageDialogUtilities {

  public static final IDialogResult show(final Component owner, final IMessage message) {
    return show(owner, message.getText(), message);
  }

  public static final IDialogResult show(final Component owner, final String title, final IMessage message) {
    return show(owner, title, message, DialogType.CLOSE);
  }

  public static IDialogResult show(
      final Component owner,
      final String title,
      final IMessage message,
      final DialogType dialogType) {
    return show(owner, title, message, GuiIcons.EMPTY_ICON, dialogType);
  }

  private static IDialogResult show(
      final Component owner,
      final String title,
      final IMessage message,
      final IGuiIcon icon,
      final DialogType dialogType) {
    return launch(GuiUtilities.getParentWindow(owner), title, message, icon, dialogType);
  }

  public static IDialogResult showUnsupportedOperationDialog(final Window owner) {
    return show(
        owner,
        DialogMessages.ERROR,
        Message.create("Unsupported operation", "Not yet implemented"), //$NON-NLS-1$ //$NON-NLS-2$
        GuiIcons.EMPTY_ICON,
        DialogType.CLOSE);
  }

  private static IDialogResult launch(
      final Window owner,
      final String title,
      final IMessage message,
      final IGuiIcon icon,
      final DialogType dialogType) {
    return MessageDialog.launcher()
        .title(title)
        .icon(icon)
        .message(message)
        .dialogType(dialogType)
        .launch(owner);
  }

  public static void show(final Window owner, final ExceptionMessage message) {
    MessageDialog.launcher().message(message).launch(owner);
  }

  public static void show(final Window owner, final Throwable e) {
    MessageDialog.launcher().throwable(e).error().launch(owner);
  }
}
