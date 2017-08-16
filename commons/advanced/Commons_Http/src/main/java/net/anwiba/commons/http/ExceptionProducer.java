// Copyright (c) 2016 by Andreas W. Bartels (bartels@anwiba.de)

package net.anwiba.commons.http;

import java.io.IOException;
import java.io.InputStream;

import net.anwiba.commons.resource.utilities.IoUtilities;

public final class ExceptionProducer implements IResultProducer<IOException> {

  private final IHttpResponseExceptionFactory[] exceptionFactories;

  public ExceptionProducer(final IHttpResponseExceptionFactory... exceptionFactories) {
    this.exceptionFactories = exceptionFactories;
  }

  @Override
  public IOException execute(
      final InputStream inputStream,
      final int statusCode,
      final String statusMessage,
      final String contentType,
      final String contentEncoding) throws IOException, InterruptedException {
    for (final IHttpResponseExceptionFactory exceptionFactory : this.exceptionFactories) {
      if (exceptionFactory.isApplicable(contentType)) {
        return exceptionFactory.create(statusCode, statusMessage, contentEncoding, inputStream);
      }
    }
    final byte[] array = IoUtilities.toByteArray(inputStream);
    throw new HttpRequestException(
        "Unexpected response content type '" + contentType + "'", // //$NON-NLS-1$ //$NON-NLS-2$
        statusCode,
        statusMessage,
        array,
        contentType,
        contentEncoding);
  }
}
