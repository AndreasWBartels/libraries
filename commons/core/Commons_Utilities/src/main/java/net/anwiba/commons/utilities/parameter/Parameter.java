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
package net.anwiba.commons.utilities.parameter;

public class Parameter implements IParameter {

  private static final long serialVersionUID = -1L;
  private final String name;
  private final String value;

  public Parameter(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.name + "=" + this.value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
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
    final Parameter other = (Parameter) obj;
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
