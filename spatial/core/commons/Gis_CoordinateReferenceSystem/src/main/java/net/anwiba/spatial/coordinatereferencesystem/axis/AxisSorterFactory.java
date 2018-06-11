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
 
package net.anwiba.spatial.coordinatereferencesystem.axis;

import java.util.Arrays;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Axis;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.AxisOrientation;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystem;

public class AxisSorterFactory {

  public static final class AxisSorter implements IAxisSorter {
    private final int[] mapping;

    public AxisSorter(final int[] mapping) {
      this.mapping = mapping;
    }

    @Override
    public ICoordinate sort(final ICoordinate coordinate) {
      final double[] ordinates = coordinate.getValues();
      final double[] values = new double[ordinates.length];
      for (int i = 0; i < values.length; i++) {
        if (i < this.mapping.length) {
          values[this.mapping[i]] = ordinates[i];
          continue;
        }
        values[i] = ordinates[i];
      }
      return new Coordinate(values, coordinate.isMeasured());
    }
  }

  public static final class NeutralAxisSorter implements IAxisSorter {
    @Override
    public ICoordinate sort(final ICoordinate coordinate) {
      return coordinate;
    }
  }

  public IAxisSorter createSorter(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    final ICoordinateSystem coordinateSystem = coordinateReferenceSystem.getCoordinateSystem();
    return createSorter(coordinateSystem.getAxises());
  }

  public IAxisSorter createInvertSorter(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    final ICoordinateSystem coordinateSystem = coordinateReferenceSystem.getCoordinateSystem();
    return createSorter(coordinateSystem.getAxises());
  }

  public IAxisSorter createSorter(final Axis[] axises) {
    if (axises == null || axises.length == 0) {
      return new NeutralAxisSorter();
    }
    if (axises.length < 2) {
      throw new IllegalArgumentException();
    }
    return new AxisSorter(createMapping(axises));
  }

  public IAxisSorter createInvertSorter(final Axis[] axises) {
    if (axises == null || axises.length == 0) {
      return new NeutralAxisSorter();
    }
    if (axises.length < 2) {
      throw new IllegalArgumentException();
    }
    return new AxisSorter(createInvertMapping(axises));
  }

  private int[] createInvertMapping(final Axis[] axises) {
    final int[] mapping = new int[axises.length];
    Arrays.fill(mapping, Integer.MIN_VALUE);
    int others = 0;
    for (int i = axises.length - 1; i > -1; i--) {
      final AxisOrientation orientation = axises[i].getOrientation();
      if (orientation == null) {
        mapping[i] = i;
        continue;
      }
      if (orientation.equals(AxisOrientation.EAST) || orientation.equals(AxisOrientation.WEST)) {
        mapping[0] = i;
        continue;
      }
      if (orientation.equals(AxisOrientation.NORTH) || orientation.equals(AxisOrientation.SOUTH)) {
        mapping[1] = i;
        continue;
      }
      if (orientation.equals(AxisOrientation.UP) || orientation.equals(AxisOrientation.DOWN)) {
        mapping[2] = i;
        continue;
      }
      mapping[mapping.length - (++others)] = i;
    }
    return mapping;
  }

  private int[] createMapping(final Axis[] axises) {
    final int[] mapping = new int[axises.length];
    Arrays.fill(mapping, Integer.MIN_VALUE);
    int others = 0;
    for (int i = axises.length - 1; i > -1; i--) {
      final AxisOrientation orientation = axises[i].getOrientation();
      if (orientation == null) {
        mapping[i] = i;
        continue;
      }
      if (orientation.equals(AxisOrientation.EAST) || orientation.equals(AxisOrientation.WEST)) {
        mapping[i] = 0;
        continue;
      }
      if (orientation.equals(AxisOrientation.NORTH) || orientation.equals(AxisOrientation.SOUTH)) {
        mapping[i] = 1;
        continue;
      }
      if (orientation.equals(AxisOrientation.UP) || orientation.equals(AxisOrientation.DOWN)) {
        mapping[i] = 2;
        continue;
      }
      mapping[i] = mapping.length - (++others);
    }
    return mapping;
  }
}