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

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public class ProjectedCoordinateSystem extends AbstractCoordinateSystem
    implements
    IProjectedCoordinateSystem {

  private static final long serialVersionUID = -1L;

  private final GeographicCoordinateSystem sphericalCoordinateSystem;
  private final Projection projection;

  public ProjectedCoordinateSystem(
      final Authority authority,
      final String name,
      final GeographicCoordinateSystem sphericalCoordinateSystem,
      final Projection projection,
      final Unit unit,
      final Axis... axises) {
    this(authority, name, sphericalCoordinateSystem, projection, null, unit, axises);
  }

  public ProjectedCoordinateSystem(
      final Authority authority,
      final String name,
      final GeographicCoordinateSystem sphericalCoordinateSystem,
      final Projection projection,
      final Area area,
      final Unit unit,
      final Axis... axises) {
    this(
        authority,
        name,
        sphericalCoordinateSystem,
        projection,
        area,
        unit,
        new ArrayList<>(),
        axises);
  }

  public ProjectedCoordinateSystem(
      final Authority authority,
      final String name,
      final GeographicCoordinateSystem sphericalCoordinateSystem,
      final Projection projection,
      final Area area,
      final Unit unit,
      final List<Extension> extensions,
      final Axis... axises) {
    super(authority, name, area, unit, extensions, axises);
    Ensure.ensureArgumentNotNull(sphericalCoordinateSystem);
    Ensure.ensureArgumentNotNull(projection);
    this.sphericalCoordinateSystem = sphericalCoordinateSystem;
    this.projection = projection;
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
    return this.projection.getParameters();
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
        && ObjectUtilities.equals(this.projection, other.projection);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ObjectUtilities.hashCode(1, prime, this.sphericalCoordinateSystem);
    result = ObjectUtilities.hashCode(result, prime, this.projection);
    return result;
  }

  @Override
  public double getAzimuth() {
    return this.projection.getAzimuth();
  }

  @Override
  public double getRectifiedGridAngle() {
    return this.projection.getRectifiedGridAngle();
  }

  @Override
  public double getCentralMeridian() {
    return this.projection.getCentralMeridian();
  }

  @Override
  public double getLongitudeOfOrigin() {
    return this.projection.getLongitudeOfOrigin();
  }

  @Override
  public double getLongitudeOfPoint1() {
    return this.projection.getLongitudeOfPoint1();
  }

  @Override
  public double getLongitudeOfPoint2() {
    return this.projection.getLongitudeOfPoint2();
  }

  @Override
  public double getLatitudeOfOrigin() {
    return this.projection.getLatitudeOfOrigin();
  }

  @Override
  public double getLatitudeOfPoint1() {
    return this.projection.getLatitudeOfPoint1();
  }

  @Override
  public double getLatitudeOfPoint2() {
    return this.projection.getLatitudeOfPoint2();
  }

  @Override
  public double getStandardParallel1() {
    return this.projection.getStandardParallel1();
  }

  @Override
  public double getStandardParallel2() {
    return this.projection.getStandardParallel2();
  }

  @Override
  public double getFalseEasting() {
    return this.projection.getFalseEasting();
  }

  @Override
  public double getFalseNorthing() {
    return this.projection.getFalseNorthing();
  }

  @Override
  public double getScaleFactor() {
    return this.projection.getScaleFactor();
  }

  public double getZoneWidth() {
    return this.projection.getZoneWidth();
  }

  @Override
  public double getSemiMajor() {
    final double semiMajor = this.projection.getSemiMajor();
    if (Double.isNaN(semiMajor)
        && this.sphericalCoordinateSystem.getDatum().getSpheroid() != null) {
      return this.sphericalCoordinateSystem.getDatum().getSpheroid().getSemiMajorAxis();
    }
    return semiMajor;
  }

  @Override
  public double getSemiMinor() {
    final double semiMinor = this.projection.getSemiMinor();
    if (Double.isNaN(semiMinor)
        && this.sphericalCoordinateSystem.getDatum().getSpheroid() != null) {
      return this.sphericalCoordinateSystem.getDatum().getSpheroid().getSemiMinorAxis();
    }
    return semiMinor;
  }

  @Override
  public double getParameterValue(final String name) {
    return this.projection.getParameterValue(name);
  }

  @Override
  public ICoordinateSystem adapt(final ToWgs84 towgs84) {
    return new ProjectedCoordinateSystem(
        getAuthority(),
        getName(),
        this.sphericalCoordinateSystem.adapt(towgs84),
        this.projection,
        getArea(),
        getUnit(),
        IterableUtilities.asList(getExtensions()),
        getAxises());
  }
}
