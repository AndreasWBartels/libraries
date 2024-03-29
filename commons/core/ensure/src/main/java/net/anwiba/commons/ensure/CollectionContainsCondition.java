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
import java.util.Collection;
import java.util.Objects;

public class CollectionContainsCondition<V, T extends Collection<V>> extends AbstractCondition<T> {

  private final Collection<V> values;

  public CollectionContainsCondition(final Collection<V> values) {
    this.values = values;
  }

  @Override
  public boolean accept(final T value) {
    Ensure.ensureArgumentNotNull(value);
    return value.containsAll(this.values);
  }

  @Override
  public String toText() {
    return MessageFormat.format("contains ''{0}''", Objects.toString(this.values)); //$NON-NLS-1$
  }
}
