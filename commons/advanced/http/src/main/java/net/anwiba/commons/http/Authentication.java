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
package net.anwiba.commons.http;

import java.util.Objects;

public final class Authentication implements IAuthentication {
  
  public enum Mode{
    LAZY,
    PREEMPTIVE,
    FORCED
  }
  
  private final String userName;
  private final String password;
  private final Mode mode;

  public Authentication(String userName, String password) {
    this(userName, password, Mode.LAZY);
  }

  public Authentication(String userName, String password, Mode mode) {
    this.userName = userName;
    this.password = password;
    this.mode = mode;
  }

  @Override
  public String getUsername() {
    return this.userName;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public boolean isLazy() {
    return Objects.equals(Mode.LAZY, mode);
  }
  
  @Override
  public boolean isPreemptive() {
    return Objects.equals(Mode.PREEMPTIVE, mode);
  }

  @Override
  public boolean isForces() {
    return Objects.equals(Mode.FORCED, mode);
  }

  @Override
  public String toString() {
    return this.userName + ";" + password;
  }
}
