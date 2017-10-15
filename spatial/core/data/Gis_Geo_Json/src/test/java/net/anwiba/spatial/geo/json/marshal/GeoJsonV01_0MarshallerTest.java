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
import java.io.StringWriter;

import org.junit.Test;

import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.geo.json.v01_0.Crs;
import net.anwiba.spatial.geo.json.v01_0.Feature;
import net.anwiba.spatial.geo.json.v01_0.GeometryCollection;

@SuppressWarnings("nls")
public class GeoJsonV01_0MarshallerTest {

  GeoJsonObjectUnmarshallerFactory unmarshallerFactory = new GeoJsonObjectUnmarshallerFactory();
  GeoJsonObjectMarshallerFactory marshallerFactory = new GeoJsonObjectMarshallerFactory(true);

  @Test
  public void crs() throws IOException, GeoJsonMarshallingException {
    final Crs response = this.unmarshallerFactory.create(Crs.class).unmarshal(GeoJsonV01_0TestResources.crs);
    assertThat(response, notNullValue());
    final String string = marshall(Crs.class, response);
    assertThat(
        StringUtilities.removeWhiteSpaces(string),
        equalTo("{ \"type\" :\"name\", \"properties\" :{\"name\" :\"urn:ogc:def:crs:OGC:1.3:CRS84\" }}"));
  }

  @SuppressWarnings("unchecked")
  private <T> String marshall(@SuppressWarnings("rawtypes") final Class clazz, final T response) throws IOException {
    final StringWriter outputStream = new StringWriter();
    this.marshallerFactory.create(clazz).marshall(outputStream, response);
    return outputStream.toString();
  }

  @Test
  public void geometryCollection() throws IOException, GeoJsonMarshallingException {
    final GeometryCollection response = this.unmarshallerFactory
        .create(GeometryCollection.class)
        .unmarshal(GeoJsonV01_0TestResources.geometryCollection);
    assertThat(response, notNullValue());
    final String string = marshall(GeometryCollection.class, response);
    assertThat(
        StringUtilities.removeWhiteSpaces(string),
        equalTo(
            "{ \"type\" :\"GeometryCollection\", \"geometries\" :[{\"type\" :\"Point\", \"coordinates\" :[100.0,0.0]},{\"type\" :\"LineString\", \"coordinates\" :[[101.0,0.0],[102.0,1.0]]}]}"));
  }

  @Test
  public void feature() throws IOException, GeoJsonMarshallingException {
    final Feature response = this.unmarshallerFactory
        .create(Feature.class)
        .unmarshal(GeoJsonV01_0TestResources.feature);
    assertThat(response, notNullValue());
    assertThat(response, notNullValue());
    final String string = marshall(Feature.class, response);
    assertThat(
        StringUtilities.removeWhiteSpaces(string),
        equalTo(
            "{ \"type\" :\"Feature\", \"geometry\" :{\"type\" :\"Polygon\", \"coordinates\" :[[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]]},\"properties\" :{\"prop0\" :\"value0\", \"prop1\" :{\"this\" :\"that\" }}}"));
  }

}
