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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.anwiba.commons.injection.impl.BindingFactory;
import net.anwiba.commons.injection.impl.IInjektionAnalyserResult;
import net.anwiba.commons.injection.impl.IResolveableForecaster;
import net.anwiba.commons.injection.impl.IValueInjectionAnalyser;
import net.anwiba.commons.injection.impl.InjectionValueProvider;
import net.anwiba.commons.injection.impl.InjektingObjectFactory;
import net.anwiba.commons.injection.impl.ResolveableForecaster;
import net.anwiba.commons.injection.impl.ValueInjectionAnalyser;
import net.anwiba.commons.injection.utilities.IValueHolder;
import net.anwiba.commons.injection.utilities.ListValueHolder;
import net.anwiba.commons.injection.utilities.SingleValueHolder;
import net.anwiba.commons.reflection.CreationException;

public class InjectionValueProviderBuilder implements IInjectionValueProviderBuilder {

  private final IBindingFactory bindingFactory = new BindingFactory();
  private final IValueInjectionAnalyser analyser = new ValueInjectionAnalyser();
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IValueHolder> services = new HashMap<>();
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IValueHolder> results = new HashMap<>();
  @SuppressWarnings("rawtypes")
  private final Map<IBinding, IBinding> links = new HashMap<>();

  private final IInjectionValueProvider valueProvider;
  private final IScope scope;

  public InjectionValueProviderBuilder(final IScope scope) {
    this(scope, new DefaultInjectionValueProvider());
  }

  public InjectionValueProviderBuilder(final IScope scope, final IInjectionValueProvider valueProvider) {
    this.scope = scope;
    this.valueProvider = valueProvider;
  }

  private <T> IBinding<T> binding(final Class<T> clazz) {
    return this.bindingFactory.create(clazz, null);
  }

  @Override
  public <T, S extends T> IInjectionValueProviderBuilder set(final Class<T> clazz, final S service) {
    return set(binding(clazz), service);
  }

  @Override
  public <T> IInjectionValueProviderBuilder set(final Class<T> clazz) {
    if (clazz.isInterface()) {
      throw new IllegalArgumentException();
    }
    return set(clazz, clazz);
  }

  @Override
  public <T> IInjectionValueProviderBuilder set(final Class<T> clazz, final Class<? extends T> serviceClass) {
    return set(binding(clazz), serviceClass);
  }

  @Override
  public <T> IInjectionValueProviderBuilder set(final Class<T> clazz, final IInjectingFactory<T> objectFactory) {
    return set(binding(clazz), objectFactory);
  }

  @Override
  public <T> IInjectionValueProviderBuilder link(final Class<? extends T> clazz, final Class<T> link) {
    return link(binding(clazz), binding(link));
  }

  @Override
  public <T> IInjectionValueProviderBuilder add(final Class<T> clazz, final IInjectingFactory<T> objectFactory) {
    return add(binding(clazz), objectFactory);
  }

  @Override
  public <T> IInjectionValueProviderBuilder add(final Class<T> clazz) {
    if (clazz.isInterface()) {
      throw new IllegalArgumentException();
    }
    return add(clazz, clazz);
  }

  @Override
  public <T> IInjectionValueProviderBuilder add(final Class<T> clazz, final Class<? extends T> serviceClass) {
    return add(binding(clazz), serviceClass);
  }

  @Override
  public <T, S extends T> IInjectionValueProviderBuilder add(final Class<T> clazz, final S service) {
    return add(binding(clazz), service);
  }

  @Override
  public <T> IInjectionValueProviderBuilder link(final IBinding<? extends T> binding, final IBinding<T> link) {
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
    if (this.results.containsKey(binding) || this.services.containsKey(binding)) {
      throw new IllegalArgumentException(
          "Scope: '" //$NON-NLS-1$
              + this.scope
              + "', double registration of single value '" //$NON-NLS-1$
              + binding.getBoundedClass().getName()
              + "'"); //$NON-NLS-1$
    }
    if (this.links.containsKey(binding)) {
      throw new IllegalArgumentException(
          "Scope: '" //$NON-NLS-1$
              + this.scope
              + "', double registration of single value '" //$NON-NLS-1$
              + binding.getBoundedClass().getName()
              + "'"); //$NON-NLS-1$
    }

    if (this.valueProvider.contains(binding)) {
      if (this.valueProvider.getAll(binding).size() > 1) {
        throw new IllegalArgumentException(
            "Scope: '" //$NON-NLS-1$
                + this.scope
                + "', double registration of single value '" //$NON-NLS-1$
                + binding.getBoundedClass().getName()
                + "', can't overide list values"); //$NON-NLS-1$
      }
    }
  }

  @Override
  public <T, S extends T> IInjectionValueProviderBuilder set(final IBinding<T> clazz, final S service) {
    checkSingleValue(clazz);
    this.services.put(clazz, new SingleValueHolder(service));
    return this;
  }

  @Override
  public <T> IInjectionValueProviderBuilder set(final IBinding<T> binding, final Class<? extends T> serviceClass) {
    checkSingleValue(binding);
    final IInjektionAnalyserResult analyserResult = this.analyser.analyse(serviceClass);
    this.results.put(binding, new SingleValueHolder(analyserResult));
    return this;
  }

