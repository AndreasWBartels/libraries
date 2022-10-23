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
import net.anwiba.spatial.geometry.utilities.HexagonGridUtilities;

public class HexagonFactory {

  private static IGeometryFactory geometryFactory;

  public HexagonFactory(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    geometryFactory = new GeometryFactory(coordinateReferenceSystem);
  }

  public IPolygon createAt(final double x, final double y, final double radius) {
    return createAt(x, y, radius, 0);
  }

  public IPolygon createAt(final double x, final double y, final double radius, final double buffer) {
    return create(x, y, radius, HexagonGridUtilities.gridWith(radius), buffer);
  }

  public IPolygon createAtGrid(final double x, final double y, final double radius) {
    return createAtGrid(HexagonGridUtilities.createKey(x, y, radius), radius, 0.);
  }

  public IPolygon createAtGrid(final double x, final double y, final double radius, final double buffer) {
    return createAtGrid(HexagonGridUtilities.createKey(x, y, radius), radius, buffer);
  }

  public IPolygon createAtGrid(final long key, final double radius) {
    return createAtGrid(key, radius, 0.);
  }

  public IPolygon createAtGrid(final long key, final double radius, final double buffer) {
    final int column = HexagonGridUtilities.toColumn(key);
    final int row = HexagonGridUtilities.toRow(key);
    final double centerY = HexagonGridUtilities.getCenterY(row, radius);
    final double gridWidth = HexagonGridUtilities.gridWith(radius);
    final double centerX = HexagonGridUtilities.getCenterX(column, row, gridWidth);
    return create(centerX, centerY, radius, gridWidth, buffer);
  }

  private IPolygon create(final double centerX,
      final double centerY,
      final double radius,
      final double gridWidth,
      final double buffer) {
    final double diagonalOffsetBuffer = buffer / Math.sqrt(2.);
    final double halfWidth = gridWidth / 2.;
    final double leftX = centerX - halfWidth;
    final double rightX = centerX + halfWidth;
    final double upperMostY = centerY + radius;
    final double upperY = centerY + radius / 2.;
    final double lowerY = centerY - radius / 2.;
    final double lowerMostY = centerY - radius;
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

