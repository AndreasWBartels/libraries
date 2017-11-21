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

import java.sql.Driver;
import java.util.Arrays;
import java.util.List;

import net.anwiba.commons.jdbc.DatabaseUtilities;

public enum FileDatabaseSoftware implements IDatabaseSoftware {
  DERBY("org.sqlite.JDBC", //$NON-NLS-1$
      "jdbc:derby:memory", //$NON-NLS-1$
      0, //
      "${protocol}:${database};create=true", //$NON-NLS-1$
      "java.lang.Object") { //$NON-NLS-1$
  },
  SQLITE("org.sqlite.JDBC", //$NON-NLS-1$
      "jdbc:sqlite", //$NON-NLS-1$
      0, //
      "${protocol}:${database}", //$NON-NLS-1$
      "java.lang.Object") { //$NON-NLS-1$
  };

  private transient Driver driver;
  private final String protocol;
  private final int port;
  private final String geometryClassName;
  private final String driverName;
  private IJdbcPattern jdbcPattern;

  private FileDatabaseSoftware(
      final String driverName,
      final String protocol,
      final int port,
      final String jdbcUrlPattern,
      final String geometryClassName) {
    this.driverName = driverName;
    this.driver = DatabaseUtilities.loadDriver(driverName);
    this.protocol = protocol;
    this.port = port;
    this.geometryClassName = geometryClassName;
    this.jdbcPattern = new JdbcPattern(getDefaultJdbcUrlPatternName(), jdbcUrlPattern);
  }

  @Override
  public Driver getDriver() {
    if (this.driver == null) {
      final Driver _driver = DatabaseUtilities.loadDriver(this.driverName);
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
    try {
      Class.forName(this.geometryClassName);
      return true;
    } catch (final ClassNotFoundException exception) {
      return false;
    }
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

  @Override
  public List<IJdbcPattern> getJdbcUrlPatterns() {
    return Arrays.asList(this.jdbcPattern);
  }
}