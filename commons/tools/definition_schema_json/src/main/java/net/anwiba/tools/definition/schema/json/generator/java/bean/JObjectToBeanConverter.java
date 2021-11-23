/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.tools.definition.schema.json.generator.java.bean;

import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_LANG_STRING;
import static net.anwiba.tools.generator.java.bean.JavaConstants.booleanTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.byteTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.characterTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.doubleTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.floatTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.integerTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.longTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.shortTypes;
import static net.anwiba.tools.generator.java.bean.JavaConstants.stringTypes;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.annotation;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.bean;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.creator;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.member;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.properties;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.reference.utilities.StringUtilities;
import net.anwiba.tools.definition.schema.json.gramma.element.IJsonTypeVisitor;
import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotation;
import net.anwiba.tools.definition.schema.json.gramma.element.JField;
import net.anwiba.tools.definition.schema.json.gramma.element.JObject;
import net.anwiba.tools.definition.schema.json.gramma.element.JParameter;
import net.anwiba.tools.definition.schema.json.gramma.element.JType;
import net.anwiba.tools.definition.schema.json.gramma.element.JValue;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.AnnotationBuilder;
import net.anwiba.tools.generator.java.bean.configuration.Argument;
import net.anwiba.tools.generator.java.bean.configuration.Bean;
import net.anwiba.tools.generator.java.bean.configuration.BeanBuilder;
import net.anwiba.tools.generator.java.bean.configuration.CreatorBuilder;
import net.anwiba.tools.generator.java.bean.configuration.Member;
import net.anwiba.tools.generator.java.bean.configuration.MemberBuilder;
import net.anwiba.tools.generator.java.bean.configuration.PropertiesBuilder;
import net.anwiba.tools.generator.java.bean.configuration.TypeBuilder;
import net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities;
import net.anwiba.tools.generator.java.bean.value.ValueType;

@SuppressWarnings("nls")
public class JObjectToBeanConverter {

  private static final String NAME = "name";
  private static final String SHAPE = "shape";
  private static final String INPUT = "input";
  private static final String OUTPUT = "output";
  private static final String INOUT = "inout";
  private static final String DELEGATION = "delegation";
  private static final String PATTERN = "pattern";
  private static final String PROPERTY = "property";
  private static final String ARGUMENTS = "arguments";
  private static final String ARGUMENT = "argument";
  private static final String CREATE = "create";
  private static final String FACTORY = "factory";
  private static final String SOURCE = "source";
  private static final String TYPE = "type";
  private static final String TYPES = "types";
  private static final String REFLECTION = "reflection";
  private static final String VALUE = "value";
  private static final String USING = "using";
  private static final String _UNKNOWN_MEMBERS = "_unknownMembers";
  private static final String TYPE_INFO = "typeinfo";

  private static final String JSSD_PROPERTIES = "JssdProperties";
  private static final String JSSD_NO_PROPERTY = "JssdNoProperty";
  private static final String JSSD_IMMUTABLE = "JssdImmutable";
  private static final String JSSD_ARRAYS_NOT_NULLABLE = "JssdArraysNotNullable";
  private static final String JSSD_NOT_NULLABLE = "JssdNotNullable";
  private static final String JSSD_FACTORY = "JssdFactory";
  private static final String JSSD_BUILDER = "JssdBuilder";
  private static final String JSSD_NAMED_VALUE_PROVIDER = "JssdNamedValueProvider";
  private static final String JSSD_UNKNOWN_MEMBER = "JssdUnknownMember";
  private static final String JSSD_IGNORE_UNKNOWN_MEMBER = "JssdIgnoreUnknownMember";
  private static final String JSSD_PRIMITIVES_ENABLED = "JssdPrimitivesEnabled";
  private static final String JSSD_EQUALS = "JssdEquals";
  private static final String JSSD_EXTENDS = "JssdExtends";
  private static final String JSSD_NAME = "JssdName";
  private static final String JSSD_PATTERN = "JssdPattern";
  private static final String JSSD_VALUE = "JssdValue";
  private static final String JSSD_INLUDE_ALLWAYS = "JssdIncludeAllways";
  private static final String JSSD_INLUDE_NON_NULL = "JssdIncludeNonNull";

  private static final String JSSD_SERIALIZER = "JssdSerializer";

