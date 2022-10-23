/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.image.imagen;

import org.eclipse.imagen.Histogram;

import net.anwiba.commons.image.histogram.HistogramBuilder;

public class ImagenHistogramToHistogramConverter {

  public net.anwiba.commons.image.histogram.Histogram convert(Histogram histogram) {
    if (histogram == null) {
      return null;
    }
    final HistogramBuilder builder = new HistogramBuilder();
    int numBands = histogram.getNumBands();
    
    for (int i = 0; i < numBands; i++) {
      double lowValue = histogram.getLowValue(i);
      double highValue = histogram.getHighValue(i);
      int numBins = histogram.getNumBins(i);
      double binWidth = (highValue - lowValue) / numBins;
      double pos = lowValue;
      int[] bins = histogram.getBins(i);
      long sum = sum(bins);
      if (sum == 0) {
        continue;
      }
      double factor = 1d / sum;
      for (int j = 0; j < numBins; j++) {
        double next = pos+binWidth;
        int count = bins[j];
        if (count <= 0) {
          pos = next;
          continue;
        }
        double percent = factor * count;
        builder.add(i, pos, next, count, percent);
        pos = next;
      }
    }
    return builder.build();
  }

  private long sum(int[] bins) {
    long sum = 0;
    for (int i = 0; i < bins.length; i++) {
      sum += bins[i];
    }
    return sum;
  }
  
}
