/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.utilities.math;

import net.jafama.FastMath;

public class MathWrapper {

  private static boolean fast = !Boolean.getBoolean("net.anwiba.math.fast.disabled"); //$NON-NLS-1$

  public static final double PI = Math.PI;
  public static final double E = Math.E;

  public static float abs(final float value) {
    return Math.abs(value);
  }

  public static double abs(final double value) {
    return Math.abs(value);
  }

  public static float min(final float value, final float other) {
    return Math.min(value, other);
  }

  public static int min(final int value, final int other) {
    return Math.min(value, other);
  }

  public static double min(final double value, final double other) {
    return Math.min(value, other);
  }

  public static float max(final float value, final float other) {
    return Math.max(value, other);
  }

  public static double max(final double value, final double other) {
    return Math.max(value, other);
  }

  public static int max(final int value, final int other) {
    return Math.max(value, other);
  }

  public static double IEEEremainder(final double f1, final double f2) {
    return Math.IEEEremainder(f1, f2);
  }

  public static double random() {
    return Math.random();
  }

  public static double tan(final double value) {
    if (fast) {
      return FastMath.tan(value);
    }
    return Math.tan(value);
  }

  public static double atan(final double value) {
    if (fast) {
      return FastMath.atan(value);
    }
    return Math.atan(value);
  }

  public static double atan2(final double x, final double y) {
    if (fast) {
      return FastMath.atan2(x, y);
    }
    return Math.atan2(x, y);
  }

  public static double sin(final double value) {
    if (fast) {
      return FastMath.sin(value);
    }
    return Math.sin(value);
  }

  public static double asin(final double value) {
    if (fast) {
      return FastMath.asin(value);
    }
    return Math.asin(value);
  }

  public static double cos(final double value) {
    if (fast) {
      return FastMath.cos(value);
    }
    return Math.cos(value);
  }

  public static double acos(final double value) {
    if (fast) {
      return FastMath.acos(value);
    }
    return Math.acos(value);
  }

  public static double sinh(final double value) {
    if (fast) {
      return FastMath.sinh(value);
    }
    return Math.sinh(value);
  }

  public static double cosh(final double value) {
    if (fast) {
      return FastMath.cosh(value);
    }
    return Math.cosh(value);
  }

  public static double sqrt(final double value) {
    if (fast) {
      return FastMath.sqrt(value);
    }
    return Math.sqrt(value);
  }

  public static double pow(final double base, final double exponent) {
    if (fast) {
      return FastMath.pow(base, exponent);
    }
    return Math.pow(base, exponent);
  }

  public static double exp(final double value) {
    if (fast) {
      return FastMath.exp(value);
    }
    return Math.exp(value);
  }

  public static double log(final double value) {
    if (fast) {
      return FastMath.log(value);
    }
    return Math.log(value);
  }

  public static double log10(final double value) {
    if (fast) {
      return FastMath.log10(value);
    }
    return Math.log10(value);
  }

  public static double signum(final double value) {
    if (fast) {
      return FastMath.signum(value);
    }
    return Math.signum(value);
  }

  public static double toRadians(final double value) {
    if (fast) {
      return FastMath.toRadians(value);
    }
    return Math.toRadians(value);
  }

  public static double toDegrees(final double value) {
    if (fast) {
      return FastMath.toDegrees(value);
    }
    return Math.toDegrees(value);
  }

}
