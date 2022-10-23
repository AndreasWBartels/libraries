/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.spatial.coordinate.calculator;

import java.util.LinkedList;
import java.util.List;

import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinate.Orientation;

public class IntersectionCalculator {

  public static List<ICoordinateSequence>
      intersect(ICoordinateSequenceFactory factory, ICoordinateSequence coordinateSequence, IEnvelope envelope, int dimension) {
    if (!envelope.interact(coordinateSequence.getEnvelope())) {
      return List.of();
    }
    if (envelope.contains(coordinateSequence.getEnvelope())) {
      return List.of(coordinateSequence);
    }
    if (coordinateSequence.isClosed() && dimension > 1) {
      return intersectClosedCoordinateSequence(factory,
          coordinateSequence,
          CoordinateSequenceOrientationCalculator.getOrientation(coordinateSequence),
          envelope);
    }
    return extracted(factory, coordinateSequence, envelope);
  }

  private static List<ICoordinateSequence> intersectClosedCoordinateSequence(
      ICoordinateSequenceFactory factory,
      ICoordinateSequence coordinateSequence,
      Orientation orientation,
      IEnvelope envelope) {
    List<ICoordinateSequence> extracted = extracted(factory, coordinateSequence, envelope);
    if (extracted.size() == 1) {
      return extracted;
    }
    return extracted;
  }

  private static List<ICoordinateSequence>
      extracted(ICoordinateSequenceFactory factory, ICoordinateSequence coordinateSequence, IEnvelope envelope) {
    List<List<ICoordinate>> coordinatesList = new LinkedList<>();
    List<ICoordinate> coordinates = new LinkedList<>();
    ICoordinateSequence coordinateSequenceOfEnvelope = envelope.getCoordinateSequence();
    ICoordinate previous = null;
    boolean isPreviousInteracting = false;
    for (ICoordinate coordinate : coordinateSequence.getCoordinates()) {
      if (envelope.interact(coordinate)) {
        coordinates.add(coordinate);
        previous = coordinate;
        isPreviousInteracting = true;
        continue;
      }

      if (previous == null) {
        previous = coordinate;
        isPreviousInteracting = false;
        continue;
      }

      if (!isPreviousInteracting && !coordinates.isEmpty()) {
        coordinatesList.add(coordinates);
        coordinates = new LinkedList<>();
      }

      List<ICoordinate> crossPoints = CoordinateSequenceUtilities
          .calculateCrossPoints(coordinateSequenceOfEnvelope, previous, coordinate);
      for (ICoordinate crossPoint : crossPoints) {
        coordinates.add(crossPoint);
      }

      previous = coordinate;
      isPreviousInteracting = false;
      continue;
    }
    if (!coordinates.isEmpty()) {
      coordinatesList.add(coordinates);
    }

    return Streams.of(coordinatesList).convert(list -> factory.create(list)).asList();
  }

}
