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

public class RgbColorBuilder implements IColorBuilder {

  private final ColorSpace colorSpace = ColorSpaces.CS_RGB;
  private float red = 1.0f;
  private float green = 1.0f;
  private float blue = 1.0f;
  private float opacity = 1.0f;
  private int[] maximumValues;

  public RgbColorBuilder() {
    this(java.awt.Color.BLACK);
  }

  public RgbColorBuilder(final java.awt.Color color) {
    this.maximumValues = ColorSpaces.maximumValues(colorSpace);
    final float[] components = color.getRGBComponents(null);
    final float[] rgb = this.colorSpace.fromRGB(components);
    this.red = rgb[0];
    this.green = rgb[1];
    this.blue = rgb[2];
    this.opacity = components[components.length - 1];
  }

  @Override
  public RgbColorBuilder component(final int index, final float value) {
    switch (index) {
      case 0 -> red(value);
      case 1 -> green(value);
      case 2 -> blue(value);
      case 3 -> opacity(value);
    }
    return this;
  }

  @Override
  public RgbColorBuilder min() {
    this.red = 0.0f;
    this.green = 0.0f;
    this.blue = 0.0f;
    return this;
  }

  @Override
  public RgbColorBuilder max() {
    this.red = 1.0f;
    this.green = 1.0f;
    this.blue = 1.0f;
    return this;
  }

  public RgbColorBuilder red(final float red) {
    this.red = red;
    return this;
  }

  public RgbColorBuilder red(final int red) {
    this.red = Colors.toFloat(red, maximumValues[0]);
    return this;
  }

  public RgbColorBuilder green(final float green) {
    this.green = green;
    return this;
  }

  public RgbColorBuilder green(final int green) {
    this.green = Colors.toFloat(green, maximumValues[1]);
    return this;
  }

  public RgbColorBuilder blue(final float blue) {
    this.blue = blue;
    return this;
  }

  public RgbColorBuilder blue(final int blue) {
    this.blue = Colors.toFloat(blue, maximumValues[2]);
    return this;
  }

  @Override
  public RgbColorBuilder opacity(final float opacity) {
    this.opacity = opacity;
    return this;
  }

  @Override
  public RgbColorBuilder opacity(final int opacity) {
    this.opacity = Colors.toFloat(opacity, 255);
    return this;
  }

  @Override
  public IColorBuilder component(final int index, final int value) {
    return component(index, Colors.toFloat(value, index >= maximumValues.length ? 255 : maximumValues[index]));
  }

  @Override
  public IColor build() {
    return new Color(this.colorSpace, new float[] { this.red, this.green, this.blue }, this.opacity);
  }

}