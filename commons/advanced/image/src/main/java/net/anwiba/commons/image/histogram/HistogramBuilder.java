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

import java.util.HashMap;
import java.util.Map;

public class HistogramBuilder {

  private double minimum = Double.NaN;
  private double maximum = Double.NaN;
  private double maximumPercent = Double.NaN;
  private final HashMap<Integer, HistorgramBandBuilder> historgramBandBuilders = new HashMap<>();

  public void
      add(final Integer band, final double minimum, final double maximum, final long count, final double percent) {
    this.minimum =
        Double.isNaN(this.minimum) ? minimum : Math.min(this.minimum, minimum);
    this.maximum =
        Double.isNaN(this.maximum) ? maximum : Math.max(this.maximum, maximum);
    this.maximumPercent =
        Double.isNaN(this.maximumPercent) ? percent : Math.max(this.maximumPercent, percent);
    this.historgramBandBuilders.putIfAbsent(band, new HistorgramBandBuilder(band));
    HistorgramBandBuilder builder = this.historgramBandBuilders.get(band);
    builder.add(minimum, maximum, count, percent);
  }

  public Histogram build() {
    final Map<Integer, HistorgramBand> historgramBands = new HashMap<>();
    for (HistorgramBandBuilder builder : this.historgramBandBuilders.values()) {
      HistorgramBand band = builder.build();
      historgramBands.put(band.getNumber(), band);
    }
    return new Histogram(Double.isNaN(this.minimum) ? 0 : this.minimum,
        Double.isNaN(this.maximum) ? 255 : this.maximum,
        Double.isNaN(this.maximumPercent) ? 1 : this.maximumPercent,
        historgramBands);
  }

}
