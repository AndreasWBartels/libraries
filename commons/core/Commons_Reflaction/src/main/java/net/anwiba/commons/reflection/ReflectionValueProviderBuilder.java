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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.anwiba.commons.reflection.binding.ClassBinding;
import net.anwiba.commons.reflection.utilities.IValueHolder;
import net.anwiba.commons.reflection.utilities.ListValueHolder;
import net.anwiba.commons.reflection.utilities.SingleValueHolder;

public class ReflectionValueProviderBuilder implements IReflectionValueProviderBuilder {

  private final IReflectionValueInjectionAnalyser analyser = new ReflectionValueInjectionAnalyser();
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IValueHolder> services = new HashMap<>();
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IValueHolder> results = new HashMap<>();
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IBinding> links = new HashMap<>();

  private final IReflectionValueProvider valueProvider;

  public ReflectionValueProviderBuilder() {
    this(new DefaultReflectionValueProvider());
  }

  public ReflectionValueProviderBuilder(final IReflectionValueProvider valueProvider) {
    this.valueProvider = valueProvider;
  }

  <T> T get(final Class<T> clazz) {
    return get(binding(clazz));
  }

  @SuppressWarnings({ "unchecked" })
  <T> T get(final IBinding<T> binding) {
    if (this.results.containsKey(binding)) {
      throw new IllegalArgumentException();
    }
    if (this.links.containsKey(binding)) {
      return (T) get(this.links.get(binding));
    }
    if (!this.services.containsKey(binding)) {
      return this.valueProvider.get(binding);
    }
    final IValueHolder valueHolder = this.services.get(binding);
    if (!(valueHolder instanceof SingleValueHolder)) {
      throw new IllegalArgumentException();
    }
    return (T) ((SingleValueHolder) valueHolder).getValue();
  }

  <T> Iterable<T> getAll(final Class<T> clazz) {
    return getAll(binding(clazz));
  }

  @SuppressWarnings({ "unchecked" })
  <T> Iterable<T> getAll(final IBinding<T> binding) {
    if (this.results.containsKey(binding)) {
      throw new IllegalArgumentException();
    }
    if (this.links.containsKey(binding)) {
      return getAll(this.links.get(binding));
    }
    if (!this.services.containsKey(binding)) {
      return this.valueProvider.getAll(binding);
    }
    final IValueHolder valueHolder = this.services.get(binding);
    if (!(valueHolder instanceof ListValueHolder)) {
      throw new IllegalArgumentException();
    }
    final List<T> values = ((ListValueHolder) valueHolder).getValue().stream().map(o -> (T) o).collect(
        Collectors.toList());
    values.addAll(this.valueProvider.getAll(binding));
    return values;
  }

  private <T> IBinding<T> binding(final Class<T> clazz) {
    return new ClassBinding<>(clazz);
  }

  @Override
  public <T, S extends T> IReflectionValueProviderBuilder set(final Class<T> clazz, final S service) {
    return set(binding(clazz), service);
  }

  @Override
  public <T> IReflectionValueProviderBuilder set(final Class<T> clazz, final Class<? extends T> serviceClass) {
    return set(binding(clazz), serviceClass);
  }

  @Override
  public <T> IReflectionValueProviderBuilder set(final Class<T> clazz, final IInjectingFactory<T> objectFactory) {
    return set(binding(clazz), objectFactory);
  }

  @Override
  public <T> IReflectionValueProviderBuilder link(final Class<T> clazz, final Class<? extends T> link) {
    return link(binding(clazz), binding(link));
  }

  @Override
  public <T> IReflectionValueProviderBuilder add(final Class<T> clazz, final IInjectingFactory<T> objectFactory) {
    return add(binding(clazz), objectFactory);
  }

  @Override
  public <T> IReflectionValueProviderBuilder add(final Class<T> clazz, final Class<? extends T> serviceClass) {
    return add(binding(clazz), serviceClass);
  }

  @Override
  public <T, S extends T> IReflectionValueProviderBuilder add(final Class<T> clazz, final S service) {
    return add(binding(clazz), service);
  }

  @Override
  public <T> IReflectionValueProviderBuilder link(final IBinding<T> binding, final IBinding<? extends T> link) {
    if (!this.results.containsKey(binding)
        && !this.services.containsKey(binding)
        && !this.valueProvider.contains(binding)) {
      throw new IllegalArgumentException();
    }
    if (this.links.containsKey(binding)) {
      throw new IllegalArgumentException();
    }
    if (this.results.containsKey(link) || this.services.containsKey(link) || this.valueProvider.contains(link)) {
      throw new IllegalArgumentException();
    }
    if (this.links.containsKey(link)) {
      throw new IllegalArgumentException();
    }
    this.links.put(link, binding);
    return this;
  }

