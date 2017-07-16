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
package net.anwiba.commons.lang.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ObjectUtilities {

  public static boolean equals(final double org, final double other) {
    return equals(Double.valueOf(org), Double.valueOf(other));
  }

  public static boolean equals(final Object org, final Object other) {
    if (org == other) {
      return true;
    } else if (org == null) {
      return other == null;
    } else {
      final boolean equals = org.equals(other);
      return equals;
    }
  }

  public static boolean equals(final byte[] is, final byte[] bs) {
    if (is == bs) {
      return true;
    }
    if (is == null || bs == null || is.length != bs.length) {
      return false;
    }
    for (int i = 0; i < bs.length; i++) {
      if (is[i] != bs[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equals(final short[] is, final short[] bs) {
    if (is == null && bs == null) {
      return true;
    }
    if (is == null || bs == null || is.length != bs.length) {
      return false;
    }
    for (int i = 0; i < bs.length; i++) {
      if (is[i] != bs[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equals(final int[] is, final int[] bs) {
    if (is == null && bs == null) {
      return true;
    }
    if (is == null || bs == null || is.length != bs.length) {
      return false;
    }
    for (int i = 0; i < bs.length; i++) {
      if (is[i] != bs[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equals(final long[] is, final long[] bs) {
    if (is == null && bs == null) {
      return true;
    }
    if (is == null || bs == null || is.length != bs.length) {
      return false;
    }
    for (int i = 0; i < bs.length; i++) {
      if (is[i] != bs[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equals(final float[] is, final float[] bs) {
    if (is == null && bs == null) {
      return true;
    }
    if (is == null || bs == null || is.length != bs.length) {
      return false;
    }
    for (int i = 0; i < bs.length; i++) {
      if (is[i] != bs[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equals(final double[] ds, final double[] bs) {
    if (ds == null && bs == null) {
      return true;
    }
    if (ds == null || bs == null || ds.length != bs.length) {
      return false;
    }
    for (int i = 0; i < bs.length; i++) {
      if (Double.isNaN(ds[i]) && Double.isNaN(bs[i])) {
        continue;
      }
      if (ds[i] != bs[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equals(final Object[] os, final Object[] bs) {
    if (os == bs || (os == null && bs == null)) {
      return true;
    }
    if (os == null || bs == null || os.length != bs.length) {
      return false;
    }
    for (int i = 0; i < bs.length; i++) {
      if (!equals(os[i], bs[i])) {
        return false;
      }
    }
    return true;
  }

  public static int hashCode(final int result, final int prime, final double value) {
    return hashCode(result, prime, Double.doubleToLongBits(value));
  }

  public static int hashCode(final int result, final int prime, final long value) {
    return prime * result + (int) (value ^ (value >>> 32));
  }

  public static int hashCode(final int result, final int prime, final Object object) {
    return prime * result + ((object == null) ? 0 : object.hashCode());
  }

  public static int hashCode(final Object... objects) {
    return Arrays.hashCode(objects);
  }

  public static boolean isToStringImplemented(final Class<?> clazz) {
    try {
      final Method method = clazz.getMethod("toString"); //$NON-NLS-1$
      return !method.getDeclaringClass().equals(Object.class);
    } catch (final Throwable throwable) {
      return false;
    }
  }

  public static String toString(final Object object) {
    return toString(object, (String) null);
  }

  public static String toString(final Object object, final String nullValue) {
    if (object == null) {
      return nullValue;
    }
    if (Object.class.equals(object.getClass())) {
      return Integer.toHexString(object.hashCode());
    }
    if (ObjectUtilities.isToStringImplemented(object.getClass())) {
      return object.toString();
    }
    final Field[] fields = object.getClass().getDeclaredFields();
    return toString(object, fields, nullValue);
  }

  private static String toString(final Object object, final Field[] fields, final String nullValue) {
    int counter = 0;
    final StringBuilder builder = new StringBuilder();
    builder.append("["); //$NON-NLS-1$
    for (final Field field : fields) {
      if (field.isSynthetic()) {
        continue;
      }
      try {
        if (!field.isAccessible()) {
          field.setAccessible(true);
        }
        final String string = toString(field.get(object), nullValue);
        if (counter > 0) {
          builder.append(", "); //$NON-NLS-1$
        }
        builder.append(string);
        counter++;
      } catch (final IllegalArgumentException exception) {
        // nothing to do
      } catch (final IllegalAccessException exception) {
        // nothing to do
      }
    }
    builder.append("]"); //$NON-NLS-1$
    return builder.toString();
  }

  public static final String NULL_STRING = "null"; //$NON-NLS-1$

}
