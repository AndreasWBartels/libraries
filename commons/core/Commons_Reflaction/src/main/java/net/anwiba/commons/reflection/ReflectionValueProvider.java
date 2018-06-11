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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.anwiba.commons.reflection.binding.ClassBinding;
import net.anwiba.commons.reflection.utilities.IValueHolder;
import net.anwiba.commons.reflection.utilities.ListValueHolder;
import net.anwiba.commons.reflection.utilities.SingleValueHolder;

public final class ReflectionValueProvider implements IReflectionValueProvider {

  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IValueHolder> services;
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IBinding> links;
  private final IReflectionValueProvider valueProvider;

  public ReflectionValueProvider(
      final IReflectionValueProvider valueProvider,
      @SuppressWarnings("rawtypes") final Map<IBinding, IValueHolder> services,
      @SuppressWarnings("rawtypes") final Map<IBinding, IBinding> links) {
    this.valueProvider = valueProvider;
    this.services = services;
    this.links = links;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Collection<T> getAll(final IBinding<T> binding) {
    if (this.links.containsKey(binding)) {
      return getAll(this.links.get(binding));
    }
    final IValueHolder holder = this.services.get(binding);
    if (holder == null) {
      return this.valueProvider.getAll(binding);
    }
    if (holder instanceof SingleValueHolder) {
      final Object value = ((SingleValueHolder) holder).getValue();
      final ArrayList<T> values = new ArrayList<>(Arrays.asList((T) value));
      values.addAll(this.valueProvider.getAll(binding));
      return values;
    }
    if (holder instanceof ListValueHolder) {
      final List<T> values = ((ListValueHolder) holder).getValue().stream().map(v -> (T) v).collect(
          Collectors.toList());
      values.addAll(this.valueProvider.getAll(binding));
      return values;
    }
    throw new IllegalStateException();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final IBinding<T> binding) {
    if (this.links.containsKey(binding)) {
      final Object object = get(this.links.get(binding));
      return (T) object;
    }
    if (!this.services.containsKey(binding)) {
      return this.valueProvider.get(binding);
    }
    final IValueHolder holder = this.services.get(binding);
    if (holder == null) {
      return null;
    }
    if (holder instanceof SingleValueHolder) {
      final Object value = ((SingleValueHolder) holder).getValue();
      return (T) value;
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean contains(final IBinding<?> binding) {
    if (this.links.containsKey(binding)) {
      final IBinding<?> link = this.links.get(binding);
      return this.services.containsKey(link) || this.valueProvider.contains(link);
    }
    return this.services.containsKey(binding) || this.valueProvider.contains(binding);
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
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
