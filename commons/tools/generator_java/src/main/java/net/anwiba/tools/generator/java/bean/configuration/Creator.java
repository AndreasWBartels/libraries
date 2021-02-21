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

import java.util.HashMap;
import java.util.List;

public class Creator extends AbstractMethod {

  private final String parameter;
  private final Argument factory;

  protected Creator(
      final String name,
      @SuppressWarnings("unused") final boolean isEnabled,
      final List<Annotation> annotations,
      final Argument factory,
      final List<Argument> arguments) {
    super(name, annotations, arguments, create(arguments.get(0).name(), arguments.get(0).annotations()), null);
    this.factory = factory;
    this.parameter = arguments.get(0).name();
  }

  private static HashMap<String, List<Annotation>> create(
      final String parameter,
      final List<Annotation> parameterAnnotations) {
    final HashMap<String, List<Annotation>> map = new HashMap<>();
    map.put(parameter, parameterAnnotations);
    return map;
  }

  public String parameter() {
    return this.parameter;
  }

  public Argument factory() {
    return this.factory;
  }
}
