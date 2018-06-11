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


package net.anwiba.spatial.coordinatereferencesystem;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.anwiba.commons.utilities.string.StringUtilities;

public class CoordinateReferenceSystemRegistry
    implements
    ICoordinateReferenceSystemRegistry,
    ICoordinateReferenceSystemProvider {

  private final Map<String, ICoordinateReferenceSystem> systems = new LinkedHashMap<>();

  public CoordinateReferenceSystemRegistry() {
  }

  @Override
  public ICoordinateReferenceSystem getSystem(final String srsName) {
    if (StringUtilities.isNullOrEmpty(srsName)) {
      return ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM;
    }
    if (!this.systems.containsKey(srsName)) {
      final Authority authority = Authority.valueOf(srsName);
      if (authority == null) {
        return ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM;
      }
      if (this.systems.containsKey(authority.getValue())) {
        return this.systems.get(authority.getValue());
      }
      return ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM;
    }
    return this.systems.get(srsName);
  }

  @Override
  public boolean containts(final String srsName) {
    if (!this.systems.containsKey(srsName)) {
      final Authority authority = Authority.valueOf(srsName);
      if (authority == null) {
        return false;
      }
      if (this.systems.containsKey(authority.getValue())) {
        return true;
      }
      return false;
    }
    return true;
  }

  @Override
  public void add(final String srsName, final ICoordinateReferenceSystem coordinateReferenceSystem) {
    this.systems.put(srsName, coordinateReferenceSystem);
  }

  @Override
  public Iterator<ICoordinateReferenceSystem> iterator() {
    return this.systems.values().iterator();
  }

  @Override
  public boolean isEmpty() {
    return this.systems.isEmpty();
  }

}
