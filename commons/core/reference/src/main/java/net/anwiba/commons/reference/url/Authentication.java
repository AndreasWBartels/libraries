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
package net.anwiba.commons.reference.url;

public final class Authentication implements IAuthentication {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
    result = prime * result + ((this.userName == null) ? 0 : this.userName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Authentication other = (Authentication) obj;
    if (this.password == null) {
      if (other.password != null)
        return false;
    } else if (!this.password.equals(other.password))
      return false;
    if (this.userName == null) {
      if (other.userName != null)
        return false;
    } else if (!this.userName.equals(other.userName))
      return false;
    return true;
  }

  private final String userName;
  private final String password;

  public Authentication(String userName, String password) {
    this.userName = userName;
    this.password = password;
  }

  @Override
  public String getUsername() {
    return this.userName;
  }

  @Override
  public String getPassword() {
    return this.password;
  }
} 
