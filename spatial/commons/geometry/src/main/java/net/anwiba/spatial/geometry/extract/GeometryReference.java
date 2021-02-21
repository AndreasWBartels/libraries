/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2010 by Andreas W. Bartels 
package net.anwiba.spatial.geometry.extract;

import net.anwiba.commons.lang.object.ObjectUtilities;

public class GeometryReference extends AbstractReference implements IGeometryReference {

  public GeometryReference(final IPath path) {
    super(path);
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof IGeometryReference)) {
      return false;
    }
    final IGeometryReference other = (IGeometryReference) obj;
    return ObjectUtilities.equals(other.getPath(), getPath());
  }

  @Override
  public int hashCode() {
    if (this.path == null) {
      return 0;
    }
    return this.path.hashCode();
  }
}
