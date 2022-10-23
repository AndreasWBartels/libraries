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

public class CmykColorBuilder implements IColorBuilder {

  private final ColorSpace colorSpace = ColorSpaces.CS_CMYK;
  private float cyan = 1.0f;
  private float magenta = 1.0f;
  private float yellow = 1.0f;
  private float black = 1.0f;
  private float opacity = 1.0f;
  private int[] maximumValues;

  public CmykColorBuilder() {
    this(java.awt.Color.BLACK);
  }

  public CmykColorBuilder(final java.awt.Color color) {
    this.maximumValues = ColorSpaces.maximumValues(colorSpace);
    final float[] components = color.getRGBComponents(null);
    final float[] cmyk = this.colorSpace.fromRGB(components);
    this.cyan = cmyk[0];
    this.magenta = cmyk[1];
    this.yellow = cmyk[2];
    this.black = cmyk[3];
    this.opacity = components[components.length - 1];
  }

  @Override
  public CmykColorBuilder component(final int index, final float value) {
    switch (index) {
      case 0 -> cyan(value);
      case 1 -> magenta(value);
      case 2 -> yellow(value);
      case 3 -> black(value);
      case 4 -> opacity(value);
    }
    return this;
  }

  @Override
  public CmykColorBuilder min() {
    this.cyan = 0.0f;
    this.magenta = 0.0f;
    this.yellow = 0.0f;
    this.black = 0.0f;
    this.opacity = 1.0f;
    return this;
  }

  @Override
  public CmykColorBuilder max() {
    this.cyan = 1.0f;
    this.magenta = 1.0f;
    this.yellow = 1.0f;
    this.black = 1.0f;
    this.opacity = 1.0f;
    return this;
  }

  public CmykColorBuilder cyan(final float cyan) {
    this.cyan = cyan;
    return this;
  }

  public CmykColorBuilder cyan(final int cyan) {
    this.cyan = Colors.toFloat(cyan, maximumValues[0]);
    return this;
  }

  public CmykColorBuilder magenta(final float magenta) {
    this.magenta = magenta;
    return this;
  }

  public CmykColorBuilder magenta(final int magenta) {
    this.magenta = Colors.toFloat(magenta, maximumValues[1]);
    return this;
  }

  public CmykColorBuilder yellow(final float yellow) {
    this.yellow = yellow;
    return this;
  }

  public CmykColorBuilder yellow(final int yellow) {
    this.yellow = Colors.toFloat(yellow, maximumValues[2]);
    return this;
  }

  public CmykColorBuilder black(final float black) {
    this.black = black;
    return this;
  }

  public CmykColorBuilder black(final int black) {
    this.black = Colors.toFloat(black, maximumValues[3]);
    return this;
  }

  @Override
  public CmykColorBuilder opacity(final float opacity) {
    this.opacity = opacity;
    return this;
  }

  @Override
  public CmykColorBuilder opacity(final int opacity) {
    this.opacity = Colors.toFloat(opacity, 255);
    return this;
  }

  @Override
  public IColorBuilder component(final int index, final int value) {
    return component(index, Colors.toFloat(value, index >= maximumValues.length ? 255 : maximumValues[index]));
  }

  @Override
  public IColor build() {
    return new Color(this.colorSpace, new float[] { this.cyan, this.magenta, this.yellow, this.black }, this.opacity);
  }
}