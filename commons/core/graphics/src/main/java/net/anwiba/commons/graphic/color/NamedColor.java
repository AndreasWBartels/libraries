/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.graphic.color;

import java.awt.color.ColorSpace;
import java.util.Objects;

public record NamedColor(String name, IColor color) implements IColor {

  @Override
  public ColorSpace getColorSpace() {
    return color.getColorSpace();
  }

  @Override
  public float[] getColorCompoments() {
    return color.getColorCompoments();
  }

  @Override
  public float getOpacity() {
    return color.getOpacity();
  }

  @Override
  public float getComponent(int index) {
    return color.getComponent(index);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    return (obj instanceof NamedColor other
        && Objects.equals(this.name, other.name())
        && color.equals(obj))
        || color.equals(obj);
  }

  @Override
  public int hashCode() {
    return color.hashCode();
  }

  @Override
  public String toString() {
    return name();
  }}
