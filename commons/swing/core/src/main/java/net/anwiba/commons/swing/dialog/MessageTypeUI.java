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

import net.anwiba.commons.message.IMessageTypeVisitor;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.icon.GuiIconSize;
import net.anwiba.commons.swing.icons.GuiIcons;

public class MessageTypeUI {

  public static Icon getIcon(final MessageType messageType, final GuiIconSize size) {
    return getIcon(messageType, null, size);
  }

  public static Icon getIcon(final MessageType messageType, final Icon defaultIcon, final GuiIconSize size) {
    final IMessageTypeVisitor<Icon> visitor = new IMessageTypeVisitor<Icon>() {

      @Override
      public Icon visitInfo() {
        return GuiIcons.INFORMATION_ICON.getIcon(size);
      }

      @Override
      public Icon visitError() {
        return GuiIcons.ERROR_ICON.getIcon(size);
      }

      @Override
      public Icon visitWarning() {
        return GuiIcons.WARNING_ICON.getIcon(size);
      }

      @Override
      public Icon visitDefault() {
        return defaultIcon;
      }

      @Override
      public Icon visitQuery() {
        return GuiIcons.QUERY_ICON.getIcon(size);
      }
    };
    return messageType.accept(visitor);
  }

  public static Color getColor(final MessageType messageType) {
    final IMessageTypeVisitor<Color> visitor = new IMessageTypeVisitor<Color>() {

      @Override
      public Color visitInfo() {
        return Color.BLUE;
      }

      @Override
      public Color visitError() {
        return Color.RED;
      }

      @Override
      public Color visitWarning() {
        return Color.ORANGE;
      }

      @Override
      public Color visitDefault() {
        return Color.BLACK;
      }

      @Override
      public Color visitQuery() {
        return Color.BLACK;
      }
    };
    return messageType.accept(visitor);
  }

}
