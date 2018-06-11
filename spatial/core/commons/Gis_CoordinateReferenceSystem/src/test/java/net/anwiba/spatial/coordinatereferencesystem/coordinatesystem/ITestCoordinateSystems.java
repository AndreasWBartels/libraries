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

import static net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ParameterName.*;

import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public interface ITestCoordinateSystems {

  public static final String WGS_84 = "WGS 84"; //$NON-NLS-1$
  public static final String DHDN = "DHDN"; //$NON-NLS-1$

  public static final GeocentricCoordinateSystem GC_DHDN = new GeocentricCoordinateSystem(
      null,
      DHDN, //
      new Datum(DHDN, Spheroid.BESSEL_1841, ToWgs84.DHDN),
      PrimeMeridian.GREENWICH,
      Unit.METER);

  public static final GeocentricCoordinateSystem GC_WGS_84 = new GeocentricCoordinateSystem(
      null,
      WGS_84, //
      new Datum(WGS_84, Spheroid.WGS_84, ToWgs84.WGS_84),
      PrimeMeridian.GREENWICH,
      Unit.METER);

  public static final GeographicCoordinateSystem GG_WGS_84 = new GeographicCoordinateSystem(
      null,
      WGS_84, //
      new Datum(WGS_84, Spheroid.WGS_84, ToWgs84.WGS_84),
      PrimeMeridian.GREENWICH,
      Unit.DEGREE);

  public static final GeographicCoordinateSystem GG_DHDN = new GeographicCoordinateSystem(
      null,
      DHDN, //
      new Datum(DHDN, Spheroid.BESSEL_1841, ToWgs84.DHDN),
      PrimeMeridian.GREENWICH,
      Unit.DEGREE);

  public static final GeographicCoordinateSystem ETRS89 = new GeographicCoordinateSystem(
      null, //
      "ETRS89", //$NON-NLS-1$
      new Datum("European_Terrestrial_Reference_System_1989", Spheroid.GRS_80, ToWgs84.NULL), //$NON-NLS-1$
      PrimeMeridian.GREENWICH,
      Unit.DEGREE);

  public static Parameter[] gk_z3_parameters = new Parameter[]{ //
      new Parameter(LATITUDE_OF_ORIGIN, 0), //
      new Parameter(LONGITUDE_OF_ORIGIN, 9), //
      new Parameter(SCALE_FACTOR, 1), //
      new Parameter(FALSE_EASTING, 3500000), //
      new Parameter(FALSE_NORTHING, 0) };
  public static Parameter[] utm_32n_parameters = new Parameter[]{ //
      new Parameter(LATITUDE_OF_ORIGIN, 0), //
      new Parameter(LONGITUDE_OF_ORIGIN, 9), //
      new Parameter(SCALE_FACTOR, 0.9996), //
      new Parameter(FALSE_EASTING, 500000), //
      new Parameter(FALSE_NORTHING, 0) };
  public static Parameter[] etrs_89_tm31_parameters = new Parameter[]{ //
      new Parameter(LATITUDE_OF_ORIGIN, 0), //
      new Parameter(LONGITUDE_OF_ORIGIN, 3), //
      new Parameter(SCALE_FACTOR, 0.9996), //
      new Parameter(FALSE_EASTING, 500000), //
      new Parameter(FALSE_NORTHING, 0) };
  public static Parameter[] dhdn_soldner_Berlin_parameters = new Parameter[]{ //
      new Parameter(LATITUDE_OF_ORIGIN, 52.25071338), //
      new Parameter(LONGITUDE_OF_ORIGIN, 13.37379332), //
      new Parameter(FALSE_EASTING, 40000.0), //
      new Parameter(FALSE_NORTHING, 10000.0) };

  public static final ProjectedCoordinateSystem DHDN_soldner_Berlin = new ProjectedCoordinateSystem(
      null, //
      "DHDN / Soldner Berlin", //$NON-NLS-1$
      GG_DHDN,
      Projection.CASSINI_SOLDNER,
      dhdn_soldner_Berlin_parameters,
      Unit.METER);

  public static final ProjectedCoordinateSystem DHDN_GK_3 = new ProjectedCoordinateSystem(
      null, //
      "DHDN Gauss Krueger Zone 3", //$NON-NLS-1$
      GG_DHDN,
      Projection.GAUSS_KRUEGER,
      gk_z3_parameters,
      Unit.METER);

  public static final ProjectedCoordinateSystem WGS_84_GK_3 = new ProjectedCoordinateSystem(
      null, //
      "WGS 84 Gauss Krueger Zone 3", //$NON-NLS-1$
      GG_WGS_84,
      Projection.GAUSS_KRUEGER,
      gk_z3_parameters,
      Unit.METER);

  public static final ProjectedCoordinateSystem WGS_84_UTM_32N = new ProjectedCoordinateSystem(
      null, //
      "UTM_32", //$NON-NLS-1$
      GG_WGS_84,
      Projection.TRANSVERSE_MERCATOR,
      utm_32n_parameters,
      Unit.METER);

  // PROJCS["ETRS89 / ETRS-TM31",
  // GEOGCS["ETRS89",
  // DATUM["European_Terrestrial_Reference_System_1989",
  // SPHEROID["GRS 1980",6378137,298.257222101,
  // AUTHORITY["EPSG","7019"]],
  // AUTHORITY["EPSG","6258"]],
  // PRIMEM["Greenwich",0,
  // AUTHORITY["EPSG","8901"]],
  // UNIT["degree",0.01745329251994328,
  // AUTHORITY["EPSG","9122"]],
  // AUTHORITY["EPSG","4258"]],
  // UNIT["metre",1,
  // AUTHORITY["EPSG","9001"]],
  // PROJECTION["Transverse_Mercator"],
  // PARAMETER["latitude_of_origin",0],
  // PARAMETER["central_meridian",3],
  // PARAMETER["scale_factor",0.9996],
  // PARAMETER["false_easting",500000],
  // PARAMETER["false_northing",0],
  // AUTHORITY["EPSG","3043"],
  // AXIS["Easting",EAST],
  // AXIS["Northing",NORTH]]
  public static final IProjectedCoordinateSystem ETRS_89_TM31 = new ProjectedCoordinateSystem(
      null,
      "ETRS89 / ETRS-TM31", //$NON-NLS-1$
      ETRS89,
      Projection.TRANSVERSE_MERCATOR,
      etrs_89_tm31_parameters,
      Unit.METER);

}
