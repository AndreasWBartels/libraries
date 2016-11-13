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
package net.anwiba.commons.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;

public class JsonObjectUnmarshaller<T> extends AbstractJsonObjectUnmarshaller<T, Void, IOException> {

  public JsonObjectUnmarshaller(final Class<T> clazz) {
    this(clazz, new HashMap<>());
  }

  public JsonObjectUnmarshaller(final Class<T> clazz, final Map<Class, Object> injectionValues) {
    super(clazz, Void.class, injectionValues, new IJsonObjectMapperExceptionFactory<Void, IOException>() {

      @Override
      public IOException create(final Void response) {
        throw new UnreachableCodeReachedException();
      }
    });
  }
}
