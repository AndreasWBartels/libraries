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
package net.anwiba.tools.definition.schema.json.gramma.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JAnnotation {

  private final String name;
  private final List<JParameter> parameters = new ArrayList<>();
  private final Map<String, JParameter> parameterByName = new HashMap<>();

  JAnnotation(final String name, final List<JParameter> parameters) {
    this.name = name;
    this.parameters.addAll(parameters);
    for (final JParameter parameter : parameters) {
      this.parameterByName.put(parameter.name(), parameter);
    }
  }

  public String name() {
    return this.name;
  }

  public Iterable<JParameter> parameters() {
    return this.parameters;
  }

  public JParameter parameter(@SuppressWarnings("hiding") final String name) {
    return this.parameterByName.get(name);
  }

  public boolean hasParameters() {
    return !this.parameters.isEmpty();
  }

  public boolean hasParameter(@SuppressWarnings("hiding") final String name) {
    return this.parameterByName.containsKey(name);
  }
}