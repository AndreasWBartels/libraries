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
package net.anwiba.commons.utilities.io.url;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.parameter.IParameter;

public class UrlBuilder {

  List<String> scheme = new ArrayList<>();
  private String username;
  private String password;
  private String hostName;
  private Integer port;
  List<IParameter> parameters = new ArrayList<>();
  List<String> path = new ArrayList<>();
  private final String fragment;

  public UrlBuilder(final IUrl url) {
    this.scheme.addAll(url.getScheme());
    final IAuthentication authentication = url.getAuthentication();
    this.username = Optional.of(authentication).convert(a -> a.getUsername()).get();
    this.password = Optional.of(authentication).convert(a -> a.getPassword()).get();
    final IHost host = url.getHost();
    this.hostName = Optional.of(host).convert(a -> a.getName()).get();
    this.port = Optional.of(host).convert(a -> a.getPort()).get();
    url.getQuery().forEach(p -> this.parameters.add(p));
    this.path.addAll(url.getPath());
    this.fragment = url.getFragment();
  }

  public IUrl build() {
    final IAuthority authority = new AuthorityBuilder() //
        .setUsername(this.username)
        .setPassword(this.password)
        .setHostName(this.hostName)
        .setPort(this.port)
        .build();

    return new Url(this.scheme, authority, this.path, this.parameters, this.fragment);
  }

  public UrlBuilder setUsername(final String username) {
    this.username = username;
    return this;
  }

  public UrlBuilder setPassword(final String password) {
    this.password = password;
    return this;
  }

  public UrlBuilder setHostName(final String hostName) {
    this.hostName = hostName;
    return this;
  }

  public UrlBuilder setPort(final Integer port) {
    this.port = port;
    return this;
  }

}
