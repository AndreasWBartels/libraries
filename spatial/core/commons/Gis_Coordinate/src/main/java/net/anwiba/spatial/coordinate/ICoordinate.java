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
 
package net.anwiba.spatial.coordinate;

import java.io.Serializable;

public interface ICoordinate extends Comparable<ICoordinate>, Serializable {

  public final static int X = 0;
  public final static int Y = 1;
  public final static int Z = 2;

  public int getDimension();

  public boolean isMeasured();

  public double getXValue();

  public double getYValue();

  public double getZValue();

  public double getMeasuredValue();

  public double getValue(int index);

  public double[] getValues();

  public boolean touch(double x, double y);

  public boolean touch(ICoordinate coordinate);

  public ICoordinate add(ICoordinate coordinate);

  public ICoordinate subtract(ICoordinate coordinate);

}