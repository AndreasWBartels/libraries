/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.process;

import net.anwiba.commons.lang.counter.Counter;
import net.anwiba.commons.lang.counter.ICounter;

public class ProcessSequencer {

  public static final class ProcessId implements IProcessIdentfier {

    private final long value;
    private static final long serialVersionUID = -675760010358153373L;

    public ProcessId(final long value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (this.value ^ (this.value >>> 32));
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof ProcessId)) {
        return false;
      }
      final ProcessId other = (ProcessId) obj;
      if (this.value != other.value) {
        return false;
      }
      return true;
    }
  }

  private static final ICounter counter = new Counter(0l, 33554432l);

  public static IProcessIdentfier getNextId() {
    return new ProcessId(counter.next());
  }
}
