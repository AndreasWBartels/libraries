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
package net.anwiba.spatial.coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.spatial.coordinate.calculator.DefaultCoordinateDistanceCalculator;
import net.anwiba.spatial.coordinate.calculator.ICoordinateDistanceCalculator;
import net.anwiba.spatial.coordinate.calculator.SmallPointCalculator;

public class CoordinateSequenceUtilities {

  private static final DefaultCoordinateDistanceCalculator DISTANCE_CALCULATOR =
      new DefaultCoordinateDistanceCalculator();
  private static ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  public static ICoordinateSequence concat(final ICoordinateSequence sequence0, final ICoordinateSequence sequence1) {
    if (sequence0 == null) {
      return sequence1;
    }
    if (sequence1 == null) {
      return sequence0;
    }
    final double[][] values0 = sequence0.getValues();
    final double[][] values1 = sequence1.getValues();
    if (values0.length != values1.length) {
      throw new IllegalArgumentException("coordinate sequences with diffrent dimensions"); //$NON-NLS-1$
    }
    if (sequence0.isMeasured() != sequence1.isMeasured()) {
      throw new IllegalArgumentException("only one coordinate sequence is measued"); //$NON-NLS-1$
    }
    if (values0[0].length == 0) {
      return sequence1;
    }
    if (values1[0].length == 0) {
      return sequence0;
    }
    final double[][] result = new double[values0.length][];
    for (int i = 0; i < result.length; i++) {
      result[i] = ArrayUtilities.concat(values0[i], values1[i]);
    }
    return coordinateSequenceFactory.create(result, sequence0.isMeasured());
  }

  public static ICoordinateSequence concat(final ICoordinateSequence[] coordinateSequences) {
    final int sequeneceCount = coordinateSequences.length;
    if (sequeneceCount == 0) {
      throw new IllegalArgumentException("empty coordinate sequence array"); //$NON-NLS-1$
    }
    if (sequeneceCount == 1) {
      return coordinateSequences[0];
    }
    ICoordinateSequence coordinateSequence = coordinateSequenceFactory
        .createEmptyCoordinateSequence(coordinateSequences[0].getDimension(), coordinateSequences[0].isMeasured());
    for (final ICoordinateSequence sequence : coordinateSequences) {
      coordinateSequence = concat(coordinateSequence, sequence);
    }
    return coordinateSequence;
  }

  public static ICoordinateSequence concat(final ICoordinateSequence sequence, final ICoordinate coordinate) {
    return concat(sequence, new CoordinateSequenceFactory().create(coordinate));
  }

  public static ICoordinateSequence copy(final ICoordinateSequence source) {
    return copy(source, 0, source.getNumberOfCoordinates());
  }

  private static ICoordinateSequence copy(
      final ICoordinateSequence source,
      final int from,
      final int to,
      final int length) {
    final double[][] sourceValues = source.getValues();
    final double[][] targetValues = coordinateSequenceFactory
        .create(source.getDimension(), length, source.isMeasured());
    for (int i = 0; i < sourceValues.length; i++) {
      System.arraycopy(sourceValues[i], from, targetValues[i], to, length);
    }
    return coordinateSequenceFactory.create(targetValues, source.isMeasured());
  }

  public static ICoordinateSequence copy(final ICoordinateSequence source, final int from, final int length) {
    return copy(source, from, 0, length);
  }

  public static ICoordinateSequence reverse(final ICoordinateSequence coordinateSequence) {
    final double[][] values = coordinateSequence.getValues();
    final double[][] reverse = new double[values.length][];
    for (int i = 0; i < values.length; i++) {
      reverse[i] = ArrayUtilities.reverse(values[i]);
    }
    return new CoordinateSequenceFactory().create(reverse, coordinateSequence.isMeasured());
  }

  public static ICoordinateSequenceSegment reverse(final ICoordinateSequenceSegment segment) {
    final double[][] values = segment.getValues();
    final double[][] reverse = new double[values.length][];
    for (int i = 0; i < values.length; i++) {
      reverse[i] = ArrayUtilities.reverse(values[i]);
    }
    return new LineCoordinateSequenceSegment(reverse, segment.isMeasured());
  }

  public static ICoordinate calculateCentroid(final ICoordinateSequence coordinateSequence) {
    if (coordinateSequence.getNumberOfCoordinates() == 1) {
      return coordinateSequence.getCoordinateN(0);
    }
    ICoordinate centroid = null;
    for (int i = 0; i < coordinateSequence.getNumberOfCoordinates() - (coordinateSequence.isClosed() ? 1 : 0); i++) {
      final ICoordinate coordinate = coordinateSequence.getCoordinateN(i);
      if (centroid == null) {
        centroid = coordinate;
        continue;
      }
      centroid = CoordinateUtilities.getAvarageCoordinate(centroid, coordinate, i + 1);
    }
    return centroid;
  }

