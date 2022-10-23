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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public enum ServiceDatabaseSoftware implements IDatabaseSoftware {

  ORACLE("oracle.jdbc.OracleDriver",
      "jdbc:oracle:thin",
      1521, //
      SID, //
      createJdbcUrlPatterns(
          Arrays.asList(
              SID, //
              SERVICE), //
          Arrays.asList(
              "${protocol}:@${host}:${port}:${database}",
              "${protocol}:@//${host}:${port}/${database}")),
      d -> exists("oracle.spatial.geometry.JGeometry")) {

    @Override
    public IOptional<String, RuntimeException> getDefaultSchema() {
      return Optional.of("${connection}");
    }
  },
  POSTGRES("org.postgresql.Driver",
      "jdbc:postgresql",
      5432, //
      DEFAULT, //
      createJdbcUrlPatterns(
          Arrays.asList(DEFAULT), //
          Arrays.asList("${protocol}://${host}:${port}/${database}")),
      d -> exists("net.postgis.jdbc.geometry.Geometry")) {

    @Override
    public IOptional<String, RuntimeException> getDefaultSchema() {
      return Optional.of("public");
    }
  },
  H2("org.h2.Driver", // isn't supported jet
      "jdbc:h2",
      0,
      TCP, //
      createJdbcUrlPatterns(
          Arrays.asList(TCP, SSL), //
          Arrays.asList("${protocol}:tcp://${host}:${port}/${database}",
              "${protocol}:ssl://${host}:${port}/${database}")),
      d -> true) {
  },
  HANA("com.sap.db.jdbc.Driver", // isn't really tested
      "jdbc:sap",
      30015, //
      DEFAULT, //
      createJdbcUrlPatterns(
          Arrays.asList(DEFAULT), //
          //          Arrays.asList("${protocol}://${host}:${port}")),
          Arrays.asList("${protocol}://${host}:${port}/?databaseName=${database}")),
      d -> true) {
  },
  MARIADB("org.mariadb.jdbc.Driver",
      "jdbc:mariadb",
      3306, //
      DEFAULT, //
      createJdbcUrlPatterns(
          Arrays.asList(DEFAULT), //
          Arrays.asList("${protocol}://${host}:${port}/${database}")),
      d -> true) {
  },
  MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver",
      "jdbc:sqlserver",
      1433, //
      DEFAULT, //
      createJdbcUrlPatterns(
          Arrays.asList(DEFAULT), //
          Arrays.asList("${protocol}://${host}:${port};databaseName=${database}")),
      d -> true) {
  },
  MYSQL("com.mysql.cj.jdbc.Driver",
      "jdbc:mysql",
      3306, //
      DEFAULT, //
      createJdbcUrlPatterns(
          Arrays.asList(DEFAULT), //
          Arrays.asList("${protocol}://${host}:${port}/${database}")),
      d -> true) {
  };

  static Map<String, IJdbcPattern> createJdbcUrlPatterns(final List<String> names, final List<String> patterns) {
    final Map<String, IJdbcPattern> jdbcUrlPatterns = new HashMap<>();
    for (int i = 0; i < names.size(); i++) {
      jdbcUrlPatterns.put(names.get(i), new JdbcPattern(names.get(i), patterns.get(i)));
    }
    return jdbcUrlPatterns;
  }

  transient private Driver driver;

  private final String driverName;
  private final String protocol;
  private final int port;
  private final String defaultJdbcUrlPatternName;
  private final Map<String, IJdbcPattern> jdbcUrlPatterns = new HashMap<>();

  private Predicate<ServiceDatabaseSoftware> isGisSupportApplicablePredicate;

  private ServiceDatabaseSoftware(
      final String driverName,
      final String protocol,
      final int port,
      final String defaultJdbcUrlPatternName,
      final Map<String, IJdbcPattern> jdbcUrlPatterns,
      final Predicate<ServiceDatabaseSoftware> isGisSupportApplicablePredicate) {
    this.driverName = driverName;
    this.defaultJdbcUrlPatternName = defaultJdbcUrlPatternName;
    this.isGisSupportApplicablePredicate = isGisSupportApplicablePredicate;
    this.jdbcUrlPatterns.putAll(jdbcUrlPatterns);
    this.driver = ConnectionUtilities.loadDriver(driverName);
    this.protocol = protocol;
    this.port = port;
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

  private static boolean exists(final String className) {
    try {
      Class.forName(className);
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

  public static ServiceDatabaseSoftware getByName(final String name) {
    if (name == null) {
      return null;
    }
    final ServiceDatabaseSoftware[] values = values();
    for (final ServiceDatabaseSoftware serviceDatabaseSoftware : values) {
      if (name.equalsIgnoreCase(serviceDatabaseSoftware.name())) {
        return serviceDatabaseSoftware;
      }
    }
    return null;
  }
}