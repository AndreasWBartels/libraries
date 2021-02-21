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

import java.awt.Color;

import javax.swing.Icon;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.icon.GuiIconSize;

public class MessageUI {

  public static Icon getIcon(final Message message) {
    return getIcon(message, null);
  }

  public static Icon getIcon(final IMessage message, final Icon defaultIcon) {
    return MessageTypeUI.getIcon(message.getMessageType(), defaultIcon, GuiIconSize.LARGE);
  }

  public static Color getColor(final IMessage message) {
    return MessageTypeUI.getColor(message.getMessageType());
  }
}
