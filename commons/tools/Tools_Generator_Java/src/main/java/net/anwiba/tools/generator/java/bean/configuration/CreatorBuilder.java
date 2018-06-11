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

public class CreatorBuilder {

  private final String name;
  private final boolean isEnabled = true;
  private final List<Annotation> annotations = new ArrayList<>();
  private final List<Argument> arguments = new ArrayList<>();
  private Argument factory;

  public CreatorBuilder(final String name) {
    this.name = name;
  }

  public CreatorBuilder annotation(@SuppressWarnings("hiding") final Annotation annotations) {
    if (annotations == null) {
      return this;
    }
    this.annotations.add(annotations);
    return this;
  }

  public Creator build() {
    return new Creator(this.name, this.isEnabled, this.annotations, this.factory, this.arguments);
  }

  public CreatorBuilder setFactory(final Argument factory) {
    this.factory = factory;
    return this;
  }

  @SuppressWarnings("hiding")
  public CreatorBuilder addArgument(final Type type, final String name, final List<Annotation> annotations) {
    addArgument(new Argument(name, annotations, type));
    return this;
  }

  public CreatorBuilder addArgument(final Argument argument) {
    this.arguments.add(argument);
    return this;
  }
}
