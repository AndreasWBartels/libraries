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

import net.anwiba.tools.generator.java.bean.value.ValueType;

public class AnnotationBuilder {

  private final String name;
  private final Parameters parameters = new Parameters();

  AnnotationBuilder(final String name) {
    this.name = name;
  }

  public Annotation build() {
    return new Annotation(this.name, this.parameters);
  }

  public AnnotationBuilder parameter(final String name, final String value) {
    this.parameters.add(name, value, ValueType.STRING);
    return this;
  }

  public AnnotationBuilder parameter(final String name, final int value) {
    this.parameters.add(name, Integer.valueOf(value), ValueType.INTEGER);
    return this;
  }

  @SuppressWarnings("rawtypes")
  public AnnotationBuilder parameter(final String name, final Class value) {
    this.parameters.add(name, value, ValueType.CLASS);
    return this;
  }
}
