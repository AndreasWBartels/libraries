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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractJsonUnmarshaller<T, O, R, E extends IOException> {

  private final ObjectMapper mapper = new ObjectMapper();
  private final Class<R> errorResponseClass;
  private final Class<T> clazz;
  @SuppressWarnings("rawtypes")
  private final Map<Class, Object> injectionValues = new HashMap<>();

  public AbstractJsonUnmarshaller(
      final Class<T> clazz,
      final Class<R> errorResponseClass,
      @SuppressWarnings("rawtypes") final Map<Class, Object> injectionValues) {
    this.clazz = clazz;
    this.errorResponseClass = errorResponseClass;
    this.injectionValues.putAll(injectionValues);
    this.mapper.getFactory().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
  }

  public O unmarshal(final InputStream inputStream) throws IOException, E {
    return unmarshal(toString(inputStream, "UTF-8")); //$NON-NLS-1$
  }

  public static String toString(final InputStream inputStream, final String contentEncoding) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final byte[] buffer = new byte[4096];
    int numChars;
    while ((numChars = inputStream.read(buffer)) > 0) {
      out.write(buffer, 0, numChars);
    }
    return out.toString(contentEncoding);
  }

  protected abstract O unmarshal(final String body) throws IOException, E;

  private <X> X check(final String body, final Class<X> type)
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

  @SuppressWarnings("unchecked")
  protected T validate(final String body) throws IOException, E {
    if (Void.class.equals(this.errorResponseClass)) {
      return null;
    }
    try {
      final R response = check(body, this.errorResponseClass);
      if (this.clazz.isInstance(response)) {
        return (T) response;
      }
      throw createException(response);
    } catch (final JsonParseException e) {
      return null;
    } catch (final JsonMappingException e) {
      return null;
    }
  }

  protected abstract E createException(R response);

  protected IOException createIOException(final String content, final Exception exception) {
    return new IOException(
        MessageFormat.format("Error during mapping json resource, coudn''t map the content:\n {0}", content), //$NON-NLS-1$
        exception);
  }
}