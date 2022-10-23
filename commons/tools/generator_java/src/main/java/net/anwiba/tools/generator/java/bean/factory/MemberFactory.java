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

import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_UTIL_ARRAYLIST;
import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_UTIL_LINKHASHMAP;
import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_UTIL_LIST;
import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_UTIL_MAP;
import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.adjust;
import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.createFieldName;
import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.isInstanceOfList;
import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.isInstanceOfMap;
import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.withoutGenerics;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.Type;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

public class MemberFactory extends AbstractSourceFactory {

  private final JCodeModel codeModel;

  public MemberFactory(final JCodeModel codeModel,
      final IFactory<String, Class<? extends java.lang.annotation.Annotation>,
          CreationException> annotationClassfactory) {
    super(codeModel, annotationClassfactory);
    this.codeModel = codeModel;
  }

  public JFieldVar create(
      final JDefinedClass instance,
      final IOptional<String, RuntimeException> comment,
      final Iterable<Annotation> annotation,
      final Type type,
      final String name,
      final Object value,
      final boolean isImutable,
      final boolean isNullable,
      final boolean isPrimitivesEnabled,
      final boolean isArrayNullable,
      final boolean isCollectionNullable) throws CreationException {
    final String fieldName = createFieldName(name);
    final JType clazz = _class(type, isPrimitivesEnabled);
    final JFieldVar field = isInstanceOfMap(clazz) //
        ? mapMember(instance, clazz, fieldName, type.generics())
        : isInstanceOfList(clazz) //
            ? listMember(instance, clazz, fieldName, type.generics(), isNullable, isCollectionNullable)
        : objectMember(instance, clazz, fieldName, value, isImutable, isArrayNullable);
    comment.consume(text -> addTo(field.javadoc(), text));
    annotate(field, annotation);

    return field;
  }

  private JFieldVar listMember(
      final JDefinedClass instance,
      final JType clazz,
      final String name,
      final String[] generics,
      final boolean isNullable,
      final boolean isCollectionNullable) {
    final JFieldVar field = instance.field(JMod.FINAL | JMod.PRIVATE, clazz, name);
    if (isNullable && isCollectionNullable) {
      return field;
    }
    final JType type = withoutGenerics(clazz.fullName()).startsWith(JAVA_UTIL_LIST)
        ? _type(JAVA_UTIL_ARRAYLIST, generics)
        : clazz;
    field.init(JExpr._new(type));
    return field;
  }

  private JFieldVar objectMember(
      final JDefinedClass instance,
      final JType clazz,
      final String name,
      final Object value,
      final boolean isImutable,
      final boolean isArrayNullable) {
    final JFieldVar field = isImutable
        ? instance.field(JMod.FINAL | JMod.PRIVATE, clazz, name)
        : instance.field(JMod.PRIVATE, clazz, name);
    adjust(this.codeModel, field, value, isArrayNullable);
    return field;
  }

  public JFieldVar mapStaticMember(
      final JDefinedClass instance,
      final JType clazz,
      final String name,
      final String[] generics) {
    final JFieldVar field = instance.field(JMod.FINAL | JMod.PRIVATE | JMod.STATIC, clazz, name);
    return mapMember(field, clazz, generics);
  }

  public JFieldVar mapMember(final JFieldVar field, final JType clazz, final String[] generics) {
    final JType type = withoutGenerics(clazz.fullName()).startsWith(JAVA_UTIL_MAP)
        ? _type(JAVA_UTIL_LINKHASHMAP, generics)
        : clazz;
    field.init(JExpr._new(type));
    return field;
  }

  public JFieldVar mapMember(
      final JDefinedClass instance,
      final JType clazz,
      final String name,
      final String[] generics) {
    final JFieldVar field = instance.field(JMod.FINAL | JMod.PRIVATE, clazz, name);
    return mapMember(field, clazz, generics);
  }
}