/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels 
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
package net.anwiba.commons.ensure;

import net.anwiba.commons.lang.object.ObjectUtilities;

import java.text.MessageFormat;

public class LowerThanCondition<T extends Comparable<T>> extends AbstractCondition<T> {

  private final Comparable<T> expected;

  public LowerThanCondition(final Comparable<T> expected) {
    this.expected = expected;
  }

  @Override
  public boolean accept(final T value) {
    Ensure.ensureArgumentNotNull(value);
    return this.expected.compareTo(value) > 0;
  }

  @Override
  public String toText() {
    return MessageFormat.format("is lower than {0}", //$NON-NLS-1$
        ObjectUtilities.toString(this.expected));
  }
}