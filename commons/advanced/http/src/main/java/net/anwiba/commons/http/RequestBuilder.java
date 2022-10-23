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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpHeaders;

import net.anwiba.commons.cache.resource.ILifeTime;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IClosure;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.parameter.IParameter;
import net.anwiba.commons.lang.parameter.Parameter;
import net.anwiba.commons.lang.parameter.ParametersBuilder;
import net.anwiba.commons.reference.url.IUrl;
import net.anwiba.commons.reference.url.parser.UrlParser;
import net.anwiba.commons.utilities.string.StringUtilities;

public class RequestBuilder {

  private HttpMethodType httpMethodType = HttpMethodType.GET;
  private HttpConnectionMode httpConnectionMode = null;
  private final String urlString;
  private final List<IParameter> queryParameters = new ArrayList<>();
  private final List<IParameter> headerParameters = new ArrayList<>();
  private IClosure<InputStream, IOException> inputStreamClosure;
  private long contentLenght;
  private String contentCharacterSetName;
  private String contentMimeType;
  private IAuthentication authentication;
  private ILifeTime cacheTime;

  public static RequestBuilder get(final String urlString) {
    return new RequestBuilder(urlString).get();
  }

  public static RequestBuilder post(final String urlString) {
    return new RequestBuilder(urlString).post();
  }

  private RequestBuilder(final String urlString) {
    this.urlString = urlString;
  }

  private RequestBuilder post() {
    this.httpMethodType = HttpMethodType.POST;
    return this;
  }

  private RequestBuilder get() {
    this.httpMethodType = HttpMethodType.GET;
    return this;
  }

  public RequestBuilder authentication(final String userName, final String password) {
    if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
      return this;
    }
    this.authentication = new Authentication(userName, password);
    return this;
  }

  public RequestBuilder preemptiveAuthentication(final String userName, final String password) {
    if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
      return this;
    }
    this.authentication = new Authentication(userName, password, Authentication.Mode.PREEMPTIVE);
    return this;
  }

  public RequestBuilder forcedAuthentication(final String userName, final String password) {
    if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
      return this;
    }
    this.authentication = new Authentication(userName, password, Authentication.Mode.FORCED);
    return this;
  }

  public RequestBuilder query(final String key, final String value) {
    this.queryParameters.add(Parameter.of(key, value));
    return this;
  }

  public RequestBuilder query(final IParameter parameter) {
    this.queryParameters.add(parameter);
    return this;
  }

  public RequestBuilder header(final String key, final String value) {
    this.headerParameters.add(Parameter.of(key, value));
    return this;
  }

  public RequestBuilder header(final IParameter parameter) {
    this.headerParameters.add(parameter);
    return this;
  }

  public RequestBuilder connectionClose() {
    this.httpConnectionMode = HttpConnectionMode.CLOSE;
    return this;
  }

  public RequestBuilder connectionKeepAlive() {
    this.httpConnectionMode = HttpConnectionMode.KEEP_ALIVE;
    return this;
  }

  public RequestBuilder content(final byte[] content) {
    this.contentLenght = content.length;
    this.inputStreamClosure = () -> new ByteArrayInputStream(content);
    return this;
  }

  public RequestBuilder contentCharacterSet(@SuppressWarnings("hiding") final String contentCharacterSetName) {
    this.contentCharacterSetName = contentCharacterSetName;
    return this;
  }

  public RequestBuilder contentMimeType(final String mimeType) {
    this.contentMimeType = mimeType;
    return this;
  }

  public RequestBuilder cacheTime(ILifeTime cacheTime) {
    this.cacheTime = cacheTime;
    return this;
  }

  public IRequest build() throws CreationException {
//    final String encoded = this.urlString.replace(" ", "%20"); //$NON-NLS-1$//$NON-NLS-2$
    final IUrl uri = new UrlParser().parse(this.urlString);
    final int port = uri.getPort();
    final String host = uri.getHostname();
    Optional.of(this.httpConnectionMode)
        .consume(
            m -> this.headerParameters.add(
                Parameter.of(
                    HttpHeaders.CONNECTION,
                    HttpConnectionMode.CLOSE.equals(this.httpConnectionMode) ? HeaderElements.CLOSE
                        : HeaderElements.KEEP_ALIVE)));
    return new Request(
        this.httpMethodType,
        uri.getScheme().stream().collect(Collectors.joining(":")),
        host,
        port,
        this.authentication,
        this.urlString,
        new ParametersBuilder().add(this.queryParameters).build(),
        new ParametersBuilder().add(this.headerParameters).build(),
        this.inputStreamClosure,
        this.contentLenght,
        this.contentCharacterSetName,
        this.contentMimeType,
        this.cacheTime);
  }

  public RequestBuilder query(final Iterable<IParameter> parameters) {
    parameters.forEach(p -> query(p));
    return this;
  }
}
