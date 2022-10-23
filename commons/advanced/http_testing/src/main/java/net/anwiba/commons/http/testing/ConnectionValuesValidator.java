/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.http.testing;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;

import jakarta.servlet.http.HttpServletRequest;

public final class ConnectionValuesValidator {

  private final Set<String> values = new HashSet<>();

  public void add(final HttpServletRequest request) {
    this.values.add(request.getHeader("Connection"));
  }

  public void reset() {
    this.values.clear();
  }

  public void assertEquals(final Set<String> expected) {
    Assertions.assertEquals(expected, this.values);
  }
}
