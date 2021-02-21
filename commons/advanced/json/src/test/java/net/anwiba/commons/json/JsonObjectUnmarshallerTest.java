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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.json.schema.v1_0.ObjectProperty;
import net.anwiba.commons.json.schema.v1_0.Property;

public class JsonObjectUnmarshallerTest {

  @Test
  public void object() throws IOException {
    final JsonObjectUnmarshaller<ObjectProperty> unmarshaller = new JsonObjectUnmarshallerBuilder<>(
        ObjectProperty.class).build();
    final ObjectProperty response = unmarshaller.unmarshal(TestResources.object);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(ObjectProperty.class));
  }

  @Test
  public void property() throws IOException {
    final JsonObjectUnmarshaller<Property> unmarshaller = new JsonObjectUnmarshallerBuilder<>(Property.class).build();
    final Property response = unmarshaller.unmarshal(TestResources.object);
    assertThat(response, notNullValue());
    assertThat(response, instanceOf(ObjectProperty.class));
  }

}