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

import net.anwiba.commons.utilities.collection.IterableUtilities;

public class BeanBuilder {

  private final String name;
  private final List<Member> members = new ArrayList<>();
  private final List<NamedValueProvider> namedValueProviders = new ArrayList<>();
  private final List<Annotation> annotations = new ArrayList<>();
  private Properties properties;
  private boolean isMutable = true;
  private boolean isArrayNullable = false;
  private boolean isCollectionNullable = false;
  private boolean primitivesEnabled = true;
  private String extend;
  private String comment;
  private Creator creator;
  private boolean isEqualsEnabled = false;
  private boolean isEnableBuilder = false;

  BeanBuilder(final String name) {
    this.name = name;
  }

  public BeanBuilder member(final Member configuration) {
    this.members.add(configuration);
    return this;
  }

  public BeanBuilder annotation(final Annotation annotation) {
    this.annotations.add(annotation);
    return this;
  }

  public BeanBuilder properties(@SuppressWarnings("hiding") final Properties properties) {
    this.properties = properties;
    return this;
  }

  public Bean build() {
    if (this.properties != null) {
      member(
          new Member(
              Builders
                  .type( //
                      java.util.Map.class.getName(), //
                      java.lang.String.class.getName(), //
                      this.properties.type().name()//
                  )
                  .build(), //
              this.properties.name(), //
              null, //
              this.properties.isNullable(), //
              this.properties.isImutable(), //
              IterableUtilities.asList(this.properties.annotations()), //
              null, // Comment
              this.properties.getSetterConfiguration(), //
              this.properties.getGetterConfiguration(), //
              (model, instance, field) -> {},
              (model, instance, field) -> {}));
      if (this.properties.isNamedValueProviderEnabled()) {
        namedValueProviders(this.properties.getNamedValueProvider());
      }
    }
    return new Bean(
        this.comment,
        this.name,
        this.extend,
        this.annotations,
        this.isMutable,
        this.isEnableBuilder,
        this.isArrayNullable,
        this.isCollectionNullable,
        this.members,
        this.namedValueProviders,
        this.creator,
        this.isEqualsEnabled,
        this.primitivesEnabled);
  }

  private void namedValueProviders(final NamedValueProvider namedValueProvider) {
    this.namedValueProviders.add(namedValueProvider);
  }

  public void setMutable(final boolean isMutable) {
    this.isMutable = isMutable;
  }

  public void setEnableBuilder(final boolean isEnableBuilder) {
    this.isEnableBuilder = isEnableBuilder;
  }

  public void extend(@SuppressWarnings("hiding") final String extend) {
    this.extend = extend;
  }

  public void comment(@SuppressWarnings("hiding") final String comment) {
    this.comment = comment;
  }

  public void setArrayNullable(final boolean isArrayNullable) {
    this.isArrayNullable = isArrayNullable;
  }

  public void setCollectionNullable(final boolean isCollectionNullable) {
    this.isCollectionNullable = isCollectionNullable;
  }

  public void creator(@SuppressWarnings("hiding") final Creator creator) {
    this.creator = creator;
  }

  public void setEqualsEnabled(final boolean isEqualsEnabled) {
    this.isEqualsEnabled = isEqualsEnabled;
  }

  public void setPrimitivesEnabled(final boolean primitivesEnabled) {
    this.primitivesEnabled = primitivesEnabled;
  }
}