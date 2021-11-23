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

import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class HttpRequestExecutorFactoryBuilder implements IHttpRequestExecutorFactoryBuilder {

  private IHttpClientConfiguration configuration = new IHttpClientConfiguration() {

    @Override
    public HttpConnectionMode getMode() {
      return HttpConnectionMode.CLOSE;
    }

    @Override
    public HttpClientConnectionManager getManager() {
      return new BasicHttpClientConnectionManager();
    }
  };

  @Override
  public IHttpRequestExecutorFactoryBuilder setProxy(final String scheme, final String hostname, final int port) {
    this.proxyConfiguration = new IHttpProxyConfiguration() {

      @Override
      public int getPort() {
        return port;
      }

      @Override
      public String getHost() {
        return hostname;
      }

      @Override
      public String getScheme() {
        return scheme;
      }
    };
    return this;
  }

  final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
      new PoolingHttpClientConnectionManager();
  private IHttpProxyConfiguration proxyConfiguration = null;

  public HttpRequestExecutorFactoryBuilder() {
    super();
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder usePoolingConnection() {
    this.configuration = new IHttpClientConfiguration() {

      @Override
      public HttpConnectionMode getMode() {
        return HttpConnectionMode.KEEP_ALIVE;
      }

      @Override
      public HttpClientConnectionManager getManager() {
        return HttpRequestExecutorFactoryBuilder.this.poolingHttpClientConnectionManager;
      }
    };
    return this;
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder useAlwaysTheSameConnection() {
    this.configuration = new IHttpClientConfiguration() {

      final BasicHttpClientConnectionManager basicHttpClientConnectionManager = new BasicHttpClientConnectionManager();

      @Override
      public HttpConnectionMode getMode() {
        return HttpConnectionMode.KEEP_ALIVE;
      }

      @Override
      public HttpClientConnectionManager getManager() {
        return this.basicHttpClientConnectionManager;
      }
    };
    return this;
  }

  @Override
  public IHttpRequestExecutorFactoryBuilder useAlwaysANewConnection() {
    this.configuration = new IHttpClientConfiguration() {

      @Override
      public HttpConnectionMode getMode() {
        return HttpConnectionMode.CLOSE;
      }

      @Override
      public HttpClientConnectionManager getManager() {
        return new BasicHttpClientConnectionManager();
      }
    };
    return this;
  }

  @Override
  public IHttpRequestExecutorFactory build() {
    return new HttpRequestExecutorFactory(new IHttpClientConfiguration() {

      @Override
      public HttpConnectionMode getMode() {
        return HttpRequestExecutorFactoryBuilder.this.configuration.getMode();
      }

      @Override
      public HttpClientConnectionManager getManager() {
        return HttpRequestExecutorFactoryBuilder.this.configuration.getManager();
      }

      @Override
      public IOptional<IHttpProxyConfiguration, RuntimeException> getProxyConfiguration() {
        return Optional.of(HttpRequestExecutorFactoryBuilder.this.proxyConfiguration);
      }
    });
  }

}
