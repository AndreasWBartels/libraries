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

import net.anwiba.commons.lang.functional.IAcceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ListUtilitiesTest {

  final Integer[] values = new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2),
      Integer.valueOf(3), Integer.valueOf(4) };
  final List<Integer> list = new ArrayList<>(Arrays.asList(this.values));

  @Test
  public void testNormalize() {
    final List<Integer> valueList =
        new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2),
            Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(4) }));
    final List<Integer> normalized = ListUtilities.normalize(valueList);
    assertThat(normalized, equalTo(this.list));
  }

  @Test
  public void testExtract() {
    final List<Integer> valueList =
        new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2),
            Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6) }));
    final List<Integer> extracted = IterableUtilities.asList(valueList, new IAcceptor<Integer>() {

      @Override
      public boolean accept(final Integer value) {
        return value.intValue() < 5;
      }
    });
    assertThat(extracted, equalTo(this.list));
  }

}