/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.jdbc.connection;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

public class ConnectionUtilities {

  public static String hash(final Connection connection) {
    return hash("#C%12x", connection);
  }

  public static String hash(final Statement statement) {
    return hash("#S%12x", statement);
  }

  public static String hash(final ResultSet resultSet) {
    return hash("#R%12x", resultSet);
  }

  public static String hash(final String prefix, final Object object) {
    if (object == null) {
      return nullHash(prefix);
    }
    return String.format(prefix, object.hashCode()).replaceAll(" ", "0");
  }

  public static String nullHash() {
    return nullHash("#N%12x");
  }

  public static String nullHash(final String prefix) {
    return String.format(prefix, 0).replaceAll(" ", "0");
  }

  public static String toDebugString(final Object object) {
    if (object == null) {
      return null;
    }
    if (object.getClass().isArray()) {
      return object.getClass().getSimpleName();
    }
    return String.valueOf(object);
  }

  public static Driver loadDriver(final String driverName) {
    try {
      final Enumeration<Driver> drivers = DriverManager.getDrivers();
      while (drivers.hasMoreElements()) {
        final Driver driver = drivers.nextElement();
        if (driver.getClass().getName().equals(driverName)) {
          return driver;
        }
      }
      @SuppressWarnings("unchecked")
      final Class<Driver> driverClass = (Class<Driver>) Class.forName(driverName);
      if (driverClass != null) {
        final Driver driver = driverClass.getDeclaredConstructor().newInstance();
        DriverManager.registerDriver(driver);
        return driver;
      }
    } catch (final ClassNotFoundException exception) {
      // nothing to do
    } catch (final InstantiationException exception) {
      // nothing to do
    } catch (final IllegalAccessException exception) {
      // nothing to do
    } catch (final SQLException exception) {
      // nothing to do
    } catch (IllegalArgumentException e) {
      // nothing to do
    } catch (InvocationTargetException e) {
      // nothing to do
    } catch (NoSuchMethodException e) {
      // nothing to do
    } catch (SecurityException e) {
      // nothing to do
    }
    return null;
  }

  public static Connection createConnection(
      final String url,
      final String user,
      final String password)
      throws SQLException {
    Connection connection = DriverManager.getConnection(url, user, password);
    return new WrappingConnection(connection);
  }

}
