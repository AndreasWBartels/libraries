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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class ColorUtilities {

  public static Color create(final Color minColor, final Color maxColor, final double value) {
    try {
      return new Color(getColorPart(minColor.getRed(), maxColor.getRed(), value),
          getColorPart(
              minColor.getGreen(),
              maxColor.getGreen(),
              value),
          getColorPart(minColor.getBlue(), maxColor.getBlue(), value),
          getColorPart(
              minColor.getAlpha(),
              maxColor.getAlpha(),
              value));
    } catch (final Exception exception) {
      return Color.RED;
    }
  }

  private static int getColorPart(final int min, final int max, final double value) {
    final long round = Math.round((min + (max - min) * value));
    return (int) round;
  }

  public static Color[] create(final Color minColor, final Color maxColor, final int size) {
    final List<Color> colors = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      final double value = (double) i / (double) size;
      colors.add(create(minColor, maxColor, value));
    }
    return colors.toArray(new Color[colors.size()]);
  }

  public static boolean isTransparent(final Color color) {
    return color == null
        || color.getAlpha() == 0;
  }
}