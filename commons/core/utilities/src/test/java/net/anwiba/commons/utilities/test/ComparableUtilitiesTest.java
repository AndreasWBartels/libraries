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
package net.anwiba.commons.utilities.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.utilities.ComparableUtilities;

public class ComparableUtilitiesTest {

  @Test
  public void isGreaterEquals() throws Exception {
    assertTrue(ComparableUtilities.isGreaterEquals(Integer.valueOf(0), Integer.valueOf(0)));
    assertTrue(ComparableUtilities.isGreaterEquals(Integer.valueOf(1), Integer.valueOf(0)));
    assertFalse(ComparableUtilities.isGreaterEquals(Integer.valueOf(0), Integer.valueOf(1)));
  }

  @Test
  public void isGreaterThan() throws Exception {
    assertFalse(ComparableUtilities.isGreaterThan(Integer.valueOf(0), Integer.valueOf(0)));
    assertTrue(ComparableUtilities.isGreaterThan(Integer.valueOf(1), Integer.valueOf(0)));
    assertFalse(ComparableUtilities.isGreaterThan(Integer.valueOf(0), Integer.valueOf(1)));
  }

  @Test
  public void isLowerThan() throws Exception {
    assertFalse(ComparableUtilities.isLowerThan(Integer.valueOf(0), Integer.valueOf(0)));
    assertFalse(ComparableUtilities.isLowerThan(Integer.valueOf(1), Integer.valueOf(0)));
    assertTrue(ComparableUtilities.isLowerThan(Integer.valueOf(0), Integer.valueOf(1)));
  }

  @Test
  public void isLowerEquals() throws Exception {
    assertTrue(ComparableUtilities.isLowerEquals(Integer.valueOf(0), Integer.valueOf(0)));
    assertFalse(ComparableUtilities.isLowerEquals(Integer.valueOf(1), Integer.valueOf(0)));
    assertTrue(ComparableUtilities.isLowerEquals(Integer.valueOf(0), Integer.valueOf(1)));
  }
}
