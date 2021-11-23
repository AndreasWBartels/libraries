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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import javax.swing.ImageIcon;

import net.anwiba.commons.lang.optional.Optional;

public interface IGuiIcon {

  ImageIcon getSmallIcon();

  ImageIcon getMediumIcon();

  ImageIcon getLargeIcon();

  ImageIcon getIcon(GuiIconSize size);

  boolean isDecorator();

  default IGuiIcon brighter() {
    return GuiImageIcon.of(
        Optional.of(getSmallIcon()).convert(IGuiIcon::brighter).get(),
        Optional.of(getMediumIcon()).convert(IGuiIcon::brighter).get(), 
        Optional.of(getLargeIcon()).convert(IGuiIcon::brighter).get());
  }

  default IGuiIcon darker() {
    return GuiImageIcon.of(
        Optional.of(getSmallIcon()).convert(IGuiIcon::darker).get(),
        Optional.of(getMediumIcon()).convert(IGuiIcon::darker).get(), 
        Optional.of(getLargeIcon()).convert(IGuiIcon::darker).get());
  }

  public static ImageIcon darker(ImageIcon imageicon) {
    return execute(imageicon, c -> c.darker());
  }

  public static ImageIcon brighter(ImageIcon imageicon) {
    return execute(imageicon, c -> c.brighter());
  }
  
  private static ImageIcon execute(ImageIcon imageicon, Function<Color, Color> converter) {
    BufferedImage image = new BufferedImage(imageicon.getIconWidth(), imageicon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics graphics = image.getGraphics();
    try {
      imageicon.paintIcon(null, graphics, 0, 0);
      execute(image, converter);
      return new ImageIcon(image);
    } finally {
      graphics.dispose();
    } 
  };

  private static void execute(BufferedImage image, Function<Color, Color> converter) {
    for (int i = 0; i < image.getHeight(); i++) {
      for (int k = 0; k < image.getWidth(); k++) {
        image.setRGB(i, k, converter.apply(new Color(image.getRGB(i, k), true)).getRGB());
      }
    }
  };  
}
