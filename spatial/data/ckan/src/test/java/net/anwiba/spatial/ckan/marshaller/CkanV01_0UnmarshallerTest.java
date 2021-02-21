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

package net.anwiba.spatial.ckan.marshaller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.json.JsonObjectUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Contact;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.PackageSearchResultResponse;
import net.anwiba.spatial.ckan.json.schema.v1_0.Resource;
import net.anwiba.spatial.ckan.json.schema.v1_0.ResourceSearchResultResponse;
import net.anwiba.spatial.ckan.json.schema.v1_0.ShapeInfo;
import net.anwiba.spatial.ckan.json.schema.v1_0.ShapeInfoString;

public class CkanV01_0UnmarshallerTest {

  CkanJsonObjectMarshallerFactory marshallerFactory = new CkanJsonObjectMarshallerFactory(true);
  CkanJsonObjectUnmarshallerFactory objectFactory = new CkanJsonObjectUnmarshallerFactory();
  CkanJsonObjectsUnmarshallerFactory objectsFactory = new CkanJsonObjectsUnmarshallerFactory();

  @Test
  public void resource() throws IOException, CkanJsonMapperException {
    final Resource response = this.objectFactory.create(Resource.class).unmarshal(CkanV01_0TestResources.resource);
    assertThat(response, notNullValue());
    final String string = JsonObjectUtilities.marshall(response);
    assertThat(string, notNullValue());
  }

  @Test
  public void resourceI18() throws IOException, CkanJsonMapperException {

    // JsonObjectUnmarshallerBuilder<Resource> builder = new JsonObjectUnmarshallerBuilder<>(Resource.class);
    // builder.addDes(I18String.class, null);
    final Resource response = this.objectFactory.create(Resource.class).unmarshal(CkanV01_0TestResources.resourceI18);
    assertThat(response, notNullValue());
    final String string = JsonObjectUtilities.marshall(response);
    assertThat(string, notNullValue());
  }

  @Test
  public void resourceSearch() throws IOException, CkanJsonMapperException {
    final ResourceSearchResultResponse response = this.objectFactory
        .create(ResourceSearchResultResponse.class)
        .unmarshal(CkanV01_0TestResources.resourceSearchResponse);
    assertThat(response, notNullValue());
  }

  @Test
  public void packageSearch() throws IOException, CkanJsonMapperException {
    final PackageSearchResultResponse response = this.objectFactory
        .create(PackageSearchResultResponse.class)
        .unmarshal(CkanV01_0TestResources.packageSearchResponse);
    assertThat(response, notNullValue());
  }

  @Test
  public void license() throws IOException, CkanJsonMapperException {
    final License response = this.objectFactory.create(License.class).unmarshal(CkanV01_0TestResources.license);
    assertThat(response, notNullValue());
  }

  @Test
  public void contacts() throws IOException, CkanJsonMapperException {
    final List<Contact> response = this.objectsFactory.create(Contact.class).unmarshal(CkanV01_0TestResources.contacts);
    assertThat(response, notNullValue());
  }

  @Test
  public void shapeInfo() {
    final ShapeInfo shapeInfo = new ShapeInfoString(CkanV01_0TestResources.shapeInfo).asShapeInfo();
    assertThat(shapeInfo, notNullValue());
    final ShapeInfoString shapeInfoString = ShapeInfoString.valueOf(shapeInfo);
    assertThat(shapeInfoString, notNullValue());
  }

}
