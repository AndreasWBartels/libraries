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
package net.anwiba.commons.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Optional;

import net.anwiba.commons.http.apache.InputStreamEntity;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.process.cancel.ICanceler;
import net.anwiba.commons.utilities.parameter.IParameter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;

public class HttpRequestExecutor implements IHttpRequestExecutor {

  private static ILogger logger = Logging.getLogger(HttpRequestExecutor.class.getName());
  private final IHttpClientFactory httpClientFactory;

  public HttpRequestExecutor(final IHttpClientFactory httpClientFactory) {
    this.httpClientFactory = httpClientFactory;
  }

  @SuppressWarnings("nls")
  @Override
  public IResponse execute(final ICanceler canceler, final IRequest request) throws InterruptedException, IOException {
    final HttpUriRequest method = create(request);
    logger.log(ILevel.DEBUG, () -> MessageFormat.format("request url: <{0}>", method.getURI()));
    final HttpClient client = this.httpClientFactory.create();
    canceler.check();
    final HttpResponse response = client.execute(method);
    return new Response(canceler, client, response);
  }

  @SuppressWarnings("nls")
  private HttpUriRequest create(final IRequest request) {
    switch (request.getMethodType()) {
      case POST: {
        final RequestBuilder requestBuilder = RequestBuilder.post(request.getUriString());
        final Iterable<IParameter> parameters = request.getParameters().parameters();
        for (final IParameter parameter : parameters) {
          requestBuilder.addParameter(parameter.getName(), parameter.getValue());
        }
        Optional.ofNullable(request.getUserAgent()).ifPresent(value -> requestBuilder.addHeader("User-Agent", value));
        final HttpEntity entity = createEntity(request);
        requestBuilder.setEntity(entity);
        return requestBuilder.build();
      }
      case GET: {
        final RequestBuilder requestBuilder = RequestBuilder.get(request.getUriString());
        final Iterable<IParameter> parameters = request.getParameters().parameters();
        for (final IParameter parameter : parameters) {
          requestBuilder.addParameter(parameter.getName(), parameter.getValue());
        }
        Optional.ofNullable(request.getUserAgent()).ifPresent(value -> requestBuilder.addHeader("User-Agent", value));
        return requestBuilder.build();
      }
    }
    throw new UnreachableCodeReachedException();
  }

  public HttpEntity createEntity(final IRequest request) {
    final String encoding = request.getEncoding();
    final Charset charset = encoding == null ? null : Charset.forName(encoding);
    final ContentType contentType = ContentType.create(request.getMimeType(), charset);
    return new InputStreamEntity(request.getContent(), request.getContentLength(), contentType);
  }
}
