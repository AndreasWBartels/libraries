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

public class JObjectBuilder implements IAnnotatable {

  private final Map<String, JField> values = new HashMap<>();
  private final List<String> names = new ArrayList<>();
  private final List<JAnnotation> annotations = new ArrayList<>();

  public void add(final JField field) {
    if (field == null) {
      return;
    }
    this.names.add(field.name());
    this.values.put(field.name(), field);
  }

  @Override
  public void add(final JAnnotation annotation) {
    if (annotation == null) {
      return;
    }
    this.annotations.add(annotation);
  }

  public JObject build() {
    return new JObject(this.names, this.values, this.annotations);
  }

}
