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

import java.io.IOException;

import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class HttpClientConfigurationBuilder {

  enum ConfigMode {
    usePoolingConnection,
    useAlwaysTheSameConnection,
    useAlwaysANewConnection
  }

  final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
  private IHttpProxyConfiguration proxyConfiguration = null;
  private ConfigMode configMode = ConfigMode.useAlwaysANewConnection;
  private String userAgent = null;

  
  public HttpClientConfigurationBuilder() {
    super();
    this.poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
  }

  public HttpClientConfigurationBuilder(IHttpClientConfiguration configuration) {
    this.userAgent = configuration.getUserAgent();
    this.proxyConfiguration = configuration.getProxyConfiguration().get();
    this.configMode = configuration.getConfigMode();
    this.poolingHttpClientConnectionManager = this.configMode == ConfigMode.usePoolingConnection 
        ? (PoolingHttpClientConnectionManager) configuration.getManager()
        : new PoolingHttpClientConnectionManager();
  }

  
  public HttpClientConfigurationBuilder setUserAgent(final String userAgent) {
    this.userAgent = userAgent;
    return this;
  }

  public HttpClientConfigurationBuilder setProxy(final String scheme, final String hostname, final int port) {
    return setProxy(scheme, hostname, port, null, null);
  }

  public HttpClientConfigurationBuilder setProxy(final String scheme, final String hostname, final int port, String username ,String password) {
    Authentication authentication = username == null || password == null 
        ? null
        : new Authentication(username, password);
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
      
      @Override
      public IOptional<IAuthentication, RuntimeException> getAuthentication() {
        return Optional.of(authentication);
      }
    };
    return this;
  }

  public HttpClientConfigurationBuilder usePoolingConnection() {
    configMode = ConfigMode.usePoolingConnection;
    return this;
  }

  public HttpClientConfigurationBuilder useAlwaysTheSameConnection() {
    configMode = ConfigMode.useAlwaysTheSameConnection;
    return this;
  }

  public HttpClientConfigurationBuilder useAlwaysANewConnection() {
    configMode = ConfigMode.useAlwaysANewConnection;
    return this;
  }

  public IHttpClientConfiguration build() {
    return  create();
  }

  private IHttpClientConfiguration create() {
    return switch (configMode) {
      case useAlwaysANewConnection ->
        new IHttpClientConfiguration() {

          @Override
          public ConfigMode getConfigMode() {
            return configMode;
          }

          @Override
          public HttpConnectionMode getMode() {
            return HttpConnectionMode.CLOSE;
          }

          @Override
          public synchronized HttpClientConnectionManager getManager() {
            return new BasicHttpClientConnectionManager();
          }

          @Override
          public IOptional<IHttpProxyConfiguration, RuntimeException> getProxyConfiguration() {
            return Optional.of(HttpClientConfigurationBuilder.this.proxyConfiguration);
          }

          @Override
          public synchronized void close() {
          }

          @Override
          public String getUserAgent() {
            return userAgent;
          }
        };
      case useAlwaysTheSameConnection ->
        new IHttpClientConfiguration() {

          final HttpClientConnectionManager httpClientConnectionManager =
              new PoolingHttpClientConnectionManager();

          @Override
          public ConfigMode getConfigMode() {
            return configMode;
          }

          @Override
          public HttpConnectionMode getMode() {
            return HttpConnectionMode.KEEP_ALIVE;
          }

          @Override
          public IOptional<IHttpProxyConfiguration, RuntimeException> getProxyConfiguration() {
            return Optional.of(HttpClientConfigurationBuilder.this.proxyConfiguration);
          }

          @Override
          public HttpClientConnectionManager getManager() {
            return httpClientConnectionManager;
          }

          @Override
          public void close() {
            try {
              httpClientConnectionManager.close();
            } catch (IOException exception) {
              exception.printStackTrace();
            }
          }

          @Override
          public String getUserAgent() {
            return userAgent;
          }
        };
      case usePoolingConnection ->
        new IHttpClientConfiguration() {

          @Override
          public ConfigMode getConfigMode() {
            return configMode;
          }

          @Override
          public HttpConnectionMode getMode() {
            return HttpConnectionMode.KEEP_ALIVE;
          }

          @Override
          public HttpClientConnectionManager getManager() {
            return poolingHttpClientConnectionManager;
          }

          @Override
          public IOptional<IHttpProxyConfiguration, RuntimeException> getProxyConfiguration() {
            return Optional.of(HttpClientConfigurationBuilder.this.proxyConfiguration);
          }

          @Override
          public void close() {
          }

          @Override
          public String getUserAgent() {
            return userAgent;
          }
        };
    };
  }

}
