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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class AbstractMethod {

  protected final String name;
  private final List<Annotation> annotations = new ArrayList<>();
  private final List<Argument> arguments = new ArrayList<>();
  private final Map<String, List<Annotation>> parameterAnnotations = new HashMap<>();
  private final String comment;

  public AbstractMethod(
      final String name,
      final List<Annotation> annotations,
      final List<Argument> arguments,
      final Map<String, List<Annotation>> parameterAnnotations,
      final String comment) {
    this.name = name;
    this.comment = comment;
    this.annotations.addAll(annotations);
    this.arguments.addAll(arguments);
    this.parameterAnnotations.putAll(parameterAnnotations);
  }

  public List<Annotation> annotations() {
    return this.annotations;
  }

  public String name() {
    return this.name;
  }

  public Iterable<Argument> arguments() {
    return this.arguments;
  }

  public IOptional<String, RuntimeException> comment() {
    return Optional.of(this.comment);
  }
}