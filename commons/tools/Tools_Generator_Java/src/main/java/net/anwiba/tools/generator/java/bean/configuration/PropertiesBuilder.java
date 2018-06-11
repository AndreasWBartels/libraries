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
import java.util.HashMap;
import java.util.List;

public class PropertiesBuilder {

  private static final String GET_VALUE = "getValue"; //$NON-NLS-1$
  private static final String GET_NAMES = "getNames"; //$NON-NLS-1$
  private final Type type;
  private final List<Annotation> annotations = new ArrayList<>();
  private boolean isSetterEnabled = true;
  private final List<Annotation> setterAnnotations = new ArrayList<>();
  private boolean isGetterEnabled = true;
  private final List<Annotation> getterAnnotations = new ArrayList<>();
  private boolean isNullable = false;
  private boolean implementsNamedValueProvider = false;
  private final String name;
  private String setterName;
  private String getterName;
  private boolean singleValueEnabled = false;
  private boolean namedValueGetterEnabled;
  private final List<Annotation> namesGetterAnnotations = new ArrayList<>();
  private final String namesGetterMethodName = GET_NAMES;
  private final String valueGetterMethodName = GET_VALUE;
  private boolean isImutable = false;
  private String injectionAnnotationName = null;

  PropertiesBuilder(final Type type, final String name) {
    this.type = type;
    this.name = name;
  }

  public Properties build() {
    final Setter setter = new Setter(
        this.setterName == null ? MemberBuilder.createSetterName(this.name) : this.setterName,
        this.isSetterEnabled,
        this.singleValueEnabled,
        true,
        this.injectionAnnotationName != null,
        this.injectionAnnotationName,
        this.setterAnnotations,
        new Argument(this.name, new ArrayList<Annotation>(), this.type),
        new HashMap<String, List<Annotation>>());
    @SuppressWarnings("hiding")
    final String getterName = this.getterName == null
        ? MemberBuilder.createGetterName(this.type, this.name)
        : this.getterName;
    final Getter getter = new Getter(
        getterName,
        this.isGetterEnabled,
        this.namedValueGetterEnabled,
        this.getterAnnotations);
    return new Properties(
        this.type,
        this.name,
        setter,
        getter,
        this.isImutable,
        this.isNullable,
        this.implementsNamedValueProvider,
        this.annotations,
        new NamedValueProvider(
            Builders.type(this.type.name()).build(),
            this.name,
            true,
            this.namesGetterAnnotations,
            this.namesGetterMethodName,
            new ArrayList<Annotation>(),
            this.valueGetterMethodName));
  }

  public PropertiesBuilder isNullable(@SuppressWarnings("hiding") final boolean isNullable) {
    this.isNullable = isNullable;
    return this;
  }

  public PropertiesBuilder isImutable(@SuppressWarnings("hiding") final boolean isImutable) {
    this.isImutable = isImutable;
    return this;
  }

  public PropertiesBuilder setSetterEnabled(final boolean isSetterEnabled) {
    this.isSetterEnabled = isSetterEnabled;
    return this;
  }

  public PropertiesBuilder setGetterEnabled(final boolean isGetterEnabled) {
    this.isGetterEnabled = isGetterEnabled;
    return this;
  }

  public PropertiesBuilder setImplementsNamedValueProvider(final boolean implementsNamedValueProvider) {
    this.implementsNamedValueProvider = implementsNamedValueProvider;
    return this;
  }

  public PropertiesBuilder annotation(final Annotation annotation) {
    if (annotation == null) {
      return this;
    }
    this.annotations.add(annotation);
    return this;
  }

  public PropertiesBuilder namesGetterAnnotation(final Annotation annotation) {
    if (annotation == null) {
      return this;
    }
    this.namesGetterAnnotations.add(annotation);
    return this;
  }

  public PropertiesBuilder setterAnnotation(final Annotation annotation) {
    if (annotation == null) {
      return this;
    }
    this.setterAnnotations.add(annotation);
    return this;
  }

  public PropertiesBuilder getterAnnotation(final Annotation annotation) {
    if (annotation == null) {
      return this;
    }
    this.getterAnnotations.add(annotation);
    return this;
  }

  public PropertiesBuilder getterName(@SuppressWarnings("hiding") final String name) {
    this.getterName = name;
    return this;
  }

  public PropertiesBuilder setterName(final String name) {
    this.setterName = name;
    return this;
  }

  public void setSingleValueSetterEnabled(final boolean singleValueEnabled) {
    this.singleValueEnabled = singleValueEnabled;
  }

  public void setNamedValueGetterEnabled(final boolean namedValueGetterEnabled) {
    this.namedValueGetterEnabled = namedValueGetterEnabled;
  }

  public void setInjectionAnnotationName(final String injectionAnnotationName) {
    this.injectionAnnotationName = injectionAnnotationName;
  }
}
