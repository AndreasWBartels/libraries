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

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.tools.definition.schema.json.JSSDReader;
import net.anwiba.tools.definition.schema.json.gramma.element.JObject;
import net.anwiba.tools.definition.schema.json.gramma.parser.JssdParserException;
import net.anwiba.tools.generator.java.bean.BeanGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonBeanGenerator {

  private final String encoding = "UTF-8"; //$NON-NLS-1$
  private final JObjectToBeanConverter objectToBeanConverter;
  private final String comment;
  private final JSSDReader jssdReader = new JSSDReader();
  private final BeanGenerator generator;

  public JsonBeanGenerator(final String packageName, final String comment, final boolean isBuilderBeanPatternEnabled) {
    this.comment = comment;
    this.objectToBeanConverter = new JObjectToBeanConverter(packageName, isBuilderBeanPatternEnabled);
    this.generator = new BeanGenerator(name -> {
      try {
        return (Class<? extends java.lang.annotation.Annotation>)
          com.fasterxml.jackson.annotation.JacksonAnnotation.class
              .getClassLoader()
              .loadClass(name);
      } catch (final ClassNotFoundException exception) {
        throw new CreationException(exception.getMessage(), exception);
      }
    });
  }

  public void add(final InputStream inputStream, final String name)
      throws CreationException,
      IOException,
      JssdParserException {
    CreationException createException = null;
    for (final JObject object : this.jssdReader.read(inputStream, this.encoding)) {
      try {
        this.generator.add(this.objectToBeanConverter.convert(name, object));
      } catch (final ConversionException exception) {
        if (createException == null) {
          createException = new CreationException(exception.getMessage(), exception);
          continue;
        }
        createException.addSuppressed(exception);
      }
    }
    if (createException != null) {
      throw createException;
    }
  }

  public void generate(final OutputStream ouputStream) throws IOException {
    this.generator.generate(ouputStream);
  }

  public void generate(final File targetFolder) throws IOException {
    this.generator.generate(targetFolder, this.comment);
  }

}
