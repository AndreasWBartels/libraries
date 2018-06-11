/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import net.anwiba.commons.reference.utilities.IoUtilities;
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
