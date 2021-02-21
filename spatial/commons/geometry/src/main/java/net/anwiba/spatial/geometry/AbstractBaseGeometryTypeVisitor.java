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

package net.anwiba.spatial.geometry;

public abstract class AbstractBaseGeometryTypeVisitor<E extends Exception> implements IBaseGeometryTypeVisitor<Void, E> {

  @Override
  public Void visitPoint() throws E {
    point();
    return null;
  }

  @Override
  public Void visitCurve() throws E {
    curve();
    return null;
  }

  @Override
  public Void visitPolygon() throws E {
    polygon();
    return null;
  }

  @Override
  public Void visitUnknown() throws E {
    unknown();
    return null;
  }

  public abstract void point() throws E;

  public abstract void curve() throws E;

  public abstract void polygon() throws E;

  public abstract void unknown() throws E;

}
