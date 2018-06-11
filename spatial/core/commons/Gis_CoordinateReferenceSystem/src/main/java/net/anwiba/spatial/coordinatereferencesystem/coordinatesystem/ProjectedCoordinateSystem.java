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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public class ProjectedCoordinateSystem extends AbstractCoordinateSystem implements IProjectedCoordinateSystem {

  private static final long serialVersionUID = -1L;

  private final GeographicCoordinateSystem sphericalCoordinateSystem;
  private final Projection projection;

  private double azimuth = Double.NaN;
  private double rectifiedGridAngle = Double.NaN;
  private double auxiliarySphereType = Double.NaN;
  private double falseEasting = 0;
  private double falseNorthing = 0;
  private double longitudeOfOrigin = 0;
  private double longitudeOfPoint1 = Double.NaN;
  private double longitudeOfPoint2 = Double.NaN;
  private double latitudeOfOrigin = 0;
  private final double latitudeOfTrueScale = Double.NaN;
  private double latitudeOfPoint1 = Double.NaN;
  private double latitudeOfPoint2 = Double.NaN;
  private double scaleFactor = 1;
  private double standardParallel1 = Double.NaN;
  private double standardParallel2 = Double.NaN;
  private double semiMajor = Double.NaN;
  private double semiMinor = Double.NaN;

  private final Map<ParameterName, String> labels = new HashMap<>();

  public ProjectedCoordinateSystem(
      final Authority authority,
      final String name,
      final GeographicCoordinateSystem sphericalCoordinateSystem,
      final Projection projection,
      final Parameter[] parameters,
      final Unit unit,
      final Axis... axises) {
    this(authority, name, sphericalCoordinateSystem, projection, parameters, null, unit, axises);
  }

  public ProjectedCoordinateSystem(
      final Authority authority,
      final String name,
      final GeographicCoordinateSystem sphericalCoordinateSystem,
      final Projection projection,
      final Parameter[] parameters,
      final Area area,
      final Unit unit,
      final Axis... axises) {
    this(authority, name, sphericalCoordinateSystem, projection, parameters, area, unit, new ArrayList<>(), axises);
  }

  public ProjectedCoordinateSystem(
      final Authority authority,
      final String name,
      final GeographicCoordinateSystem sphericalCoordinateSystem,
      final Projection projection,
      final Parameter[] parameters,
      final Area area,
      final Unit unit,
      final List<Extension> extensions,
      final Axis... axises) {
    super(authority, name, area, unit, extensions, axises);
    Ensure.ensureArgumentNotNull(sphericalCoordinateSystem);
    Ensure.ensureArgumentNotNull(projection);
    Ensure.ensureArgumentNotNull(parameters);
    this.sphericalCoordinateSystem = sphericalCoordinateSystem;
    this.projection = projection;
    for (final Parameter parameter : parameters) {
      setParameterValue(parameter.getName(), parameter.getValue());
    }
  }

  @Override
  public GeographicCoordinateSystem getGeographicCoordinateSystem() {
    return this.sphericalCoordinateSystem;
  }

  @Override
  public Projection getProjection() {
    return this.projection;
  }

  @Override
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
    return parameters.toArray(new Parameter[parameters.size()]);
  }

  private void addParameter(final List<Parameter> parameters, final ParameterName name, final double value) {
    if (!this.labels.containsKey(name) || Double.isNaN(value)) {
      return;
    }
    final String label = this.labels.get(name);
    parameters.add(new Parameter(label, value));
  }

  @Override
  public ICoordinateSystemType getCoordinateSystemType() {
    return CoordinateSystemType.PROJECTED;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ProjectedCoordinateSystem)) {
      return false;
    }
    final ProjectedCoordinateSystem other = (ProjectedCoordinateSystem) obj;
    return super.equals(other)
        && ObjectUtilities.equals(this.sphericalCoordinateSystem, other.sphericalCoordinateSystem)
        && ObjectUtilities.equals(this.projection, other.projection)
        && ObjectUtilities.equals(this.azimuth, other.azimuth)
        && ObjectUtilities.equals(this.falseEasting, other.falseEasting)
        && ObjectUtilities.equals(this.falseNorthing, other.falseNorthing)
        && ObjectUtilities.equals(this.latitudeOfOrigin, other.latitudeOfOrigin)
        && ObjectUtilities.equals(this.latitudeOfPoint1, other.latitudeOfPoint1)
        && ObjectUtilities.equals(this.latitudeOfPoint2, other.latitudeOfPoint2)
        && ObjectUtilities.equals(this.longitudeOfOrigin, other.longitudeOfOrigin)
        && ObjectUtilities.equals(this.longitudeOfPoint1, other.longitudeOfPoint1)
        && ObjectUtilities.equals(this.longitudeOfPoint2, other.longitudeOfPoint2)
        && ObjectUtilities.equals(this.rectifiedGridAngle, other.rectifiedGridAngle)
        && ObjectUtilities.equals(this.scaleFactor, other.scaleFactor)
        && ObjectUtilities.equals(this.semiMajor, other.semiMajor)
        && ObjectUtilities.equals(this.semiMinor, other.semiMinor)
        && ObjectUtilities.equals(this.standardParallel1, other.standardParallel1)
        && ObjectUtilities.equals(this.standardParallel2, other.standardParallel2);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ObjectUtilities.hashCode(1, prime, this.sphericalCoordinateSystem);
    result = ObjectUtilities.hashCode(result, prime, this.projection);
    result = ObjectUtilities.hashCode(result, prime, this.azimuth);
    result = ObjectUtilities.hashCode(result, prime, this.longitudeOfOrigin);
    result = ObjectUtilities.hashCode(result, prime, this.latitudeOfOrigin);
    result = ObjectUtilities.hashCode(result, prime, this.falseEasting);
    result = ObjectUtilities.hashCode(result, prime, this.falseNorthing);
    result = ObjectUtilities.hashCode(result, prime, this.scaleFactor);
    return result;
  }

  @Override
  public double getAzimuth() {
    return this.azimuth;
  }

  @Override
  public double getRectifiedGridAngle() {
    return this.rectifiedGridAngle;
  }

  @Override
  public double getCentralMeridian() {
    return this.longitudeOfOrigin;
  }

  @Override
  public double getLongitudeOfOrigin() {
    return this.longitudeOfOrigin;
  }

  @Override
  public double getLongitudeOfPoint1() {
    return this.longitudeOfPoint1;
  }

  @Override
  public double getLongitudeOfPoint2() {
    return this.longitudeOfPoint2;
  }

  @Override
  public double getLatitudeOfOrigin() {
    return this.latitudeOfOrigin;
  }

  @Override
  public double getLatitudeOfPoint1() {
    return this.latitudeOfPoint1;
  }

  @Override
  public double getLatitudeOfPoint2() {
    return this.latitudeOfPoint2;
  }

  @Override
  public double getStandardParallel1() {
    return this.standardParallel1;
  }

  @Override
  public double getStandardParallel2() {
    return this.standardParallel2;
  }

  @Override
  public double getFalseEasting() {
    return this.falseEasting;
  }

  @Override
  public double getFalseNorthing() {
    return this.falseNorthing;
  }

  @Override
  public double getScaleFactor() {
    return this.scaleFactor;
  }

  @Override
  public double getSemiMajor() {
    if (Double.isNaN(this.semiMajor) && this.sphericalCoordinateSystem.getDatum().getSpheroid() != null) {
      return this.sphericalCoordinateSystem.getDatum().getSpheroid().getSemiMajorAxis();
    }
    return this.semiMajor;
  }

  @Override
  public double getSemiMinor() {
    if (Double.isNaN(this.semiMinor) && this.sphericalCoordinateSystem.getDatum().getSpheroid() != null) {
      return this.sphericalCoordinateSystem.getDatum().getSpheroid().getSemiMinorAxis();
    }
    return this.semiMinor;
  }

  private void setParameterValue(final String name, final double value) {
    final ParameterName valueType = ParameterName.byName(name);
    if (valueType != null) {
      final IParameterValueVisitor visitor = new IParameterValueVisitor() {

        @Override
        public void visitAzimuth() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.AZIMUTH, name);
          ProjectedCoordinateSystem.this.azimuth = value;
        }

        @Override
        public void visitAuxiliarySphereType() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.AUXILIARY_SPHERE_TYPE, name);
          ProjectedCoordinateSystem.this.auxiliarySphereType = value;
        }

        @Override
        public void visitFalseEasting() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.FALSE_EASTING, name);
          ProjectedCoordinateSystem.this.falseEasting = value;
        }

        @Override
        public void visitFalseNorthing() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.FALSE_NORTHING, name);
          ProjectedCoordinateSystem.this.falseNorthing = value;
        }

        @Override
        public void visitLatitudeOfOrigin() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.LATITUDE_OF_ORIGIN, name);
          ProjectedCoordinateSystem.this.latitudeOfOrigin = value;
        }

        @Override
        public void visitLatitudeOfPoint1() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.LATITUDE_OF_POINT_1, name);
          ProjectedCoordinateSystem.this.latitudeOfPoint1 = value;
        }

        @Override
        public void visitLatitudeOfPoint2() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.LATITUDE_OF_POINT_2, name);
          ProjectedCoordinateSystem.this.latitudeOfPoint2 = value;
        }

        @Override
        public void visitLongitudeOfCenter() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.LONGITUDE_OF_ORIGIN, name);
          ProjectedCoordinateSystem.this.longitudeOfOrigin = value;
        }

        @Override
        public void visitLongitudeOfPoint1() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.LONGITUDE_OF_POINT_1, name);
          ProjectedCoordinateSystem.this.longitudeOfPoint1 = value;
        }

        @Override
        public void visitLongitudeOfPoint2() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.LONGITUDE_OF_POINT_2, name);
          ProjectedCoordinateSystem.this.longitudeOfPoint2 = value;
        }

        @Override
        public void visitScaleFactor() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.SCALE_FACTOR, name);
          ProjectedCoordinateSystem.this.scaleFactor = value;
        }

        @Override
        public void visitStandardParallel1() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.STANDARD_PARALLEL_1, name);
          ProjectedCoordinateSystem.this.standardParallel1 = value;
        }

        @Override
        public void visitStandardParallel2() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.STANDARD_PARALLEL_2, name);
          ProjectedCoordinateSystem.this.standardParallel2 = value;
        }

        @Override
        public double getResult() {
          throw new UnreachableCodeReachedException();
        }

        @Override
        public void visitSemiMinor() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.SEMI_MINOR, name);
          ProjectedCoordinateSystem.this.semiMinor = value;
        }

        @Override
        public void visitSemiMajor() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.SEMI_MAJOR, name);
          ProjectedCoordinateSystem.this.semiMajor = value;
        }

        @Override
        public void visitRectifiedGridAngle() {
          ProjectedCoordinateSystem.this.labels.put(ParameterName.RECTIFIED_GRID_ANGLE, name);
          ProjectedCoordinateSystem.this.rectifiedGridAngle = value;
        }

      };
      valueType.accept(visitor);
    }
  }

  @Override
  public double getParameterValue(final String name) {
    if (!this.labels.containsKey(ParameterName.byName(name))) {
      return Double.NaN;
    }
    final IParameterValueVisitor visitor = new IParameterValueVisitor() {

      private double value = Double.NaN;

      @Override
      public void visitAzimuth() {
        this.value = ProjectedCoordinateSystem.this.azimuth;
      }

      @Override
      public void visitAuxiliarySphereType() {
        this.value = ProjectedCoordinateSystem.this.auxiliarySphereType;
      }

      @Override
      public void visitFalseEasting() {
        this.value = ProjectedCoordinateSystem.this.falseEasting;
      }

      @Override
      public void visitFalseNorthing() {
        this.value = ProjectedCoordinateSystem.this.falseNorthing;
      }

      @Override
      public void visitLatitudeOfOrigin() {
        this.value = ProjectedCoordinateSystem.this.latitudeOfOrigin;
      }

      @Override
      public void visitLatitudeOfPoint1() {
        this.value = ProjectedCoordinateSystem.this.latitudeOfPoint1;
      }

      @Override
      public void visitLatitudeOfPoint2() {
        this.value = ProjectedCoordinateSystem.this.latitudeOfPoint2;
      }

      @Override
      public void visitLongitudeOfCenter() {
        this.value = ProjectedCoordinateSystem.this.longitudeOfOrigin;
      }

      @Override
      public void visitLongitudeOfPoint1() {
        this.value = ProjectedCoordinateSystem.this.longitudeOfPoint1;
      }

      @Override
      public void visitLongitudeOfPoint2() {
        this.value = ProjectedCoordinateSystem.this.longitudeOfPoint2;
      }

      @Override
      public void visitRectifiedGridAngle() {
        this.value = ProjectedCoordinateSystem.this.rectifiedGridAngle;
      }

      @Override
      public void visitScaleFactor() {
        this.value = ProjectedCoordinateSystem.this.scaleFactor;
      }

      @Override
      public void visitStandardParallel1() {
        this.value = ProjectedCoordinateSystem.this.standardParallel1;
      }

      @Override
      public void visitStandardParallel2() {
        this.value = ProjectedCoordinateSystem.this.standardParallel2;
      }

      @Override
      public double getResult() {
        return this.value;
      }

      @Override
      public void visitSemiMinor() {
        this.value = ProjectedCoordinateSystem.this.semiMinor;
      }

      @Override
      public void visitSemiMajor() {
        this.value = ProjectedCoordinateSystem.this.semiMajor;
      }
    };
    final ParameterName valueType = ParameterName.byName(name);
    if (valueType != null) {
      valueType.accept(visitor);
    }
    return visitor.getResult();
  }

  @Override
  public double getLatitudeOfTrueScale() {
    return this.latitudeOfTrueScale;
  }
}
