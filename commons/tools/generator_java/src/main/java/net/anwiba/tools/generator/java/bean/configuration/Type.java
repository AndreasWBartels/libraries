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

public class Type {

  private final String name;
  private final String[] generics;
  private final int dimension;

  public Type(final String name, final String[] generics, final int arrayDimension) {
    this.name = name;
    this.generics = generics;
    this.dimension = arrayDimension;
  }

  public String name() {
    return this.name;
  }

  public String[] generics() {
    return this.generics;
  }

  public int dimension() {
    return this.dimension;
  }
}
