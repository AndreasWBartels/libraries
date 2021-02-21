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
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.utilities;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Area;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Axis;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Datum;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.GeocentricCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.GeographicCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystemTypeVisitor;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.IProjectedCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.PrimeMeridian;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ProjectedCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Spheroid;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ToWgs84;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public class CoordinateSystemUtilities {

  public static GeocentricCoordinateSystem getGeocentricCoordinateSystem(
      final GeographicCoordinateSystem geographicSystem) {
    return new GeocentricCoordinateSystem(
        geographicSystem.getDatum().getAuthority(),
        geographicSystem.getDatum().getName(),
        geographicSystem.getDatum(),
        geographicSystem.getPrimeMeridian(),
        Unit.METER);
  }

  public static final String EMPTY_PAGE =
      "<html><body><br><br><br><br><br><br><br><br><br><br><br><br><br></body></html>\n"; //$NON-NLS-1$

  public static String getDescriptionText(final ICoordinateSystem coordinateSystem) {
    if (coordinateSystem == null) {
      return EMPTY_PAGE;
    }
    final ICoordinateSystemTypeVisitor<String, RuntimeException> visitor = new ICoordinateSystemTypeVisitor<>() {

      @Override
      public String visitGeocentric() {
        final GeocentricCoordinateSystem system = (GeocentricCoordinateSystem) coordinateSystem;
        return "<font size=-1><b>Geocentric System:</b> " //$NON-NLS-1$
            + system.getName().replaceAll("_", " ") //$NON-NLS-1$ //$NON-NLS-2$
            + "</font><table>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$
            + getDescriptionText(system.getDatum())
            + "</td></tr>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$
            + getDescriptionText(system.getPrimeMeridian())
            + "</td></tr>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>Unit:</b> " //$NON-NLS-1$
            + system.getUnit().getName().replaceAll("_", " ") //$NON-NLS-1$//$NON-NLS-2$
            + "</font></td></tr>" //$NON-NLS-1$
            + "</table><"; //$NON-NLS-1$
      }

      @Override
      public String visitGeographic() {
        final GeographicCoordinateSystem system = (GeographicCoordinateSystem) coordinateSystem;
        return "<font size=-1><b>Geographic System:</b> " //$NON-NLS-1$
            + system.getName().replaceAll("_", " ") //$NON-NLS-1$ //$NON-NLS-2$
            + "</font><table>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$
            + getDescriptionText(system.getDatum())
            + "</td></tr>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$
            + getDescriptionText(system.getPrimeMeridian())
            + "</td></tr>" //$NON-NLS-1$
            + getArea(system.getArea())
            + "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>Unit:</b> " //$NON-NLS-1$
            + system.getUnit().getName().replaceAll("_", " ") //$NON-NLS-1$//$NON-NLS-2$
            + "</font></td></tr>" //$NON-NLS-1$
            + getAxises(system.getAxises())
            + "</table>"; //$NON-NLS-1$
      }

      @Override
      public String visitNone() {
        return ""; //$NON-NLS-1$
      }

      @Override
      public String visitProjected() {
        final ProjectedCoordinateSystem system = (ProjectedCoordinateSystem) coordinateSystem;
        return "<font size=-1><b>Projection:</b> " //$NON-NLS-1$
            + system.getName().replaceAll("_", " ") //$NON-NLS-1$ //$NON-NLS-2$
            + "</font><table>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$
            + CoordinateSystemUtilities.getDescriptionText(system.getGeographicCoordinateSystem())
            + "</td></tr>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>Projection:</b> " //$NON-NLS-1$
            + system.getProjection().getPrintName()
            + "</font></td></tr>" //$NON-NLS-1$
            + getParameterDescription(system, "Azimuth") //$NON-NLS-1$
            + getParameterDescription(system, "False_Easting") //$NON-NLS-1$
            + getParameterDescription(system, "False_Northing") //$NON-NLS-1$
            + getParameterDescription(system, "Scale_Factor") //$NON-NLS-1$
            + getParameterDescription(system, "Longitude_of_Origin") //$NON-NLS-1$
            + getParameterDescription(system, "Longitude_of_Point_1") //$NON-NLS-1$
            + getParameterDescription(system, "Longitude_of_Point_2") //$NON-NLS-1$
            + getParameterDescription(system, "Latitude_of_Origin") //$NON-NLS-1$
            + getParameterDescription(system, "Latitude_of_Point_1") //$NON-NLS-1$
            + getParameterDescription(system, "Latitude_of_Point_2") //$NON-NLS-1$
            + getParameterDescription(system, "Standard_parallel_1") //$NON-NLS-1$
            + getParameterDescription(system, "Standard_parallel_2") //$NON-NLS-1$
            + getArea(system.getArea())
            + "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>Unit:</b> " //$NON-NLS-1$
            + system.getUnit().getName()
            + "</font></td></tr>" //$NON-NLS-1$
            + getAxises(system.getAxises())
            + "</table>"; //$NON-NLS-1$
      }

      private String getDescriptionText(final Datum datum) {
        return "<font size=-1><b>Datum:</b> " //$NON-NLS-1$
            + datum.getName().replaceAll("_", " ") //$NON-NLS-1$ //$NON-NLS-2$
            + "<table>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$
            + getSpheroidText(datum.getSpheroid())
            + "</td></tr>" //$NON-NLS-1$
            + getDescriptionText(datum.getToWgs84()) //
            + "</table>"; //$NON-NLS-1$
      }

      private String getDescriptionText(final PrimeMeridian primeMeridian) {
        return "<font size=-1><b>Prime Meridian:</b> " //$NON-NLS-1$
            + primeMeridian.getName().replaceAll("_", " ") //$NON-NLS-1$ //$NON-NLS-2$
            + "</font><table>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>Longitude:</b> " //$NON-NLS-1$
            + primeMeridian.getLongitude()
            + "</font></td></tr>" //$NON-NLS-1$
            + "</table>"; //$NON-NLS-1$
      }

      private String getDescriptionText(final ToWgs84 toWgs84) {
        if (toWgs84 == null) {
          return ""; //$NON-NLS-1$
        }
        String string = "<tr><td><font size=-1>&#160;</font></td><td><font size=-1>" //$NON-NLS-1$
            + "<b>Transformation:</b> " //$NON-NLS-1$
            + "<table>" //$NON-NLS-1$
            + getArea(toWgs84.getArea())
            + "<tr><td><font size=-1>&#160;</font></td>" //$NON-NLS-1$
            + "<td><font size=-1><b>Parameter:</b> " //$NON-NLS-1$
            + toWgs84.getDX()
            + ", " //$NON-NLS-1$
            + toWgs84.getDY()
            + ", " //$NON-NLS-1$
            + toWgs84.getDZ()
            + ", " //$NON-NLS-1$
            + toWgs84.getRotX()
            + ", " //$NON-NLS-1$
            + toWgs84.getRotY()
            + ", " //$NON-NLS-1$
            + toWgs84.getRotZ()
            + ", " //$NON-NLS-1$
            + toWgs84.getSC()
            + "</font>" //$NON-NLS-1$
            + "</td></tr>"; //$NON-NLS-1$ "
        if (toWgs84.getAccuracy() != null) {
          string += "</td></tr>" //$NON-NLS-1$ "
              + "<tr><td><font size=-1>&#160;</font></td>" //$NON-NLS-1$
              + "<td><font size=-1><b>Accuracy:</b> " //$NON-NLS-1$
              + toWgs84.getAccuracy().getValue()
              + "</font>" //$NON-NLS-1$
              + "</td></tr>"; //$NON-NLS-1$
        }
        string +=
            "</table>"; //$NON-NLS-1$
        return string;
      }

      private String getParameterDescription(
          final IProjectedCoordinateSystem projectedCoordinatSystem,
          final String name) {
        final double value = projectedCoordinatSystem.getParameterValue(name);
        if (Double.isNaN(value)) {
          return ""; //$NON-NLS-1$
        }
        return "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>" //$NON-NLS-1$
            + name.replaceAll("_", " ") //$NON-NLS-1$ //$NON-NLS-2$
            + ": </b> " //$NON-NLS-1$
            + value
            + "</font></td></tr>"; //$NON-NLS-1$ //;
      }

      private String getSpheroidText(final Spheroid spheroid) {
        return "<font size=-1><b>Spheroid:</b> " //$NON-NLS-1$
            + spheroid.getName().replaceAll("_", " ") //$NON-NLS-1$//$NON-NLS-2$
            + "</font>" //$NON-NLS-1$
            + "<table>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>Semi Major Axis:</b> " //$NON-NLS-1$
            + spheroid.getSemiMajorAxis()
            + "</font></td></tr>" //$NON-NLS-1$
            + "<tr><td><font size=-1>&#160;</font></td><td><font size=-1><b>Inverse Flattening:</b> " //$NON-NLS-1$
            + spheroid.getInverseFlattening()
            + "</font></td></tr>" //$NON-NLS-1$
            + "</table>"; //$NON-NLS-1$
      }

      private String getArea(final Area area) {
        if (area == null) {
          return ""; //$NON-NLS-1$
        }
        final IEnvelope envelope = area.getEnvelope();
        return "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$

            + "<font size=-1><b>Area:</b></font><table>" //$NON-NLS-1$
            + "<tr><td><font size=-1><b>Name:</b></font></td><td><font size=-1>" //$NON-NLS-1$
            + area.getName().toString()
            + "</font></td></tr>" //$NON-NLS-1$
            + "<tr><td><font size=-1><b>Bounding box:</b></font></td>" //$NON-NLS-1$
            + "<td><font size=-1>" //$NON-NLS-1$
            + envelope.getMinimum().getXValue()
            + " " //$NON-NLS-1$
            + envelope.getMaximum().getXValue()
            + " " //$NON-NLS-1$
            + envelope.getMinimum().getYValue()
            + " " //$NON-NLS-1$
            + envelope.getMaximum().getYValue()
            + "</font></td></tr>" //$NON-NLS-1$
            + "</table></td></tr>"; //$NON-NLS-1$
      }

      private String getAxises(final Axis[] axises) {
        if (axises.length == 0) {
          return ""; //$NON-NLS-1$
        }
        String text = "<tr><td><font size=-1>&#160;</font></td><td>" //$NON-NLS-1$
            + "<font size=-1><b>Axis:</b></font><table>"; //$NON-NLS-1$
        for (final Axis axis : axises) {
          final String orientationName = axis.getOrientation() == null ? "" : axis.getOrientation().name(); //$NON-NLS-1$
          text += "<tr><td><b>" //$NON-NLS-1$
              + axis.getName()
              + ":</b></td><td><font size=-1>" //$NON-NLS-1$
              + orientationName
              + "</font></td></tr>"; //$NON-NLS-1$
        }
        text += "</table></td></tr>"; //$NON-NLS-1$
        return text;
      }

      @Override
      public String visitVertical() throws RuntimeException {
        return "";
      }

    };
    return coordinateSystem.getCoordinateSystemType().accept(visitor);
  }

  public static double getMetersPerUnit(final ICoordinateSystem coordinateSystem) {
    final Unit unit = coordinateSystem.getUnit();
    switch (unit.getType()) {
      case ANGLE: {
        return (unit.getConversionFactor() / Unit.DEGREE.getConversionFactor()) * 111319.490793;
      }
      case DISTANCE: {
        return unit.getConversionFactor();
      }
      case TIME:
      case SCALE:
      case UNKNOWN: {
        return 1;
      }
    }
    throw new UnreachableCodeReachedException();
  }

}
