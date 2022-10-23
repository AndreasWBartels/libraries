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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.lang.stream.Streams;

public final class Properties implements IProperties {

  private final Map<String, IProperty> map = new LinkedHashMap<>();

  public static IProperties empty() {
    return builder().build();
  }

  public static PropertiesBuilder builder() {
    return new PropertiesBuilder();
  }

  public static PropertiesBuilder builder(IProperties properties) {
    return new PropertiesBuilder(properties);
  }

  public Properties(final List<IProperty> properties) {
    Streams.of(properties)
        .filter(property -> property.getName() != null)
        .foreach(property -> this.map.put(property.getName(), property));
  }

  @Override
  public Iterable<IProperty> properties() {
    return this.map.values();
  }

  @Override
  public Iterable<String> getNames() {
    return this.map.keySet();
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
    result = prime * result + this.map.hashCode();
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
    if (!this.map.equals(other.map)) {
      return false;
    }
    return true;
  }

  @Override
  public String getValueOrDefault(final String name, final String defaultValue) {
    if (!this.map.containsKey(name)) {
      return defaultValue;
    }
    return this.map.get(name).getValue();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

}