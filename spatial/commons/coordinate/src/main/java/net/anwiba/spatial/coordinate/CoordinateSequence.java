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

import java.util.Arrays;

public class CoordinateSequence implements ICoordinateSequence {

  private static final long serialVersionUID = 1L;
  private final ICoordinateSequenceSegment segment;

  CoordinateSequence(final ICoordinateSequenceSegment coordinateSequenceSegment) {
    this.segment = coordinateSequenceSegment;
  }

  @Override
  public double getXValue(final int index) {
    return this.segment.getXValue(index);
  }

  @Override
  public double getYValue(final int index) {
    return this.segment.getYValue(index);
  }

  @Override
  public double getZValue(final int index) {
    return this.segment.getZValue(index);
  }

  @Override
  public double[] getXValues() {
    return this.segment.getXValues();
  }

  @Override
  public double[] getYValues() {
    return this.segment.getYValues();
  }

  @Override
  public double[] getZValues() {
    return this.segment.getZValues();
  }

  @Override
  public int getDimension() {
    return this.segment.getDimension();
  }

  @Override
  public boolean isMeasured() {
    return this.segment.isMeasured();
  }

  @Override
  public double getMeasuredValue(final int index) {
    return this.segment.getMeasuredValue(index);
  }

  @Override
  public double[] getMeasuredValues() {
    return this.segment.getMeasuredValues();
  }

  @Override
  public ICoordinate getCoordinateN(final int index) {
    return this.segment.getCoordinateN(index);
  }

  @Override
  public int getNumberOfCoordinates() {
    return this.segment.getNumberOfCoordinates();
  }

  @Override
  public double[][] getValues() {
    return this.segment.getValues();
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
    return this.segment.getCoordinates();
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
    return Arrays.asList(new ICoordinateSequenceSegment[]{ this.segment });
  }

  @Override
  public IEnvelope getEnvelope() {
    return this.segment.getEnvelope();
  }

  @Override
  public boolean isEmpty() {
    return this.segment.isEmpty();
  }

  @Override
  public boolean isCompouned() {
    return false;
  }
}
