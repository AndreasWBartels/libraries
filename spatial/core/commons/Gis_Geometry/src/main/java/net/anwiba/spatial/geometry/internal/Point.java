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
 
package net.anwiba.spatial.geometry.internal;

import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.IPoint;

public class Point extends AbstractGeometry implements IPoint {

  private static final long serialVersionUID = -3618728196988091583L;
  private final ICoordinate coordinate;

  public Point(final ICoordinateReferenceSystem coordinateReferenceSystem, final ICoordinate coordinate) {
    this(coordinateReferenceSystem, coordinate, new CoordinateSequenceFactory().create(coordinate).getEnvelope());
  }

  public Point(
      final ICoordinateReferenceSystem coordinateReferenceSystem,
      final ICoordinate coordinate,
      final IEnvelope envelope) {
    super(coordinateReferenceSystem, 0, envelope);
    this.coordinate = coordinate;
  }

  @Override
  public int getCoordinateDimension() {
    return this.coordinate.getDimension();
  }

  @Override
  public ICoordinateSequence getCoordinateSequence() {
    return new CoordinateSequenceFactory().create(this.coordinate);
  }

  @Override
  public GeometryType getGeometryType() {
    return GeometryType.POINT;
  }

  @Override
  public ICoordinate getCoordinate() {
    return this.coordinate;
  }
}