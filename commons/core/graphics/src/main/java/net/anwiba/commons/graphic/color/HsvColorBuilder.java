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

public class HsvColorBuilder implements IColorBuilder {

  private final ColorSpace colorSpace = ColorSpaces.CS_HSV;
  private float hue = 1.0f;
  private float saturation = 1.0f;
  private float value = 1.0f;
  private float opacity = 1.0f;
  private int[] maximumValues;

  public HsvColorBuilder() {
    this(java.awt.Color.BLACK);
  }

  public HsvColorBuilder(final java.awt.Color color) {
    this.maximumValues = ColorSpaces.maximumValues(colorSpace);
    final float[] components = color.getRGBComponents(null);
    final float[] hsv = this.colorSpace.fromRGB(components);
    this.hue = hsv[0];
    this.saturation = hsv[1];
    this.value = hsv[2];
    this.opacity = components[components.length - 1];
  }

  @Override
  public HsvColorBuilder component(final int index, final float value) {
    switch (index) {
      case 0 -> hue(value);
      case 1 -> saturation(value);
      case 2 -> value(value);
      case 3 -> opacity(value);
    }
    return this;
  }

  @Override
  public HsvColorBuilder min() {
    this.hue = 0.0f;
    this.saturation = 0.0f;
    this.value = 0.0f;
    return this;
  }

  @Override
  public HsvColorBuilder max() {
    this.hue = 1.0f;
    this.saturation = 1.0f;
    this.value = 1.0f;
    return this;
  }

  public HsvColorBuilder hue(final float hue) {
    this.hue = hue;
    return this;
  }

  public HsvColorBuilder hue(final int hue) {
    this.hue = Colors.toFloat(hue, maximumValues[0]);
    return this;
  }

  public HsvColorBuilder saturation(final float saturation) {
    this.saturation = saturation;
    return this;
  }

  public HsvColorBuilder saturation(final int saturation) {
    this.saturation = Colors.toFloat(saturation, maximumValues[1]);
    return this;
  }

  public HsvColorBuilder value(final float value) {
    this.value = value;
    return this;
  }

  public HsvColorBuilder value(final int value) {
    this.value = Colors.toFloat(value, maximumValues[2]);
    return this;
  }

  @Override
  public HsvColorBuilder opacity(final float opacity) {
    this.opacity = opacity;
    return this;
  }

  @Override
  public HsvColorBuilder opacity(final int opacity) {
    this.opacity = Colors.toFloat(opacity,255);
    return this;
  }

  @Override
  public IColorBuilder component(final int index, final int value) {
    return component(index, Colors.toFloat(value, index >= maximumValues.length ? 255 : maximumValues[index]));
  }

  @Override
  public IColor build() {
    return new Color(this.colorSpace, new float[] { this.hue, this.saturation, this.value }, this.opacity);
  }
}