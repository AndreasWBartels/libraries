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
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.io.Serializable;

import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class VerticalDatum implements Serializable {

  private static final long serialVersionUID = 1L;
  private final Authority authority;
  private final String name;
  private final int datumType;
  private final Area area;

  public VerticalDatum(final Authority authority, final String name, final int datumType, final Area area) {
    this.authority = authority;
    this.name = name;
    this.datumType = datumType;
    this.area = area;
  }

  public String getName() {
    return this.name;
  }

  public Area getArea() {
    return this.area;
  }

  public Authority getAuthority() {
    return this.authority;
  }

  public int getDatumType() {
    return this.datumType;
  }

}
