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
package net.anwiba.commons.lang.functional;

import java.util.Objects;

@FunctionalInterface
public interface IApplicable<T> {

  boolean isApplicable(T context);

  static <T> IApplicable<T> not(IApplicable<T> applicable) {
    Objects.requireNonNull(applicable);
    return applicable.not();
  }

  default IApplicable<T> not() {
    return (t) -> !isApplicable(t);
  }

  default IApplicable<T> and(final IApplicable<? super T> other) {
    Objects.requireNonNull(other);
    return (t) -> isApplicable(t) && other.isApplicable(t);
  }

  default IApplicable<T> or(final IApplicable<? super T> other) {
    Objects.requireNonNull(other);
    return (t) -> isApplicable(t) || other.isApplicable(t);
  }

}
