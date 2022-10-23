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

import net.anwiba.commons.cache.resource.ILifeTime;
import net.anwiba.commons.lang.functional.IClosure;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.parameter.IParameters;

public final class Request implements IRequest {

  private final HttpMethodType methodType;
  private final String urlString;
  private final IParameters parameters;
  private final IClosure<InputStream, IOException> inputStreamClosure;
  private final long contentLength;
  private final String charsetName;
  private final String mimeType;
  private final IParameters properties;
  private final IAuthentication authentication;
  private final String host;
  private final int port;
  private final String protocoll;
  private ILifeTime cacheTime;

  Request(
      final HttpMethodType methodType,
      final String protocoll,
      final String host,
      final int port,
      final IAuthentication authentication,
      final String urlString,
      final IParameters parameters,
      final IParameters properties,
      final IClosure<InputStream, IOException> inputStreamClosure,
      final long contentLength,
      final String charsetName,
      final String mimeType,
      final ILifeTime cacheTime) {
    super();
    this.methodType = methodType;
    this.protocoll = protocoll;
    this.host = host;
    this.port = port;
    this.urlString = urlString;
    this.authentication = authentication;
    this.parameters = parameters;
    this.properties = properties;
    this.inputStreamClosure = inputStreamClosure;
    this.contentLength = contentLength;
    this.charsetName = charsetName;
    this.mimeType = mimeType;
    this.cacheTime = cacheTime;
  }

  @Override
  public String getUriString() {
    return this.urlString;
  }

  @Override
  public IAuthentication getAuthentication() {
    return this.authentication;
  }

  @Override
  public HttpMethodType getMethodType() {
    return this.methodType;
  }

  @Override
  public IParameters getParameters() {
    return this.parameters;
  }

  @Override
  public long getContentLength() {
    return this.contentLength;
  }

  @Override
  public IClosure<InputStream, IOException> getContent() {
    return this.inputStreamClosure;
  }

  @Override
  public String getContentMimeType() {
    return this.mimeType;
  }

  @Override
  public String getContentCharset() {
    return this.charsetName;
  }

  @Override
  public IParameters getProperties() {
    return this.properties;
  }

  @Override
  public String getHost() {
    return this.host;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  public IOptional<ILifeTime, RuntimeException> getCacheTime() {
    return Optional.of(cacheTime);
  }
}