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

import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.between;
import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.ceil;
import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.duration;
import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.floor;
import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.minus;
import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.period;
import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.plus;
import static net.anwiba.commons.utilities.time.TemporalAmountUtilities.round;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class TemporalAmountUtilitiesTest {

  @Test
  public void floorDateTime() {
    LocalDateTime time = time(1989, 11, 23, 12, 45, 34);
    assertEquals(time(1989, 11, 23, 12, 45, 0), floor(time, ChronoUnit.MINUTES));
    assertEquals(time(1989, 11, 23, 12, 0, 0), floor(time, ChronoUnit.HOURS));
    assertEquals(time(1989, 11, 23, 0, 0, 0), floor(time, ChronoUnit.DAYS));
    assertEquals(time(1989, 11, 1, 0, 0, 0), floor(time, ChronoUnit.MONTHS));
    assertEquals(time(1989, 1, 1, 0, 0, 0), floor(time, ChronoUnit.YEARS));
  }

  @Test
  public void floorTemporalAmount() {
    Duration duration = duration(1989, 11, 23, 12, 45, 34);
    assertEquals(duration(1989, 11, 23, 12, 45, 0), floor(duration, ChronoUnit.MINUTES));
    assertEquals(duration(1989, 11, 23, 12, 0, 0), floor(duration, ChronoUnit.HOURS));
    assertEquals(period(1989, 11, 23, 0, 0, 0), floor(duration, ChronoUnit.DAYS));
    assertEquals(period(1989, 11, 1, 0, 0, 0), floor(duration, ChronoUnit.MONTHS));
    assertEquals(period(1989, 1, 1, 0, 0, 0), floor(duration, ChronoUnit.YEARS));
  }

  @Test
  public void betweenTemporalAmount() {
    LocalDateTime from = time(2019, 8, 5, 19, 45, 34);
    assertEquals(Duration.ofMinutes(0), between(from, time(2019, 8, 5, 19, 45, 4), ChronoUnit.MINUTES));
    assertEquals(Duration.ofMinutes(0), between(from, time(2019, 8, 5, 19, 45, 40), ChronoUnit.MINUTES));
    assertEquals(Duration.ofMinutes(-1), between(from, time(2019, 8, 5, 19, 44, 4), ChronoUnit.MINUTES));
    assertEquals(Duration.ofMinutes(-1), between(from, time(2019, 8, 5, 19, 44, 40), ChronoUnit.MINUTES));
    assertEquals(Duration.ofMinutes(1), between(from, time(2019, 8, 5, 19, 46, 4), ChronoUnit.MINUTES));
    assertEquals(Duration.ofMinutes(1), between(from, time(2019, 8, 5, 19, 46, 40), ChronoUnit.MINUTES));

    assertEquals(Duration.ofHours(0), between(from, time(2019, 8, 5, 19, 5, 34), ChronoUnit.HOURS));
    assertEquals(Duration.ofHours(0), between(from, time(2019, 8, 5, 19, 45, 34), ChronoUnit.HOURS));
    assertEquals(Duration.ofHours(-1), between(from, time(2019, 8, 5, 18, 5, 34), ChronoUnit.HOURS));
    assertEquals(Duration.ofHours(-1), between(from, time(2019, 8, 5, 18, 45, 34), ChronoUnit.HOURS));
    assertEquals(Duration.ofHours(1), between(from, time(2019, 8, 5, 20, 5, 34), ChronoUnit.HOURS));
    assertEquals(Duration.ofHours(1), between(from, time(2019, 8, 5, 20, 45, 34), ChronoUnit.HOURS));

    assertEquals(Period.ofDays(0), between(from, time(2019, 8, 5, 7, 45, 34), ChronoUnit.DAYS));
    assertEquals(Period.ofDays(0), between(from, time(2019, 8, 5, 19, 45, 34), ChronoUnit.DAYS));
    assertEquals(Period.ofDays(-1), between(from, time(2019, 8, 4, 7, 45, 34), ChronoUnit.DAYS));
    assertEquals(Period.ofDays(-1), between(from, time(2019, 8, 4, 19, 45, 34), ChronoUnit.DAYS));
    assertEquals(Period.ofDays(1), between(from, time(2019, 8, 6, 7, 45, 34), ChronoUnit.DAYS));
    assertEquals(Period.ofDays(1), between(from, time(2019, 8, 6, 19, 45, 34), ChronoUnit.DAYS));

//    assertEquals(duration(1989, 11, 23, 12, 0, 0), floor(duration, ChronoUnit.HOURS));
//    assertEquals(period(1989, 11, 23, 0, 0, 0), floor(duration, ChronoUnit.DAYS));
//    assertEquals(period(1989, 11, 1, 0, 0, 0), floor(duration, ChronoUnit.MONTHS));
//    assertEquals(period(1989, 1, 1, 0, 0, 0), floor(duration, ChronoUnit.YEARS));
  }

  @Test
  public void ceilTemporalAmount() {
    Duration duration = duration(1989, 11, 23, 12, 45, 34);
    assertEquals(duration(1989, 11, 23, 12, 46, 0), ceil(duration, ChronoUnit.MINUTES));
    assertEquals(duration(1989, 11, 23, 13, 0, 0), ceil(duration, ChronoUnit.HOURS));
    assertEquals(period(1989, 11, 24, 0, 0, 0), ceil(duration, ChronoUnit.DAYS));
    assertEquals(period(1989, 12, 1, 0, 0, 0), ceil(duration, ChronoUnit.MONTHS));
    assertEquals(period(1990, 1, 1, 0, 0, 0), ceil(duration, ChronoUnit.YEARS));
  }

  @Test
  public void roundTemporalAmount() {
    Duration duration = duration(1989, 11, 23, 12, 45, 34);
    assertEquals(duration(1989, 11, 23, 12, 46, 0), round(duration, ChronoUnit.MINUTES));
    assertEquals(duration(1989, 11, 23, 13, 0, 0), round(duration, ChronoUnit.HOURS));
    assertEquals(period(1989, 11, 24, 0, 0, 0), round(duration, ChronoUnit.DAYS));
    assertEquals(period(1989, 12, 1, 0, 0, 0), round(duration, ChronoUnit.MONTHS));
    assertEquals(period(1990, 1, 1, 0, 0, 0), round(duration, ChronoUnit.YEARS));
  }

  @Test
  public void plusTemporalAmount() {
    Duration duration = duration(1989, 11, 23, 12, 45, 34);
    assertEquals(duration(1989, 11, 23, 12, 45, 44), plus(duration, 10, ChronoUnit.SECONDS));
    assertEquals(duration(1989, 11, 23, 12, 55, 34), plus(duration, 10, ChronoUnit.MINUTES));
    assertEquals(duration(1989, 11, 23, 22, 45, 34), plus(duration, 10, ChronoUnit.HOURS));
    assertEquals(period(1989, 12, 3, 0, 0, 0), plus(duration, 10, ChronoUnit.DAYS));
    assertEquals(period(1990, 9, 23, 0, 0, 0), plus(duration, 10, ChronoUnit.MONTHS));
    assertEquals(period(1999, 11, 23, 0, 0, 0), plus(duration, 10, ChronoUnit.YEARS));
  }

  @Test
  public void minusTemporalAmount() {
    Duration duration = duration(1989, 11, 23, 12, 45, 34);
    assertEquals(duration(1989, 11, 23, 12, 45, 24), minus(duration, 10, ChronoUnit.SECONDS));
    assertEquals(duration(1989, 11, 23, 12, 35, 34), minus(duration, 10, ChronoUnit.MINUTES));
    assertEquals(duration(1989, 11, 23, 2, 45, 34), minus(duration, 10, ChronoUnit.HOURS));
    assertEquals(period(1989, 11, 13, 0, 0, 0), minus(duration, 10, ChronoUnit.DAYS));
    assertEquals(period(1989, 1, 23, 0, 0, 0), minus(duration, 10, ChronoUnit.MONTHS));
    assertEquals(period(1979, 11, 23, 0, 0, 0), minus(duration, 10, ChronoUnit.YEARS));
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
