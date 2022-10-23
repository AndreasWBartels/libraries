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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.url.IUrl;
import net.anwiba.commons.reference.url.parser.UrlParser;

@SuppressWarnings("nls")
public class HttpConnectionDescriptionTest {

  @Test
  public void name() throws CreationException {
    final String urlString =
        "http://services.foo.de/index.phtml?REQUEST=GetCapabilities&VERSION=1.1.1&SERVICE=WMS&SERVICE_NAME=bar";
    final IUrl url = new UrlParser().parse(urlString);
    final HttpConnectionDescription connectionDescription = new HttpConnectionDescription(
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
    final String urlString2 = connectionDescription.getUrlString();
    final IUrl url2 = new UrlParser().parse(urlString2);
    final HttpConnectionDescription connectionDescription2 = new HttpConnectionDescription(
        url2.getHostname(),
        url2.getPort(),
        url2.getPathString(),
        url2.getUserName(),
        url2.getPassword(),
        url2.getQuery(),
        Optional
            .of(url2.getScheme())
            .accept(s -> !s.isEmpty())
            .convert(s -> s.get(s.size() - 1))
            .accept(p -> "https".equalsIgnoreCase(p)) //$NON-NLS-1$
            .isAccepted());
    assertEquals(connectionDescription, connectionDescription2);
  }

}
