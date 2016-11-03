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

public class Getter extends AbstractBeanMethod {

  private final boolean namedValueGetterEnabled;

  public Getter(
    final String name,
    final boolean isEnabled,
    final boolean namedValueGetterEnabled,
    final List<Annotation> annotations) {
    super(name, isEnabled, annotations, new ArrayList<Argument>(), new HashMap<String, List<Annotation>>());
    this.namedValueGetterEnabled = namedValueGetterEnabled;
  }

  public boolean isNamedValueGetterEnabled() {
    return this.namedValueGetterEnabled;
  }

}
