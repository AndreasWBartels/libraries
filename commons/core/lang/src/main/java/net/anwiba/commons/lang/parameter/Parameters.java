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
package net.anwiba.commons.lang.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public final class Parameters implements IParameters {

  private static final long serialVersionUID = -1L;

  private final List<IParameter> parameters = new ArrayList<>();
  private final List<String> names = new ArrayList<>();
  private final Map<String, IParameter> map = new HashMap<>();

  public static Parameters empty() {
    return of(List.of());
  }

  public static Parameters of(final IParameter... parameters) {
    return of(List.of(parameters));
  }

  public static Parameters of(final List<IParameter> parameters) {
    return new Parameters(parameters);
  }

  Parameters(final List<IParameter> parameters) {
    this.parameters.addAll(parameters);
    for (final IParameter parameter : parameters) {
      this.names.add(parameter.getName());
      this.map.put(parameter.getName(), parameter);
    }
  }

  @Override
  public int getNumberOfParameter() {
    return this.parameters.size();
  }

  @Override
  public IParameter getParameter(final int index) {
    return this.parameters.get(index);
  }

  @Override
  public IParameters adapt(final int index, final IParameter parameter) {
    final IParameter[] array = this.parameters.toArray(new IParameter[this.parameters.size()]);
    array[index] = parameter;
    return new Parameters(Arrays.asList(array));
  }

  @Override
  public Iterable<IParameter> parameters() {
    return this.parameters;
  }

  @Override
  public IParameters toLowerCase() {
    return new Parameters(
        this.parameters.stream().map(p -> new Parameter(p.getName(), p.getValue())).collect(Collectors.toList()));
  }

  @Override
  public Iterable<String> getNames() {
    return this.names;
  }

  @Override
  public String getValue(final String name) throws RuntimeException {
    if (!this.map.containsKey(name)) {
      return null;
    }
    return this.map.get(name).getValue();
  }

  @Override
  public boolean contains(final String name) {
    return this.map.containsKey(name);
  }

  @Override
  public void forEach(final Consumer<IParameter> consumer) {
    this.parameters.forEach(consumer);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.parameters == null) ? 0 : this.parameters.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Parameters other = (Parameters) obj;
    if (this.parameters == null) {
      if (other.parameters != null) {
        return false;
      }
    } else if (!this.parameters.equals(other.parameters)) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    return this.parameters.isEmpty();
  }

  @Override
  public IOptional<IParameter, RuntimeException> getParameter(final String name) {
    return Optional.of(this.map.get(name));
  }

  @Override
  public IParameters toSortedByName() {
    return new Parameters(this.parameters.stream().sorted().collect(Collectors.toList()));
  }

  public static ParametersBuilder builder() {
    return ParametersBuilder.of();
  }
}