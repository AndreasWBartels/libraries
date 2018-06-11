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
 
package net.anwiba.spatial.geometry;

import net.anwiba.commons.utilities.string.StringUtilities;

public enum GeometryType {
  POINT(BaseGeometryType.POINT, false) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitPoint();
    }
  },
  LINESTRING(BaseGeometryType.CURVE, false) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitLineString();
    }
  },
  LINEARRING(BaseGeometryType.CURVE, false) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitLinearRing();
    }
  },
  POLYGON(BaseGeometryType.POLYGON, false) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitPolygon();
    }
  },
  MULTIPOINT(BaseGeometryType.POINT, true) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitMultiPoint();
    }
  },
  MULTILINESTRING(BaseGeometryType.CURVE, true) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitMultiLineString();
    }
  },
  MULTIPOLYGON(BaseGeometryType.POLYGON, true) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitMultiPolygon();
    }
  },
  COLLECTION(BaseGeometryType.UNKNOWN, true) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitCollection();
    }
  },
  UNKNOWN(BaseGeometryType.UNKNOWN, true) {

    @Override
    public <T, E extends Exception> T accept(final IGeometryTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnknown();
    }
  };

  private final BaseGeometryType baseType;
  private final boolean isCollection;

  private GeometryType(final BaseGeometryType baseType, final boolean isCollection) {
    this.baseType = baseType;
    this.isCollection = isCollection;
  }

  public BaseGeometryType getBaseGeometryType() {
    return this.baseType;
  }

  public abstract <T, E extends Exception> T accept(IGeometryTypeVisitor<T, E> visitor) throws E;

  public boolean isCollection() {
    return this.isCollection;
  }

  public static GeometryType getByName(final String name) {
    final GeometryType[] values = values();
    for (final GeometryType value : values) {
      if (StringUtilities.equalsIgnoreCase(name, value.name())) {
        return value;
      }
    }
    if (StringUtilities.equalsIgnoreCase(name, "geometrycollection")) { //$NON-NLS-1$
      return GeometryType.COLLECTION;
    }
    return GeometryType.UNKNOWN;
  }
}