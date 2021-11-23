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

import net.anwiba.commons.message.IMessage;

public class MessageDialog extends AbstractDialog {

  private static final long serialVersionUID = 1L;

  public static MessageDialogLauncher launcher() {
    return new MessageDialogLauncher();
  }

  public MessageDialog(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType) {
    super(owner, title, message, icon, dialogType, true);
  }

  @Override
  protected Component getDetailsComponent() {
    return null;
  }

  @Override
  protected boolean apply() {
    return true;
  }

  @Override
  protected boolean tryOut() {
    return true;
  }

  @Override
  protected boolean cancel() {
    return true;
  }
}
