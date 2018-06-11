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

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;

public class Projection implements Serializable {

  private static final long serialVersionUID = -2787032102572737245L;
  public static final Projection CASSINI_SOLDNER = new Projection(ProjectionType.CASSINI_SOLDNER);
  public static final Projection GAUSS_KRUEGER = new Projection(ProjectionType.GAUSS_KRUGER);
  public static final Projection TRANSVERSE_MERCATOR = new Projection(ProjectionType.TRANSVERSE_MERCATOR);
  public static final Projection MERCATOR = new Projection(ProjectionType.MERCATOR);
  public static final Projection MERCATOR_1SP = new Projection(ProjectionType.MERCATOR_1SP);

  private final IProjectionType type;

  public Projection(final IProjectionType type) {
    Ensure.ensureArgumentNotNull(type);
    this.type = type;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(1, 31, this.type);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof Projection)) {
      return false;
    }
    final Projection other = (Projection) obj;
    return ObjectUtilities.equals(this.type, other.type);
  }

  public String getName() {
    return this.type.getName();
  }

  public String getPrintName() {
    return this.type.getPrintName();
  }

  public IProjectionType getProjectionType() {
    return this.type;
  }
}