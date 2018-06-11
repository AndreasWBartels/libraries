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
package net.anwiba.spatial.geometry.operator;

import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.geometry.ILineSegment;
import net.anwiba.spatial.geometry.utilities.LineSegmentIterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LineSegmentIntersectOperator {

  private final ILineSegment segment;

  public LineSegmentIntersectOperator(final ILineSegment segment) {
    this.segment = segment;
  }

  public ICoordinateSequence calculate(final ICoordinateSequence... sequences) {
    final List<ICoordinate> coordinates = new ArrayList<>();
    for (final ICoordinateSequence sequence : sequences) {
      for (final ILineSegment lineSegment : new LineSegmentIterable(sequence)) {
        final ICoordinate coordinate = calculate(lineSegment);
        if (coordinate != null) {
          coordinates.add(coordinate);
        }
      }
    }
    final ICoordinate[] array = coordinates.toArray(new ICoordinate[coordinates.size()]);
    final ICoordinate c0 = this.segment.getStartPoint();
    Arrays.sort(array, new Comparator<ICoordinate>() {

      @Override
      public int compare(final ICoordinate c1, final ICoordinate c2) {
        return Double.compare(
            CoordinateUtilities.calculateDistance(c0, c1),
            CoordinateUtilities.calculateDistance(c0, c2));
      }
    });
    return new CoordinateSequenceFactory().create(array);
  }

  private ICoordinate calculate(final ILineSegment other) {
    try {
      final ICoordinate coordinate =
          CoordinateUtilities.calculateIntersection(
              this.segment.getStartPoint(),
              this.segment.getEndPoint(),
              other.getStartPoint(),
              other.getEndPoint());
      return CoordinateUtilities.isBetween(this.segment.getStartPoint(), this.segment.getEndPoint(), coordinate, 0)
          ? coordinate
          : null;
    } catch (final Exception exception) {
      return null;
    }
  }
}