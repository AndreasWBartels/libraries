/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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

import static net.anwiba.commons.utilities.time.LocalDateTimeUtilities.truncat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class LocalDateTimeUtilitiesTest {
  @Test
  public void floorDateTime() {
    LocalDateTime time = time(1989, 11, 23, 12, 45, 34);
    assertEquals(time(1989, 11, 23, 12, 45, 34), truncat(time, Duration.of(1, ChronoUnit.SECONDS), ChronoUnit.SECONDS));
    assertEquals(time(1989, 11, 23, 12, 45, 30), truncat(time, Duration.of(5, ChronoUnit.SECONDS), ChronoUnit.SECONDS));
    assertEquals(time(1989, 11, 23, 12, 45, 20),
        truncat(time, Duration.of(20, ChronoUnit.SECONDS), ChronoUnit.SECONDS));

    assertEquals(time(1989, 11, 23, 12, 45, 0), truncat(time, Duration.of(1, ChronoUnit.MINUTES), ChronoUnit.MINUTES));
    assertEquals(time(1989, 11, 23, 12, 44, 0), truncat(time, Duration.of(2, ChronoUnit.MINUTES), ChronoUnit.MINUTES));
    assertEquals(time(1989, 11, 23, 12, 45, 0), truncat(time, Duration.of(5, ChronoUnit.MINUTES), ChronoUnit.MINUTES));
    assertEquals(time(1989, 11, 23, 12, 40, 0), truncat(time, Duration.of(10, ChronoUnit.MINUTES), ChronoUnit.MINUTES));
    assertEquals(time(1989, 11, 23, 12, 30, 0), truncat(time, Duration.of(30, ChronoUnit.MINUTES), ChronoUnit.MINUTES));
    assertEquals(time(1989, 11, 23, 12, 0, 0), truncat(time, Duration.of(60, ChronoUnit.MINUTES), ChronoUnit.MINUTES));

    assertEquals(time(1989, 11, 23, 12, 0, 0), truncat(time, Duration.of(1, ChronoUnit.HOURS), ChronoUnit.HOURS));
    assertEquals(time(1989, 11, 23, 10, 0, 0), truncat(time, Duration.of(5, ChronoUnit.HOURS), ChronoUnit.HOURS));
    assertEquals(time(1989, 11, 23, 0, 0, 0), truncat(time, Duration.of(24, ChronoUnit.HOURS), ChronoUnit.HOURS));

    assertEquals(time(1989, 11, 23, 0, 0, 0), truncat(time, Period.ofDays(1), ChronoUnit.DAYS));
    assertEquals(time(1989, 11, 21, 0, 0, 0), truncat(time, Period.ofDays(5), ChronoUnit.DAYS));

    assertEquals(time(1989, 11, 1, 0, 0, 0), truncat(time, Period.ofMonths(1), ChronoUnit.MONTHS));
    assertEquals(time(1989, 9, 1, 0, 0, 0), truncat(time, Period.ofMonths(4), ChronoUnit.MONTHS));
  }

  private LocalDateTime time(final int year,
      final int month,
      final int dayOfMonth,
      final int hour,
      final int minute,
      final int second) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
  }
}
