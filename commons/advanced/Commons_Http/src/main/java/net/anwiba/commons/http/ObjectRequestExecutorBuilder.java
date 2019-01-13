/*
 * #%L
 * *
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ObjectRequestExecutorBuilder<T> implements IObjectRequestExecutorBuilder<T> {

  private final ConvertingHttpRequestExecutorBuilder builder = new ConvertingHttpRequestExecutorBuilder();
  private final List<IApplicableHttpResponseExceptionFactory> applicableHttpResponseExceptionFactories = new ArrayList<>();
  private final List<IApplicableResultProducer<T>> applicableResultProducers = new ArrayList<>();

  @Override
  public IObjectRequestExecutorBuilder<T> usePoolingConnection() {
    this.builder.usePoolingConnection();
    return this;
  }

  @Override
  public IObjectRequestExecutorBuilder<T> useAlwaysTheSameConnection() {
    this.builder.useAlwaysTheSameConnection();
    return this;
  }

  @Override
  public IObjectRequestExecutorBuilder<T> useAlwaysANewConnection() {
    this.builder.useAlwaysANewConnection();
    return this;
  }

  @Override
  public IObjectRequestExecutorBuilder<T> setResultProducer(final IResultProducer<T> resultProducer) {
    this.applicableResultProducers.add(new IApplicableResultProducer<T>() {

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
    });
    return this;
  }

  @Override
  public IObjectRequestExecutorBuilder<T> addExceptionFactory(final IApplicableHttpResponseExceptionFactory factory) {
    this.applicableHttpResponseExceptionFactories.add(factory);
    return this;
  }

  @Override
  public IObjectRequestExecutorBuilder<T> addExceptionFactory(
      final IApplicable<String> applicable,
      final IHttpResponseExceptionFactory factory) {
    return addExceptionFactory(new IApplicableHttpResponseExceptionFactory() {

      @Override
      public IOException create(
          final int statusCode,
          final String statusMessage,
          final String contentEncoding,
          final InputStream inputStream)
          throws IOException {
        return factory.create(statusCode, statusMessage, contentEncoding, inputStream);
      }

      @Override
      public boolean isApplicable(final String contentType) {
        return applicable.isApplicable(contentType);
      }
    });
  }

  @SuppressWarnings("resource")
  @Override
  public IObjectRequestExecutor<T> build() {
    final IConvertingHttpRequestExecutor convertingExecutor = this.builder.build();
    final IApplicableHttpResponseExceptionFactory[] exceptionFactories = ObjectRequestExecutorBuilder.this.applicableHttpResponseExceptionFactories
        .stream()
        .toArray(IApplicableHttpResponseExceptionFactory[]::new);
    return new IObjectRequestExecutor<T>() {

      @Override
      public T execute(final ICanceler cancelable, final IRequest request) throws CanceledException, IOException {
        return convertingExecutor.execute(cancelable, request, new IApplicableResultProducer<T>() {

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
            for (final IApplicableResultProducer<T> producer : ObjectRequestExecutorBuilder.this.applicableResultProducers) {
              if (!producer.isApplicable(statusCode, contentType)) {
                continue;
              }
              return producer.execute(canceler, statusCode, statusMessage, contentType, contentEncoding, inputStream);
            }
            final IResultProducer<T> producer = (c, sc, sm, t, e, i) -> null;
            return producer.execute(canceler, statusCode, statusMessage, contentType, contentEncoding, inputStream);
          }

          @Override
          public boolean isApplicable(final int statusCode, final String contentType) {
            for (final IApplicableResultProducer<T> producer : ObjectRequestExecutorBuilder.this.applicableResultProducers) {
              if (producer.isApplicable(statusCode, contentType)) {
                return true;
              }
            }
            return statusCode >= 200 && statusCode < 300;
          }
        }, exceptionFactories);
      }

      @Override
      public void close() throws IOException {
        convertingExecutor.close();
      }
    };
  }

  @Override
  public IObjectRequestExecutorBuilder<T> addResultProducer(
      final IApplicable<String> applicable,
      final IResultProducer<T> resultProducer) {
    this.applicableResultProducers.add(new IApplicableResultProducer<T>() {

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
        return applicable.isApplicable(contentType);
      }
    });
    return this;
  }

  @Override
  public IObjectRequestExecutorBuilder<T> addResultProducer(
      final BiFunction<Integer, String, Boolean> applicable,
      final IResultProducer<T> resultProducer) {
    this.applicableResultProducers.add(new IApplicableResultProducer<T>() {

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
        return applicable.apply(statusCode, contentType);
      }
    });
    return this;
  }
}