  @Override
  public <T> IInjectionValueProviderBuilder set(final IBinding<T> binding, final IInjectingFactory<T> objectFactory) {
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
  public <T> IInjectionValueProviderBuilder add(final IBinding<T> binding, final IInjectingFactory<T> objectFactory) {
    checkListResult(binding);
    final IInjektionAnalyserResult analyserResult = this.analyser.analyse(objectFactory);
    ((ListValueHolder) this.results.get(binding)).add(analyserResult);
    return this;
  }

  @Override
  public <T> IInjectionValueProviderBuilder add(final IBinding<T> binding, final Class<? extends T> serviceClass) {
    checkListResult(binding);
    final IInjektionAnalyserResult analyserResult = this.analyser.analyse(serviceClass);
    ((ListValueHolder) this.results.get(binding)).add(analyserResult);
    return this;
  }

  @Override
  public <T, S extends T> IInjectionValueProviderBuilder add(final IBinding<T> binding, final S service) {
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

  @SuppressWarnings("nls")
  @Override
  public IInjectionValueProvider build() throws CreationException {
    final InjektingObjectFactory factory = InjektingObjectFactory.create(this.valueProvider, this.services, this.links);
    final IResolveableForecaster forecaster = new ResolveableForecaster(
        this.valueProvider,
        this.services,
        this.links,
        this.analyser,
        this.results);

    int numberOfResults = 0;
    while (!this.results.isEmpty() && numberOfResults != this.results.size()) {
      @SuppressWarnings("rawtypes")
      final Set<IBinding> keySet = new HashSet<>(this.results.keySet());
      numberOfResults = this.results.size();
      for (@SuppressWarnings("rawtypes")
      final IBinding binding : keySet) {
        final IValueHolder valueHolder = this.results.get(binding);
        if (valueHolder instanceof SingleValueHolder) {
          final IInjektionAnalyserResult result = (IInjektionAnalyserResult) ((SingleValueHolder) valueHolder)
              .getValue();
          if (forecaster.isResolveable(result)) {
            final Object object = factory.create(result);
            this.services.put(binding, new SingleValueHolder(object));
            this.results.remove(binding);
          }
          continue;
        }
        if (valueHolder instanceof ListValueHolder) {
          final List<Object> analyserResults = ((ListValueHolder) valueHolder).getValue();
          if (!this.services.containsKey(binding)) {
            this.services.put(binding, new ListValueHolder());
          }
          for (final Object resultObject : analyserResults) {
            final IInjektionAnalyserResult result = (IInjektionAnalyserResult) resultObject;
            if (forecaster.isResolveable(result)) {
              final Object object = factory.create(result);
              ((ListValueHolder) this.services.get(binding)).add(object);
              ((ListValueHolder) valueHolder).remove(result);
              if (((ListValueHolder) valueHolder).isEmty()) {
                this.results.remove(binding);
              }
            }
          }
          continue;
        }
      }
    }
    if (!this.results.isEmpty()) {
      final MissingBindingStringFactory stringFactory = new MissingBindingStringFactory(
          forecaster,
          this.results,
          this.services,
          this.links,
          this.valueProvider);
      final Optional<String> classes = this.results
          .keySet()
          .stream()
          .filter(v -> v != null)
          .map(v -> stringFactory.create(v))
          .sorted()
          .reduce((i, v) -> i == null ? v : i + "\n" + v);
      throw new CreationException(
          "Scope: '" + this.scope + "', couldn't create objects for '" + classes.orElse("---") + "'");
    }
    return new InjectionValueProvider(this.valueProvider, new HashMap<>(this.services), new HashMap<>(this.links));
  }

  @SuppressWarnings("rawtypes")
  public static class MissingBindingStringFactory {

    private final Map<IBinding, IValueHolder> results;
    private final Map<IBinding, IValueHolder> services;
    private final Map<IBinding, IBinding> links;
    private final IInjectionValueProvider valueProvider;
    private final IResolveableForecaster forecaster;

    public MissingBindingStringFactory(
        final IResolveableForecaster forecaster,
        final Map<IBinding, IValueHolder> results,
        final Map<IBinding, IValueHolder> services,
        final Map<IBinding, IBinding> links,
        final IInjectionValueProvider valueProvider) {
      this.forecaster = forecaster;
      this.results = results;
      this.services = services;
      this.links = links;
      this.valueProvider = valueProvider;
    }

    @SuppressWarnings({ "nls" })
    public String create(final IBinding binding) {
      final IValueHolder valueHolder = this.results.get(binding);
      final StringBuilder builder = new StringBuilder();
      builder.append(binding);
      builder.append(" missing (");
      if (valueHolder instanceof SingleValueHolder) {
        final IInjektionAnalyserResult result = (IInjektionAnalyserResult) ((SingleValueHolder) valueHolder).getValue();
        addIfMissed(builder, result);
        builder.append(")");
        return builder.toString();
      }
      if (valueHolder instanceof ListValueHolder) {
        final List<Object> analyserResults = ((ListValueHolder) valueHolder).getValue();
        for (final Object resultObject : analyserResults) {
          final IInjektionAnalyserResult result = (IInjektionAnalyserResult) resultObject;
          addIfMissed(builder, result);
        }
      }
      builder.append(")");
      return builder.toString();
    }

    private void addIfMissed(final StringBuilder builder, final IInjektionAnalyserResult result) {
      if (!this.forecaster.isResolveable(result)) {
        result.getBindings().forEach(b -> {
          if (!contains(b)) {
            builder.append(b);
            builder.append(" "); //$NON-NLS-1$
          }
        });
      }
    }

    boolean contains(final IBinding binding) {
      return this.services.containsKey(binding)
          || (this.links.containsKey(binding) && this.services.containsKey(this.links.get(binding)))
          || this.valueProvider.contains(binding);
    }

  }
}
