/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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
package net.anwiba.spatial.geometry.utilities;

import java.util.List;

import net.jafama.FastMath;

public class HoneycombUtilities {

  public static long createKey(final double x, final double y, final double radius) {
    double gridWidth = FastMath.sqrt(3.) * radius;
    double halfWidth = gridWidth / 2.;
    double relativeX = (x - halfWidth) / gridWidth;
    double relativeY = y / radius;
    double temp = FastMath.floor(relativeX + relativeY);
    int row = FastMath.floorToInt((FastMath.floor(relativeY - relativeX) + temp) / 3.);
    int column = FastMath.floorToInt((FastMath.floor(2. * relativeX + 1.) + temp) / 3.) - row;
    return toKey(row, column);
  }

  public static long toKey(final int row, final int column) {
    return (((long) row) << 32) | (column & 0xffffffffL);
  }

  public static int toColumn(final long key) {
    return (int) key;
  }

  public static int toRow(final long key) {
    return (int) (key >> 32);
  }

  public static List<Long> getNeighborKeys(final long key) {
    int r = toRow(key);
    int c = toColumn(key);
    return List.of(toKey(r, c),
        toKey(r, c + 1),
        toKey(r, c - 1),
        toKey(r - 1, c),
        toKey(r + 1, c),
        toKey(r - 1, c + 1),
        toKey(r + 1, c - 1));
  }

  public static long convertKey(final long sourceKey, final double sourceRadius, final double targetRadius) {
    double gridWidth = Math.sqrt(3.) * sourceRadius;
    int column = toColumn(sourceKey);
    int row = toRow(sourceKey);
    double centerY = getCenterY(row, sourceRadius);
    double centerX = getCenterX(column, row, gridWidth);
    return createKey(centerX, centerY, targetRadius);
  }

  public static double getCenterX(final int column, final int row, final double gridWidth) {
    return column * gridWidth + (row + 1) * gridWidth / 2.;
  }

  public static double getCenterY(final int row, final double radius) {
    return row * 3. / 2. * radius + radius;
  }
}
