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
package net.anwiba.commons.reflection.privileged;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

final public class OptionalPrivilegedFieldSetterAction extends AbstractPrivilegedAction<Void> {
  private final Object object;
  private final Object value;
  private final String fieldName;

  public OptionalPrivilegedFieldSetterAction(final Object object, final String fieldName, final Object value) {
    this.object = object;
    this.fieldName = fieldName;
    this.value = value;
  }

  @Override
  protected Void invoke() {
    final Field field = getField();
    if (field == null) {
      return null;
    }
    final boolean accessible = field.isAccessible();
    try {
      field.setAccessible(true);
      @SuppressWarnings("hiding")
      final Object value = convert(field, this.value);
      field.set(this.object, value);
      return null;
    } catch (IllegalArgumentException | IllegalAccessException exception) {
      return null;
    } finally {
      field.setAccessible(accessible);
    }
  }

  @SuppressWarnings({ "hiding", "rawtypes", "unchecked" })
  private Object convert(final Field field, final Object value) {
    final Class declaringClass = field.getType();
    if (declaringClass.isArray()) {
      if (value == null) {
        return value;
      }
      if (value.getClass().isArray()) {
        return value;
      }
      if (Collection.class.isInstance(value)) {
        if (declaringClass.isAssignableFrom(double[].class)) {
          return createDoubleArray((Collection) value);
        }
        if (declaringClass.isAssignableFrom(long[].class)) {
          return createLongArray((Collection) value);
        }
        if (declaringClass.isAssignableFrom(int[].class)) {
          return createIntegerArray((Collection) value);
        }
        return create(declaringClass, (Collection) value);
      }
    }
    return null;
  }

  private double[] createDoubleArray(@SuppressWarnings("rawtypes") final Collection collection) {
    final double[] array = new double[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).doubleValue();
    }
    return array;
  }

  private long[] createLongArray(@SuppressWarnings("rawtypes") final Collection collection) {
    final long[] array = new long[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).longValue();
    }
    return array;
  }

  private int[] createIntegerArray(@SuppressWarnings("rawtypes") final Collection collection) {
    final int[] array = new int[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).intValue();
    }
    return array;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <T> T[] create(final Class declaringClass, final Collection collection) {
    final T[] newInstance = (T[]) Array.newInstance(declaringClass, collection.size());
    return (T[]) collection.toArray(newInstance);
  }

  private Field getField() {
    return Stream
        .of(this.object.getClass().getDeclaredFields())
        .filter(f -> Objects.equals(f.getName(), this.fieldName))
        .findFirst()
        .orElseGet(
            () -> Stream
                .of(this.object.getClass().getFields())
                .filter(f -> Objects.equals(f.getName(), this.fieldName))
                .findFirst()
                .orElseGet(() -> null));
  }
}
