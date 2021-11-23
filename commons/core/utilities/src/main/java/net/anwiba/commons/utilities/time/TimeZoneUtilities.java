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
package net.anwiba.commons.utilities.time;

import java.util.TimeZone;

import net.anwiba.commons.lang.optional.Optional;

@SuppressWarnings("nls")
public class TimeZoneUtilities {

  private static final int MIM_PER_HOUR = 60;
  private static final int MILLISECONDS_PER_MIN = 60000;
  private static final int MILLISECONDS_PER_HOUR = 3600000;

  public static TimeZone getUniversalTimeZone() {
    return TimeZone.getTimeZone("UTC");
  }

  public static TimeZone getSystemTimeZone() {
    return TimeZone.getDefault();
  }

  public static TimeZone getUserTimeZone() {
    return Optional.of(System.getProperty("user.timezone")).convert(z -> TimeZone.getTimeZone(z)).getOr(
        () -> TimeZone.getDefault());
  }

  public static void setTimeZoneToGmt_NNN() {
    TimeZone.setDefault(TimeZone.getTimeZone(getTimeZoneToGmt_NNN()));
  }

  private static String getTimeZoneToGmt_NNN() {
    final int rawOffset = getRawOffset();
    return "GMT" + toString(rawOffset);
  }

  private static int getRawOffset() {
    return TimeZone.getDefault().getRawOffset();
  }

  private static String toString(final int rawOffset) {
    if (rawOffset == 0) {
      return "";
    }
    final int hours = rawOffset / MILLISECONDS_PER_HOUR;
    final int min = (rawOffset / MILLISECONDS_PER_MIN) - (hours * MIM_PER_HOUR);
    final String string = String.valueOf(hours) + ":" + String.valueOf(min);
    return rawOffset < 0 ? string : "+" + string;
  }
}