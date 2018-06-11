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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("nls")
public enum ParameterName {

  AZIMUTH("AZIMUTH", "AZIMUTH OF INITIAL LINE") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitAzimuth();
    }
  },
  AUXILIARY_SPHERE_TYPE("AUXILIARY_SPHERE_TYPE") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitAuxiliarySphereType();
    }
  },
  LONGITUDE_OF_ORIGIN("CENTRAL_MERIDIAN", "LONGITUDE_OF_CENTER", "LONGITUDE OF NATURAL ORIGIN",
      "LONGITUDE OF FALSE ORIGIN", "LONGITUDE_OF_ORIGIN", "NATORIGINLONG", "LONGITUDE OF PROJECTION CENTRE") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitLongitudeOfCenter();
    }
  },
  FALSE_EASTING("FALSE_EASTING", "FALSE EASTING", "EASTING AT FALSE ORIGIN", "EASTING AT PROJECTION CENTRE",
      "FALSEEASTING") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitFalseEasting();
    }
  },
  FALSE_NORTHING("FALSE_NORTHING", "FALSE NORTHING", "NORTHING AT FALSE ORIGIN", "NORTHING AT PROJECTION CENTRE",
      "FALSENORTHING") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitFalseNorthing();
    }
  },
  LATITUDE_OF_ORIGIN("LATITUDE_OF_ORIGIN", "LATITUDE OF FALSE ORIGIN", "LATITUDE OF NATURAL ORIGIN",
      "LATITUDE_OF_CENTER", "NATORIGINLAT", "LATITUDE_OF_TRUE_SCALE", "LATITUDE OF PROJECTION CENTRE") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitLatitudeOfOrigin();
    }
  },
  LATITUDE_OF_POINT_1("LATITUDE_OF_POINT_1") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitLatitudeOfPoint1();
    }
  },
  LATITUDE_OF_POINT_2("LATITUDE_OF_POINT_2") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitLatitudeOfPoint2();
    }
  },
  LONGITUDE_OF_POINT_1("LONGITUDE_OF_POINT_1") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitLongitudeOfPoint1();
    }
  },
  LONGITUDE_OF_POINT_2("LONGITUDE_OF_POINT_2") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitLongitudeOfPoint2();
    }
  },
  RECTIFIED_GRID_ANGLE("RECTIFIED_GRID_ANGLE", "ANGLE FROM RECTIFIED TO SKEW GRID") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitRectifiedGridAngle();
    }
  },
  SCALE_FACTOR("SCALE_FACTOR", "SCALE FACTOR AT NATURAL ORIGIN", "SCALE FACTOR ON INITIAL LINE", "SCALEATNATORIGIN",
      "SCALEATCENTER") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitScaleFactor();
    }
  },
  STANDARD_PARALLEL_1("STANDARD_PARALLEL_1", "LATITUDE OF 1ST STANDARD PARALLEL", "STDPARALLEL1") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitStandardParallel1();
    }
  },
  STANDARD_PARALLEL_2("STANDARD_PARALLEL_2", "LATITUDE OF 2ND STANDARD PARALLEL", "STDPARALLEL2") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitStandardParallel2();
    }
  },
  SEMI_MAJOR("SEMI_MAJOR", "SEMI-MAJOR AXIS") {

    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitSemiMajor();
    }
  },
  SEMI_MINOR("SEMI_MINOR", "SEMI-MINOR AXIS") {

    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitSemiMinor();
    }
  };

  private final Set<String> names;

  private ParameterName(final String... names) {
    this.names = new HashSet<>(Arrays.asList(names));
  }

  public abstract void accept(IParameterValueVisitor visitor);

  public static ParameterName byName(final String name) {
    for (final ParameterName valueType : values()) {
      if (valueType.names.contains(name.toUpperCase())) {
        return valueType;
      }
    }
    return null;
  }
}