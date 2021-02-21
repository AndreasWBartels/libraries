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
package net.anwiba.commons.utilities.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class ZipUtilitiesTest {

  @Test
  public void succcessful() throws IOException {
    assertCleanedName("evil.txt", "evil.txt");
    assertCleanedName("tmp/evil.txt", "tmp/evil.txt");
    assertCleanedName("./tmp/./evil.txt", "tmp/evil.txt");
    assertCleanedName("./tmp/../tmp/evil.txt", "tmp/evil.txt");
  }

  @Test
  public void faild() throws IOException {
    assertThrows(IOException.class, () -> {
      assertCleanedName("../foo/../tmp/evil.txt", "tmp/evil.txt");
    });
  }

  private void assertCleanedName(final String name, final String expected) throws IOException {
    final String file = ZipUtilities.resolveSpecialPathNames(name);
    assertThat(file, equalTo(expected));
  }

}
