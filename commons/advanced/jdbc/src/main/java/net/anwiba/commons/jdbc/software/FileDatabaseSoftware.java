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
package net.anwiba.commons.jdbc.software;

import net.anwiba.commons.jdbc.connection.ConnectionUtilities;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

import java.sql.Driver;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public enum FileDatabaseSoftware implements IDatabaseSoftware {
  DERBY(
      "org.apache.derby.iapi.jdbc.AutoloadedDriver", //$NON-NLS-1$
      "jdbc:derby", //$NON-NLS-1$
      0, //
      "${protocol}:${database}", //$NON-NLS-1$
      d -> false) { //$NON-NLS-1$
  },
  SQLITE(
      "org.sqlite.JDBC", //$NON-NLS-1$
      "jdbc:sqlite", //$NON-NLS-1$
      0, //
      // https://sqlite.org/uri.html
      "${protocol}:${database}", //$NON-NLS-1$
      d -> true) { //$NON-NLS-1$
  },
  SPATIALITE(
      "org.spatialite.JDBC", //$NON-NLS-1$
      "jdbc:spatialite", //$NON-NLS-1$
      0, //
      "${protocol}:${database}", //$NON-NLS-1$
      d -> true) { //$NON-NLS-1$
  },
  H2(
      "org.h2.Driver", //$NON-NLS-1$
      "jdbc:h2", //$NON-NLS-1$
      0, //
      "${protocol}:${database}", //$NON-NLS-1$
      d -> true) { //$NON-NLS-1$

    @Override
    public IOptional<String, RuntimeException> getDefaultSchema() {
      return Optional.of("PUBLIC");
    }
  },
  HSQLDB(
      "org.hsqldb.jdbc.JDBCDriver", //$NON-NLS-1$
      "jdbc:hsqldb", //$NON-NLS-1$
      0, //
      "${protocol}:${database}", //$NON-NLS-1$
      d -> false) { //$NON-NLS-1$
  };

  private transient Driver driver;
  private final String protocol;
  private final int port;
  private final String driverName;
  private IJdbcPattern jdbcPattern;
  private Predicate<FileDatabaseSoftware> isGisSupportApplicablePredicate;

  private FileDatabaseSoftware(
      final String driverName,
      final String protocol,
      final int port,
      final String jdbcUrlPattern,
      final Predicate<FileDatabaseSoftware> isGisSupportApplicablePredicate) {
    this.driverName = driverName;
    this.isGisSupportApplicablePredicate = isGisSupportApplicablePredicate;
    this.driver = ConnectionUtilities.loadDriver(driverName);
    this.protocol = protocol;
    this.port = port;
    this.jdbcPattern = new JdbcPattern(getDefaultJdbcUrlPatternName(), jdbcUrlPattern);
  }

  @Override
  public Driver getDriver() {
    if (this.driver == null) {
      final Driver _driver = ConnectionUtilities.loadDriver(this.driverName);
      this.driver = _driver;
    }
    return this.driver;
  }

  @Override
  public String getDriverName() {
    return this.driverName;
  }

  @Override
  public String getProtocol() {
    return this.protocol;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  @Override
  public boolean isApplicable() {
    return getDriver() != null;
  }

  @Override
  public boolean isGisSupportApplicable() {
    return this.isGisSupportApplicablePredicate.test(this);
  }

  public static FileDatabaseSoftware getByUrl(final String url) {
    if (url == null) {
      return null;
    }
    final FileDatabaseSoftware[] values = values();
    for (final FileDatabaseSoftware serviceDatabaseSoftware : values) {
      if (url.toLowerCase().startsWith(serviceDatabaseSoftware.getProtocol())) {
        return serviceDatabaseSoftware;
      }
    }
    return null;
  }

  @Override
  public String getDefaultJdbcUrlPatternName() {
    return DEFAULT;
  }

  @Override
  public IJdbcPattern getJdbcUrlPattern(final String name) {
    return this.jdbcPattern;
  }

  @Override
  public List<IJdbcPattern> getJdbcUrlPatterns() {
    return Arrays.asList(this.jdbcPattern);
  }

  public static FileDatabaseSoftware getByDriverNamw(final String driverName) {
    if (driverName == null) {
      return null;
    }
    final FileDatabaseSoftware[] values = values();
    for (final FileDatabaseSoftware serviceDatabaseSoftware : values) {
      if (driverName.equals(serviceDatabaseSoftware.getDriverName())) {
        return serviceDatabaseSoftware;
      }
    }
    return null;
  }

  public static FileDatabaseSoftware getByName(final String name) {
    if (name == null) {
      return null;
    }
    final FileDatabaseSoftware[] values = values();
    for (final FileDatabaseSoftware serviceDatabaseSoftware : values) {
      if (name.equalsIgnoreCase(serviceDatabaseSoftware.name())) {
        return serviceDatabaseSoftware;
      }
    }
    return null;
  }

  public static FileDatabaseSoftware getByProtocol(final String protocol) {
    if (protocol == null) {
      return null;
    }
    final FileDatabaseSoftware[] values = values();
    for (final FileDatabaseSoftware serviceDatabaseSoftware : values) {
      if (protocol.equalsIgnoreCase(serviceDatabaseSoftware.getProtocol())) {
        return serviceDatabaseSoftware;
      }
    }
    return null;
  }
}