/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.spatial.geo.json.marshal;

import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;

public class GeoJsonV01_0TestResources extends AbstractResourceFactory {

  static {
    initialize(GeoJsonV01_0TestResources.class);
  }

  @Location("crs.json")
  public static String crs;

  @Location("point.json")
  public static String point;

  @Location("lineString.json")
  public static String lineString;

  @Location("polygon.json")
  public static String polygon;

  @Location("multiPoint.xml")
  public static String multiPointXml;

  @Location("multiPoint.json")
  public static String multiPoint;

  @Location("multiLineString.json")
  public static String multiLineString;

  @Location("multiPolygon.json")
  public static String multiPolygon;

  @Location("disorderedMultiPolygon.json")
  public static String disorderedMultiPolygon;

  @Location("geometryCollection.json")
  public static String geometryCollection;

  @Location("feature.json")
  public static String feature;

  @Location("disorderedFeatureWithUnknownfields.json")
  public static String disorderedFeatureWithUnknownfields;

  @Location("featureCollection.json")
  public static String featureCollection;

  @Location("disorderdFeatureCollection.json")
  public static String disorderdFeatureCollection;

  @Location("errorResponse.json")
  public static String errorResponse;

}
