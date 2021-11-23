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
package net.anwiba.commons.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;

public abstract class AbstractJsonObjectUnmarshaller<T, R, E extends IOException>
    extends
    AbstractJsonUnmarshaller<T, T, R, IOException> {

  private final ObjectMapper mapper;
  private final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory;
  private final Class<T> clazz;
  private final Map<String, Object> injectionValues = new HashMap<>();

  public AbstractJsonObjectUnmarshaller(
      final Class<T> clazz,
      final Class<R> errorResponseClass,
      final Map<String, Object> injectionValues,
      final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory) {
    this(clazz, errorResponseClass, injectionValues, Collections.emptyList(), exceptionFactory);
  }

  public AbstractJsonObjectUnmarshaller(
      final Class<T> clazz,
      final Class<R> errorResponseClass,
      final Map<String, Object> injectionValues,
      final Collection<DeserializationProblemHandler> problemHandlers,
      final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory) {
    super(clazz, errorResponseClass, injectionValues, problemHandlers);
    this.clazz = clazz;
    this.injectionValues.putAll(injectionValues);
    this.exceptionFactory = exceptionFactory;
    final Builder builder = JsonMapper.builder()
        .findAndAddModules()
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS);
    problemHandlers.forEach(h -> builder.addHandler(h));
    this.mapper = builder.build();
  }

  @Override
  protected T _unmarshal(final InputStream stream) throws IOException, E {
    stream.mark(Integer.MAX_VALUE);
    final T result = validate(stream);
    if (result != null) {
      return result;
    }
    stream.reset();
    stream.mark(Integer.MAX_VALUE);
    try {
      return read(stream, this.clazz);
    } catch (final JsonParseException | JsonMappingException exception) {
      stream.reset();
      throw createIOException(stream, exception);
    }
  }

  private <X> X read(final InputStream stream, final Class<X> type)
      throws IOException,
      JsonParseException,
      JsonMappingException,
      JsonProcessingException {
    final InjectableValues.Std injectableValues = new InjectableValues.Std();
    for (final String key : this.injectionValues.keySet()) {
      injectableValues.addValue(key, this.injectionValues.get(key));
    }
    return this.mapper.readerFor(type).with(injectableValues).readValue(stream);
  }

  @Override
  protected IOException createException(final R response) {
    return this.exceptionFactory.create(response);
  }
}