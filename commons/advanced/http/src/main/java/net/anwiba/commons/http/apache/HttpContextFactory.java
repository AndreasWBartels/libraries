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

import net.anwiba.commons.http.IAuthentication;
import net.anwiba.commons.http.IHttpClientConfiguration;
import net.anwiba.commons.http.IHttpProxyConfiguration;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.lang.optional.Optional;

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.RequestConfig.Builder;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.protocol.HttpContext;

public final class HttpContextFactory {

  public HttpContext create(final IHttpClientConfiguration configuration, final IRequest request) {
    final HttpClientContext context = HttpClientContext.create();
    final AuthCache authCache = new BasicAuthCache();
    if (request.getAuthentication() != null) {
      final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      addAuthentificationCredentialsTo(
          request.getHost(),
          request.getPort(),
          request.getAuthentication(),
          authCache,
          credentialsProvider);

      configuration.getProxyConfiguration()
          .consume(c -> c.getAuthentication()
              .consume(a -> addAuthentificationCredentialsTo(
                  c.getHost(),
                  c.getPort(),
                  a,
                  authCache,
                  credentialsProvider)));

      context.setCredentialsProvider(credentialsProvider);
      context.setAuthCache(authCache);
    }

    Builder builder = RequestConfig.custom();
    configuration.getProxyConfiguration().consume(c -> addProxies(builder, c));
    context.setRequestConfig(builder.build());
    return context;
  }

  private void addProxies(final Builder builder, final IHttpProxyConfiguration configuration) {
    builder.setProxy(new HttpHost(configuration.getScheme(), configuration.getHost(), configuration.getPort()));
  }

  private void addAuthentificationCredentialsTo(
      final String host,
      final int port,
      final IAuthentication authentication,
      final AuthCache authCache,
      final BasicCredentialsProvider credentialsProvider) {
    if (authentication == null) {
      return;
    }
    final char[] password = Optional.of(authentication.getPassword())
        .convert(p -> p.toCharArray())
        .get();
    final HttpHost httpHost = new HttpHost(
        URIScheme.HTTP.getId(),
        host,
        port);
    final HttpHost httpsHost = new HttpHost(
        URIScheme.HTTPS.getId(),
        host,
        port);
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(authentication.getUsername(), password);
    credentialsProvider.setCredentials(new AuthScope(httpHost), credentials);
    credentialsProvider.setCredentials(new AuthScope(httpsHost), credentials);
    BasicScheme authScheme = new BasicScheme();
    if (authentication.isPreemptive() || authentication.isForces()) {
      authScheme.initPreemptive(credentials);
    }
    authCache.put(httpHost, authScheme);
    authCache.put(httpsHost, authScheme);
  }
}
