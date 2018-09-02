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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Setter extends AbstractBeanMethod {

  private final boolean isMultiValue;
  private final boolean isSingleValue;

  public Setter(
      final String name,
      final boolean isEnabled,
      final boolean isSingleValue,
      final boolean isMultiValue,
      final List<Annotation> annotations,
      final Argument argument,
      final Map<String, List<Annotation>> map) {
    super(name, isEnabled, annotations, Arrays.asList(argument), map);
    this.isSingleValue = isSingleValue;
    this.isMultiValue = isMultiValue;
  }

  public boolean isMultiValue() {
    return this.isMultiValue;
  }

  public boolean isSingleValue() {
    return this.isSingleValue;
  }
}
