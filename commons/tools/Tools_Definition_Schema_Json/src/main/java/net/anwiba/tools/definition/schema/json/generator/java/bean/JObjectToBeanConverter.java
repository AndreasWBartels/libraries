/*
 * #%L anwiba commons tools %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.tools.definition.schema.json.generator.java.bean;

import static net.anwiba.tools.generator.java.bean.JavaConstants.*;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.anwiba.commons.resource.utilities.StringUtilities;
import net.anwiba.tools.definition.schema.json.gramma.element.IJsonTypeVisitor;
import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotation;
import net.anwiba.tools.definition.schema.json.gramma.element.JField;
import net.anwiba.tools.definition.schema.json.gramma.element.JObject;
import net.anwiba.tools.definition.schema.json.gramma.element.JParameter;
import net.anwiba.tools.definition.schema.json.gramma.element.JType;
import net.anwiba.tools.definition.schema.json.gramma.element.JValue;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.Argument;
import net.anwiba.tools.generator.java.bean.configuration.Bean;
import net.anwiba.tools.generator.java.bean.configuration.BeanBuilder;
import net.anwiba.tools.generator.java.bean.configuration.CreatorBuilder;
import net.anwiba.tools.generator.java.bean.configuration.Member;
import net.anwiba.tools.generator.java.bean.configuration.MemberBuilder;
import net.anwiba.tools.generator.java.bean.configuration.PropertiesBuilder;
import net.anwiba.tools.generator.java.bean.configuration.TypeBuilder;
import net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities;

@SuppressWarnings("nls")
public class JObjectToBeanConverter {

  private static final String DELEGATION = "delegation";
  private static final String PROPERTY = "property";
  private static final String ARGUMENTS = "arguments";
  private static final String ARGUMENT = "argument";
  private static final String CREATE = "create";
  private static final String FACTORY = "factory";
  private static final String SOURCE = "source";
  private static final String TYPE = "type";
  private static final String REFLECTION = "reflection";
  private static final String VALUE = "value";
  private static final String _UNKNOWN_MEMBERS = "_unknownMembers";

  private static final String JSSD_NO_PROPERTY = "JssdNoProperty";
  private static final String JSSD_IMMUTABLE = "JssdImmutable";
  private static final String JSSD_ARRAYS_NOT_NULLABLE = "JssdArraysNotNullable";
  private static final String JSSD_NOT_NULLABLE = "JssdNotNullable";
  private static final String JSSD_FACTORY = "JssdFactory";
  private static final String JSSD_NAMED_VALUE_PROVIDER = "JssdNamedValueProvider";
  private static final String JSSD_UNKNOWN_MEMBER = "JssdUnknownMember";
  private static final String JSSD_PRIMITIVES_ENABLED = "JssdPrimitivesEnabled";
  private static final String JSSD_EQUALS = "JssdEquals";
  private static final String JSSD_EXTENDS = "JssdExtends";
  private static final String JSSD_NAME = "JssdName";

  private static final String ORG_CODEHAUS_JACKSON_MAP_ANNOTATE_JACKSON_INJECT = com.fasterxml.jackson.annotation.JacksonInject.class
      .getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY = com.fasterxml.jackson.annotation.JsonProperty.class
      .getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_CREATOR = com.fasterxml.jackson.annotation.JsonCreator.class
      .getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_IGNORE = com.fasterxml.jackson.annotation.JsonIgnore.class
      .getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_SETTER = com.fasterxml.jackson.annotation.JsonAnySetter.class
      .getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_GETTER = com.fasterxml.jackson.annotation.JsonAnyGetter.class
      .getName();

  private final BeanNameConverter beanNameConverter;
  private final AnnotationHandler annotationHandler;
  private final String comment;
  private final boolean isBuilderBeanPatternEnabled;

  public JObjectToBeanConverter(
      final String packageName,
      final String comment,
      final boolean isBuilderBeanPatternEnabled) {
    this.comment = comment;
    this.isBuilderBeanPatternEnabled = isBuilderBeanPatternEnabled;
    this.beanNameConverter = new BeanNameConverter(packageName);
    this.annotationHandler = new AnnotationHandler();
  }

  public Bean convert(final String name, final JObject object) {
    final Map<String, JAnnotation> jssdAnnotations = this.annotationHandler
        .extractJssdAnnotations(object.annotations());
    final BeanBuilder builder = bean(getClassName(jssdAnnotations, JSSD_NAME, VALUE, name));
    builder.extend(getClassName(jssdAnnotations, JSSD_EXTENDS, TYPE, null));
    builder.setMutable(!this.isBuilderBeanPatternEnabled);
    builder.setArrayNullable(!jssdAnnotations.containsKey(JSSD_ARRAYS_NOT_NULLABLE));
    builder.setCollectionNullable(!jssdAnnotations.containsKey(JSSD_ARRAYS_NOT_NULLABLE));
    builder.setEqualsEnabled(jssdAnnotations.containsKey(JSSD_EQUALS));
    builder.setPrimitivesEnabled(jssdAnnotations.containsKey(JSSD_PRIMITIVES_ENABLED));
    builder.comment(this.comment);
    addMembers(object, builder);
    if (jssdAnnotations.containsKey(JSSD_UNKNOWN_MEMBER)) {
      addUnknownMembersMethod(builder, jssdAnnotations);
    }
    if (jssdAnnotations.containsKey(JSSD_FACTORY)) {
      addFactoryMethode(builder, jssdAnnotations);
    }
    addClassAnnotations(object, builder);
    return builder.build();
  }

  private void addClassAnnotations(final JObject object, final BeanBuilder builder) {
    for (final JAnnotation annotation : object.annotations()) {
      if (this.annotationHandler.isJssdAnnotation(annotation)) {
        continue;
      }
      builder.annotation(this.annotationHandler.convert(annotation));
    }
  }

  private void addUnknownMembersMethod(final BeanBuilder builder, final Map<String, JAnnotation> jssdAnnotations) {
    final PropertiesBuilder properties = properties(
        type(this.beanNameConverter.convert(java.lang.Object.class.getName())).build(),
        _UNKNOWN_MEMBERS);
    properties.getterName("get").getterAnnotation(annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_GETTER).build());
    properties.setterName("set").setterAnnotation(annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_SETTER).build());
    properties.isNullable(true);
    properties.setImplementsNamedValueProvider(jssdAnnotations.containsKey(JSSD_NAMED_VALUE_PROVIDER));
    properties.namesGetterAnnotation(annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_IGNORE).build());
    builder.properties(properties.build());
  }

  private void addFactoryMethode(final BeanBuilder builder, final Map<String, JAnnotation> jssdAnnotations) {
    final JAnnotation annotation = jssdAnnotations.get(JSSD_FACTORY);
    final CreatorBuilder createMethodeBuilder = creator(CREATE).annotation(
        annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_CREATOR).build());
    final JParameter typeParameter = annotation.parameter(TYPE);
    if (!annotation.hasParameters() || (typeParameter == null || typeParameter.value().equals(REFLECTION))) {
      final String anotationName = getAnnotationName(annotation.parameter(SOURCE));
      final JParameter argumentParameter = annotation.parameter(ARGUMENT);
      if (argumentParameter == null) {
        builder.creator(createMethodeBuilder.addArgument(argument(JAVA_LANG_STRING, TYPE, anotationName)).build());
      } else {
        builder.creator(createMethodeBuilder.addArgument(
            argument(JAVA_LANG_STRING, argumentParameter.value().toString(), anotationName)).build());
      }
    } else if (typeParameter.value().equals(DELEGATION)) {
      final JParameter factoryParameter = annotation.parameter(FACTORY);
      if (factoryParameter == null) {
        throw new IllegalArgumentException("missing factory parameter in JssdFactory annotation");
      }
      createMethodeBuilder.setFactory(argument(
          factoryParameter.value().toString(),
          ORG_CODEHAUS_JACKSON_MAP_ANNOTATE_JACKSON_INJECT));
      final JParameter argumentsParameter = annotation.parameter(ARGUMENTS);
      if (argumentsParameter != null) {
        final List<Argument> arguments = arguments(
            argumentsParameter.value().toString(),
            ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY);
        for (final Argument argument : arguments) {
          createMethodeBuilder.addArgument(argument);
        }
      }
      builder.creator(createMethodeBuilder.build());
    }
  }

  private String getAnnotationName(final JParameter sourceParameter) {
    if (sourceParameter == null || sourceParameter.value().equals(PROPERTY)) {
      return ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY;
    } else if (sourceParameter.value().equals("inject")) {
      return ORG_CODEHAUS_JACKSON_MAP_ANNOTATE_JACKSON_INJECT;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  private List<Argument> arguments(final String parameters, final String anotationName) {
    final StringTokenizer tokenizer = new StringTokenizer(parameters, ",");
    final ArrayList<Argument> arguments = new ArrayList<>();
    while (tokenizer.hasMoreTokens()) {
      arguments.add(argument(tokenizer.nextToken(), anotationName));
    }
    return arguments;
  }

  private Argument argument(final String parameter, final String anotationName) {
    final String name = StringUtilities.getStringBeforLastChar(parameter, ':').trim();
    final String type = StringUtilities.getStringAfterLastChar(parameter, ':').trim();
    return argument(type, name, anotationName);
  }

  private Argument argument(final String type, final String name, final String anotationName) {
    return new Argument(type(type).build(), SourceFactoryUtilities.createFieldName(name), Arrays.asList(annotation(
        anotationName).parameter(VALUE, name).build()));
  }

  public String getClassName(
      final Map<String, JAnnotation> jssdAnnotations,
      final String annotationName,
      final String parameterName,
      final String defaultValue) {
    if (jssdAnnotations.containsKey(annotationName)) {
      final JParameter parameter = jssdAnnotations.get(annotationName).parameter(parameterName);
      if (parameter != null) {
        final String name = parameter.value();
        if (name != null) {
          return this.beanNameConverter.convert(name);
        }
      }
    }
    return defaultValue == null ? null : this.beanNameConverter.convert(defaultValue);
  }

  public void addMembers(final JObject object, final BeanBuilder builder) {
    for (final String fieldname : object.names()) {
      final JField field = object.field(fieldname);
      final Map<String, JAnnotation> jssdAnnotations = this.annotationHandler.extractJssdAnnotations(field
          .annotations());
      if (jssdAnnotations.containsKey("JssdProperties")) {
        final PropertiesBuilder properties = properties(type(this.beanNameConverter.convert(field.type().name()))
            .build(), field.name());
        final Annotation jsonPropertyAnnotation = annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY).parameter(
            VALUE,
            field.name()).build();
        properties.annotation(jsonPropertyAnnotation);
        properties.getterAnnotation(jsonPropertyAnnotation);
        properties.setterAnnotation(jsonPropertyAnnotation);
        properties.isNullable(true);
        properties.setSingleValueSetterEnabled(true);
        properties.setNamedValueGetterEnabled(true);
        properties.setImplementsNamedValueProvider(false);
        builder.properties(properties.build());
        continue;
      }
      builder.member(convert(jssdAnnotations, field));
    }
  }

  public Member convert(final Map<String, JAnnotation> jssdAnnotations, final JField field) {
    final JType type = field.type();
    final TypeBuilder typeBuilder = type(this.beanNameConverter.convert(type.name()));
    typeBuilder.dimension(type.dimension());
    for (final String generic : type.generics()) {
      typeBuilder.generic(this.beanNameConverter.convert(generic));
    }
    final MemberBuilder builder = member(typeBuilder.build(), field.name());
    if (!jssdAnnotations.containsKey(JSSD_NO_PROPERTY)) {
      final Annotation jsonPropertyAnnotation = annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY).parameter(
          VALUE,
          field.name()).build();
      builder.annotation(jsonPropertyAnnotation);
      builder.getterAnnotation(jsonPropertyAnnotation);
      builder.setterAnnotation(jsonPropertyAnnotation);
    }
    builder.isNullable(!(jssdAnnotations.containsKey(JSSD_NOT_NULLABLE)));
    builder.isSetterEnabled(!(jssdAnnotations.containsKey(JSSD_IMMUTABLE)));
    adjustValue(builder, type, field.value());
    for (final JAnnotation annotation : field.annotations()) {
      if (this.annotationHandler.isJssdAnnotation(annotation)) {
        continue;
      }
      if (this.annotationHandler.isGetterAnnotation(annotation)) {
        builder.getterAnnotation(this.annotationHandler.convert(annotation));
        continue;
      }
      if (this.annotationHandler.isSetterAnnotation(annotation)) {
        builder.setterAnnotation(this.annotationHandler.convert(annotation));
        continue;
      }
      builder.annotation(this.annotationHandler.convert(annotation));
    }
    return builder.build();
  }

  private void adjustValue(final MemberBuilder builder, final JType type, final JValue value) {
    final IJsonTypeVisitor<Void, RuntimeException> visitor = new IJsonTypeVisitor<Void, RuntimeException>() {

      @Override
      public Void visitString() throws RuntimeException {
        if (value == null) {
          return null;
        }
        final String string = (String) value.value();
        builder.value(string);
        return null;
      }

      @Override
      public Void visitObject() throws RuntimeException {
        throw new UnsupportedOperationException();
      }

      @Override
      public Void visitNumber() throws RuntimeException {
        final Number number = (Number) value.value();
        if (shortTypes.contains(type.name())) {
          builder.value(number.shortValue());
          return null;
        }
        if (integerTypes.contains(type.name())) {
          builder.value(number.intValue());
          return null;
        }
        if (longTypes.contains(type.name())) {
          builder.value(number.longValue());
          return null;
        }
        if (floatTypes.contains(type.name())) {
          builder.value(number.floatValue());
          return null;
        }
        if (doubleTypes.contains(type.name())) {
          builder.value(number.doubleValue());
          return null;
        }
        if (byteTypes.contains(type.name())) {
          builder.value(number.byteValue());
          return null;
        }
        throw new UnsupportedOperationException("not yet implemented");
      }

      @Override
      public Void visitNull() throws RuntimeException {
        return null;
      }

      @Override
      public Void visitBoolean() throws RuntimeException {
        builder.value(((Boolean) value.value()).booleanValue());
        return null;
      }

      @Override
      public Void visitArray() throws RuntimeException {
        if (shortTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final short[] values = new short[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? (short) 0 : list.get(i).shortValue();
          }
          builder.value(values);
          return null;
        }
        if (integerTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final int[] values = new int[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? 0 : list.get(i).intValue();
          }
          builder.value(values);
          return null;
        }
        if (longTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final long[] values = new long[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? 0 : list.get(i).longValue();
          }
          builder.value(values);
          return null;
        }
        if (floatTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final float[] values = new float[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? 0 : list.get(i).floatValue();
          }
          builder.value(values);
          return null;
        }
        if (doubleTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final double[] values = new double[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? 0 : list.get(i).doubleValue();
          }
          builder.value(values);
          return null;
        }
        if (byteTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final byte[] values = new byte[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? 0 : list.get(i).byteValue();
          }
          builder.value(values);
          return null;
        }
        if (booleanTypes.contains(type.name())) {
          final List<Boolean> list = value.value();
          final boolean[] values = new boolean[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? false : list.get(i).booleanValue();
          }
          builder.value(values);
          return null;
        }
        if (characterTypes.contains(type.name())) {
          final List<Character> list = value.value();
          final char[] values = new char[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? 0 : list.get(i).charValue();
          }
          builder.value(values);
          return null;
        }
        if (stringTypes.contains(type.name())) {
          final List<String> list = value.value();
          final String[] values = new String[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null ? null : list.get(i);
          }
          builder.value(values);
          return null;
        }
        throw new UnsupportedOperationException("not yet implemented");
      }

      @Override
      public Void visitCharacter() throws RuntimeException {
        if (value == null) {
          return null;
        }
        final Character string = (Character) value.value();
        builder.value(string.charValue());
        return null;
      }
    };
    value.type().accept(visitor);
  }
}
