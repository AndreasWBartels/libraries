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
 
package net.anwiba.spatial.geometry.ring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.IGeometryFactoryProvider;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.ILinearRing;

public class LinearRingBuilder implements ILinearRingBuilder {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(LinearRingBuilder.class.getName());
  final List<ILineString> lineStrings = new ArrayList<>();
  private final IGeometryFactoryProvider geometryFactoryProvider;
  private ICoordinateReferenceSystem coordinateReferenceSystem;
  private double tolerance = -1;

  public LinearRingBuilder(final IGeometryFactoryProvider geometryFactoryProvider) {
    this.geometryFactoryProvider = geometryFactoryProvider;
  }

  @Override
  public ILinearRingBuilder setTolerance(final double tolerance) {
    this.tolerance = tolerance;
    return this;
  }

  @Override
  public ILinearRingBuilder add(final ILineString lineString) {
    if (this.coordinateReferenceSystem == null) {
      this.coordinateReferenceSystem = lineString.getCoordinateReferenceSystem();
    } else if (!this.coordinateReferenceSystem.equals(lineString.getCoordinateReferenceSystem())) {
      throw new IllegalArgumentException();
    }
    this.lineStrings.add(lineString);
    return this;
  }

  @Override
  public List<ILinearRing> build() {
    final IGeometryFactory geometryFactory = this.geometryFactoryProvider
        .getGeometryFactory(this.coordinateReferenceSystem);
    final ArrayList<ILinearRing> result = new ArrayList<>();
    final ISequences sequences = new Sequences();
    for (final ILineString lineString : this.lineStrings) {
      if (lineString instanceof ILinearRing) {
        result.add((ILinearRing) lineString);
        continue;
      }
      final ICoordinateSequence coordinateSequence = lineString.getCoordinateSequence();
      if (coordinateSequence.isClosed()) {
        result.add(geometryFactory.createLinearRing(coordinateSequence));
        continue;
      }
      if (!sequences.touches(coordinateSequence)) {
        sequences.add(coordinateSequence);
        continue;
      }
      final List<ICoordinateSequence> touchedSequences = sequences.touched(coordinateSequence);
      ICoordinateSequence dummy = coordinateSequence;
      for (final ICoordinateSequence touchedSequence : touchedSequences) {
        final ICoordinateSequence tmp = concat(dummy, touchedSequence);
        if (tmp == null) {
          continue;
        }
        dummy = CoordinateSequenceUtilities.clean(tmp, this.tolerance);
        sequences.remove(touchedSequence);
      }
      if (dummy.isClosed()) {
        result.add(geometryFactory.createLinearRing(dummy));
        continue;
      }
      sequences.add(dummy);
    }
    if (sequences.isEmpty()) {
      return result;
    }
    if (sequences.size() == 1) {
      final ICoordinateSequence coordinateSequence = sequences.get(0);
      final ICoordinateSequence closed = CoordinateSequenceUtilities.concat(
          coordinateSequence,
          coordinateSequence.getCoordinateN(0));
      result.add(geometryFactory.createLinearRing(closed));
    }
    return result;
  }

