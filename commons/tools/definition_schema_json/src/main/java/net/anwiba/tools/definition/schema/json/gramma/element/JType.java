/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.definition.schema.json.gramma.element;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.object.ObjectUtilities;

public class JType {

  private final String name;
  private final List<JDimension> dimensions = new ArrayList<>();
  private final String[] generics;

  @Override
  public int hashCode() {
    return 31 + ((this.name == null)
        ? 0
        : this.name.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof JType)) {
      return false;
    }
    final JType other = (JType) obj;
    return ObjectUtilities.equals(this.name, other.name) && ObjectUtilities.equals(this.dimensions, other.dimensions);
  }

  public JType(final String name, final String[] generics, final List<JDimension> list) {
    this.name = name;
    this.generics = generics;
    this.dimensions.addAll(list);
  }

  public String name() {
    return this.name;
  }

  public Iterable<JDimension> dimensions() {
    return this.dimensions;
  }

  @Override
  public String toString() {
    return MessageFormat.format("{0}{1}", this.name, this.dimensions); //$NON-NLS-1$
  }

  public boolean isArray() {
    return !this.dimensions.isEmpty();
  }

  public int dimension() {
    return this.dimensions.size();
  }

  public String[] generics() {
    return this.generics;
  }
}