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
// Copyright (c) 2009 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class Projection implements Serializable {

  private static final long serialVersionUID = -2787032102572737245L;
//  public static final Projection CASSINI_SOLDNER = new Projection(ProjectionType.CASSINI_SOLDNER);
//  public static final Projection GAUSS_KRUEGER = new Projection(ProjectionType.GAUSS_KRUGER);
//  public static final Projection TRANSVERSE_MERCATOR = new Projection(ProjectionType.TRANSVERSE_MERCATOR);
//  public static final Projection MERCATOR = new Projection(ProjectionType.MERCATOR);
//  public static final Projection MERCATOR_1SP = new Projection(ProjectionType.MERCATOR_1SP);

  private final IProjectionType type;
  private final Area area;
  private final Authority authority;

  private double azimuth = Double.NaN;
  private double rectifiedGridAngle = Double.NaN;
  private double auxiliarySphereType = Double.NaN;
  private double falseEasting = 0;
  private double falseNorthing = 0;
  private double longitudeOfOrigin = 0;
  private double longitudeOfPoint1 = Double.NaN;
  private double longitudeOfPoint2 = Double.NaN;
  private double latitudeOfOrigin = 0;
  private double latitudeOfPoint1 = Double.NaN;
  private double latitudeOfPoint2 = Double.NaN;
  private double scaleFactor = 1;
  private double standardParallel1 = Double.NaN;
  private double standardParallel2 = Double.NaN;
  private double semiMajor = Double.NaN;
  private double semiMinor = Double.NaN;
  private double zoneWidth = Double.NaN;

  private final Map<ParameterName, String> labels = new HashMap<>();
  private final Map<String, Double> unkownParameters = new HashMap<>();

  public Projection(final IProjectionType type, final Parameter[] parameters) {
    this(type, parameters, null, null);
  }

  public Projection(final IProjectionType type,
      final Parameter[] parameters,
      final Area area,
      final Authority authority) {
    Ensure.ensureArgumentNotNull(type);
    this.type = type;
    this.area = area;
    this.authority = authority;
    for (final Parameter parameter : parameters) {
      setParameterValue(parameter.getName(), parameter.getValue());
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ObjectUtilities.hashCode(1, prime, this.type);
    result = ObjectUtilities.hashCode(result, prime, this.azimuth);
    result = ObjectUtilities.hashCode(result, prime, this.longitudeOfOrigin);
    result = ObjectUtilities.hashCode(result, prime, this.latitudeOfOrigin);
    result = ObjectUtilities.hashCode(result, prime, this.falseEasting);
    result = ObjectUtilities.hashCode(result, prime, this.falseNorthing);
    result = ObjectUtilities.hashCode(result, prime, this.scaleFactor);
    return result;

  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof Projection)) {
      return false;
    }
    final Projection other = (Projection) obj;
    return ObjectUtilities.equals(this.type, other.type)
        && equals(this.azimuth, other.azimuth)
        && equals(this.falseEasting, other.falseEasting)
        && equals(this.falseNorthing, other.falseNorthing)
        && equals(this.latitudeOfOrigin, other.latitudeOfOrigin)
        && equals(this.latitudeOfPoint1, other.latitudeOfPoint1)
        && equals(this.latitudeOfPoint2, other.latitudeOfPoint2)
        && equals(this.longitudeOfOrigin, other.longitudeOfOrigin)
        && equals(this.longitudeOfPoint1, other.longitudeOfPoint1)
        && equals(this.longitudeOfPoint2, other.longitudeOfPoint2)
        && equals(this.rectifiedGridAngle, other.rectifiedGridAngle)
        && equals(this.scaleFactor, other.scaleFactor)
        && equals(this.semiMajor, other.semiMajor)
        && equals(this.semiMinor, other.semiMinor)
        && equals(this.standardParallel1, other.standardParallel1)
        && equals(this.standardParallel2, other.standardParallel2);
  }

  private boolean equals(final double value, final double other) {
    return (Double.isNaN(value) && Double.isNaN(value))
        || (Double.isInfinite(value) && Double.isInfinite(value))
        || value == other;
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

  public Area getArea() {
    return this.area;
  }

  public Authority getAuthority() {
    return this.authority;
  }

  private void setParameterValue(final String name, final double value) {
    final ParameterName valueType = ParameterName.byName(name);
    if (valueType != null) {
      final IParameterValueVisitor visitor = new IParameterValueVisitor() {

        @Override
        public void visitAzimuth() {
          Projection.this.labels.put(ParameterName.AZIMUTH, name);
          Projection.this.azimuth = value;
        }

        @Override
        public void visitAuxiliarySphereType() {
          Projection.this.labels.put(ParameterName.AUXILIARY_SPHERE_TYPE, name);
          Projection.this.auxiliarySphereType = value;
        }

        @Override
        public void visitFalseEasting() {
          Projection.this.labels.put(ParameterName.FALSE_EASTING, name);
          Projection.this.falseEasting = value;
        }

        @Override
        public void visitFalseNorthing() {
          Projection.this.labels.put(ParameterName.FALSE_NORTHING, name);
          Projection.this.falseNorthing = value;
        }

        @Override
        public void visitLatitudeOfOrigin() {
          Projection.this.labels.put(ParameterName.LATITUDE_OF_ORIGIN, name);
          Projection.this.latitudeOfOrigin = value;
        }

        @Override
        public void visitLatitudeOfPoint1() {
          Projection.this.labels.put(ParameterName.LATITUDE_OF_POINT_1, name);
          Projection.this.latitudeOfPoint1 = value;
        }

        @Override
        public void visitLatitudeOfPoint2() {
          Projection.this.labels.put(ParameterName.LATITUDE_OF_POINT_2, name);
          Projection.this.latitudeOfPoint2 = value;
        }

        @Override
        public void visitLongitudeOfCenter() {
          Projection.this.labels.put(ParameterName.LONGITUDE_OF_ORIGIN, name);
          Projection.this.longitudeOfOrigin = value;
        }

        @Override
        public void visitLongitudeOfPoint1() {
          Projection.this.labels.put(ParameterName.LONGITUDE_OF_POINT_1, name);
          Projection.this.longitudeOfPoint1 = value;
        }

        @Override
        public void visitLongitudeOfPoint2() {
          Projection.this.labels.put(ParameterName.LONGITUDE_OF_POINT_2, name);
          Projection.this.longitudeOfPoint2 = value;
        }

        @Override
        public void visitScaleFactor() {
          Projection.this.labels.put(ParameterName.SCALE_FACTOR, name);
          Projection.this.scaleFactor = value;
        }

        @Override
        public void visitStandardParallel1() {
          Projection.this.labels.put(ParameterName.STANDARD_PARALLEL_1, name);
          Projection.this.standardParallel1 = value;
        }

        @Override
        public void visitStandardParallel2() {
          Projection.this.labels.put(ParameterName.STANDARD_PARALLEL_2, name);
          Projection.this.standardParallel2 = value;
        }

        @Override
        public double getResult() {
          throw new UnreachableCodeReachedException();
        }

        @Override
        public void visitSemiMinor() {
          Projection.this.labels.put(ParameterName.SEMI_MINOR, name);
          Projection.this.semiMinor = value;
        }

        @Override
        public void visitSemiMajor() {
          Projection.this.labels.put(ParameterName.SEMI_MAJOR, name);
          Projection.this.semiMajor = value;
        }

        @Override
        public void visitRectifiedGridAngle() {
          Projection.this.labels.put(ParameterName.RECTIFIED_GRID_ANGLE, name);
          Projection.this.rectifiedGridAngle = value;
        }

        @Override
        public void visitZoneWidth() {
          Projection.this.labels.put(ParameterName.ZONE_WIDTH, name);
          Projection.this.zoneWidth = value;
        }

        @Override
        public void visitUnkown() {
          Projection.this.unkownParameters.put(name, Double.valueOf(value));
        }

      };
      valueType.accept(visitor);
    }
  }

  public double getParameterValue(final String name) {
    if (!this.labels.containsKey(ParameterName.byName(name))) {
      return Double.NaN;
    }
    final IParameterValueVisitor visitor = new IParameterValueVisitor() {

      private double value = Double.NaN;

      @Override
      public void visitAzimuth() {
        this.value = Projection.this.azimuth;
      }

      @Override
      public void visitAuxiliarySphereType() {
        this.value = Projection.this.auxiliarySphereType;
      }

      @Override
      public void visitFalseEasting() {
        this.value = Projection.this.falseEasting;
      }

      @Override
      public void visitFalseNorthing() {
        this.value = Projection.this.falseNorthing;
      }

      @Override
      public void visitLatitudeOfOrigin() {
        this.value = Projection.this.latitudeOfOrigin;
      }

      @Override
      public void visitLatitudeOfPoint1() {
        this.value = Projection.this.latitudeOfPoint1;
      }

      @Override
      public void visitLatitudeOfPoint2() {
        this.value = Projection.this.latitudeOfPoint2;
      }

      @Override
      public void visitLongitudeOfCenter() {
        this.value = Projection.this.longitudeOfOrigin;
      }

      @Override
      public void visitLongitudeOfPoint1() {
        this.value = Projection.this.longitudeOfPoint1;
      }

      @Override
      public void visitLongitudeOfPoint2() {
        this.value = Projection.this.longitudeOfPoint2;
      }

      @Override
      public void visitRectifiedGridAngle() {
        this.value = Projection.this.rectifiedGridAngle;
      }

      @Override
      public void visitScaleFactor() {
        this.value = Projection.this.scaleFactor;
      }

      @Override
      public void visitStandardParallel1() {
        this.value = Projection.this.standardParallel1;
      }

      @Override
      public void visitStandardParallel2() {
        this.value = Projection.this.standardParallel2;
      }

      @Override
      public double getResult() {
        return this.value;
      }

      @Override
      public void visitSemiMinor() {
        this.value = Projection.this.semiMinor;
      }

      @Override
      public void visitSemiMajor() {
        this.value = Projection.this.semiMajor;
      }

      @Override
      public void visitZoneWidth() {
        this.value = Projection.this.zoneWidth;
      }

      @Override
      public void visitUnkown() {
        this.value = Projection.this.unkownParameters.getOrDefault(name, Double.valueOf(Double.NaN))
            .doubleValue();
      }
    };
    final ParameterName valueType = ParameterName.byName(name);
    if (valueType != null) {
      valueType.accept(visitor);
    }
    return visitor.getResult();
  }

  public double getAzimuth() {
    return this.azimuth;
  }

  public double getRectifiedGridAngle() {
    return this.rectifiedGridAngle;
  }

  public double getCentralMeridian() {
    return this.longitudeOfOrigin;
  }

  public double getLongitudeOfOrigin() {
    return this.longitudeOfOrigin;
  }

  public double getLongitudeOfPoint1() {
    return this.longitudeOfPoint1;
  }

  public double getLongitudeOfPoint2() {
    return this.longitudeOfPoint2;
  }

  public double getLatitudeOfOrigin() {
    return this.latitudeOfOrigin;
  }

  public double getLatitudeOfPoint1() {
    return this.latitudeOfPoint1;
  }

  public double getLatitudeOfPoint2() {
    return this.latitudeOfPoint2;
  }

  public double getStandardParallel1() {
    return this.standardParallel1;
  }

  public double getStandardParallel2() {
    return this.standardParallel2;
  }

  public double getFalseEasting() {
    return this.falseEasting;
  }

  public double getFalseNorthing() {
    return this.falseNorthing;
  }

  public double getScaleFactor() {
    return this.scaleFactor;
  }

  public double getZoneWidth() {
    return this.zoneWidth;
  }

  public Parameter[] getParameters() {
    final List<Parameter> parameters = new ArrayList<>();
    addParameter(parameters, ParameterName.AZIMUTH, this.azimuth);
    addParameter(parameters, ParameterName.SEMI_MAJOR, this.semiMajor);
    addParameter(parameters, ParameterName.SEMI_MINOR, this.semiMinor);
    addParameter(parameters, ParameterName.FALSE_EASTING, this.falseEasting);
    addParameter(parameters, ParameterName.FALSE_NORTHING, this.falseNorthing);
    addParameter(parameters, ParameterName.LATITUDE_OF_ORIGIN, this.latitudeOfOrigin);
    addParameter(parameters, ParameterName.LATITUDE_OF_POINT_1, this.latitudeOfPoint1);
    addParameter(parameters, ParameterName.LATITUDE_OF_POINT_2, this.latitudeOfPoint2);
    addParameter(parameters, ParameterName.LONGITUDE_OF_ORIGIN, this.longitudeOfOrigin);
    addParameter(parameters, ParameterName.LONGITUDE_OF_POINT_1, this.longitudeOfPoint1);
    addParameter(parameters, ParameterName.LONGITUDE_OF_POINT_2, this.longitudeOfPoint2);
    addParameter(parameters, ParameterName.RECTIFIED_GRID_ANGLE, this.rectifiedGridAngle);
    addParameter(parameters, ParameterName.SCALE_FACTOR, this.scaleFactor);
    addParameter(parameters, ParameterName.STANDARD_PARALLEL_1, this.standardParallel1);
    addParameter(parameters, ParameterName.STANDARD_PARALLEL_2, this.standardParallel2);
    addParameter(parameters, ParameterName.ZONE_WIDTH, this.zoneWidth);
    this.unkownParameters.forEach((n, v) -> parameters.add(new Parameter(n, v.intValue())));
    return parameters.toArray(new Parameter[parameters.size()]);
  }

  private void addParameter(
      final List<Parameter> parameters,
      final ParameterName name,
      final double value) {
    if (!this.labels.containsKey(name) || Double.isNaN(value)) {
      return;
    }
    final String label = this.labels.get(name);
    parameters.add(new Parameter(label, value));
  }

  public double getSemiMajor() {
    return this.semiMajor;
  }

  public double getSemiMinor() {
    return this.semiMinor;
  }

}
