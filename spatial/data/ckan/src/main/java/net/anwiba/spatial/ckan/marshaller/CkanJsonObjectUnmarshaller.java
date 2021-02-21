/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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

package net.anwiba.spatial.ckan.marshaller;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

import net.anwiba.commons.json.AbstractJsonObjectUnmarshaller;
import net.anwiba.commons.lang.map.HashMapBuilder;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.spatial.ckan.json.factory.ExtraValueFactory;
import net.anwiba.spatial.ckan.json.schema.v1_0.Response;
import net.anwiba.spatial.ckan.json.schema.v1_0.Result;

public class CkanJsonObjectUnmarshaller<T> extends
    AbstractJsonObjectUnmarshaller<T, Response, CkanJsonMapperException> {

  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(CkanJsonObjectUnmarshaller.class);

  public CkanJsonObjectUnmarshaller(final Class<T> clazz) {
    super(
        clazz,
        Response.class,
        new HashMapBuilder<String, Object>().put("extravaluefactory", new ExtraValueFactory()).build(), //$NON-NLS-1$
        Arrays.asList(new DeserializationProblemHandler() {
          @Override
          public Object handleMissingInstantiator(
              final DeserializationContext ctxt,
              final Class<?> instClass,
              final ValueInstantiator valueInsta,
              final JsonParser p,
              final String msg)
              throws IOException {
            if (Response.class.isAssignableFrom(instClass) || Result.class.isAssignableFrom(instClass)) {
              return super.handleMissingInstantiator(ctxt, instClass, valueInsta, p, msg);
            }
            logger.log(ILevel.WARNING, "Cannot construct instance of '" + instClass.getName() + "', " + msg);
            return null;
          }
        }),
        new CkanJsonMapperExceptionFactory());
  }

}
