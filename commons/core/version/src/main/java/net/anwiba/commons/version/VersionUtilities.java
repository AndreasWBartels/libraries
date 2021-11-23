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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.version;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

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
      final ZonedDateTime date = getDate(properties);
      return new Version(major, minor, releaseState, step, productState, date, buildCount);
    } catch (final IOException exception) {
      // nothing to do
    }
    return Version.DUMMY;
  }

  private static ZonedDateTime getDate(final Properties properties) {
    final String property = properties.getProperty("build.date"); //$NON-NLS-1$
    if (property == null) {
      return Version.defaultDate;
    }
    try {
      return ZonedDateTime.of(LocalDateTime
          .parse(
              property,
              DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm", Locale.getDefault())),
          Clock.systemUTC().getZone());
    } catch (final DateTimeParseException exception) {
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

  public static IVersion valueOf(final String string) {
    return new VersionParser().parse(string);
  }

  public static String getText(final IVersion version) {
    String string = internal(version);
    final ProductState productState = version.getProductState();
    if (productState != ProductState.STABLE) {
      string += " "; //$NON-NLS-1$
      string += String.valueOf(productState.getAcronym());
    }
    return string;
  }

  private static String internal(final IVersion version) {
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
    return string;
  }

  public static String getTextLong(final IVersion version) {
    String string = internal(version);
    final ProductState productState = version.getProductState();
    string += " "; //$NON-NLS-1$
    string += String.valueOf(productState.getAcronym());
    string += " "; //$NON-NLS-1$
    string += version.getBuildCount();
    string += " "; //$NON-NLS-1$
    string += version
        .getDate()
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm", Locale.getDefault())); //$NON-NLS-1$
    return string;
  }

  public static String getTextShort(final IVersion version) {
    return internal(version);
  }

  public static boolean isGreaterThan(final IVersion version, final IVersion other) {
    if (version.getMajor() > other.getMajor()) {
      return true;
    }
    if (version.getMinor() > other.getMinor()) {
      return true;
    }
    if (version.getStep() > other.getStep()) {
      return true;
    }
    if (version.getBuildCount() > other.getBuildCount()) {
      return true;
    }
    if (version.getReleaseState().ordinal() > other.getReleaseState().ordinal()) {
      return true;
    }
    if (version.getReleaseState().ordinal() > other.getProductState().ordinal()) {
      return true;
    }
    if (version.getDate().isAfter(other.getDate())) {
      return true;
    }
    return false;
  }

  public static boolean isGreaterEquals(final IVersion version, final IVersion other) {
    if (Objects.equals(version, other)) {
      return true;
    }
    if (version.getMajor() < other.getMajor()) {
      return false;
    } else if (version.getMajor() > other.getMajor()) {
      return true;
    }
    if (version.getMinor() < other.getMinor()) {
      return false;
    } else if (version.getMinor() > other.getMinor()) {
      return true;
    }
    if (version.getStep() < other.getStep()) {
      return false;
    } else if (version.getStep() > other.getStep()) {
      return true;
    }
    if (version.getBuildCount() < other.getBuildCount()) {
      return false;
    } else if (version.getBuildCount() > other.getBuildCount()) {
      return true;
    }
    if (version.getReleaseState().ordinal() < other.getReleaseState().ordinal()) {
      return false;
    } else if (version.getReleaseState().ordinal() > other.getReleaseState().ordinal()) {
      return true;
    }
    if (version.getReleaseState().ordinal() < other.getProductState().ordinal()) {
      return false;
    } else if (version.getReleaseState().ordinal() > other.getProductState().ordinal()) {
      return true;
    }
    if (version.getDate().isBefore(other.getDate())) {
      return false;
    } else if (version.getDate().isAfter(other.getDate())) {
      return true;
    }
    return true;
  }
}
