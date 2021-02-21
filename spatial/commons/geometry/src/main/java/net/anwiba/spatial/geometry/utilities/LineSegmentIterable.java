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
package net.anwiba.spatial.geometry.utilities;

import java.util.Iterator;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.geometry.ILineSegment;
import net.anwiba.spatial.geometry.internal.LineSegment;

public class LineSegmentIterable implements Iterable<ILineSegment> {

  public static final class LineSegmentIterator implements Iterator<ILineSegment> {

    private final Iterator<ICoordinate> iterator;
    private ICoordinate next;

    public LineSegmentIterator(final ICoordinateSequence sequence) {
      this.iterator = sequence.getCoordinates().iterator();
      this.next = this.iterator.hasNext() ? this.iterator.next() : null;
    }

    @Override
    public boolean hasNext() {
      return this.iterator.hasNext();
    }

    @Override
    public ILineSegment next() {
      final ICoordinate previous = this.next;
      this.next = this.iterator.next();
      return new LineSegment(previous, this.next);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private final ICoordinateSequence sequence;

  public LineSegmentIterable(final ICoordinateSequence sequence) {
    this.sequence = sequence;
  }

  @Override
  public Iterator<ILineSegment> iterator() {
    return new LineSegmentIterator(this.sequence);
  }
}
