/*
 * #%L
 * anwiba spatial
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
package net.anwiba.commons.image.histogram;

import net.anwiba.commons.lang.stream.Streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class HistorgramBand implements Iterable<HistorgramBandEntry> {

  private final Integer number;
  private final Collection<HistorgramBandEntry> entries;
  private final double minimum;
  private final double maximum;
  private final double maximumPercent;

  public HistorgramBand(final Integer number,
      final double minimum,
      final double maximum,
      final double maximumPercent,
      final Collection<HistorgramBandEntry> entries) {
    this.number = number;
    this.minimum = minimum;
    this.maximum = maximum;
    this.maximumPercent = maximumPercent;
    this.entries = new ArrayList<>(entries);
  }

  public Integer getNumber() {
    return this.number;
  }

  public Collection<HistorgramBandEntry> getEntries() {
    return this.entries;
  }

  public double getMinimum() {
    return this.minimum;
  }

  public double getMaximum() {
    return this.maximum;
  }

  public double getMaximumPercent() {
    return this.maximumPercent;
  }

  @Override
  public Iterator<HistorgramBandEntry> iterator() {
    return this.entries.iterator();
  }

  public double getPercent(final double value) {
    for (HistorgramBandEntry entry : this.entries) {
      if (entry.getMinimum() <= value && entry.getMaximum() > value) {
        return entry.getPercent();
      }
    }
    return Double.NaN;
  }

  public Long getSumOfCounts() {
    return Streams.of(this.entries).aggregate(0l, (o, entry) -> o + entry.getCount()).get();
  }

}