  private <T> void checkSingleValue(final IBinding<T> binding) {
    if (this.results.containsKey(binding)
        || this.services.containsKey(binding)
        || this.valueProvider.contains(binding)) {
      throw new IllegalArgumentException();
    }
    if (this.links.containsKey(binding)) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T, S extends T> IReflectionValueProviderBuilder set(final IBinding<T> clazz, final S service) {
    checkSingleValue(clazz);
    this.services.put(clazz, new SingleValueHolder(service));
    return this;
  }

  @Override
  public <T> IReflectionValueProviderBuilder set(final IBinding<T> binding, final Class<? extends T> serviceClass) {
    checkSingleValue(binding);
    final IInjektionAnalyserResult analyserResult = this.analyser.analyse(serviceClass);
    this.results.put(binding, new SingleValueHolder(analyserResult));
    return this;
  }

  @Override
  public <T> IReflectionValueProviderBuilder set(final IBinding<T> binding, final IInjectingFactory<T> objectFactory) {
    checkSingleValue(binding);
    final IInjektionAnalyserResult analyserResult = this.analyser.analyse(objectFactory);
    this.results.put(binding, new SingleValueHolder(analyserResult));
    return this;
  }

  private <T> void checkListResult(final IBinding<T> binding) {
    if (this.links.containsKey(binding)) {
      throw new IllegalArgumentException();
    }
    if (this.services.containsKey(binding)) {
      final IValueHolder valueHolder = this.services.get(binding);
      if (!(valueHolder instanceof ListValueHolder)) {
        throw new IllegalArgumentException();
      }
    }
    if (!this.results.containsKey(binding)) {
      this.results.put(binding, new ListValueHolder());
    }
    final IValueHolder valueHolder = this.results.get(binding);
    if (!(valueHolder instanceof ListValueHolder)) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> IReflectionValueProviderBuilder add(final IBinding<T> binding, final IInjectingFactory<T> objectFactory) {
    checkListResult(binding);
    final IInjektionAnalyserResult analyserResult = this.analyser.analyse(objectFactory);
    ((ListValueHolder) this.results.get(binding)).add(analyserResult);
    return this;
  }

  @Override
  public <T> IReflectionValueProviderBuilder add(final IBinding<T> binding, final Class<? extends T> serviceClass) {
    checkListResult(binding);
    final IInjektionAnalyserResult analyserResult = this.analyser.analyse(serviceClass);
    ((ListValueHolder) this.results.get(binding)).add(analyserResult);
    return this;
  }

  @Override
  public <T, S extends T> IReflectionValueProviderBuilder add(final IBinding<T> binding, final S service) {
    if (this.results.containsKey(binding)) {
      final IValueHolder valueHolder = this.results.get(binding);
      if (!(valueHolder instanceof ListValueHolder)) {
        throw new IllegalArgumentException();
      }
    }
    if (!this.services.containsKey(binding)) {
      this.services.put(binding, new ListValueHolder());
    }
    final IValueHolder valueHolder = this.services.get(binding);
    if (!(valueHolder instanceof ListValueHolder)) {
      throw new IllegalArgumentException();
    }
    ((ListValueHolder) this.services.get(binding)).add(service);
    return this;
  }

  private boolean isResolveable(final IInjektionAnalyserResult analyserResult) {
    if (analyserResult.isIndependent()) {
      return true;
    }
    for (@SuppressWarnings("rawtypes")
    final IBinding binding : analyserResult.getTypes()) {
      if (!this.services.containsKey(binding) && !this.valueProvider.contains(binding)) {
        if (this.results.containsKey(binding)) {
          return false;
        }
        if (analyserResult.isNullable(binding)) {
          continue;
        }
        throw new IllegalStateException("Missing type '" + binding + "' for object '" + analyserResult.getType() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
      if (!this.results.containsKey(binding)) {
        continue;
      }
      if (analyserResult.isIterable(binding)) {
        return false;
      }
      throw new IllegalStateException(
          "Found listvalue of type '" //$NON-NLS-1$
              + binding
              + "' for singlevalue member of object '" //$NON-NLS-1$
              + analyserResult.getType()
              + "'"); //$NON-NLS-1$
    }
    return true;
  }

  @SuppressWarnings("nls")
  @Override
  public IReflectionValueProvider build() throws CreationException {
    final InjektingObjectFactory factory = InjektingObjectFactory.create(this.valueProvider, this.services, this.links);
    int numberOfResults = 0;
    while (!this.results.isEmpty() && numberOfResults != this.results.size()) {
      @SuppressWarnings("rawtypes")
      final Set<IBinding> keySet = new HashSet<>(this.results.keySet());
      numberOfResults = this.results.size();
      for (@SuppressWarnings("rawtypes")
      final IBinding clazz : keySet) {
        final IValueHolder valueHolder = this.results.get(clazz);
        if (valueHolder instanceof SingleValueHolder) {
          final IInjektionAnalyserResult result = (IInjektionAnalyserResult) ((SingleValueHolder) valueHolder)
              .getValue();
          if (isResolveable(result)) {
            final Object object = factory.create(result);
            this.services.put(clazz, new SingleValueHolder(object));
            this.results.remove(clazz);
          }
          continue;
        }
        if (valueHolder instanceof ListValueHolder) {
          final List<Object> analyserResults = ((ListValueHolder) valueHolder).getValue();
          if (!this.services.containsKey(clazz)) {
            this.services.put(clazz, new ListValueHolder());
          }
          for (final Object resultObject : analyserResults) {
            final IInjektionAnalyserResult result = (IInjektionAnalyserResult) resultObject;
            if (isResolveable(result)) {
              final Object object = factory.create(result);
              ((ListValueHolder) this.services.get(clazz)).add(object);
              ((ListValueHolder) valueHolder).remove(result);
              if (((ListValueHolder) valueHolder).isEmty()) {
                this.results.remove(clazz);
              } ;
            }
          }
          continue;
        }
      }
    }
    if (!this.results.isEmpty()) {
      final Optional<String> classes = this.results
          .keySet()
          .stream()
          .filter(v -> v != null)
          .map(v -> v.getBoundedClass())
          .map(v -> v.getName())
          .sorted()
          .reduce((i, v) -> i == null ? v : i + ", " + v);
      throw new CreationException("Couldn't create objects for '" + classes + "'");
    }
    return new ReflectionValueProvider(this.valueProvider, new HashMap<>(this.services), new HashMap<>(this.links));
  }
}
