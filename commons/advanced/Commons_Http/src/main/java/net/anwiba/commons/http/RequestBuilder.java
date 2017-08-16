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

import net.anwiba.commons.lang.functional.IClosure;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.Parameter;
import net.anwiba.commons.utilities.parameter.Parameters;

public class RequestBuilder {

  private HttpMethodType httpMethodType = HttpMethodType.GET;
  private final String urlString;
  private final List<IParameter> queryParameters = new ArrayList<>();
  IClosure<InputStream, IOException> inputStreamClosure;
  private long contentLenght;
  private String encoding;
  private String mimeType;
  private String userAgent;

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

  public IRequest build() {
    return new Request(
        this.httpMethodType,
        this.urlString,
        new Parameters(this.queryParameters),
        this.userAgent,
        this.inputStreamClosure,
        this.contentLenght,
        this.encoding,
        this.mimeType);
  }

  public RequestBuilder query(final String key, final String value) {
    this.queryParameters.add(new Parameter(key, value));
    return this;
  }

  public RequestBuilder query(final IParameter parameter) {
    this.queryParameters.add(parameter);
    return this;
  }

  public RequestBuilder content(final byte[] content) {
    this.contentLenght = content.length;
    this.inputStreamClosure = () -> new ByteArrayInputStream(content);
    return this;
  }

  public RequestBuilder contentEncoding(final String encoding) {
    this.encoding = encoding;
    return this;
  }

  public RequestBuilder mimeType(final String mimeTye) {
    this.mimeType = mimeTye;
    return this;
  }

  public RequestBuilder userAgent(final String userAgent) {
    this.userAgent = userAgent;
    return this;
  }

}
