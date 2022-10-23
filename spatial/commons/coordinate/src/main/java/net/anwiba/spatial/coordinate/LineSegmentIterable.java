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
package net.anwiba.spatial.coordinate;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineSegmentIterable implements Iterable<ILineSegment> {

  public static final class LineSegmentIterator implements Iterator<ILineSegment> {

    private final Iterator<ICoordinate> iterator;
    private ICoordinate previous;
    private ILineSegment next;

    public LineSegmentIterator(final ICoordinateSequence sequence) {
      this.iterator = sequence.getCoordinates().iterator();
    }

    @Override
    public boolean hasNext() {
      if (next != null) {
        return true;
      }
      while (this.iterator.hasNext() && next == null) {
        ICoordinate coordinate = this.iterator.next();
        if (this.previous == null) {
          this.previous = coordinate;
          continue;
        }
        this.next = new LineSegment(previous, coordinate);
        this.previous = coordinate;
      };
      return next != null;
    }

    @Override
    public ILineSegment next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      try {
        return next;
      } finally {
        this.next = null;
      }
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
