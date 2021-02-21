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

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class Member {

  private final Type type;
  private final String name;
  private final Setter setter;
  private final Getter getter;
  private final List<Annotation> annotations = new ArrayList<>();
  private final Object value;
  private final boolean isNullable;
  private final boolean isImutable;
  private final IValueOfMethodFactory valueOfMethodFactory;
  private final IAsObjectMethodFactory asObjectMethodFactory;
  private final String comment;

  public Member(
      final Type type,
      final String name,
      final Object value,
      final boolean isNullable,
      final boolean isImutable,
      final List<Annotation> annotationConfigurations,
      final String comment,
      final Setter setter,
      final Getter getter,
      final IValueOfMethodFactory valueOfMethodFactory,
      final IAsObjectMethodFactory asObjectMethodFactory) {
    this.type = type;
    this.name = name;
    this.value = value;
    this.isNullable = isNullable;
    this.isImutable = isImutable;
    this.comment = comment;
    this.setter = setter;
    this.getter = getter;
    this.valueOfMethodFactory = valueOfMethodFactory;
    this.asObjectMethodFactory = asObjectMethodFactory;
    this.annotations.addAll(annotationConfigurations);
  }

  public Type type() {
    return this.type;
  }

  public String name() {
    return this.name;
  }

  public Setter setter() {
    return this.setter;
  }

  public Getter getter() {
    return this.getter;
  }

  public Object value() {
    return this.value;
  }

  public boolean isNullable() {
    return this.isNullable;
  }

  public Iterable<Annotation> annotations() {
    return this.annotations;
  }

  public IOptional<String, RuntimeException> comment() {
    return Optional.of(this.comment);
  }

  public boolean isImutable() {
    return this.isImutable;
  }

  public IAsObjectMethodFactory asObjectMethodFactory() {
    return this.asObjectMethodFactory;
  }

  public IValueOfMethodFactory valueOfMethodFactory() {
    return this.valueOfMethodFactory;
  }
}
