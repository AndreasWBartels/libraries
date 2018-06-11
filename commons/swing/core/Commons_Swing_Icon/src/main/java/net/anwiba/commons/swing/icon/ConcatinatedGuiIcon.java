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

import static net.anwiba.commons.swing.icon.GuiIconDecorator.*;

import javax.swing.ImageIcon;

public class ConcatinatedGuiIcon implements IGuiIcon {

  private final IGuiIcon icon;
  private final IGuiIcon[] additionalIcons;

  public ConcatinatedGuiIcon(final IGuiIcon icon, final IGuiIcon... additionalIcons) {
    this.icon = icon;
    this.additionalIcons = additionalIcons;
  }

  @Override
  public ImageIcon getSmallIcon() {
    return concat(GuiIconSize.SMALL);
  }

  private ImageIcon concat(final GuiIconSize size) {
    ImageIcon image = this.icon.getIcon(size);
    for (final IGuiIcon additionalIcon : this.additionalIcons) {
      final ImageIcon additionalImage = additionalIcon.getIcon(size);
      image = add(image, additionalImage, 0, 0, additionalImage.getIconWidth(), additionalImage.getIconHeight());
    }
    return image;
  }

  @Override
  public ImageIcon getMediumIcon() {
    return concat(GuiIconSize.MEDIUM);
  }

  @Override
  public ImageIcon getLargeIcon() {
    return concat(GuiIconSize.LARGE);
  }

  @Override
  public ImageIcon getIcon(final GuiIconSize size) {
    return concat(size);
  }

  @Override
  public boolean isDecorator() {
    return false;
  }

}
