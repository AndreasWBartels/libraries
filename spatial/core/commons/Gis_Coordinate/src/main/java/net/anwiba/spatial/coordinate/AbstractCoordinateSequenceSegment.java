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
package net.anwiba.spatial.coordinate;

import java.util.Iterator;

import net.anwiba.commons.utilities.ArrayUtilities;

public abstract class AbstractCoordinateSequenceSegment implements ICoordinateSequenceSegment {

  private static final long serialVersionUID = 1L;
  private final boolean isMeasured;
  private final double[][] ordinates;
  private final int measuredIndex;
  private final CoordinateSequenceSegmentType coordinateSequenceSegmentType;
  private final IEnvelope envelope;

  public AbstractCoordinateSequenceSegment(
      final double ordinates[][],
      final boolean isMeasured,
      final CoordinateSequenceSegmentType coordinateSequenceSegmentType) {
    this.coordinateSequenceSegmentType = coordinateSequenceSegmentType;
    if (ordinates.length < 2 || isMeasured && ordinates.length < 3) {
      throw new IllegalArgumentException("Coordinatesequence dimension is lower than 2"); //$NON-NLS-1$
    }

    for (int i = 1; i < ordinates.length; i++) {
      if (ordinates[0].length != ordinates[i].length) {
        throw new IllegalArgumentException("ordinates count are not equal"); //$NON-NLS-1$
      }
    }
    if (isMeasured) {
      this.measuredIndex = ordinates.length - 1;
    } else {
      this.measuredIndex = 0;
    }
    this.ordinates = ordinates;
    this.isMeasured = isMeasured;

    final double[] min = new double[ordinates.length];
    final double[] max = new double[ordinates.length];

    for (int i = 0; i < ordinates.length; i++) {
      min[i] = ArrayUtilities.getMin(ordinates[i]);
      max[i] = ArrayUtilities.getMax(ordinates[i]);
    }
    this.envelope = new Envelope(min, max, isMeasured);
  }

  @Override
  public double[] getXValues() {
    return getOrdinates(ICoordinate.X, "Coordinatesequence has no x values"); //$NON-NLS-1$
  }

  @Override
  public double[] getYValues() {
    return getOrdinates(ICoordinate.Y, "Coordinatesequence has no y values"); //$NON-NLS-1$
  }

  @Override
  public double[] getZValues() {
    return getOrdinates(ICoordinate.Z, "Coordinatesequence has no z values"); //$NON-NLS-1$
  }

  @Override
  public double[] getMeasuredValues() {
    if (!this.isMeasured) {
      throw new IllegalArgumentException("Coordinatesequence has no measured values"); //$NON-NLS-1$
    }
    return this.ordinates[this.measuredIndex];
  }

  private double[] getOrdinates(final int index, final String message) {
    final int p = this.isMeasured ? this.ordinates.length - 2 : this.ordinates.length - 1;
    if (p >= index) {
      return this.ordinates[index];
    }
    throw new IllegalArgumentException(message);
  }

  @Override
  public double getXValue(final int index) {
    return getOrdinate(index, ICoordinate.X, "Coordinatesequence has no x values"); //$NON-NLS-1$
  }

  @Override
  public double getYValue(final int index) {
    return getOrdinate(index, ICoordinate.Y, "Coordinatesequence has no y values"); //$NON-NLS-1$
  }

  @Override
  public double getZValue(final int index) {
    return getOrdinate(index, ICoordinate.Z, "Coordinatesequence has no z values"); //$NON-NLS-1$
  }

  private double getOrdinate(final int index, final int dimension, final String message) {
    final int p = this.isMeasured ? this.ordinates.length - 2 : this.ordinates.length - 1;
    if (p >= dimension) {
      return this.ordinates[dimension][index];
    }
    throw new IllegalArgumentException(message);
  }

  @Override
  public int getDimension() {
    return this.ordinates.length - (this.isMeasured ? 1 : 0);
  }

  @Override
  public boolean isMeasured() {
    return this.isMeasured;
  }

  @Override
  public double getMeasuredValue(final int index) {
    if (!this.isMeasured) {
      throw new IllegalArgumentException("Coordinatesequence has no measured values"); //$NON-NLS-1$
    }
    return this.ordinates[this.measuredIndex][index];
  }

  @Override
  public ICoordinate getCoordinateN(final int index) {
    final double[] values = new double[this.ordinates.length];
    for (int i = 0; i < values.length; i++) {
      values[i] = this.ordinates[i][index];
    }
    return new Coordinate(values, this.isMeasured);
  }

  @Override
  public int getNumberOfCoordinates() {
    return this.ordinates[0].length;
  }

  @Override
  public double[][] getValues() {
    return this.ordinates;
  }

  @Override
  public String toString() {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("["); //$NON-NLS-1$
    boolean cordinateFlag = false;
    boolean ordinateFlag = false;
    for (final ICoordinate coordinate : getCoordinates()) {
      if (cordinateFlag) {
        buffer.append("; "); //$NON-NLS-1$
      }
      final double[] values = coordinate.getValues();
      ordinateFlag = false;
      for (final double value : values) {
        if (ordinateFlag) {
          buffer.append(", "); //$NON-NLS-1$
        }
        buffer.append(value);
        ordinateFlag = true;
      }
      cordinateFlag = true;
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  @Override
  public Iterable<ICoordinate> getCoordinates() {
    return new Iterable<ICoordinate>() {

      @Override
      public Iterator<ICoordinate> iterator() {
        return new Iterator<ICoordinate>() {

          int index = 0;

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }

          @Override
          public ICoordinate next() {
            return getCoordinateN(this.index++);
          }

          @Override
          public boolean hasNext() {
            return this.index < getNumberOfCoordinates();
          }
        };
      }
    };
  }

  @Override
  public CoordinateSequenceSegmentType getType() {
    return this.coordinateSequenceSegmentType;
  }

  @Override
  public IEnvelope getEnvelope() {
    return this.envelope;
  }

  @Override
  public boolean isEmpty() {
    return this.ordinates.length == 0;
  }
}