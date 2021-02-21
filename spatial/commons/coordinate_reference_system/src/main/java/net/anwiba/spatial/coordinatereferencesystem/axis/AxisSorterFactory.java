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
// Copyright (c) 2009 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem.axis;

import java.util.Arrays;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.Envelope;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Axis;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.AxisOrientation;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.IAxisOrientation;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystem;

public class AxisSorterFactory implements IAxisSorterFactory {

  public static final class XySwappingAxisSorter implements IAxisSorter {
    @Override
    public ICoordinate sort(final ICoordinate coordinate) {
      final double[] ordinates = coordinate.getValues();
      final double[] values = new double[ordinates.length];
      for (int i = 0; i < values.length; i++) {
        if (i == 0) {
          values[1] = ordinates[i];
          continue;
        }
        if (i == 1) {
          values[0] = ordinates[i];
          continue;
        }
        values[i] = ordinates[i];
      }
      return new Coordinate(values, coordinate.isMeasured());
    }

    @Override
    public IEnvelope sort(final IEnvelope envelope) {
      return Envelope.create(sort(envelope.getMinimum()), sort(envelope.getMaximum()));
    }
  }

  public static final class AxisSorter implements IAxisSorter {
    private final int[] mapping;

    public AxisSorter(final int[] mapping) {
      this.mapping = mapping;
    }

    @Override
    public ICoordinate sort(final ICoordinate coordinate) {
      if (coordinate == null) {
        return null;
      }
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

    @Override
    public IEnvelope sort(final IEnvelope envelope) {
      return envelope;
    }
  }

  final static NeutralAxisSorter neutralAxisSorter = new NeutralAxisSorter();
  final static XySwappingAxisSorter xySwappingAxisSorter = new XySwappingAxisSorter();

  @Override
  public IAxisSorter createSorter(final AxisOrder axisOrder,
      final ICoordinateReferenceSystem coordinateReferenceSystem) {
    switch (axisOrder) {
      case COORDINATEREFERENCESYSTEM: {
        if (coordinateReferenceSystem == null) {
          return neutralAxisSorter();
        }
        final ICoordinateSystem coordinateSystem = coordinateReferenceSystem.getCoordinateSystem();
        return createSorter(coordinateSystem.getAxises());
      }
      case XY: {
        return neutralAxisSorter();
      }
      case YX: {
        return swapXyAxisSorter();
      }
    }
    return neutralAxisSorter();
  }

  @Override
  public IAxisSorter createInvertSorter(final AxisOrder axisOrder,
      final ICoordinateReferenceSystem coordinateReferenceSystem) {
    switch (axisOrder) {
      case COORDINATEREFERENCESYSTEM: {
        if (coordinateReferenceSystem == null) {
          return neutralAxisSorter();
        }
        final ICoordinateSystem coordinateSystem = coordinateReferenceSystem.getCoordinateSystem();
        return createInvertSorter(coordinateSystem.getAxises());
      }
      case XY: {
        return neutralAxisSorter();
      }
      case YX: {
        return swapXyAxisSorter();
      }
    }
    return neutralAxisSorter();
  }

  public static IAxisSorter swapXyAxisSorter() {
    return xySwappingAxisSorter;
  }

  public IAxisSorter createSorter(final Axis[] axises) {
    if (axises == null || axises.length == 0) {
      return neutralAxisSorter();
    }
    if (axises.length < 2) {
      throw new IllegalArgumentException();
    }
    return new AxisSorter(createMapping(axises));
  }

  public static IAxisSorter neutralAxisSorter() {
    return neutralAxisSorter;
  }

  public IAxisSorter createInvertSorter(final Axis[] axises) {
    if (axises == null || axises.length == 0) {
      return neutralAxisSorter();
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
      final IAxisOrientation orientation = axises[i].getOrientation();
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
      final IAxisOrientation orientation = axises[i].getOrientation();
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
