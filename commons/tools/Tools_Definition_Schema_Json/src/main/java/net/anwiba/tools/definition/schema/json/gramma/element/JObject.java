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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JObject implements IJNode {

  private final Map<String, JField> values = new HashMap<>();
  private final List<String> names = new ArrayList<>();
  private final List<JAnnotation> annotations = new ArrayList<>();

  JObject(final List<String> names, final Map<String, JField> values, final List<JAnnotation> annotations) {
    this.names.addAll(names);
    this.values.putAll(values);
    this.annotations.addAll(annotations);
  }

  public void add(final JField field) {
    this.names.add(field.name());
    this.values.put(field.name(), field);
  }

  public JValue value(final String name) {
    return this.values.get(name).value();
  }

  public int numberOfValues() {
    return this.values.size();
  }

  public Iterable<String> names() {
    return this.names;
  }

  public Iterable<JAnnotation> annotations() {
    return this.annotations;
  }

  public JField field(final String name) {
    return this.values.get(name);
  }
}
