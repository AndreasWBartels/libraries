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
// Copyright (c) 2016 by Andreas W. Bartels

package net.anwiba.spatial.geometry.extract;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.ILineSegment;
import net.anwiba.spatial.geometry.operator.IEnvelopeConverter;
import net.anwiba.spatial.geometry.operator.Rtree;
import net.anwiba.spatial.geometry.utilities.LineSegmentIterable;

public class LineSegmentExtractor {

  private final IGeometry geometry;
  private Rtree<ILineSegment> tree;

  public LineSegmentExtractor(final IGeometry geometry) {
    this.geometry = geometry;
  }

  public List<ILineSegment> find(final ICoordinate coordinate) {
    if (this.tree == null) {
      this.tree = createTree(this.geometry);
    }
    final List<ILineSegment> segments = this.tree.query(coordinate.getYValue());
    final List<ILineSegment> result = new ArrayList<>();
    for (final ILineSegment segment : segments) {
      if (!CoordinateUtilities.isBetween(segment.getStartPoint(), segment.getEndPoint(), coordinate, Double.NaN)) {
        continue;
      }
      result.add(segment);
    }
    return result;
  }

  private Rtree<ILineSegment> createTree(@SuppressWarnings("hiding") final IGeometry geometry) {
    final Rtree<ILineSegment> rTree = new Rtree<>(new IEnvelopeConverter<ILineSegment>() {

      @Override
      public int getDimensions() {
        return 1;
      }

      @Override
      public double getMin(final int axis, final ILineSegment lineSegment) {
        if (axis == 0) {
          return Math.min(lineSegment.getStartPoint().getYValue(), lineSegment.getEndPoint().getYValue());
        }
        throw new IllegalArgumentException();
      }

      @Override
      public double getMax(final int axis, final ILineSegment lineSegment) {
        if (axis == 0) {
          return Math.max(lineSegment.getStartPoint().getYValue(), lineSegment.getEndPoint().getYValue());
        }
        throw new IllegalArgumentException();
      }
    });
    final Iterable<ILineSegment> lineSegments = new LineSegmentIterable(geometry.getCoordinateSequence());
    for (final ILineSegment lineSegment : lineSegments) {
      rTree.insert(lineSegment);
    }
    rTree.build();
    return rTree;
  }

}
