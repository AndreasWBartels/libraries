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
// Copyright (c) 2015 by Andreas W. Bartels

package net.anwiba.spatial.coordinatereferencesystem;

import java.util.Iterator;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.utilities.string.StringUtilities;

public class CoordinateReferenceSystemProvider implements ICoordinateReferenceSystemProvider {

  private static ILogger logger = Logging.getLogger(CoordinateReferenceSystemProvider.class.getName());
  private final ICoordinateReferenceSystemFactory coordinateReferenceSystemFactory;
  private final CoordinateReferenceSystemRegistry registry;

  public CoordinateReferenceSystemProvider(
      final ICoordinateReferenceSystemFactory coordinateReferenceSystemFactory,
      final CoordinateReferenceSystemRegistry registry) {
    this.coordinateReferenceSystemFactory = coordinateReferenceSystemFactory;
    this.registry = registry;
  }

  @Override
  public ICoordinateReferenceSystem getSystem(final String srsName) {
    if (StringUtilities.isNullOrEmpty(srsName)) {
      return ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM;
    }
    if (!this.registry.contains(srsName)) {
      try {
        final Authority authority = Authority.valueOf(srsName);
        if (authority == null) {
          return ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM;
        }
        if (this.registry.contains(authority.getValue())) {
          return this.registry.getSystem(authority.getValue());
        }
        return this.coordinateReferenceSystemFactory.createFromId(String.valueOf(authority.getNumber()));
      } catch (final Exception exception) {
        logger.log(ILevel.DEBUG, "Couldn't find reference system '" + srsName + "'", exception); //$NON-NLS-1$//$NON-NLS-2$
        return ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM;
      }
    }
    return this.registry.getSystem(srsName);
  }

  @Override
  public boolean isEmpty() {
    return this.registry.isEmpty();
  }

  @Override
  public Iterator<ICoordinateReferenceSystem> iterator() {
    return this.registry.iterator();
  }

}
