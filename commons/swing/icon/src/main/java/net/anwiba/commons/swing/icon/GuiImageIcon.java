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

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class GuiImageIcon implements IGuiIcon {

  public static GuiImageIcon of(final ImageIcon smallImageIcon,
      final ImageIcon mediumImageIcon,
      final ImageIcon largeImageIcon) {
    return new GuiImageIcon(smallImageIcon, mediumImageIcon, largeImageIcon);
  }

  public GuiImageIcon(final ImageIcon smallImageIcon) {
    this(smallImageIcon, null, null);
  }

  public static GuiImageIcon of(final Icon smallImageIcon,
      final Icon mediumImageIcon,
      final Icon largeImageIcon) {
    return new GuiImageIcon(toImageIcon(smallImageIcon),
        toImageIcon(mediumImageIcon),
        toImageIcon(largeImageIcon));
  }

  public GuiImageIcon(final Icon smallImageIcon) {
    this(toImageIcon(smallImageIcon), null, null);
  }

  private static ImageIcon toImageIcon(final Icon icon) {
    if (icon instanceof ImageIcon) {
      return (ImageIcon) icon;
    }
    return new ImageIcon(toImage(icon));
  }

  private static BufferedImage toImage(final Icon icon) {
    BufferedImage image = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .getDefaultConfiguration()
        .createCompatibleImage(icon.getIconWidth(), icon.getIconHeight());
    Graphics2D g = image.createGraphics();
    try {
      icon.paintIcon(null, g, 0, 0);
      return image;
    } finally {
      g.dispose();
    }
  }

  private final ImageIcon smallImageIcon;
  private final ImageIcon mediumImageIcon;
  private final ImageIcon largeImageIcon;

  public GuiImageIcon(final ImageIcon smallImageIcon,
      final ImageIcon mediumImageIcon,
      final ImageIcon largeImageIcon) {
    this.smallImageIcon = smallImageIcon;
    this.mediumImageIcon = mediumImageIcon;
    this.largeImageIcon = largeImageIcon;
  }

  @Override
  public ImageIcon getSmallIcon() {
    return this.smallImageIcon;
  }

  @Override
  public ImageIcon getMediumIcon() {
    return this.mediumImageIcon;
  }

  @Override
  public ImageIcon getLargeIcon() {
    return this.largeImageIcon;
  }

  @Override
  public ImageIcon getIcon(final GuiIconSize size) {
    final IGuiIconSizeVisitor<ImageIcon> visitor = new IGuiIconSizeVisitor<ImageIcon>() {

      @Override
      public ImageIcon vistSmall() {
        return getSmallIcon();
      }

      @Override
      public ImageIcon vistMedium() {
        return getMediumIcon();
      }

      @Override
      public ImageIcon vistLarge() {
        return getLargeIcon();
      }
    };
    return size.accept(visitor);
  }

  @Override
  public boolean isDecorator() {
    return false;
  }

}
