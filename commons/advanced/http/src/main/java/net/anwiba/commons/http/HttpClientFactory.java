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

import java.util.Optional;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

public class HttpClientFactory implements IHttpClientFactory {

  public HttpClientFactory() {
  }

  @Override
  public CloseableHttpClient create(final IHttpClientConfiguration configuration) {
    final HttpClientBuilder builder = HttpClients
        .custom()
        .setUserAgent(null)
        .setRedirectStrategy(new DefaultRedirectStrategy())
        .setConnectionManager(configuration.getManager());
    Optional.ofNullable(configuration.getUserAgent()).ifPresent(value -> builder.setUserAgent(value));
    return switch (configuration.getMode()) {
      case CLOSE -> builder
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

      case KEEP_ALIVE ->
        builder
            .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
            .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
            .build();
      };
  }
}