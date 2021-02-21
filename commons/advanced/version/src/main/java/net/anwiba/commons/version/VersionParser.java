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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.version;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;

public class VersionParser {

  public enum ParseState {
    UNDEFIND, MAJOR, MINOR, RELEASESTATE, STEP, PRODUCTSTATE, COUNT, YEAR, MONTH, DAY, HOUR, MIN;
  }

  public IVersion parse(final String string) {
    try {
      if (string == null) {
        return null;
      }
      boolean lastCharaterWasWhiteSpace = false;
      ParseState state = ParseState.UNDEFIND;
      StringBuilder builder = new StringBuilder();
      final StringBuilder donebuilder = new StringBuilder();
      final VersionBuilder versionBuilder = new VersionBuilder();
      for (final char c : string.toCharArray()) {
        donebuilder.append(c);
        final ParseState previousState = state;
        if (c == '.') {
          state = point(previousState, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        } else if (c == '-') {
          state = hyphen(previousState, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        } else if (c == ':') {
          state = colon(previousState, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        } else if (Character.isWhitespace(c)) {
          if (lastCharaterWasWhiteSpace) {
            continue;
          }
          state = checkWhitespace(previousState, builder, versionBuilder);
          lastCharaterWasWhiteSpace = true;
        } else if (Character.isDigit(c)) {
          state = digit(previousState, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        } else if (Character.isLetter(c)) {
          state = letter(previousState, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        }
        if (!state.equals(previousState)) {
          builder = new StringBuilder();
        }
        if (c == '.' || c == ':' || c == '-' || Character.isWhitespace(c)) {
          continue;
        }
        builder.append(c);
      }
      state = resolve(state, builder, versionBuilder);
      return versionBuilder.build();

    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Couldn't parse " + string, e);
    }
  }

  private ParseState resolve(
      final ParseState state,
      final StringBuilder builder,
      final VersionBuilder versionBuilder) {
    switch (state) {
      case UNDEFIND: {
        return state;
      }
      case MAJOR: {
        versionBuilder.setMajor(builder.toString());
        return state;
      }
      case MINOR: {
        versionBuilder.setMinor(builder.toString());
        return state;
      }
      case RELEASESTATE: {
        versionBuilder.setReleaseState(builder.toString());
        return state;
      }
      case STEP: {
        versionBuilder.setStep(builder.toString());
        return state;
      }
      case PRODUCTSTATE: {
        versionBuilder.setProductState(builder.toString());
        return state;
      }
      case COUNT: {
        versionBuilder.setCount(builder.toString());
        return state;
      }
      case YEAR: {
        versionBuilder.setYear(builder.toString());
        return state;
      }
      case MONTH: {
        versionBuilder.setMonth(builder.toString());
        return state;
      }
      case DAY: {
        versionBuilder.setDay(builder.toString());
        return state;
      }
      case HOUR: {
        versionBuilder.setHour(builder.toString());
        return state;
      }
      case MIN: {
        versionBuilder.setMinute(builder.toString());
        return state;
      }
    }
    throw new UnreachableCodeReachedException();
  }

  private ParseState checkWhitespace(
      final ParseState state,
      final StringBuilder builder,
      final VersionBuilder versionBuilder) {
    switch (state) {
      case UNDEFIND: {
        return state;
      }
      case MAJOR: {
        versionBuilder.setMajor(builder.toString());
        return ParseState.RELEASESTATE;
      }
      case MINOR: {
        versionBuilder.setMinor(builder.toString());
        return ParseState.RELEASESTATE;
      }
      case RELEASESTATE: {
        versionBuilder.setReleaseState(builder.toString());
        return ParseState.STEP;
      }
      case STEP: {
        versionBuilder.setStep(builder.toString());
        return ParseState.PRODUCTSTATE;
      }
      case PRODUCTSTATE: {
        versionBuilder.setProductState(builder.toString());
        return ParseState.COUNT;
      }
      case COUNT: {
        versionBuilder.setCount(builder.toString());
        return ParseState.YEAR;
      }
      case YEAR: {
        versionBuilder.setYear(builder.toString());
        return ParseState.HOUR;
      }
      case MONTH: {
        versionBuilder.setMonth(builder.toString());
        return ParseState.HOUR;
      }
      case DAY: {
        versionBuilder.setDay(builder.toString());
        return ParseState.HOUR;
      }
      case HOUR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case MIN: {
        throw new IllegalArgumentException(toString(state, builder));
      }
    }
    throw new UnreachableCodeReachedException();
  }

  private ParseState hyphen(
      final ParseState state,
      final StringBuilder builder,
      final VersionBuilder versionBuilder) {
    switch (state) {
      case UNDEFIND: {
        return state;
      }
      case MAJOR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case MINOR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case RELEASESTATE: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case STEP: {
        versionBuilder.setStep(builder.toString());
        return ParseState.RELEASESTATE;
      }
      case PRODUCTSTATE: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case COUNT: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case YEAR: {
        versionBuilder.setYear(builder.toString());
        return ParseState.MONTH;
      }
      case MONTH: {
        versionBuilder.setMonth(builder.toString());
        return ParseState.DAY;
      }
      case DAY: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case HOUR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case MIN: {
        throw new IllegalArgumentException(toString(state, builder));
      }
    }
    throw new UnreachableCodeReachedException();
  }

  private ParseState colon(
      final ParseState state,
      final StringBuilder builder,
      final VersionBuilder versionBuilder) {
    switch (state) {
      case UNDEFIND: {
        return state;
      }
      case MAJOR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case MINOR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case RELEASESTATE: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case STEP: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case PRODUCTSTATE: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case COUNT: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case YEAR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case MONTH: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case DAY: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case HOUR: {
        versionBuilder.setHour(builder.toString());
        return ParseState.MIN;
      }
      case MIN: {
        throw new IllegalArgumentException();
      }
    }
    throw new UnreachableCodeReachedException();
  }

  private ParseState point(
      final ParseState state,
      final StringBuilder builder,
      final VersionBuilder versionBuilder) {
    switch (state) {
      case UNDEFIND: {
        return ParseState.MINOR;
      }
      case MAJOR: {
        versionBuilder.setMajor(builder.toString());
        return ParseState.MINOR;
      }
      case MINOR: {
        versionBuilder.setMinor(builder.toString());
        return ParseState.STEP;
      }
      case RELEASESTATE: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case STEP: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case PRODUCTSTATE: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case COUNT: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case YEAR: {
        versionBuilder.setYear(builder.toString());
        return ParseState.MONTH;
      }
      case MONTH: {
        versionBuilder.setMonth(builder.toString());
        return ParseState.DAY;
      }
      case DAY: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case HOUR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case MIN: {
        throw new IllegalArgumentException(toString(state, builder));
      }
    }
    throw new UnreachableCodeReachedException();
  }

  private ParseState letter(
      final ParseState state,
      final StringBuilder builder,
      final VersionBuilder versionBuilder) {
    switch (state) {
      case UNDEFIND: {
        return ParseState.RELEASESTATE;
      }
      case MAJOR: {
        versionBuilder.setMajor(builder.toString());
        return ParseState.RELEASESTATE;
      }
      case MINOR: {
        versionBuilder.setMinor(builder.toString());
        return ParseState.RELEASESTATE;
      }
      case RELEASESTATE: {
        return state;
      }
      case STEP: {
        versionBuilder.setStep(builder.toString());
        return ParseState.PRODUCTSTATE;
      }
      case PRODUCTSTATE: {
        return state;
      }
      case YEAR: {
        throw new IllegalArgumentException();
      }
      case MONTH: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case DAY: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case HOUR: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case MIN: {
        throw new IllegalArgumentException(toString(state, builder));
      }
      case COUNT: {
        throw new IllegalArgumentException(toString(state, builder));
      }
    }
    throw new UnreachableCodeReachedException();
  }

  private ParseState digit(
      final ParseState state,
      final StringBuilder builder,
      final VersionBuilder versionBuilder) {
    switch (state) {
      case UNDEFIND: {
        return ParseState.MAJOR;
      }
      case MAJOR: {
        return state;
      }
      case MINOR: {
        return state;
      }
      case RELEASESTATE: {
        versionBuilder.setReleaseState(builder.toString());
        return ParseState.STEP;
      }
      case STEP: {
        return state;
      }
      case PRODUCTSTATE: {
        versionBuilder.setProductState(builder.toString());
        return ParseState.YEAR;
      }
      case YEAR: {
        return state;
      }
      case MONTH: {
        return state;
      }
      case DAY: {
        return state;
      }
      case HOUR: {
        return state;
      }
      case MIN: {
        return state;
      }
      case COUNT: {
        return state;
      }
    }
    throw new IllegalArgumentException(toString(state, builder));
  }

  private String toString(final ParseState state, final StringBuilder builder) {
    return "state: " + state + " part:" + builder.toString();
  }

}
