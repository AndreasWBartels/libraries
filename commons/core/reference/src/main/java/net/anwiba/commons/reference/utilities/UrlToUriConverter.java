/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.reference.utilities;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlToUriConverter {

  @SuppressWarnings({ "nls", "unused" })
  public URI convert(final URL url) throws URISyntaxException {
    if (url == null) {
      return null;
    }
    try {
      return url.toURI();
    } catch (final Exception exception) {
      final String protocol = url.getProtocol();
      if (protocol.equalsIgnoreCase("file")) {
        final String file = url.getFile();
        return new URI(protocol, file, null);
      }
      final String userInfo = url.getUserInfo();
      final String host = url.getHost();
      final int port = url.getPort();
      final String path = url.getPath();
      final String query = url.getQuery();
      final String ref = url.getRef();
      return new URI(protocol, userInfo, host, port, path, query, null);
    }
  }
}
