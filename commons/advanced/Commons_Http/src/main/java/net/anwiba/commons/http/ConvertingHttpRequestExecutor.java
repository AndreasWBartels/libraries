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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import net.anwiba.commons.lang.io.NoneClosingInputStream;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ConvertingHttpRequestExecutor implements IConvertingHttpRequestExecutor {

  private final IHttpRequestExecutor httpRequestExecutor;

  public ConvertingHttpRequestExecutor(final IHttpRequestExecutor httpRequestExecutor) {
    this.httpRequestExecutor = httpRequestExecutor;
  }

  @Override
  public <T> T execute(
      final ICanceler cancelable,
      final IRequest request,
      final IApplicableResultProducer<T> resultProducer,
      final IApplicableHttpResponseExceptionFactory... exceptionFactories)
      throws InterruptedException,
      HttpServerException,
      HttpRequestException,
      IOException {
    return execute(cancelable, request, resultProducer, new ExceptionProducer(exceptionFactories));
  }

  @Override
  public <T> T execute(
      final ICanceler cancelable,
      final IRequest request,
      final IApplicableResultProducer<T> resultProducer,
      final IResultProducer<IOException> errorProducer)
      throws InterruptedException,
      HttpServerException,
      HttpRequestException,
      IOException {

    try {
      try (final IResponse response = this.httpRequestExecutor.execute(cancelable, request)) {
        final int statusCode = response.getStatusCode();
        final String statusText = response.getStatusText();
        final long contentLength = response.getContentLength();
        if (contentLength == 0) {
          throw new HttpServerException("Http request faild, empty response", statusCode, statusText); //$NON-NLS-1$
        }
        final String contentType = response.getContentType();
        if (resultProducer.isApplicable(statusCode, contentType)) {
          try (InputStream stream = response.getInputStream()) {
            try (InputStream inputStream = new BufferedInputStream(new NoneClosingInputStream(stream))) {
              try {
                inputStream.mark(IoUtilities.maximumLimitOfBytes(contentLength));
                return resultProducer.execute(
                    cancelable,
                    statusCode,
                    statusText,
                    contentType,
                    response.getContentEncoding(),
                    inputStream);
              } catch (final IOException exception) {
                inputStream.reset();
                throw new HttpRequestException(
                    "Unexpected response content type '" + contentType + "'", // //$NON-NLS-1$ //$NON-NLS-2$
                    statusCode,
                    statusText,
                    IoUtilities.toByteArray(inputStream),
                    contentType,
                    response.getContentEncoding(),
                    exception);
              }
            }
          }
        }
        try (InputStream inputStream = response.getInputStream()) {
          throw errorProducer
              .execute(cancelable, statusCode, statusText, contentType, response.getContentEncoding(), inputStream);
        }
      }
    } catch (final InterruptedIOException exception) {
      final InterruptedException interruptedException = new InterruptedException();
      interruptedException.initCause(exception);
      throw interruptedException;
    }
  }

  @Override
  public void close() throws IOException {
    this.httpRequestExecutor.close();
  }
}
