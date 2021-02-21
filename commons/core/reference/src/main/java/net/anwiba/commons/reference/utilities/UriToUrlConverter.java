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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class UriToUrlConverter {

  @SuppressWarnings({ "unused", "nls" })
  public URL convert(final URI uri) throws MalformedURLException {
    if (uri == null) {
      return null;
    }
    try {
      return uri.toURL();
    } catch (final Exception exception) {
      final String scheme = uri.getScheme();
      final String schemeSpecificPart = uri.getSchemeSpecificPart();
      final String authority = uri.getAuthority();
      final String userInfo = uri.getUserInfo();
      final String host = uri.getHost();
      final int port = uri.getPort();
      final String path = uri.getPath();
      final String query = uri.getQuery();
      final String fragment = uri.getFragment();
      if (scheme == null || scheme.equalsIgnoreCase("file")) {
        return new URL("file", null, port, schemeSpecificPart);
      }
      return new URL(scheme, host, port, path);
    }
  }

}
