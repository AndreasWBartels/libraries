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

import net.anwiba.commons.process.cancel.ICanceler;

public interface IConvertingHttpRequestExecutor {

  <T> T execute(
      ICanceler cancelable,
      IRequest request,
      IResultProducer<T> resultProducer,
      IApplicableHttpResponseExceptionFactory... exceptionFactories)
      throws InterruptedException,
      HttpServerException,
      HttpRequestException,
      IOException;

  <T> T execute(
      ICanceler cancelable,
      IRequest request,
      IResultProducer<T> resultProducer,
      IResultProducer<IOException> errorProducer)
      throws InterruptedException,
      HttpServerException,
      HttpRequestException,
      IOException;

}
