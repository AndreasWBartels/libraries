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
package net.anwiba.commons.utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.ObjectUtilities;

public class ArrayUtilities {

  public static int getMin(final int[] values) {
    int result = Integer.MAX_VALUE;
    for (final int value : values) {
      if (value < result) {
        result = value;
      }
    }
    return result;
  }

  public static int getMax(final int[] values) {
    int result = Integer.MAX_VALUE;
    for (final int value : values) {
      if (value > result) {
        result = value;
      }
    }
    return result;
  }

  public static double getMin(final double[] values) {
    double result = Double.POSITIVE_INFINITY;
    for (final double value : values) {
      if (!Double.isNaN(value) && value < result) {
        result = value;
      }
    }
    return result;
  }

  public static double getMax(final double[] values) {
    double result = Double.NEGATIVE_INFINITY;
    for (final double value : values) {
      if (!Double.isNaN(value) && value > result) {
        result = value;
      }
    }
    return result;
  }

  public static byte[] concat(final byte[] values0, final byte[] values1) {
    if (values0.length == 0) {
      return copy(values1);
    }
    if (values1.length == 0) {
      return copy(values0);
    }
    return concat(values0, values1, values1.length);
  }

  public static byte[] concat(final byte[] values0, final byte[] values1, final int length) {
    final byte[] result = new byte[values0.length + length];
    System.arraycopy(values0, 0, result, 0, values0.length);
    System.arraycopy(values1, 0, result, values0.length, length);
    return result;
  }

  public static byte[] copy(final byte[] values1) {
    final byte[] result = new byte[values1.length];
    System.arraycopy(values1, 0, result, 0, values1.length);
    return result;
  }

  public static int[] concat(final int[] values0, final int... values1) {
    if (values0.length == 0) {
      return copy(values1);
    }
    if (values1.length == 0) {
      return copy(values0);
    }
    final int[] result = new int[values0.length + values1.length];
    System.arraycopy(values0, 0, result, 0, values0.length);
    System.arraycopy(values1, 0, result, values0.length, values1.length);
    return result;
  }

  public static int[] copy(final int[] values) {
    final int[] result = new int[values.length];
    System.arraycopy(values, 0, result, 0, values.length);
    return result;
  }

  public static double[] concat(final double[] values0, final double[] values1) {
    if (values0.length == 0) {
      return copy(values1);
    }
    if (values1.length == 0) {
      return copy(values0);
    }
    final double[] result = new double[values0.length + values1.length];
    System.arraycopy(values0, 0, result, 0, values0.length);
    System.arraycopy(values1, 0, result, values0.length, values1.length);
    return result;
  }

  public static double[] copy(final double[] values) {
    final double[] result = new double[values.length];
    System.arraycopy(values, 0, result, 0, values.length);
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] copy(final T[] array) {
    return copy((Class<T>) array.getClass().getComponentType(), array);
  }

  public static <O, T extends O> O[] copy(final Class<O> clazz, final T[] array) {
    if (array == null) {
      return array;
    }
    final O[] target = create(clazz, array.length);
    System.arraycopy(array, 0, target, 0, array.length);
    return target;
  }

  @SuppressWarnings("unchecked")
  public static <O, T extends O> O[] concat(final Class<O> clazz, final T[] array1, final T... array2) {
    if (array2 == null || array2.length == 0) {
      return array1;
    }
    if (array1 == null || array1.length == 0) {
      return array2;
    }
    final O[] mergedArray = create(clazz, array1.length + array2.length);
    System.arraycopy(array1, 0, mergedArray, 0, array1.length);
    System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
    return mergedArray;
  }

  @SuppressWarnings("unchecked")
  public static <O, T extends O> O[] concat(final Class<O> clazz, final T object, final T... array) {
    final O[] objects = asArray(clazz, object);
    return concat(clazz, objects, array);
  }

  public static <O, T extends O> O[] asArray(final Class<O> clazz, final T object) {
    final O[] array = create(clazz, 1);
    array[0] = object;
    return array;
  }

  public static double[] reverse(final double[] values) {
    final int n = values.length;
    final double[] reverse = new double[n];
    for (int i = 0; i < n; i++) {
      reverse[n - 1 - i] = values[i];
    }
    return reverse;
  }

  public static boolean allValuesNaN(final double[] values) {
    for (final double value : values) {
      if (!Double.isNaN(value)) {
        return false;
      }
    }
    return true;
  }

