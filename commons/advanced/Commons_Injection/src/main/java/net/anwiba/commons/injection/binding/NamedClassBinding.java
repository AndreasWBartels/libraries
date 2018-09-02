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
package net.anwiba.commons.injection.binding;

import net.anwiba.commons.injection.IBinding;

public class NamedClassBinding<T> implements IBinding<T> {

  private final Class<T> boundedClass;
  private final String name;

  public NamedClassBinding(final Class<T> clazz, final String name) {
    this.boundedClass = clazz;
    this.name = name;
  }

  @Override
  public Class<T> getBoundedClass() {
    return this.boundedClass;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.boundedClass == null) ? 0 : this.boundedClass.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
    @SuppressWarnings("rawtypes")
    final NamedClassBinding other = (NamedClassBinding) obj;
    if (this.boundedClass == null) {
      if (other.boundedClass != null) {
        return false;
      }
    } else if (!this.boundedClass.equals(other.boundedClass)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @SuppressWarnings("nls")
  @Override
  public String toString() {
    return this.boundedClass.getName() + "[" + this.name + "]";
  }

}
