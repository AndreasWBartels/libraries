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
package net.anwiba.commons.reference.url.parser;

import java.util.regex.Pattern;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.url.Authentication;
import net.anwiba.commons.reference.url.Authority;
import net.anwiba.commons.reference.url.Host;
import net.anwiba.commons.reference.url.IAuthority;

class AuthorityBuilder {

  private enum State {
    FIRST, SECOND
  }

  public class PairBuilder {

    private StringBuilder builder = null;
    private State state = State.FIRST;
    private String name = null;

    public ObjectPair<String, String> build() {
      switch (this.state) {
        case FIRST: {
          if (this.builder == null) {
            return null;
          }
          return new ObjectPair<>(this.builder.toString(), null);
        }
        case SECOND: {
          if (this.builder == null) {
            return new ObjectPair<>(this.name, null);
          }
          return new ObjectPair<>(this.name, this.builder.toString());
        }
      }
      return null;
    }

    public void add(final char character) {
      switch (this.state) {
        case FIRST: {
          if (character == ':') {
            this.name = Optional.of(this.builder).convert(b -> b.toString()).get();
            this.state = State.SECOND;
            this.builder = null;
            return;
          }
          if (this.builder == null) {
            this.builder = new StringBuilder();
          }
          this.builder.append(character);
          return;
        }
        case SECOND: {
          if (this.builder == null) {
            this.builder = new StringBuilder();
          }
          this.builder.append(character);
          return;
        }
      }
    }
  }

  private PairBuilder builder = null;
  private State state = State.FIRST;
  private ObjectPair<String, String> first;

  public IAuthority build() throws CreationException {
    switch (this.state) {
      case FIRST: {
        if (this.builder == null) {
          return null;
        }
        final ObjectPair<String, String> pair = this.builder.build();
        return new Authority(null, new Host(pair.getFirstObject(), port(pair.getSecondObject())));
      }
      case SECOND: {
        final String username = Optional
            .of(this.first.getFirstObject())
            .accept(s -> !(s == null || s.isBlank()))
            .convert(s -> s.trim())
            .get();
        final String password = Optional
            .of(this.first.getSecondObject())
            .accept(s -> !(s == null || s.isBlank()))
            .get();
        if (this.builder == null) {
          return new Authority(new Authentication(username, password), null);
        }
        return new Authority(new Authentication(username, password), host(this.builder.build()));
      }
    }
    return null;
  }

  private Host host(final ObjectPair<String, String> pair) throws CreationException {
    if (pair == null) {
      return null;
    }
    return new Host(pair.getFirstObject(), port(pair.getSecondObject()));
  }

  final private Pattern integerPattern = Pattern.compile("-?\\d+");

  private Integer port(final String string) throws CreationException {
    if (string == null || string.isBlank()) {
      return null;
    }
    if (!integerPattern.matcher(string.trim()).matches()) {
      throw new CreationException("No number '" + string + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return Optional.of(string).convert(s -> Integer.valueOf(s.trim())).get();
  }

  public void add(final char character) {
    switch (this.state) {
      case FIRST: {
        if (character == '@') {
          this.first = Optional.of(this.builder).convert(b -> b.build()).get();
          this.state = State.SECOND;
          this.builder = null;
          return;
        }
        if (this.builder == null) {
          this.builder = new PairBuilder();
        }
        this.builder.add(character);
        return;
      }
      case SECOND: {
        if (this.builder == null) {
          this.builder = new PairBuilder();
        }
        this.builder.add(character);
        return;
      }
    }
  }
}
