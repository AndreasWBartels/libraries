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
import java.util.Collection;
import java.util.List;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.lang.visitor.EnumSwitches;
import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class Envelope implements IEnvelope {

  private static final long serialVersionUID = -5809619183596261579L;
  private final boolean isMeasured;
  private final ICoordinate maximum;
  private final ICoordinate minimum;
  private final int dimension;
  public static final IEnvelope NULL_ENVELOPE = new Envelope(
      new double[] { Double.NaN, Double.NaN },
      new double[] { Double.NaN, Double.NaN },
      false);

  public static IEnvelope create(final ICoordinate minimum, final ICoordinate maximum) {
    return new Envelope(new double[] { minimum.getXValue(), minimum.getYValue() },
        new double[] { maximum.getXValue(), maximum.getYValue() },
        false);
  }

  public static IEnvelope create(final double minX, final double minY, final double maxX, final double maxY) {
    return new Envelope(new double[] { minX, minY }, new double[] { maxX, maxY }, false);
  }

  public static IEnvelope create(final String string) {
    final String[] values = StringUtilities.tokens(string, ',');
    final Double[] doubles = ArrayUtilities.convert(new IConverter<String, Double, RuntimeException>() {

      @Override
      public Double convert(final String input) throws RuntimeException {
        return Double.valueOf(input);
      }
    }, values, Double.class);
    return create(doubles[0].doubleValue(),
        doubles[1].doubleValue(),
        doubles[2].doubleValue(),
        doubles[3].doubleValue());
  }

  public Envelope(final double[] min, final double[] max, final boolean isMeasured) {
    this.minimum = new Coordinate(min, isMeasured);
    this.maximum = new Coordinate(max, isMeasured);
    this.isMeasured = isMeasured;
    this.dimension = Math.min(this.minimum.getDimension(), this.maximum.getDimension());
  }

  @Override
  public boolean isMeasured() {
    return this.isMeasured;
  }

  @Override
  public double getX() {
    return this.minimum.getXValue();
  }

  @Override
  public double getY() {
    return this.minimum.getYValue();
  }

  @Override
  public double getWidth() {
    return this.maximum.getXValue() - this.minimum.getXValue();
  }

  @Override
  public double getHeight() {
    return this.maximum.getYValue() - this.minimum.getYValue();
  }

  @Override
  public ICoordinate getMaximum() {
    return this.maximum;
  }

  @Override
  public ICoordinate getMinimum() {
    return this.minimum;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof IEnvelope)) {
      return false;
    }
    final IEnvelope other = (IEnvelope) obj;
    return this.isMeasured == other.isMeasured()
        && this.dimension == other.getDimension()
        && ObjectUtilities.equals(this.minimum, other.getMinimum())
        && ObjectUtilities.equals(this.maximum, other.getMaximum());
  }

  @Override
  public int hashCode() {
    long bits = 0;
    bits ^= this.minimum.hashCode();
    bits ^= this.maximum.hashCode();
    return (int) (bits ^ (bits >> 32));
  }

  @Override
  public String toString() {
    return this.minimum.getXValue()
        + " " //$NON-NLS-1$
        + this.minimum.getYValue()
        + " " //$NON-NLS-1$
        + this.maximum.getXValue()
        + " " //$NON-NLS-1$
        + this.maximum.getYValue();
  }

  @Override
  public int getDimension() {
    return this.dimension;
  }

  @Override
  public ICoordinate getCenterCoordinate() {
    return CoordinateUtilities.getAvarageCoordinate(this.minimum, this.maximum);
  }

  @Override
  public boolean interact(final ICoordinate coordinate) {
    final int minimumDimension = Math.min(coordinate.getDimension(), getDimension());
    for (int i = 0; i < minimumDimension; i++) {
      final double value = coordinate.getValue(i);
      if (value < this.minimum.getValue(i) || value > this.maximum.getValue(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean interact(final double x, final double y) {
    if (x < this.minimum.getXValue() || x > this.maximum.getXValue()) {
      return false;
    }
    if (y < this.minimum.getYValue() || y > this.maximum.getYValue()) {
      return false;
    }
    return true;
  }

  @Override
  public boolean interact(final IEnvelope other) {
    if (other == null) {
      return false;
    }
    for (int i = 0; i < Math.min(getDimension(), other.getDimension()); i++) {
      if (other.getMinimum().getValue(i) > this.maximum.getValue(i)
          || other.getMaximum().getValue(i) < this.minimum.getValue(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean contains(final IEnvelope other) {
    if (other == null) {
      return false;
    }
    for (int i = 0; i < Math.min(getDimension(), other.getDimension()); i++) {
      if (other.getMinimum().getValue(i) < this.minimum.getValue(i)
          || other.getMinimum().getValue(i) > this.maximum.getValue(i)) {
        return false;
      }
      if (other.getMaximum().getValue(i) < this.minimum.getValue(i)
          || other.getMaximum().getValue(i) > this.maximum.getValue(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean cross(final ICoordinate c0, final ICoordinate c1) {
    if (this.minimum.touch(this.maximum)) {
      return CoordinateUtilities.isInterior(c0, c1, this.minimum);
    }
    final Iterable<ICoordinate> coordinates = getCoordinateSequence().getCoordinates();
    ICoordinate prior = null;
    for (final ICoordinate next : coordinates) {
      if (prior == null) {
        prior = next;
        continue;
      }
      if (CoordinateUtilities.isCrossing(prior, next, c0, c1)) {
        return true;
      }
      prior = next;
    }
    return false;
  }

  @Override
  public ICoordinateSequence getCoordinateSequence() {
    return getCoordinateSequence(0);
  }

  @Override
  public ICoordinateSequence getCoordinateSequence(final int steps) {
    if (equals(Envelope.NULL_ENVELOPE)) {
      return new CoordinateSequenceFactory().createEmptyCoordinateSequence(2, false);
    }
    if (steps <= 0) {
      final List<ICoordinate> coordinates = new ArrayList<>(5);
      coordinates.add(this.minimum);
      coordinates.add(new Coordinate(this.minimum.getXValue(), this.maximum.getYValue()));
      coordinates.add(this.maximum);
      coordinates.add(new Coordinate(this.maximum.getXValue(), this.minimum.getYValue()));
      coordinates.add(this.minimum);
      return new CoordinateSequenceFactory().create(coordinates);
    }

    final List<ICoordinate> coordinates = new ArrayList<>(5 + 4 * steps);
    coordinates.add(this.minimum);
    coordinates
        .addAll(steps(this.minimum.getXValue(), Axis.X, this.minimum.getYValue(), this.maximum.getYValue(), steps));
    coordinates.add(new Coordinate(this.minimum.getXValue(), this.maximum.getYValue()));
    coordinates
        .addAll(steps(this.maximum.getYValue(), Axis.Y, this.minimum.getXValue(), this.maximum.getXValue(), steps));
    coordinates.add(this.maximum);
    coordinates
        .addAll(steps(this.maximum.getXValue(), Axis.X, this.maximum.getYValue(), this.minimum.getYValue(), steps));
    coordinates.add(new Coordinate(this.maximum.getXValue(), this.minimum.getYValue()));
    coordinates
        .addAll(steps(this.minimum.getYValue(), Axis.Y, this.maximum.getXValue(), this.minimum.getXValue(), steps));
    coordinates.add(this.minimum);
    return new CoordinateSequenceFactory().create(coordinates);
  }

  private enum Axis {
    X, Y
  }

  private Collection<? extends ICoordinate> steps(
      final double value,
      final Axis axis,
      final double min,
      final double max,
      final int steps) {
    final List<ICoordinate> coordinates = new ArrayList<>(steps);
    for (int i = 0; i < steps; i++) {
      final double other = min > max
          ? max + (((min - max) / (steps + 1)) * ((steps) - i))
          : min + (((max - min) / (steps + 1)) * (i + 1));
      coordinates.add(
          EnumSwitches
              .<Axis, ICoordinate>of()
              .ifCase(() -> new Coordinate(value, other), Axis.X)
              .ifCase(() -> new Coordinate(other, value), Axis.Y)
              .switchTo(axis));
    }
    return coordinates;
  }

  @Override
  public IEnvelope concat(final IEnvelope other) {
    if (other == null || NULL_ENVELOPE.equals(other)) {
      return this;
    }
    if (NULL_ENVELOPE.equals(this)) {
      return other;
    }
    final int length = Math.min(
        this.isMeasured ? this.dimension + 1 : this.dimension,
        other.isMeasured() ? other.getDimension() + 1 : other.getDimension());
    final double[] min = min(this.minimum.getValues(), other.getMinimum().getValues(), length);
    final double[] max = max(this.maximum.getValues(), other.getMaximum().getValues(), length);
    return new Envelope(min, max, this.isMeasured && other.isMeasured());
  }

  private double[] max(final double[] values, final double[] others, final int length) {
    final double[] result = new double[length];
    for (int i = 0; i < length; i++) {
      result[i] = Math.max(values[i], others[i]);
    }
    return result;
  }

  private double[] min(final double[] values, final double[] others, final int length) {
    final double[] result = new double[length];
    for (int i = 0; i < length; i++) {
      result[i] = Math.min(values[i], others[i]);
    }
    return result;
  }

  @Override
  public IEnvelope intersection(final IEnvelope other) {
    if (other == null || NULL_ENVELOPE.equals(other)) {
      return NULL_ENVELOPE;
    }
    if (NULL_ENVELOPE.equals(this)) {
      return NULL_ENVELOPE;
    }
    if (!interact(other)) {
      return NULL_ENVELOPE;
    }
    final int length = Math.min(
        this.isMeasured ? this.dimension + 1 : this.dimension,
        other.isMeasured() ? other.getDimension() + 1 : other.getDimension());

    final double[] min = max(this.minimum.getValues(), other.getMinimum().getValues(), length);
    final double[] max = min(this.maximum.getValues(), other.getMaximum().getValues(), length);
    return new Envelope(min, max, this.isMeasured && other.isMeasured());
  }
}
