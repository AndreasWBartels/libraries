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
    return ObjectUtilities.hashCode(this.value);
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
