/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.utilities.number;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.utilities.string.StringUtilities;

// NOT_PUBLISHED
public class RobustDoubleParser {

  private static final String INFINITY = "infinity"; //$NON-NLS-1$

  private class SeparatorConfiguration {

    public int preSeparatorBlockCount;
    public int postSeparatorBlockIndex;
    public int exponentSeparatorBlockIndex;
  }

  private final int MIN = '0';
  private final int MAX = '9';

  public double parse(final String string) {
    if (isInfinity(string)) {
      return extractInfinity(string);
    }
    if (isNaN(string)) {
      return Double.NaN;
    }

    final char[] chars = string.toCharArray();
    final List<LongValueBuilder> longValueBuilders = new ArrayList<>(20);
    final List<Character> separators = new ArrayList<>(20);
    LongValueBuilder longValueBuilder = new LongValueBuilder(string);
    for (int i = 0; i < chars.length; i++) {
      final char c = chars[i];

      if (isNegativeSign(c)) {
        longValueBuilder.setNegativ();
      } else if (isDigit(c)) {
        longValueBuilder.add(c);
      } else if (c == '+') {
        continue;
      } else if ((c == ',' || c == '.' || c == 'E' || c == 'e')) {
        longValueBuilders.add(longValueBuilder);
        separators.add(c);
        longValueBuilder = new LongValueBuilder(string);
      } else {
        throwException(string);
      }
    }
    longValueBuilders.add(longValueBuilder);

    final SeparatorConfiguration separatorConfiguration = analyseSeparators(separators, string);

    final long preSeparatorValue = extractPreSeparatorValue(
        longValueBuilders,
        separatorConfiguration.preSeparatorBlockCount);
    final double postSeparatorValue = extractPostSeparatorValue(
        longValueBuilders,
        separatorConfiguration.postSeparatorBlockIndex);
    final long exponentValue = extractExponentValue(longValueBuilders, separatorConfiguration.exponentSeparatorBlockIndex);

    double value = (preSeparatorValue + postSeparatorValue) * Math.pow(10, exponentValue);
    if (isNegative(longValueBuilders)) {
      value = value * -1;
    }

    return value;
  }

  private boolean isNaN(final String string) {
    return string.matches("-?(\\?|[Nn][Aa][Nn])"); //$NON-NLS-1$
  }

  private boolean isInfinity(final String string) {
    return StringUtilities.containsIgnoreCase(string, INFINITY);
  }

  private double extractInfinity(final String string) {
    final int minusCount = StringUtilities.numberOfMatches(string, "-"); //$NON-NLS-1$
    return isEven(minusCount) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
  }

  private boolean isEven(final int minuses) {
    return minuses % 2 == 0;
  }

  private void throwException(final String originalString) {
    throw new NumberFormatException(MessageFormat.format("Unable to parse string ''{0}'' as Double.", //$NON-NLS-1$
        originalString));
  }

  private SeparatorConfiguration analyseSeparators(final List<Character> separators, final String originalString) {
    Character lastSepataror = null;
    boolean exponentSeparatorFound = false;
    boolean separatorFound = false;
    boolean secondSeparatorFound = false;
    final int separatorsCount = separators.size();
    for (int i = 0; i < separatorsCount; i++) {
      final Character separator = separators.get(i);
      final char separatorValue = separator.charValue();

      if (separatorValue == '.' || separatorValue == ',') {
        if (exponentSeparatorFound) {
          throwException(originalString);
        }
        separatorFound = true;

        if (lastSepataror != null && separatorValue != lastSepataror.charValue()) {
          if (secondSeparatorFound) {
            throwException(originalString);
          }
          secondSeparatorFound = true;
        }
      } else if (separatorValue == 'E' || separatorValue == 'e') {
        exponentSeparatorFound = true;
      }
      lastSepataror = separator;
    }

    final SeparatorConfiguration separatorConfiguration = new SeparatorConfiguration();
    int preSeparatorBlockCount = separatorsCount + 1;
    int postSeparatorBlockIndex = -1;
    int exponentSeparatorBlockIndex = -1;
    if (separatorFound) {
      preSeparatorBlockCount--;
      postSeparatorBlockIndex = separatorsCount;
    }
    if (exponentSeparatorFound) {
      preSeparatorBlockCount--;
      postSeparatorBlockIndex--;
      exponentSeparatorBlockIndex = separatorsCount;
    }
    separatorConfiguration.preSeparatorBlockCount = preSeparatorBlockCount;
    separatorConfiguration.postSeparatorBlockIndex = postSeparatorBlockIndex;
    separatorConfiguration.exponentSeparatorBlockIndex = exponentSeparatorBlockIndex;
    return separatorConfiguration;
  }

  private boolean isNegative(final List<LongValueBuilder> longValueBuilders) {
    return longValueBuilders.get(0).isNegativ();
  }

  private long extractExponentValue(final List<LongValueBuilder> longValueBuilders, final int exponentSeparatorBlockIndex) {
    if (exponentSeparatorBlockIndex < 0) {
      return 0;
    }
    final LongValueBuilder longValueBuilder = longValueBuilders.get(exponentSeparatorBlockIndex);
    final long parsedLongValue = longValueBuilder.build();
    if (!longValueBuilder.isNegativ()) {
      return parsedLongValue;
    }
    return parsedLongValue * -1;
  }

  private double extractPostSeparatorValue(final List<LongValueBuilder> longValueBuilders, final int valueString) {
    if (valueString < 0) {
      return 0.0;
    }
    final LongValueBuilder longValueBuilder = longValueBuilders.get(valueString);
    final long postSeparatorNumber = longValueBuilder.build();
    final int length = longValueBuilder.length();
    return postSeparatorNumber / (Math.pow(10.0, length));
  }

  private long extractPreSeparatorValue(final List<LongValueBuilder> longValueBuilders, final int preSeparatorBlockCount) {
    long amount = 0;
    for (int i = 0; i < preSeparatorBlockCount; i++) {
      final double factor = Math.pow(1000, preSeparatorBlockCount - i - 1);
      final long longValue = longValueBuilders.get(i).build();
      amount += Math.abs(longValue * factor);
    }
    return amount;
  }

  private boolean isDigit(final int c) {
    return (this.MIN <= c && c <= this.MAX);
  }

  private boolean isNegativeSign(final int c) {
    return c == '-';
  }
}