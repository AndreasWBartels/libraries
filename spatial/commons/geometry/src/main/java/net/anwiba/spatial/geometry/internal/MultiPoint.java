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
package net.anwiba.spatial.geometry.internal;

import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.IMultiPoint;
import net.anwiba.spatial.geometry.IPoint;

public class MultiPoint extends AbstractGeometryCollection<IPoint> implements IMultiPoint {

  private static final long serialVersionUID = 5249723861266950172L;

  MultiPoint(final ICoordinateReferenceSystem coordinateReferenceSystem, final IPoint[] points) {
    super(coordinateReferenceSystem, points);
  }

  @Override
  public GeometryType getGeometryType() {
    return GeometryType.MULTIPOINT;
  }
}
