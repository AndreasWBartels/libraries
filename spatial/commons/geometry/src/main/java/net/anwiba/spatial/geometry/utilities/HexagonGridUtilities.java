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

import net.anwiba.commons.utilities.math.MathWrapper;
import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;

import java.util.List;

public class HexagonGridUtilities {

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
    final int column = toColumn(key);
    final int row = toRow(key);
    return List.of(toKey(row, column),
        toKey(row, column + 1),
        toKey(row, column - 1),
        toKey(row - 1, column),
        toKey(row + 1, column),
        toKey(row - 1, column + 1),
        toKey(row + 1, column - 1));
  }

  public static long convertKey(final long sourceKey, final double sourceRadius, final double targetRadius) {
    final double gridWidth = gridWith(sourceRadius);
    final int column = toColumn(sourceKey);
    final int row = toRow(sourceKey);
    final double centerY = getCenterY(row, sourceRadius);
    final double centerX = getCenterX(column, row, gridWidth);
    return createKey(centerX, centerY, targetRadius);
  }

  public static long createKey(final ICoordinate coordinate, final double clusterGridRadius) {
    return createKey(coordinate.getXValue(), coordinate.getYValue(), clusterGridRadius);
  }

  public static long createKey(final double x, final double y, final double radius) {
    final double gridWidth = gridWith(radius);
    final double halfWidth = gridWidth / 2.;
    final double relativeX = (x - halfWidth) / gridWidth;
    final double relativeY = y / radius;
    final double temp = MathWrapper.floor(relativeX + relativeY);
    final int row = (int) MathWrapper.floor((MathWrapper.floor(relativeY - relativeX) + temp) / 3.);
    final int column = (int) (MathWrapper.floor((MathWrapper.floor(2. * relativeX + 1.) + temp) / 3.) - row);
    return toKey(row, column);
  }

  public static double gridWith(final double radius) {
    return MathWrapper.sqrt(3.) * radius;
  }

  public static double getCenterX(final int column, final int row, final double gridWidth) {
    return column * gridWidth + (row + 1) * gridWidth / 2.;
  }

  public static double getCenterY(final int row, final double radius) {
    return row * 3. / 2. * radius + radius;
  }

  public static ICoordinate center(final long key, final double radius) {
    final double gridWidth = gridWith(radius);
    final int column = toColumn(key);
    final int row = toRow(key);
    final double centerY = getCenterY(row, radius);
    final double centerX = getCenterX(column, row, gridWidth);
    return new Coordinate(centerX, centerY);
  }

}

