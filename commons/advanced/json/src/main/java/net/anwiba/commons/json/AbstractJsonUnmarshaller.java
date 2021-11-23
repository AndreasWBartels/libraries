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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;

import net.anwiba.commons.lang.io.NoneClosingInputStream;

public abstract class AbstractJsonUnmarshaller<T, O, R, E extends IOException> {

  private final ObjectMapper mapper;
  private final Class<R> errorResponseClass;
  private final Class<T> clazz;
  private final Map<String, Object> injectionValues = new HashMap<>();

  public AbstractJsonUnmarshaller(
      final Class<T> clazz,
      final Class<R> errorResponseClass,
      final Map<String, Object> injectionValues,
      final Collection<DeserializationProblemHandler> problemHandlers) {
    this.clazz = clazz;
    this.errorResponseClass = errorResponseClass;
    this.injectionValues.putAll(injectionValues);
    final Builder builder = JsonMapper.builder()
        .findAndAddModules()
        .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS);
    problemHandlers.forEach(h -> builder.addHandler(h));
    this.mapper = builder.build();
  }

  public final O unmarshal(final String body) throws IOException, E {
    return unmarshal(new ByteArrayInputStream(body.getBytes(Charset.forName("UTF-8")))); //$NON-NLS-1$
  }

  @SuppressWarnings("resource")
  public final O unmarshal(final InputStream inputStream) throws IOException, E {
    return _unmarshal(
        new NoneClosingInputStream(
            inputStream instanceof BufferedInputStream
                ? (BufferedInputStream) inputStream
                : new BufferedInputStream(inputStream)));
  }

  protected abstract O _unmarshal(final InputStream inputStream) throws IOException, E;

  private String toString(final InputStream inputStream, final String contentEncoding) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final byte[] buffer = new byte[4096];
    int numChars;
    while ((numChars = inputStream.read(buffer)) > 0) {
      out.write(buffer, 0, numChars);
    }
    final String string = out.toString(contentEncoding);
    return string.length() > 8200 ? string.substring(0, 8200) + "..." : string; //$NON-NLS-1$
  }

  private <X> X check(final InputStream stream, final Class<X> type)
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

  @SuppressWarnings("unchecked")
  protected T validate(final InputStream stream) throws IOException, E {
    if (Void.class.equals(this.errorResponseClass)) {
      return null;
    }
    try {
      final R response = check(stream, this.errorResponseClass);
      if (this.errorResponseClass.isInstance(response)) {
        if (this.errorResponseClass == this.clazz) {
          return (T) response;
        }
        if (!isErrorResponse(response)) {
          return null;
        }
        throw createException(response);
      }
      if (this.clazz.isInstance(response)) {
        return (T) response;
      }
      return null;
    } catch (final JsonParseException e) {
      return null;
    } catch (final JsonMappingException e) {
      return null;
    }
  }

  protected boolean isErrorResponse(final R response) {
    return true;
  }

  protected abstract E createException(R response);

  protected IOException createIOException(final InputStream content, final Exception exception) {
    try {
      return new IOException(
          MessageFormat.format(
              "Error during mapping json resource, coudn''t map the content:\n {0}", //$NON-NLS-1$
              toString(content, "UTF-8")), //$NON-NLS-1$
          exception);
    } catch (final IOException exception1) {
      return new IOException(
          "Error during mapping json resource, coudn''t map the content", //$NON-NLS-1$
          exception);
    }
  }
}