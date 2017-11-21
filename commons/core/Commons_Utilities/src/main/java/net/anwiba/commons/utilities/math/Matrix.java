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

package net.anwiba.commons.utilities.math;

import java.util.Arrays;

public class Matrix {

  private final double[][] values;
  private final int n;
  private final int m;

  static public Matrix matrix(final double[][] values) {
    return new Matrix(values.length, values.length > 0 ? values[0].length : 0, values);
  }

  public Matrix(final int n, final int m, final double[][] values) {
    this.n = n;
    this.m = m;
    this.values = values;
  }

  public int n() {
    return this.n;
  }

  public int m() {
    return this.m;
  }

  public double[][] values() {
    return this.values;
  }

  public Matrix multiply(final double value) {
    return matrix(multiply(this.n, this.m, value, this.values));
  }

  public Matrix multiply(final Matrix other) {
    if (other.n != this.m) {
      throw new IllegalArgumentException();
    }
    return matrix(multiply(this.n, this.m, other.m, this.values, other.values));
  }

  static public double[][] multiply(
      final int n,
      final int m,
      final int p,
      final double[][] matrix,
      final double[][] other) {
    final double[][] result = new double[n][p];

    final double[] otherVector = new double[m];

    double sum = 0.0;
    for (int j = 0; j < p; j++) {
      for (int k = 0; k < m; k++) {
        otherVector[k] = other[k][j];
      }
      for (int i = 0; i < n; i++) {
        final double[] vector = matrix[i];
        sum = 0.0;
        for (int k = 0; k < m; k++) {
          sum += vector[k] * otherVector[k];
        }
        result[i][j] = sum;
      }
    }
    return result;
  }

  private void check(final Matrix other) {
    if (other.m != this.m || other.n != this.n) {
      throw new IllegalArgumentException();
    }
  }

  public Matrix summate(final double value) {
    return matrix(summate(this.n, this.m, this.values, value));
  }

  public Matrix summate(final Matrix other) {
    check(other);
    return matrix(summate(this.n, this.m, this.values, other.values));
  }

  public Matrix subtract(final double value) {
    return matrix(subtract(this.n, this.m, this.values, value));
  }

  public Matrix subtract(final Matrix other) {
    check(other);
    return matrix(subtract(this.n, this.m, this.values, other.values));
  }

  static public double[][] copy(final int n, final int m, final double[][] source) {
    final double[][] result = new double[n][m];
    copy(n, m, source, result);
    return result;
  }