  public static ICoordinate findNearestNeighbor(
      final ICoordinate centroid,
      final ICoordinateSequence coordinateSequence) {
    ICoordinate result = null;
    double distance = Double.MAX_VALUE;
    for (final ICoordinate coordinate : coordinateSequence.getCoordinates()) {
      final double calculatedDistance = Math.abs(CoordinateUtilities.calculateDistance(centroid, coordinate));
      if (calculatedDistance < distance) {
        distance = calculatedDistance;
        result = coordinate;
      }
    }
    return result;
  }

  public static ICoordinateSequence createMinimalBoundingRectangleSequence(final ICoordinateSequence sequence) {
    ICoordinate minimum = null;
    ICoordinate maximum = null;
    for (final ICoordinate coordinate : sequence.getCoordinates()) {
      minimum = CoordinateUtilities.getMinimum(minimum, coordinate);
      maximum = CoordinateUtilities.getMaximum(maximum, coordinate);
    }
    if (minimum == null || maximum == null) {
      return coordinateSequenceFactory.create(new ICoordinate[0]);
    }
    return coordinateSequenceFactory.create(
        new ICoordinate[] {
            minimum,
            CoordinateUtilities.createAdapted(minimum, ICoordinate.Y, maximum.getYValue()),
            maximum,
            CoordinateUtilities.createAdapted(minimum, ICoordinate.X, maximum.getXValue()),
            minimum });
  }

  public static boolean hasEqualNeigbors(final ICoordinateSequence coordinateSequence, final double tolerance) {
    final Iterable<ICoordinate> coordinates = coordinateSequence.getCoordinates();
    ICoordinate ancestor = null;
    for (final ICoordinate coordinate : coordinates) {
      if (ancestor != null && CoordinateUtilities.interact(ancestor, coordinate, tolerance)) {
        return true;
      }
      ancestor = coordinate;
    }
    return false;
  }

  public static ICoordinateSequence clean(final ICoordinateSequence source, final double tolerance) {
    return clean(DISTANCE_CALCULATOR, source, tolerance);
  }

  public static ICoordinateSequence
      clean(final ICoordinateDistanceCalculator calculator, final ICoordinateSequence source, final double tolerance) {
    ICoordinate last = null;
    final List<ICoordinate> result = new ArrayList<>();
    for (final ICoordinate coordinate : source.getCoordinates()) {
      if (last == null) {
        last = coordinate;
        continue;
      }
      if (touches(coordinate, last, tolerance)) {
        continue;
      }
      result.add(last);
      last = coordinate;
    }
    final ICoordinate lastCoordinate = source.getCoordinateN(source.getNumberOfCoordinates() - 1);
    if (lastCoordinate.equals(last)) {
      result.add(last);
    } else {
      result.set(result.size() - 1, last);
    }
    return coordinateSequenceFactory.create(result);
  }

  public static boolean touches(final ICoordinate coordinate, final ICoordinate other, final double tolerance) {
    return (tolerance <= 0 && other.equals(coordinate))
        || (tolerance > 0 && CoordinateUtilities.calculateDistance(other, coordinate) <= tolerance);
  }

  public static class Segment {

    final private ICoordinate from;
    final private ICoordinate to;

    public Segment(final ICoordinate from, final ICoordinate to) {
      super();
      this.from = from;
      this.to = to;
    }

    public ICoordinate getFrom() {
      return this.from;
    }

    public ICoordinate getTo() {
      return this.to;
    }

  }

  @SuppressWarnings("null")
  public static ICoordinateSequence parallel(final ICoordinateSequence sequence, final double distance)
      throws CoordinateCalculationException {

    final Iterable<Segment> segments = createSegmentIterable(sequence);
    Segment firstParallel = null;
    Segment previousParallel = null;

    final List<ICoordinate> coordinates = new ArrayList<>();

    for (final Segment segment : segments) {
      final Segment nextParallel = parallel(segment, distance);
      if (firstParallel == null) {
        firstParallel = nextParallel;
        previousParallel = nextParallel;
        coordinates.add(nextParallel.from);
        continue;
      }

      try {
        coordinates.add(
            CoordinateUtilities
                .calculateIntersection(previousParallel.from, previousParallel.to, nextParallel.to, nextParallel.from));
      } catch (final CoordinateCalculationException exception) {
        coordinates.add(CoordinateUtilities.calculateSmallPoint(previousParallel.to, previousParallel.from, -distance));
        coordinates.add(CoordinateUtilities.calculateSmallPoint(nextParallel.from, nextParallel.to, -distance));
      }
      previousParallel = nextParallel;
    }
    if (previousParallel == null) {
      return new CoordinateSequenceFactory().create(coordinates);
    }
    coordinates.add(previousParallel.to);
    if (sequence.isClosed()) {
      try {
        final ICoordinate intersection = CoordinateUtilities
            .calculateIntersection(previousParallel.from, previousParallel.to, firstParallel.to, firstParallel.from);
        coordinates.set(0, intersection);
        coordinates.set(coordinates.size() - 1, intersection);
      } catch (final CoordinateCalculationException exception) {
        coordinates.add(CoordinateUtilities.calculateSmallPoint(previousParallel.to, previousParallel.from, -distance));
        final ICoordinate point = CoordinateUtilities
            .calculateSmallPoint(firstParallel.from, firstParallel.to, -distance);
        coordinates.set(0, point);
        coordinates.set(coordinates.size() - 1, point);
      }
    }
    return new CoordinateSequenceFactory().create(coordinates);
  }

