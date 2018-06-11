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
 
package net.anwiba.spatial.geometry.extract;

import net.anwiba.commons.lang.object.ObjectUtilities;

public class Path implements IPath {

  private final IStep step;

  public Path(final IStep step) {
    this.step = step;
  }

  @Override
  public IStep getStep() {
    return this.step;
  }

  @Override
  public boolean hasStep() {
    return this.step != null;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof IPath)) {
      return false;
    }
    final IPath other = (IPath) obj;
    return other.hasStep() == hasStep() && ObjectUtilities.equals(other.getStep(), getStep());
  }

  @Override
  public int hashCode() {
    if (!hasStep()) {
      return 0;
    }
    IStep currentStep = this.step;
    int hasCode = currentStep.hashCode();
    while (currentStep.hasNext()) {
      currentStep = currentStep.next();
      hasCode = hasCode + 17 * currentStep.hashCode();
    }
    return hasCode;
  }
}
