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
package net.anwiba.commons.injection.binding;

import java.util.Optional;

import net.anwiba.commons.injection.IBinding;

public class Bindings {

  public static <T> IBinding<T> of(final Class<T> clazz, final String name) {
    return Optional.ofNullable(name).map(n -> (IBinding<T>) new NamedClassBinding<>(clazz, n)).orElseGet(
        () -> new ClassBinding<>(clazz));
  }

  public static <T> IBinding<T> of(final Class<T> clazz) {
    return  new ClassBinding<>(clazz);
  }

}
