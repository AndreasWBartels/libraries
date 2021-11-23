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

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class ImageScaleOperation implements IImageOperation {

  private final float widthFactor;
  private final float heightFactor;

  public ImageScaleOperation(final float widthFactor, final float heightFactor) {
    this.widthFactor = widthFactor;
    this.heightFactor = heightFactor;
  }

  public float getWidthFactor() {
    return this.widthFactor;
  }

  public float getHeightFactor() {
    return this.heightFactor;
  }

  public static IOptional<ImageScaleOperation, RuntimeException>
      aggregate(final IObjectList<IImageOperation> operations) {
    ImageScaleOperation scaleOperation = null;
    for (IImageOperation operation : operations) {
      if (operation instanceof ImageScaleOperation) {
        if (scaleOperation == null) {
          scaleOperation = (ImageScaleOperation) operation;
        } else {
          scaleOperation = scaleOperation.adjust((ImageScaleOperation) operation);
        }
      }
    }
    return Optional.of(scaleOperation);
  }

  ImageScaleOperation adjust(final ImageScaleOperation operation) {
    return new ImageScaleOperation(
        operation.getWidthFactor() * this.widthFactor,
        operation.getHeightFactor() * this.heightFactor);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(this.heightFactor);
    result = prime * result + Float.floatToIntBits(this.widthFactor);
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
    ImageScaleOperation other = (ImageScaleOperation) obj;
    if (Float.floatToIntBits(this.heightFactor) != Float.floatToIntBits(other.heightFactor)) {
      return false;
    }
    if (Float.floatToIntBits(this.widthFactor) != Float.floatToIntBits(other.widthFactor)) {
      return false;
    }
    return true;
  }

}
