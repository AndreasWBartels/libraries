/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.definition.schema.json.gramma.element;

import java.util.List;

public class JValue implements IJNode {

  private final Object value;
  private final JsonType type;

  public JValue(final Object value) {
    this.value = value;
    this.type = createType(value);
  }

  @SuppressWarnings("unchecked")
  public <T> T value() {
    return (T) this.value;
  }

  public JsonType type() {
    return this.type;
  }

  private JsonType createType(final Object object) {
    if (object instanceof JObject) {
      return JsonType.OBJECT;
    }
    if (object instanceof List) {
      return JsonType.ARRAY;
    }
    if (object == null) {
      return JsonType.NULL;
    }
    if (object instanceof String) {
      return JsonType.STRING;
    }
    if (object instanceof Number) {
      return JsonType.NUMBER;
    }
    if (object instanceof Boolean) {
      return JsonType.BOOLEAN;
    }
    if (object instanceof Character) {
      return JsonType.CHARACTER;
    }
    throw new IllegalArgumentException();
  }
}