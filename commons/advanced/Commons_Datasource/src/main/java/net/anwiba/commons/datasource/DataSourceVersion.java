/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.datasource;

import java.io.Serializable;

public class DataSourceVersion implements Serializable {
  private static final long serialVersionUID = 8597676323085050679L;
  private final int major;
  private final int minor;
  private final String description;

  public DataSourceVersion(final int major, final int minor, final String description) {
    this.major = major;
    this.minor = minor;
    this.description = description;
  }

  public int getMajor() {
    return this.major;
  }

  public int getMinor() {
    return this.minor;
  }

  @Override
  public String toString() {
    if (this.description == null) {
      return String.valueOf(this.major) + "." + String.valueOf(this.minor); //$NON-NLS-1$
    }
    if (this.description.indexOf("\n") == -1) { //$NON-NLS-1$
      return this.description;
    }
    return this.description.substring(0, this.description.indexOf("\n")); //$NON-NLS-1$
  }
}
