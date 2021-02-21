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
package net.anwiba.commons.utilities.collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.functional.IAcceptor;

public class IterableUtilitiesTest {
  final Integer[] values = new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2),
      Integer.valueOf(3), Integer.valueOf(4) };
  final List<Integer> list = new ArrayList<>(Arrays.asList(this.values));

  @Test
  public void testAsList() {
    final List<Integer> copy = IterableUtilities.asList(this.list);
    assertThat(copy, equalTo(this.list));
    assertNotSame(copy, this.list);
  }

  @Test
  public void testContainsAcceptedItems() {
    final IAcceptor<String> validator = new IAcceptor<String>() {

      @Override
      public boolean accept(final String value) {
        return value.length() == 4;
      }
    };
    final List<String> strings = new ArrayList<>();
    assertFalse(IterableUtilities.containsAcceptedItems(strings, validator));
    strings.addAll(Arrays.asList(new String[] { "123" })); //$NON-NLS-1$
    assertFalse(IterableUtilities.containsAcceptedItems(strings, validator));
    strings.addAll(Arrays.asList(new String[] { "1234" })); //$NON-NLS-1$
    assertTrue(IterableUtilities.containsAcceptedItems(strings, validator));
  }
}