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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JField {

  private final String name;
  private final JValue value;

  private final Map<String, JAnnotation> annotations = new LinkedHashMap<>();
  private final JType type;

  JField(final String name, final JType type, final JValue value, final List<JAnnotation> annotations) {
    this.name = name;
    this.type = type;
    this.value = value;
    annotations.forEach(a -> this.annotations.put(a.name(), a));
  }

  public String name() {
    return this.name;
  }

  public JValue value() {
    return this.value;
  }

  public Iterable<JAnnotation> annotations() {
    return this.annotations.values();
  }

  public JType type() {
    return this.type;
  }

  @SuppressWarnings("hiding")
  public boolean hasAnnotation(final String name) {
    return this.annotations.containsKey(name);
  }
}
