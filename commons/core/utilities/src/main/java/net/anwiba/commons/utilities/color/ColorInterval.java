/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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

import net.anwiba.commons.lang.object.ObjectUtilities;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorInterval implements IColorInterval {

  private static final long serialVersionUID = 1L;
  private final Color minColor;
  private final Color maxColor;

  private final Map<Integer, Color> cache = new HashMap<>();
  private final int numberOfColors;

  public ColorInterval(final Color minColor, final Color maxColor) {
    this.minColor = minColor;
    this.maxColor = maxColor;
    this.numberOfColors = calulateNumberOfColors(minColor, maxColor);
  }

  private static int calulateNumberOfColors(final Color minColor, final Color maxColor) {
    int result = 0;
    result = Math.max(result, calculateDiffernce(minColor.getAlpha(), maxColor.getAlpha()));
    result = Math.max(result, calculateDiffernce(minColor.getBlue(), maxColor.getBlue()));
    result = Math.max(result, calculateDiffernce(minColor.getGreen(), maxColor.getGreen()));
    result = Math.max(result, calculateDiffernce(minColor.getRed(), maxColor.getRed()));
    return result;
  }

  private static int calculateDiffernce(final int value, final int other) {
    return Math.max(value, other) - Math.min(value, other);
  }

  @Override
  public Color get(final double fraction) {
    final Integer key = Integer.valueOf(fraction == 0
        ? 0
        : (int) Math.round(this.numberOfColors / Math.abs(fraction)));
    if (this.cache.containsKey(key)) {
      this.cache.get(key);
    }
    final Color color = ColorUtilities.create(this.minColor, this.maxColor, fraction);
    this.cache.put(key, color);
    return color;
  }

  @Override
  public Color getMinimumColor() {
    return this.minColor;
  }

  @Override
  public Color getMaximumColor() {
    return this.maxColor;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(getMinimumColor(), getMaximumColor());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof IColorInterval)) {
      return false;
    }
    final IColorInterval other = (IColorInterval) obj;
    return ObjectUtilities.equals(getMinimumColor(), other.getMinimumColor())
        && ObjectUtilities.equals(getMaximumColor(), other.getMaximumColor());
  }

}
