/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.graphic.color;

import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.Objects;

class Color implements IColor {

  private final ColorSpace colorSpace;
  private final float[] colorCompoments;
  private final float opacity;

  Color(final ColorSpace colorSpace, final float[] compoments, final float opacity) {
    IllegalArgumentException exception = null;
    if (compoments.length != colorSpace.getNumComponents()) {
      exception =
          exception(exception,
              new IllegalArgumentException("unexpected number of components, current "
                  + compoments.length
                  + " expected "
                  + colorSpace.getNumComponents()));
    }
    this.colorSpace = colorSpace;
    this.colorCompoments = compoments;
    this.opacity = opacity;
  }

  private IllegalArgumentException exception(final IllegalArgumentException exception,
      final IllegalArgumentException other) {
    if (exception != null) {
      exception.addSuppressed(other);
    }
    return other;
  }

  @Override
  public ColorSpace getColorSpace() {
    return this.colorSpace;
  }

  @Override
  public float[] getColorCompoments() {
    return this.colorCompoments;
  }

  @Override
  public float getComponent(final int index) {
    if (index < this.colorCompoments.length) {
      return this.colorCompoments[index];
    }
    if (index == this.colorCompoments.length) {
      return this.opacity;
    }
    throw new IllegalArgumentException("Unexpected index " + index);
  }

  @Override
  public float getOpacity() {
    return this.opacity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(this.colorCompoments);
    result = prime * result + Objects.hash(this.colorSpace, this.opacity);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    return obj instanceof IColor other
        && Arrays.equals(this.colorCompoments, other.getColorCompoments())
        && Objects.equals(this.colorSpace, other.getColorSpace())
        && Float.floatToIntBits(this.opacity) == Float.floatToIntBits(other.getOpacity());
  }

}
