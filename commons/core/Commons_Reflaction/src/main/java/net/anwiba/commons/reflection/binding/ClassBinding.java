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
package net.anwiba.commons.reflection.binding;

import net.anwiba.commons.reflection.IBinding;

public class ClassBinding<T> implements IBinding<T> {

  private final Class<T> boundedClass;

  public ClassBinding(final Class<T> clazz) {
    this.boundedClass = clazz;
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
    final ClassBinding other = (ClassBinding) obj;
    if (this.boundedClass == null) {
      if (other.boundedClass != null) {
        return false;
      }
    } else if (!this.boundedClass.equals(other.boundedClass)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return this.boundedClass.toString();
  }

}
