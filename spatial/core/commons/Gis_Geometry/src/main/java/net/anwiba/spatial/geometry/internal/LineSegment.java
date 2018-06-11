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
package net.anwiba.spatial.geometry.internal;

import net.anwiba.spatial.coordinate.Envelope;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.geometry.ILineSegment;

public class LineSegment implements ILineSegment {

  private final ICoordinate startPoint;
  private final ICoordinate endPoint;

  public LineSegment(final ICoordinate startPoint, final ICoordinate endPoint) {
    this.startPoint = startPoint;
    this.endPoint = endPoint;
  }

  @Override
  public ICoordinate getStartPoint() {
    return this.startPoint;
  }

  @Override
  public ICoordinate getEndPoint() {
    return this.endPoint;
  }

  @Override
  public String toString() {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("["); //$NON-NLS-1$
    boolean cordinateFlag = false;
    boolean ordinateFlag = false;
    for (final ICoordinate coordinate : new ICoordinate[]{ this.startPoint, this.endPoint }) {
      if (cordinateFlag) {
        buffer.append("; "); //$NON-NLS-1$
      }
      cordinateFlag = true;
      final double[] values = coordinate.getValues();
      ordinateFlag = false;
      for (final double value : values) {
        if (ordinateFlag) {
          buffer.append(", "); //$NON-NLS-1$
        }
        ordinateFlag = true;
        buffer.append(value);
      }
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  @Override
  public IEnvelope getEnvelope() {
    return new Envelope( //
        new double[]{
            Math.min(this.startPoint.getXValue(), this.endPoint.getXValue()),
            Math.min(this.startPoint.getYValue(), this.endPoint.getYValue()) },//
        new double[]{
            Math.max(this.startPoint.getXValue(), this.endPoint.getXValue()),
            Math.max(this.startPoint.getYValue(), this.endPoint.getYValue()) },
        false);
  }
}
