/*
 * #%L
 *
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
package net.anwiba.commons.lang.stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class StreamsTest {

  @Test
  public void isInstanceOf() throws Exception {
    final Object[] objects = new Object[] { new StreamsTest() };
    assertNotNull(Streams.of(objects).instanceOf(StreamsTest.class).first().get());
  }

  @Test
  public void isNotInstanceOf() throws Exception {
    final Object[] objects = new Object[] { new Object() };
    assertNull(Streams.of(objects).instanceOf(StreamsTest.class).first().get());
  }
}
