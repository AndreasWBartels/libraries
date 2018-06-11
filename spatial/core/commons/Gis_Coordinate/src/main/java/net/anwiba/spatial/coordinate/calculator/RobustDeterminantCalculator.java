/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.spatial.coordinate.calculator;

/**
 * Implements an algorithm to compute the sign of a 2x2 determinant for double precision values robustly. It is a
 * reimplemetation of code developed by Olivier Devillers.
 * 
 * Source: http://www-sop.inria.fr/prisme/logiciel/determinant.html Author : Olivier Devillers
 * Olivier.Devillers@sophia.inria.fr
 */
public class RobustDeterminantCalculator {

  public static int signOfDet(final double x0, final double y0, final double x1, final double y1) {
    return signOfDet(new double[][] { { x0, y0 }, { x1, y1 } });
  }

  private static int signOfDet(final double[][] m) {
    int sign = 1;
    if (m[0][0] == 0.0 || m[1][1] == 0.0) {
      if (m[0][1] == 0.0 || m[1][0] == 0.0) {
        return 0;
      }
      return signFor(m[0][1], m[1][0], sign);
    }
    if (m[0][1] == 0.0 || m[1][0] == 0.0) {
      return signFor(m[1][1], m[0][0], -sign);
    }
    sign = swap(m, sign);
    if (0.0 < m[0][0]) {
      if (0.0 < m[1][0]) {
        if (m[0][0] > m[1][0]) {
          return sign;
        }
      } else {
        return sign;
      }
    } else {
      if (0.0 < m[1][0]) {
        return -sign;
      }
      if (m[0][0] >= m[1][0]) {
        sign = -sign;
        m[0][0] = -m[0][0];
        m[1][0] = -m[1][0];
      } else {
        return -sign;
      }
    }
    while (true) {
      double k = Math.floor(m[1][0] / m[0][0]);
      m[1][0] = m[1][0] - k * m[0][0];
      m[1][1] = m[1][1] - k * m[0][1];
      if (m[1][1] < 0.0) {
        return -sign;
      }
      if (m[1][1] > m[0][1]) {
        return sign;
      }
      if (m[0][0] > m[1][0] + m[1][0]) {
        if (m[0][1] < m[1][1] + m[1][1]) {
          return sign;
        }
      } else {
        if (m[0][1] > m[1][1] + m[1][1]) {
          return -sign;
        }
        m[1][0] = m[0][0] - m[1][0];
        m[1][1] = m[0][1] - m[1][1];
        sign = -sign;
      }
      if (m[1][1] == 0.0) {
        if (m[1][0] == 0.0) {
          return 0;
        }
        return -sign;
      }
      if (m[1][0] == 0.0) {
        return sign;
      }
      k = Math.floor(m[0][0] / m[1][0]);
      m[0][0] = m[0][0] - k * m[1][0];
      m[0][1] = m[0][1] - k * m[1][1];
      if (m[0][1] < 0.0) {
        return sign;
      }
      if (m[0][1] > m[1][1]) {
        return -sign;
      }
      if (m[1][0] > m[0][0] + m[0][0]) {
        if (m[1][1] < m[0][1] + m[0][1]) {
          return -sign;
        }
      } else {
        if (m[1][1] > m[0][1] + m[0][1]) {
          return sign;
        }
        m[0][0] = m[1][0] - m[0][0];
        m[0][1] = m[1][1] - m[0][1];
        sign = -sign;
      }
      if (m[0][1] == 0.0) {
        if (m[0][0] == 0.0) {
          return 0;
        }
        return sign;
      }
      if (m[0][0] == 0.0) {
        return -sign;
      }
    }
  }

  private static int swap(final double[][] m, final int sign) {
    if (0.0 < m[0][1]) {
      if (0.0 < m[1][1]) {
        if (m[0][1] > m[1][1]) {
          swap(m, 1, 1);
          return -sign;
        }
      } else {
        if (m[0][1] <= -m[1][1]) {
          invert(m[1]);
          return -sign;
        }
        swap(m, 1, -1);
        return sign;
      }
      return sign;
    }
    if (0.0 < m[1][1]) {
      if (-m[0][1] <= m[1][1]) {
        invert(m[0]);
        return -sign;
      }
      swap(m, -1, 1);
      return sign;
    }
    if (m[0][1] >= m[1][1]) {
      invert(m[0]);
      invert(m[1]);
      return sign;
    }
    swap(m, -1, -1);
    return -sign;
  }

  private static void swap(final double[][] m, final int sign0, final int sign1) {
    double swap = sign0 * m[0][0];
    m[0][0] = sign1 * m[1][0];
    m[1][0] = swap;
    swap = sign0 * m[0][1];
    m[0][1] = sign1 * m[1][1];
    m[1][1] = swap;
  }

  private static void invert(final double[] m) {
    m[0] = -m[0];
    m[1] = -m[1];
  }

  private static int signFor(final double a, final double b, final int sign) {
    if (a > 0) {
      return signFor(b, sign);
    }
    return signFor(b, -sign);
  }

  private static int signFor(final double b, final int sign) {
    if (b > 0) {
      return -sign;
    }
    return sign;
  }
}