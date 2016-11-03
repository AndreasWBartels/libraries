/*
 * #%L anwiba commons tools %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.tools.definition.schema.json.gramma.parser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotation;
import net.anwiba.tools.definition.schema.json.gramma.element.JField;
import net.anwiba.tools.definition.schema.json.gramma.element.JObject;
import net.anwiba.tools.definition.schema.json.gramma.element.JParameter;
import net.anwiba.tools.definition.schema.json.gramma.element.JType;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

@SuppressWarnings({ "boxing", "nls" })
public class JSONSchemaDefinitionTreeParserTest extends AbstractJSONTest {

  @Test
  public void testObject() throws RecognitionException, JssdParserException, IOException {
    assertObjects("{\"one\":<int>2,\"two\":<int>3}",//
        1,//
        new String[]{},//
        new String[]{ "one", "two" },//
        new String[]{ "int", "int" },//
        new Object[]{ 2L, 3L });
  }

  @Test
  public void testObjectArray() throws RecognitionException, JssdParserException, IOException {
    final Iterable<JObject> objects = getObjects("[{\"text\": <String> \"one\"},{\"value\": <int> 2}]");
    final Iterator<JObject> iterator = objects.iterator();
    assertObject(iterator.next(),//
        new String[]{},//
        new String[]{ "text" },//
        new String[]{ "String" },//
        new Object[]{ "one" });
    assertObject(iterator.next(),//
        new String[]{},//
        new String[]{ "value" },//
        new String[]{ "int" },
        new Object[]{ 2L }//
    );
  }

  @Test
  public void testObjectWithAnnotations() throws RecognitionException, JssdParserException, IOException {
    assertObjects("@test() @type {\"one\":<int>2,\"two\":<int>3}", //
        1,//
        new String[]{ "test", "type" },//
        new String[]{ "one", "two" },//
        new String[]{ "int", "int" },//
        new Object[]{ 2L, 3L });
  }

  @Test
  public void testEmptyObjectWithAnnotations() throws RecognitionException, JssdParserException, IOException {
    assertObjects("@type {}", //
        1,//
        new String[]{ "type" },//
        new String[]{},//
        new String[]{},//
        new Object[]{});
  }

  @Test
  public void testObjectWithArray() throws RecognitionException, JssdParserException, IOException {
    final Iterable<JObject> objects = assertObjects(
        "{\"one\":<java.util.Map<java.lang.String,java.lang.Object>>\"eins\",\"two\":<java.lang.String[][]>null}", //
        1,//
        new String[]{}, //
        new String[]{ "one", "two" }, //
        new String[]{ "java.util.Map", "java.lang.String" },//
        new Object[]{ "eins", null });
    final JObject object = objects.iterator().next();
    assertField(object, "one", false, "java.lang.String", "java.lang.Object");
    assertField(object, "two", true);

  }

  public void assertField(final JObject object, final String name, final boolean isArray, final String... generics) {
    final JField field = object.field(name);
    final JType type = field.type();
    assertThat(type.isArray(), equalTo(isArray));
    assertThat(type.generics(), equalTo(generics));
  }

  @Test
  public void testObjectWithParametrizedAnnotation() throws RecognitionException, JssdParserException, IOException {
    final Iterable<JObject> objects = assertObjects("@test(name=4) {\"one\":<int>2,\"two\":<int>3}",//
        1,//
        new String[]{ "test" }, //
        new String[]{ "one", "two" },//
        new String[]{ "int", "int" },//
        new Object[]{ 2L, 3L });//
    final JObject object = objects.iterator().next();
    final JAnnotation annotation = object.annotations().iterator().next();
    final JParameter parameter = annotation.parameters().iterator().next();
    assertThat(parameter, notNullValue());
    assertThat(parameter.name(), equalTo("name"));
    assertThat(parameter.value(), equalTo((Object) 4L));
  }
}