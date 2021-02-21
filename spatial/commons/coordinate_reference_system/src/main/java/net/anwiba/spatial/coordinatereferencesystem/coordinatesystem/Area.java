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
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class Area implements Serializable {

  private static final long serialVersionUID = 1L;
  private final String name;
  private final IEnvelope envelope;
  private final Authority authority;

  public Area(final String name, final IEnvelope envelope) {
    this(name, envelope, null);
  }

  public Area(final String name, final IEnvelope envelope, final Authority authority) {
    this.envelope = envelope;
    this.authority = authority;
    Ensure.ensureArgumentNotNull(name);
    this.name = name.trim().toLowerCase();
  }

  public String getName() {
    return this.name;
  }

  public Authority getAuthority() {
    return this.authority;
  }

  public IEnvelope getEnvelope() {
    return this.envelope;
  }

  @Override
  public int hashCode() {
    return this.envelope.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof Area)) {
      return false;
    }
    final Area other = (Area) obj;
    return ObjectUtilities.equals(this.envelope, other.envelope);
  }
}
