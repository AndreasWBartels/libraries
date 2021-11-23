/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.image.operation;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class ImageCropOperation implements IImageOperation {

  private final float x;
  private final float y;
  private final float width;
  private final float height;

  public ImageCropOperation(final float x, final float y, final float width, final float height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public float getX() {
    return this.x;
  }

  public float getY() {
    return this.y;
  }

  public float getWidth() {
    return this.width;
  }

  public float getHeight() {
    return this.height;
  }

  public static IOptional<ImageCropOperation, RuntimeException>
      aggregate(final IObjectList<IImageOperation> operations) {
    ImageScaleOperation scaleOperation = null;
    ImageCropOperation cropOperation = null;
    for (IImageOperation operation : operations) {
      if (operation instanceof ImageScaleOperation) {
        if (scaleOperation == null) {
          scaleOperation = (ImageScaleOperation) operation;
        } else {
          scaleOperation = scaleOperation.adjust((ImageScaleOperation) operation);
        }
      }
      if (operation instanceof ImageCropOperation) {
        if (cropOperation == null) {
          cropOperation = transform(scaleOperation, (ImageCropOperation) operation);
        } else {
          ImageCropOperation other = transform(scaleOperation, (ImageCropOperation) operation);
          cropOperation = new ImageCropOperation(cropOperation.getX() + other.getX(),
              cropOperation.getY() + other.getY(),
              other.getWidth(),
              other.getHeight());
        }
      }
    }
    return Optional.of(cropOperation);
  }

  private static ImageCropOperation transform(final ImageScaleOperation scaleOperation,
      final ImageCropOperation cropOperation) {
    if (scaleOperation == null) {
      return cropOperation;
    }
    final Rectangle.Float rectangle = new Rectangle.Float(
        cropOperation.getX(),
        cropOperation.getY(),
        cropOperation.getWidth(),
        cropOperation.getHeight());
    final AffineTransform transform = AffineTransform
        .getScaleInstance(
            1 / scaleOperation.getWidthFactor(),
            1 / scaleOperation.getHeightFactor());
    final Shape shape = transform.createTransformedShape(rectangle);
    Rectangle bounds = shape.getBounds();
    return new ImageCropOperation((float) bounds.getX(),
        (float) bounds.getY(),
        (float) bounds.getWidth(),
        (float) bounds.getHeight());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(this.height);
    result = prime * result + Float.floatToIntBits(this.width);
    result = prime * result + Float.floatToIntBits(this.x);
    result = prime * result + Float.floatToIntBits(this.y);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ImageCropOperation other = (ImageCropOperation) obj;
    if (Float.floatToIntBits(this.height) != Float.floatToIntBits(other.height)) {
      return false;
    }
    if (Float.floatToIntBits(this.width) != Float.floatToIntBits(other.width)) {
      return false;
    }
    if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
      return false;
    }
    if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
      return false;
    }
    return true;
  }

  public Rectangle getBounds() {
    return new Rectangle.Float(
        this.x,
        this.y,
        this.width,
        this.height).getBounds();
  }
}
