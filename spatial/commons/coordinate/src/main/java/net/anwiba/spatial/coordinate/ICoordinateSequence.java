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
package net.anwiba.spatial.coordinate;

import java.io.Serializable;

public interface ICoordinateSequence extends Serializable {

  public double[] getXValues();

  public double[] getYValues();

  public double[] getZValues();

  public double[] getMeasuredValues();

  public double getXValue(final int index);

  public double getYValue(final int index);

  public double getZValue(final int index);

  public double getMeasuredValue(final int index);

  public int getDimension();

  public boolean isMeasured();

  public ICoordinate getCoordinateN(final int index);

  public int getNumberOfCoordinates();

  public Iterable<ICoordinate> getCoordinates();

  public Iterable<ICoordinateSequenceSegment> getCoordinateSequenceSegments();

  public boolean isCompouned();

  public boolean isClosed();

  IEnvelope getEnvelope();

  double[][] getValues();

  public boolean isEmpty();

}
