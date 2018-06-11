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

import java.util.ArrayList;
import java.util.List;

import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.IBaseGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.calculator.GeometryCalculator;

public abstract class AbstractGeometryCollection<T extends IBaseGeometry> extends AbstractGeometry
    implements
    IGeometryCollection {

  private static final long serialVersionUID = 1498634025534697222L;
  private final List<T> geometries = new ArrayList<>();

  AbstractGeometryCollection(final ICoordinateReferenceSystem coordinateReferenceSystem, final T[] geometries) {
    super(coordinateReferenceSystem, 2, GeometryCalculator.createEnvelope(geometries));
    for (final T geometry : geometries) {
      this.geometries.add(geometry);
    }
  }

  @Override
  public int getCoordinateDimension() {
    if (this.geometries.size() == 0) {
      return 2;
    }
    return this.geometries.get(0).getCoordinateDimension();
  }

  @Override
  public boolean isMeasured() {
    for (final T geometry : this.geometries) {
      if (geometry.isMeasured()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ICoordinateSequence getCoordinateSequence() {
    ICoordinateSequence sequence = null;
    for (final T geometry : this.geometries) {
      sequence = CoordinateSequenceUtilities.concat(sequence, geometry.getCoordinateSequence());
    }
    return sequence;
  }

  @Override
  public T getGeometryN(final int index) {
    return this.geometries.get(index);
  }

  @Override
  public Iterable<T> geometries() {
    return this.geometries;
  }

  @Override
  public int getNumberOfGeometries() {
    return this.geometries.size();
  }
}