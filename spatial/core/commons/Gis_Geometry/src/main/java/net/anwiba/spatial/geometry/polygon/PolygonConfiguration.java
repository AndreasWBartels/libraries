/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.spatial.geometry.polygon;

import net.anwiba.spatial.coordinate.Orientation;

public class PolygonConfiguration implements IPolygonConfiguration {

  private final Orientation exteriorOrientation;
  private final Orientation interiorOrientation;

  public PolygonConfiguration(final Orientation exteriorOrientation, final Orientation interiorOrientation) {
    super();
    this.exteriorOrientation = exteriorOrientation;
    this.interiorOrientation = interiorOrientation;
  }

  @Override
  public Orientation getExteriorOrientation() {
    return this.exteriorOrientation;
  }

  @Override
  public Orientation getInteriorOrientation() {
    return this.interiorOrientation;
  }

}
