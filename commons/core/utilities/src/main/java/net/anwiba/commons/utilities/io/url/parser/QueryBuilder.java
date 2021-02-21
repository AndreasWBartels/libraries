/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.utilities.io.url.parser;

import java.util.LinkedList;
import java.util.List;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.Parameter;

class QueryBuilder {

  private enum State {
    NAME, VALUE
  }

  class ParameterBuilder {

    private StringBuilder builder = null;
    private State state = State.NAME;
    private String name = null;

    public IParameter build() {
      switch (this.state) {
        case NAME: {
          if (this.builder == null) {
            return null;
          }
          return Parameter.of(this.builder.toString(), null);
        }
        case VALUE: {
          if (this.builder == null) {
            return Parameter.of(this.name, null);
          }
          return Parameter.of(this.name, this.builder.toString());
        }
      }
      return null;
    }

    public void add(final char character) {
      switch (this.state) {
        case NAME: {
          if (character == '=') {
            this.name = Optional.of(this.builder).convert(b -> b.toString()).get();
            this.state = State.VALUE;
            this.builder = null;
            return;
          }
          if (this.builder == null) {
            this.builder = new StringBuilder();
          }
          this.builder.append(character);
          return;
        }
        case VALUE: {
          if (this.builder == null) {
            this.builder = new StringBuilder();
          }
          this.builder.append(character);
          return;
        }
      }
    }
  }

  final private List<IParameter> parameters = new LinkedList<>();
  private ParameterBuilder parameterBuilder;

  public List<IParameter> build() {
    Optional.of(this.parameterBuilder).convert(b -> b.build()).consume(p -> this.parameters.add(p));
    return this.parameters;
  }

  public void add(final char character) {
    if (character == '&') {
      Optional.of(this.parameterBuilder).convert(b -> b.build()).consume(p -> this.parameters.add(p));
      this.parameterBuilder = null;
      return;
    }
    if (this.parameterBuilder == null) {
      this.parameterBuilder = new ParameterBuilder();
    }
    this.parameterBuilder.add(character);
  }
}
