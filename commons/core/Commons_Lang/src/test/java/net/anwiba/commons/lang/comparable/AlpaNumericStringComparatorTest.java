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
package net.anwiba.commons.lang.comparable;

import java.util.Comparator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

@SuppressWarnings({ "boxing", "nls" })
public class AlpaNumericStringComparatorTest {

  final Comparator<String> stringComparator = new AlpaNumericStringComparator();

  @Test
  public void test() throws Exception {

    assertThat(this.stringComparator.compare(
        "68766 Hockenheim, Ludwigshafener Straße 10",
        "68766 Hockenheim, Ludwigshafener Straße 10"), equalTo(0));

    assertThat(this.stringComparator.compare(
        "68766 Hockenheim, Ludwigshafener Straße 9",
        "68766 Hockenheim, Ludwigshafener Straße 10"), equalTo(-1));
    assertThat(this.stringComparator.compare(
        "68765 Hockenheim, Ludwigshafener Straße 10",
        "68766 Hockenheim, Ludwigshafener Straße 10"), equalTo(-1));
    assertThat(this.stringComparator.compare(
        "68765 Hockenheim, Ludwigshafener Straße 11",
        "68766 Hockenheim, Ludwigshafener Straße 10"), equalTo(-1));

    assertThat(this.stringComparator.compare(
        "68766 Hockenheim, Ludwigshafener Straße 11",
        "68766 Hockenheim, Ludwigshafener Straße 10"), equalTo(1));
    assertThat(this.stringComparator.compare(
        "68767 Hockenheim, Ludwigshafener Straße 10",
        "68766 Hockenheim, Ludwigshafener Straße 10"), equalTo(1));
    assertThat(this.stringComparator.compare(
        "68767 Hockenheim, Ludwigshafener Straße 9",
        "68766 Hockenheim, Ludwigshafener Straße 10"), equalTo(1));
  }

  @Test
  public void testNumber() throws Exception {
    int j = -1;
    for (int i = 0; i < 10000; i++) {
      assertLowerAndGreater(j, i);
      j = i;
    }
  }

  @Test
  public void testOrder() throws Exception {
    assertLowerAndGreater(" ", 0);
    assertLowerAndGreater(0, "a");
    assertLowerAndGreater(" ", "a");
    assertLowerAndGreater("a", "aa");
    assertLowerAndGreater("a", "A");
  }

  private <T> void assertLowerAndGreater(final T object, final T other) {
    assertThat(
        "'" + object + "' < '" + other + "'",
        this.stringComparator.compare(object.toString(), other.toString()),
        equalTo(-1));
    assertThat(
        "'" + other + "' > '" + object + "'",
        this.stringComparator.compare(other.toString(), object.toString()),
        equalTo(1));
  }

}
