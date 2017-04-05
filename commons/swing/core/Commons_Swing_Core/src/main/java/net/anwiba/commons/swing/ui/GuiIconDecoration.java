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

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.icon.IGuiIconDecoration;

public enum GuiIconDecoration implements IGuiIconDecoration {
  NONE(GuiIcons.EMPTY_ICON), //
  INFO(GuiIcons.INFORMATION_ICON), //
  WARNING(GuiIcons.WARNING_DECORATION), //
  ERROR(GuiIcons.ERROR_DECORATION), //
  FILTER(GuiIcons.FILTER_DECORATION), //
  QUERY(GuiIcons.QUERY_ICON); //

  private final IGuiIcon decoration;

  private GuiIconDecoration(final IGuiIcon decoration) {
    this.decoration = decoration;
  }

  @Override
  public IGuiIcon getGuiIcon() {
    return this.decoration;
  }

  public static GuiIconDecoration getByMessageType(final MessageType messageType) {
    if (messageType == null) {
      return NONE;
    }
    switch (messageType) {
      case DEFAULT:
        return NONE;
      case QUERY:
        return QUERY;
      case INFO:
        return INFO;
      case WARNING:
        return WARNING;
      case ERROR:
        return ERROR;
    }
    throw new UnreachableCodeReachedException();
  }
}
