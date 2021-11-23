/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.datasource.constaint;

import java.util.Collections;
import java.util.List;

public class License implements ILicense {

  private final String identifier;
  private final String name;
  private final String reference;
  private List<String> attributes;

  public License(final String identifier, final String name, final String reference, List<String> attributes) {
    super();
    this.identifier = identifier;
    this.name = name;
    this.reference = reference;
    this.attributes = Collections.unmodifiableList(attributes);
  }

  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getReference() {
    return this.reference;
  }

  @Override
  public List<String> getAttributes() {
    return attributes;
  }

  public static ILicense of(final String identifier, final String name, final String reference, List<String> attributes) {
    return new License(identifier, name, reference, attributes);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
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
    final License other = (License) obj;
    if (this.identifier == null) {
      if (other.identifier != null) {
        return false;
      }
    } else if (!this.identifier.equals(other.identifier)) {
      return false;
    }
    return true;
  }

}
