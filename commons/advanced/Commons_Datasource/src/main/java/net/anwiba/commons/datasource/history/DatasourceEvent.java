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
package net.anwiba.commons.datasource.history;

import java.time.Duration;
import java.time.LocalDateTime;

import net.anwiba.commons.datasource.resource.IResourceDescription;

public final class DatasourceEvent implements IDatasourceEvent {

  private final IResourceDescription targetResourceDescription;
  private final IResourceDescription sourceResourceDescription;
  private final LocalDateTime date;
  private final String kind;
  private final String join;
  private final String condition;
  private final Long numberOfRows;
  private final Duration duration;

  public DatasourceEvent(
      final String kind,
      final IResourceDescription targetResourceDescription,
      final IResourceDescription sourceResourceDescription,
      final LocalDateTime date,
      final Duration duration,
      final String join,
      final String condition,
      final Long numberOfRows) {
    this.targetResourceDescription = targetResourceDescription;
    this.sourceResourceDescription = sourceResourceDescription;
    this.date = date;
    this.kind = kind;
    this.duration = duration;
    this.join = join;
    this.condition = condition;
    this.numberOfRows = numberOfRows;
  }

  @Override
  public IResourceDescription getSourceResourceDescription() {
    return this.sourceResourceDescription;
  }

  @Override
  public IResourceDescription getResourceDescription() {
    return this.targetResourceDescription;
  }

  @Override
  public String getKind() {
    return this.kind;
  }

  @Override
  public LocalDateTime getDate() {
    return this.date;
  }

  @Override
  public String getJoin() {
    return this.join;
  }

  @Override
  public String getCondition() {
    return this.condition;
  }

  @Override
  public Long getNumberOfRows() {
    return this.numberOfRows;
  }

  @Override
  public Duration getDuration() {
    return this.duration;
  }
}
