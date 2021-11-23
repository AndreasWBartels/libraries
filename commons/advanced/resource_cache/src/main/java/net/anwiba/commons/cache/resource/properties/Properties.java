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
package net.anwiba.commons.cache.resource.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Properties implements IProperties {

  private static final long serialVersionUID = 655956184779585973L;
  private final List<String> names = new ArrayList<>();
  private final List<IProperty> properties = new ArrayList<>();
  private final Map<String, IProperty> map = new HashMap<>();

  public static IProperties empty() {
    return builder().build();
  }

  public static PropertiesBuilder builder() {
    return new PropertiesBuilder();
  }

  public Properties(final List<IProperty> properties) {
    for (final IProperty property : properties) {
      this.names.add(property.getName());
      this.map.put(property.getName(), property);
    }
    this.names.forEach(name -> this.properties.add(this.map.get(name)));
  }

  @Override
  public Iterable<String> getNames() {
    return this.names;
  }

  @Override
  public <T extends Serializable> T getValue(final String name)  {
    if (!this.map.containsKey(name)) {
      return null;
    }
    return this.map.get(name).getValue();
  }

  @Override
  public Iterator<IProperty> iterator() {
    return properties.iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.properties == null)
        ? 0
        : this.properties.hashCode());
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
    if (this.properties == null) {
      if (other.properties != null) {
        return false;
      }
    } else if (!this.properties.equals(other.properties)) {
      return false;
    }
    return true;
  }

}