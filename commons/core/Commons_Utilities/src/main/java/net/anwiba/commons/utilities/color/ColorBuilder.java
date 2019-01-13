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
package net.anwiba.commons.utilities.color;

import java.awt.Color;

public class ColorBuilder {

  private Color color;
  private float opacity = 1;

  public ColorBuilder() {
    this(Color.BLACK);
  }

  public ColorBuilder(final Color color) {
    this.color = color;
  }

  public ColorBuilder setColor(final String color) {
    this.color = Color.decode(color);
    return this;
  }

  public ColorBuilder setColor(final Color color) {
    this.color = color;
    return this;
  }

  public ColorBuilder setOpacity(final float opacity) {
    this.opacity = opacity;
    return this;
  }

  public Color build() {
    final float[] rgb = this.color.getRGBColorComponents(null);
    return new Color(rgb[0], rgb[1], rgb[2], this.opacity);
  }

}