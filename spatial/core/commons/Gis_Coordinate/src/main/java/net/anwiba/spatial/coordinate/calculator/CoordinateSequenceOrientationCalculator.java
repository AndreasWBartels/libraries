/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.spatial.coordinate.calculator;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.Orientation;

public class CoordinateSequenceOrientationCalculator {

  private static int getUpperLeftCoordinateIndex(final ICoordinateSequence coordinateSequence) {
    int index = 0;
    int i = 0;
    double minX = Double.POSITIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;
    for (int j = 0; j < coordinateSequence.getNumberOfCoordinates(); j++) {
      if (coordinateSequence.getXValue(j) < minX) {
        index = i;
        minX = coordinateSequence.getXValue(j);
        maxY = coordinateSequence.getYValue(j);
      } else if (coordinateSequence.getXValue(j) == minX && maxY > coordinateSequence.getYValue(j)) {
        index = i;
        minX = coordinateSequence.getXValue(j);
        maxY = coordinateSequence.getYValue(j);
      }
      i++;
    }
    return index;
  }

  public static Orientation getOrientation(final ICoordinateSequence coordinateSequence) {
    return isOrientationPositive(coordinateSequence)
        ? Orientation.POSITIVE
        : Orientation.NEGATIVE;
  }

  public static boolean isOrientationPositive(final ICoordinateSequence coordinateSequence) {
    if (coordinateSequence.getNumberOfCoordinates() < 3) {
      throw new IllegalArgumentException("Number of Coordinates must be 3 or more"); //$NON-NLS-1$
    }
    final int upperLeftCoordinateIndex = getUpperLeftCoordinateIndex(coordinateSequence);
    final ICoordinate upperLeftCoordinate = coordinateSequence.getCoordinateN(upperLeftCoordinateIndex);
    final ICoordinate successor = getSuccessor(coordinateSequence, upperLeftCoordinateIndex, upperLeftCoordinate);
    if (upperLeftCoordinate.getXValue() == successor.getXValue()) {
      return false;
    }
    final ICoordinate predecessor = getPredecessor(coordinateSequence, upperLeftCoordinateIndex, upperLeftCoordinate);
    if (predecessor.getXValue() == upperLeftCoordinate.getXValue()) {
      return true;
    }
    final double a =
        nullSaveDividing(successor.getYValue() - upperLeftCoordinate.getYValue(), successor.getXValue()
            - upperLeftCoordinate.getXValue());
    final double b =
        nullSaveDividing(upperLeftCoordinate.getYValue() - predecessor.getYValue(), upperLeftCoordinate.getXValue()
            - predecessor.getXValue());
    return (a <= b);
  }

  private static double nullSaveDividing(final double dividend, final double divisor) {
    return divisor == 0
        ? Double.POSITIVE_INFINITY
        : dividend / divisor;
  }

  private static ICoordinate getPredecessor(
      final ICoordinateSequence coordinateSequence,
      final int upperLeftCoordinateIndex,
      final ICoordinate upperLeftCoordinate) {
    final int numberOfCoordinates = coordinateSequence.getNumberOfCoordinates();
    int predecessorIndex = upperLeftCoordinateIndex == 0
        ? numberOfCoordinates - 1
        : upperLeftCoordinateIndex - 1;
    while (coordinateSequence.getXValue(predecessorIndex) == upperLeftCoordinate.getXValue()
        && coordinateSequence.getYValue(predecessorIndex) == upperLeftCoordinate.getYValue()) {
      predecessorIndex--;
      if (predecessorIndex == upperLeftCoordinateIndex) {
        throw new IllegalArgumentException("All sequence coordinates are equals"); //$NON-NLS-1$
      }
      if (predecessorIndex < 0) {
        predecessorIndex = numberOfCoordinates - 1;
      }
    }
    return coordinateSequence.getCoordinateN(predecessorIndex);
  }

  private static ICoordinate getSuccessor(
      final ICoordinateSequence coordinateSequence,
      final int upperLeftCoordinateIndex,
      final ICoordinate upperLeftCoordinate) {
    final int numberOfCoordinates = coordinateSequence.getNumberOfCoordinates();
    int successorIndex = (upperLeftCoordinateIndex + 1) % numberOfCoordinates;
    while (upperLeftCoordinate.getXValue() == coordinateSequence.getXValue(successorIndex)
        && upperLeftCoordinate.getYValue() == coordinateSequence.getYValue(successorIndex)) {
      successorIndex = ++successorIndex % numberOfCoordinates;
      if (successorIndex == upperLeftCoordinateIndex) {
        throw new IllegalArgumentException("All sequence coordinates are equals"); //$NON-NLS-1$
      }
    }
    return coordinateSequence.getCoordinateN(successorIndex);
  }

}
