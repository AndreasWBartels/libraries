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

package net.anwiba.spatial.coordinatereferencesystem.utilities;

import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystemConstants;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Area;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Axis;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Datum;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Extension;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.GeocentricCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.GeographicCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystemTypeVisitor;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Parameter;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.PrimeMeridian;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ProjectedCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Projection;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Spheroid;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ToWgs84;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.utilities.CoordinateSystemUtilities;

public class CoordinateReferenceSystemUtilities {

  public static String createWellKnownText(final ICoordinateReferenceSystem projection) {
    return createWellKnownText(projection.getCoordinateSystem(), false);
  }

  public static String createWellKnownText(final ICoordinateReferenceSystem projection, final boolean isEnhanced) {
    return createWellKnownText(projection.getCoordinateSystem(), isEnhanced);
  }

  public static String createWellKnownText(final ICoordinateSystem coordinateSystem) {
    return createWellKnownText(coordinateSystem, false);
  }

  public static String createWellKnownText(final ICoordinateSystem coordinateSystem, final boolean isEnhanced) {
    final ICoordinateSystemTypeVisitor<String, RuntimeException> visitor = new ICoordinateSystemTypeVisitor<String, RuntimeException>() {

      @Override
      public String visitProjected() {
        return createProjectedCoordinateSystemText((ProjectedCoordinateSystem) coordinateSystem, isEnhanced);
      }

      @Override
      public String visitNone() {
        return ""; //$NON-NLS-1$
      }

      @Override
      public String visitGeographic() {
        return createGeographicCoordinateSystemText((GeographicCoordinateSystem) coordinateSystem, isEnhanced);
      }

      @Override
      public String visitGeocentric() {
        return createGeocentricCoordinateSystemText((GeocentricCoordinateSystem) coordinateSystem, isEnhanced);
      }
    };
    return coordinateSystem.getCoordinateSystemType().accept(visitor);
  }