  public static void copy(final int n, final int m, final double[][] source, final double[][] target) {
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        target[i][j] = source[i][j];
      }
    }
  }

  static public double[][] identityZ(final int n) {
    final double[][] result = new double[n][n];
    final double d = -1.0 / n;
    final double e = 1.0 + d;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        result[i][j] = (i != j) ? d : e;
      }
    }
    return result;
  }

  static public double[][] identity(final int n) {
    final double[][] result = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        result[i][j] = (i != j) ? 0 : 1;
      }
    }
    return result;
  }

  static public double[][] transpose(final double[][] source) {
    return transpose(source.length, source[0].length, source);
  }

  static public double[][] transpose(final int n, final int m, final double[][] source) {
    final double[][] result = new double[m][n];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        result[i][j] = source[j][i];
      }
    }
    return result;
  }

  static public double[][] multiply(final int n, final int m, final double scal, final double[][] source) {
    final double[][] result = new double[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        result[i][j] = scal * source[i][j];
      }
    }
    return result;
  }

  static public double[] multiply(final int n, final int m, final double[][] matrix, final double[] vector) {
    final double[] result = new double[n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        result[i] += matrix[i][j] * vector[j];
      }
    }
    return result;
  }

  static public double[][] summate(final int n, final int m, final double[][] matrix, final double[][] other) {
    final double[][] result = new double[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        result[i][j] = matrix[i][j] + other[i][j];
      }
    }
    return result;
  }

  static public double[][] summate(final int n, final int m, final double[][] matrix, final double value) {
    final double[][] result = new double[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        result[i][j] = matrix[i][j] + value;
      }
    }
    return result;
  }

  static public double[][] subtract(final int n, final int m, final double[][] matrix, final double[][] other) {
    final double[][] result = new double[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        result[i][j] = matrix[i][j] - other[i][j];
      }
    }
    return result;
  }

  static public double[][] subtract(final int n, final int m, final double[][] matrix, final double value) {
    final double[][] result = new double[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        result[i][j] = matrix[i][j] - value;
      }
    }
    return result;
  }

  static public double trace(final int n, final int m, final double[][] matrix) {
    double result = 0.0;
    for (int i = 0; i < Math.min(n, m); i++) {
      result += matrix[i][i];
    }
    return (result);
  }

  public static void leftHandAccumulation(final int m, final int n, final double[][] p, final double[] d) {
    int l = 0;
    double g = 0.0;
    for (int i = n - 1; i >= 0; i--) {
      l = i + 1;
      g = d[i];
      if (i < n - 1) {
        for (int j = l; j < n; j++) {
          p[i][j] = 0.0;
        }
      }
      if (g != 0) {
        g = 1.0 / g;
        if (i != n - 1) {
          for (int j = l; j < n; j++) {
            double s = 0.0;
            for (int k = l; k < m; k++) {
              s += p[k][i] * p[k][j];
            }
            final double f = (s / p[i][i]) * g;
            for (int k = i; k < m; k++) {
              p[k][j] += f * p[k][i];
            }
          }
        }
        for (int j = i; j < m; j++) {
          p[j][i] *= g;
        }
      } else {
        for (int j = i; j < m; j++) {
          p[j][i] = 0.0;
        }
      }
      ++p[i][i];
    }
  }

  public static void rightHandAccumulation(final int n, final double[][] p, final double[][] q, final double[] r) {
    int l = n;
    double g = 0.0;
    for (int i = n - 1; i >= 0; i--) {
      if (i < n - 1) {
        if (g != 0) {
          for (int j = l; j < n; j++) {
            q[j][i] = (p[i][j] / p[i][l]) / g;
          }
          for (int j = l; j < n; j++) {
            double s = 0.0;
            for (int k = l; k < n; k++) {
              s += p[i][k] * q[k][j];
            }
            for (int k = l; k < n; k++) {
              q[k][j] += s * q[k][i];
            }
          }
        }
        for (int j = l; j < n; j++) {
          q[i][j] = q[j][i] = 0.0;
        }
      }
      q[i][i] = 1.0;
      g = r[i];
      l = i;
    }
  }

  public static double householderReduction(
      final int m,
      final int n,
      final double[][] p,
      final double[] d,
      final double[] r) {
    double anorm = 0;
    int l = 0;
    double g = 0.0;
    double scale = 0;
    for (int i = 0; i < n; i++) {
      double s = 0.0;
      l = i + 1;
      r[i] = scale * g;
      g = scale = 0.0;
      if (i < m) {
        for (int k = i; k < m; k++) {
          scale += Math.abs(p[k][i]);
        }
        if (scale > 0) {
          for (int k = i; k < m; k++) {
            p[k][i] /= scale;
            s += p[k][i] * p[k][i];
          }
          double f = p[i][i];
          g = -Matrix.sign(Math.sqrt(s), f);
          final double h = f * g - s;
          p[i][i] = f - g;
          if (i != n - 1) {
            for (int j = l; j < n; j++) {
              s = 0.0;
              for (int k = i; k < m; k++) {
                s += p[k][i] * p[k][j];
              }
              f = s / h;
              for (int k = i; k < m; k++) {
                p[k][j] += f * p[k][i];
              }
            }
          }
          for (int k = i; k < m; k++) {
            p[k][i] *= scale;
          }
        }
      }
      d[i] = scale * g;
      g = s = scale = 0.0;
      if (i < m && i != n - 1) {
        for (int k = l; k < n; k++) {
          scale += Math.abs(p[i][k]);
        }
        if (scale != 0) {
          for (int k = l; k < n; k++) {
            p[i][k] /= scale;
            s += p[i][k] * p[i][k];
          }
          final double f = p[i][l];
          g = -Matrix.sign(Math.sqrt(s), f);
          final double h = f * g - s;
          p[i][l] = f - g;
          for (int k = l; k < n; k++) {
            r[k] = p[i][k] / h;
          }
          if (i != m - 1) {
            for (int j = l; j < m; j++) {
              s = 0.0;
              for (int k = l; k < n; k++) {
                s += p[j][k] * p[i][k];
              }
              for (int k = l; k < n; k++) {
                p[j][k] += s * r[k];
              }
            }
          }
          for (int k = l; k < n; k++) {
            p[i][k] *= scale;
          }
        }
      }
      anorm = Math.max(anorm, Math.abs(d[i]) + Math.abs(r[i]));
    }
    return anorm;
  }

  static double radius(final double u, final double v) {
    final double absU = Math.abs(u);
    final double absV = Math.abs(v);
    if (absU > absV) {
      final double w = absV / absU;
      return (absU * Math.sqrt(1. + w * w));
    }
    if (absV > 0) {
      final double w = absU / absV;
      return (absV * Math.sqrt(1. + w * w));
    }
    return 0.0;
  }

  static double sign(final double u, final double v) {
    return (v) >= 0.0 ? Math.abs(u) : -Math.abs(u);
  }

  public static void diagonalization(
      final int maxNumberOfIterations,
      final int m,
      final int n,
      final double[][] p,
      final double[] d,
      final double[][] q,
      final double[] r,
      final double anorm) {
    boolean flag = false;
    int nm = 0;
    int l = 0;
    for (int k = n - 1; k >= 0; k--) { /* loop over singlar values */
      for (int iteration = 0; iteration < maxNumberOfIterations; iteration++) { /* loop over allowed iterations */
        flag = true;
        for (l = k; l >= 0; l--) { /* test for splitting */
          nm = l - 1; /* note that r[l] is always zero */
          if (Math.abs(r[l]) + anorm == anorm) {
            flag = false;
            break;
          }
          if (Math.abs(d[nm]) + anorm == anorm) {
            break;
          }
        }
        if (flag) {
          /* cancellation of r[l], if l>1 */
          double s = 1.0;
          for (int i = l; i <= k; i++) {
            final double f = s * r[i];
            if (Math.abs(f) + anorm != anorm) {
              final double g = d[i];
              double h = radius(f, g);
              d[i] = h;
              h = 1.0 / h;
              s = (-f * h);
              foo(nm, i, m, p, g * h, s);
            }
          }
        }
        if (l == k) { /* convergence */
          if (d[k] < 0.0) {
            d[k] = -d[k];
            for (int j = 0; j < n; j++) {
              q[j][k] = (-q[j][k]);
            }
          }
          break;
        }
        if (iteration == maxNumberOfIterations) {
          // error("svd: No convergence in 30 svd iterations", non_fatal);
          return;
        }
        nm = k - 1;
        qrTransformation(l, k, n, m, nm, p, q, d, r);
      }
    }
  }

  public static void qrTransformation(
      final int l,
      final int k,
      final int n,
      final int m,
      final int k_1,
      final double[][] p,
      final double[][] q,
      final double[] d,
      final double[] r) {
    double x = d[l]; /* shift from bottom 2-by-2 minor */
    final double r_k = r[k];
    double f = ((d[k_1] - d[k]) * (d[k_1] + d[k]) + (r[k_1] - r_k) * (r[k_1] + r_k)) / (2.0 * r_k * d[k_1]);
    f = ((x - d[k]) * (x + d[k]) + r_k * ((d[k_1] / (f + sign(radius(f, 1.0), f))) - r_k)) / x;
    double c = 1.0;
    double s = 1.0;
    for (int j = l; j <= k_1; j++) {
      final int i = j + 1;
      double y = d[i];
      double h = s * r[i];
      double g = c * r[i];
      double z = radius(f, h);
      r[j] = z;
      c = f / z;
      s = h / z;
      f = x * c + g * s;
      g = g * c - x * s;
      h = y * s;
      y = y * c;
      foo(j, i, n, q, c, s);
      z = radius(f, h);
      d[j] = z; /* rotation can be arbitrary id z=0 */
      if (z != 0) {
        z = 1.0 / z;
        c = f * z;
        s = h * z;
      }
      f = (c * g) + (s * y);
      x = (c * y) - (s * g);
      foo(j, i, m, p, c, s);
    }
    r[l] = 0.0;
    r[k] = f;
    d[k] = x;
  }

  private static void foo(final int j, final int i, final int m, final double[][] p, final double c, final double s) {
    double x;
    double y;
    for (int k = 0; k < m; k++) {
      x = p[k][i];
      y = p[k][j];
      p[k][j] = y * c + x * s;
      p[k][i] = x * c - y * s;
    }
  }

  /*
   * a = p d q' to get p[m][n], diag d[n] and q[n][n].
   */
  static public double singularValueDecomposition(
      final int maxNumberOfIterations,
      final int m,
      final int n,
      final double[][] a,
      final double[][] p,
      final double[] d,
      final double[][] q) {
    final double[] r = new double[n];
    Matrix.copy(n, n, a, p);
    final double anorm = householderReduction(m, n, p, d, r);
    rightHandAccumulation(n, p, q, r);
    leftHandAccumulation(m, n, p, d);
    diagonalization(maxNumberOfIterations, m, n, p, d, q, r, anorm);
    return anorm;
  }

  public static double[] oneVector(final int m) {
    final double[] result = new double[m];
    Arrays.fill(result, 1.0);
    return result;
  }

  public static double[][] fill(final double[][] ds) {
    final int dim = Math.max(ds.length, ds[0].length);
    final double[][] result = new double[dim][dim];
    for (int i = 0; i < ds.length; i++) {
      for (int j = 0; j < ds[i].length; j++) {
        result[i][j] = ds[i][j];
      }
    }
    return result;
  }

  public double get(final int i, final int j) {
    return this.values[i][j];
  }

}
