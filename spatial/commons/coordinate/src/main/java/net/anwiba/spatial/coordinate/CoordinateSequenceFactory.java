/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2006 by Andreas W. Bartels
package net.anwiba.spatial.coordinate;

import java.util.List;

public class CoordinateSequenceFactory implements ICoordinateSequenceFactory {

  private static final long serialVersionUID = 1L;

  @Override
  public ICoordinateSequence createEmptyCoordinateSequence(final int dimension, final boolean isMeasured) {
    final double[][] coordinates = create(dimension, 0, isMeasured);
    return create(coordinates, isMeasured);
  }

  @Override
  public double[][] create(final int dimension, final int length, final boolean isMeasured) {
    final int arrayDimension = isMeasured ? dimension + 1 : dimension;
    final double[][] coordinates = new double[arrayDimension][];
    for (int i = 0; i < coordinates.length; i++) {
      coordinates[i] = new double[length];
    }
    if (isMeasured) {
      for (int i = 0; i < coordinates[arrayDimension - 1].length; i++) {
        coordinates[arrayDimension - 1][i] = Double.NaN;
      }
    }
    return coordinates;
  }

  @Override
  public ICoordinateSequence create(final double[] values, final int dimension, final boolean isMeasured) {
    final int numberOfOrdinates = dimension + (isMeasured ? 1 : 0);
    final int numberOfCoordinates = values.length / numberOfOrdinates;
    final double[][] coordinates = new double[numberOfOrdinates][numberOfCoordinates];
    for (int j = 0; j < numberOfOrdinates; j++) {
      for (int i = 0; i < numberOfCoordinates; i++) {
        coordinates[j][i] = values[numberOfOrdinates * i + j];
      }
    }
    return create(coordinates, isMeasured);
  }

  @Override
  public ICoordinateSequence create(final double xs, final double ys) {
    final double[][] coordinates = { { xs }, { ys } };
    return create(coordinates, false);
  }

  @Override
  public ICoordinateSequence create(final double xs, final double ys, final double zs) {
    final double[][] coordinates = { { xs }, { ys }, { zs } };
    return create(coordinates, false);
  }

  @Override
  public ICoordinateSequence create(final double xs, final double ys, final double ns, final boolean isMeasured) {
    final double[][] coordinates = { { xs }, { ys }, { ns } };
    return create(coordinates, isMeasured);
  }

  @Override
  public ICoordinateSequence create(final double xs, final double ys, final double zs, final double ms) {
    final double[][] coordinates = { { xs }, { ys }, { zs }, { ms } };
    return create(coordinates, true);
  }

  @Override
  public ICoordinateSequence create(final double[] xs, final double[] ys) {
    final double[][] coordinates = { xs, ys };
    return create(coordinates, false);
  }

  @Override
  public ICoordinateSequence create(final double[] xs, final double[] ys, final double[] ns, final boolean isMeasured) {
    final double[][] coordinates = { xs, ys, ns };
    return create(coordinates, isMeasured);
  }

  @Override
  public ICoordinateSequence create(final double[] xs, final double[] ys, final double[] zs, final double[] ms) {
    final double[][] coordinates = { xs, ys, zs, ms };
    return create(coordinates, true);
  }

  @Override
  public ICoordinateSequence create(final double[][] coordinates) {
    return create(coordinates, false);
  }

  @Override
  public ICoordinateSequence create(final double[][] coordinates, final boolean isMeasured) {
    return new CoordinateSequence(new LineCoordinateSequenceSegment(coordinates, isMeasured));
  }

  @Override
  public ICoordinateSequence create(final ICoordinate... coordinates) {
    final int dimension = getDimension(coordinates);
    final boolean isMeasured = isMeasured(coordinates);
    final boolean is3D = dimension == 3;
    final double[][] array = new double[dimension + (isMeasured ? 1 : 0)][];
    for (int i = 0; i < array.length; i++) {
      array[i] = new double[coordinates.length];
      for (int j = 0; j < coordinates.length; j++) {
        final ICoordinate coordinate = coordinates[j];
        if (coordinate == null) {
          continue;
        }
        if (i == 0) {
          array[i][j] = coordinate.getValue(i);
        } else if (i == 1) {
          array[i][j] = coordinate.getValue(i);
        } else if (i == 2 && is3D) {
          array[i][j] = coordinate.getZValue();
        } else if (i == 2 && !is3D && isMeasured) {
          array[i][j] = coordinate.getMeasuredValue();
        } else if (i == 3 && is3D && isMeasured) {
          array[i][j] = coordinate.getMeasuredValue();
        } else {
          array[i][j] = Double.NaN;
        }
      }
    }
    return create(array, isMeasured);
  }

  private static int getDimension(final ICoordinate[] coordinates) {
    int dimension = Integer.MAX_VALUE;
    for (final ICoordinate coordinate : coordinates) {
      if (coordinate == null) {
        continue;
      }
      dimension = Math.min(dimension, coordinate.getDimension());
    }
    return dimension == Integer.MAX_VALUE ? 2 : dimension;
  }

  private static boolean isMeasured(final ICoordinate[] coordinates) {
    for (final ICoordinate coordinate : coordinates) {
      if (!coordinate.isMeasured()) {
        return false;
      }
    }
    return coordinates.length == 0 ? false : true;
  }

  @Override
  public ICoordinateSequence create(
      final double[] ordinates,
      final int numberOfPoints,
      final int dimensions,
      final boolean isMeasured) {
    final double[][] coordinates = new double[dimensions][];
    for (int i = 0; i < dimensions; i++) {
      coordinates[i] = new double[numberOfPoints];
    }
    int counter = 0;
    for (int n = 0; n < numberOfPoints; n++) {
      for (int i = 0; i < dimensions; i++) {
        coordinates[i][n] = ordinates[counter++];
      }
    }
    return create(coordinates, isMeasured);
  }

  @Override
  public ICoordinateSequence create(final List<ICoordinate> coordinates) {
    return create(coordinates.toArray(new ICoordinate[coordinates.size()]));
  }

  @Override
  public ICoordinateSequence create(
      final int coordinateDimension,
      final double[] coordinates,
      final boolean is3D,
      final boolean isMeasured) {
    final int numberOfCoordinates = coordinates.length / coordinateDimension;
    final double[][] values = new double[coordinateDimension][numberOfCoordinates];
    for (int i = 0; i < numberOfCoordinates; ++i) {
      for (int j = 0; j < coordinateDimension; ++j) {
        values[j][i] = coordinates[i * coordinateDimension + j];
      }
    }
    return create(values, (coordinateDimension == 3 && isMeasured && !is3D) || (coordinateDimension > 3 && isMeasured));
  }

  @Override
  public ICoordinateSequence create(final int dimension, final boolean isMeasured, final double... values) {
    return create(dimension, values, isMeasured ? dimension > 3 : dimension > 2, isMeasured);
  }
}
