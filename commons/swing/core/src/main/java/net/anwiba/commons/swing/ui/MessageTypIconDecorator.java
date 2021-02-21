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
package net.anwiba.commons.swing.ui;

import javax.swing.ImageIcon;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.icon.GuiIconDecorator;
import net.anwiba.commons.swing.icon.GuiIconSize;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.icon.IGuiIconDecoration;

public class MessageTypIconDecorator {

  private static IGuiIconDecoration getGuiIconDecoration(final MessageType messageType) {
    switch (messageType) {
      case DEFAULT: {
        return GuiIconDecoration.NONE;
      }
      case INFO: {
        return GuiIconDecoration.INFO;
      }
      case QUERY: {
        return GuiIconDecoration.QUERY;
      }
      case WARNING: {
        return GuiIconDecoration.WARNING;
      }
      case ERROR: {
        return GuiIconDecoration.ERROR;
      }
    }
    throw new UnreachableCodeReachedException();
  }

  public static ImageIcon decorate(final GuiIconSize size, final IGuiIcon icon, final MessageType messageType) {
    return GuiIconDecorator.decorate(size, icon, getGuiIconDecoration(messageType));
  }

  public static ImageIcon decorate(final GuiIconSize size, final ImageIcon icon, final MessageType messageType) {
    return GuiIconDecorator.decorate(size, icon, getGuiIconDecoration(messageType));
  }

}