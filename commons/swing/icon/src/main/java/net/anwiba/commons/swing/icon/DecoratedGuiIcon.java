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

package net.anwiba.commons.swing.icon;

import static net.anwiba.commons.swing.icon.GuiIconDecorator.decorate;

import javax.swing.ImageIcon;

public class DecoratedGuiIcon implements IGuiIcon {

  private final IGuiIcon decoration;
  private final IGuiIcon icon;
  private final DecorationPosition position;

  public DecoratedGuiIcon(final IGuiIcon icon, final IGuiIcon decoration) {
    this.icon = icon;
    this.decoration = decoration;
    this.position = DecorationPosition.LowerLeft;
  }

  public DecoratedGuiIcon(final GuiIcon icon, final GuiIcon decoration, final DecorationPosition position) {
    this.icon = icon;
    this.decoration = decoration;
    this.position = position;
  }

  @Override
  public ImageIcon getSmallIcon() {
    return decorate(GuiIconSize.SMALL, this.position, this.icon, this.decoration);
  }

  @Override
  public ImageIcon getMediumIcon() {
    return decorate(GuiIconSize.MEDIUM, this.position, this.icon, this.decoration);
  }

  @Override
  public ImageIcon getLargeIcon() {
    return decorate(GuiIconSize.LARGE, this.position, this.icon, this.decoration);
  }

  @Override
  public ImageIcon getIcon(final GuiIconSize size) {
    return decorate(size, this.position, this.icon, this.decoration);
  }

  @Override
  public boolean isDecorator() {
    return false;
  }

}
