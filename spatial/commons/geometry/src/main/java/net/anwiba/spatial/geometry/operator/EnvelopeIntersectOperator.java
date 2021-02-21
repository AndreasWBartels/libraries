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
// Copyright (c) 2007 by Andreas W. Bartels 
package net.anwiba.spatial.geometry.operator;

import net.anwiba.spatial.coordinate.CoordinateCalculationException;
import net.anwiba.spatial.coordinate.CoordinateSequence;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.IGeometryTypeVisitor;
import net.anwiba.spatial.geometry.IMultiLineString;
import net.anwiba.spatial.geometry.utilities.GeometryUtilities;

import java.util.ArrayList;
import java.util.List;

public class EnvelopeIntersectOperator {

  final IEnvelope envelope;

  public EnvelopeIntersectOperator(final IEnvelope envelope) {
    this.envelope = envelope;
  }

  public IGeometry intersect(final IGeometry geometry) {
    if (this.envelope.contains(geometry.getEnvelope())) {
      return geometry;
    }
    if (!this.envelope.interact(geometry.getEnvelope())) {
      return null;
    }
    final IGeometryFactory geometryFactory =
        GeometryUtilities.getGeometryFactory(geometry.getCoordinateReferenceSystem());
    final IGeometryTypeVisitor<IGeometry, RuntimeException> visitor =
        new IGeometryTypeVisitor<IGeometry, RuntimeException>() {

          @Override
          public IGeometry visitUnknown() throws RuntimeException {
            throw new UnsupportedOperationException();
          }

          @Override
          public IGeometry visitPolygon() throws RuntimeException {
            // TODO NOW (20071219) andreas: implement geometry windowing algorithm
            throw new UnsupportedOperationException();
          }

          @Override
          public IGeometry visitPoint() throws RuntimeException {
            if (EnvelopeIntersectOperator.this.envelope.interact(geometry.getCoordinateN(0))) {
              return geometry;
            }
            return null;
          }

          @Override
          public IGeometry visitMultiPolygon() throws RuntimeException {
            // TODO NOW (20071219) andreas: implement geometry windowing algorithm
            throw new UnsupportedOperationException();
          }

          @Override
          public IGeometry visitMultiPoint() throws RuntimeException {
            final ICoordinateSequence sequence = getInteractingCoordinateSequence(geometry.getCoordinateSequence());
            if (sequence.getNumberOfCoordinates() == 0) {
              return null;
            }
            return GeometryUtilities.getGeometryFactory(geometry.getCoordinateReferenceSystem()).createMultiPoint(
                sequence);
          }

          @Override
          public IGeometry visitMultiLineString() throws RuntimeException {
            final IMultiLineString multiLineString = (IMultiLineString) geometry;
            final List<ICoordinateSequence> sequences = new ArrayList<>();
            for (int i = 0; i < multiLineString.getNumberOfGeometries(); i++) {
              final IGeometry part = multiLineString.getGeometryN(i);
              final ICoordinateSequence sequence = getInteractingCoordinateSequence(part.getCoordinateSequence());
              if (sequence.getNumberOfCoordinates() > 1) {
                sequences.add(sequence);
              }
            }
            if (sequences.isEmpty()) {
              return null;
            }
            return geometryFactory.createMultiLineString(sequences.toArray(new CoordinateSequence[sequences.size()]));
          }

          @Override
          public IGeometry visitLinearRing() throws RuntimeException {
            final ICoordinateSequence sequence =
                getInteractingSegmentCoordinateSequence(geometry.getCoordinateSequence());
            if (sequence.getNumberOfCoordinates() == 0) {
              return null;
            }
            if (sequence.getNumberOfCoordinates() > 2 && sequence.isClosed()) {
              return geometryFactory.createLinearRing(sequence);
            }
            return geometryFactory.createLineString(sequence);
          }

          @Override
          public IGeometry visitLineString() throws RuntimeException {
            final ICoordinateSequence sequence =
                getInteractingSegmentCoordinateSequence(geometry.getCoordinateSequence());
            if (sequence.getNumberOfCoordinates() == 0) {
              return null;
            }
            if (sequence.getNumberOfCoordinates() == 1) {
              return null;
            }
            return geometryFactory.createLineString(sequence);
          }

          @Override
          public IGeometry visitCollection() throws RuntimeException {
            throw new UnsupportedOperationException();
          }
        };
    return geometry.getGeometryType().accept(visitor);
  }

  protected ICoordinateSequence getInteractingSegmentCoordinateSequence(final ICoordinateSequence sequence) {
    final List<ICoordinate> coordinates = new ArrayList<>();
    ICoordinate prior = null;
    boolean isPriorInteracting = false;
    for (final ICoordinate next : sequence.getCoordinates()) {
      final boolean isNextInteracting = this.envelope.interact(next);
      if (prior == null) {
        if (isNextInteracting) {
          coordinates.add(next);
        }
        isPriorInteracting = isNextInteracting;
        prior = next;
        continue;
      }
      if (prior.touch(next)) {
        prior = next;
        continue;
      }
      if (!isNextInteracting || !isPriorInteracting) {
        coordinates.addAll(getCrossingCoordinats(prior, next));
      }
      if (isNextInteracting && !coordinates.contains(next)) {
        coordinates.add(next);
      }
      isPriorInteracting = isNextInteracting;
      prior = next;
    }
    return new CoordinateSequenceFactory().create(coordinates);
  }

  private List<ICoordinate> getCrossingCoordinats(final ICoordinate c0, final ICoordinate c1) {
    final List<ICoordinate> coordinates = new ArrayList<>();
    ICoordinate prior = null;
    for (final ICoordinate next : this.envelope.getCoordinateSequence().getCoordinates()) {
      if (prior == null) {
        prior = next;
        continue;
      }
      try {
        final ICoordinate coordinate = CoordinateUtilities.calculateIntersection(c0, c1, prior, next);
        if (CoordinateUtilities.isInterior(prior, next, coordinate)) {
          coordinates.add(coordinate);
        }
      } catch (final CoordinateCalculationException exception) {
        // nothing to do
      }
      prior = next;
    }
    return coordinates;
  }

  protected ICoordinateSequence getInteractingCoordinateSequence(final ICoordinateSequence sequence) {
    final List<ICoordinate> coordinates = new ArrayList<>();
    for (final ICoordinate coordinate : sequence.getCoordinates()) {
      if (this.envelope.interact(coordinate)) {
        coordinates.add(coordinate);
      }
    }
    return new CoordinateSequenceFactory().create(coordinates);
  }
}
