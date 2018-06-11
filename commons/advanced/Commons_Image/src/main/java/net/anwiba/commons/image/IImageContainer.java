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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface IImageContainer {

  void dispose();

  int getWidth();

  int getHeight();

  BufferedImage asBufferImage();

  IImageContainer crop(float x, float y, float width, float height);

  IImageContainer fit(int width, int height);

  IImageContainer scale(float widthFactor, float heightFactor);

  IImageContainer scale(float factor);

  IImageContainer invert();

  //  IImageContainer rotate(float factor);
  //
  //  IImageContainer rotate(float x, float y);

  BufferedImage asBufferImage(int x, int y, int w, int h);

  BufferedImage asBufferImage(Rectangle rectangle);

  int getNumberOfBands();

  IImageContainer mapBands(int[] bandMapping);

  IImageContainer toGrayScale();

  int getColorSpaceType();

}
