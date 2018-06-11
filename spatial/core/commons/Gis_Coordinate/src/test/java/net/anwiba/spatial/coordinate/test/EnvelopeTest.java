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

package net.anwiba.spatial.coordinate.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.Envelope;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;

public class EnvelopeTest {

  ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  @Test
  public void testGetCoordinateSequenceForNullEnvelope() {
    final ICoordinateSequence sequence = Envelope.NULL_ENVELOPE.getCoordinateSequence();
    assertThat(Integer.valueOf(sequence.getDimension()), equalTo(Integer.valueOf(2)));
    assertThat(Integer.valueOf(sequence.getNumberOfCoordinates()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testGetCoordinateSequence() {
    final ICoordinateSequence sequence = new Envelope(new double[]{ 5, 5 }, new double[]{ 10, 10 }, false)
        .getCoordinateSequence(1);
    assertThat(Integer.valueOf(sequence.getDimension()), equalTo(Integer.valueOf(2)));
    assertThat(Integer.valueOf(sequence.getNumberOfCoordinates()), equalTo(Integer.valueOf(9)));
    assertThat(sequence.getCoordinateN(0), equalTo((ICoordinate) new Coordinate(5, 5)));
    assertThat(sequence.getCoordinateN(1), equalTo((ICoordinate) new Coordinate(5, 7.5)));
    assertThat(sequence.getCoordinateN(2), equalTo((ICoordinate) new Coordinate(5, 10)));
    assertThat(sequence.getCoordinateN(3), equalTo((ICoordinate) new Coordinate(7.5, 10)));
    assertThat(sequence.getCoordinateN(4), equalTo((ICoordinate) new Coordinate(10, 10)));
    assertThat(sequence.getCoordinateN(5), equalTo((ICoordinate) new Coordinate(10, 7.5)));
    assertThat(sequence.getCoordinateN(6), equalTo((ICoordinate) new Coordinate(10, 5)));
    assertThat(sequence.getCoordinateN(7), equalTo((ICoordinate) new Coordinate(7.5, 5)));
    assertThat(sequence.getCoordinateN(8), equalTo((ICoordinate) new Coordinate(5, 5)));
  }

  @Test
  public void testGetCenterCoordinate() {
    final Envelope envelope = new Envelope(new double[]{ 5, 5 }, new double[]{ 10, 10 }, false);
    assertThat(envelope.getCenterCoordinate(), equalTo((ICoordinate) new Coordinate(7.5, 7.5)));
  }

  @Test
  public void testGetX() {
    final Envelope envelope = new Envelope(new double[]{ 5, 5 }, new double[]{ 10, 10 }, false);
    assertThat(Double.valueOf(envelope.getX()), equalTo(Double.valueOf(5)));
  }

  @Test
  public void testGetY() {
    final Envelope envelope = new Envelope(new double[]{ 5, 5 }, new double[]{ 10, 10 }, false);
    assertThat(Double.valueOf(envelope.getY()), equalTo(Double.valueOf(5)));
  }

  @Test
  public void testGetWidth() {
    final Envelope envelope = new Envelope(new double[]{ 5, 5 }, new double[]{ 10, 10 }, false);
    assertThat(Double.valueOf(envelope.getWidth()), equalTo(Double.valueOf(5)));
  }

  @Test
  public void testGetHeight() {
    final Envelope envelope = new Envelope(new double[]{ 5, 5 }, new double[]{ 10, 10 }, false);
    assertThat(Double.valueOf(envelope.getHeight()), equalTo(Double.valueOf(5)));
  }
}
