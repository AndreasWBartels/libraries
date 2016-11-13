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

import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.*;

import java.util.List;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;

public class SetterFactory extends AbstractSourceFactory {

  private final EnsurePredicateFactory ensurePredicateFactory;
  private final JCodeModel codeModel;

  public SetterFactory(final JCodeModel codeModel, final EnsurePredicateFactory ensurePredicateFactory) {
    super(codeModel);
    this.codeModel = codeModel;
    this.ensurePredicateFactory = ensurePredicateFactory;
  }

  public void create(
      final JDefinedClass instance,
      final JFieldVar field,
      final String name,
      final boolean isImutable,
      final boolean isNullable,
      final List<Annotation> annotations,
      final JType nameType,
      final String nameVar,
      final JType valueType,
      final String value) {
    mapSetter(instance, field, name, isImutable, isNullable, annotations, nameType, nameVar, valueType, value);
  }

  private JVar mapSetter(
      final JDefinedClass instance,
      final JFieldVar field,
      final String name,
      final boolean isImutable,
      final boolean isNullable,
      final List<Annotation> annotationConfigurations,
      final JType nameVariableType,
      final String nameVariableName,
      final JType valueVariableType,
      final String valueVariableName) {
    final JMethod method = instance.method(JMod.PUBLIC, _void(), name);
    annotate(method, annotationConfigurations);
    if (isImutable) {
      return SourceFactoryUtilities.addParameter(method, field);
    }
    if (isNullable) {
      return addMapParameter(
          method,
          field,
          nameVariableType,
          nameVariableName,
          valueVariableType,
          valueVariableName,
          createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, method, new IAcceptor<JVar>() {

            @Override
            public boolean accept(final JVar value) {
              return value.name().equals(nameVariableName);
            }
          }));
    }
    return addMapParameter(
        method,
        field,
        nameVariableType,
        nameVariableName,
        valueVariableType,
        valueVariableName,
        createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, method));
  }

  public JVar create(
      final JDefinedClass instance,
      final boolean returnInstance,
      final JFieldVar field,
      final String name,
      final boolean isImutable,
      final boolean isNullable,
      final boolean isArrayNullable,
      final boolean isCollectionNullable,
      final List<Annotation> annotations) {
    final JMethod method = instance.method(JMod.PUBLIC, returnInstance ? instance : _void(), name);
    annotate(method, annotations);
    final JVar variable = addParameter(
        method,
        returnInstance ? JExpr._this() : null,
        field,
        isImutable,
        isNullable,
        isArrayNullable,
        isCollectionNullable);
    if (returnInstance) {
      method.body()._return(JExpr._this());
    }
    return variable;
  }

  private JVar addParameter(
      final JMethod method,
      final JExpression returnValue,
      final JFieldVar field,
      final boolean isImutable,
      final boolean isNullable,
      final boolean isArrayNullable,
      final boolean isCollectionNullable) {
    if (isImutable) {
      return SourceFactoryUtilities.addParameter(method, field);
    }
    if (isInstanceOfMap(field.type())) {
      return mapSetter(method, returnValue, field, isNullable);
    }
    if (isInstanceOfList(field.type())) {
      return listSetter(method, returnValue, field, isNullable, isCollectionNullable);
    }
    return objectSetter(method, returnValue, field, isNullable, isArrayNullable);
  }

  private JVar mapSetter(
      final JMethod method,
      final JExpression returnValue,
      final JFieldVar field,
      final boolean isNullable) {
    if (isNullable) {
      return setMapParameters(method, field, true, createAddIfNullClearMapAndReturnClosure(method, returnValue));
    }
    return setMapParameters(
        method,
        field,
        true,
        createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, method));
  }

  private JVar listSetter(
      final JMethod method,
      final JExpression returnValue,
      final JFieldVar field,
      final boolean isNullable,
      final boolean isCollectionNullable) {
    if (isNullable) {
      if (!isCollectionNullable) {
        return addListParameter(method, field, true, createAddIfNullClearListAndReturnClosure(method, returnValue));
      }
      return addListParameter(method, field, true, createAddIfNullReturnClosure(method, returnValue));
    }
    return addListParameter(
        method,
        field,
        true,
        createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, method));
  }

  private JVar objectSetter(
      final JMethod method,
      final JExpression returnValue,
      final JFieldVar field,
      final boolean isNullable,
      final boolean isArrayNullable) {
    if (isNullable) {
      if (!isArrayNullable && field.type().isArray()) {
        return addObjectParameter(
            method,
            field,
            createAddIfNullSetEmptyArrayAndReturnClosure(this.codeModel, method, returnValue));
      }
      return addObjectParameter(method, field);
    }
    return addObjectParameter(method, field, createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, method));
  }
}