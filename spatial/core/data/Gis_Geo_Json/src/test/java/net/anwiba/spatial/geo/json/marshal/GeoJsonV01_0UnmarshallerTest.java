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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.anwiba.spatial.geo.json.v01_0.Crs;
import net.anwiba.spatial.geo.json.v01_0.ErrorResponse;
import net.anwiba.spatial.geo.json.v01_0.Feature;
import net.anwiba.spatial.geo.json.v01_0.FeatureCollection;
import net.anwiba.spatial.geo.json.v01_0.GeoJsonObject;
import net.anwiba.spatial.geo.json.v01_0.Geometry;
import net.anwiba.spatial.geo.json.v01_0.GeometryCollection;
import net.anwiba.spatial.geo.json.v01_0.LineString;
import net.anwiba.spatial.geo.json.v01_0.MultiLineString;
import net.anwiba.spatial.geo.json.v01_0.MultiPoint;
import net.anwiba.spatial.geo.json.v01_0.MultiPolygon;
import net.anwiba.spatial.geo.json.v01_0.Point;
import net.anwiba.spatial.geo.json.v01_0.Polygon;

public class GeoJsonV01_0UnmarshallerTest {

  GeoJsonObjectUnmarshallerFactory factory = new GeoJsonObjectUnmarshallerFactory();

  @Test
  public void crs() throws IOException, GeoJsonMarshallingException {
    final Crs response = this.factory.create(Crs.class).unmarshal(GeoJsonV01_0TestResources.crs);
    assertThat(response, notNullValue());
  }

  @Test
  public void point() throws IOException, GeoJsonMarshallingException {
    final Point response = this.factory.create(Point.class).unmarshal(GeoJsonV01_0TestResources.point);
    assertThat(response, notNullValue());
  }

  @Test
  public void lineString() throws IOException, GeoJsonMarshallingException {
    final LineString response = this.factory.create(LineString.class).unmarshal(GeoJsonV01_0TestResources.lineString);
    assertThat(response, notNullValue());
  }

  @Test
  public void polygon() throws IOException, GeoJsonMarshallingException {
    final Polygon response = this.factory.create(Polygon.class).unmarshal(GeoJsonV01_0TestResources.polygon);
    assertThat(response, notNullValue());
  }

  @Test
  public void multiPoint() throws IOException, GeoJsonMarshallingException {
    final MultiPoint response = this.factory.create(MultiPoint.class).unmarshal(GeoJsonV01_0TestResources.multiPoint);
    assertThat(response, notNullValue());
  }

  @Test
  public void multiLineString() throws IOException, GeoJsonMarshallingException {
    final MultiLineString response = this.factory
        .create(MultiLineString.class)
        .unmarshal(GeoJsonV01_0TestResources.multiLineString);
    assertThat(response, notNullValue());
  }

  @Test
  public void multiPpolygon() throws IOException, GeoJsonMarshallingException {
    final MultiPolygon response = this.factory
        .create(MultiPolygon.class)
        .unmarshal(GeoJsonV01_0TestResources.multiPolygon);
    assertThat(response, notNullValue());
  }

  @Test
  public void geometryCollection() throws IOException, GeoJsonMarshallingException {
    final GeometryCollection response = this.factory
        .create(GeometryCollection.class)
        .unmarshal(GeoJsonV01_0TestResources.geometryCollection);
    assertThat(response, notNullValue());
  }

  @Test
  public void feature() throws IOException, GeoJsonMarshallingException {
    final Feature response = this.factory.create(Feature.class).unmarshal(GeoJsonV01_0TestResources.feature);
    assertThat(response, notNullValue());
  }

  @Test
  public void featureCollection() throws IOException, GeoJsonMarshallingException {
    final FeatureCollection response = this.factory
        .create(FeatureCollection.class)
        .unmarshal(GeoJsonV01_0TestResources.featureCollection);
    assertThat(response, notNullValue());
  }

  @Test
  public void geojsonObject() throws IOException, GeoJsonMarshallingException {
    final GeoJsonObjectUnmarshaller<GeoJsonObject> unmarshaller = this.factory.create(GeoJsonObject.class);
    GeoJsonObject response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.featureCollection);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(FeatureCollection.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.feature);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(Feature.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.geometryCollection);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(Geometry.class));
  }

  @Test
  public void geometry() throws IOException, GeoJsonMarshallingException {
    final GeoJsonObjectUnmarshaller<Geometry> unmarshaller = this.factory.create(Geometry.class);
    GeoJsonObject response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.geometryCollection);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(GeometryCollection.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.point);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(Point.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.lineString);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(LineString.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.polygon);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(Polygon.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.multiPoint);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(MultiPoint.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.multiLineString);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(MultiLineString.class));
    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.multiPolygon);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(MultiPolygon.class));
  }

  @Test
  public void disorderedMultiPolygon() throws IOException, GeoJsonMarshallingException {
    final GeoJsonObjectUnmarshaller<Geometry> unmarshaller = this.factory.create(Geometry.class);
    final GeoJsonObject response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.disorderedMultiPolygon);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(MultiPolygon.class));
  }

  @Test
  public void disorderedFeatureWithUnknownfields() throws IOException, GeoJsonMarshallingException {
    final GeoJsonObjectUnmarshaller<Feature> unmarshaller = this.factory.create(Feature.class);
    final GeoJsonObject response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.disorderedFeatureWithUnknownfields);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(Feature.class));
  }

  @Test
  public void disorderdFeatureCollection() throws IOException, GeoJsonMarshallingException {
    final GeoJsonObjectUnmarshaller<FeatureCollection> unmarshaller = this.factory.create(FeatureCollection.class);
    final GeoJsonObject response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.disorderdFeatureCollection);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(FeatureCollection.class));
  }

  @Test
  public void disorderdGeoJsonObject() throws IOException, GeoJsonMarshallingException {
    final GeoJsonObjectUnmarshaller<GeoJsonObject> unmarshaller = this.factory.create(GeoJsonObject.class);
    GeoJsonObject response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.disorderdFeatureCollection);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(FeatureCollection.class));

    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.disorderedFeatureWithUnknownfields);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(Feature.class));

    response = unmarshaller.unmarshal(GeoJsonV01_0TestResources.disorderedMultiPolygon);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(MultiPolygon.class));
  }

  @Test
  public void errorResponse() throws IOException, GeoJsonMarshallingException {
    final ErrorResponse response = this.factory
        .create(ErrorResponse.class)
        .unmarshal(GeoJsonV01_0TestResources.errorResponse);
    assertThat(response, notNullValue());
  }

  @Test(expected = GeoJsonMarshallingException.class)
  public void throwErrorResponse() throws IOException, GeoJsonMarshallingException {
    this.factory.create(Crs.class).unmarshal(GeoJsonV01_0TestResources.errorResponse);
  }
}
