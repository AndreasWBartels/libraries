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
package net.anwiba.tools.generator.java.bean.configuration;

import net.anwiba.commons.ensure.Conditions;
import net.anwiba.commons.ensure.Ensure;
import net.anwiba.tools.generator.java.bean.value.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parameters {

  private final List<String> names = new ArrayList<>();
  private final Map<String, Set<Object>> values = new HashMap<>();
  private final Map<String, ValueType> types = new HashMap<>();

  public void add(final String name, final Object value, final ValueType type) {
    Ensure.ensureArgumentNotNull(name);
    Ensure.ensureArgumentNotNull(value);
    Ensure.ensureArgumentNotNull(type);
    if (!this.values.containsKey(name)) {
      this.names.add(name);
      this.values.put(name, new HashSet<>());
      this.types.put(name, type);
    }
    Ensure.ensureThatArgument(type, Conditions.equalTo(this.types.get(name)));
    final Set<Object> set = this.values.get(name);
    set.add(value);
  }

  public Iterable<Parameter> parameters() {
    final ArrayList<Parameter> list = new ArrayList<>();
    for (final String name : this.names) {
      list.add(new Parameter(name, this.types.get(name), this.values.get(name)));
    }
    return list;
  }
}
