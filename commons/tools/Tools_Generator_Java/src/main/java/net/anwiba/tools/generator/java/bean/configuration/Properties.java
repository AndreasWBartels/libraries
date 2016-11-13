/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.generator.java.bean.configuration;

import java.util.ArrayList;
import java.util.List;

public class Properties {

  private final Type type;
  private final Setter setter;
  private final Getter getter;
  private final List<Annotation> annotations = new ArrayList<>();
  private final boolean isNamedValueProviderEnabled;
  private final String name;
  private final boolean isNullable;
  private final NamedValueProvider namedValueProvider;
  private final boolean isImutable;

  public Properties(
      final Type type,
      final String name,
      final Setter setter,
      final Getter getter,
      final boolean isImutable,
      final boolean isNullable,
      final boolean isNamedValueProviderEnabled,
      final List<Annotation> annotationConfigurations,
      final NamedValueProvider namedValueProvider) {
    this.type = type;
    this.name = name;
    this.setter = setter;
    this.getter = getter;
    this.isImutable = isImutable;
    this.isNullable = isNullable;
    this.namedValueProvider = namedValueProvider;
    this.isNamedValueProviderEnabled = isNamedValueProviderEnabled && namedValueProvider != null;
    this.annotations.addAll(annotationConfigurations);
  }

  public Type type() {
    return this.type;
  }

  public Setter getSetterConfiguration() {
    return this.setter;
  }

  public Getter getGetterConfiguration() {
    return this.getter;
  }

  public Iterable<Annotation> annotations() {
    return this.annotations;
  }

  public boolean isNamedValueProviderEnabled() {
    return this.isNamedValueProviderEnabled;
  }

  public String name() {
    return this.name;
  }

  public boolean isNullable() {
    return this.isNullable;
  }

  public NamedValueProvider getNamedValueProvider() {
    return this.namedValueProvider;
  }

  public boolean isImutable() {
    return this.isImutable;
  }
}