  public static boolean contains(final int[] values, final int value) {
    for (final int i : values) {
      if (i == value) {
        return true;
      }
    }
    return false;
  }

  public static <T> boolean contains(final T[] objects, final T value) {
    for (final T object : objects) {
      if (object.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static <I, O, E extends Exception> O[] convert(
      final IConverter<I, O, E> converter,
      final I[] values,
      final Class<O> clazz) throws E {
    final List<O> list = new ArrayList<>();
    for (final I value : values) {
      final O result = converter.convert(value);
      if (result != null) {
        list.add(result);
      }
    }
    final int length = values.length;
    final O[] array = create(clazz, length);
    return list.toArray(array);
  }

  public static <C> boolean instanceOf(final Object[] objects, final Class<C> clazz) {
    for (final Object object : objects) {
      if (!clazz.isInstance(object)) {
        return false;
      }
    }
    return true;
  }

  public static <O, T extends O> O[] remove(final O[] source, final T value, final Class<O> clazz) {
    final List<O> list = new ArrayList<>();
    for (final O item : source) {
      if (ObjectUtilities.equals(value, item)) {
        continue;
      }
      list.add(item);
    }
    return toArray(clazz, list);
  }

  private static <O, T extends O> O[] toArray(final Class<O> clazz, final List<T> list) {
    final O[] target = create(clazz, list.size());
    for (int i = 0; i < target.length; i++) {
      target[i] = list.get(i);
    }
    return target;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] filter(final T[] array, final IAcceptor<T> validator) {
    return filter((Class<T>) array.getClass().getComponentType(), array, validator);
  }

  public static <O, T extends O> O[] filter(final Class<O> clazz, final T[] array, final IAcceptor<O> validator) {
    final List<O> list = new ArrayList<>();
    for (final O item : array) {
      if (!validator.accept(item)) {
        continue;
      }
      list.add(item);
    }
    return toArray(clazz, list);
  }

  public static <T> T findFirst(final T[] values, final IAcceptor<T> validator) {
    for (final T value : values) {
      if (validator.accept(value)) {
        return value;
      }
    }
    return null;
  }

  public static int[] primitives(final Integer[] array) {
    final int[] result = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i].intValue();
    }
    return result;
  }

  public static double[] primitives(final Double[] array) {
    final double[] result = new double[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i].doubleValue();
    }
    return result;
  }

  public static byte[] primitives(final Byte[] array) {
    final byte[] result = new byte[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i].byteValue();
    }
    return result;
  }

  public static <T> int indexOf(final T[] objects, final IAcceptor<T> validator) {
    for (int index = 0; index < objects.length; index++) {
      if (validator.accept(objects[index])) {
        return index;
      }
    }
    return -1;
  }

  public static Short[] objects(final short[] array) {
    final Short[] result = new Short[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Short.valueOf(array[i]);
    }
    return result;
  }

  public static Integer[] objects(final int[] array) {
    final Integer[] result = new Integer[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Integer.valueOf(array[i]);
    }
    return result;
  }

  public static Long[] objects(final long[] array) {
    final Long[] result = new Long[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Long.valueOf(array[i]);
    }
    return result;
  }

  public static Float[] objects(final float[] array) {
    final Float[] result = new Float[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Float.valueOf(array[i]);
    }
    return result;
  }

  public static Double[] objects(final double[] array) {
    final Double[] result = new Double[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Double.valueOf(array[i]);
    }
    return result;
  }

  public static Byte[] objects(final byte[] array) {
    final Byte[] result = new Byte[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Byte.valueOf(array[i]);
    }
    return result;
  }

  public static Character[] objects(final char[] array) {
    final Character[] result = new Character[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Character.valueOf(array[i]);
    }
    return result;
  }

  public static Boolean[] objects(final boolean[] array) {
    final Boolean[] result = new Boolean[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Boolean.valueOf(array[i]);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] create(final T... objects) {
    return objects;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] normalize(final T... values) {
    final List<T> list = new ArrayList<>(values.length);
    for (final T t : values) {
      if (t == null) {
        continue;
      }
      list.add(t);
    }
    return toArray((Class<T>) values.getClass().getComponentType(), list);
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] create(final Class<T> clazz, final int size) {
    return (T[]) Array.newInstance(clazz, size);
  }

  public static <T> boolean containts(final T[] array, final T object) {
    for (final T item : array) {
      if (ObjectUtilities.equals(item, object)) {
        return false;
      }
    }
    return false;
  }

}
