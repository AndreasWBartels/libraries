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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import net.anwiba.commons.http.apache.InputStreamEntity;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.functional.IWatcher;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.parameter.IParameter;

@SuppressWarnings({ "nls" })
public class HttpRequestExecutor implements IHttpRequestExecutor {

  private static ILogger logger = Logging.getLogger(HttpRequestExecutor.class.getName());
  private CloseableHttpClient client;
  private IHttpClientFactory httpClientFactory;
  private final boolean isClosed = false;
  private final HttpConnectionMode httpConnectionMode;

  HttpRequestExecutor(final HttpConnectionMode httpConnectionMode, final IHttpClientFactory httpClientFactory) {
    this.httpConnectionMode = httpConnectionMode;
    this.httpClientFactory = httpClientFactory;
  }

  @Override
  public IResponse execute(final ICanceler canceler, final IRequest request) throws InterruptedException, IOException {
    if (this.isClosed) {
      throw new IOException("executor is closed");
    }
    final HttpUriRequest method = create(request);
    try {
      logger.log(ILevel.DEBUG, () -> MessageFormat.format("request url: <{0}>", method.getURI()));
      canceler.check();
      this.client = this.client == null ? this.httpClientFactory.create() : this.client;
      final HttpResponse httpResponse = query(canceler, this.client, request, method);
      if (HttpConnectionMode.CLOSE.equals(this.httpClientFactory.getClientConfiguration().getMode())) {
        final Response response = new Response(canceler, this.client, method, httpResponse);
        this.client = null;
        return response;
      }
      return new Response(canceler, () -> {
      }, method, httpResponse);
    } catch (final IllegalStateException exception) {
      logger.log(ILevel.ALL, exception.getMessage(), exception);
      throw new InterruptedException(exception.getMessage());
    }
  }

  private HttpResponse query(
      final ICanceler canceler,
      final CloseableHttpClient httpClient,
      final IRequest request,
      final HttpUriRequest method)
      throws IOException,
      ClientProtocolException {
    try (IWatcher watcher = canceler.watcherFactory().create(() -> method.abort())) {
      if (request.getAuthentication() != null) {
        return httpClient.execute(method, createContext(request));
      }
      return httpClient.execute(method);
    }
  }

  private HttpContext createContext(final IRequest request) {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    final IAuthentication authentication = request.getAuthentication();
    credentialsProvider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(authentication.getUsername(), authentication.getPassword()));
    final AuthCache authCache = new BasicAuthCache();
    authCache.put(
        new HttpHost(
            request.getHost(),
            request.getPort(),
            request.getUriString().toLowerCase().startsWith("https:") ? "https" : "http"),
        new BasicScheme());
    final HttpClientContext context = HttpClientContext.create();
    context.setCredentialsProvider(credentialsProvider);
    context.setAuthCache(authCache);
    return context;
  }

  private HttpUriRequest create(final IRequest request) {
    switch (request.getMethodType()) {
      case POST: {
        final RequestBuilder requestBuilder = RequestBuilder.post(request.getUriString());
        addToQuery(request, requestBuilder);
        addToHeader(request.getProperties().parameters(), requestBuilder);
        Optional.ofNullable(request.getUserAgent()).ifPresent(value -> requestBuilder.addHeader("User-Agent", value));
        final HttpEntity entity = createEntity(request);
        requestBuilder.setEntity(entity);
        return requestBuilder.build();
      }
      case GET: {
        final RequestBuilder requestBuilder = RequestBuilder.get(request.getUriString());
        addToQuery(request, requestBuilder);
        addToHeader(request.getProperties().parameters(), requestBuilder);
        Optional.ofNullable(request.getUserAgent()).ifPresent(value -> requestBuilder.addHeader("User-Agent", value));
        return requestBuilder.build();
      }
    }
    throw new UnreachableCodeReachedException();
  }

  private void addToQuery(final IRequest request, final RequestBuilder requestBuilder) {
    for (final IParameter parameter : request.getParameters().parameters()) {
      requestBuilder.addParameter(parameter.getName(), URLDecoder.decode(parameter.getValue()));
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

  @Override
  public void close() throws IOException {
    if (this.isClosed) {
      return;
    }
    try {
      if (this.client == null) {
        return;
      }
      this.client.close();
    } finally {
      this.httpClientFactory = null;
      this.client = null;
    }
  }
}
