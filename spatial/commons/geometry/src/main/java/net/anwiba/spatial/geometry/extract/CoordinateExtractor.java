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
// Copyright (c) 2010 by Andreas W. Bartels
package net.anwiba.spatial.geometry.extract;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.utilities.GeometryUtilities;

public class CoordinateExtractor implements ICoordinateExtractor {

  private final IGeometry geometry;

  public CoordinateExtractor(final IGeometry geometry) {
    this.geometry = geometry;
  }

  @Override
  public ICoordinate extract(final ICoordinateReference reference) {
    final IPath path = reference.getPath();
    if (!path.hasStep()) {
      return null;
    }
    return extract(this.geometry, path.getStep());
  }

  private ICoordinate extract(@SuppressWarnings("hiding") final IGeometry geometry, final IStep step) {
    if (step.hasNext()) {
      extract(GeometryUtilities.extract(geometry, step.getIndex()), step.next());
    }
    return geometry.getCoordinateN(step.getIndex());
  }
}
