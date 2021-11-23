/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.lang.object;

import java.io.Serializable;
import java.util.Objects;

public class ObjectPair<F, S> implements Serializable {

  private static final long serialVersionUID = 1L;
  private final S secondObject;
  private final F firstObject;

  public ObjectPair(final F firstObject, final S secondObject) {
    this.firstObject = firstObject;
    this.secondObject = secondObject;
  }

  public F getFirstObject() {
    return this.firstObject;
  }

  public S getSecondObject() {
    return this.secondObject;
  }

  public static <F, S> ObjectPair<F, S> of(final F firstObject, final S secondObject) {
    return new ObjectPair<F, S>(firstObject, secondObject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstObject, secondObject);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ObjectPair other = (ObjectPair) obj;
    return Objects.equals(this.firstObject, other.firstObject) && Objects.equals(this.secondObject, other.secondObject);
  }
}
