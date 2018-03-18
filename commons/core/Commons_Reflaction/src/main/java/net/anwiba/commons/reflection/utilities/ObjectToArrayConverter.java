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
package net.anwiba.commons.reflection.utilities;

import java.lang.reflect.Array;
import java.util.Collection;

public class ObjectToArrayConverter {

  public static Object toPrimativArray(final Collection object, final Class<?> componentType) {
    if (componentType.isAssignableFrom(double.class)) {
      return toDoubleArray(object);
    }
    if (componentType.isAssignableFrom(float.class)) {
      return toFloatArray(object);
    }
    if (componentType.isAssignableFrom(long.class)) {
      return toLongArray(object);
    }
    if (componentType.isAssignableFrom(int.class)) {
      return toIntArray(object);
    }
    if (componentType.isAssignableFrom(short.class)) {
      return toShortArray(object);
    }
    if (componentType.isAssignableFrom(boolean.class)) {
      return toBooleanArray(object);
    }
    if (componentType.isAssignableFrom(byte.class)) {
      return toByteArray(object);
    }
    if (componentType.isAssignableFrom(char.class)) {
      return toCharArray(object);
    }
    throw new RuntimeException();
  }

  @SuppressWarnings({ "rawtypes" })
  public static Object toArray(final Collection object, final Class<?> clazz) {
    if (!clazz.getComponentType().isArray()) {
      if (clazz.getComponentType().isPrimitive()) {
        return toPrimativArray(object, clazz.getComponentType());
      }
      final Object array = Array.newInstance(clazz.getComponentType(), object.size());
      int index = 0;
      for (final Object value : object) {
        if (!clazz.getComponentType().isInstance(object)) {
          continue;
        }
        Array.set(array, index, clazz.getComponentType().cast(value));
        index++;
      }
      return array;
    }
    final Object array = Array.newInstance(clazz.getComponentType(), object.size());
    int index = 0;
    for (final Object values : object) {
      final Object value = toArray((Collection) values, clazz.getComponentType());
      Array.set(array, index, value);
      index++;
    }
    return array;
  }

  public static double[] toDoubleArray(final Collection collection) {
    final double[] array = new double[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).doubleValue();
    }
    return array;
  }

  private static float[] toFloatArray(final Collection collection) {
    final float[] array = new float[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).floatValue();
    }
    return array;
  }

  public static long[] toLongArray(final Collection collection) {
    final long[] array = new long[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).longValue();
    }
    return array;
  }

  public static int[] toIntArray(final Collection collection) {
    final int[] array = new int[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).intValue();
    }
    return array;
  }

  public static short[] toShortArray(final Collection collection) {
    final short[] array = new short[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Number) objects[i]).shortValue();
    }
    return array;
  }

  private static boolean[] toBooleanArray(final Collection collection) {
    final boolean[] array = new boolean[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Boolean) objects[i]).booleanValue();
    }
    return array;
  }

  public static byte[] toByteArray(final Collection collection) {
    final byte[] array = new byte[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Byte) objects[i]).byteValue();
    }
    return array;
  }

  private static char[] toCharArray(final Collection collection) {
    final char[] array = new char[collection.size()];
    final Object[] objects = collection.toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Character) objects[i]).charValue();
    }
    return array;
  }

}
