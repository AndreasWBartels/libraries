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

package net.anwiba.commons.injection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.anwiba.commons.injection.binding.ClassBinding;

@SuppressWarnings("rawtypes")
public final class DefaultInjectionValueProvider implements IInjectionValueProvider {

  private final HashMap<IBinding, List<Object>> values = new HashMap<>();

  public DefaultInjectionValueProvider() {
    this(new HashMap<IBinding, List<Object>>());
  }

  public DefaultInjectionValueProvider(final Map<IBinding, List<Object>> values) {
    this.values.putAll(values);
  }

  @Override
  public boolean contains(final IBinding<?> binding) {
    return this.values.containsKey(binding);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final IBinding<T> binding) {
    final List<Object> list = this.values.get(binding);
    if (list.size() != 1) {
      throw new IllegalStateException();
    }
    return (T) list.get(0);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Collection<T> getAll(final IBinding<T> binding) {
    return Optional
        .ofNullable(this.values.get(binding))
        .map(l -> l.stream().map(o -> (T) o).collect(Collectors.toList()))
        .orElseGet(() -> new ArrayList<>());
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean contains(final Class clazz) {
    return contains(binding(clazz));
  }

  @Override
  public <T> T get(final Class<T> clazz) {
    return get(binding(clazz));
  }

  @Override
  public <T> Collection<T> getAll(final Class<T> clazz) {
    return getAll(binding(clazz));
  }

  private <T> IBinding<T> binding(final Class<T> clazz) {
    return new ClassBinding<>(clazz);
  }
}
