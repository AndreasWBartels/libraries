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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.reference.io.IStreamConnector;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

public final class HttpClientConnector implements IStreamConnector<URI> {

  private static ILogger logger = Logging.getLogger(HttpClientConnector.class.getName());
  private final IHttpRequestExecutorFactory httpRequestExcecutorFactory;

  public HttpClientConnector(final IHttpRequestExecutorFactory httpRequestExcecutorFactory) {
    this.httpRequestExcecutorFactory = httpRequestExcecutorFactory;
  }

  private IHttpRequestExecutor requestExecutor() {
    return this.httpRequestExcecutorFactory.create();
  }

  @SuppressWarnings("nls")
  @Override
  public boolean exist(final URI uri) {
    try (final IResponse response = response(uri);) {
      if (StatusCodes.isSuccessful(response.getStatusCode())) {
        return true;
      }
      logger.log(
          ILevel.WARNING,
          "connect to '" + uri.toString() + "' faild " + response.getStatusCode() + " " + response.getStatusText());
      if (logger.isLoggable(ILevel.DEBUG)) {
        logger.log(ILevel.DEBUG, response.getBody());
      }
      return false;
    } catch (CanceledException | IOException exception) {
      logger.log(ILevel.WARNING, "connect to '" + uri.toString() + "' faild " + exception.getMessage());
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return false;
    }
  }

  private IResponse response(final URI uri) throws CanceledException, IOException {
    IResponse response = null;
    try {
      IRequest request = request(uri);
      response = requestExecutor().execute(ICanceler.DummyCanceler, request);
      return response;
    } catch (CreationException exception) {
      IOException closeException = IoUtilities.close(response, null);
      final IOException wrappingException = new IOException(exception.getMessage(), exception);
      if (closeException != null) {
        wrappingException.addSuppressed(wrappingException);
      }
      throw wrappingException;
    }
  }

  private IRequest request(final URI uri) throws CreationException {
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

  @Override
  public InputStream openInputStream(final URI uri) throws IOException {
    return openInputStream(uri, s -> true);
  }

  @SuppressWarnings({ "nls", "resource" })
  @Override
  public InputStream openInputStream(final URI uri, final IAcceptor<String> contentTypeAcceptor) throws IOException {
    try {
      final IResponse response = response(uri);
      final int statusCode = response.getStatusCode();
      final String contentType = response.getContentType();
      if (StatusCodes.isSuccessful(statusCode)) {
        if (contentTypeAcceptor.accept(contentType)) {
          return new FilterInputStream(response.getInputStream()) {

            @Override
            public void close() throws IOException {
              response.close();
            }
          };
        }
      }
      try {
        if (StatusCodes.isSuccessful(statusCode)) {
          logger.log(
              ILevel.WARNING,
              "connect to '" + uri.toString() + "' status"
                  + " " + statusCode
                  + " " + response.getStatusText());
        } else {
          logger.log(
              ILevel.WARNING,
              "connect to '" + uri.toString() + "' faild"
                  + " " + statusCode
                  + " " + response.getStatusText());
        }
        if (!contentTypeAcceptor.accept(contentType)) {
          logger.log(
              ILevel.WARNING,
              "unexpected content type  '" + contentType + "'"
                  + " " + statusCode
                  + " " + response.getStatusText());
        }
        if (logger.isLoggable(ILevel.DEBUG)) {
          final String body = response.getBody();
          logger.log(ILevel.DEBUG, body);
          throw HttpRequestException
              .create(statusCode + " - " + response.getStatusText(), response, body.getBytes());
        }
        throw HttpRequestException.create(statusCode + " - " + response.getStatusText(), response);
      } finally {
        response.close();
      }
    } catch (CanceledException | IOException exception) {
      logger.log(ILevel.DEBUG, "connect to '" + uri.toString() + "' faild " + exception.getMessage());
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
      if (StatusCodes.isSuccessful(response.getStatusCode())) {
        return response.getContentLength();
      }
      return -1;
    } catch (CanceledException | IOException exception) {
      return -1;
    }
  }

  @Override
  public String getContentType(final URI uri) throws IOException {
    try (final IResponse response = response(uri);) {
      if (StatusCodes.isSuccessful(response.getStatusCode())) {
        return response.getContentType();
      }
      return null;
    } catch (CanceledException | IOException exception) {
      return null;
    }
  }
}