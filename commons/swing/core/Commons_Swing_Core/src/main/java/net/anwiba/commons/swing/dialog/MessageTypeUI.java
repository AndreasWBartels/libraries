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
import net.anwiba.commons.swing.icon.GuiIcons;

public class MessageTypeUI {

  public static Icon getIcon(final MessageType messageType, final GuiIconSize size) {
    return getIcon(messageType, null, size);
  }

  public static Icon getIcon(final MessageType messageType, final Icon defaultIcon, final GuiIconSize size) {
    final IMessageTypeVisitor<Icon> visitor = new IMessageTypeVisitor<Icon>() {

      Icon result;

      @Override
      public Icon getResult() {
        return this.result;
      }

      @Override
      public void visitInfo() {
        this.result = GuiIcons.INFORMATION_ICON.getIcon(size);
      }

      @Override
      public void visitError() {
        this.result = GuiIcons.ERROR_ICON.getIcon(size);
      }

      @Override
      public void visitWarning() {
        this.result = GuiIcons.WARNING_ICON.getIcon(size);
      }

      @Override
      public void visitDefault() {
        this.result = defaultIcon;
      }

      @Override
      public void visitQuery() {
        this.result = GuiIcons.QUERY_ICON.getIcon(size);
      }
    };
    messageType.accept(visitor);
    return visitor.getResult();
  }

  public static Color getColor(final MessageType messageType) {
    final IMessageTypeVisitor<Color> visitor = new IMessageTypeVisitor<Color>() {

      Color result;

      @Override
      public Color getResult() {
        return this.result;
      }

      @Override
      public void visitInfo() {
        this.result = Color.BLUE;
      }

      @Override
      public void visitError() {
        this.result = Color.RED;
      }

      @Override
      public void visitWarning() {
        this.result = Color.ORANGE;
      }

      @Override
      public void visitDefault() {
        this.result = Color.BLACK;
      }

      @Override
      public void visitQuery() {
        this.result = Color.BLACK;
      }
    };
    messageType.accept(visitor);
    return visitor.getResult();
  }

}
