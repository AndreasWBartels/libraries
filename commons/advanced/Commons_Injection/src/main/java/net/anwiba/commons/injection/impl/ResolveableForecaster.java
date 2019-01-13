/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.util.Map;
import java.util.Objects;

import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectionValueProvider;
import net.anwiba.commons.injection.binding.ClassBinding;
import net.anwiba.commons.injection.utilities.IValueHolder;

public class ResolveableForecaster implements IResolveableForecaster {

  private final IInjectionValueProvider valueProvider;
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IValueHolder> services;
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IValueHolder> results;
  private final IValueInjectionAnalyser analyser;
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IBinding> links;

  @SuppressWarnings("rawtypes")
  public ResolveableForecaster(
      final IInjectionValueProvider valueProvider,
      final Map<IBinding, IValueHolder> services,
      final Map<IBinding, IBinding> links,
      final IValueInjectionAnalyser analyser,
      final Map<IBinding, IValueHolder> results) {
    this.valueProvider = valueProvider;
    this.services = services;
    this.links = links;
    this.analyser = analyser;
    this.results = results;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public boolean isResolveable(final IInjektionAnalyserResult result) {
    if (Objects.equals(IInjektionAnalyserResult.UNRESOLVEABLE, result)) {
      return false;
    }
    if (result.isIndependent()) {
      return true;
    }
    for (final IBinding binding : result.getBindings()) {
      if (!exists(binding)) {
        if (isDeclared(binding)) {
          return false;
        }
        if (result.isIterable(binding)) {
          if (result.isEmptiable(binding)) {
            continue;
          }
          return false;
        }
        if (result.isNullable(binding)) {
          continue;
        }
        if (result.isImitable(binding)) {
          continue;
        }
        if (!(binding instanceof ClassBinding) || this.links.containsKey(binding)) {
          return false;
        }
        if (this.analyser.analyse(binding.getBoundedClass()).isIndependent()) {
          continue;
        }
        return false;
      }
      if (!isDeclared(binding)) {
        continue;
      }
      if (result.isIterable(binding)) {
        return false;
      }
      throw new IllegalStateException(
          "Found listvalue of type '" //$NON-NLS-1$
              + binding
              + "' for singlevalue member of object '" //$NON-NLS-1$
              + result.getType()
              + "'"); //$NON-NLS-1$
    }
    return true;
  }

  private boolean isDeclared(@SuppressWarnings("rawtypes") final IBinding binding) {
    return this.results.containsKey(binding)
        || (this.links.containsKey(binding) && this.results.containsKey(this.links.get(binding)));
  }

  private boolean exists(@SuppressWarnings("rawtypes") final IBinding binding) {
    return (this.links.containsKey(binding) && this.services.containsKey(this.links.get(binding)))
        || this.services.containsKey(binding)
        || this.valueProvider.contains(binding);
  }
}
