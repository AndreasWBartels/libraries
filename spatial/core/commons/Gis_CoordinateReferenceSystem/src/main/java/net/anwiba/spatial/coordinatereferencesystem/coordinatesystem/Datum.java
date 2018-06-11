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

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class Datum implements Serializable {

  private static final long serialVersionUID = 6701094134527395536L;

  private final String name;
  private final Spheroid spheroid;
  private final ToWgs84 toWgs84;

  private final Authority authority;

  public Datum(final String name, final Spheroid spheroid, final ToWgs84 toWgs84) {
    this(name, spheroid, toWgs84, null);
  }

  public Datum(final String name, final Spheroid spheroid, final ToWgs84 toWgs84, final Authority authority) {
    this.authority = authority;
    Ensure.ensureArgumentNotNull(name);
    Ensure.ensureArgumentNotNull(spheroid);
    this.name = name;
    this.spheroid = spheroid;
    this.toWgs84 = toWgs84;
  }

  public String getName() {
    return this.name;
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
}