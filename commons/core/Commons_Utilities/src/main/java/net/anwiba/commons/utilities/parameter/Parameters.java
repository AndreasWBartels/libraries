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
package net.anwiba.commons.utilities.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Parameters implements IParameters {

  private static final long serialVersionUID = 655956184779585973L;
  private final List<String> names = new ArrayList<>();
  private final List<IParameter> parameters = new ArrayList<>();
  private final Map<String, IParameter> map = new HashMap<>();

  public Parameters(final List<IParameter> parameters) {
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
  public boolean containts(final String name) {
    return this.map.containsKey(name);
  }
}