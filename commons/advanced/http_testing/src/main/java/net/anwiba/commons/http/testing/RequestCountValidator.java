/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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

import org.junit.jupiter.api.Assertions;

import jakarta.servlet.http.HttpServletRequest;
import net.anwiba.commons.lang.counter.Counter;

public final class RequestCountValidator {

  private final Counter counter = new Counter(0);

  public void add(final HttpServletRequest request) {
    this.counter.increment();
  }

  public void reset() {
    this.counter.set(0);
  }

  public void assertEquals(final long expected) {
    Assertions.assertEquals(expected, this.counter.value());
  }
}
