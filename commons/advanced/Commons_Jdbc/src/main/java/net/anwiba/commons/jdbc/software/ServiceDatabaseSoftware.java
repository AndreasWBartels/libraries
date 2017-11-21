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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.jdbc.DatabaseUtilities;

public enum ServiceDatabaseSoftware implements IDatabaseSoftware {

  ORACLE("oracle.jdbc.OracleDriver", //$NON-NLS-1$
      "jdbc:oracle:thin", //$NON-NLS-1$
      1521, //
      SID, //
      createJdbcUrlPatterns(
          Arrays.asList(
              SID, //
              SERVICE), //
          Arrays.asList(
              "${protocol}:@${host}:${port}:${database}", //$NON-NLS-1$
              "${protocol}:@//${host}:${port}/${database}" //$NON-NLS-1$
          )), "oracle.spatial.geometry.JGeometry") { //$NON-NLS-1$
    @Override
    public <T, E extends Exception> T accept(final IDatabaseSoftwareVisitor<T, E> visitor) throws E {
      return visitor.visitOracle();
    }
  },
  POSTGRES("org.postgresql.Driver", //$NON-NLS-1$
      "jdbc:postgresql", //$NON-NLS-1$
      5432, //
      DEFAULT, //
      createJdbcUrlPatterns(
          Arrays.asList(DEFAULT), //
          Arrays.asList("${protocol}://${host}:${port}/${database}")), //$NON-NLS-1$
      "org.postgis.Geometry") { //$NON-NLS-1$
    @Override
    public <T, E extends Exception> T accept(final IDatabaseSoftwareVisitor<T, E> visitor) throws E {
      return visitor.visitPostgres();
    }
  },
  HANA("com.sap.db.jdbc.Driver", //$NON-NLS-1$
      "jdbc:sap", //$NON-NLS-1$
      30015, //
      DEFAULT, //
      createJdbcUrlPatterns(
          Arrays.asList(DEFAULT), //
          Arrays.asList("${protocol}://${host}:${port}")), //$NON-NLS-1$
      "java.lang.Object") { //$NON-NLS-1$
    @Override
    public <T, E extends Exception> T accept(final IDatabaseSoftwareVisitor<T, E> visitor) throws E {
      return visitor.visitHana();
    }
  };

  static Map<String, IJdbcPattern> createJdbcUrlPatterns(final List<String> names, final List<String> patterns) {
    final Map<String, IJdbcPattern> jdbcUrlPatterns = new HashMap<>();
    for (int i = 0; i < names.size(); i++) {
      jdbcUrlPatterns.put(names.get(i), new JdbcPattern(names.get(i), patterns.get(i)));
    }
    return jdbcUrlPatterns;
  }

  transient private Driver driver;

  private final Map<String, IJdbcPattern> jdbcUrlPatterns = new HashMap<>();
  private final String protocol;
  private final int port;
  private final String geometryClassName;
  private final String driverName;
  private final String defaultJdbcUrlPatternName;

  private ServiceDatabaseSoftware(
      final String driverName,
      final String protocol,
      final int port,
      final String defaultJdbcUrlPatternName,
      final Map<String, IJdbcPattern> jdbcUrlPatterns,
      final String geometryClassName) {
    this.driverName = driverName;
    this.defaultJdbcUrlPatternName = defaultJdbcUrlPatternName;
    this.jdbcUrlPatterns.putAll(jdbcUrlPatterns);
    this.driver = DatabaseUtilities.loadDriver(driverName);
    this.protocol = protocol;
    this.port = port;
    this.geometryClassName = geometryClassName;
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

  public abstract <T, E extends Exception> T accept(final IDatabaseSoftwareVisitor<T, E> visitor) throws E;

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

  public static ServiceDatabaseSoftware getByUrl(final String url) {
    if (url == null) {
      return null;
    }
    final ServiceDatabaseSoftware[] values = values();
    for (final ServiceDatabaseSoftware serviceDatabaseSoftware : values) {
      if (url.toLowerCase().startsWith(serviceDatabaseSoftware.getProtocol())) {
        return serviceDatabaseSoftware;
      }
    }
    return null;
  }

  @Override
  public String getDefaultJdbcUrlPatternName() {
    return this.defaultJdbcUrlPatternName;
  }

  @Override
  public IJdbcPattern getJdbcUrlPattern(final String name) {
    return this.jdbcUrlPatterns.get(name);
  }

  @Override
  public List<IJdbcPattern> getJdbcUrlPatterns() {
    return new ArrayList<>(this.jdbcUrlPatterns.values());
  }
}