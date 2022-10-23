/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.object;

import net.anwiba.eclipse.project.dependency.java.IItem;

public class DependencyRelation implements IDependencyRelation {

  private final IItem item;
  private final RelationType relation;

  public DependencyRelation(final IItem item, final RelationType relation) {
    this.item = item;
    this.relation = relation;
  }

  @Override
  public IItem getItem() {
    return this.item;
  }

  @Override
  public RelationType getRelationType() {
    return this.relation;
  }

}
