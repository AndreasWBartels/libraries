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
package net.anwiba.spatial.coordinatereferencesystem;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.functional.IEqualComperator;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public class CoordinateReferenceSystemModel implements ICoordinateReferenceSystemModel {

  private final List<ICoordinateReferenceSystemListener> coordinateReferenceSystemListeners = new ArrayList<>();
  private ICoordinateReferenceSystem coordinateReferenceSystem =
      ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM;
  private final IEqualComperator<ICoordinateReferenceSystem> comperator;

  public CoordinateReferenceSystemModel() {
    this(new CoordinateReferenceSystemEqualComperator());
  }

  public CoordinateReferenceSystemModel(final IEqualComperator<ICoordinateReferenceSystem> comperator) {
    this.comperator = comperator;
  }

  @Override
  public void addCoordinateReferenceSystemListener(final ICoordinateReferenceSystemListener listener) {
    synchronized (this.coordinateReferenceSystemListeners) {
      this.coordinateReferenceSystemListeners.add(listener);
    }
  }

  @Override
  public ICoordinateReferenceSystem getCoordinateReferenceSystem() {
    synchronized (this) {
      return this.coordinateReferenceSystem;
    }
  }

  @Override
  public synchronized void setCoordinateReferenceSystem(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    final ICoordinateReferenceSystem currentCoordinateReferenceSystem = this.coordinateReferenceSystem;
    final ICoordinateReferenceSystem internalCoordinateReferenceSystem = coordinateReferenceSystem == null
        ? ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM
        : coordinateReferenceSystem;
    synchronized (this) {
      if (this.comperator.equals(this.coordinateReferenceSystem, internalCoordinateReferenceSystem)) {
        return;
      }
      this.coordinateReferenceSystem = internalCoordinateReferenceSystem;
    }
    fireCoordinateReferenceSystemChanged(currentCoordinateReferenceSystem, internalCoordinateReferenceSystem);
  }

  @Override
  public void removeCoordinateReferenceSystemListener(final ICoordinateReferenceSystemListener listener) {
    synchronized (this.coordinateReferenceSystemListeners) {
      this.coordinateReferenceSystemListeners.remove(listener);
    }
  }

  private void fireCoordinateReferenceSystemChanged(
      final ICoordinateReferenceSystem oldCoordinateReferenceSystem,
      final ICoordinateReferenceSystem newCoordinateReferenceSystem) {
    List<ICoordinateReferenceSystemListener> listeners;
    synchronized (this.coordinateReferenceSystemListeners) {
      listeners = IterableUtilities.asList(this.coordinateReferenceSystemListeners);
    }
    for (final ICoordinateReferenceSystemListener listener : listeners) {
      listener.coordinateReferenceSystemChanged(oldCoordinateReferenceSystem, newCoordinateReferenceSystem);
    }
  }
}
