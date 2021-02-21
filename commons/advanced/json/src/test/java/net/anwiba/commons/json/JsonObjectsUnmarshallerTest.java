/*
 * #%L
 *
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
package net.anwiba.commons.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.json.schema.v1_0.ObjectProperty;
import net.anwiba.commons.json.schema.v1_0.Property;

public class JsonObjectsUnmarshallerTest {

  @Test
  public void property() throws IOException {
    final JsonObjectsUnmarshaller<Property> unmarshaller = new JsonObjectsUnmarshallerBuilder<>(Property.class).build();
    final List<Property> response = unmarshaller.unmarshal(TestResources.object);
    assertThat(response, notNullValue());
    assertThat(response.size(), equalTo(1));
    assertThat(response.get(0), notNullValue());
    assertThat(response.get(0), instanceOf(ObjectProperty.class));
  }

  @Test
  public void object() throws IOException {
    final JsonObjectsUnmarshaller<ObjectProperty> unmarshaller = new JsonObjectsUnmarshallerBuilder<>(
        ObjectProperty.class).build();
    final List<ObjectProperty> response = unmarshaller.unmarshal(TestResources.object);
    assertThat(response, notNullValue());
    assertThat(response.size(), equalTo(1));
    assertThat(response.get(0), notNullValue());
    assertThat(response.get(0), instanceOf(ObjectProperty.class));
  }

  @Test
  public void properties() throws IOException {
    final JsonObjectsUnmarshaller<Property> unmarshaller = new JsonObjectsUnmarshallerBuilder<>(Property.class).build();
    final List<Property> response = unmarshaller.unmarshal(TestResources.objects);
    assertThat(response, notNullValue());
    assertThat(response.size(), equalTo(2));
    assertThat(response.get(0), notNullValue());
    assertThat(response.get(0), instanceOf(ObjectProperty.class));
    assertThat(response.get(1), notNullValue());
    assertThat(response.get(1), instanceOf(ObjectProperty.class));
  }

  @Test
  public void objects() throws IOException {
    final JsonObjectsUnmarshaller<ObjectProperty> unmarshaller = new JsonObjectsUnmarshallerBuilder<>(
        ObjectProperty.class).build();
    final List<ObjectProperty> response = unmarshaller.unmarshal(TestResources.objects);
    assertThat(response, notNullValue());
    assertThat(response.size(), equalTo(2));
    assertThat(response.get(0), notNullValue());
    assertThat(response.get(0), instanceOf(ObjectProperty.class));
    assertThat(response.get(1), notNullValue());
    assertThat(response.get(1), instanceOf(ObjectProperty.class));
  }

}