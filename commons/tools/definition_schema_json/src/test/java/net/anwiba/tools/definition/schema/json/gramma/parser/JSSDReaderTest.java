/*
 * #%L
 * anwiba commons advanced
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.tools.definition.schema.json.gramma.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import net.anwiba.tools.definition.schema.json.JSSDReader;
import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotation;
import net.anwiba.tools.definition.schema.json.gramma.element.JField;
import net.anwiba.tools.definition.schema.json.gramma.element.JObject;
import net.anwiba.tools.definition.schema.json.gramma.element.JsonType;

@SuppressWarnings("nls")
public class JSSDReaderTest {

  JSSDReader reader = new JSSDReader();

  @Test
  public void object() throws JssdParserException, IOException {
    final Iterable<JObject> iterable = this.reader.read("{}");
    final Iterator<JObject> iterator = iterable.iterator();
    assertThat(iterator.hasNext(), is(true));
    final JObject object = iterator.next();
    assertThat(object, notNullValue());
    assertThat(iterator.hasNext(), is(false));
  }

  @Test
  public void objects() throws JssdParserException, IOException {
    final Iterable<JObject> iterable = this.reader.read("[{},{}]");
    final Iterator<JObject> iterator = iterable.iterator();
    assertThat(iterator.hasNext(), is(true));
    JObject object = iterator.next();
    assertThat(object, notNullValue());
    assertThat(iterator.hasNext(), is(true));
    object = iterator.next();
    assertThat(object, notNullValue());
    assertThat(iterator.hasNext(), is(false));
  }

  @Test
  public void member() throws JssdParserException, IOException {
    final Iterator<JObject> objects = this.reader
        .read("/* foo */ { // member \n // member \n \"name\" : <int> 2 }")
        .iterator();
    assertThat(objects.hasNext(), is(true));
    final JObject object = objects.next();
    assertThat(object, notNullValue());
    final Iterator<String> names = object.names().iterator();
    assertMember(object, names, "name", "int", JsonType.NUMBER, 2L);
    assertThat(names.hasNext(), is(false));
  }

  @Test
  public void annotation() throws JssdParserException, IOException {
    final Iterator<JObject> objects = this.reader.read(" @foo() @bee() { @rt() \"name\" : <int> 2 }").iterator();
    assertThat(objects.hasNext(), is(true));
    final JObject object = objects.next();

    final Iterator<JAnnotation> annotations = object.annotations().iterator();
    assertAnnotation(annotations, "foo");
    assertAnnotation(annotations, "bee");
    assertThat(annotations.hasNext(), is(false));
    assertThat(object, notNullValue());

    final Iterator<String> names = object.names().iterator();
    final JField member = assertMember(object, names, "name", "int", JsonType.NUMBER, 2L);
    assertAnnotation(member.annotations().iterator(), "rt");
    assertThat(names.hasNext(), is(false));
    assertThat(object, notNullValue());
  }

  @Test
  public void members() throws JssdParserException, IOException {
    final Iterator<JObject> objects = this.reader.read(
        "{ \"name\" : <int> 2 , \"text\" : <String> \"text\" , \"foo\" : <String> \"text\" }").iterator();
    assertThat(objects.hasNext(), is(true));
    final JObject object = objects.next();
    assertThat(object, notNullValue());
    final Iterator<String> names = object.names().iterator();
    assertMember(object, names, "name", "int", JsonType.NUMBER, 2L);
    assertMember(object, names, "text", "String", JsonType.STRING, "text");
    assertMember(object, names, "foo", "String", JsonType.STRING, "text");
    assertThat(names.hasNext(), is(false));
  }

  private JAnnotation assertAnnotation(final Iterator<JAnnotation> annotations, final String name) {
    assertThat(annotations.hasNext(), is(true));
    final JAnnotation annotation = annotations.next();
    assertThat(name, equalTo(annotation.name()));
    return annotation;
  }

  public JField assertMember(
      final JObject object,
      final Iterator<String> names,
      final String memberName,
      final String classname,
      final JsonType type,
      final Object value) {
    assertThat(names.hasNext(), is(true));
    final String name = names.next();
    assertThat(name, notNullValue());
    assertThat(name, equalTo(memberName));
    final JField field = object.field(name);
    assertThat(field, notNullValue());
    assertThat(field.type().name(), equalTo(classname));
    assertThat(field.value().type(), equalTo(type));
    assertThat(field.value().value(), equalTo(value));
    return field;
  }

}
