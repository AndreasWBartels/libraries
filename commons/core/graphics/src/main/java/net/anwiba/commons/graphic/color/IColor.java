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

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.Serializable;
import java.util.Objects;

public interface IColor extends Paint, Serializable {

  public ColorSpace getColorSpace();

  public float[] getColorCompoments();

  default public float[] getCompoments() {
    final int numberOfComponents = getColorSpace().getNumComponents();
    final float[] result = new float[numberOfComponents + 1];
    for (int i = 0; i < numberOfComponents + 1; i++) {
      result[i] = getComponent(i);
    }
    return result;
  }

  public float getOpacity();

  public float getComponent(int index);

  default IColor adaptTo(final int index, final int value) {
    return Colors.builder(getColorSpace())
        .components(getCompoments())
        .component(index, value)
        .build();
  }

  default IColor adaptTo(final int index, final float value) {
    return Colors.builder(getColorSpace())
        .components(getCompoments())
        .component(index, value)
        .build();
  }

  default IColor adaptTo(final ColorSpace colorSpace) {
    return Objects.equals(getColorSpace(), colorSpace)
        ? this
        : Colors.builder(colorSpace.getType(), this.toColor()).build();
  }

  default int value(final int index) {
    int[] maximumValues = ColorSpaces.maximumValues(getColorSpace());
    return Colors.toInteger(getComponent(index), index >= maximumValues.length ? 255 : maximumValues[index]);
  }

  default int rgb() {
    return toColor().getRGB();
  }

  default int value(final String name) {
    Objects.requireNonNull(name);
    ColorSpace colorSpace = getColorSpace();
    final int numberOfComponents = colorSpace.getNumComponents();
    if (name.equalsIgnoreCase("alpha")
        || name.equalsIgnoreCase("opacity")) {
      return value(numberOfComponents);
    }
    for (int i = 0; i < numberOfComponents; i++) {
      if (name.equalsIgnoreCase(colorSpace.getName(i))) {
        return value(i);
      }
    }
    throw new IllegalArgumentException("Unexpected component name '" + name + "'");
  }

  default java.awt.Color toColor() {
    float[] rgb = getColorSpace().toRGB(getColorCompoments());
    return new java.awt.Color(ColorSpaces.CS_RGB, rgb, getOpacity());
  }

  @Override
  default PaintContext createContext(final ColorModel cm,
      final Rectangle deviceBounds,
      final Rectangle2D userBounds,
      final AffineTransform xform,
      final RenderingHints hints) {
    return toColor().createContext(cm, deviceBounds, userBounds, xform, hints);
  }

  @Override
  default int getTransparency() {
    return toColor().getTransparency();
  }

}
