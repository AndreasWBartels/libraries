// Copyright (c) 2016 by Andreas W. Bartels 

package net.anwiba.commons.http;

import java.io.IOException;
import java.io.InputStream;

import net.anwiba.commons.resource.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

public final class ExceptionProducer implements IResultProducer<IOException> {

  private final IApplicableHttpResponseExceptionFactory[] exceptionFactories;

  public ExceptionProducer(final IApplicableHttpResponseExceptionFactory... exceptionFactories) {
    this.exceptionFactories = exceptionFactories;
  }

  @Override
  public IOException execute(
      final ICanceler canceler,
      final int statusCode,
      final String statusMessage,
      final String contentType,
      final String contentEncoding,
      final InputStream inputStream) throws IOException, InterruptedException {
    for (final IApplicableHttpResponseExceptionFactory exceptionFactory : this.exceptionFactories) {
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
