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
package net.anwiba.spatial.coordinate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.Envelope;
import net.anwiba.spatial.coordinate.EnvelopeUtilities;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;

public class EnvelopeUtilitiesTest {

  ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  @Test
  public void testCreateByCoordinateSequence() {
    assertEquals(
        Envelope.NULL_ENVELOPE,
        EnvelopeUtilities.createEnvelope(this.coordinateSequenceFactory.createEmptyCoordinateSequence(2, false)));
    assertEquals(
        new Envelope(new double[] { 5, 5 }, new double[] { 5, 5 }, false),
        EnvelopeUtilities.createEnvelope(this.coordinateSequenceFactory.create(5, 5)));
    assertEquals(
        new Envelope(new double[] { 5, 5 }, new double[] { 10, 10 }, false),
        EnvelopeUtilities.createEnvelope(this.coordinateSequenceFactory.create(new double[] { 10, 5 },
            new double[] {
                5, 10 })));
    assertEquals(
        new Envelope(new double[] { 5, 5 }, new double[] { 10, 10 }, false),
        EnvelopeUtilities.createEnvelope(this.coordinateSequenceFactory.create(new double[] { 10, 5, 8 },
            new double[] {
                5, 10, 8 })));
  }

  @Test
  public void testConcat() throws Exception {
    assertEquals(Envelope.NULL_ENVELOPE, EnvelopeUtilities.concat(Envelope.NULL_ENVELOPE, null));
    assertEquals(Envelope.NULL_ENVELOPE, EnvelopeUtilities.concat(null, Envelope.NULL_ENVELOPE));
    assertEquals(new Envelope(new double[] { 5, 5 }, new double[] { 10, 10 }, false),
        EnvelopeUtilities.concat(
            new Envelope(new double[] { 5, 5 }, new double[] { 10, 10 }, false),
            Envelope.NULL_ENVELOPE));
    assertEquals(new Envelope(new double[] { 5, 5 }, new double[] { 10, 10 }, false),
        EnvelopeUtilities.concat(
            Envelope.NULL_ENVELOPE,
            new Envelope(new double[] { 5, 5 }, new double[] { 10, 10 }, false)));
    assertEquals(new Envelope(new double[] { 2, 5 }, new double[] { 12, 10 }, false),
        EnvelopeUtilities.concat(
            new Envelope(new double[] { 5, 5 }, new double[] { 10, 10 }, false),
            new Envelope(new double[] { 2, 7 }, new double[] { 12, 8 }, false)));
  }
}
