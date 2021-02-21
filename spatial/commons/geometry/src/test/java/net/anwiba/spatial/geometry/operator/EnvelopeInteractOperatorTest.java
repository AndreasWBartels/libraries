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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.anwiba.spatial.coordinate.Envelope;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.utilities.GeometryUtilities;

public class EnvelopeInteractOperatorTest {

  IGeometryFactory factory = GeometryUtilities.getDefaultGeometryFactory();

  // 5484712.5, 5484926.0,5485134.0,5485342.5

  // 3540814.75, 3540798.5, 3540797.75, 3540822.5

  @Test
  public void test() throws Exception {
    final Envelope envelope =
        new Envelope(new double[] { 3540736.643725507, 5484932.200808836 },
            new double[] { 3540859.381529854,
                5485029.237107145 },
            false);
    // final Envelop envelop = new Envelop(new double[] { 5, 5 }, new double[] { 10, 10 }, false);
    final EnvelopeInteractOperator operator = new EnvelopeInteractOperator(envelope);
    // final LineString lineString = this.factory.createLineString(new double[] { 7.5, 7.5 }, new double[] { 2.5, 12.5
    // });
    final ILineString lineString =
        this.factory.createLineString(new double[] { 3540798.5, 3540797.75 }, new double[] { 5484926.0, 5485134.0 });
    // final LineString lineString =
    // this.factory.createLineString(new double[] { 3540814.75, 3540798.5, 3540797.75, 3540822.5 }, new double[] {
    // 5484712.5, 5484926.0, 5485134.0, 5485342.5 });
    assertTrue(operator.interact(lineString));
  }
}
