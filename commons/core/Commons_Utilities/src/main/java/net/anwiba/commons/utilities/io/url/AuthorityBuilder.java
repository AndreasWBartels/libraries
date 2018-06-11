/*
 * #%L
 * *
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

public class AuthorityBuilder {

  private String username;
  private String password;
  private String hostName;
  private Integer port;

  public IAuthority build() {
    final IAuthentication authentication = new AuthenticationBuilder() //
        .setUsername(this.username)
        .setPassword(this.password)
        .build();
    final IHost host = new HostBuilder() //
        .setHostName(this.hostName)
        .setPort(this.port)
        .build();
    return new Authority(authentication, host);
  }

  public AuthorityBuilder setUsername(final String username) {
    this.username = username;
    return this;
  }

  public AuthorityBuilder setPassword(final String password) {
    this.password = password;
    return this;
  }

  public AuthorityBuilder setHostName(final String hostName) {
    this.hostName = hostName;
    return this;
  }

  public AuthorityBuilder setPort(final Integer port) {
    this.port = port;
    return this;
  }

}
