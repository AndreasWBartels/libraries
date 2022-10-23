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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.anwiba.commons.lang.stream.Streams;

public class Histogram implements Iterable<HistorgramBand> {

  private final double minimum;
  private final double maximum;
  private final double maximumPercent;
  private final Map<Integer, HistorgramBand> historgramBands;

  public Histogram(final double minimum,
      final double maximum,
      final double maximumPercent,
      final Map<Integer, HistorgramBand> historgramBands) {
    this.minimum = minimum;
    this.maximum = maximum;
    this.maximumPercent = maximumPercent;
    this.historgramBands = historgramBands;
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

  public List<Integer> getBandNumbers() {
    ArrayList<Integer> numbers = new ArrayList<>(this.historgramBands.keySet());
    Collections.sort(numbers);
    return numbers;
  }

  public int getNumberOfBands() {
    return this.historgramBands.size();
  }

  public HistorgramBand getBand(final Integer number) {
    return this.historgramBands.get(number);
  }

  @Override
  public Iterator<HistorgramBand> iterator() {
    return this.historgramBands.values().iterator();
  }

  record Interval(double minimum, double maximum) {};
  
  public static Histogram merge(final List<Histogram> histograms) {
    if (histograms.size() == 1) {
      return histograms.get(0);
    }
    
    final HistogramBuilder builder = new HistogramBuilder();
    final List<Integer> bandNumbers = bandNumbers(histograms);
    for (Integer bandNumber : bandNumbers) {
      final List<HistorgramBand> historgramBands = historgramBands(bandNumber, histograms);
      final Map<Interval,Long> countsByInterval = new  HashMap<>(); 
      for (HistorgramBand historgramBand : historgramBands) {
        final Collection<HistorgramBandEntry> entries = historgramBand.getEntries();
        for (HistorgramBandEntry entry : entries) {
          final Interval interval = new Interval(entry.getMinimum(), entry.getMaximum());
          if (countsByInterval.containsKey(interval)) {
            countsByInterval.put(interval, countsByInterval.get(interval) + entry.getCount());
            continue;
          }
          countsByInterval.put(interval, entry.getCount());
        }
      }
      final List<Entry<Interval, Long>> entries = new ArrayList<>(countsByInterval.entrySet());
      Collections.sort(entries, new Comparator<Entry<Interval, Long>>() {

        @Override
        public int compare(Entry<Interval, Long> o1, Entry<Interval, Long> o2) {
          return Double.valueOf(o1.getKey().minimum()).compareTo(Double.valueOf(o2.getKey().minimum()));
        }
      });
      final long sumOfCounts = Streams
          .of(historgramBands)
          .aggregate(0l, (o, band) -> o + band.getSumOfCounts())
          .getOr(() -> 0l);
      final double factor = 1d / sumOfCounts;
      for (Entry<Interval,Long> entry : entries) {
        long count = entry.getValue();
        builder.add(bandNumber, entry.getKey().minimum(), entry.getKey().maximum(), count, count * factor);
      }
    }
    return builder.build();
  }

  private static List<HistorgramBand> historgramBands(Integer bandNumber, List<Histogram> histograms) {
    return Streams.of(histograms)
        .convert(histogram -> histogram.getBand(bandNumber))
        .asList();
  }

  private static List<Integer> bandNumbers(final List<Histogram> histograms) {
    Set<Integer> numbers = new LinkedHashSet<>();
    histograms.forEach(histogram -> numbers.addAll(histogram.getBandNumbers()));
    final ArrayList<Integer> result = new ArrayList<>(numbers);
    Collections.sort(result);
    return result;
  }

}
