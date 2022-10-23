/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.lang.parameter;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.stream.IStream;
import net.anwiba.commons.lang.stream.Streams;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

public interface IParameters extends Serializable {

  int getNumberOfParameter();

  IParameter getParameter(int index);

  IOptional<IParameter, RuntimeException> getParameter(String name);

  String getValue(String name);

  Iterable<IParameter> parameters();

  boolean contains(String name);

  Iterable<String> getNames();

  IParameters adapt(int rowIndex, IParameter parameter);

  IParameters toLowerCase();

  IParameters toSortedByName();

  void forEach(Consumer<IParameter> consumer);

  boolean isEmpty();

  default IStream<IParameter, RuntimeException> stream() {
    return Streams.of(parameters());
  }

  default boolean contains(IParameter parameter) {
    return contains(parameter.getName()) && Objects.equals(getValue(parameter.getName()), parameter.getValue());
  }
}
