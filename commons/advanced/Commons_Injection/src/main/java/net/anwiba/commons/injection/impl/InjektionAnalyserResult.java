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

package net.anwiba.commons.injection.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectingFactory;

@SuppressWarnings("rawtypes")
public final class InjektionAnalyserResult implements IInjektionAnalyserResult {

  private final List<IInjektionAnalyserValueResult> results;
  private final List<IBinding> bindings;
  private final Map<IBinding, IInjektionAnalyserValueResult> map;
  private final Class type;
  private final IInjectingFactory factory;

  public static IInjektionAnalyserResult create(final Class type, final List<IInjektionAnalyserValueResult> results) {
    return create(type, results, null);
  }

  public static IInjektionAnalyserResult create(
      final Class type,
      final List<IInjektionAnalyserValueResult> results,
      final IInjectingFactory factory) {
    final List<IBinding> types = new ArrayList<>();
    final Map<IBinding, IInjektionAnalyserValueResult> map = new HashMap<>();
    for (final IInjektionAnalyserValueResult result : results) {
      types.add(result.getBinding());
      map.put(result.getBinding(), result);
    }
    return new InjektionAnalyserResult(type, results, types, map, factory);
  }

  private InjektionAnalyserResult(
      final Class type,
      final List<IInjektionAnalyserValueResult> results,
      final List<IBinding> types,
      final Map<IBinding, IInjektionAnalyserValueResult> map,
      final IInjectingFactory factory) {
    this.type = type;
    this.results = results;
    this.bindings = types;
    this.map = map;
    this.factory = factory;
  }

  @Override
  public Iterable<IBinding> getBindings() {
    return this.bindings;
  }

  @Override
  public boolean isNullable(final IBinding binding) {
    final IInjektionAnalyserValueResult result = this.map.get(binding);
    if (result == null) {
      throw new IllegalStateException();
    }
    return result.isNullable();
  }

  @Override
  public boolean isImitable(final IBinding binding) {
    final IInjektionAnalyserValueResult result = this.map.get(binding);
    if (result == null) {
      throw new IllegalStateException();
    }
    return result.isImitable();
  }

  @Override
  public boolean isEmptiable(final IBinding binding) {
    final IInjektionAnalyserValueResult result = this.map.get(binding);
    if (result == null) {
      throw new IllegalStateException();
    }
    return result.isEmptiable();
  }

  @Override
  public boolean isIterable(final IBinding binding) {
    final IInjektionAnalyserValueResult result = this.map.get(binding);
    if (result == null) {
      throw new IllegalStateException();
    }
    return result.isIterable();
  }

  @Override
  public boolean hasNullable() {
    return this.results.stream().anyMatch(i -> i.isNullable());
  }

  @Override
  public boolean hasIterable() {
    return this.results.stream().anyMatch(i -> i.isIterable());
  }

  @Override
  public boolean isIndependent() {
    return !(Modifier.isAbstract(this.type.getModifiers()) || this.type.isInterface()) && this.bindings.isEmpty();
  }

  @Override
  public Class getType() {
    return this.type;
  }

  @Override
  public IInjectingFactory getFactory() {
    return this.factory;
  }

  @Override
  public boolean isFactory() {
    return this.factory != null;
  }

}
