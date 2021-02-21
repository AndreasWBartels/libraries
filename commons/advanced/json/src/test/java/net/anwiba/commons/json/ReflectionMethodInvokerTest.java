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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationTargetException;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

import net.anwiba.commons.json.schema.v1_0.Property;
import net.anwiba.commons.reflection.ReflectionMethodInvoker;

@SuppressWarnings("nls")
public class ReflectionMethodInvokerTest {

  @Test
  public void name() throws InvocationTargetException {
    final ReflectionMethodInvoker<Object, Object> setter = ReflectionMethodInvoker
        .createSetter(Property.class, "JsonProperty", "value", "title");
    assertNotNull(setter);
    final Property property = new Property();
    setter.invoke(property, "Test");
    assertThat(property.getTitle(), IsEqual.equalTo("Test"));
  }

}
