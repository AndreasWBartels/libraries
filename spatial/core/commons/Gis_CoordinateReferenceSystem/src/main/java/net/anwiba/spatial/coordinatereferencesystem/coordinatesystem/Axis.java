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
 
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.io.Serializable;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class Axis implements Serializable {

  private static final long serialVersionUID = -7621278443053657479L;

  private final String dimension;
  private final AxisOrientation orientation;

  public Axis(final String dimension, final String orientation) {
    this(dimension, AxisOrientation.valueOf(orientation.trim().toUpperCase()));
  }

  public Axis(final String dimension, final AxisOrientation orientation) {
    this.dimension = dimension.trim();
    this.orientation = orientation;
  }

  public String getName() {
    return this.dimension;
  }

  public AxisOrientation getOrientation() {
    return this.orientation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    final int result = ObjectUtilities.hashCode(1, prime, this.dimension);
    return ObjectUtilities.hashCode(result, prime, this.orientation);
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Axis)) {
      return false;
    }
    final Axis other = (Axis) obj;
    return StringUtilities.equalsIgnoreCase(this.dimension, other.getName()) //
        && ObjectUtilities.equals(this.orientation, other.getOrientation());
  }
}
