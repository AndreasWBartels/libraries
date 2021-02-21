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
package net.anwiba.commons.lang.tree.distance;

public final class LevenshteinStringDistanceCalculator implements IObjectDistanceCalculator<String> {
  @Override
  public double calculate(final String string, final String other) {
    if (string == null || other == null) {
      throw new IllegalArgumentException();
    }
    final int stringLength = string.length();
    final int otherLength = other.length();
    if (stringLength == 0) {
      return otherLength;
    } else if (otherLength == 0) {
      return stringLength;
    }
    int horizontalPreviousCosts[] = new int[stringLength + 1];
    int horizontalCosts[] = new int[stringLength + 1];
    int swappingArray[];
    for (int i = 0; i <= stringLength; i++) {
      horizontalPreviousCosts[i] = i;
    }
    for (int j = 1; j <= otherLength; j++) {
      final char c = other.charAt(j - 1);
      horizontalCosts[0] = j;
      for (int i = 1; i <= stringLength; i++) {
        final int cost = string.charAt(i - 1) == c
            ? 0
            : 1;
        horizontalCosts[i] =
            Math.min(
                Math.min(horizontalCosts[i - 1] + 1, horizontalPreviousCosts[i] + 1),
                horizontalPreviousCosts[i - 1] + cost);
      }
      swappingArray = horizontalPreviousCosts;
      horizontalPreviousCosts = horizontalCosts;
      horizontalCosts = swappingArray;
    }
    return horizontalPreviousCosts[stringLength];
  }
}