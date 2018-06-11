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

import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClients;

public class HttpClientFactory implements IHttpClientFactory {

  private final IHttpClientConfiguration configuration;

  public HttpClientFactory(final IHttpClientConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public CloseableHttpClient create() {
    switch (this.configuration.getMode()) {
      case CLOSE: {
        return HttpClients
            .custom()
            .setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
            .setConnectionManager(this.configuration.getManager())
            .build();
      }
      case KEEP_ALIVE: {
        return HttpClients
            .custom()
            .setConnectionReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE)
            .setConnectionManager(this.configuration.getManager())
            .build();
      }
    }
    return HttpClients.custom().setConnectionManager(this.configuration.getManager()).build();
  }

  @Override
  public IHttpClientConfiguration getClientConfiguration() {
    return this.configuration;
  }

}
