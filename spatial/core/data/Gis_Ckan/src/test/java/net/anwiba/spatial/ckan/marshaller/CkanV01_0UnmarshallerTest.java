// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

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
    final PackageSearchResultResponse response = this.objectFactory.create(PackageSearchResultResponse.class).unmarshal(
        CkanV01_0TestResources.packageSearchResponse);
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
