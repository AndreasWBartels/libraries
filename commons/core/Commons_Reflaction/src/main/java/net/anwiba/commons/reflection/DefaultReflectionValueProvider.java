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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public final class DefaultReflectionValueProvider implements IReflectionValueProvider {

  private final HashMap<Class, List<Object>> values = new HashMap<>();

  public DefaultReflectionValueProvider() {
    this(new HashMap<Class, List<Object>>());
  }

  private DefaultReflectionValueProvider(final HashMap<Class, List<Object>> values) {
    this.values.putAll(values);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Collection<T> getAll(final Class<T> clazz) {
    return Optional
        .ofNullable(this.values.get(clazz))
        .map(l -> l.stream().map(o -> (T) o).collect(Collectors.toList()))
        .orElseGet(() -> new ArrayList<>());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final Class<T> clazz) {
    final List<Object> list = this.values.get(clazz);
    if (list.size() != 1) {
      throw new IllegalStateException();
    }
    return (T) list.get(0);
  }

  @Override
  public boolean contains(final Class<?> clazz) {
    return this.values.containsKey(clazz);
  }
}
