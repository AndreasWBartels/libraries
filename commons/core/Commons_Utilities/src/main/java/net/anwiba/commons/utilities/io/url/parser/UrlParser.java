/*
 * #%L
 * *
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

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.io.url.IUrl;
import net.anwiba.commons.utilities.io.url.Url;
import net.anwiba.commons.utilities.string.StringUtilities;

public class UrlParser {

  private enum State {
    PROTOCOL, RESOURCE, AUTHORITY, PATH, QUERY, FRAGMENT
  }

  public IUrl parse(final String string) throws CreationException {

    if (StringUtilities.isNullOrTrimmedEmpty(string)) {
      return null;
    }

    final ProtocolBuilder protocolBuilder = new ProtocolBuilder();
    final AuthorityBuilder authorityBuilder = new AuthorityBuilder();
    final PathBuilder pathBuilder = new PathBuilder();
    final QueryBuilder queryBuilder = new QueryBuilder();
    final StringBuilder fragmentBuilder = new StringBuilder();
    State state = State.PROTOCOL;

    for (int i = 0; i < string.length(); i++) {
      final char character = string.charAt(i);
      switch (state) {
        case PROTOCOL: {
          state = protocol(protocolBuilder, character);
          break;
        }
        case RESOURCE: {
          state = resource(pathBuilder, character);
          break;
        }
        case AUTHORITY: {
          state = authority(authorityBuilder, pathBuilder, character);
          break;
        }
        case PATH: {
          state = path(pathBuilder, character);
          break;
        }
        case QUERY: {
          state = query(queryBuilder, character);
          break;
        }
        case FRAGMENT: {
          fragmentBuilder.append(character);
          break;
        }
      }
    }
    return new Url(
        protocolBuilder.build(),
        authorityBuilder.build(),
        pathBuilder.build(),
        queryBuilder.build(),
        Optional.of(fragmentBuilder.toString()).accept(f -> !StringUtilities.isNullOrEmpty(f)).get());
  }

  private State query(final QueryBuilder queryBuilder, final char character) {
    switch (character) {
      case '#': {
        return State.FRAGMENT;
      }
      default: {
        queryBuilder.add(character);
        return State.QUERY;
      }
    }
  }

  private State path(final PathBuilder pathBuilder, final char character) {
    switch (character) {
      case '?': {
        return State.QUERY;
      }
      case '#': {
        return State.FRAGMENT;
      }
      default: {
        pathBuilder.add(character);
        return State.PATH;
      }
    }
  }

  private State authority(
      final AuthorityBuilder authorityBuilder,
      final PathBuilder pathBuilder,
      final char character) {
    switch (character) {
      case '/': {
        pathBuilder.add(character);
        return State.PATH;
      }
      case '?': {
        return State.QUERY;
      }
      case '#': {
        return State.FRAGMENT;
      }
      default: {
        authorityBuilder.add(character);
        return State.AUTHORITY;
      }
    }
  }

  private State resource(final PathBuilder pathBuilder, final char character) {
    switch (character) {
      case '/': {
        return State.AUTHORITY;
      }
      case '?': {
        return State.QUERY;
      }
      case '#': {
        return State.FRAGMENT;
      }
      default: {
        pathBuilder.add(character);
        return State.PATH;
      }
    }
  }

  private State protocol(final ProtocolBuilder protocolBuilder, final char character) {
    switch (character) {
      case '/': {
        return State.RESOURCE;
      }
      default: {
        protocolBuilder.add(character);
        return State.PROTOCOL;
      }
    }
  }
}
