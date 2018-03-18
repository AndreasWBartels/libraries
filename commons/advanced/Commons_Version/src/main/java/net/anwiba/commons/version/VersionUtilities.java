/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.version;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;

public class VersionUtilities {

  public static IVersion getVersion(final URL resource) {
    if (resource == null) {
      return Version.DUMMY;
    }
    final Properties properties = new Properties();
    try (InputStream inputstream = resource.openStream();
        InputStreamReader reader = new InputStreamReader(inputstream, "UTF-8"); //$NON-NLS-1$
    ) {
      properties.load(reader);
      final int major = getIntProperty(properties, "version.mayor"); //$NON-NLS-1$
      final int minor = getIntProperty(properties, "version.minor"); //$NON-NLS-1$
      final int step = getIntProperty(properties, "version.step"); //$NON-NLS-1$
      final ReleaseState releaseState = getReleaseState(properties);
      final ProductState productState = getProductState(properties);
      final int buildCount = getIntProperty(properties, "build.count"); //$NON-NLS-1$
      final Date date = getDate(properties);
      return new Version(major, minor, releaseState, step, productState, date, buildCount);
    } catch (final IOException exception) {
      // nothing to do
    }
    return Version.DUMMY;
  }

  private static Date getDate(final Properties properties) {
    final String property = properties.getProperty("build.date"); //$NON-NLS-1$
    if (property == null) {
      return Version.defaultDate;
    }
    try {
      return new SimpleDateFormat("yyyy.MM.dd HH:mm").parse(property); //$NON-NLS-1$
    } catch (final ParseException exception) {
      return Version.defaultDate;
    }
  }

  private static ProductState getProductState(final Properties properties) {
    final String property = properties.getProperty("version.state.product"); //$NON-NLS-1$
    if (property == null) {
      return ProductState.EXPERIMENTAL;
    }
    try {
      return ProductState.valueOf(property);
    } catch (final IllegalArgumentException e) {
      return ProductState.EXPERIMENTAL;
    }
  }

  private static ReleaseState getReleaseState(final Properties properties) {
    final String property = properties.getProperty("version.state.release"); //$NON-NLS-1$
    if (property == null) {
      return ReleaseState.RELEASE;
    }
    try {
      return ReleaseState.valueOf(property);
    } catch (final IllegalArgumentException e) {
      return ReleaseState.RELEASE;
    }
  }

  private static int getIntProperty(final Properties properties, final String key) {
    final String property = properties.getProperty(key);
    if (property == null) {
      return 0;
    }
    return Integer.parseInt(property);
  }

  public enum ParseState {
    UNDEFIND, MAJOR, MINOR, RELEASESTATE, STEP, PRODUCTSTATE, YEAR, MONTH, DAY, HOUR, MIN, COUNT;
  }

  public static IVersion valueOf(final String string) {
    return new VersionParser().parse(string);
  }

  public static class VersionParser {

