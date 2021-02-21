/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.internal.java;

import net.anwiba.eclipse.project.dependency.java.IPath;

import java.util.Arrays;

public class Path implements IPath {

  private static final PathFactory factory = new PathFactory('.');
  private final String[] segments;
  private final String identifier;

  public Path(final String[] segments) {
    this.segments = segments == null
        ? new String[0]
        : segments;
    this.identifier = create(this.segments);
  }

  private String create(final String[] path) {
    return factory.create(path);
  }

  @Override
  public String toString() {
    return this.identifier;
  }

  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  @Override
  public String[] getSegments() {
    return this.segments;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof IPath) {
      final IPath other = (IPath) obj;
      return Arrays.equals(this.segments, other.getSegments());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.segments);
  }

  @Override
  public IPath getParent() {
    if (this.segments.length <= 1) {
      return new Path(new String[0]);
    }
    final String[] parentSegments = new String[this.segments.length - 1];
    System.arraycopy(this.segments, 0, parentSegments, 0, parentSegments.length);
    return new Path(parentSegments);
  }

  @Override
  public String lastSegment() {
    if (this.segments.length < 1) {
      return ""; //$NON-NLS-1$
    }
    return this.segments[this.segments.length - 1];
  }

  @Override
  public boolean isEmpty() {
    return (this.segments.length == 0);
  }

  @Override
  public boolean isParent(final IPath path) {
    return path.getSegments().length < this.segments.length
        & Arrays.equals(path.getSegments(), Arrays.copyOf(this.segments, path.getSegments().length));
  }
}
