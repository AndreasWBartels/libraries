/*
 * #%L
 * anwiba commons advanced
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

import java.util.ArrayList;
import java.util.List;

public class JTypeBuilder {

  private final List<JDimension> dimensions = new ArrayList<>();
  private final List<String> generics = new ArrayList<>();
  private String name = "value"; //$NON-NLS-1$

  public void add(final JDimension dimension) {
    if (dimension == null) {
      throw new IllegalArgumentException();
    }
    this.dimensions.add(dimension);
  }

  public JType build() {
    final String typeName = withoutGenerics(this.name);
    return new JType(typeName, this.generics.toArray(new String[this.generics.size()]), this.dimensions);
  }

  public static String withoutGenerics(final String name) {
    if (name.indexOf('<') == -1 && name.indexOf('>') == -1) {
      return name;
    }
    return name.substring(0, name.indexOf('<'));
  }

  public void name(@SuppressWarnings("hiding") final String name) {
    this.name = name;
  }

  public void generic(final String generic) {
    this.generics.add(generic);
  }
}
