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
package net.anwiba.spatial.coordinate;

import java.util.Arrays;

import net.anwiba.commons.utilities.ArrayUtilities;

public class EnvelopeUtilities {

  public static IEnvelope concat(final IEnvelope envelope, final IEnvelope other) {
    if (isNullEnvelope(envelope)) {
      return other == null ? Envelope.NULL_ENVELOPE : other;
    }
    return envelope.concat(other);
  }

  public static boolean isNullEnvelope(final IEnvelope envelope) {
    return envelope == null || envelope.equals(Envelope.NULL_ENVELOPE);
  }

  public static IEnvelope createEnvelope(final Coordinate coordinate, final double boundery) {
    final double[] minValues = getMovedValues(coordinate.getValues(), -boundery);
    final double[] maxValues = getMovedValues(coordinate.getValues(), boundery);
    return new Envelope(minValues, maxValues, coordinate.isMeasured());
  }

  public static IEnvelope createEnvelope(final ICoordinateSequence coordinateSequence) {
    assert coordinateSequence != null;
    if (coordinateSequence.getNumberOfCoordinates() == 0) {
      return Envelope.NULL_ENVELOPE;
    }
    final double coordinates[][] = coordinateSequence.getValues();
    final double[] min = new double[coordinates.length];
    final double[] max = new double[coordinates.length];
    for (int i = 0; i < coordinates.length; i++) {
      min[i] = ArrayUtilities.getMin(coordinates[i]);
      max[i] = ArrayUtilities.getMax(coordinates[i]);
    }
    return new Envelope(min, max, coordinateSequence.isMeasured());
  }

  public static IEnvelope createEnvelope(final ICoordinate c0, final ICoordinate c1) {
    assert c0 != null;
    assert c1 != null;
    final int length = Math.min(c0.getValues().length, c1.getValues().length);
    final double[] min = new double[length];
    final double[] max = new double[length];
    for (int i = 0; i < length; i++) {
      min[i] = Math.min(c0.getValue(i), c1.getValue(i));
      max[i] = Math.max(c0.getValue(i), c1.getValue(i));
    }
    return new Envelope(min, max, c0.isMeasured() && c1.isMeasured());
  }

  public static IEnvelope createEnvelope(final IEnvelope envelope, final double boundery) {
    if (boundery == 0) {
      return envelope;
    }
    final double[] minValues = getMovedValues(envelope.getMinimum().getValues(), -boundery);
    final double[] maxValues = getMovedValues(envelope.getMaximum().getValues(), boundery);
    return new Envelope(minValues, maxValues, envelope.isMeasured());
  }

  public static IEnvelope createEnvelope(final ICoordinate[] boundingBox) {
    final int length = Math.min(
        boundingBox[0].isMeasured() ? boundingBox[0].getDimension() + 1 : boundingBox[0].getDimension(),
        boundingBox[1].isMeasured() ? boundingBox[1].getDimension() + 1 : boundingBox[1].getDimension());
    return new Envelope(
        Arrays.copyOf(boundingBox[0].getValues(), length),
        Arrays.copyOf(boundingBox[1].getValues(), length),
        boundingBox[0].isMeasured() && boundingBox[1].isMeasured());
  }

  private static double[] getMovedValues(final double[] values, final double distance) {
    final double[] newValues = new double[values.length];
    for (int i = 0; i < newValues.length; i++) {
      newValues[i] = values[i] + distance;
    }
    return newValues;
  }

  public static IEnvelope scale(final IEnvelope envelope, final double scaleFactor) {
    if (scaleFactor == 1) {
      return envelope;
    }
    final double[] oldMin = envelope.getMinimum().getValues();
    final double[] oldMax = envelope.getMaximum().getValues();
    final double[] min = new double[oldMin.length];
    final double[] max = new double[oldMax.length];
    for (int i = 0; i < oldMin.length; i++) {
      final double oldDist = oldMax[i] - oldMin[i];
      final double avg = oldMin[i] + oldDist * 0.5;
      final double dist = oldDist * scaleFactor * 0.5;
      min[i] = avg - dist;
      max[i] = avg + dist;
    }
    return new Envelope(min, max, envelope.isMeasured());
  }

  public static IEnvelope moveCenterTo(final IEnvelope envelope, final ICoordinate coordinate) {
    return new TargetEnvelopeCalculator(3, 0.05).moveCenterTo(envelope, coordinate);
  }

  public static IEnvelope calculateTargetEnvelope(
      final IEnvelope currentEnvelope,
      final IEnvelope maximalEnvelope,
      final IEnvelope objectEnvelope,
      final boolean isMoveEnabled) {
    return new TargetEnvelopeCalculator(3, 0.05)
        .calculate(currentEnvelope, maximalEnvelope, objectEnvelope, isMoveEnabled);
  }

  public static boolean isInfinity(final IEnvelope envelope) {
    return Double.isInfinite(envelope.getMinimum().getXValue())
        || Double.isInfinite(envelope.getMinimum().getYValue())
        || Double.isInfinite(envelope.getMaximum().getXValue())
        || Double.isInfinite(envelope.getMaximum().getYValue());
  }
}