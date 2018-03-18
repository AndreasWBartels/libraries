/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels 
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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public final class StringDecorationIcon implements IGuiIcon {
  private final Color backGroundColor;
  private final Color fontColor;
  private final String extention;

  public StringDecorationIcon(final Color backGroundColor, final Color fontColor, final String extention) {
    this.backGroundColor = backGroundColor;
    this.fontColor = fontColor;
    this.extention = extention;
  }

  @Override
  public boolean isDecorator() {
    return true;
  }

  @Override
  public ImageIcon getSmallIcon() {
    return create(16, this.extention);
  }

  @Override
  public ImageIcon getMediumIcon() {
    return create(22, this.extention);
  }

  @Override
  public ImageIcon getLargeIcon() {
    return create(32, this.extention);
  }

  private ImageIcon create(final int size, final String string) {
    final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphic = (Graphics2D) image.getGraphics();
    final Color color = graphic.getColor();
    final Font font = graphic.getFont();
    try {
      graphic.setColor(this.backGroundColor);
      graphic.fillRect(0, size / 2, size, size / 2);
      graphic.setColor(this.fontColor);
      graphic.setFont(font.deriveFont((float) (size / 2.5)));
      graphic.drawString(string, 1, size - 2);
      return new ImageIcon(image);
    } finally {
      graphic.setColor(color);
      graphic.setFont(font);
      graphic.dispose();
    }
  }

  @Override
  public ImageIcon getIcon(final GuiIconSize size) {
    return size.accept(new IGuiIconSizeVisitor<ImageIcon>() {

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

    });
  }
}
