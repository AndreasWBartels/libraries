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

import java.text.MessageFormat;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IProcedure;

import com.sun.codemodel.JArray;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

@SuppressWarnings("nls")
public class SourceFactoryUtilities {

  public static final class ValueConverter implements IConverter<Object, JExpression, RuntimeException> {
    private final JCodeModel codeModel;

    public ValueConverter(final JCodeModel codeModel) {
      this.codeModel = codeModel;
    }

    @Override
    public JExpression convert(final Object object) throws RuntimeException {
      if (object == null) {
        return JExpr._null();
      }
      if (object instanceof JExpression) {
        return (JExpression) object;
      }
      if (object instanceof Long) {
        return JExpr.lit(((Long) object).longValue());
      }
      if (object instanceof Integer) {
        return JExpr.lit(((Integer) object).intValue());
      }
      if (object instanceof Short) {
        return JExpr.lit(((Short) object).shortValue());
      }
      if (object instanceof Double) {
        return JExpr.lit(((Double) object).doubleValue());
      }
      if (object instanceof Float) {
        return JExpr.lit(((Float) object).floatValue());
      }
      if (object instanceof Character) {
        return JExpr.lit(((Character) object).charValue());
      }
      if (object instanceof Byte) {
        return JExpr.lit(((Byte) object).byteValue());
      }
      if (object instanceof Boolean) {
        return JExpr.lit(((Boolean) object).booleanValue());
      }
      if (object instanceof String) {
        return JExpr.lit(((String) object));
      }
      if (object.getClass().isArray()) {
        if (object instanceof Short[]) {
          final Short[] values = (Short[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.SHORT);
          for (final Short value : values) {
            newArray.add(JExpr.lit(value.shortValue()));
          }
          return newArray;
        }
        if (object instanceof Integer[]) {
          final Integer[] values = (Integer[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.INT);
          for (final Integer value : values) {
            newArray.add(JExpr.lit(value.intValue()));
          }
          return newArray;
        }
        if (object instanceof Long[]) {
          final Long[] values = (Long[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.LONG);
          for (final Long value : values) {
            newArray.add(JExpr.lit(value.longValue()));
          }
          return newArray;
        }
        if (object instanceof Float[]) {
          final Float[] values = (Float[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.FLOAT);
          for (final Float i : values) {
            newArray.add(JExpr.lit(i.floatValue()));
          }
          return newArray;
        }
        if (object instanceof Double[]) {
          final Double[] values = (Double[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.DOUBLE);
          for (final Double i : values) {
            newArray.add(JExpr.lit(i.doubleValue()));
          }
          return newArray;
        }
        if (object instanceof Character[]) {
          final Character[] values = (Character[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.CHAR);
          for (final Character i : values) {
            newArray.add(JExpr.lit(i.charValue()));
          }
          return newArray;
        }
        if (object instanceof Byte[]) {
          final Byte[] values = (Byte[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.BYTE);
          for (final Byte i : values) {
            newArray.add(JExpr.lit(i.byteValue()));
          }
          return newArray;
        }
        if (object instanceof Boolean[]) {
          final Boolean[] values = (Boolean[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel.BOOLEAN);
          for (final Boolean i : values) {
            newArray.add(JExpr.lit(i.booleanValue()));
          }
          return newArray;
        }
        if (object instanceof String[]) {
          final String[] values = (String[]) object;
          final JArray newArray = JExpr.newArray(this.codeModel._ref(java.lang.String.class));
          for (final String i : values) {
            newArray.add(JExpr.lit(i));
          }
          return newArray;
        }
      }
      throw new IllegalArgumentException();

    }
  }

  public static String createFieldName(final String name) {
    return resevedNames.contains(name) ? MessageFormat.format("_{0}", name) : name;
  }

  @SafeVarargs
  public static JVar addObjectParameter(
      final JMethod method,
      final JFieldVar field,
      final IProcedure<JVar, RuntimeException>... procedure) {
    if (method == null || field == null) {
      return null;
    }
    final JVar param = method.param(JMod.FINAL, field.type(), field.name());
    for (final IProcedure<JVar, RuntimeException> closure : procedure) {
      closure.execute(param);
    }
    method.body().assign(JExpr.refthis(field.name()), param);
    return param;
  }

  @SafeVarargs
  public static JVar addMapParameter(
      final JMethod method,
      final JFieldVar field,
      final JType nameType,
      final String nameVariableName,
      final JType valueType,
      final String valueVariableName,
      final IProcedure<JVar, RuntimeException>... procedure) {
    if (method == null || field == null) {
      return null;
    }
    final JVar nameParam = method.param(JMod.FINAL, nameType, nameVariableName);
    final JVar valueParam = method.param(JMod.FINAL, valueType, valueVariableName);
    for (final IProcedure<JVar, RuntimeException> closure : procedure) {
      closure.execute(nameParam);
      closure.execute(valueParam);
    }
    method.body().add(JExpr.refthis(field.name()).invoke("put").arg(nameParam).arg(valueParam)); //$NON-NLS-1$
    return valueParam;
  }

  @SafeVarargs
  public static JVar setMapParameters(
      final JMethod method,
      final JFieldVar field,
      final boolean isClearEnabled,
      final IProcedure<JVar, RuntimeException>... procedure) {
    if (method == null || field == null) {
      return null;
    }
    final JVar param = method.param(JMod.FINAL, field.type(), field.name());
    for (final IProcedure<JVar, RuntimeException> closure : procedure) {
      closure.execute(param);
    }
    if (isClearEnabled) {
      method.body().add(JExpr.refthis(field.name()).invoke("clear")); //$NON-NLS-1$
    }
    method.body().add(JExpr.refthis(field.name()).invoke("putAll").arg(param)); //$NON-NLS-1$
    return param;
  }

  @SafeVarargs
  public static JVar addListParameter(
      final JMethod method,
      final JFieldVar field,
      final boolean isClearEnabled,
      final IProcedure<JVar, RuntimeException>... procedure) {
    if (method == null || field == null) {
      return null;
    }
    final JVar param = method.param(JMod.FINAL, field.type(), field.name());
    for (final IProcedure<JVar, RuntimeException> closure : procedure) {
      closure.execute(param);
    }
    if (isClearEnabled) {
      method.body().add(JExpr.refthis(field.name()).invoke("clear")); //$NON-NLS-1$
    }
    method.body().add(JExpr.refthis(field.name()).invoke("addAll").arg(param)); //$NON-NLS-1$
    return param;
  }

  public static IProcedure<JVar, RuntimeException> createAddIfNullReturnNullClosure(final JMethod method) {
    ensureThatArgument(method, notNull());
    return new IProcedure<JVar, RuntimeException>() {

      @Override
      public void execute(final JVar param) throws RuntimeException {
        ensureThatArgument(param, notNull());
        method.body()._if(param.eq(JExpr._null()))._then()._return(JExpr._null());
      }
    };
  }

  public static IProcedure<JVar, RuntimeException> createAddIfNullSetEmptyArrayAndReturnClosure(
      final JCodeModel codeModel,
      final JMethod method,
      final JExpression returnValue) {
    ensureThatArgument(method, notNull());
    return new IProcedure<JVar, RuntimeException>() {

      @Override
      public void execute(final JVar param) throws RuntimeException {
        ensureThatArgument(param, notNull());
        final ValueConverter valueConverter = new ValueConverter(codeModel);
        final JInvocation invocation = JExpr._new(param.type());
        method
            .body()
            ._if(param.eq(JExpr._null()))
            ._then()
            .block()
            .assign(JExpr.refthis(param.name()), valueConverter.convert(invocation))
            ._return(returnValue);
      }
    };
  }

  public static IProcedure<JVar, RuntimeException> createAddIfNullClearMapAndReturnClosure(
      final JMethod method,
      final JExpression returnValue) {
    ensureThatArgument(method, notNull());
    return new IProcedure<JVar, RuntimeException>() {

      @Override
      public void execute(final JVar param) throws RuntimeException {
        ensureThatArgument(param, notNull());
        method
            .body()
            ._if(param.eq(JExpr._null()))
            ._then()
            .block()
            .add(JExpr.refthis(param.name()).invoke("clear"))
            ._return(returnValue);
      }
    };
  }

  public static IProcedure<JVar, RuntimeException> createAddIfNullClearListAndReturnClosure(
      final JMethod method,
      final JExpression returnValue) {
    ensureThatArgument(method, notNull());
    return new IProcedure<JVar, RuntimeException>() {

      @Override
      public void execute(final JVar param) throws RuntimeException {
        ensureThatArgument(param, notNull());
        method
            .body()
            ._if(param.eq(JExpr._null()))
            ._then()
            .block()
            .add(JExpr.refthis(param.name()).invoke("clear"))
            ._return(returnValue);
      }
    };
  }

  public static IProcedure<JVar, RuntimeException> createAddIfNullReturnClosure(
      final JMethod method,
      final JExpression returnValue) {
    ensureThatArgument(method, notNull());
    return new IProcedure<JVar, RuntimeException>() {

      @Override
      public void execute(final JVar param) throws RuntimeException {
        ensureThatArgument(param, notNull());
        method.body()._if(param.eq(JExpr._null()))._then()._return(returnValue);
      }
    };
  }

  @SafeVarargs
  public static IProcedure<JVar, RuntimeException> createEnsureArgumentNotNullClosure(
      final EnsurePredicateFactory ensurePredicateFactory,
      final JMethod method,
      final IAcceptor<JVar>... acceptors) {
    ensureThatArgument(ensurePredicateFactory, notNull());
    ensureThatArgument(method, notNull());
    return new IProcedure<JVar, RuntimeException>() {

      @Override
      public void execute(final JVar param) throws RuntimeException {
        ensureThatArgument(param, notNull());
        for (final IAcceptor<JVar> acceptor : acceptors) {
          if (!acceptor.accept(param)) {
            return;
          }
        }
        method.body().add(ensurePredicateFactory.ensureArgumentNotNull(param));
      }
    };
  }

  public static void adjust(
      final JCodeModel codeModel,
      final JFieldVar field,
      final Object value,
      final boolean isArrayNullable) {
    final ValueConverter valueConverter = new ValueConverter(codeModel);
    if (value instanceof JExpression) {
      field.init(valueConverter.convert(value));
    }
    final String typeName = field.type().isArray() ? field.type().elementType().name() : field.type().name();
    if (stringTypes.contains(typeName)) {
      if (!field.type().isArray()) {
        field.init(valueConverter.convert(value));
        return;
      }
      if (value == null && isArrayNullable) {
        field.init(JExpr._null());
        return;
      }
      final JArray newArray = JExpr.newArray(field.type().elementType());
      if (value != null) {
        final String[] values = (String[]) value;
        for (final String i : values) {
          newArray.add(valueConverter.convert(i));
        }
      }
      field.init(newArray);
      return;
    }
    if (floatTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_FLOAT, Float[].class);
      return;
    }
    if (doubleTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_DOUBLE, Double[].class);
      return;
    }
    if (shortTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_SHORT, Short[].class);
      return;
    }
    if (integerTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_INTEGER, Integer[].class);
      return;
    }
    if (longTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_LONG, Long[].class);
      return;
    }
    if (booleanTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_BOOLEAN, Boolean[].class);
      return;
    }
    if (byteTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_BYTE, Byte[].class);
      return;
    }
    if (characterTypes.contains(typeName)) {
      adjust(codeModel, field, value, isArrayNullable, valueConverter, JAVA_LANG_CHARACTER, Character[].class);
      return;
    }
    if (value == null) {
      field.init(JExpr._null());
    }
  }

