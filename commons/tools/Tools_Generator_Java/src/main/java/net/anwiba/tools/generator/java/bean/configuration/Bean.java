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
import java.util.Map;

public class Bean {

  private final String name;
  private final List<Member> members = new ArrayList<>();
  private final Map<String, Member> membersByName = new HashMap<>();
  private final Map<String, NamedValueProvider> namedValueProviders = new HashMap<>();
  private final List<Annotation> annotations = new ArrayList<>();
  private final boolean mutable;
  private final String extend;
  private final String comment;
  private final boolean arrayNullable;
  private final boolean collectionNullable;
  private final Creator creator;
  private final boolean equalsEnabled;
  private final boolean primitivesEnabled;
  private final boolean isBuilderEnabled;

  Bean(
      final String comment,
      final String name,
      final String extend,
      final List<Annotation> annotationConfigurations,
      final boolean mutable,
      final boolean isBuilderEnabled,
      final boolean arrayNullable,
      final boolean collectionNullable,
      final List<Member> members,
      final List<NamedValueProvider> namedValueProviders,
      final Creator creator,
      final boolean isEqualsEnabled,
      final boolean primitivesEnabled) {
    this.comment = comment;
    this.name = name;
    this.extend = extend;
    this.mutable = mutable;
    this.isBuilderEnabled = isBuilderEnabled;
    this.arrayNullable = arrayNullable;
    this.collectionNullable = collectionNullable;
    this.creator = creator;
    this.equalsEnabled = isEqualsEnabled;
    this.primitivesEnabled = primitivesEnabled;
    this.annotations.addAll(annotationConfigurations);
    this.members.addAll(members);
    for (final Member configuration : members) {
      this.membersByName.put(configuration.name(), configuration);
    }
    for (final NamedValueProvider namedValueProvider : namedValueProviders) {
      this.namedValueProviders.put(namedValueProvider.getFieldName(), namedValueProvider);
    }
  }

  public Iterable<Member> members() {
    return this.members;
  }

  public Iterable<Annotation> annotations() {
    return this.annotations;
  }

  public String name() {
    return this.name;
  }

  public boolean isMutable() {
    return this.mutable;
  }

  public boolean isBuilderEnabled() {
    return this.isBuilderEnabled;
  }

  public Member member(final String name) {
    if (name.startsWith("_") && this.membersByName.containsKey(name.substring(1, name.length()))) { //$NON-NLS-1$
      return this.membersByName.get(name.substring(1, name.length()));
    }
    return this.membersByName.get(name);
  }

  public NamedValueProvider namedValueProvider(final String name) {
    return this.namedValueProviders.get(name);
  }

  public String extend() {
    return this.extend;
  }

  public String comment() {
    return this.comment;
  }

  public boolean isArrayNullable() {
    return this.arrayNullable;
  }

  public boolean isCollectionNullable() {
    return this.collectionNullable;
  }

  public Creator creator() {
    return this.creator;
  }

  public boolean isEqualsEnabled() {
    return this.equalsEnabled;
  }

  public boolean isPrimitivesEnabled() {
    return this.primitivesEnabled;
  }
}
