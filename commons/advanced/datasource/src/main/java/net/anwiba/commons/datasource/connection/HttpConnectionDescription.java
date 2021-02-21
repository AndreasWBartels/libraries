/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.datasource.connection;

import java.net.URI;
import java.net.URL;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.utilities.io.url.IAuthentication;
import net.anwiba.commons.utilities.io.url.IUrl;
import net.anwiba.commons.utilities.io.url.parser.UrlParser;
import net.anwiba.commons.utilities.parameter.IParameters;

public class HttpConnectionDescription extends AbstractHttpConnectionDescription implements
    IResourceReferenceConnectionDescription {

  private static final long serialVersionUID = 1L;

  public HttpConnectionDescription(
    final String host,
    final int port,
    final String path,
    final String userName,
    final String password,
    final IParameters parameters,
    final boolean sslEnabled) {
    super(host, port, path, userName, password, parameters, sslEnabled);
  }

  @Override
  public HttpConnectionDescription adapt(final IAuthentication authentication) {
    return new HttpConnectionDescription(
        getHost(),
        getPort(),
        getPath(),
        authentication.getUsername(),
        authentication.getPassword(),
        getParameters(),
        isSslEnabled());
  }

  @Override
  public IResourceReference getResourceReference() {
    return new ResourceReferenceFactory().create(getURI());
  }

  @Override
  public String toString() {
    return toString(getFormat());
  }

  @Override
  public String getFormat() {
    return "Web"; //$NON-NLS-1$
  }

  public static IHttpConnectionDescription of(final URI uri) {
    try {
      IUrl url;
      url = new UrlParser().parse(uri.toString());
      return new HttpConnectionDescription(
          url.getHostname(),
          url.getPort(),
          url.getPathString(),
          url.getUserName(),
          url.getPassword(),
          url.getQuery(),
          Optional
              .of(url.getScheme())
              .accept(s -> !s.isEmpty())
              .convert(s -> s.get(s.size() - 1))
              .accept(p -> "https".equalsIgnoreCase(p)) //$NON-NLS-1$
              .isAccepted());
    } catch (CreationException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  public static IHttpConnectionDescription of(final URL uri) {
    try {
      IUrl url;
      url = new UrlParser().parse(uri.toString());
      return new HttpConnectionDescription(
          url.getHostname(),
          url.getPort(),
          url.getPathString(),
          url.getUserName(),
          url.getPassword(),
          url.getQuery(),
          Optional
              .of(url.getScheme())
              .accept(s -> !s.isEmpty())
              .convert(s -> s.get(s.size() - 1))
              .accept(p -> "https".equalsIgnoreCase(p)) //$NON-NLS-1$
              .isAccepted());
    } catch (CreationException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
}
