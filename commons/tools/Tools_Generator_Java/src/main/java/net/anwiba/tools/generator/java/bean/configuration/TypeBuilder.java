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
package net.anwiba.tools.generator.java.bean.configuration;

import java.util.ArrayList;
import java.util.List;

public class TypeBuilder {

  private final String name;
  private int dimension;
  private final List<String> generics = new ArrayList<>();

  public TypeBuilder(final String name) {
    this.name = name;
  }

  public TypeBuilder dimension(@SuppressWarnings("hiding") final int dimension) {
    this.dimension = dimension;
    return this;
  }

  public TypeBuilder generic(final String generic) {
    this.generics.add(generic);
    return this;
  }

  public Type build() {
    return new Type(this.name, this.generics.toArray(new String[this.generics.size()]), this.dimension);
  }

}
