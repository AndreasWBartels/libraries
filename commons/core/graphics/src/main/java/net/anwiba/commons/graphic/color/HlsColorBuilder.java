/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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

public class HlsColorBuilder implements IColorBuilder {

  private final ColorSpace colorSpace = ColorSpaces.CS_HLS;
  private float hue = 1.0f;
  private float saturation = 1.0f;
  private float lightness = 1.0f;
  private float opacity = 1.0f;
  private final int[] maximumValues;

  public HlsColorBuilder() {
    this(java.awt.Color.BLACK);
  }

  public HlsColorBuilder(final java.awt.Color color) {
    this.maximumValues = ColorSpaces.maximumValues(this.colorSpace);
    final float[] components = color.getRGBComponents(null);
    final float[] hls = this.colorSpace.fromRGB(components);
    this.hue = hls[0];
    this.lightness = hls[1];
    this.saturation = hls[2];
    this.opacity = components[components.length - 1];
  }

  @Override
  public HlsColorBuilder component(final int index, final float value) {
    switch (index) {
      case 0 -> hue(value);
      case 1 -> lightness(value);
      case 2 -> saturation(value);
      case 3 -> opacity(value);
    }
    return this;
  }

  @Override
  public HlsColorBuilder min() {
    this.hue = 0.0f;
    this.lightness = 0.0f;
    this.saturation = 0.0f;
    return this;
  }

  @Override
  public HlsColorBuilder max() {
    this.hue = 1.0f;
    this.lightness = 1.0f;
    this.saturation = 1.0f;
    return this;
  }

  public HlsColorBuilder hue(final float hue) {
    this.hue = hue;
    return this;
  }

  public HlsColorBuilder hue(final int hue) {
    this.hue = Colors.toFloat(hue, this.maximumValues[0]);
    return this;
  }

  public HlsColorBuilder lightness(final float lightness) {
    this.lightness = lightness;
    return this;
  }

  public HlsColorBuilder lightness(final int lightness) {
    this.lightness = Colors.toFloat(lightness, this.maximumValues[1]);
    return this;
  }

  public HlsColorBuilder saturation(final float saturation) {
    this.saturation = saturation;
    return this;
  }

  public HlsColorBuilder saturation(final int saturation) {
    this.saturation = Colors.toFloat(saturation, this.maximumValues[2]);
    return this;
  }

  @Override
  public HlsColorBuilder opacity(final float opacity) {
    this.opacity = opacity;
    return this;
  }

  @Override
  public HlsColorBuilder opacity(final int opacity) {
    this.opacity = Colors.toFloat(opacity, 255);
    return this;
  }

  @Override
  public IColorBuilder component(final int index, final int value) {
    return component(index,
        Colors.toFloat(value, index >= this.maximumValues.length ? 255 : this.maximumValues[index]));
  }

  @Override
  public IColor build() {
    return new Color(this.colorSpace, new float[] { this.hue, this.lightness, this.saturation }, this.opacity);
  }

}