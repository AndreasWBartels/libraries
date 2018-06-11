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

import java.util.List;

public class NamedValueProvider {

  private final Type type;
  private final String fieldName;
  private final List<Annotation> namesMethodAnnotations;
  private final String namesMethodName;
  private final List<Annotation> valueGetterMethodAnnotations;
  private final String valueGetterMethodName;
  private final boolean isNameGetterEnabled;

  public NamedValueProvider(
      final Type type,
      final String fieldName,
      final boolean isNameGetterEnabled,
      final List<Annotation> namesMethodAnnotations,
      final String namesMethodName,
      final List<Annotation> valueGetterMethodAnnotations,
      final String valueGetterMethodName) {
    this.type = type;
    this.fieldName = fieldName;
    this.isNameGetterEnabled = isNameGetterEnabled;
    this.namesMethodAnnotations = namesMethodAnnotations;
    this.namesMethodName = namesMethodName;
    this.valueGetterMethodAnnotations = valueGetterMethodAnnotations;
    this.valueGetterMethodName = valueGetterMethodName;
  }

  public Type getType() {
    return this.type;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  public Iterable<Annotation> getNamesMethodAnnotations() {
    return this.namesMethodAnnotations;
  }

  public String getNamesMethodName() {
    return this.namesMethodName;
  }

  public Iterable<Annotation> getValueGetterMethodAnnotations() {
    return this.valueGetterMethodAnnotations;
  }

  public String getValueGetterMethodName() {
    return this.valueGetterMethodName;
  }

  public boolean isNameGetterEnabled() {
    return this.isNameGetterEnabled;
  }
}
