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

import static net.anwiba.commons.ensure.Conditions.*;
import static net.anwiba.commons.ensure.Ensure.*;
import static net.anwiba.tools.generator.java.bean.JavaConstants.*;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.Parameter;
import net.anwiba.tools.generator.java.bean.configuration.Type;
import net.anwiba.tools.generator.java.bean.value.IValueTypeVisitor;

public class AbstractSourceFactory {

  private final JCodeModel codeModel;

  public AbstractSourceFactory(final JCodeModel codeModel) {
    ensureThatArgument(codeModel, notNull());
    this.codeModel = codeModel;
  }

  protected void annotate(final JAnnotatable annotatable, final Iterable<Annotation> annotationConfigurations) {
    ensureThatArgument(annotatable, notNull());
    ensureThatArgument(annotationConfigurations, notNull());
    for (final Annotation annotationConfiguration : annotationConfigurations) {
      annotate(annotatable, annotationConfiguration);
    }
  }

  protected void annotate(final JAnnotatable annotatable, final Annotation annotation) {
    ensureThatArgument(annotatable, notNull());
    ensureThatArgument(annotation, notNull());
    final JAnnotationUse annotate = annotatable.annotate(_classByNames(annotation.name()));
    final Iterable<Parameter> parameters = annotation.parameters();
    for (final Parameter parameter : parameters) {
      for (final Object value : parameter.values()) {
        parameter.type().accept(new IValueTypeVisitor() {

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
            annotate.param(parameter.name(), Class.class.cast(value));
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

      @SuppressWarnings("synthetic-access")
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

      @SuppressWarnings("synthetic-access")
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

  public JExpression format(final String string, final JExpression... params) {
    final JClass formater = _classByNames(java.text.MessageFormat.class.getName());
    JInvocation expression = formater.staticInvoke("format").arg(string); //$NON-NLS-1$
    for (final JExpression var : params) {
      expression = expression.arg(var);
    }
    return expression;
  }
}