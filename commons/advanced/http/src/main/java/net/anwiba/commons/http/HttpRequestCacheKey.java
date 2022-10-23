/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.lang.parameter.ParametersBuilder;
import net.anwiba.commons.reference.url.IUrl;
import net.anwiba.commons.reference.url.builder.UrlBuilder;
import net.anwiba.commons.reference.url.parser.UrlParser;
import net.anwiba.commons.reference.utilities.IoUtilities;

import java.io.IOException;
import java.util.Objects;

public class HttpRequestCacheKey {

  private final String url;
  private final String body;

  private HttpRequestCacheKey(final String url, final String body) {
    this.url = url;
    this.body = body;
  }

  public String getUrl() {
    return this.url;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.url);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    HttpRequestCacheKey other = (HttpRequestCacheKey) obj;
    return Objects.equals(this.url, other.url)
        && Objects.equals(this.body, other.body);
  }

  public static IOptional<HttpRequestCacheKey, RuntimeException> of(final IRequest request) {
    try {
      IParameters parameters = request.getParameters();
      ParametersBuilder builder = ParametersBuilder.of(parameters);
      //    Parameter.of("Content-Type", "application/x-www-form-urlencoded");
      String body = getBody(request);
      request.getProperties().getParameter("Authorization").consume(builder::add);
      request.getProperties().getParameter("Content-Type").consume(builder::add);
      String url = createUrl(request.getUriString(), builder.build());
      return Optional.of(new HttpRequestCacheKey(url, body));
    } catch (IOException exception) {
      return Optional.empty();
    }
  }

  private static String getBody(final IRequest request) throws IOException {
    if (Objects.equals(request.getMethodType(), HttpMethodType.POST)) {
      return request.getContent(stream -> {
        return IoUtilities.toString(stream, "UTF-8");
      });
    }
    return null;
  }

  private static String createUrl(final String uriString, final IParameters parameters) {
    try {
      IUrl url = new UrlParser().parse(uriString);
      UrlBuilder builder = new UrlBuilder(url);
      parameters.forEach(p -> builder.addQueryParameter(p));
      return builder.build().toString();
    } catch (CreationException e) {
      return uriString;
    }
  }

}
