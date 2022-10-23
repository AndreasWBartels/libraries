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

class CmykColorSpace extends ColorSpace {

  private CmykColorSpace() {
    super(ColorSpace.TYPE_CMYK, 4);
  }

  @Override
  public float[] toRGB(final float[] cmyk) {
    float[] rgb = new float[3];
    rgb[0] = 1.0f + cmyk[0] * cmyk[3] - cmyk[3] - cmyk[0];
    rgb[1] = 1.0f + cmyk[1] * cmyk[3] - cmyk[3] - cmyk[1];
    rgb[2] = 1.0f + cmyk[2] * cmyk[3] - cmyk[3] - cmyk[2];
    for (int i = 0; i < rgb.length; i++) {
      rgb[i] = rgb[i] < 0f ? 0f : rgb[i] > 1f ? 1f : rgb[i];
    }
    return rgb;
  }

  @Override
  public float[] fromRGB(final float[] rgb) {
    float[] cmyk = new float[4];
    float max = Colors.max(rgb[0], rgb[1], rgb[2]);
    if (max > 0.0f) {
      cmyk[0] = 1.0f - rgb[0] / max;
      cmyk[1] = 1.0f - rgb[1] / max;
      cmyk[2] = 1.0f - rgb[2] / max;
    } else {
      cmyk[0] = 0.0f;
      cmyk[1] = 0.0f;
      cmyk[2] = 0.0f;
    }
    cmyk[3] = 1.0f - max;
    return cmyk;
  }

  @Override
  public float[] toCIEXYZ(final float[] colorvalue) {
    return ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB).toCIEXYZ(toRGB(colorvalue));
  }

  @Override
  public float[] fromCIEXYZ(final float[] xyzvalue) {
    return fromRGB(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB).fromCIEXYZ(xyzvalue));
  }

  private final static CmykColorSpace colorSpace = new CmykColorSpace();

  public static ColorSpace getInstance() {
    return colorSpace;
  }

}
