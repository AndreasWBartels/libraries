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

import net.anwiba.commons.utilities.scale.ScaleRange;
import net.anwiba.commons.utilities.scale.ScaleValue;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScaleValueTest {

  @Test
  public void test() throws Exception {
    assertTrue(ScaleValue.NULL_VALUE.equals(ScaleValue.NULL_VALUE));
    assertTrue(ScaleRange.MIN_VALUE.equals(ScaleRange.MIN_VALUE));
    assertTrue(ScaleRange.MAX_VALUE.equals(ScaleRange.MAX_VALUE));
    assertTrue(ScaleValue.NULL_VALUE.equals(ScaleRange.MIN_VALUE));
    assertTrue(ScaleValue.NULL_VALUE.equals(new ScaleValue(0)));
    assertTrue(ScaleValue.NULL_VALUE.equals(new ScaleValue(0D)));
    assertTrue(ScaleRange.MIN_VALUE.equals(ScaleValue.NULL_VALUE));
    assertTrue(new ScaleValue(0).equals(ScaleValue.NULL_VALUE));
    assertTrue(new ScaleValue(0D).equals(ScaleValue.NULL_VALUE));
    assertTrue(new ScaleValue(0).equals(ScaleRange.MIN_VALUE));
    assertTrue(new ScaleValue(0D).equals(ScaleRange.MIN_VALUE));
    assertFalse(ScaleRange.MAX_VALUE.equals(ScaleRange.MIN_VALUE));
    assertFalse(ScaleRange.MIN_VALUE.equals(ScaleRange.MAX_VALUE));
    assertFalse(ScaleValue.NULL_VALUE.equals(ScaleRange.MAX_VALUE));
    assertFalse(ScaleRange.MAX_VALUE.equals(ScaleValue.NULL_VALUE));
  }
}
