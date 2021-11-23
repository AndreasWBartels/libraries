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
package net.anwiba.spatial.geometry.polygon;

import java.util.List;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.geometry.ILineSegment;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.operator.IEnvelopeConverter;
import net.anwiba.spatial.geometry.operator.LineIntersector;
import net.anwiba.spatial.geometry.operator.LineSegmentIterable;
import net.anwiba.spatial.geometry.operator.Rtree;

public class ContainsLinearRingOperator {

  private Rtree<ILineSegment> tree;
  private final double minX;
  private final double maxX;
  private final double horizontalAuxiliaryXValue;
  private final ILinearRing baseRing;

  public ContainsLinearRingOperator(final ILinearRing ring) {
    this.baseRing = ring;
    final IEnvelope envelope = ring.getEnvelope();
    this.minX = envelope.getMinimum().getXValue();
    this.maxX = envelope.getMaximum().getXValue();
    this.horizontalAuxiliaryXValue = this.maxX + 10;
  }

  private Rtree<ILineSegment> createTree(final ILinearRing ring) {
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
    final Iterable<ILineSegment> lineSegments = new LineSegmentIterable(ring.getCoordinateSequence());
    for (final ILineSegment lineSegment : lineSegments) {
      rTree.insert(lineSegment);
    }
    rTree.build();
    return rTree;
  }

  public boolean contains(final ILinearRing ring) {
    return contains(ring, true);
  }

  public boolean contains(final ILineString ring, final boolean borderTouch) {
    for (int i = 0; i < ring.getNumberOfCoordinates(); ++i) {
      final ICoordinate coordinate = ring.getCoordinateN(i);
      if (!contains(coordinate, borderTouch)) {
        return false;
      }
    }
    return true;
  }

  public boolean containsOrCross(final ILineString ring) {
    for (int i = 0; i < ring.getNumberOfCoordinates() - 1; ++i) {
      final ICoordinate startPoint = ring.getCoordinateN(i);
      final ICoordinate endPoint = ring.getCoordinateN(i + 1);
      if (contains(startPoint, true)) {
        return true;
      }
      if (cross(startPoint, endPoint)) {
        return true;
      }
    }
    return false;
  }

  public boolean contains(final ICoordinate coordinate, final boolean touchEnabled) {
    if (this.minX > coordinate.getXValue() || coordinate.getXValue() > this.maxX) {
      //      logger.log(ILevel.DEBUG, "outside bounding box");
      return false;
    }
    final ICoordinate otherCoordinate = createHorizontalAuxiliaryCoordinate(coordinate);
    final List<ILineSegment> lineSegments = getTree().query(coordinate.getYValue());
    int intersectionCounter = 0;
    for (final ILineSegment lineSegment : lineSegments) {
      final LineIntersector lineIntersector = new LineIntersector();
      lineIntersector
          .computeIntersection(lineSegment.getStartPoint(), lineSegment.getEndPoint(), coordinate, otherCoordinate);
      if (!lineIntersector.hasIntersection()) {
        continue;
      }
      if (lineIntersector.isIntersection(coordinate)) {
        return touchEnabled;
      }
      if (lineIntersector.isIntersection(
          lineSegment.getStartPoint().getYValue() < lineSegment.getEndPoint().getYValue()
              ? lineSegment.getStartPoint()
              : lineSegment.getEndPoint())) {
        continue;
      }
      intersectionCounter += 1;
    }
    return intersectionCounter % 2 == 1;
  }

  protected boolean cross(final ICoordinate startPoint, final ICoordinate endPoint) {
    final List<ILineSegment> lineSegments = getTree().query(startPoint.getYValue(), endPoint.getYValue());
    for (final ILineSegment lineSegment : lineSegments) {
      final LineIntersector lineIntersector = new LineIntersector();
      lineIntersector.computeIntersection(lineSegment.getStartPoint(), lineSegment.getEndPoint(), startPoint, endPoint);
      if (lineIntersector.hasIntersection()) {
        return true;
      }
    }
    return false;
  }

  private ICoordinate createHorizontalAuxiliaryCoordinate(final ICoordinate coordinate) {
    return new Coordinate(this.horizontalAuxiliaryXValue, coordinate.getYValue());
  }

  public Rtree<ILineSegment> getTree() {
    if (this.tree == null) {
      this.tree = createTree(this.baseRing);
    }
    return this.tree;
  }

}
