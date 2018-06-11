/*
 * #%L
 * anwiba commons core
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

package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.io.Serializable;

import net.anwiba.commons.lang.object.ObjectUtilities;

public class ToWgs84 implements Serializable {

  // England, Schottland, Wales WGS84 OSGB36 446.448,-125.157,542.06,0.1502,0.247,0.8421,-20.4894
  // Irland WGS84 Ireland 1965 482.53,-130.596,564.557,-1.042,-0.214,-0.631,8.15
  // Deutschland WGS84 DHDN 591.28,81.35,396.39,-1.4770,0.0736,1.4580,9.82  ???
  // Deutschland WGS84 DHDN 598.1, 73.7, 418.2, 0.202, 0.045, -2.455, 6.7
  // Deutschland WGS84 Bessel 1841 582,105,414,1.04,0.35,-3.08,8.3
  // Deutschland WGS84 Krassovski 1940 24,-123,-94,0.02,-0.26,-0.13,1.1
  // Oesterreich (BEV) WGS84 MGI 577.326,90.129,463.920,2.423,-5.137,-1.474,-5.297
  // USA WGS84 Clarke 1866 -8,160,176,0,0,0,0

  private static final long serialVersionUID = -2514984450314088929L;

  public static ToWgs84 DHDN = new ToWgs84(598.1, 73.7, 418.2, 0.202, 0.045, -2.455, 6.7);
  public static ToWgs84 BESSEL_1841 = new ToWgs84(582.0, 105.0, 414.0, 1.04, 0.35, -3.08, 8.3);
  public static ToWgs84 WGS_84 = new ToWgs84(0, 0, 0, 0, 0, 0, 0);
  public static ToWgs84 NULL = WGS_84;

  private final double dx;
  private final double dy;
  private final double dz;
  private final double rotx;
  private final double roty;
  private final double rotz;
  private final double sc;

  private final Area area;

  public ToWgs84(
      final double dx,
      final double dy,
      final double dz,
      final double rotx,
      final double roty,
      final double rotz,
      final double sc) {
    this(null, dx, dy, dz, rotx, roty, rotz, sc);
  }

  public ToWgs84(
      final Area area,
      final double dx,
      final double dy,
      final double dz,
      final double rotx,
      final double roty,
      final double rotz,
      final double sc) {
    this.area = area;
    this.dx = dx;
    this.dy = dy;
    this.dz = dz;
    this.rotx = rotx;
    this.roty = roty;
    this.rotz = rotz;
    this.sc = sc;
  }

  public Area getArea() {
    return this.area;
  }

  public double getDX() {
    return this.dx;
  }

  public double getDY() {
    return this.dy;
  }

  public double getDZ() {
    return this.dz;
  }

  public double getRotX() {
    return this.rotx;
  }

  public double getRotY() {
    return this.roty;
  }

  public double getRotZ() {
    return this.rotz;
  }

  public double getSC() {
    return this.sc;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof ToWgs84)) {
      return false;
    }
    final ToWgs84 other = (ToWgs84) obj;
    return this.dx == other.dx //
        && this.dy == other.dy //
        && this.dz == other.dz //
        && this.rotx == other.rotx //
        && this.roty == other.roty //
        && this.rotz == other.rotz //
        && this.sc == other.sc;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ObjectUtilities.hashCode(1, prime, this.dx);
    result = ObjectUtilities.hashCode(1, prime, this.dy);
    result = ObjectUtilities.hashCode(1, prime, this.dy);
    result = ObjectUtilities.hashCode(1, prime, this.dz);
    result = ObjectUtilities.hashCode(1, prime, this.rotx);
    result = ObjectUtilities.hashCode(1, prime, this.roty);
    result = ObjectUtilities.hashCode(1, prime, this.rotz);
    return ObjectUtilities.hashCode(result, prime, this.sc);
  }
}