  private static String createProjectedCoordinateSystemText(
      final ProjectedCoordinateSystem coordinateSystem,
      final boolean isEnhanced) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("PROJCS[\""); //$NON-NLS-1$
    buffer.append(coordinateSystem.getName());
    buffer.append("\","); //$NON-NLS-1$
    buffer.append(createGeographicCoordinateSystemText(coordinateSystem.getGeographicCoordinateSystem(), isEnhanced));
    buffer.append(","); //$NON-NLS-1$
    if (isEnhanced) {
      buffer.append(createAreaText(coordinateSystem.getArea()));
    }
    buffer.append(createProjectionText(coordinateSystem.getProjection()));
    buffer.append(","); //$NON-NLS-1$
    buffer.append(createParametersText(coordinateSystem.getParameters()));
    buffer.append(createUnitText(coordinateSystem.getUnit()));
    buffer.append(createExtensionsText(coordinateSystem.getExtensions()));
    buffer.append(createAxisesText(coordinateSystem.getAxises()));
    final Authority authority = coordinateSystem.getAuthority();
    if (authority != null) {
      buffer.append(","); //$NON-NLS-1$
      buffer.append(createAuthorityText(authority));
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createAreaText(final Area area) {

    if (area == null) {
      return ""; //$NON-NLS-1$
    }
    final StringBuffer buffer = new StringBuffer();
    buffer.append("AREA[\""); //$NON-NLS-1$
    buffer.append(area.getName());
    buffer.append("\", "); //$NON-NLS-1$
    final IEnvelope envelope = area.getEnvelope();
    buffer.append(envelope.getMinimum().getXValue());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(envelope.getMaximum().getXValue());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(envelope.getMinimum().getYValue());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(envelope.getMaximum().getYValue());
    buffer.append("],"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static Object createExtensionsText(final Iterable<Extension> extensions) {
    final StringBuffer buffer = new StringBuffer();
    for (final Extension extension : extensions) {
      buffer.append(","); //$NON-NLS-1$
      buffer.append(createExtensionText(extension));
    }
    return buffer.toString();
  }

  private static Object createExtensionText(final Extension extension) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("EXTENSION[\""); //$NON-NLS-1$
    buffer.append(extension.getName());
    buffer.append("\", \""); //$NON-NLS-1$
    buffer.append(extension.getValue());
    buffer.append("\"]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static Object createAuthorityText(final Authority authority) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("AUTHORITY[\""); //$NON-NLS-1$
    buffer.append(authority.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(String.valueOf(authority.getNumber()));
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static Object createAxisesText(final Axis[] axises) {
    final StringBuffer buffer = new StringBuffer();
    for (final Axis axis : axises) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createAxisText(axis));
    }
    return buffer.toString();
  }

  private static Object createAxisText(final Axis axis) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("AXIS[\""); //$NON-NLS-1$
    buffer.append(axis.getName());
    buffer.append("\""); //$NON-NLS-1$
    if (axis.getOrientation() != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(axis.getOrientation().name());
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createProjectionText(final Projection projection) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("PROJECTION[\""); //$NON-NLS-1$
    buffer.append(projection.getPrintName());
    buffer.append("\"]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createParametersText(final Parameter[] parameters) {
    final StringBuffer buffer = new StringBuffer();
    for (final Parameter parameter : parameters) {
      buffer.append(createParameterText(parameter));
      buffer.append(", "); //$NON-NLS-1$
    }
    return buffer.toString();
  }

  private static String createParameterText(final Parameter parameter) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("PARAMETER[\""); //$NON-NLS-1$
    buffer.append(parameter.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(parameter.getValue());
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createUnitText(final Unit unit) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("UNIT[\""); //$NON-NLS-1$
    buffer.append(unit.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(unit.getConversionFactor());
    final Authority authority = unit.getAuthority();
    if (authority != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createAuthorityText(authority));
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createGeographicCoordinateSystemText(
      final GeographicCoordinateSystem coordinateSystem,
      final boolean isEnhanced) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("GEOGCS[\""); //$NON-NLS-1$
    buffer.append(coordinateSystem.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(createDatumText(coordinateSystem.getDatum(), isEnhanced));
    buffer.append(", "); //$NON-NLS-1$
    if (isEnhanced) {
      buffer.append(createAreaText(coordinateSystem.getArea()));
    }
    buffer.append(createPrimeMeridanText(coordinateSystem.getPrimeMeridian()));
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(createUnitText(coordinateSystem.getUnit()));
    buffer.append(createExtensionsText(coordinateSystem.getExtensions()));
    buffer.append(createAxisesText(coordinateSystem.getAxises()));
    final Authority authority = coordinateSystem.getAuthority();
    if (authority != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createAuthorityText(authority));
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createDatumText(final Datum datum, final boolean isEnhanced) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("DATUM[\""); //$NON-NLS-1$
    buffer.append(datum.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(createSpheroidText(datum.getSpheroid()));
    if (datum.getToWgs84() != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createToWgs84Text(datum.getToWgs84(), isEnhanced));
    }
    final Authority authority = datum.getAuthority();
    if (authority != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createAuthorityText(authority));
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createSpheroidText(final Spheroid spheroid) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("SPHEROID[\""); //$NON-NLS-1$
    buffer.append(spheroid.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(spheroid.getSemiMajorAxis());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(spheroid.getInverseFlattening());
    final Authority authority = spheroid.getAuthority();
    if (authority != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createAuthorityText(authority));
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createToWgs84Text(final ToWgs84 toWgs84, final boolean isEnhanced) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("TOWGS84["); //$NON-NLS-1$
    if (isEnhanced) {
      buffer.append(createAreaText(toWgs84.getArea()));
    }
    buffer.append(toWgs84.getDX());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(toWgs84.getDY());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(toWgs84.getDZ());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(toWgs84.getRotX());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(toWgs84.getRotY());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(toWgs84.getRotZ());
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(toWgs84.getSC());
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createPrimeMeridanText(final PrimeMeridian primeMeridian) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("PRIMEM[\""); //$NON-NLS-1$
    buffer.append(primeMeridian.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(primeMeridian.getLongitude());
    final Authority authority = primeMeridian.getAuthority();
    if (authority != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createAuthorityText(authority));
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  private static String createGeocentricCoordinateSystemText(
      final GeocentricCoordinateSystem coordinateSystem,
      final boolean isEnhanced) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("GEOCCS[\""); //$NON-NLS-1$
    buffer.append(coordinateSystem.getName());
    buffer.append("\", "); //$NON-NLS-1$
    buffer.append(createDatumText(coordinateSystem.getDatum(), isEnhanced));
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(createPrimeMeridanText(coordinateSystem.getPrimeMeridian()));
    buffer.append(", "); //$NON-NLS-1$
    buffer.append(createUnitText(coordinateSystem.getUnit()));
    final Authority authority = coordinateSystem.getAuthority();
    if (authority != null) {
      buffer.append(", "); //$NON-NLS-1$
      buffer.append(createAuthorityText(authority));
    }
    buffer.append("]"); //$NON-NLS-1$
    return buffer.toString();
  }

  public static double getMetersPerUnit(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    return CoordinateSystemUtilities.getMetersPerUnit(coordinateReferenceSystem.getCoordinateSystem());
  }

  public static boolean isNull(final ICoordinateReferenceSystem system) {
    return system == null || ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM.equals(system);
  }
}
