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

import java.util.SortedSet;
import java.util.TreeSet;

public class HistorgramBandBuilder {

  private double minimum = Double.NaN;
  private double maximum = Double.NaN;
  private double maximumPercent = Double.NaN;
  private final Integer band;

  SortedSet<HistorgramBandEntry> entries = new TreeSet<>();

  public HistorgramBandBuilder(final Integer band) {
    this.band = band;
  }

  public void add(final double minimum, final double maximum, final long count, final double percent) {
    this.minimum =
        Double.isNaN(this.minimum) ? minimum : Math.min(this.minimum, minimum);
    this.maximum =
        Double.isNaN(this.maximum) ? maximum : Math.max(this.maximum, maximum);
    this.maximumPercent =
        Double.isNaN(this.maximumPercent) ? percent : Math.max(this.maximum, percent);
    this.entries.add(new HistorgramBandEntry(minimum, maximum, count, percent));

  }

  public HistorgramBand build() {
    return new HistorgramBand(this.band, this.minimum, this.maximum, this.maximumPercent, this.entries);
  }

}
