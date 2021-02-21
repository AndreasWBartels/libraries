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

import net.anwiba.commons.lang.object.ObjectUtilities;

public class Accuracy implements Serializable {

  private static final long serialVersionUID = 1L;
  private final double value;

  public Accuracy(final double value) {
    this.value = value;
  }

  public String getName() {
    return "ACCURACY"; //$NON-NLS-1$
  }

  public double getValue() {
    return this.value;
  }

  @Override
  public int hashCode() {
    return Double.hashCode(this.value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof Accuracy)) {
      return false;
    }
    final Accuracy other = (Accuracy) obj;
    return ObjectUtilities.equals(this.value, other.value);
  }
}
