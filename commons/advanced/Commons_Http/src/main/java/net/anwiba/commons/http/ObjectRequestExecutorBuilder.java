/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
import java.util.Optional;

import net.anwiba.commons.lang.functional.IApplicable;

public class ObjectRequestExecutorBuilder<T> implements IObjectRequestExecutorBuilder<T> {

  private final ConvertingHttpRequestExecutorBuilder builder = new ConvertingHttpRequestExecutorBuilder();
  private final List<IApplicableHttpResponseExceptionFactory> applicableHttpResponseExceptionFactories = new ArrayList<>();
  private IResultProducer<T> resultProducer = null;

  @Override
  public IObjectRequestExecutorBuilder<T> setConnectionManagerProvider(
      final IHttpClientConnectionManagerProvider connectionManagerProvider) {
    this.builder.setConnectionManagerProvider(connectionManagerProvider);
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
    this.resultProducer = resultProducer;
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
          final InputStream inputStream) throws IOException {
        return factory.create(statusCode, statusMessage, contentEncoding, inputStream);
      }

      @Override
      public boolean isApplicable(final String contentType) {
        return applicable.isApplicable(contentType);
      }
    });
  }

  @Override
  public IObjectRequestExecutor<T> build() {
    final IConvertingHttpRequestExecutor convertingExecutor = this.builder.build();
    final IResultProducer<T> producer = Optional.ofNullable(this.resultProducer).orElseGet(
        () -> (canceler, statusCode, statusMessage, contentType, contentEncoding, inputStream) -> null);
    final IApplicableHttpResponseExceptionFactory[] exceptionFactories = ObjectRequestExecutorBuilder.this.applicableHttpResponseExceptionFactories
        .stream()
        .toArray(IApplicableHttpResponseExceptionFactory[]::new);
    return (cancelable, request) -> convertingExecutor.execute(cancelable, request, producer, exceptionFactories);
  }

}
