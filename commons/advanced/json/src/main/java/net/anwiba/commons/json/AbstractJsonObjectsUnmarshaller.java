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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

public abstract class AbstractJsonObjectsUnmarshaller<T, R, E extends IOException>
    extends
    AbstractJsonUnmarshaller<T, List<T>, R, IOException> {

  private final ObjectMapper mapper = new ObjectMapper();
  private final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory;
  private final Class<T> clazz;
  private final Map<String, Object> injectionValues = new HashMap<>();

  public AbstractJsonObjectsUnmarshaller(
      final Class<T> clazz,
      final Class<R> errorResponseClass,
      final Map<String, Object> injectionValues,
      final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory) {
    this(clazz, errorResponseClass, injectionValues, Collections.emptyList(), exceptionFactory);
  }

  public AbstractJsonObjectsUnmarshaller(
      final Class<T> clazz,
      final Class<R> errorResponseClass,
      final Map<String, Object> injectionValues,
      final Collection<DeserializationProblemHandler> problemHandlers,
      final IJsonObjectMarshallingExceptionFactory<R, E> exceptionFactory) {
    super(clazz, errorResponseClass, injectionValues, problemHandlers);
    this.clazz = clazz;
    this.injectionValues.putAll(injectionValues);
    this.exceptionFactory = exceptionFactory;
    this.mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    this.mapper.findAndRegisterModules();
  }

  @Override
  protected List<T> _unmarshal(final InputStream stream) throws IOException, E {
    stream.mark(Integer.MAX_VALUE);
    @SuppressWarnings("unchecked")
    final List<T> result = (List<T>) validate(stream);
    if (result != null) {
      return result;
    }
    stream.reset();
    stream.mark(Integer.MAX_VALUE);
    try {
      final InjectableValues.Std injectableValues = new InjectableValues.Std();
      for (final String key : this.injectionValues.keySet()) {
        injectableValues.addValue(key, this.injectionValues.get(key));
      }

      final JsonFactory f = new JsonFactory();
      try (final JsonParser parser = f.createParser(stream)) {
        final ObjectReader reader =
            this.mapper.readerFor(this.clazz).with(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS).with(injectableValues);
        final JsonToken token = parser.nextToken();
        if (token != JsonToken.START_ARRAY) {
          final T value = reader.readValue(parser);
          return Arrays.asList(value);
        }
        // and then each time, advance to opening START_OBJECT
        final List<T> results = new ArrayList<>();
        while (parser.nextToken() == JsonToken.START_OBJECT) {
          final T value = reader.readValue(parser);
          results.add(value);
        }
        return results;
      }
    } catch (final JsonParseException | JsonMappingException exception) {
      stream.reset();
      throw createIOException(stream, exception);
    }
  }

  @Override
  protected IOException createException(final R response) {
    return this.exceptionFactory.create(response);
  }
}