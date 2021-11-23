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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.utilities.interval.IntegerInterval;

public class MulitSegmentCoordinateSequence implements ICoordinateSequence {

  private static final long serialVersionUID = 1L;
  private final List<ICoordinateSequenceSegment> segments;
  private final List<ObjectPair<IntegerInterval, ICoordinateSequenceSegment>> indexIntervalsAndSegments = new ArrayList<>();
  private IEnvelope envelope;
  boolean isEmpty;
  int numberOfCoordinates;
  private final boolean isMeasured;
  private final int dimension;
  private int hashcode = -1;

  public MulitSegmentCoordinateSequence(final List<ICoordinateSequenceSegment> segments) {
    this.segments = segments;
    @SuppressWarnings("hiding")
    boolean isEmpty = true;
    @SuppressWarnings("hiding")
    int numberOfCoordinates = 0;
    @SuppressWarnings("hiding")
    boolean isMeasured = segments.isEmpty() ? false : true;
    @SuppressWarnings("hiding")
    int dimension = segments.isEmpty() ? 2 : Integer.MAX_VALUE;
    for (final ICoordinateSequenceSegment segment : this.segments) {
      isMeasured = isMeasured && segment.isMeasured();
      isEmpty = isEmpty && segment.isEmpty();
      final IntegerInterval interval = new IntegerInterval(
          numberOfCoordinates,
          numberOfCoordinates + segment.getNumberOfCoordinates());

      this.indexIntervalsAndSegments.add(new ObjectPair<>(interval, segment));

      numberOfCoordinates = interval.getMaxValue();
      dimension = Math.min(dimension, segment.getDimension());
    }
    this.isEmpty = isEmpty;
    this.numberOfCoordinates = numberOfCoordinates;
    this.isMeasured = isMeasured;
    this.dimension = dimension;
  }

  @Override
  public double getXValue(final int index) {
    return getCoordinateN(index).getXValue();
  }

  @Override
  public double getYValue(final int index) {
    return getCoordinateN(index).getYValue();
  }

  @Override
  public double getZValue(final int index) {
    return getCoordinateN(index).getZValue();
  }

  @Override
  public double[] getXValues() {
    return getOrdinates(ICoordinate.X);
  }

  private double[] getOrdinates(final int index) {
    final double[] result = new double[this.numberOfCoordinates];
    for (final ObjectPair<IntegerInterval, ICoordinateSequenceSegment> objectPair : this.indexIntervalsAndSegments) {
      final double[] values = objectPair.getSecondObject().getValues()[index];
      System.arraycopy(values, 0, result, objectPair.getFirstObject().getMinValue(), values.length);
    }
    return result;
  }

  @Override
  public double[] getYValues() {
    return getOrdinates(ICoordinate.Y);
  }

  @Override
  public double[] getZValues() {
    return getOrdinates(ICoordinate.Z);
  }

  @Override
  public int getDimension() {
    return this.dimension;
  }

  @Override
  public boolean isMeasured() {
    return this.isMeasured;
  }

  @Override
  public double getMeasuredValue(final int index) {
    return getCoordinateN(index).getMeasuredValue();
  }

  @Override
  public double[] getMeasuredValues() {
    if (!this.isMeasured) {
      throw new IllegalArgumentException("Coordinatesequence has no measured values"); //$NON-NLS-1$
    }
    return getOrdinates(this.dimension);
  }

  @Override
  public ICoordinate getCoordinateN(final int index) {
    for (final ObjectPair<IntegerInterval, ICoordinateSequenceSegment> objectPair : this.indexIntervalsAndSegments) {
      if (objectPair.getFirstObject().interact(index)) {
        return objectPair.getSecondObject().getCoordinateN(index - objectPair.getFirstObject().getMinValue());
      }
    }
    throw new ArrayIndexOutOfBoundsException(index);
  }

  @Override
  public int getNumberOfCoordinates() {
    return this.numberOfCoordinates;
  }

  @Override
  public double[][] getValues() {
    final double[][] result = new double[this.segments.iterator().next().getValues().length][this.numberOfCoordinates];
    for (final ObjectPair<IntegerInterval, ICoordinateSequenceSegment> objectPair : this.indexIntervalsAndSegments) {
      final double[][] values = objectPair.getSecondObject().getValues();
      for (int i = 0; i < values.length; i++) {
        System.arraycopy(values[i], 0, result[i], objectPair.getFirstObject().getMinValue(), values[0].length);
      }
    }
    return result;
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
      cordinateFlag = true;
      final double[] values = coordinate.getValues();
      ordinateFlag = false;
      for (final double value : values) {
        if (ordinateFlag) {
          buffer.append(", "); //$NON-NLS-1$
        }
        ordinateFlag = true;
        buffer.append(value);
      }
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

          ICoordinate coordinate = null;
          Iterator<ICoordinate> coordinateIterator;
          Iterator<ICoordinateSequenceSegment> segmentsIterator = MulitSegmentCoordinateSequence.this.segments
              .iterator();

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }

          @Override
          public ICoordinate next() {
            try {
              if (hasNext()) {
                return this.coordinate;
              }
              throw new NoSuchElementException();
            } finally {
              this.coordinate = null;
            }
          }

          @Override
          public boolean hasNext() {
            if (this.coordinate != null) {
              return true;
            }
            if (this.coordinateIterator == null || !this.coordinateIterator.hasNext()) {
              if (!this.segmentsIterator.hasNext()) {
                return false;
              }
              this.coordinateIterator = this.segmentsIterator.next().getCoordinates().iterator();
              if (!this.coordinateIterator.hasNext()) {
                return false;
              }
            }
            this.coordinate = this.coordinateIterator.next();
            return true;
          }
        };
      }
    };
  }

  @Override
  public boolean isClosed() {
    if (getNumberOfCoordinates() < 3) {
      return false;
    }
    return getCoordinateN(0).equals(getCoordinateN(getNumberOfCoordinates() - 1));
  }

  @Override
  public Iterable<ICoordinateSequenceSegment> getCoordinateSequenceSegments() {
    return this.segments;
  }

  @Override
  public IEnvelope getEnvelope() {
    this.envelope = Optional.of(this.envelope).getOr(
        () -> Streams
            .of(this.segments)
            .convert(s -> s.getEnvelope())
            .aggregate(Envelope.NULL_ENVELOPE, (i, e) -> i.concat(e))
            .getOr(() -> Envelope.NULL_ENVELOPE));
    return this.envelope;
  }

  @Override
  public boolean isEmpty() {
    return this.isEmpty;
  }

  @Override
  public boolean isCompouned() {
    return this.segments.size() > 1;
  }

  @Override
  public int hashCode() {
    if (hashcode  == -1) {
      hashcode = Objects.hash(dimension, envelope, isEmpty, isMeasured, numberOfCoordinates, segments);
    }
    return hashcode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MulitSegmentCoordinateSequence other = (MulitSegmentCoordinateSequence) obj;
    return this.dimension == other.dimension && Objects.equals(this.envelope, other.envelope)
        && this.isEmpty == other.isEmpty && this.isMeasured == other.isMeasured
        && this.numberOfCoordinates == other.numberOfCoordinates && Objects.equals(this.segments, other.segments);
  }
}
