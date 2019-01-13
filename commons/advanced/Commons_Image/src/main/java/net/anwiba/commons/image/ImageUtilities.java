/*
 * #%L
 * anwiba commons advanced
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

package net.anwiba.commons.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.anwiba.commons.image.codec.IImageCodecVisitor;
import net.anwiba.commons.image.codec.ImageCodec;

public class ImageUtilities {

  public static BufferedImage create(final int width, final int height) {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  }

  public static BufferedImage getEmptyImage(final Dimension size, final ImageCodec imageCodec) {
    if (imageCodec == null) {
      return getTransparentImage(size);
    }
    final IImageCodecVisitor<BufferedImage, RuntimeException> visitor = new IImageCodecVisitor<BufferedImage, RuntimeException>() {
  
      @Override
      public BufferedImage visitUnknown() {
        return getTransparentImage(size);
      }
  
      @Override
      public BufferedImage visitPng() {
        return getTransparentImage(size);
      }
  
      @Override
      public BufferedImage visitJpeg() {
        return getNonTransparentImage(size);
      }
  
      @Override
      public BufferedImage visitBmp() throws RuntimeException {
        return getNonTransparentImage(size);
      }
  
      @Override
      public BufferedImage visitTiff() throws RuntimeException {
        return getNonTransparentImage(size);
      }
  
      @Override
      public BufferedImage visitGif() throws RuntimeException {
        return getTransparentImage(size);
      }
  
    };
    return imageCodec.accept(visitor);
  }

  public static BufferedImage getTransparentImage(final Dimension size) {
    return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
  }

  public static BufferedImage getNonTransparentImage(final Dimension size) {
    Graphics2D graphic = null;
    try {
      final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
      graphic = (Graphics2D) image.getGraphics();
      graphic.setColor(Color.WHITE);
      graphic.fillRect(0, 0, size.width, size.height);
      return image;
    } finally {
      if (graphic != null) {
        graphic.dispose();
      }
    }
  }

}
