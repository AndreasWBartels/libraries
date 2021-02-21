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

public abstract class AbstractGeometryTypeVisitor<E extends Exception> implements IGeometryTypeVisitor<Void, E> {

  public abstract void point() throws E;

  public abstract void lineString() throws E;

  public abstract void polygon() throws E;

  public abstract void linearRing() throws E;

  public abstract void multiPoint() throws E;

  public abstract void multiLineString() throws E;

  public abstract void multiPolygon() throws E;

  public abstract void collection() throws E;

  public abstract void unknown() throws E;

  @Override
  public Void visitPoint() throws E {
    point();
    return null;
  }

  @Override
  public Void visitLineString() throws E {
    lineString();
    return null;
  }

  @Override
  public Void visitPolygon() throws E {
    polygon();
    return null;
  }

  @Override
  public Void visitLinearRing() throws E {
    linearRing();
    return null;
  }

  @Override
  public Void visitMultiPoint() throws E {
    multiPoint();
    return null;
  }

  @Override
  public Void visitMultiLineString() throws E {
    multiLineString();
    return null;
  }

  @Override
  public Void visitMultiPolygon() throws E {
    multiPolygon();
    return null;
  }

  @Override
  public Void visitCollection() throws E {
    collection();
    return null;
  }

  @Override
  public Void visitUnknown() throws E {
    // TODO Auto-generated method stub
    return null;
  }

}
