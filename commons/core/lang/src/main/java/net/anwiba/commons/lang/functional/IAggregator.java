/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels 
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
package net.anwiba.commons.lang.functional;

import java.util.Objects;

@FunctionalInterface
public interface IAggregator<I, V, O, E extends Exception> {

  O aggregate(I value, V other) throws E;

  default IAggregator<I, V, O, E> then(final IFunction<? super O, ? extends O, E> after) {
    Objects.requireNonNull(after);
    return (final I o, final V t) -> after.execute(aggregate(o, t));
  }
}
