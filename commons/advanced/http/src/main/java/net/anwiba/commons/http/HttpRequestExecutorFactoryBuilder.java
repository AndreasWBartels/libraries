/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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

public class HttpRequestExecutorFactoryBuilder implements IHttpRequestExecutorFactoryBuilder {

  private HttpClientConfigurationBuilder builder;

  public HttpRequestExecutorFactoryBuilder() {
    builder = new HttpClientConfigurationBuilder();
  }

  public HttpRequestExecutorFactoryBuilder(IHttpClientConfiguration configuration) {
    builder = new HttpClientConfigurationBuilder(configuration);
  }
  
  @Override
  public HttpRequestExecutorFactoryBuilder setUserAgent(final String userAgent) {
    builder.setUserAgent(userAgent);
    return this;
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder setProxy(final String scheme, final String hostname, final int port) {
    builder.setProxy(scheme, hostname, port);
    return this;
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder
      setProxy(final String scheme, final String hostname, final int port, String username, String password) {
    builder.setProxy(scheme, hostname, port, username, password);
    return this;
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder usePoolingConnection() {
    builder.usePoolingConnection();
    return this;
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder useAlwaysTheSameConnection() {
    builder.useAlwaysTheSameConnection();
    return this;
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder useAlwaysANewConnection() {
    builder.useAlwaysANewConnection();
    return this;
  }

  @Override
  public IHttpRequestExecutorFactory build() {
    return new HttpRequestExecutorFactory(() -> builder.build());
  }

}
