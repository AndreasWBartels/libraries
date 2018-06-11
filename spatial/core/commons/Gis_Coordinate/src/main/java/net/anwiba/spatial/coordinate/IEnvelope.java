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

import java.io.Serializable;

public interface IEnvelope extends Serializable {

  public boolean isMeasured();

  public double getX();

  public double getY();

  public double getWidth();

  public double getHeight();

  public ICoordinate getMaximum();

  public ICoordinate getMinimum();

  public int getDimension();

  public ICoordinate getCenterCoordinate();

  public boolean interact(final ICoordinate coordinate);

  public boolean interact(final IEnvelope other);

  public boolean contains(final IEnvelope other);

  public boolean cross(final ICoordinate c0, final ICoordinate c1);

  public ICoordinateSequence getCoordinateSequence();

  public ICoordinateSequence getCoordinateSequence(int steps);

  public IEnvelope concat(IEnvelope other);

  public IEnvelope intersection(IEnvelope envelope);

}