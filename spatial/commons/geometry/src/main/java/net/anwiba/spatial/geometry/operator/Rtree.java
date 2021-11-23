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

package net.anwiba.spatial.geometry.operator;

import java.util.ArrayList;
import java.util.List;

import org.khelekore.prtree.MBR;
import org.khelekore.prtree.MBRConverter;
import org.khelekore.prtree.PRTree;

import net.anwiba.commons.utilities.collection.IterableUtilities;

public class Rtree<T> {

  public static class Mbr implements MBR {

    private final double startValue;
    private final double endValue;

    public Mbr(final double startValue, final double endValue) {
      this.startValue = startValue;
      this.endValue = endValue;
    }

    @Override
    public MBR union(final MBR mbr) {
      return new Mbr(Math.min(mbr.getMin(0), getMin(0)), Math.min(mbr.getMax(0), getMax(0)));
    }

    @Override
    public <T> boolean intersects(final T other, final MBRConverter<T> converter) {
      if (other == null) {
        return false;
      }
      return intersects(converter.getMin(0, other), converter.getMax(0, other));
    }

    @Override
    public boolean intersects(final MBR other) {
      if (other == null) {
        return false;
      }
      return intersects(other.getMin(0), other.getMax(0));
    }

    private boolean intersects(final double otherMin, final double otherMax) {
      boolean intersects = !(otherMin > getMax(0) || otherMax < getMin(0));
      return intersects;
    }

    @Override
    public double getMin(final int axis) {
      return Math.min(this.startValue, this.endValue);
    }

    @Override
    public double getMax(final int axis) {
      return Math.max(this.startValue, this.endValue);
    }

    @Override
    public int getDimensions() {
      return 1;
    }
  }

  List<T> objects = new ArrayList<>();
  private MBRConverter<T> mbrConverter;
  private PRTree<T> tree;

  public Rtree(final IEnvelopeConverter<T> converter) {
    this.mbrConverter = new MBRConverter<T>() {

      @Override
      public int getDimensions() {
        return converter.getDimensions();
      }

      @Override
      public double getMin(final int axis, final T t) {
        return converter.getMin(axis, t);
      }

      @Override
      public double getMax(final int axis, final T t) {
        return converter.getMax(axis, t);
      }
    };
    this.tree = new PRTree<>(this.mbrConverter, 10);
  }

  public List<T> query(final double startValue, final double endValue) {
    final Iterable<T> iterable = this.tree.find(new Mbr(startValue, endValue));
    return IterableUtilities.asList(iterable);
  }

  public List<T> query(final double value) {
    final Iterable<T> iterable = this.tree.find(new Mbr(value, value));
    List<T> list = IterableUtilities.asList(iterable);
    return list;
  }

  public void insert(final T object) {
    this.objects.add(object);
  }

  public void build() {
    this.tree.load(this.objects);
  }

}
