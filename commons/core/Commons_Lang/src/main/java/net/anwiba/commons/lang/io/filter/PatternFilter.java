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
package net.anwiba.commons.lang.io.filter;

import net.anwiba.commons.lang.queue.IntValueQueue;

public class PatternFilter implements IFilteringInputStreamValidator {

  private final byte[] pattern;
  private int index = 0;

  private final IntValueQueue queue = new IntValueQueue();

  public PatternFilter(final byte[] pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean accept(final int value) {
    if (this.pattern[this.index] != value) {
      this.queue.add(value);
      this.index = 0;
      return true;
    }
    if (this.index + 1 == this.pattern.length) {
      this.index = 0;
      this.queue.clear();
      return false;
    }
    this.index = this.index + 1;
    this.queue.add(value);
    return false;
  }

  @Override
  public IntValueQueue getQueue() {
    return this.queue;
  }

}