  private ICoordinateSequence concat(final ICoordinateSequence sequence, final ICoordinateSequence other) {
    if (sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1).equals(other.getCoordinateN(0))) {
      return CoordinateSequenceUtilities.concat(sequence, other);
    }
    if (other.getCoordinateN(other.getNumberOfCoordinates() - 1).equals(sequence.getCoordinateN(0))) {
      return CoordinateSequenceUtilities.concat(other, sequence);
    }
    if (sequence.getCoordinateN(0).equals(other.getCoordinateN(0))) {
      return CoordinateSequenceUtilities.concat(CoordinateSequenceUtilities.reverse(sequence), other);
    }
    if (sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1).equals(
        other.getCoordinateN(other.getNumberOfCoordinates() - 1))) {
      return CoordinateSequenceUtilities.concat(sequence, CoordinateSequenceUtilities.reverse(other));
    }
    return null;
  }

  public interface ISequences {

    void add(ICoordinateSequence sequence);

    ICoordinate nearestNeighbor(ICoordinate coordinate);

    ICoordinateSequence get(int index);

    int size();

    boolean isEmpty();

    void remove(ICoordinateSequence sequence);

    boolean contains(ICoordinate coordinate);

    ICoordinateSequence get(ICoordinate coordinate);

    boolean touches(ICoordinateSequence sequence);

    List<ICoordinateSequence> touched(ICoordinateSequence sequence);

  }

  public static class Sequences implements ISequences {

    final List<ICoordinateSequence> sequences = new ArrayList<>();
    final Map<ICoordinate, ICoordinateSequence> sequencesByFirstCoordinate = new HashMap<>();
    final Map<ICoordinate, ICoordinateSequence> sequencesByLastCoordinate = new HashMap<>();

    @Override
    public void add(final ICoordinateSequence sequence) {
      logger.log(ILevel.DEBUG, "add sequence " //$NON-NLS-1$
          + sequence.getCoordinateN(0)
          + " " //$NON-NLS-1$
          + sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1)
          + " " //$NON-NLS-1$
          + sequence.getNumberOfCoordinates());
      this.sequences.add(sequence);
      this.sequencesByFirstCoordinate.put(sequence.getCoordinateN(0), sequence);
      this.sequencesByLastCoordinate.put(sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1), sequence);
    }

    @Override
    public void remove(final ICoordinateSequence sequence) {
      logger.log(ILevel.DEBUG, "remove sequence " //$NON-NLS-1$
          + sequence.getCoordinateN(0)
          + " " //$NON-NLS-1$
          + sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1)
          + " " //$NON-NLS-1$
          + sequence.getNumberOfCoordinates());
      this.sequences.remove(sequence);
      this.sequencesByFirstCoordinate.remove(sequence.getCoordinateN(0));
      this.sequencesByLastCoordinate.remove(sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1));
    }

    @Override
    public boolean touches(final ICoordinateSequence sequence) {
      if (isEmpty()) {
        return false;
      }
      return contains(sequence.getCoordinateN(0))
          || contains(sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1));
    }

    @Override
    public boolean isEmpty() {
      if (this.sequencesByFirstCoordinate.isEmpty() != this.sequencesByLastCoordinate.isEmpty()) {
        throw new IllegalStateException();
      }
      return this.sequencesByFirstCoordinate.isEmpty() && this.sequencesByLastCoordinate.isEmpty();
    }

    @Override
    public int size() {
      if (this.sequencesByFirstCoordinate.size() != this.sequencesByLastCoordinate.size()) {
        throw new IllegalStateException();
      }
      return this.sequencesByFirstCoordinate.size();
    }

    @Override
    public List<ICoordinateSequence> touched(final ICoordinateSequence sequence) {
      final Set<ICoordinateSequence> result = new HashSet<>();
      final ICoordinate firstCoordinate = sequence.getCoordinateN(0);
      final ICoordinate lastCoordinate = sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1);
      if (contains(firstCoordinate)) {
        result.add(get(firstCoordinate));
      }
      if (contains(lastCoordinate)) {
        result.add(get(lastCoordinate));
      }
      return new ArrayList<>(result);
    }

    @Override
    public boolean contains(final ICoordinate coordinate) {
      return this.sequencesByFirstCoordinate.containsKey(coordinate)
          || this.sequencesByLastCoordinate.containsKey(coordinate);
    }

    @Override
    public ICoordinate nearestNeighbor(final ICoordinate coordinate) {
      ICoordinate nearest = null;
      for (final ICoordinate other : this.sequencesByFirstCoordinate.keySet()) {
        if (nearest == null) {
          nearest = other;
          continue;
        }
        if (CoordinateUtilities.calculateDistance(coordinate, other) > CoordinateUtilities.calculateDistance(
            coordinate,
            nearest)) {
          continue;
        }
        nearest = other;
      }
      for (final ICoordinate other : this.sequencesByLastCoordinate.keySet()) {
        if (nearest == null) {
          nearest = other;
          continue;
        }
        if (CoordinateUtilities.calculateDistance(coordinate, other) > CoordinateUtilities.calculateDistance(
            coordinate,
            nearest)) {
          continue;
        }
        nearest = other;
      }
      return nearest;
    }

    @Override
    public ICoordinateSequence get(final ICoordinate coordinate) {
      if (this.sequencesByFirstCoordinate.containsKey(coordinate)) {
        return this.sequencesByFirstCoordinate.get(coordinate);
      }
      if (this.sequencesByLastCoordinate.containsKey(coordinate)) {
        return this.sequencesByLastCoordinate.get(coordinate);
      }
      throw new IllegalArgumentException();
    }

    @Override
    public ICoordinateSequence get(final int index) {
      return this.sequences.get(index);
    }

  }

}
