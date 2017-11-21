/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.jdbc.software;

public class JdbcPattern implements IJdbcPattern {

  private final String identifier;
  private final String pattern;

  public JdbcPattern(final String identifier, final String pattern) {
    super();
    this.identifier = identifier;
    this.pattern = pattern;
  }

  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  @Override
  public String getPattern() {
    return this.pattern;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
    result = prime * result + ((this.pattern == null) ? 0 : this.pattern.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final JdbcPattern other = (JdbcPattern) obj;
    if (this.identifier == null) {
      if (other.identifier != null) {
        return false;
      }
    } else if (!this.identifier.equals(other.identifier)) {
      return false;
    }
    if (this.pattern == null) {
      if (other.pattern != null) {
        return false;
      }
    } else if (!this.pattern.equals(other.pattern)) {
      return false;
    }
    return true;
  }

}
