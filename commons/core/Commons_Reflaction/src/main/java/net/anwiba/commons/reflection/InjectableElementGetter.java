/*
 * #%L
 * *
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
package net.anwiba.commons.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

import net.anwiba.commons.reflection.annotation.Injection;

public final class InjectableElementGetter {

  @SuppressWarnings("unchecked")
  public <T> Constructor<T> getConstructor(final Class<T> clazz) {
    final Constructor<?>[] constructors = clazz.getConstructors();
    if (constructors.length == 0) {
      throw new IllegalArgumentException();
    }
    if (constructors.length > 1) {
      for (final Constructor<?> constructor : constructors) {
        if (injectable(constructor)) {
          return (Constructor<T>) constructor;
        }
      }
      for (final Constructor<?> constructor : constructors) {
        if (constructor.getParameterCount() == 0) {
          return (Constructor<T>) constructor;
        }
      }
      throw new IllegalArgumentException();
    }
    return (Constructor<T>) constructors[0];
  }

  public boolean injectable(final AccessibleObject object) {
    final Injection annotation = object.getAnnotation(Injection.class);
    return annotation != null && annotation.value();
  }
}
