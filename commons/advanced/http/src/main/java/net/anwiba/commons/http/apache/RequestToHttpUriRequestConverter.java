/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.http.apache;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

import net.anwiba.commons.http.HttpConnectionMode;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.utilities.io.url.IUrl;
import net.anwiba.commons.utilities.io.url.UrlBuilder;
import net.anwiba.commons.utilities.io.url.parser.UrlParser;
import net.anwiba.commons.utilities.parameter.IParameter;

public final class RequestToHttpUriRequestConverter {

  private final HttpConnectionMode httpConnectionMode;

  public RequestToHttpUriRequestConverter(final HttpConnectionMode httpConnectionMode) {
    this.httpConnectionMode = httpConnectionMode;
  }

  @SuppressWarnings("nls")
  public HttpUriRequest convert(final IRequest request) throws ConversionException {
    try {
      UrlBuilder urlBuilder = new UrlBuilder(new UrlParser().parse(request.getUriString()));
      request.getParameters().forEach(urlBuilder::addQueryParameter);
      IUrl url = urlBuilder.build();
      switch (request.getMethodType()) {
        case POST: {
          final RequestBuilder requestBuilder = RequestBuilder.post(url.encoded());
          addToHeader(request.getProperties().parameters(), requestBuilder);
          Optional.ofNullable(request.getUserAgent()).ifPresent(value -> requestBuilder.addHeader("User-Agent", value));
          final HttpEntity entity = createEntity(request);
          requestBuilder.setEntity(entity);
          return requestBuilder.build();
        }
        case GET: {
          final RequestBuilder requestBuilder = RequestBuilder.get(url.encoded());
          addToHeader(request.getProperties().parameters(), requestBuilder);
          Optional.ofNullable(request.getUserAgent()).ifPresent(value -> requestBuilder.addHeader("User-Agent", value));
          return requestBuilder.build();
        }
      }
    } catch (CreationException | IllegalArgumentException exception) {
      throw new ConversionException("Couldn't convert reqeut to appache http request.", exception);
    }
    throw new UnreachableCodeReachedException();
  }

  private void addToQuery(final IRequest request, final RequestBuilder requestBuilder) {
    for (final IParameter parameter : request.getParameters().parameters()) {
      requestBuilder.addParameter(parameter.getName(), parameter.getValue());
    }
  }

  private void addToHeader(final Iterable<IParameter> parameters, final RequestBuilder requestBuilder) {
    for (final IParameter parameter : parameters) {
      if (Objects.equals(HttpHeaders.CONNECTION, parameter.getName())
          && HttpConnectionMode.CLOSE.equals(this.httpConnectionMode)) {
        continue;
      }
      requestBuilder.addHeader(parameter.getName(), parameter.getValue());
    }
    if (HttpConnectionMode.CLOSE.equals(this.httpConnectionMode)) {
      requestBuilder.addHeader(HttpHeaders.CONNECTION, HTTP.CONN_CLOSE);
    }
  }

  private HttpEntity createEntity(final IRequest request) {
    final String encoding = request.getEncoding();
    final Charset charset = encoding == null ? null : Charset.forName(encoding);
    final ContentType contentType = ContentType.create(request.getMimeType(), charset);
    return new InputStreamEntity(request.getContent(), request.getContentLength(), contentType);
  }
}
