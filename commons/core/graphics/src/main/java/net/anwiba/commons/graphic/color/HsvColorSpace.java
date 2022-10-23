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

class HsvColorSpace extends ColorSpace {

  private HsvColorSpace() {
    super(ColorSpace.TYPE_HSV, 3);
  }

  @Override
  public float[] toRGB(final float[] hsv) {
    float[] rgb = new float[3];
    float hue = hsv[0];
    float saturation = hsv[1];
    float value = hsv[2];

    rgb[0] = value;
    rgb[1] = value;
    rgb[2] = value;

    if (saturation > 0.0f) {
      hue = (hue < 1.0f) ? hue * 6.0f : 0.0f;
      int integer = (int) hue;
      float f = hue - integer;
      switch (integer) {
        case 0:
          rgb[1] *= 1.0f - saturation * (1.0f - f);
          rgb[2] *= 1.0f - saturation;
          break;
        case 1:
          rgb[0] *= 1.0f - saturation * f;
          rgb[2] *= 1.0f - saturation;
          break;
        case 2:
          rgb[0] *= 1.0f - saturation;
          rgb[2] *= 1.0f - saturation * (1.0f - f);
          break;
        case 3:
          rgb[0] *= 1.0f - saturation;
          rgb[1] *= 1.0f - saturation * f;
          break;
        case 4:
          rgb[0] *= 1.0f - saturation * (1.0f - f);
          rgb[1] *= 1.0f - saturation;
          break;
        case 5:
          rgb[1] *= 1.0f - saturation;
          rgb[2] *= 1.0f - saturation * f;
          break;
      }
    }
    for (int i = 0; i < rgb.length; i++) {
      rgb[i] = rgb[i] < 0f ? 0f : rgb[i] > 1f ? 1f : rgb[i];
    }
    return rgb;
  }

  @Override
  public float[] fromRGB(final float[] rgb) {
    float[] hsv = new float[3];
    float min = Colors.min(rgb[0], rgb[1], rgb[2]);
    float max = Colors.max(rgb[0], rgb[1], rgb[2]);
    float saturation = max - min;
    if (saturation > 0.0f) {
      saturation = saturation / max;
    }
    hsv[0] = Colors.hue(rgb[0], rgb[1], rgb[2], max, min);
    hsv[1] = saturation;
    hsv[2] = max;
    return hsv;
  }

  @Override
  public float[] toCIEXYZ(final float[] colorvalue) {
    return ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB).toCIEXYZ(toRGB(colorvalue));
  }

  @Override
  public float[] fromCIEXYZ(final float[] xyzvalue) {
    return fromRGB(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB).fromCIEXYZ(xyzvalue));
  }

  private final static HsvColorSpace colorSpace = new HsvColorSpace();

  public static ColorSpace getInstance() {
    return colorSpace;
  }

}
