/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2007 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("nls")
public enum ParameterName {

  AZIMUTH("AZIMUTH", "AZIMUTH_OF_INITIAL_LINE") {
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
  LONGITUDE_OF_ORIGIN("CENTRAL_MERIDIAN",
      "LONGITUDE_OF_CENTER",
      "LONGITUDE_OF_NATURAL_ORIGIN",
      "LONGITUDE_OF_FALSE_ORIGIN",
      "LONGITUDE_OF_ORIGIN",
      "NATORIGINLONG",
      "LONGITUDE_OF_PROJECTION_CENTRE",
      "INITIAL_LONGITUDE") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitLongitudeOfCenter();
    }
  },
  FALSE_EASTING("FALSE_EASTING",
      "EASTING_AT_FALSE_ORIGIN",
      "EASTING_AT_PROJECTION_CENTRE",
      "FALSEEASTING") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitFalseEasting();
    }
  },
  FALSE_NORTHING("FALSE_NORTHING",
      "NORTHING_AT_FALSE_ORIGIN",
      "NORTHING_AT_PROJECTION_CENTRE",
      "FALSENORTHING") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitFalseNorthing();
    }
  },
  LATITUDE_OF_ORIGIN("LATITUDE_OF_ORIGIN",
      "LATITUDE_OF_FALSE_ORIGIN",
      "LATITUDE_OF_NATURAL_ORIGIN",
      "LATITUDE_OF_CENTER",
      "NATORIGINLAT",
      "LATITUDE_OF_TRUE_SCALE",
      "LATITUDE_OF_PROJECTION_CENTRE",
      "LATITUDE_OF_STANDARD_PARALLEL") {
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
  RECTIFIED_GRID_ANGLE("RECTIFIED_GRID_ANGLE", "ANGLE_FROM_RECTIFIED_TO_SKEW_GRID") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitRectifiedGridAngle();
    }
  },
  SCALE_FACTOR("SCALE_FACTOR",
      "SCALE_FACTOR_AT_NATURAL_ORIGIN",
      "SCALE_FACTOR_ON_INITIAL_LINE",
      "SCALEATNATORIGIN",
      "SCALEATCENTER",
      "ELLIPSOID_SCALING_FACTOR") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitScaleFactor();
    }
  },
  STANDARD_PARALLEL_1("STANDARD_PARALLEL_1", "LATITUDE_OF_1ST_STANDARD_PARALLEL", "STDPARALLEL1") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitStandardParallel1();
    }
  },
  STANDARD_PARALLEL_2("STANDARD_PARALLEL_2", "LATITUDE_OF_2ND_STANDARD_PARALLEL", "STDPARALLEL2") {
    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitStandardParallel2();
    }
  },
  SEMI_MAJOR("SEMI_MAJOR", "SEMI-MAJOR_AXIS") {

    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitSemiMajor();
    }
  },
  SEMI_MINOR("SEMI_MINOR", "SEMI-MINOR_AXIS") {

    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitSemiMinor();
    }
  },
  ZONE_WIDTH("ZONE_WIDTH") {

    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitZoneWidth();
    }
  },
  UNKOWN() {

    @Override
    public void accept(final IParameterValueVisitor visitor) {
      visitor.visitUnkown();
    }
  };

  private final Set<String> names;

  private ParameterName(final String... names) {
    this.names = new HashSet<>(Arrays.asList(names));
  }

  public abstract void accept(IParameterValueVisitor visitor);

  public static ParameterName byName(final String name) {
    final String preparedName = name.toUpperCase().replaceAll(" ", "_");
    for (final ParameterName valueType : values()) {
      if (valueType.names.contains(preparedName)) {
        return valueType;
      }
    }
    return ParameterName.UNKOWN;
  }
}
