/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anwiba.commons.logging.ILevel;

public class DateTimeInterval {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(DateTimeInterval.class);

  private final ZonedDateTime from;
  private final ZonedDateTime until;
  private final Duration resolution;

  public DateTimeInterval(final ZonedDateTime from, final ZonedDateTime until, final Duration resolution) {
    this.from = from;
    this.until = until;
    this.resolution = resolution;
  }

  public ZonedDateTime getFrom() {
    return this.from;
  }

  public ZonedDateTime getUntil() {
    return this.until;
  }

  public Duration getDuration() {
    return Duration.between(this.from, this.until);
  }

  public Duration getResolution() {
    return this.resolution;
  }

  public boolean containts(final ZonedDateTime value) {
    return (!this.from.isAfter(value)) && (!this.until.isBefore(value));
  }

  @Override
  public String toString() {
    return this.from.toString() + "/" + this.until.toString() + "/" + this.resolution.toString(); //$NON-NLS-1$//$NON-NLS-2$
  }

  public static DateTimeInterval valueOf(final String range) {
    try {
      final Matcher matcher = Pattern.compile("(.+)/(.+)/(.+)").matcher(range); //$NON-NLS-1$
      if (matcher.matches() && matcher.groupCount() == 3) {
        final ZonedDateTime from = ZonedDateTime.parse(matcher.group(1));
        final ZonedDateTime until = ZonedDateTime.parse(matcher.group(2));
        final Duration resolution = Duration.parse(matcher.group(3));
        return new DateTimeInterval(from, until, resolution);
      }
      return null;
    } catch (final DateTimeException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return null;
    }
  }

}
