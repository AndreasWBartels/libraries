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
package net.anwiba.spatial.geometry.operator;

import java.util.List;

import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinate.ILineSegment;
import net.anwiba.spatial.coordinate.LineSegmentIterable;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.ILineString;

public class LineStringInteractOperator {

  private Rtree<ILineSegment> tree;
  private final ILineString baseLineString;
  private final EnvelopeInteractOperator envelopeInteractOperator;

  public LineStringInteractOperator(final ILineString lineString) {
    this.baseLineString = lineString;
    final IEnvelope envelope = lineString.getEnvelope();
    this.envelopeInteractOperator = new EnvelopeInteractOperator(envelope);
  }

  private Rtree<ILineSegment> createTree(final ILineString lineString) {
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
    final Iterable<ILineSegment> lineSegments = new LineSegmentIterable(lineString.getCoordinateSequence());
    for (final ILineSegment lineSegment : lineSegments) {
      rTree.insert(lineSegment);
    }
    rTree.build();
    return rTree;
  }

  public boolean contains(final IGeometry geometry) {
    for (int i = 0; i < geometry.getNumberOfCoordinates(); ++i) {
      final ICoordinate coordinate = geometry.getCoordinateN(i);
      if (!touch(coordinate)) {
        return false;
      }
    }
    return true;
  }

  public boolean touch(final ICoordinate coordinate) {
    if (!this.envelopeInteractOperator.interact(coordinate)) {
      return false;
    }
    final List<ILineSegment> lineSegments = getTree().query(coordinate.getYValue());
    for (final ILineSegment lineSegment : lineSegments) {
      if (CoordinateUtilities.isBetween(lineSegment.getStartPoint(), lineSegment.getEndPoint(), coordinate, Double.NaN)) {
        return true;
      }
    }
    return false;
  }

  public Rtree<ILineSegment> getTree() {
    if (this.tree == null) {
      this.tree = createTree(this.baseLineString);
    }
    return this.tree;
  }

}
