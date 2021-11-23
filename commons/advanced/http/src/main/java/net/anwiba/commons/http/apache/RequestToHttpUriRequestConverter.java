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

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpMessage;

import net.anwiba.commons.http.HttpConnectionMode;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.parameter.IParameter;
import net.anwiba.commons.utilities.io.url.IUrl;
import net.anwiba.commons.utilities.io.url.UrlBuilder;
import net.anwiba.commons.utilities.io.url.parser.UrlParser;

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
          HttpPost post = new HttpPost(url.encoded());
          addToHeader(request.getProperties().parameters(), post);
          Optional.ofNullable(request.getUserAgent()).ifPresent(value -> post.addHeader("User-Agent", value));
          final HttpEntity entity = createEntity(request);
          post.setEntity(entity);
          return post;
        }
        case GET: {
          HttpGet get = new HttpGet(url.encoded());
          addToHeader(request.getProperties().parameters(), get);
          Optional.ofNullable(request.getUserAgent()).ifPresent(value -> get.addHeader("User-Agent", value));
          return get;
        }
      }
    } catch (CreationException | IllegalArgumentException exception) {
      throw new ConversionException("Couldn't convert reqeut to appache http request.", exception);
    }
    throw new UnreachableCodeReachedException();
  }

  private void addToHeader(final Iterable<IParameter> parameters, final HttpMessage requestBuilder) {
    for (final IParameter parameter : parameters) {
      if (Objects.equals(HttpHeaders.CONNECTION, parameter.getName())
          && HttpConnectionMode.CLOSE.equals(this.httpConnectionMode)) {
        continue;
      }
      requestBuilder.addHeader(parameter.getName(), parameter.getValue());
    }
    if (HttpConnectionMode.CLOSE.equals(this.httpConnectionMode)) {
      requestBuilder.addHeader(HttpHeaders.CONNECTION, HeaderElements.CLOSE);
    }
  }

  private HttpEntity createEntity(final IRequest request) {
    final String encoding = request.getEncoding();
    final Charset charset = encoding == null ? null : Charset.forName(encoding);
    final ContentType contentType = ContentType.create(request.getMimeType(), charset);
    return new InputStreamEntity(request.getContent(), request.getContentLength(), contentType, encoding);
  }
}