    public IVersion parse(final String string) {
      if (string == null) {
        return null;
      }
      boolean lastCharaterWasWhiteSpace = false;
      ParseState lastState = ParseState.UNDEFIND;
      ParseState state = ParseState.UNDEFIND;
      StringBuilder builder = new StringBuilder();
      final VersionBuilder versionBuilder = new VersionBuilder();
      for (final char c : string.toCharArray()) {
        final ParseState tmpState = state;
        if (c == '.') {
          state = point(state, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        }
        if (c == ':') {
          state = colon(state, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        }
        if (Character.isWhitespace(c) || c == '-') {
          if (lastCharaterWasWhiteSpace) {
            continue;
          }
          state = checkWhitespace(state, builder, versionBuilder);
          lastCharaterWasWhiteSpace = true;
        }
        if (Character.isDigit(c)) {
          state = digit(state, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        }
        if (Character.isLetter(c)) {
          state = letter(state, builder, versionBuilder);
          lastCharaterWasWhiteSpace = false;
        }
        if (!state.equals(tmpState)) {
          lastState = state;
          builder = new StringBuilder();
        }
        if (c == '.' || c == ':' || Character.isWhitespace(c)) {
          continue;
        }
        builder.append(c);
      }
      state = resolve(state, builder, versionBuilder);
      return versionBuilder.build();
    }

    private ParseState resolve(final ParseState state, final StringBuilder builder, final VersionBuilder versionBuilder) {
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
        case COUNT: {
          versionBuilder.setCount(builder.toString());
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
          versionBuilder.setHour(builder.toString());
          return ParseState.COUNT;
        }
        case MIN: {
          versionBuilder.setMinute(builder.toString());
          return ParseState.COUNT;
        }
        case COUNT: {
          throw new IllegalArgumentException();
        }
      }
      throw new UnreachableCodeReachedException();
    }

    private ParseState colon(final ParseState state, final StringBuilder builder, final VersionBuilder versionBuilder) {
      switch (state) {
        case UNDEFIND: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case MAJOR: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case MINOR: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case RELEASESTATE: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case STEP: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case PRODUCTSTATE: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case YEAR: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case MONTH: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case DAY: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case HOUR: {
          versionBuilder.setHour(builder.toString());
          return ParseState.MIN;
        }
        case MIN: {
          versionBuilder.setMinute(builder.toString());
          return ParseState.MIN;
        }
        case COUNT: {
          throw new IllegalArgumentException();
        }
      }
      throw new UnreachableCodeReachedException();
    }

    private ParseState point(final ParseState state, final StringBuilder builder, final VersionBuilder versionBuilder) {
      switch (state) {
        case UNDEFIND: {
          versionBuilder.setMajor(builder.toString());
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
          throw new IllegalArgumentException();
        }
        case STEP: {
          throw new IllegalArgumentException();
        }
        case PRODUCTSTATE: {
          throw new IllegalArgumentException();
        }
        case YEAR: {
          throw new IllegalArgumentException();
        }
        case MONTH: {
          throw new IllegalArgumentException();
        }
        case DAY: {
          throw new IllegalArgumentException();
        }
        case HOUR: {
          throw new IllegalArgumentException();
        }
        case MIN: {
          throw new IllegalArgumentException();
        }
        case COUNT: {
          throw new IllegalArgumentException();
        }
      }
      throw new UnreachableCodeReachedException();
    }

    private ParseState letter(final ParseState state, final StringBuilder builder, final VersionBuilder versionBuilder) {
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
          throw new IllegalArgumentException();
        }
        case DAY: {
          throw new IllegalArgumentException();
        }
        case HOUR: {
          throw new IllegalArgumentException();
        }
        case MIN: {
          throw new IllegalArgumentException();
        }
        case COUNT: {
          throw new IllegalArgumentException();
        }
      }
      throw new UnreachableCodeReachedException();
    }

    private ParseState digit(final ParseState state, final StringBuilder builder, final VersionBuilder versionBuilder) {
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
      throw new IllegalArgumentException();
    }
  }

  public static String getText(final IVersion version) {
    String string = String.valueOf(version.getMajor());
    string += "."; //$NON-NLS-1$
    string += String.valueOf(version.getMinor());
    final ReleaseState releaseState = version.getReleaseState();
    if (releaseState != ReleaseState.RELEASE) {
      string += String.valueOf(releaseState.getAcronym());
    } else {
      string += "."; //$NON-NLS-1$
    }
    string += String.valueOf(version.getStep());
    final ProductState productState = version.getProductState();
    if (productState != ProductState.STABLE) {
      string += " "; //$NON-NLS-1$
      string += String.valueOf(productState.getAcronym());
    }
    return string;
  }

  public static String getTextLong(final IVersion version) {
    String string = getText(version);
    string += " "; //$NON-NLS-1$
    string += version.getBuildCount();
    string += " "; //$NON-NLS-1$
    string += new SimpleDateFormat("dd.MM.yyyy HH:mm").format(version.getDate()); //$NON-NLS-1$
    return string;
  }
}
