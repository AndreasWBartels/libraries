/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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

public class DoubleArrays {

  public static double[] copy(final double[] values) {
    return copy(values, 0, values.length);
  }

  public static double[] copy(final double[] values, int startIndex, int numberOfValues) {
    final double[] result = new double[numberOfValues];
    System.arraycopy(values, startIndex, result, 0, numberOfValues);
    return result;
  }

  public static double[][] copy(final double[][] values) {
    final double[][] result = new double[values.length][];
    System.arraycopy(values, 0, result, 0, values.length);
    return result;
  }
}
