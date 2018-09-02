/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.injection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.anwiba.commons.injection.binding.ClassBinding;

public class ValueInjektorTest {

  private IValueInjector createInjector(final Object... objects) {
    @SuppressWarnings("rawtypes")
    final Map<IBinding, List<Object>> values = new HashMap<>();
    for (final Object object : objects) {
      values.put(new ClassBinding<>(object.getClass()), Arrays.asList(object));
    }
    return new ValueInjector(new DefaultInjectionValueProvider(values));
  }

  @Test
  public void fields() throws Exception {
    final TestClass test = new TestClass();
    final TestValue value = new TestValue();
    final IValueInjector injektor = createInjector(test, value);
    final TestObjectValues object = injektor.create(TestObjectValues.class);
    assertThat(object.getTest(), equalTo(test));
    assertThat(object.getValue(), equalTo(value));
    final Iterable<TestValue> values = object.getValues();
    assertThat(values, notNullValue());
    assertThat(values.iterator().hasNext(), equalTo(true));
    assertThat(values.iterator().next(), equalTo(value));
  }

  @Test
  public void constructor() throws Exception {
    final TestClass test = new TestClass();
    final TestValue value = new TestValue();
    final IValueInjector injektor = createInjector(test, value);
    final TestObjectConstructor object = injektor.create(TestObjectConstructor.class);
    assertThat(object.getTest(), equalTo(test));
    assertThat(object.getValue(), equalTo(value));
    final Iterable<TestValue> values = object.getValues();
    assertThat(values, notNullValue());
    assertThat(values.iterator().hasNext(), equalTo(true));
    assertThat(values.iterator().next(), equalTo(value));
  }
}
