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

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;

import net.anwiba.commons.http.IAuthentication;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.lang.optional.Optional;

public final class HttpContextFactory {

  @SuppressWarnings("nls")
  public HttpContext create(final IRequest request) {
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    final IAuthentication authentication = request.getAuthentication();
    final char[] password = Optional.of(authentication.getPassword())
        .convert(p -> p.toCharArray())
        .get();
    final HttpHost host = new HttpHost(
        request.getUriString().toLowerCase().startsWith("https:") ? "https" : "http",
        request.getHost(),
        request.getPort());
    credentialsProvider.setCredentials(
        new AuthScope(host),
        new UsernamePasswordCredentials(authentication.getUsername(), password));
    final AuthCache authCache = new BasicAuthCache();
    authCache.put(host, new BasicScheme());
    final HttpClientContext context = HttpClientContext.create();
    context.setCredentialsProvider(credentialsProvider);
    context.setAuthCache(authCache);
    return context;
  }
}
