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
import java.util.List;
import java.util.Objects;

class AnyOfCondition<T> extends AbstractCondition<T> {

  private final List<ICondition<? super T>> conditions;

  AnyOfCondition(final List<ICondition<? super T>> conditions) {
    this.conditions = conditions;
  }

  @Override
  public boolean accept(final T value) {
    for (final ICondition<? super T> condition : this.conditions) {
      if (condition.accept(value)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toText() {
    return MessageFormat.format(
        "all of {0}", //$NON-NLS-1$
        Objects.toString(this.conditions));
  }
}
