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
import java.io.InputStream;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.thread.cancel.ICanceler;

public interface IConvertingHttpRequestExecutor extends AutoCloseable {

  <T> T execute(
      ICanceler cancelable,
      IRequest request,
      IApplicableResultProducer<T> resultProducer,
      IApplicableHttpResponseExceptionFactory... exceptionFactories)
      throws CanceledException,
      HttpServerException,
      HttpRequestException,
      IOException;

  <T> T execute(
      ICanceler cancelable,
      IRequest request,
      IApplicableResultProducer<T> resultProducer,
      IResultProducer<IOException> errorProducer)
      throws CanceledException,
      HttpServerException,
      HttpRequestException,
      IOException;

  default <T> T execute(
      final ICanceler cancelable,
      final IRequest request,
      final IResultProducer<T> resultProducer,
      final IResultProducer<IOException> errorProducer)
      throws CanceledException,
      HttpServerException,
      HttpRequestException,
      IOException {
    return execute(cancelable, request, new IApplicableResultProducer<T>() {

      @Override
      public T execute(
          final ICanceler canceler,
          final int statusCode,
          final String statusMessage,
          final String contentType,
          final String contentEncoding,
          final InputStream inputStream)
          throws IOException,
          CanceledException {
        return resultProducer.execute(canceler, statusCode, statusMessage, contentType, contentEncoding, inputStream);
      }

      @Override
      public boolean isApplicable(final int statusCode, final String contentType) {
        return statusCode >= 200 && statusCode < 300;
      }
    }, errorProducer);
  }

  @Override
  void close() throws IOException;
}