  public static <C> void adjust(
      final JCodeModel codeModel,
      final JFieldVar field,
      final Object value,
      final boolean isArrayNullable,
      final ValueConverter valueConverter,
      final String className,
      final Class<C[]> clazz) {
    if (!field.type().isArray()) {
      if (field.type().isPrimitive()) {
        if (value == null) {
          if (field.type().name().equals("short")) {
            return;
          }
          if (field.type().name().equals("int")) {
            field.init(JExpr.lit(0));
            return;
          }
          if (field.type().name().equals("long")) {
            field.init(JExpr.lit(0l));
            return;
          }
          if (field.type().name().equals("float")) {
            field.init(JExpr.lit(Float.NaN));
            return;
          }
          if (field.type().name().equals("double")) {
            field.init(JExpr.lit(Double.NaN));
            return;
          }
          if (field.type().name().equals("boolean")) {
            field.init(JExpr.lit(false));
            return;
          }
          if (field.type().name().equals("byte")) {
            field.init(JExpr.lit(0x0b));
            return;
          }
          if (field.type().name().equals("char")) {
            field.init(JExpr.lit(0x0c));
            return;
          }
        }
        field.init(valueConverter.convert(value));
        return;
      }
      if (value == null) {
        field.init(JExpr._null());
        return;
      }
      field.init(codeModel.ref(className).staticInvoke("valueOf").arg(valueConverter.convert(value)));
      return;
    }
    if (value == null && isArrayNullable) {
      field.init(JExpr._null());
      return;
    }
    final JArray newArray = JExpr.newArray(field.type().elementType());
    if (value != null) {
      final C[] values = clazz.cast(value);
      for (final C i : values) {
        newArray.add((field.type().isArray() ? field.type().elementType().isPrimitive() : field.type().isPrimitive())
            ? valueConverter.convert(i)
            : codeModel.ref(className).staticInvoke("valueOf").arg(valueConverter.convert(i)));
      }
    }
    field.init(newArray);
  }

  public static boolean isInstanceOfList(final JType type) {
    try {
      final String className = withoutGenerics(type.fullName());
      final Class<?> clazz = Class.forName(className);
      return java.util.List.class.isAssignableFrom(clazz);
    } catch (final ClassNotFoundException exception) {
      return false;
    }
  }

  public static boolean isInstanceOfMap(final JType jclazz) {
    try {
      final String className = withoutGenerics(jclazz.fullName());
      final Class<?> clazz = Class.forName(className);
      return java.util.Map.class.isAssignableFrom(clazz);
    } catch (final ClassNotFoundException exception) {
      return false;
    }
  }

  public static String withoutGenerics(final String name) {
    if (name.indexOf('<') == -1) {
      return name;
    }
    return name.substring(0, name.indexOf('<'));
  }
}
