/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.commons.utilities.string;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class StringCollectionAcceptorBuilderTest {

  @Test
  public void testName() throws Exception {
    assertFalse(
        new StringCollectionAcceptorBuilder()
            .ignoreCase()
            .accept("sqlite")
            .accept("gridtiles")
            .otherwise(v -> "file".equalsIgnoreCase(v))
            .build()
            .accept(List.of("sqlite")));
    assertTrue(
        new StringCollectionAcceptorBuilder()
            .ignoreCase()
            .accept("sqlite")
            .accept("gridtiles")
            .otherwise(v -> "file".equalsIgnoreCase(v))
            .build()
            .accept(List.of("sqlite", "gridtiles")));
    assertTrue(
        new StringCollectionAcceptorBuilder()
            .ignoreCase()
            .accept("sqlite")
            .accept("gridtiles")
            .otherwise(v -> "file".equalsIgnoreCase(v))
            .build()
            .accept(List.of("sqlite", "gridtiles", "file")));
    assertFalse(
        new StringCollectionAcceptorBuilder()
            .ignoreCase()
            .accept("sqlite")
            .accept("gridtiles")
            .otherwise(v -> "file".equalsIgnoreCase(v))
            .build()
            .accept(List.of("file")));
    assertFalse(
        new StringCollectionAcceptorBuilder()
            .ignoreCase()
            .accept("sqlite")
            .accept("gridtiles")
            .otherwise(v -> "file".equalsIgnoreCase(v))
            .build()
            .accept(List.of("zip", "file")));
  }

}
