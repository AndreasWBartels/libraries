/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2009 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.io.Serializable;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class Datum implements Serializable {

  private static final long serialVersionUID = 6701094134527395536L;

  private final String name;
  private final Spheroid spheroid;
  private final ToWgs84 toWgs84;

  private final Authority authority;

  private final Area area;

  public Datum(final String name, final Spheroid spheroid, final ToWgs84 toWgs84) {
    this(name, spheroid, toWgs84, null);
  }

  public Datum(final String name, final Spheroid spheroid, final ToWgs84 toWgs84, final Authority authority) {
    this(name, spheroid, toWgs84, null, authority);
  }

  public Datum(
      final String name,
      final Spheroid spheroid,
      final ToWgs84 toWgs84,
      final Area area,
      final Authority authority) {
    Ensure.ensureArgumentNotNull(name);
    Ensure.ensureArgumentNotNull(spheroid);
    this.name = name;
    this.spheroid = spheroid;
    this.toWgs84 = toWgs84;
    this.area = area;
    this.authority = authority;
  }

  public String getName() {
    return this.name;
  }

  public Area getArea() {
    return this.area;
  }

  public Spheroid getSpheroid() {
    return this.spheroid;
  }

  public ToWgs84 getToWgs84() {
    return this.toWgs84;
  }

  public Authority getAuthority() {
    return this.authority;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(0, 31, this.spheroid);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || !(obj instanceof Datum)) {
      return false;
    }
    final Datum other = (Datum) obj;
    return ObjectUtilities.equals(this.spheroid, other.spheroid);
  }

  public Datum adapt(ToWgs84 towgs842) {
    return new Datum(name, spheroid, towgs842, area, authority);
  }
}