  private static final String ORG_CODEHAUS_JACKSON_MAP_ANNOTATE_JACKSON_INJECT =
      com.fasterxml.jackson.annotation.JacksonInject.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY =
      com.fasterxml.jackson.annotation.JsonProperty.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_CREATOR =
      com.fasterxml.jackson.annotation.JsonCreator.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_TYPE_INFO =
      com.fasterxml.jackson.annotation.JsonTypeInfo.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_SUB_TYPE =
      com.fasterxml.jackson.annotation.JsonSubTypes.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_SUB_TYPE_TYPE =
      com.fasterxml.jackson.annotation.JsonSubTypes.Type.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_IGNORE =
      com.fasterxml.jackson.annotation.JsonIgnore.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_IGNORE_PROPERTIES =
      com.fasterxml.jackson.annotation.JsonIgnoreProperties.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_SETTER =
      com.fasterxml.jackson.annotation.JsonAnySetter.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_GETTER =
      com.fasterxml.jackson.annotation.JsonAnyGetter.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_FORMAT =
      com.fasterxml.jackson.annotation.JsonFormat.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_VALUE =
      com.fasterxml.jackson.annotation.JsonValue.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_INLUDE =
      com.fasterxml.jackson.annotation.JsonInclude.class.getName();

  private static final String ORG_CODEHAUS_JACKSON_DATABIND_ANNOTATE_JSON_SERIALIZER =
      com.fasterxml.jackson.databind.annotation.JsonSerialize.class.getName();
  private static final String ORG_CODEHAUS_JACKSON_DATABIND_ANNOTATE_JSON_DESERIALIZER =
      com.fasterxml.jackson.databind.annotation.JsonDeserialize.class.getName();

  private final BeanNameConverter beanNameConverter;
  private final AnnotationHandler annotationHandler;
  private final boolean isBuilderBeanPatternEnabled;

  public JObjectToBeanConverter(
      final String packageName,
      final boolean isBuilderBeanPatternEnabled) {
    this.isBuilderBeanPatternEnabled = isBuilderBeanPatternEnabled;
    this.beanNameConverter = new BeanNameConverter(packageName);
    this.annotationHandler = new AnnotationHandler();
  }

  public Bean convert(final String name, final JObject object) throws ConversionException {
    try {
      final Map<String, JAnnotation> jssdAnnotations =
          this.annotationHandler.extractJssdAnnotations(object.annotations());
      final BeanBuilder builder = bean(getClassName(jssdAnnotations, JSSD_NAME, VALUE, name));
      builder.extend(getClassName(jssdAnnotations, JSSD_EXTENDS, TYPE, null));
      builder.setMutable(!(this.isBuilderBeanPatternEnabled || jssdAnnotations.containsKey(JSSD_VALUE)));
      builder.setEnableBuilder(jssdAnnotations.containsKey(JSSD_BUILDER) || this.isBuilderBeanPatternEnabled);
      builder.setArrayNullable(!jssdAnnotations.containsKey(JSSD_ARRAYS_NOT_NULLABLE));
      builder.setCollectionNullable(!jssdAnnotations.containsKey(JSSD_ARRAYS_NOT_NULLABLE));
      builder.setEqualsEnabled(jssdAnnotations.containsKey(JSSD_EQUALS));
      builder.setPrimitivesEnabled(jssdAnnotations.containsKey(JSSD_PRIMITIVES_ENABLED));
      builder.comment(object.comment());
      if (jssdAnnotations.containsKey(JSSD_VALUE) && object.numberOfValues() != 1) {
        throw new ConversionException("Jssd value allows only on member");
      }
      addMembers(object, builder, getFactoryMembers(jssdAnnotations.get(JSSD_FACTORY)));
      if (jssdAnnotations.containsKey(JSSD_FACTORY)) {
        addFactoryMethode(builder, jssdAnnotations);
      }
      if (jssdAnnotations.containsKey(JSSD_UNKNOWN_MEMBER)) {
        addUnknownMembersMethod(builder, jssdAnnotations);
      } else if (jssdAnnotations.containsKey(JSSD_IGNORE_UNKNOWN_MEMBER)) {
        addIgnoreUnknownMembersMethod(builder);
      }
      addClassAnnotations(object, builder);
      return builder.build();
    } catch (final ConversionException exception) {
      throw new ConversionException("Couldn't convert object '" + name + "', " + exception.getMessage(), exception);
    }
  }

