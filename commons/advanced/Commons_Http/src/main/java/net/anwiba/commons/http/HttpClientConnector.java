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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.process.cancel.ICanceler;
import net.anwiba.commons.resource.reference.IStreamConnector;

public final class HttpClientConnector implements IStreamConnector<URI> {

  private static ILogger logger = Logging.getLogger(HttpClientConnector.class.getName());
  private final IHttpRequestExcecutorFactory httpRequestExcecutorFactory;

  public HttpClientConnector(final IHttpRequestExcecutorFactory httpRequestExcecutorFactory) {
    this.httpRequestExcecutorFactory = httpRequestExcecutorFactory;
  }

  private IHttpRequestExecutor requestExecutor() {
    return this.httpRequestExcecutorFactory.create();
  }

  @SuppressWarnings("nls")
  @Override
  public boolean exist(final URI uri) {
    try (final IResponse response = response(uri);) {
      if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
        return true;
      }
      logger.log(
          ILevel.WARNING,
          "connect to " + uri.toString() + "faild " + response.getStatusCode() + " " + response.getStatusText());
      if (logger.isLoggable(ILevel.DEBUG)) {
        logger.log(ILevel.DEBUG, response.getBody());
      }
      return false;
    } catch (InterruptedException | IOException exception) {
      logger.log(ILevel.WARNING, "connect to " + uri.toString() + "faild " + exception.getMessage());
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return false;
    }
  }

  private IResponse response(final URI uri) throws InterruptedException, IOException {
    final IRequest request = request(uri);
    return requestExecutor().execute(ICanceler.DummyCancler, request);
  }

  private IRequest request(final URI uri) {
    return RequestBuilder.get(uri.toString()).build();
  }

  @Override
  public boolean canRead(final URI uri) {
    try (InputStream inputStream = openInputStream(uri)) {
      return true;
    } catch (final IOException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return false;
    }
  }

  @Override
  public boolean canWrite(final URI uri) {
    return false;
  }

  @SuppressWarnings({ "nls", "resource" })
  @Override
  public InputStream openInputStream(final URI uri) throws IOException {
    try {
      final IResponse response = response(uri);
      if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
        return response.getInputStream();
      }
      logger.log(
          ILevel.DEBUG,
          "connect to " + uri.toString() + "faild " + response.getStatusCode() + " " + response.getStatusText());
      if (logger.isLoggable(ILevel.DEBUG)) {
        logger.log(ILevel.DEBUG, response.getBody());
      }
      throw new IOException(response.getStatusText());
    } catch (InterruptedException | IOException exception) {
      logger.log(ILevel.DEBUG, "connect to " + uri.toString() + "faild " + exception.getMessage());
      throw new IOException(exception);
    }
  }

  @Override
  public OutputStream openOutputStream(final URI uri) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getContentLength(final URI uri) throws IOException {
    try (final IResponse response = response(uri);) {
      final int statusCode = response.getStatusCode();
      if (200 <= statusCode && statusCode < 300) {
        return response.getContentLength();
      }
      return -1;
    } catch (InterruptedException | IOException exception) {
      return -1;
    }
  }
}