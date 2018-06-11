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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CompoundCoordinateSequence implements ICoordinateSequence {

  private static final long serialVersionUID = 1L;
  private final List<ICoordinateSequenceSegment> segments = new ArrayList<>();
  private final int numberOfCoordinates;
  private final IEnvelope envelope;
  private final boolean isEmpty;
  private final boolean isMeasured;
  private final int dimension;

  CompoundCoordinateSequence(final List<ICoordinateSequenceSegment> segments) {
    int pos = 0;
    @SuppressWarnings("hiding")
    IEnvelope envelope = Envelope.NULL_ENVELOPE;
    if (segments.isEmpty()) {
      this.numberOfCoordinates = pos;
      this.envelope = envelope;
      this.isEmpty = true;
      this.isMeasured = false;
      this.dimension = 2;
      return;
    }
    @SuppressWarnings("hiding")
    boolean isEmpty = true;
    @SuppressWarnings("hiding")
    boolean isMeasured = true;
    @SuppressWarnings("hiding")
    int dimension = 3;
    for (final ICoordinateSequenceSegment segment : segments) {
      @SuppressWarnings("hiding")
      final int numberOfCoordinates = segment.getNumberOfCoordinates();
      envelope = envelope.concat(segment.getEnvelope());
      pos += numberOfCoordinates;
      isEmpty &= segment.isEmpty();
      isMeasured &= segment.isMeasured();
      dimension = Math.min(dimension, segment.getDimension());
    }
    segments.addAll(segments);
    this.numberOfCoordinates = pos;
    this.envelope = envelope;
    this.isEmpty = isEmpty;
    this.isMeasured = isMeasured;
    this.dimension = dimension;
  }

  private ICoordinate getCoordinate(
      final BiFunction<ICoordinateSequenceSegment, Integer, ICoordinate> function,
      final int index) {
    int from = 0;
    for (final ICoordinateSequenceSegment segment : this.segments) {
      final int until = from + segment.getNumberOfCoordinates();
      if (index < until) {
        return function.apply(segment, index - from);
      }
      from = until;
    }
    throw new IllegalArgumentException();
  }

  private ICoordinate getCoordinate(final int index) {
    return getCoordinate((segment, pos) -> segment.getCoordinateN(pos), index);
  }

  private double getOrdinate(final BiFunction<ICoordinateSequenceSegment, Integer, Double> function, final int index) {
    int from = 0;
    for (final ICoordinateSequenceSegment segment : this.segments) {
      final int until = from + segment.getNumberOfCoordinates();
      if (index < until) {
        return function.apply(segment, index - from);
      }
      from = until;
    }
    throw new IllegalArgumentException();
  }

  @Override
  public double getXValue(final int index) {
    return getOrdinate((segment, pos) -> segment.getXValue(pos), index);
  }

  @Override
  public double getYValue(final int index) {
    return getOrdinate((segment, pos) -> segment.getYValue(pos), index);
  }

  @Override
  public double getZValue(final int index) {
    return getOrdinate((segment, pos) -> segment.getZValue(pos), index);
  }

  @Override
  public double getMeasuredValue(final int index) {
    return getOrdinate((segment, pos) -> segment.getMeasuredValue(pos), index);
  }

  @Override
  public ICoordinate getCoordinateN(final int index) {
    final ICoordinate coordinate = getCoordinate(index);
    return coordinate;
  }

  private double[] getOrdinates(final Function<ICoordinateSequenceSegment, double[]> function) {
    final double[] values = new double[getNumberOfCoordinates()];
    int pos = 0;
    for (final ICoordinateSequenceSegment segment : this.segments) {
      final int length = segment.getNumberOfCoordinates();
      System.arraycopy(function.apply(segment), 0, segment, pos, length);
      pos += length;
    }
    return values;
  }

  @Override
  public double[] getXValues() {
    return getOrdinates(segment -> segment.getXValues());
  }

  @Override
  public double[] getYValues() {
    return getOrdinates(segment -> segment.getYValues());
  }

  @Override
  public double[] getZValues() {
    return getOrdinates(segment -> segment.getZValues());
  }

  @Override
  public double[] getMeasuredValues() {
    return getOrdinates(segment -> segment.getMeasuredValues());
  }

  @Override
  public double[][] getValues() {
    final double[][] values = new double[getDimension() + (isMeasured() ? 1 : 0)][];
    values[0] = getXValues();
    values[1] = getYValues();
    if (getDimension() == 2) {
      if (isMeasured()) {
        values[2] = getMeasuredValues();
      }
      return values;
    }
    values[2] = getZValues();
    if (isMeasured()) {
      values[3] = getMeasuredValues();
    }
    return values;
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
  public int getNumberOfCoordinates() {
    return this.numberOfCoordinates;
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
  public boolean isClosed() {
    if (getNumberOfCoordinates() == 0) {
      return false;
    }
    return getCoordinateN(0).equals(getCoordinateN(getNumberOfCoordinates() - 1));
  }

  @Override
  public Iterable<ICoordinateSequenceSegment> getCoordinateSequenceSegments() {
    return Collections.unmodifiableCollection(this.segments);
  }

  @Override
  public IEnvelope getEnvelope() {
    return this.envelope;
  }

  @Override
  public boolean isEmpty() {
    return this.isEmpty;
  }

  @Override
  public boolean isCompouned() {
    return true;
  }
}
