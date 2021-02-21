/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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

import net.anwiba.commons.message.IMessageTypeVisitor;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.icons.GuiIcons;

public class MessageGuiIconFactory {

  public static IGuiIcon create(final MessageType messageType) {
    return create(messageType, GuiIcons.INFORMATION_ICON);
  }

  public static IGuiIcon create(final MessageType messageType, final IGuiIcon defaultIcon) {
    final IMessageTypeVisitor<IGuiIcon> visitor = new IMessageTypeVisitor<IGuiIcon>() {

      @Override
      public IGuiIcon visitInfo() {
        return GuiIcons.INFORMATION_ICON;
      }

      @Override
      public IGuiIcon visitError() {
        return GuiIcons.ERROR_ICON;
      }

      @Override
      public IGuiIcon visitWarning() {
        return GuiIcons.WARNING_ICON;
      }

      @Override
      public IGuiIcon visitDefault() {
        return defaultIcon;
      }

      @Override
      public IGuiIcon visitQuery() {
        return GuiIcons.QUERY_ICON;
      }
    };
    return messageType.accept(visitor);
  }

}
