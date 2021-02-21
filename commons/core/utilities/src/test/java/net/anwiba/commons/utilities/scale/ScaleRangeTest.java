/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.utilities.scale;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ScaleRangeTest {

  @Test
  public void test() throws Exception {
    IScaleRange scaleRange = new ScaleRange(ScaleValue.NULL_VALUE, ScaleValue.NULL_VALUE);
    assertTrue(ScaleRange.MIN_VALUE.equals(scaleRange.getMin()));
    assertTrue(ScaleRange.MAX_VALUE.equals(scaleRange.getMax()));
    scaleRange = new ScaleRange(ScaleRange.MIN_VALUE, ScaleRange.MAX_VALUE);
    assertTrue(ScaleRange.MIN_VALUE.equals(scaleRange.getMin()));
    assertTrue(ScaleRange.MAX_VALUE.equals(scaleRange.getMax()));
    scaleRange = new ScaleRange(0d, 0d);
    assertTrue(ScaleRange.MIN_VALUE.equals(scaleRange.getMin()));
    assertTrue(ScaleRange.MAX_VALUE.equals(scaleRange.getMax()));
    scaleRange = new ScaleRange(0d, 1d);
    assertTrue(ScaleRange.MIN_VALUE.equals(scaleRange.getMin()));
    assertTrue(ScaleRange.MAX_VALUE.equals(scaleRange.getMax()));
  }
}
