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

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.tools.generator.java.bean.factory.IAsObjectMethodFactory;
import net.anwiba.tools.generator.java.bean.factory.IValueOfMethodFactory;

@SuppressWarnings("nls")
public class MemberBuilder {

  private final String name;
  private final Type type;
  private boolean isNullable = true;
  private boolean isSetterEnabled = true;
  private boolean isGetterEnabled = true;
  private Object value;
  private final List<Annotation> annotations = new ArrayList<>();
  private final List<Annotation> setterAnnotations = new ArrayList<>();
  private final List<Annotation> setterArgumentAnnotations = new ArrayList<>();
  private final List<Annotation> getterAnnotations = new ArrayList<>();
  private boolean isMultiValue = false;
  private final boolean isSingleValue = true;
  private boolean isImutable = false;
  private IValueOfMethodFactory valueOfMethodFactory;
  private IAsObjectMethodFactory asObjectMethodFactory;

  MemberBuilder(final Type type, final String name) {
    this.type = type;
    this.name = name;
  }

  public Member build() {
    final HashMap<String, List<Annotation>> map = new HashMap<>();
    map.put(this.name, this.setterArgumentAnnotations);
    final Setter setter = new Setter(
        createSetterName(this.name),
        this.isSetterEnabled,
        this.isSingleValue,
        this.isMultiValue,
        false,
        null,
        this.setterAnnotations,
        new Argument(this.name, this.setterArgumentAnnotations, this.type),
        map);
    final Getter getter = new Getter(
        createGetterName(this.type, this.name),
        this.isGetterEnabled,
        false,
        this.getterAnnotations);
    return new Member(
        this.type,
        this.name,
        this.value,
        this.isNullable,
        this.isImutable,
        this.annotations,
        setter,
        getter,
        this.valueOfMethodFactory == null ? (model, instance, field) -> {
        } : this.valueOfMethodFactory,
        this.asObjectMethodFactory == null ? (model, instance, field) -> {
        } : this.asObjectMethodFactory);
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final String value) {
    this.value = value;
    return this;
  }

  public MemberBuilder value(final String[] values) {
    this.value = values;
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final short value) {
    this.value = Short.valueOf(value);
    return this;
  }

  public MemberBuilder value(final short[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final int value) {
    this.value = Integer.valueOf(value);
    return this;
  }

  public MemberBuilder value(final int[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final long value) {
    this.value = Long.valueOf(value);
    return this;
  }

  public MemberBuilder value(final long[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final float value) {
    this.value = Float.valueOf(value);
    return this;
  }

  public MemberBuilder value(final float[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final double value) {
    this.value = Double.valueOf(value);
    return this;
  }

  public MemberBuilder value(final double[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final char value) {
    this.value = Character.valueOf(value);
    return this;
  }

  public MemberBuilder value(final char[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final byte value) {
    this.value = Byte.valueOf(value);
    return this;
  }

  public MemberBuilder value(final byte[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder value(@SuppressWarnings("hiding") final boolean value) {
    this.value = Boolean.valueOf(value);
    return this;
  }

  public MemberBuilder value(final boolean[] values) {
    this.value = ArrayUtilities.objects(values);
    return this;
  }

  public MemberBuilder isMultiValue(@SuppressWarnings("hiding") final boolean isMultiValue) {
    this.isMultiValue = isMultiValue;
    return this;
  }

  public MemberBuilder isSetterEnabled(@SuppressWarnings("hiding") final boolean isSetterEnabled) {
    this.isSetterEnabled = isSetterEnabled;
    return this;
  }

  public MemberBuilder isGetterEnabled(@SuppressWarnings("hiding") final boolean isGetterEnabled) {
    this.isGetterEnabled = isGetterEnabled;
    return this;
  }

  public MemberBuilder isNullable(@SuppressWarnings("hiding") final boolean isNullable) {
    this.isNullable = isNullable;
    return this;
  }

  public MemberBuilder setterArgumentAnnotation(final Annotation annotation) {
    this.setterArgumentAnnotations.add(annotation);
    return this;
  }

  public MemberBuilder isImutable(@SuppressWarnings("hiding") final boolean isImutable) {
    this.isImutable = isImutable;
    return this;
  }

  public MemberBuilder annotation(@SuppressWarnings("hiding") final Annotation annotations) {
    if (annotations == null) {
      return this;
    }
    this.annotations.add(annotations);
    return this;
  }

  public MemberBuilder setterAnnotation(final Annotation annotations) {
    if (annotations == null) {
      return this;
    }
    this.setterAnnotations.add(annotations);
    return this;
  }

  public MemberBuilder getterAnnotation(@SuppressWarnings("hiding") final Annotation annotations) {
    if (annotations == null) {
      return this;
    }
    this.getterAnnotations.add(annotations);
    return this;
  }

  public static String createSetterName(final String name) {
    Ensure.ensureArgumentNotNull(name);
    return createMethodName("set", name); //$NON-NLS-1$
  }

  public static String createGetterName(final Type type, final String name) {
    if (type.name().equals("boolean")) {
      if (name.startsWith("is")) {
        return createMethodName(null, name);
      }
      return createMethodName("is", name);
    }
    if (name.equalsIgnoreCase("class")) {
      return "getCLASS";
    }
    return createMethodName("get", name);
  }

  private static String createMethodName(final String prefix, final String name) {
    if (StringUtilities.isNullOrTrimmedEmpty(prefix)) {
      return name;
    }
    final StringBuilder builder = new StringBuilder();
    builder.append(prefix);
    builder.append(name.substring(0, 1).toUpperCase());
    builder.append(name.substring(1, name.length()));
    return builder.toString();
  }

  public void addValueOf(@SuppressWarnings("hiding") final IValueOfMethodFactory valueOfMethodFactory) {
    this.valueOfMethodFactory = valueOfMethodFactory;

  }

  public void asObject(@SuppressWarnings("hiding") final IAsObjectMethodFactory asObjectMethodFactory) {
    this.asObjectMethodFactory = asObjectMethodFactory;
  }

}