  private void addIgnoreUnknownMembersMethod(final BeanBuilder builder) {
    builder
        .annotation(
            annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_IGNORE_PROPERTIES).parameter("ignoreUnknown", true).build());
  }

  private Set<String> getFactoryMembers(final JAnnotation annotation) {
    if (annotation == null) {
      return Collections.emptySet();
    }
    final JParameter typeParameter = annotation.parameter(TYPE);
    if (typeParameter != null && typeParameter.value().equals(TYPE_INFO)) {
      return Collections.emptySet();
    }

    if (annotation.hasParameters()) {
      if (annotation.parameter(ARGUMENT) != null) {
        return new HashSet<>(Arrays.asList(getTypeName(annotation.parameter(ARGUMENT).value().toString())));
      }
      if (annotation.parameter(ARGUMENTS) != null) {
        return new HashSet<>(getTypeNames(annotation.parameter(ARGUMENTS).value().toString()));
      }
    }
    return new HashSet<>(Arrays.asList(TYPE));
  }

  private List<String> getTypeNames(final String types) {
    final StringTokenizer tokenizer = new StringTokenizer(types, ",");
    final ArrayList<String> typeNames = new ArrayList<>();
    while (tokenizer.hasMoreTokens()) {
      typeNames.add(getTypeName(tokenizer.nextToken()));
    }
    return typeNames;
  }

  private String getTypeName(final String type) {
    if (type.indexOf(':') == -1) {
      return type;
    }
    return StringUtilities.getStringAfterLastChar(type, ':').trim();
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
    final PropertiesBuilder properties =
        properties(type(this.beanNameConverter.convert(java.lang.Object.class.getName())).build(), _UNKNOWN_MEMBERS);
    properties.getterName("get").getterAnnotation(annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_GETTER).build());
    properties.setterName("set").setterAnnotation(annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_ANY_SETTER).build());
    properties.isImutable(false);
    properties.isNullable(true);
    properties.setImplementsNamedValueProvider(jssdAnnotations.containsKey(JSSD_NAMED_VALUE_PROVIDER));
    properties.namesGetterAnnotation(annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_IGNORE).build());
    builder.properties(properties.build());
  }

  private void addFactoryMethode(final BeanBuilder builder, final Map<String, JAnnotation> jssdAnnotations)
      throws ConversionException {
    final JAnnotation annotation = jssdAnnotations.get(JSSD_FACTORY);
    final JParameter typeParameter = annotation.parameter(TYPE);
    if (typeParameter != null && typeParameter.value().equals(TYPE_INFO)) {
      final JParameter propertyParameter = annotation.parameter(PROPERTY);
      builder
          .annotation(
              annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_TYPE_INFO)
                  .parameter("use", JsonTypeInfo.Id.NAME)
                  .parameter("include", JsonTypeInfo.As.EXISTING_PROPERTY)
                  .parameter(
                      "property",
                      propertyParameter == null
                          ? TYPE
                          : propertyParameter.value().toString())
                  .build());
      addSubTypes(builder, annotation);
      return;
    }

    final CreatorBuilder createMethodeBuilder = creator(CREATE)
        .annotation(
            annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_CREATOR)
                .parameter("mode", JsonCreator.Mode.PROPERTIES)
                .build());
    if (!annotation.hasParameters() || (typeParameter == null || typeParameter.value().equals(REFLECTION))) {
      final String anotationName = getAnnotationName(annotation.parameter(SOURCE));
      final JParameter argumentParameter = annotation.parameter(ARGUMENT);
      if (argumentParameter == null) {
        builder.creator(createMethodeBuilder.addArgument(argument(anotationName, TYPE, JAVA_LANG_STRING)).build());
      } else {
        builder
            .creator(
                createMethodeBuilder
                    .addArgument(argument(anotationName, argumentParameter.value().toString(), JAVA_LANG_STRING))
                    .build());
      }
    } else if (typeParameter.value().equals(DELEGATION)) {
      final JParameter factoryParameter = annotation.parameter(FACTORY);
      if (factoryParameter == null) {
        throw new ConversionException("missing factory parameter in JssdFactory annotation");
      }
      createMethodeBuilder
          .setFactory(argument(ORG_CODEHAUS_JACKSON_MAP_ANNOTATE_JACKSON_INJECT, factoryParameter.value().toString()));
      final JParameter argumentsParameter = annotation.parameter(ARGUMENTS);
      if (argumentsParameter != null) {
        final List<Argument> arguments =
            arguments(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY, argumentsParameter.value().toString());
        for (final Argument argument : arguments) {
          createMethodeBuilder.addArgument(argument);
        }
      }
      builder.creator(createMethodeBuilder.build());
    }
  }

  protected void addSubTypes(final BeanBuilder builder, final JAnnotation annotation) {
    final JParameter typesParameter = annotation.parameter(TYPES);
    if (typesParameter != null) {
      final AnnotationBuilder annotationBuilder = annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_SUB_TYPE);

      final List<Annotation> annotations = new ArrayList<>();

      final StringTokenizer typeTokenizer = new StringTokenizer(typesParameter.value().toString(), ",");
      while (typeTokenizer.hasMoreTokens()) {
        final String token = typeTokenizer.nextToken().trim();
        final int index = token.indexOf(':');
        if (index > -1) {
          final String value = token.substring(0, index).trim();
          final String name = token.substring(index + 1, token.length()).trim();
          annotations
              .add(
                  annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_SUB_TYPE_TYPE)
                      .parameter(VALUE, convertToClassName(value), ValueType.CLASS)
                      .parameter(NAME, name)
                      .build());
          continue;
        }
        annotations
            .add(
                annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_SUB_TYPE_TYPE)
                    .parameter(VALUE, convertToClassName(token), ValueType.CLASS)
                    .build());
      }
      annotationBuilder.parameter(VALUE, annotations);
      builder.annotation(annotationBuilder.build());
    }
  }

  private String convertToClassName(final String value) {
    return value.contains(".")
        ? value
        : net.anwiba.commons.utilities.string.StringUtilities.setFirstTrimedCharacterToUpperCase(value);
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

  private List<Argument> arguments(final String anotationName, final String parameters) {
    final StringTokenizer tokenizer = new StringTokenizer(parameters, ",");
    final ArrayList<Argument> arguments = new ArrayList<>();
    while (tokenizer.hasMoreTokens()) {
      arguments.add(argument(anotationName, tokenizer.nextToken()));
    }
    return arguments;
  }

  private Argument argument(final String anotationName, final String parameter) {
    final String name = StringUtilities.getStringBeforLastChar(parameter, ':').trim();
    final String type = StringUtilities.getStringAfterLastChar(parameter, ':').trim();
    return argument(anotationName, name, type);
  }

  private Argument argument(final String anotationName, final String value, final String type) {
    return new Argument(
        SourceFactoryUtilities.createFieldName(value),
        Arrays.asList(annotation(anotationName).parameter(VALUE, value).build()),
        type(type).build());
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
    return defaultValue == null
        ? null
        : this.beanNameConverter.convert(defaultValue);
  }

  public void addMembers(final JObject object, final BeanBuilder builder, final Set<String> factoryMembers)
      throws ConversionException {
    for (final String fieldname : object.names()) {
      final JField field = object.field(fieldname);
      final Map<String, JAnnotation> jssdAnnotations =
          this.annotationHandler.extractJssdAnnotations(field.annotations());
      if (jssdAnnotations.containsKey(JSSD_PROPERTIES)) {
        final PropertiesBuilder properties =
            properties(type(this.beanNameConverter.convert(field.type().name())).build(), field.name());
        properties.fieldComment(field.fieldComment());
        properties.setterComment(field.setterComment());
        properties.getterComment(field.getterComment());
        properties
            .getterAnnotation(
                annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY).parameter(VALUE, field.name()).build());
        properties
            .setterAnnotation(
                annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY).parameter(VALUE, field.name()).build());
        properties.isNullable(true);
        properties.setSingleValueSetterEnabled(true);
        properties.setNamedValueGetterEnabled(true);
        properties.setImplementsNamedValueProvider(false);
        builder.properties(properties.build());
        continue;
      }
      builder.member(convert(object, field, factoryMembers));
    }
  }

  public Member convert(final JObject object, final JField field, final Set<String> factoryMembers)
      throws ConversionException {
    final JType type = field.type();
    final TypeBuilder typeBuilder = type(this.beanNameConverter.convert(type.name()));
    typeBuilder.dimension(type.dimension());
    for (final String generic : type.generics()) {
      typeBuilder.generic(this.beanNameConverter.convert(generic));
    }
    final MemberBuilder builder = member(typeBuilder.build(), field.name());
    adjustValue(builder, type, field.value());
    builder.fieldComment(field.fieldComment());
    builder.setterComment(field.setterComment());
    builder.getterComment(field.getterComment());
    if (object.hasAnnotation(JSSD_VALUE)) {
      final JAnnotation annotation = object.annotation(JSSD_VALUE);
      builder.getterAnnotation(annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_VALUE).build());
      if (annotation.hasParameter(TYPE)) {
        final String jsonType = this.beanNameConverter.convert(annotation.parameter(TYPE).value());
        builder.addValueOf((model, instance, member) -> {
          final JMethod method = instance.method(JMod.PUBLIC | JMod.STATIC, instance, "valueOf"); //$NON-NLS-1$
          final JVar value = method.param(model.ref(jsonType), "object"); //$NON-NLS-1$
          final JTryBlock _try = method.body()._try();
          final JExpression dotclass = model.ref(jsonType).dotclass();
          final JInvocation result = JExpr
              ._new(model.ref(com.fasterxml.jackson.databind.ObjectMapper.class.getName()))
              .invoke("findAndRegisterModules")
              .invoke("writerFor")
              .arg(dotclass)
              .invoke("writeValueAsString")
              .arg(value);
          _try.body()._return(JExpr._new(instance).arg(result));
          final JCatchBlock _catch = _try._catch(model.ref(Exception.class));
          final JVar exception = _catch.param("exception");
          _catch.body()._throw(JExpr._new(model.ref(IllegalArgumentException.class)).arg(exception));
        });
        builder.asObject((model, instance, member) -> {
          final JClass ref = model.ref(jsonType);
          final JMethod method = instance.method(JMod.PUBLIC, ref, "as" + ref.name());
          final JBlock body = method.body();
          final JTryBlock _try = body._try();
          final JExpression dotclass = ref.dotclass();
          _try
              .body()
              ._return(
                  JExpr
                      ._new(model.ref(com.fasterxml.jackson.databind.ObjectMapper.class.getName()))
                      .invoke("findAndRegisterModules")
                      .invoke("readerFor")
                      .arg(dotclass)
                      .invoke("readValue")
                      .arg(member));
          final JCatchBlock _catch = _try._catch(model.ref(Exception.class));
          final JVar exception = _catch.param("exception");
          _catch.body()._throw(JExpr._new(model.ref(IllegalStateException.class)).arg(exception));
        });
      }
      return builder.build();
    }
    if (!field.hasAnnotation(JSSD_NO_PROPERTY)) {
      final Annotation jsonPropertyAnnotation =
          annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_PROPERTY).parameter(VALUE, field.name()).build();
      builder.getterAnnotation(jsonPropertyAnnotation);
      builder.setterAnnotation(jsonPropertyAnnotation);
    } else {
      final Annotation jsonPropertyAnnotation = annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_IGNORE).build();
      builder.getterAnnotation(jsonPropertyAnnotation);
      builder.setterAnnotation(jsonPropertyAnnotation);
    }
    if (field.hasAnnotation(JSSD_INLUDE_NON_NULL)) {
      final Annotation jsonPropertyAnnotation = annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_INLUDE)
          .parameter("value", JsonInclude.Include.NON_NULL)
          .build();
      builder.getterAnnotation(jsonPropertyAnnotation);
    } else if (field.hasAnnotation(JSSD_INLUDE_ALLWAYS)) {
      final Annotation jsonPropertyAnnotation =
          annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_INLUDE).parameter("value", JsonInclude.Include.ALWAYS).build();
      builder.getterAnnotation(jsonPropertyAnnotation);
    }
    builder.isNullable(!field.hasAnnotation(JSSD_NOT_NULLABLE));
    builder.isImutable((field.hasAnnotation(JSSD_IMMUTABLE)));
    builder.isSetterEnabled(!factoryMembers.contains(field.name()));
    for (final JAnnotation annotation : field.annotations()) {
      if (Objects.equals(annotation.name(), JSSD_SERIALIZER)) {
        if (annotation.hasParameter(INPUT)) {
          final Annotation jsonAnnotation = annotation(ORG_CODEHAUS_JACKSON_DATABIND_ANNOTATE_JSON_DESERIALIZER)
              .parameter(USING, annotation.parameter(INPUT).value().toString())
              .build();
          builder.setterAnnotation(jsonAnnotation);
        }
        if (annotation.hasParameter(OUTPUT)) {
          final Annotation jsonAnnotation = annotation(ORG_CODEHAUS_JACKSON_DATABIND_ANNOTATE_JSON_SERIALIZER)
              .parameter(USING, annotation.parameter(OUTPUT).value().toString())
              .build();
          builder.setterAnnotation(jsonAnnotation);
        }
      }
      if (Objects.equals(annotation.name(), JSSD_PATTERN)) {
        final AnnotationBuilder jsonAnnotationBuilder = annotation(ORG_CODEHAUS_JACKSON_ANNOTATE_JSON_FORMAT);
        final Annotation jsonAnnotation = jsonAnnotationBuilder.build();
        if (!(annotation.hasParameter(INPUT) || annotation.hasParameter(OUTPUT) || annotation.hasParameter(INOUT))) {
          throw new IllegalArgumentException("missing pattern parameter in JssdPattern annotation");
        }
        if (annotation.hasParameter(OUTPUT)) {
          jsonAnnotationBuilder.parameter(SHAPE, JsonFormat.Shape.STRING);
          jsonAnnotationBuilder.parameter(PATTERN, annotation.parameter(OUTPUT).value().toString());
          builder.getterAnnotation(jsonAnnotation);
        }
        if (annotation.hasParameter(INPUT)) {
          jsonAnnotationBuilder.parameter(SHAPE, JsonFormat.Shape.STRING);
          jsonAnnotationBuilder.parameter(PATTERN, annotation.parameter(INPUT).value().toString());
          builder.setterAnnotation(jsonAnnotation);
        }
        if (annotation.hasParameter(INOUT)) {
          jsonAnnotationBuilder.parameter(SHAPE, JsonFormat.Shape.STRING);
          jsonAnnotationBuilder.parameter(PATTERN, annotation.parameter(INOUT).value().toString());
          builder.getterAnnotation(jsonAnnotation);
          builder.setterAnnotation(jsonAnnotation);
        }
      }
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

  private void adjustValue(final MemberBuilder builder, final JType type, final JValue value)
      throws ConversionException {
    final IJsonTypeVisitor<Void, ConversionException> visitor = new IJsonTypeVisitor<>() {

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
      public Void visitArray() throws ConversionException {
        if (shortTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final short[] values = new short[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? (short) 0
                : list.get(i).shortValue();
          }
          builder.value(values);
          return null;
        }
        if (integerTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final int[] values = new int[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? 0
                : list.get(i).intValue();
          }
          builder.value(values);
          return null;
        }
        if (longTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final long[] values = new long[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? 0
                : list.get(i).longValue();
          }
          builder.value(values);
          return null;
        }
        if (floatTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final float[] values = new float[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? 0
                : list.get(i).floatValue();
          }
          builder.value(values);
          return null;
        }
        if (doubleTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final double[] values = new double[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? 0
                : list.get(i).doubleValue();
          }
          builder.value(values);
          return null;
        }
        if (byteTypes.contains(type.name())) {
          final List<Number> list = value.value();
          final byte[] values = new byte[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? 0
                : list.get(i).byteValue();
          }
          builder.value(values);
          return null;
        }
        if (booleanTypes.contains(type.name())) {
          final List<Boolean> list = value.value();
          final boolean[] values = new boolean[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? false
                : list.get(i).booleanValue();
          }
          builder.value(values);
          return null;
        }
        if (characterTypes.contains(type.name())) {
          final List<Character> list = value.value();
          final char[] values = new char[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? 0
                : list.get(i).charValue();
          }
          builder.value(values);
          return null;
        }
        if (stringTypes.contains(type.name())) {
          final List<String> list = value.value();
          final String[] values = new String[list.size()];
          for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i) == null
                ? null
                : list.get(i);
          }
          builder.value(values);
          return null;
        }
        throw new ConversionException("type '" + type.name() + "', isn't supported");
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
