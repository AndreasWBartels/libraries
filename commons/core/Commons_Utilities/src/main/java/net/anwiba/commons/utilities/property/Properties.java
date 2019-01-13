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
package net.anwiba.commons.utilities.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Properties implements IProperties {

  private static final long serialVersionUID = 655956184779585973L;
  private final List<String> names = new ArrayList<>();
  private final List<IProperty> parameters = new ArrayList<>();
  private final Map<String, IProperty> map = new HashMap<>();

  public Properties(final List<IProperty> parameters) {
    this.parameters.addAll(parameters);
    for (final IProperty parameter : parameters) {
      this.names.add(parameter.getName());
      this.map.put(parameter.getName(), parameter);
    }
  }

  @Override
  public int getNumberOfProperties() {
    return this.parameters.size();
  }

  @Override
  public IProperty getProperty(final int index) {
    return this.parameters.get(index);
  }

  @Override
  public IProperties adapt(final int index, final IProperty parameter) {
    final IProperty[] array = this.parameters.toArray(new IProperty[this.parameters.size()]);
    array[index] = parameter;
    return new Properties(Arrays.asList(array));
  }

  @Override
  public Iterable<IProperty> properties() {
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
  public boolean contains(final String name) {
    return this.map.containsKey(name);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.map == null) ? 0 : this.map.hashCode());
    result = prime * result + ((this.names == null) ? 0 : this.names.hashCode());
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
    final Properties other = (Properties) obj;
    if (this.map == null) {
      if (other.map != null) {
        return false;
      }
    } else if (!this.map.equals(other.map)) {
      return false;
    }
    if (this.names == null) {
      if (other.names != null) {
        return false;
      }
    } else if (!this.names.equals(other.names)) {
      return false;
    }
    if (this.parameters == null) {
      if (other.parameters != null) {
        return false;
      }
    } else if (!this.parameters.equals(other.parameters)) {
      return false;
    }
    return true;
  }

}