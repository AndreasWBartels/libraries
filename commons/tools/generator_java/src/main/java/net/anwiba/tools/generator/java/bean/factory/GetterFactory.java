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

import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.isInstanceOfMap;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;

import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class GetterFactory extends AbstractSourceFactory {

  public GetterFactory(final JCodeModel codeModel,
      final IFactory<String, Class<? extends java.lang.annotation.Annotation>,
          CreationException> annotationClassfactory) {
    super(codeModel, annotationClassfactory);
  }

  public void create(
      final JDefinedClass instance,
      final boolean isNullable,
      final boolean isCollectionNullable,
      final List<Annotation> annotationConfigurations,
      final IOptional<String, RuntimeException> comment,
      final JFieldVar field,
      final String name) throws CreationException {
    final JMethod method = instance.method(JMod.PUBLIC, field.type(), name);
    comment.consume(text -> addTo(method.javadoc(), text));
    annotate(method, annotationConfigurations);
    final JBlock body = method.body();
    if (isCollectionNullable && isNullable && isInstanceOfMap(field.type())) {
      body._if(JExpr.refthis(field.name()).invoke("isEmpty"))._then()._return(JExpr._null()); //$NON-NLS-1$
    }
    body._return(JExpr.refthis(field.name()));
  }
}