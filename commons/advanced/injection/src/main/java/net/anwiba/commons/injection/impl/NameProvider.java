/*
 * #%L
 * 
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.injection.impl;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import net.anwiba.commons.annotation.Named;

public class NameProvider {

  public String getName(final AnnotatedElement annotatedElement, final String defaultName) {
    return Optional
        .ofNullable(annotatedElement.getAnnotation(Named.class))
        .map(a -> a.value())
        .map(s -> s.trim().isEmpty() ? defaultName : s)
        .orElseGet(() -> null);
  }
} 
