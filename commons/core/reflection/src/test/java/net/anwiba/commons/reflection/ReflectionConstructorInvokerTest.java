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
package net.anwiba.commons.reflection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

public class ReflectionConstructorInvokerTest {

  @Test
  public void create() throws InvocationTargetException {
    final ReflectionConstructorInvoker<TestObjectValues> factory =
        new ReflectionConstructorInvoker<>(TestObjectValues.class, String.class);
    final TestObjectValues object = factory.invoke("VALUE"); //$NON-NLS-1$
    assertThat(object, not(nullValue()));
    assertThat(object, isA(TestObjectValues.class));
  }
}