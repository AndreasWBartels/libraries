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

public class HistorgramBandEntry implements Comparable<HistorgramBandEntry> {

  private final double minimum;
  private final double maximum;
  private final long count;
  private final double percent;
  private final double mean;

  public HistorgramBandEntry(final double minimum, final double maximum, final long count, final double percent) {
    this.minimum = minimum;
    this.maximum = maximum;
    this.mean = (this.minimum + this.maximum) / 2;
    this.count = count;
    this.percent = percent;
  }

  public double getMinimum() {
    return this.minimum;
  }

  public double getMaximum() {
    return this.maximum;
  }

  public long getCount() {
    return this.count;
  }

  public double getPercent() {
    return this.percent;
  }

  public double getMean() {
    return this.mean;
  }

  @Override
  public int compareTo(final HistorgramBandEntry other) {
    return Double.compare(this.mean, other.mean);
  }

}
