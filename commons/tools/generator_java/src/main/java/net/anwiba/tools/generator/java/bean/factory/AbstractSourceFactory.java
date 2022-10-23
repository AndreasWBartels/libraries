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
package net.anwiba.tools.generator.java.bean.factory;

import static net.anwiba.commons.ensure.Conditions.notNull;
import static net.anwiba.commons.ensure.Ensure.ensureThatArgument;
import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_LANG_CLASS;
import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_LANG_STRING;
import static net.anwiba.tools.generator.java.bean.JavaConstants.primitiveClasses;
import static net.anwiba.tools.generator.java.bean.JavaConstants.primitives;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.Parameter;
import net.anwiba.tools.generator.java.bean.configuration.Type;
import net.anwiba.tools.generator.java.bean.value.IValueTypeVisitor;

import java.util.List;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;

public class AbstractSourceFactory {

  private final JCodeModel codeModel;
  private final IFactory<String, Class<? extends java.lang.annotation.Annotation>,
      CreationException> annotationClassfactory;

  public AbstractSourceFactory(final JCodeModel codeModel,
      final IFactory<String, Class<? extends java.lang.annotation.Annotation>,
          CreationException> annotationClassfactory) {
    this.annotationClassfactory = annotationClassfactory;
    ensureThatArgument(codeModel, notNull());
    this.codeModel = codeModel;
  }

  protected JCodeModel getCodeModel() {
    return this.codeModel;
  }

  protected void annotate(final JAnnotatable annotatable, final Iterable<Annotation> annotationConfigurations)
      throws CreationException {
    ensureThatArgument(annotatable, notNull());
    ensureThatArgument(annotationConfigurations, notNull());
    for (final Annotation annotationConfiguration : annotationConfigurations) {
      annotate(annotatable, annotationConfiguration);
    }
  }

  protected void addTo(final JDocComment javadoc, final String comment) {
    if (comment == null || comment.isBlank()) {
      return;
    }
    javadoc.add(comment);
  }

  @SuppressWarnings("unchecked")
  Class<? extends java.lang.annotation.Annotation> getAnnotationClass(final String name) throws CreationException {
    return this.annotationClassfactory.create(name);
  }

  protected void annotate(final JAnnotatable annotatable, final Annotation annotation) throws CreationException {
    ensureThatArgument(annotatable, notNull());
    ensureThatArgument(annotation, notNull());
    final JAnnotationUse annotate = annotatable.annotate(_classByNames(annotation.name()));
    parameter(annotate, annotation);
  }

  private void parameter(final JAnnotationUse annotate, final Annotation annotation) throws CreationException {
    final Iterable<Parameter> parameters = annotation.parameters();
    for (final Parameter parameter : parameters) {
      for (final Object value : parameter.values()) {
        parameter.type().accept(new IValueTypeVisitor() {

          @Override
          public void annotations() throws CreationException {
            @SuppressWarnings("unchecked")
            final List<Annotation> chAnnotations = (List<Annotation>) value;
            final JAnnotationArrayMember paramArray = annotate.paramArray(parameter.name());
            for (final Annotation parameter2 : chAnnotations) {
              parameter(paramArray.annotate(getAnnotationClass(parameter2.name())), parameter2);
            }
          }

          @Override
          public void annotation() throws CreationException {
            final Annotation chAnnotation = (Annotation) value;
            parameter(
                annotate.annotationParam(parameter.name(), getAnnotationClass(chAnnotation.name())),
                chAnnotation);
          }

          @SuppressWarnings("rawtypes")
          @Override
          public void enumartation() {
            annotate.param(parameter.name(), (Enum) value);
          }

          @Override
          public void string() {
            annotate.param(parameter.name(), String.class.cast(value));
          }

          @Override
          public void integer() {
            annotate.param(parameter.name(), Integer.class.cast(value).intValue());
          }

          @Override
          public void clazz() {
            if (value instanceof String) {
              annotate.param(parameter.name(), _class((String) value));
              return;
            }
            annotate.param(parameter.name(), Class.class.cast(value));
          }

          @Override
          public void logical() {
            annotate.param(parameter.name(), Boolean.class.cast(value));
          }
        });
      }
    }
  }

  protected JDefinedClass _class(final String name, final ClassType clazz) throws JClassAlreadyExistsException {
    ensureThatArgument(name, notNull());
    ensureThatArgument(clazz, notNull());
    return this.codeModel._class(name, ClassType.CLASS);
  }

  protected JType _class(final Type configuration, final boolean isPrimitivesEnabled) {
    if (!isPrimitivesEnabled && primitives.contains(configuration.name()) && configuration.dimension() == 0) {
      JType _class = _type(primitiveClasses.get(configuration.name()));
      for (int i = 0; i < configuration.dimension(); i++) {
        _class = _class.array();
      }
      return _class;
    }
    JType _class = _type(configuration.name(), configuration.generics());
    for (int i = 0; i < configuration.dimension(); i++) {
      _class = _class.array();
    }
    return _class;
  }

  protected JClass _classByNames(final String type, final String... generics) {
    ensureThatArgument(type, notNull());
    if (generics.length == 0) {
      return this.codeModel.ref(type);
    }
    final JClass[] classes = ArrayUtilities.convert(new IConverter<String, JClass, RuntimeException>() {

      @Override
      public JClass convert(final String input) throws RuntimeException {
        return AbstractSourceFactory.this.codeModel.ref(input);
      }
    }, generics, JClass.class);
    return this.codeModel.ref(type).narrow(classes);
  }

  protected JClass _class(final String typeName, final JClass... generics) {
    return this.codeModel.ref(typeName).narrow(generics);
  }

  protected JType _type(final String type, final String... generics) {
    ensureThatArgument(type, notNull());
    if (generics.length == 0) {
      try {
        return this.codeModel.parseType(type);
      } catch (final ClassNotFoundException exception) {
        return this.codeModel.ref(type);
      }
    }
    final JClass[] classes = ArrayUtilities.convert(new IConverter<String, JClass, RuntimeException>() {

      @Override
      public JClass convert(final String input) throws RuntimeException {
        return AbstractSourceFactory.this.codeModel.ref(input);
      }
    }, generics, JClass.class);
    return this.codeModel.ref(type).narrow(classes);
  }

  protected JPrimitiveType _void() {
    return this.codeModel.VOID;
  }

  protected JPrimitiveType _boolean() {
    return this.codeModel.BOOLEAN;
  }

  protected JClass _String() {
    return _classByNames(JAVA_LANG_STRING);
  }

  protected JClass _Class() {
    return _classByNames(JAVA_LANG_CLASS, "?"); //$NON-NLS-1$
  }

  public JExpression format(final String string, final JExpression... params) {
    final JClass formater = _classByNames(java.text.MessageFormat.class.getName());
    JInvocation expression = formater.staticInvoke("format").arg(string); //$NON-NLS-1$
    for (final JExpression var : params) {
      expression = expression.arg(var);
    }
    return expression;
  }
}