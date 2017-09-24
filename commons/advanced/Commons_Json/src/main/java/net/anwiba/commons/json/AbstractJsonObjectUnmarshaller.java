/*
 * #%L anwiba commons advanced %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.commons.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractJsonObjectUnmarshaller<T, R, E extends IOException>
    extends
    AbstractJsonUnmarshaller<T, T, R, IOException> {

  private final ObjectMapper mapper = new ObjectMapper();
  private final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory;
  private final Class<T> clazz;
  @SuppressWarnings("rawtypes")
  private final Map<Class, Object> injectionValues = new HashMap<>();

  @SuppressWarnings("rawtypes")
  public AbstractJsonObjectUnmarshaller(
      final Class<T> clazz,
      final Class<R> errorResponseClass,
      final Map<Class, Object> injectionValues,
      final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory) {
    super(clazz, errorResponseClass, injectionValues);
    this.clazz = clazz;
    this.injectionValues.putAll(injectionValues);
    this.exceptionFactory = exceptionFactory;
    this.mapper.getFactory().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
  }

  @Override
  public T unmarshal(final String body) throws IOException, E {
    final T result = validate(body);
    if (result != null) {
      return result;
    }
    try {
      return read(body, this.clazz);
    } catch (final JsonParseException exception) {
      throw createIOException(body, exception);
    } catch (final JsonMappingException exception) {
      throw createIOException(body, exception);
    }
  }

  private <X> X read(final String body, final Class<X> type)
      throws IOException,
      JsonParseException,
      JsonMappingException,
      JsonProcessingException {
    final InjectableValues.Std injectableValues = new InjectableValues.Std();
    for (@SuppressWarnings("rawtypes")
    final Class key : this.injectionValues.keySet()) {
      injectableValues.addValue(key, this.injectionValues.get(key));
    }
    return this.mapper.readerFor(type).with(injectableValues).readValue(body);
  }

  @Override
  protected IOException createException(final R response) {
    return this.exceptionFactory.create(response);
  }
}