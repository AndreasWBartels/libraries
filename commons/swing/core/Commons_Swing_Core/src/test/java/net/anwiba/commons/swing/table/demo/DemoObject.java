/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.table.demo;

public class DemoObject {

  private final Integer nummer;
  private final String name;
  private final Double value;
  private final Boolean flag;

  public DemoObject(final Integer nummer, final String name, final Double value, final Boolean flag) {
    this.nummer = nummer;
    this.name = name;
    this.value = value;
    this.flag = flag;
  }

  public Integer getNummer() {
    return this.nummer;
  }

  public String getName() {
    return this.name;
  }

  public Double getValue() {
    return this.value;
  }

  public Boolean getFlag() {
    return this.flag;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("[ "); //$NON-NLS-1$
    builder.append(this.nummer);
    builder.append(", "); //$NON-NLS-1$
    builder.append(this.name);
    builder.append(", "); //$NON-NLS-1$
    builder.append(this.value);
    builder.append(", "); //$NON-NLS-1$
    builder.append(this.flag);
    builder.append("]"); //$NON-NLS-1$
    return builder.toString();
  }
}
