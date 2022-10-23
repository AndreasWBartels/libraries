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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.reference.url;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.lang.stream.Streams;

public interface IUrl {

  String getFragment();

  IParameters getQuery();

  List<String> getPath();

  default List<String> getPathNames() {
    return Streams.of(getPath())
        .convert(n -> n.startsWith("/") ? n.substring(1) : n)
        .filter(n -> !n.isEmpty())
        .asList();
  }

  IHost getHost();

  IAuthentication getAuthentication();

  List<String> getScheme();

  int getPort();

  String getHostname();

  String getPathString();

  String getUserName();

  String getPassword();

  String encoded();

  String decoded();

  default URL toURL() throws MalformedURLException {
    String protocol = String.join(":", getScheme());
    String host = getHostname();
    int port = getPort();
    String file = getPathString();
    return new URL(encoded());
  }

  default URI toURI() throws URISyntaxException {
    String scheme = String.join(":", getScheme());
    String userInfo = ""; 
    String host = getHostname();
    int port = getPort();
    String path = getPathString(); 
    String query = ""; 
    String fragment = getFragment();
    return URI.create(encoded());
  }

}
