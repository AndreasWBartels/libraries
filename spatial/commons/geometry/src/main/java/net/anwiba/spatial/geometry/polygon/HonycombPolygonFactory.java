/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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

import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.internal.GeometryFactory;
import net.anwiba.spatial.geometry.utilities.HoneycombUtilities;

public class HonycombPolygonFactory {

  private static IGeometryFactory geometryFactory;

  public HonycombPolygonFactory(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    geometryFactory = new GeometryFactory(coordinateReferenceSystem);
  }

  public IPolygon create(final double x, final double y, final double radius, final double buffer) {
    return create(HoneycombUtilities.createKey(x, y, radius), radius, buffer);
  }

  public IPolygon create(final long key, final double radius, final double buffer) {
    double gridWidth = Math.sqrt(3.) * radius;
    double halfWidth = gridWidth / 2.;
    int column = HoneycombUtilities.toColumn(key);
    int row = HoneycombUtilities.toRow(key);
    double centerY = HoneycombUtilities.getCenterY(row, radius);
    double centerX = HoneycombUtilities.getCenterX(column, row, gridWidth);
    double diagonalOffsetBuffer = buffer / Math.sqrt(2.);
    double leftX = centerX - halfWidth;
    double rightX = centerX + halfWidth;
    double upperMostY = centerY + radius;
    double upperY = centerY + radius / 2.;
    double lowerY = centerY - radius / 2.;
    double lowerMostY = centerY - radius;
    return geometryFactory.createPolygon(new CoordinateSequenceFactory().create(
        new double[] {
            leftX - diagonalOffsetBuffer,
            centerX,
            rightX + diagonalOffsetBuffer,
            rightX + diagonalOffsetBuffer,
            centerX,
            leftX - diagonalOffsetBuffer,
            leftX - diagonalOffsetBuffer
        },
        new double[] {
            upperY + diagonalOffsetBuffer,
            upperMostY + buffer,
            upperY + diagonalOffsetBuffer,
            lowerY - diagonalOffsetBuffer,
            lowerMostY - buffer,
            lowerY - diagonalOffsetBuffer,
            upperY + diagonalOffsetBuffer
        }));
  }
}
