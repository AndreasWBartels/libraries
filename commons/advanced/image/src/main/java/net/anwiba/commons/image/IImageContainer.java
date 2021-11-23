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
import java.awt.image.Raster;

import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.cancel.ICanceler;

public interface IImageContainer {

  default BufferedImage asBufferImage() {
    try {
      return asBufferImage(ICanceler.DummyCanceler);
    } catch (CanceledException exception) {
      throw new UnreachableCodeReachedException(exception);
    }
  }

  default BufferedImage asBufferImage(final ICanceler canceler) throws CanceledException {
    return asBufferImage(IMessageCollector.DummyCollector, canceler);
  }

  BufferedImage asBufferImage(IMessageCollector messageCollector, ICanceler canceler) throws CanceledException;

  IImageContainer crop(float x, float y, float width, float height);

  IImageContainer crop(Rectangle rectangle);

  IImageContainer fitTo(int width, int height);

  default IImageContainer scale(final double widthFactor, final double heightFactor) {
    return scale(Double.valueOf(widthFactor).floatValue(), Double.valueOf(heightFactor).floatValue());
  };

  IImageContainer scaleTo(int width, int height);

  IImageContainer scale(float widthFactor, float heightFactor);

  IImageContainer scale(float factor);

  IImageContainer invert();

  IImageContainer opacity(float factor);

  default IImageContainer operation(final IImageOperation operation) {
    return this;
  }

  IImageContainer mapBands(int[] bandMapping);

  IImageContainer toGrayScale();

  void dispose();

  int getWidth();

  int getHeight();

  int getNumberOfComponents();

  int getNumberOfBands();

  int getColorSpaceType();

  default boolean isScaleRecommended() {
    return false;
  }

  default IImageMetadata getMetadata() {
    return null;
  }

  default Number[] getValues(int x, int y) {
    return getValues(x, y, 1, 1)[0];
  }

  default Number[][] getValues(int x, int y,int width, int height) {
    Raster raster = asBufferImage().getData(new Rectangle(x,y,width,height));
    raster.getDataElements(0,0,width, height, null);
    return new Number[][] {new Number[] {}};
  }
  

}