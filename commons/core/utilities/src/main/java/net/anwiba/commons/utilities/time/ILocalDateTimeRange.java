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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public interface ILocalDateTimeRange extends Serializable {

  LocalDateTime getFrom();

  LocalDateTime getUntil();

  LocalDateTime getCenter();

  TemporalAmount getDuration();

  TemporalAmount getDurationFromUntilToNow();

  TemporalAmount getDurationFromFromToNow();

  boolean interact(LocalDateTime time);

  boolean interact(ILocalDateTimeRange segment);

  ILocalDateTimeRange intersection(ILocalDateTimeRange segment);

  ILocalDateTimeRange shift(long value, TemporalUnit unit);

  ILocalDateTimeRange duration(long value, TemporalUnit temporalUnit);

  ILocalDateTimeRange plus(long value, TemporalUnit unit);

  ILocalDateTimeRange minus(long value, TemporalUnit unit);

  ILocalDateTimeRange toRelative();

  ILocalDateTimeRange toRelative(TemporalUnit unit);

  ILocalDateTimeRange toAbsolute();

}
