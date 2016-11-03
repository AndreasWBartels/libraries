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
package net.anwiba.tools.definition.schema.json.generator.java.bean;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotation;
import net.anwiba.tools.definition.schema.json.gramma.element.JParameter;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.AnnotationBuilder;

import java.util.HashMap;
import java.util.Map;

import static net.anwiba.tools.generator.java.bean.configuration.Builders.*;

public class AnnotationHandler {

  boolean isJssdAnnotation(final JAnnotation annotation) {
    return !annotation.name().contains(".") && annotation.name().toLowerCase().startsWith("jssd"); //$NON-NLS-1$//$NON-NLS-2$
  }

  boolean isGetterAnnotation(final JAnnotation annotation) {
    return IterableUtilities.containsAcceptedItems(annotation.parameters(), new IAcceptor<JParameter>() {

      @Override
      public boolean accept(final JParameter parameter) {
        return parameter.name().equalsIgnoreCase("JssdTarget") //$NON-NLS-1$
            && parameter.value().toString().equalsIgnoreCase("getter"); //$NON-NLS-1$
      }
    });
  }

  Annotation convertGetterAnnotation(final JAnnotation annotation) {
    return convert(annotation);
  }

  Annotation convertSetterAnnotation(final JAnnotation annotation) {
    return convert(annotation);
  }

  boolean isSetterAnnotation(final JAnnotation annotation) {
    return IterableUtilities.containsAcceptedItems(annotation.parameters(), new IAcceptor<JParameter>() {

      @Override
      public boolean accept(final JParameter parameter) {
        return parameter.name().equalsIgnoreCase("JssdTarget") //$NON-NLS-1$
            && parameter.value().toString().equalsIgnoreCase("setter"); //$NON-NLS-1$
      }
    });
  }

  Annotation convert(final JAnnotation annotation) {
    final AnnotationBuilder builder = annotation(annotation.name());
    for (final JParameter parameter : annotation.parameters()) {
      addAnnotationParameter(builder, parameter);
    }
    return builder.build();
  }

  @SuppressWarnings("rawtypes")
  private void addAnnotationParameter(final AnnotationBuilder builder, final JParameter parameter) {
    if (parameter.name().toLowerCase().startsWith("jssd")) { //$NON-NLS-1$
      return;
    }
    final Object value = parameter.value();
    if (value == null) {
      throw new IllegalArgumentException();
    }
    if (value instanceof String) {
      builder.parameter(parameter.name(), (String) value);
      return;
    }
    if (value instanceof Class) {
      builder.parameter(parameter.name(), (Class) value);
      return;
    }
    if (value instanceof Integer) {
      builder.parameter(parameter.name(), ((Integer) value).intValue());
      return;
    }
    throw new UnsupportedOperationException();
  }

  public Map<String, JAnnotation> extractJssdAnnotations(final Iterable<JAnnotation> annotations) {
    final Map<String, JAnnotation> map = new HashMap<>();
    for (final JAnnotation annotation : annotations) {
      if (isJssdAnnotation(annotation)) {
        map.put(annotation.name(), annotation);
      }
    }
    return map;
  }
}