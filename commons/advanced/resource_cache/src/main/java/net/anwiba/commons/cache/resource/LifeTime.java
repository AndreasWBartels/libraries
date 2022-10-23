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
package net.anwiba.commons.cache.resource;

import java.time.Duration;

public class LifeTime implements ILifeTime {

  private final Duration duration;
  private final ResourceAccessEvent startPointOfTimeMeasuring;

  public static ILifeTime of() {
    return of(Duration.ofMinutes(5));
  }

  public static ILifeTime of(long duration) {
    return of(Duration.ofMillis(duration));
  }
  
  public static ILifeTime of(Duration duration) {
    return of(duration, ResourceAccessEvent.CREATED);
  }

  public static ILifeTime of(Duration duration, ResourceAccessEvent pointOfTime) {
    return new LifeTime(duration, pointOfTime);
  }

  private LifeTime(Duration duration, ResourceAccessEvent pointOfTime) {
    this.duration = duration;
    this.startPointOfTimeMeasuring = pointOfTime;
  }
  
  @Override
  public Duration getDuration() {
    return duration;
  }

  @Override
  public ResourceAccessEvent getStartPointOfTimeMeasuring() {
    return startPointOfTimeMeasuring;
  }
}
