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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Iterator;

import org.antlr.v4.runtime.RecognitionException;

import net.anwiba.commons.lang.counter.Counter;
import net.anwiba.tools.definition.schema.json.JSSDReader;
import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotation;
import net.anwiba.tools.definition.schema.json.gramma.element.JField;
import net.anwiba.tools.definition.schema.json.gramma.element.JObject;

public abstract class AbstractJSONTest {

  public Iterable<JObject> assertObjects(
      final String source,
      final long numberOfObjects,
      final String[] annotations,
      final String[] names,
      final Object[] types,
      final Object[] values)
      throws RecognitionException,
      JssdParserException,
      IOException {
    assertNotNull(names);
    assertNotNull(values);
    assertThat(names.length, equalTo(values.length));
    final Iterable<JObject> objects = getObjects(source);
    assertNotNull(objects);
    final Iterator<JObject> iterator = objects.iterator();
    assertThat(iterator.hasNext(), equalTo(true));
    final JObject object = iterator.next();
    assertObject(object, annotations, names, types, values);
    final Counter counter = new Counter(1);
    while (iterator.hasNext()) {
      assertNotNull(iterator.next());
      counter.next();
    }
    assertThat(counter.value(), equalTo(numberOfObjects));
    return objects;
  }

  public void assertObject(
      final JObject object,
      final String[] annotations,
      final String[] names,
      final Object[] types,
      final Object[] values) {
    assertNotNull(object);
    assertThat(object.numberOfValues(), equalTo(values.length));
    for (int i = 0; i < names.length; i++) {
      final JField field = object.field(names[i]);
      final Object value = field.value().value();
      assertThat(value, equalTo(values[i]));
      assertThat(field.type().name(), equalTo(types[i]));
    }
    final Iterator<JAnnotation> actualAnnotations = object.annotations().iterator();
    for (int i = 0; i < annotations.length; i++) {
      assertThat(actualAnnotations.hasNext(), equalTo(true));
      assertThat(actualAnnotations.next().name(), equalTo(annotations[i]));
    }
  }

  JSSDReader reader = new JSSDReader();

  public Iterable<JObject> getObjects(final String source)
      throws RecognitionException,
      JssdParserException,
      IOException {
    return this.reader.read(source);
  }

}
