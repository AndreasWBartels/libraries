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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.anwiba.commons.lang.stream.Streams;

public final class PropertiesBuilder {

  private final Map<String, IProperty> properties = new LinkedHashMap<>();

  PropertiesBuilder() {
  }

  PropertiesBuilder(final IProperties properties) {
    Streams.of(properties.properties()).foreach(p -> this.properties.put(p.getName(), p));
  }

  public PropertiesBuilder put(final String name, final String value) {
    return put(new Property(name, value));
  }

  public PropertiesBuilder put(final IProperty property) {
    this.properties.put(property.getName(), property);
    return this;
  }

  public IProperties build() {
    return new Properties(new ArrayList<>(this.properties.values()));
  }

  public PropertiesBuilder consume(final Consumer<PropertiesBuilder> consumer) {
    consumer.accept(this);
    return this;
  }

  public PropertiesBuilder putIfAbsent(final String name, final String value) {
    return putIfAbsent(new Property(name, value));
  }

  private PropertiesBuilder putIfAbsent(final Property property) {
    this.properties.putIfAbsent(property.getName(), property);
    return this;
  }

  public PropertiesBuilder putIfAbsentIgnoreCase(final String name, final String value) {
    return putIfAbsentIgnoreCase(new Property(name, value));
  }

  private PropertiesBuilder putIfAbsentIgnoreCase(final Property property) {
    if (this.properties.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(property.getName()))) {
      return this;
    }
    this.properties.put(property.getName(), property);
    return this;
  }
}