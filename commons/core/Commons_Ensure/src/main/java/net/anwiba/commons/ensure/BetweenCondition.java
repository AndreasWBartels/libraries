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

import java.text.MessageFormat;
import java.util.Objects;

public class BetweenCondition<T extends Comparable<T>> extends AbstractCondition<T> {

  private final Comparable<T> minimum;
  private final Comparable<T> maximum;

  public BetweenCondition(final Comparable<T> minimum, final Comparable<T> maximum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }

  @Override
  public boolean accept(final T value) {
    return this.minimum.compareTo(value) <= 0 && this.maximum.compareTo(value) >= 0;
  }

  @Override
  public String toText() {
    return MessageFormat.format(
        "is between {0} and {1}", //$NON-NLS-1$
        Objects.toString(this.minimum),
        Objects.toString(this.maximum));
  }
}