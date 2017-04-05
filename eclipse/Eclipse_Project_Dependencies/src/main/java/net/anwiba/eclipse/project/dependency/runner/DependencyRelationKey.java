/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.eclipse.project.dependency.object.RelationType;

public class DependencyRelationKey  {

  private final String identifier;
  private final RelationType relationType;
  private int hashCode;

  public DependencyRelationKey(final String identifier, final RelationType relationType) {
    this.identifier = identifier;
    this.relationType = relationType;
    hashCode = ObjectUtilities.hashCode(this.identifier, this.relationType);
  }

  private String getIdentifier() {
    return this.identifier;
  }

  private RelationType getRelationType() {
    return this.relationType;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DependencyRelationKey)) {
      return false;
    }
    final DependencyRelationKey other = (DependencyRelationKey) obj;
    return ObjectUtilities.equals(this.getIdentifier(), other.getIdentifier())
        && ObjectUtilities.equals(this.getRelationType(), other.getRelationType());
  }
}