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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.http;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;

public class HttpClientFactory implements IHttpClientFactory {

  private final class RedirectStrategyImplementation extends DefaultRedirectStrategy
      implements
      RedirectStrategy {

    @Override
    public boolean isRedirected(
        final HttpRequest request,
        final HttpResponse response,
        final HttpContext context)
        throws ProtocolException {
      Args.notNull(request, "HTTP request");
      Args.notNull(response, "HTTP response");
      return super.isRedirected(request, response, context)
          || isRedirectedHttp308(request, response);
    }

    private boolean isRedirectedHttp308(final HttpRequest request, final HttpResponse response) {
      final int statusCode = response.getCode();
      return statusCode == 308;
    }
  }

  private final IHttpClientConfiguration configuration;

  public HttpClientFactory(final IHttpClientConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public CloseableHttpClient create() {
    final HttpClientBuilder builder = HttpClients
        .custom()
        .setRedirectStrategy(new RedirectStrategyImplementation())
        .setConnectionManager(this.configuration.getManager());
    this.configuration.getProxyConfiguration().consume(c -> addProxies(builder, c));
    switch (this.configuration.getMode()) {
      case CLOSE: {
        return builder
            .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

              @Override
              public TimeValue getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                return TimeValue.ZERO_MILLISECONDS;
              }
            })
            .setConnectionReuseStrategy(new ConnectionReuseStrategy() {
              @Override
              public boolean keepAlive(
                  final HttpRequest request,
                  final HttpResponse response,
                  final HttpContext context) {
                return false;
              }
            })
            .build();
      }
      default: {
        return builder
            .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
            .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
            .build();
      }
    }
  }

  private void addProxies(final HttpClientBuilder builder, final IHttpProxyConfiguration configuration) {
    builder.setProxy(new HttpHost(configuration.getScheme(), configuration.getHost(), configuration.getPort()));
  }

  @Override
  public IHttpClientConfiguration getClientConfiguration() {
    return this.configuration;
  }

}