  private static Segment parallel(final Segment segment, final double distance) {
    final SmallPointCalculator calculator = new SmallPointCalculator(segment.from, segment.to);
    final ICoordinate from = calculator.calculate(0, distance);
    final ICoordinate to = calculator
        .calculate(DISTANCE_CALCULATOR.calculateDistance(segment.from, segment.to), distance);
    return new Segment(from, to);
  }

  private static Iterable<Segment> createSegmentIterable(final ICoordinateSequence sequence) {
    return new Iterable<CoordinateSequenceUtilities.Segment>() {

      @Override
      public Iterator<Segment> iterator() {
        final Iterator<ICoordinate> iterator = sequence.getCoordinates().iterator();
        return new Iterator<CoordinateSequenceUtilities.Segment>() {

          ICoordinate previous = null;
          Segment segment = null;

          @Override
          public boolean hasNext() {
            if (this.segment != null) {
              return true;
            }
            if (!iterator.hasNext()) {
              return false;
            }
            if (this.previous == null) {
              this.previous = iterator.next();
              if (!iterator.hasNext()) {
                return false;
              }
            }
            final ICoordinate next = iterator.next();
            this.segment = new Segment(this.previous, next);
            this.previous = next;
            return true;
          }

          @Override
          public Segment next() {
            try {
              return this.segment;
            } finally {
              this.segment = null;
            }
          }
        };
      }
    };

  }

  public static List<List<ICoordinate>> getDuplicatedSupportingPoints(
      final ICoordinateSequence coordinateSequence,
      final double tolerance) {
    final List<List<ICoordinate>> coordinates = new ArrayList<>();
    ICoordinate previous = null;
    for (final ICoordinate coordinate : coordinateSequence.getCoordinates()) {
      if (previous == null) {
        previous = coordinate;
        continue;
      }
      if (CoordinateUtilities.calculateDistance(previous, coordinate) < tolerance) {
        coordinates.add(Arrays.asList(previous, coordinate));
      }
      previous = coordinate;
    }
    return coordinates;
  }

  public static boolean isRectangle(final ICoordinateSequence coordinateSequence) {
    final ICoordinateSequence sequence = removePointsOnStaightLineAndDupplicates(coordinateSequence, 5);
    if (sequence.getNumberOfCoordinates() != 5) {
      return false;
    }
    if (sequence.getXValue(0) == sequence.getXValue(1)) {
      if (sequence.getYValue(1) != sequence.getYValue(2)) {
        return false;
      }
      if (sequence.getXValue(2) != sequence.getXValue(3)) {
        return false;
      }
      if (sequence.getYValue(3) != sequence.getYValue(4)) {
        return false;
      }
      return true;
    } else if (sequence.getYValue(0) == sequence.getYValue(1)) {
      if (sequence.getXValue(1) != sequence.getXValue(2)) {
        return false;
      }
      if (sequence.getYValue(2) != sequence.getYValue(3)) {
        return false;
      }
      if (sequence.getXValue(3) != sequence.getXValue(4)) {
        return false;
      }
      return true;
    }
    return false;
  }

  private static ICoordinateSequence removePointsOnStaightLineAndDupplicates(
      final ICoordinateSequence coordinateSequence,
      final int breakSize) {
    final List<ICoordinate> coordinates = new ArrayList<>();
    ICoordinate previous = null;
    ICoordinate next = null;
    double gradient = Double.NaN;
    for (final ICoordinate coordinate : coordinateSequence.getCoordinates()) {
      if (previous == null) {
        previous = coordinate;
        coordinates.add(coordinate);
        continue;
      }
      if (next == null) {
        gradient = calculateGradient(previous, coordinate);
        next = coordinate;
        continue;
      }
      final double currentGradient = calculateGradient(previous, coordinate);
      if (currentGradient == gradient) {
        next = coordinate;
        continue;
      }
      coordinates.add(next);
      if (coordinates.size() > breakSize) {
        return coordinateSequence;
      }
      gradient = currentGradient;
      previous = next;
      next = coordinate;
    }
    if (next != null) {
      coordinates.add(next);
    }
    return new CoordinateSequenceFactory().create(coordinates);
  }

  private static double calculateGradient(final ICoordinate previous, final ICoordinate next) {
    return (next.getXValue() - previous.getXValue()) / (next.getYValue() - previous.getYValue());
  }
}
