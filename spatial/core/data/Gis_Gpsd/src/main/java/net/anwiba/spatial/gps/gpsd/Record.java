/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels 
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
package net.anwiba.spatial.gps.gpsd;

public class Record implements IRecord {

  private final String time;
  private final Integer mode;
  private final Double lat;
  private final Double lon;
  private final Double alt;

  public Record(final String time, final Integer mode, final Double lat, final Double lon, final Double alt) {
    this.time = time;
    this.mode = mode;
    this.lat = lat;
    this.lon = lon;
    this.alt = alt;
  }

  @Override
  public String getTime() {
    return this.time;
  }

  @Override
  public Integer getMode() {
    return this.mode;
  }

  @Override
  public Double getLat() {
    return this.lat;
  }

  @Override
  public Double getLon() {
    return this.lon;
  }

  @Override
  public Double getAlt() {
    return this.alt;
  }

}
