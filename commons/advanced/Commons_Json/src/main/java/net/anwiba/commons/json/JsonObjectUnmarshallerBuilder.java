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

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class JsonObjectUnmarshallerBuilder<T> {

  private final Class<T> clazz;
  final Map<Class, Object> injectionValues = new HashMap<>();

  public JsonObjectUnmarshallerBuilder(final Class<T> clazz) {
    this.clazz = clazz;
  }

  public <C> JsonObjectUnmarshallerBuilder<T> addInjectionValues(final Class<C> clazz, final C value) {
    this.injectionValues.put(clazz, value);
    return this;
  }

  public JsonObjectUnmarshaller<T> build() {
    return new JsonObjectUnmarshaller<>(this.clazz, this.injectionValues);
  }
}
