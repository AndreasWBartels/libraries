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
package net.anwiba.spatial.coordinatereferencesystem;

import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Axis;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.AxisOrientation;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Datum;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.GeographicCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystemConstants;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Parameter;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.PrimeMeridian;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ProjectedCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Projection;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ProjectionType;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Spheroid;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ToWgs84;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

@SuppressWarnings("nls")
public interface ICoordinateReferenceSystemConstants {

  public static final String WGS_84 = "WGS 84";

  public static final ICoordinateReferenceSystem NULL_COORDIANTE_REFERENCE_SYSTEM = new CoordinateReferenceSystem(
      new Authority(null, -1),
      -1,
      ICoordinateSystemConstants.NONE);

  public static final GeographicCoordinateSystem GG_WGS_84 = new GeographicCoordinateSystem(
      new Authority("EPSG", 4326), //
      WGS_84, //
      new Datum(WGS_84, Spheroid.WGS_84, ToWgs84.WGS_84),
      PrimeMeridian.GREENWICH,
      Unit.DEGREE,
      new Axis("north", AxisOrientation.NORTH), //
      new Axis("east", AxisOrientation.EAST)); //

  public static final GeographicCoordinateSystem GG_CRS_84 = new GeographicCoordinateSystem(
      new Authority("CRS", 84), //
      WGS_84, //
      new Datum(WGS_84, Spheroid.WGS_84, ToWgs84.WGS_84),
      PrimeMeridian.GREENWICH,
      Unit.DEGREE,
      new Axis("east", AxisOrientation.EAST), //
      new Axis("north", AxisOrientation.NORTH)); //

  public static final ProjectedCoordinateSystem PC_EPSG_3857 = new ProjectedCoordinateSystem(
      new Authority("EPSG", 3857), //
      "WGS 84 / Pseudo-Mercator", //
      GG_WGS_84,
      new Projection(ProjectionType.MERCATOR_1SP,
          new Parameter[] { //
              new Parameter("central_meridian", 0), //
              new Parameter("scale_factor", 1), //
              new Parameter("false_easting", 0), //
              new Parameter("false_northing", 0) }), //
      Unit.METER,
      new Axis("east", AxisOrientation.EAST), //
      new Axis("north", AxisOrientation.NORTH)); //

  public static final ProjectedCoordinateSystem PC_EPSG_3785 = new ProjectedCoordinateSystem(
      new Authority("EPSG", 3785), //
      "PSUEDO MERCATOR", //
      GG_WGS_84,
      new Projection(ProjectionType.MERCATOR_1SP,
          new Parameter[] {}),
      Unit.METER,
      new Axis("east", AxisOrientation.EAST), //
      new Axis("north", AxisOrientation.NORTH)); //

  public static final ICoordinateReferenceSystem GGS_CRS_84 = new CoordinateReferenceSystem(
      new Authority("CRS", 84), //
      84,
      GG_CRS_84);
  public static final ICoordinateReferenceSystem GGS_WGS_84 = new CoordinateReferenceSystem(
      new Authority("EPSG", 4326), //
      4326,
      GG_WGS_84);
  public static final ICoordinateReferenceSystem EPSG_3785 = new CoordinateReferenceSystem(
      new Authority("EPSG", 3785), //
      3785,
      PC_EPSG_3785);
  public static final ICoordinateReferenceSystem EPSG_3857 = new CoordinateReferenceSystem(
      new Authority("EPSG", 3857), //
      3857,
      PC_EPSG_3857);

  public static final ICoordinateReferenceSystem PSUEDO_MERCATOR = EPSG_3857;

}
