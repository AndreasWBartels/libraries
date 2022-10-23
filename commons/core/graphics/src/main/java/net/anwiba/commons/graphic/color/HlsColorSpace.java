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

class HlsColorSpace extends ColorSpace {

  private HlsColorSpace() {
    super(ColorSpace.TYPE_HLS, 3);
  }

  @Override
  public float[] toRGB(final float[] hls) {
    float[] rgb = new float[3];
    float hue = hls[0];
    float lightness = hls[1];
    float saturation = hls[2];
    if (saturation > 0.0f) {
      hue = (hue < 1.0f) ? hue * 6.0f : 0.0f;
      float q = lightness + saturation * ((lightness > 0.5f) ? 1.0f - lightness : lightness);
      float p = 2.0f * lightness - q;
      rgb[0] = Colors.normalize(q, p, (hue < 4.0f) ? (hue + 2.0f) : (hue - 4.0f));
      rgb[1] = Colors.normalize(q, p, hue);
      rgb[2] = Colors.normalize(q, p, (hue < 2.0f) ? (hue + 4.0f) : (hue - 2.0f));
    } else {
      rgb[0] = lightness;
      rgb[1] = lightness;
      rgb[2] = lightness;
    }
    for (int i = 0; i < rgb.length; i++) {
      rgb[i] = rgb[i] < 0f ? 0f : rgb[i] > 1f ? 1f : rgb[i];
    }
    return rgb;
  }

  @Override
  public float[] fromRGB(final float[] rgb) {
    float[] hls = new float[3];
    float min = Colors.min(rgb[0], rgb[1], rgb[2]);
    float max = Colors.max(rgb[0], rgb[1], rgb[2]);
    float summa = max + min;
    float saturation = max - min;
    if (saturation > 0.0f) {
      saturation /= (summa > 1.0f)
          ? 2.0f - summa
          : summa;
    }
    hls[0] = Colors.hue(rgb[0], rgb[1], rgb[2], max, min);
    hls[1] = summa / 2.0f;
    hls[2] = saturation;
    return hls;
  }

  @Override
  public float[] toCIEXYZ(final float[] colorvalue) {
    return ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB).toCIEXYZ(toRGB(colorvalue));
  }

  @Override
  public float[] fromCIEXYZ(final float[] xyzvalue) {
    return fromRGB(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB).fromCIEXYZ(xyzvalue));
  }

  private final static HlsColorSpace colorSpace = new HlsColorSpace();

  public static ColorSpace getInstance() {
    return colorSpace;
  }

}
