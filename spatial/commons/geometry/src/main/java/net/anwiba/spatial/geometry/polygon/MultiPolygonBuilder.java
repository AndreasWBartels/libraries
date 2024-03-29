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
// Copyright (c) 2013 by Andreas W. Bartels 
package net.anwiba.spatial.geometry.polygon;

import java.util.List;

import net.anwiba.spatial.geometry.IGeometryFactoryProvider;
import net.anwiba.spatial.geometry.IMultiPolygon;
import net.anwiba.spatial.geometry.IPolygon;

public class MultiPolygonBuilder extends AbstractPolygonBuilder {

  private final IGeometryFactoryProvider factoryProvider;

  public MultiPolygonBuilder(final IGeometryFactoryProvider factoryProvider) {
    super(factoryProvider);
    this.factoryProvider = factoryProvider;
  }

  @Override
  public IMultiPolygon build() {
    final List<IPolygon> polygons = getPolygons();
    if (polygons.isEmpty()) {
      return this.factoryProvider.getGeometryFactory(null).createMultiPolygon(
          polygons.toArray(new IPolygon[polygons.size()]));
    }
    return this.factoryProvider.getGeometryFactory(polygons.get(0).getCoordinateReferenceSystem()).createMultiPolygon(
        polygons.toArray(new IPolygon[polygons.size()]));
  }
}
