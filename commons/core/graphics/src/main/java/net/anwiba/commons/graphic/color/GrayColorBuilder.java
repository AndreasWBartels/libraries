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

public class GrayColorBuilder implements IColorBuilder {

  private final ColorSpace colorSpace = ColorSpaces.CS_GRAY;
  private float gray = 1.0f;
  private float opacity = 1.0f;
  private int[] maximumValues;

  public GrayColorBuilder() {
    this(java.awt.Color.BLACK);
  }

  public GrayColorBuilder(final java.awt.Color color) {
    this.maximumValues = ColorSpaces.maximumValues(colorSpace);
    final float[] components = color.getRGBComponents(null);
    final float[] values = this.colorSpace.fromRGB(components);
    this.gray = values[0];
    this.opacity = components[components.length - 1];
  }

  @Override
  public GrayColorBuilder component(final int index, final float value) {
    switch (index) {
      case 0 -> gray(value);
      case 1 -> opacity(value);
    }
    return this;
  }

  @Override
  public GrayColorBuilder min() {
    this.gray = 0.0f;
    return this;
  }

  @Override
  public GrayColorBuilder max() {
    this.gray = 1.0f;
    return this;
  }

  public GrayColorBuilder gray(final float gray) {
    this.gray = gray;
    return this;
  }

  public GrayColorBuilder gray(final int gray) {
    this.gray = Colors.toFloat(gray, maximumValues[0]);
    return this;
  }

  @Override
  public GrayColorBuilder opacity(final float opacity) {
    this.opacity = opacity;
    return this;
  }

  @Override
  public GrayColorBuilder opacity(final int opacity) {
    this.opacity = Colors.toFloat(opacity, 255);
    return this;
  }

  @Override
  public IColorBuilder component(final int index, final int value) {
    return component(index, Colors.toFloat(value, index >= maximumValues.length ? 255 : maximumValues[index]));
  }

  @Override
  public IColor build() {
    return new Color(this.colorSpace, new float[] { this.gray }, this.opacity);
  }

}