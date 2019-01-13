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
package net.anwiba.commons.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import net.anwiba.commons.jdbc.connection.IDatabaseConnector;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.constraint.Constraint;
import net.anwiba.commons.jdbc.constraint.ConstraintType;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.result.IResults;
import net.anwiba.commons.jdbc.result.ResultSetToResultAdapter;
import net.anwiba.commons.jdbc.result.ResultSetToResultsAdapter;
import net.anwiba.commons.jdbc.value.IDatabaseValue;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IClosableIterator;
import net.anwiba.commons.lang.functional.ICloseable;
import net.anwiba.commons.lang.functional.ICloseableConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.functional.IInterruptableFunction;
import net.anwiba.commons.lang.functional.IInterruptableProcedure;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.functional.IWatcher;
import net.anwiba.commons.lang.number.ComparableNumber;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class DatabaseUtilities {

  private static ILogger logger = Logging.getLogger(DatabaseUtilities.class.getName());

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
        final Driver driver = driverClass.newInstance();
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
    }
    return null;
  }

  public static Connection createConnection(
      final String url,
      final String user,
      final String password,
      final boolean isReadOnly)
      throws SQLException {
    final Connection connection = DriverManager.getConnection(url, user, password);
    connection.setReadOnly(isReadOnly);
    return connection;
  }

  public static Connection createConnection(final String url, final String user, final String password)
      throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  public static void close(final Connection connection) {

    try {
      if (connection != null) {
        connection.close();
      }
    } catch (final SQLException e) {
      // nothing todo
    }
  }

  public static SQLException close(final Connection connection, final SQLException exception) {
    if (connection == null) {
      return exception;
    }
    try {
      //      if (connection.isClosed()) {
      //        return exception;
      //      }
      connection.close();
      return exception;
    } catch (final SQLException sqlException) {
      if (exception == null) {
        return sqlException;
      }
      exception.addSuppressed(sqlException);
      return exception;
    }
  }

  public static SQLException close(final Statement statement, final SQLException exception) {
    if (statement == null) {
      return exception;
    }
    try {
      //      if (statement.isClosed()) {
      //        return exception;
      //      }
      statement.close();
      return exception;
    } catch (final SQLException sqlException) {
      if (exception == null) {
        return sqlException;
      }
      exception.addSuppressed(sqlException);
      return exception;
    }
  }

  public static SQLException close(final ResultSet resultSet, final SQLException exception) {
    if (resultSet == null) {
      return exception;
    }
    try {
      //      if (resultSet.isClosed()) {
      //        return exception;
      //      }
      resultSet.close();
      return exception;
    } catch (final SQLException sqlException) {
      if (exception == null) {
        return sqlException;
      }
      exception.addSuppressed(sqlException);
      return exception;
    }
  }

  public static SQLException close(final IResults results, final SQLException exception) {
    if (results == null) {
      return exception;
    }
    try {
      //      if (results.isClosed()) {
      //        return exception;
      //      }
      results.close();
      return exception;
    } catch (final SQLException sqlException) {
      if (exception == null) {
        return sqlException;
      }
      exception.addSuppressed(sqlException);
      return exception;
    }
  }

  public static void close(final IResults results) {

    try {
      if (results != null) {
        results.close();
      }
    } catch (final SQLException e) {
      // nothing todo
    }
  }

  public static void close(final Statement statement) {

    try {
      if (statement != null) {
        statement.close();
      }
    } catch (final SQLException e) {
      // nothing todo
    }
  }

  public static void close(final ResultSet resultSet) {

    try {
      if (resultSet != null) {
        resultSet.close();
      }
    } catch (final SQLException e) {
      // nothing todo
    }
  }

  public static String getStatementString(
      final Connection connection,
      final double beforVersion,
      final String statement,
      final String defaultStatement)
      throws SQLException {
    final double version = getVersionAsDouble(connection);
    if (version < beforVersion) {
      return statement;
    }
    return defaultStatement;
  }

  public static double getVersionAsDouble(final Connection connection) throws SQLException {
    final int mayor = DatabaseUtilities.getMajorVersion(connection);
    final int minor = DatabaseUtilities.getMinorVersion(connection);
    final double version = mayor + Double.parseDouble("0." + String.valueOf(minor)); //$NON-NLS-1$
    return version;
  }

  public static String getProduct(final Connection connection) throws SQLException {
    return connection.getMetaData().getDatabaseProductName();
  }

  public static String getVersion(final Connection connection) throws SQLException {
    return connection.getMetaData().getDatabaseProductVersion();
  }

  public static int getMajorVersion(final Connection connection) throws SQLException {
    return connection.getMetaData().getDatabaseMajorVersion();
  }

  public static int getMinorVersion(final Connection connection) throws SQLException {
    return connection.getMetaData().getDatabaseMinorVersion();
  }

  public static Map<String, Constraint> readConstraints(
      final Connection connection,
      final String selectStatement,
      final String schemaName,
      final String tableName)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Query: Schema " + schemaName + " table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$
    logger.log(ILevel.DEBUG, "Query: " + selectStatement); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(selectStatement)) {
      final Map<String, Constraint> constraints = new HashMap<>();
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      if (statement.execute()) {
        try (ResultSet resultSet = statement.getResultSet();) {
          while (resultSet.next()) {
            final String columnName = resultSet.getString(1);
            final String constraintName = resultSet.getString(2);
            final ConstraintType constraintType = ConstraintType.getTypeById(resultSet.getString(3));
            final String condition = resultSet.getString(4);
            final Constraint constraint = getConstraint(constraints, constraintName, constraintType, condition);
            constraint.add(columnName);
          }
        }
      }
      return constraints;
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + selectStatement + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private static Constraint getConstraint(
      final Map<String, Constraint> constraints,
      final String constraintName,
      final ConstraintType constraintType,
      final String condition) {
    Constraint constraint = constraints.get(constraintName);
    if (constraint == null) {
      constraint = new Constraint(constraintName, constraintType, condition);
      constraints.put(constraintName, constraint);
    }
    return constraint;
  }

  public static void dropTable(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    final String qualifiedTableName = schemaName == null ? tableName : schemaName + "." + tableName; //$NON-NLS-1$
    final String statementString = "DROP TABLE " + qualifiedTableName; //$NON-NLS-1$
    execute(connection, statementString);
  }

  public static void dropIndex(final Connection connection, final String schemaName, final String indexName)
      throws SQLException {
    final String qualifiedIndexName = schemaName == null ? indexName : schemaName + "." + indexName; //$NON-NLS-1$
    final String statementString = "DROP INDEX " + qualifiedIndexName; //$NON-NLS-1$
    execute(connection, statementString);
  }

  public static String getOwner(final Connection connection, final String owner) throws SQLException {
    return owner == null ? connection.getMetaData().getUserName() : owner;
  }

  public static <T> List<T> results(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IConverter<IResult, T, SQLException> function)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
      return results(connection, statementString, function);
    }
  }

  public static <T> List<T> results(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> function)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
      return results(connection, statementString, prepareProcedure, function);
    }
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IConverter<IResult, T, SQLException> function)
      throws SQLException {
    return results(connection, statementString, (IProcedure<PreparedStatement, SQLException>) statement -> {
      // nothing to do
    }, function);
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> resultProcedure)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString)) {
      prepareProcedure.execute(statement);
      final List<T> resultList = new ArrayList<>();
      if (statement.execute()) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          final IResult result = new ResultSetToResultAdapter(resultSet);
          while (resultSet.next()) {
            final T object = resultProcedure.convert(result);
            if (object == null) {
              continue;
            }
            resultList.add(object);
          }
        }
      }
      return resultList;
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static <T> List<T> results(
      final IFactory<IBlock<RuntimeException>, IWatcher, RuntimeException> cancelWatcherFactory,
      final Connection connection,
      final String statementString,
      final IConverter<IResult, T, SQLException> resultFunction)
      throws SQLException {
    return results(cancelWatcherFactory, connection, statementString, s -> {
    }, resultFunction);
  }

  public static <T> List<T> results(
      final IFactory<IBlock<RuntimeException>, IWatcher, RuntimeException> cancelWatcherFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> resultConverter)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString)) {
      try (final ICloseable<RuntimeException> cancler = cancelWatcherFactory.create(() -> {
        try {
          statement.cancel();
        } catch (final SQLException exception) {
        }
      })) {
        prepareProcedure.execute(statement);
        final List<T> resultList = new ArrayList<>();
        if (statement.execute()) {
          try (final ResultSet resultSet = statement.getResultSet()) {
            final IResult result = new ResultSetToResultAdapter(resultSet);
            while (resultSet.next()) {
              final T object = resultConverter.convert(result);
              if (object == null) {
                continue;
              }
              resultList.add(object);
            }
          }
        }
        return resultList;
      }
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static ResultSet resultSet(final Connection connection, final String string) throws SQLException {
    return resultSet(connection, string, value -> {
    });
  }

  @SuppressWarnings("resource")
  public static <T> ResultSet resultSet(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    try {
      logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
      final PreparedStatement statement = connection.prepareStatement(statementString);
      prepareProcedure.execute(statement);
      if (statement.execute()) {
        return statement.getResultSet();
      }
      return null;
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static String stringResult(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
      return stringResult(connection, statementString, prepareProcedure);
    }
  }

  public static String stringResult(final Connection connection, final String statementString, final Object... values)
      throws SQLException {
    return stringResult(connection, statementString, setterProcedur(values));
  }

  public static String stringResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    final IFunction<IResult, String, SQLException> resultFunction = new IFunction<IResult, String, SQLException>() {

      @Override
      public String execute(final IResult value) throws SQLException {
        if (value == null) {
          return null;
        }
        return value.getString(1);
      }
    };
    return stringResult(connection, statementString, prepareProcedure, resultFunction);
  }

  public static String stringResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, String, SQLException> resultFunction)
      throws SQLException {
    return result(connection, statementString, prepareProcedure, resultFunction);
  }

  public static Long longResult(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
      return longResult(connection, statementString, prepareProcedure);
    }
  }

  public static Long longResult(final Connection connection, final String statementString, final Object... values)
      throws SQLException {
    return longResult(connection, statementString, setterProcedur(values));
  }

  public static Long longResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    final IConverter<IResult, Long, SQLException> resultFunction = new IConverter<IResult, Long, SQLException>() {

      @Override
      public Long convert(final IResult value) throws SQLException {
        if (value == null) {
          return null;
        }
        return value.getLong(1);
      }
    };
    return longResult(connection, statementString, prepareProcedure, resultFunction);
  }

  public static Long longResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, Long, SQLException> resultFunction)
      throws SQLException {
    return result(connection, statementString, prepareProcedure, resultFunction);
  }

  public static boolean booleanResult(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
      return booleanResult(connection, statementString, prepareProcedure);
    }
  }

  public static boolean booleanResult(final Connection connection, final String statementString, final Object... values)
      throws SQLException {
    return booleanResult(connection, statementString, setterProcedur(values));
  }

  public static boolean booleanResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return result(
        connection,
        statementString,
        prepareProcedure,
        new IConverter<IOptional<IResult, SQLException>, Boolean, SQLException>() {

          @Override
          public Boolean convert(final IOptional<IResult, SQLException> value) throws SQLException {
            return value.convert(v -> v.getBoolean(1, false)).getOr(() -> Boolean.FALSE);
          }
        });
  }

  public static <T> T result(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultFunction)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
      return result(connection, statementString, prepareProcedure, resultFunction);
    }
  }

  public static <T> T result(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultFunction)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription);) {
      return result(connection, statementString, resultFunction);
    }
  }

  public static <T> T result(
      final Connection connection,
      final String statementString,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> function)
      throws SQLException {
    return result(connection, statementString, (IProcedure<PreparedStatement, SQLException>) statement -> {
      // nothing to do
    }, function);
  }

  public static <T> T result(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultFunction)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString)) {
      prepareProcedure.execute(statement);
      if (statement.execute()) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          final IResult result = new ResultSetToResultAdapter(resultSet);
          if (resultSet.next()) {
            final T value = resultFunction.convert(Optional.of(SQLException.class, result));
            if (resultSet.next()) {
              throw new SQLException("Statement result isn't unique '" + statementString + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return value;
          }
        }
      }
      return resultFunction.convert(Optional.<IResult, SQLException> empty(SQLException.class));
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IInterruptableFunction<IResult, T, SQLException> resultFunction)
      throws SQLException,
      CanceledException {
    return results(b -> () -> {
    }, connection, statementString, s -> {
    }, resultFunction);
  }

  public static <T> List<T> results(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IInterruptableFunction<IResult, T, SQLException> resultFunction)
      throws SQLException,
      CanceledException {
    try (Connection connection = connector.connectReadOnly(connectionDescription);) {
      return results(b -> () -> {
      }, connection, statementString, s -> {
      }, resultFunction);
    }
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IInterruptableProcedure<PreparedStatement, SQLException> prepareClosure,
      final IInterruptableFunction<IResult, T, SQLException> resultFunction)
      throws SQLException,
      CanceledException {
    return results(b -> () -> {
    }, connection, statementString, prepareClosure, resultFunction);
  }

  public static <T> List<T> results(
      final IFactory<IBlock<RuntimeException>, IWatcher, RuntimeException> cancelWatcherFactory,
      final Connection connection,
      final String statementString,
      final IInterruptableProcedure<PreparedStatement, SQLException> prepareClosure,
      final IInterruptableFunction<IResult, T, SQLException> resultProcedure)
      throws SQLException,
      CanceledException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString)) {
      try (final ICloseable<RuntimeException> cancler = cancelWatcherFactory.create(() -> {
        try {
          statement.cancel();
        } catch (final SQLException exception) {
        }
      })) {

        prepareClosure.execute(statement);
        final List<T> resultList = new ArrayList<>();
        if (statement.execute()) {
          try (final ResultSet resultSet = statement.getResultSet()) {
            final IResult result = new ResultSetToResultAdapter(resultSet);
            while (resultSet.next()) {
              final T object = resultProcedure.execute(result);
              if (object != null) {
                resultList.add(object);
              }
            }
          }
        }
        return resultList;
      }
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static <T> T aggregate(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IConverter<Iterable<IResult>, T, SQLException> function)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription);) {
      return aggregate(connection, statementString, function);
    }
  }

  public static <T> T aggregate(
      final Connection connection,
      final String statementString,
      final IConverter<Iterable<IResult>, T, SQLException> function)
      throws SQLException {
    return aggregate(connection, statementString, statement -> {
      // nothing to do
    }, function);
  }

  public static <T> T aggregate(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<Iterable<IResult>, T, SQLException> resultProcedure)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString)) {
      prepareProcedure.execute(statement);
      if (statement.execute()) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          final List<SQLException> exceptions = new ArrayList<>(4);
          final IResult result = new ResultSetToResultAdapter(resultSet);
          final Iterable<IResult> iterable = () -> new Iterator<IResult>() {

            @Override
            public boolean hasNext() {
              try {
                return resultSet.next();
              } catch (final SQLException exception) {
                exceptions.add(exception);
                return false;
              }
            }

            @Override
            public IResult next() {
              return result;
            }
          };
          try {
            return resultProcedure.convert(iterable);
          } catch (SQLException exception) {
            for (final SQLException sqlException : exceptions) {
              sqlException.addSuppressed(exception);
              exception = sqlException;
            }
            throw exception;
          }
        }
      }
      return resultProcedure.convert(new ArrayList<IResult>());
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static boolean execute(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IProcedure<ResultSet, SQLException> resultProcedure)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString)) {
      prepareProcedure.execute(statement);
      if (statement.execute()) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          resultProcedure.execute(resultSet);
          return true;
        }
      }
      return statement.getUpdateCount() > 0;
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static final boolean execute(final Connection connection, final String statementString, final Object... values)
      throws SQLException {
    return execute(connection, statementString, setterProcedur(values), each -> {
      // nothing to do
    });
  }

  public static final boolean call(final Connection connection, final String statementString, final Object... values)
      throws SQLException {
    return call(connection, statementString, setterProcedur(values), each -> {
      // nothing to do
    });
  }

  public static final boolean call(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> procedure)
      throws SQLException {
    return call(connection, statementString, procedure, each -> {
      // nothing to do
    });
  }

  public static boolean call(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IProcedure<ResultSet, SQLException> resultProcedure)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (CallableStatement statement = connection.prepareCall(statementString)) {
      prepareProcedure.execute(statement);
      if (statement.execute()) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          resultProcedure.execute(resultSet);
          return true;
        }
      }
      return statement.getUpdateCount() > 0;
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  @SuppressWarnings("nls")
  public static IProcedure<PreparedStatement, SQLException> setterProcedur(final Object... objects) {
    return statement -> {
      for (int i = 0; i < objects.length; ++i) {
        logger.log(ILevel.DEBUG, "  value: " + toDebugString(objects[i]));
        final Object adjustValue = adjustValue(objects[i]);
        if (adjustValue == null) {
          statement.setNull(i + 1, 0);
          continue;
        }
        statement.setObject(i + 1, adjustValue);
      }
    };
  }

  public static Object adjustValue(final Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof java.util.Date
        && !(value instanceof java.sql.Date || value instanceof java.sql.Timestamp || value instanceof java.sql.Time)) {
      return new java.sql.Timestamp(((java.util.Date) value).getTime());
    }
    if (value instanceof ComparableNumber) {
      return adjustValue(value);
    }
    if (value instanceof Double) {
      if (Double.isNaN((double) value)) {
        return null;
      }
      return value;
    }
    return value;
  }

  public static int count(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final Object... values)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription);) {
      return count(connection, statementString, values);
    }
  }

  public static int count(final Connection connection, final String statementString, final Object... values)
      throws SQLException {
    return count(connection, statementString, setterProcedur(values));
  }

  public static Long next(final Connection connection, final String nextValueStatement) throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + nextValueStatement); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(nextValueStatement)) {
      if (statement.execute()) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          if (resultSet.next()) {
            final Number number = (Number) resultSet.getObject(1);
            if (number == null) {
              return null;
            }
            return number.longValue();
          }
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + nextValueStatement + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static int count(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString)) {
      return count(statement, prepareProcedure);
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static int count(
      final PreparedStatement statement,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    prepareProcedure.execute(statement);
    if (statement.execute()) {
      try (final ResultSet resultSet = statement.getResultSet()) {
        if (resultSet.next()) {
          final Object object = resultSet.getObject(1);
          if (!(object instanceof Number)) {
            return 0;
          }
          final Number number = (Number) object;
          return number.intValue();
        }
      }
    }
    return 0;
  }

  public static boolean contains(
      final Connection connection,
      final String statementString,
      final String columnName,
      final Object value)
      throws SQLException {
    final Boolean count = aggregate(connection, statementString, results -> {
      for (final IResult result : results) {
        final Object object = result.getObject(columnName);
        if (ObjectUtilities.equals(value, object)) {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    });
    return count.booleanValue();
  }

  public static boolean execute(final Connection connection, final String statementString) throws SQLException {
    return execute(connection, statementString, (IProcedure<PreparedStatement, SQLException>) statement -> {
      // nothing to do
    });
  }

  public static final boolean execute(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> procedure)
      throws SQLException {
    return execute(connection, statementString, procedure, each -> {
      // nothing to do
    });
  }

  public static void execute(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final IProcedure<Connection, SQLException> procedure)
      throws SQLException {
    try (Connection connection = connector.connectWritable(connectionDescription);) {
      procedure.execute(connection);
    }
  }

  public static boolean exists(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription) {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
      return true;
    } catch (final SQLException exception) {
      logger.log(ILevel.WARNING, exception.getMessage(), exception);
      return false;
    }
  }

  public static List<Object> update(
      final Connection connection,
      final String statementString,
      final String[] returnColumns,
      final Object... values)
      throws SQLException {
    return update(connection, statementString, returnColumns, setterProcedur(values));
  }

  public static List<Object> update(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String updatetStatement,
      final String[] returnColumns,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    try (Connection connection = connector.connectWritable(connectionDescription)) {
      return update(connection, updatetStatement, returnColumns, prepareProcedure);
    }
  }

  public static List<Object> update(
      final Connection connection,
      final String statementString,
      final String[] returnColumns,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString, returnColumns)) {
      prepareProcedure.execute(statement);
      final int numberOfChangedRows = statement.executeUpdate();
      if (numberOfChangedRows == 0) {
        return new ArrayList<>();
      }
      final ArrayList<Object> keys = new ArrayList<>();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        while (generatedKeys.next()) {
          final Object key = generatedKeys.getObject(1);
          logger.log(ILevel.DEBUG, "    key: " + toDebugString(key)); //$NON-NLS-1$
          keys.add(key);
        }
      }
      return keys;
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static List<Object> update(final Connection connection, final String statementString, final Object... values)
      throws SQLException {
    return update(connection, statementString, setterProcedur(values));
  }

  public static List<Object> update(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String updatetStatement,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    try (Connection connection = connector.connectWritable(connectionDescription)) {
      return update(connection, updatetStatement, prepareProcedure);
    }
  }

  public static List<Object> update(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(statementString, Statement.RETURN_GENERATED_KEYS)) {
      try {
        prepareProcedure.execute(statement);
        final int numberOfChangedRows = statement.executeUpdate();
        if (numberOfChangedRows == 0) {
          return new ArrayList<>();
        }
        final ArrayList<Object> keys = new ArrayList<>();
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          while (generatedKeys.next()) {
            final Object key = generatedKeys.getObject(1);
            logger.log(ILevel.DEBUG, "    key: " + toDebugString(key)); //$NON-NLS-1$
            keys.add(key);
          }
        }
        return keys;
      } catch (final SQLException exception) {
        throw exception;
      }
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static <I> ICloseableConsumer<I, Boolean, SQLException> update(
      final IFactory<IBlock<RuntimeException>, IWatcher, RuntimeException> cancelWatcherFactory,
      final Connection connection,
      final String statementString,
      final IConverter<I, List<IDatabaseValue>, SQLException> converter) {

    final IAggregator<PreparedStatement, I, Boolean, SQLException> aggregator = new IAggregator<PreparedStatement, I, Boolean, SQLException>() {

      @Override
      public Boolean aggregate(final PreparedStatement statement, final I object) throws SQLException {
        final List<IDatabaseValue> values = converter.convert(object);
        if (values.isEmpty()) {
          return false;
        }
        for (int i = 0; i < values.size(); i++) {
          setTo(statement, i + 1, values.get(i));
        }
        return true;
      }

      private void setTo(final PreparedStatement statement, final int i, final IDatabaseValue value)
          throws SQLException {
        try {
          if (value.getObject(connection) == null) {
            if (value.getTypeName() != null) {
              statement.setNull(i, value.getType(), value.getTypeName());
            } else {
              statement.setNull(i, value.getType());
            }
            return;
          }
          statement.setObject(i, value.getObject(connection));
        } catch (final SQLException exception) {
          logger.log(ILevel.WARNING, "Couldn't set value on column '" + i + "'", exception); //$NON-NLS-1$ //$NON-NLS-2$
          if (value.getTypeName() != null) {
            statement.setNull(i, value.getType(), value.getTypeName());
          } else {
            statement.setNull(i, value.getType());
          }
        }
      }
    };
    return update(cancelWatcherFactory, connection, statementString, aggregator);
  }

  public static <I> ICloseableConsumer<I, Boolean, SQLException> update(
      final IFactory<IBlock<RuntimeException>, IWatcher, RuntimeException> cancelWatcherFactory,
      final Connection connection,
      final String statementString,
      final IAggregator<PreparedStatement, I, Boolean, SQLException> aggregator) {

    return new ICloseableConsumer<I, Boolean, SQLException>() {

      private IWatcher statementCancler;
      private boolean isClosed = false;
      private PreparedStatement statement;

      @Override
      public void close() throws SQLException {
        if (this.isClosed) {
          throw new SQLException("consumer is closed"); //$NON-NLS-1$
        }
        this.isClosed = true;
        this.statementCancler.close();
        final SQLException exception = DatabaseUtilities.close(this.statement, null);
        if (exception != null) {
          throw exception;
        }
      }

      @Override
      public Boolean consume(final I object) throws SQLException {
        if (this.isClosed) {
          throw new SQLException("consumer is closed"); //$NON-NLS-1$
        }
        if (this.statement == null) {
          initialize();
        }
        final Boolean aggregated = aggregator.aggregate(this.statement, object);
        if (aggregated) {
          this.statement.addBatch();
        }
        return aggregated;
      }

      private void initialize() throws SQLException {
        logger.log(ILevel.DEBUG, statementString);
        this.statement = connection.prepareStatement(statementString);
        this.statementCancler = cancelWatcherFactory.create(() -> {
          try {
            this.statement.cancel();
          } catch (final SQLException exception) {
          }
        });
      }
    };
  }

  public static void add(final PreparedStatement statement, final Object... values) throws SQLException {
    add(statement, setterProcedur(values));
  }

  public static void add(
      final PreparedStatement statement,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    try {
      prepareProcedure.execute(statement);
      statement.addBatch();
    } catch (final SQLException exception) {
      throw exception;
    }
  }

  public static int[] transfer(final PreparedStatement statement) throws SQLException {
    try {
      return statement.executeBatch();
    } catch (final SQLException exception) {
      throw exception;
    }
  }

  public IDatabaseValue value(final Object object, final int type) {
    return value(object, type, null);
  }

  public IDatabaseValue value(final Object object, final int type, final String typeName) {
    return new IDatabaseValue() {

      @Override
      public String getTypeName() {
        return typeName;
      }

      @Override
      public int getType() {
        return type;
      }

      @Override
      public Object getObject(final Connection connection) throws SQLException {
        return object;
      }
    };
  }

  public static <T> IClosableIterator<T, SQLException> query(
      final IFactory<IBlock<RuntimeException>, IWatcher, RuntimeException> cancelWatcherFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> converter) {

    return new IClosableIterator<T, SQLException>() {

      private boolean isClosed = false;
      private IResults results;
      private T value;
      private IWatcher statementCancler;
      private PreparedStatement statement;

      @Override
      public void close() throws SQLException {
        if (this.isClosed) {
          throw new SQLException("iterator is closed"); //$NON-NLS-1$
        }
        this.statementCancler.close();
        SQLException exception = DatabaseUtilities.close(this.results, null);
        exception = DatabaseUtilities.close(this.statement, exception);
        this.isClosed = true;
        if (exception != null) {
          throw exception;
        }
      }

      @Override
      public boolean hasNext() throws SQLException {
        if (this.isClosed) {
          throw new SQLException("iterator is closed"); //$NON-NLS-1$
        }
        if (this.value != null) {
          return true;
        }
        if (this.results == null) {
          if ((this.results = initialize()) == null) {
            return false;
          }
        }
        while (this.results.hasNext()) {
          final IResult result = this.results.next();
          this.value = converter.convert(result);
          if (this.value != null) {
            return true;
          }
        }
        return false;
      }

      private IResults initialize() throws SQLException {
        logger.log(ILevel.DEBUG, statementString);
        this.statement = connection.prepareStatement(statementString);
        this.statementCancler = cancelWatcherFactory.create(() -> {
          try {
            this.statement.cancel();
          } catch (final SQLException exception) {
          }
        });
        prepareProcedure.execute(this.statement);
        if (this.statement.execute()) {
          return new ResultSetToResultsAdapter(this.statement.getResultSet());
        }
        return null;
      }

      @Override
      public T next() throws SQLException {
        if (this.isClosed) {
          throw new SQLException("iterator is closed"); //$NON-NLS-1$
        }
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        try {
          return this.value;
        } finally {
          this.value = null;
        }
      }
    };
  }

  private static String toDebugString(final Object object) {
    if (object == null) {
      return null;
    }
    if (object.getClass().isArray()) {
      return object.getClass().getSimpleName();
    }
    return String.valueOf(object);
  }

  public static String getSchemaName(final Connection connection, final String schemaName) throws SQLException {
    return (schemaName == null ? connection.getMetaData().getUserName() : schemaName);
  }

  public static void createIfNotExists(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String createStatementString)
      throws SQLException {
    if (exists(connection, schemaName, tableName)) {
      return;
    }
    create(connection, createStatementString);
  }

  public static boolean exists(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {

    try (ResultSet tables = connection.getMetaData().getTables(
        connection.getCatalog(), //
        schemaName,
        tableName,
        new String[]{
            "TABLE", //$NON-NLS-1$
            "VIEW", //$NON-NLS-1$
            "SYSTEM TABLE", //$NON-NLS-1$
            "GLOBAL TEMPORARY", //$NON-NLS-1$
            "LOCAL TEMPORARY", //$NON-NLS-1$
            "ALIAS", //$NON-NLS-1$
            "SYNONYM" //$NON-NLS-1$
        })) {
      return tables.next();
    }
  }

  public static boolean execute(final Statement statement, final String statementString) throws SQLException {
    try {
      logger.log(ILevel.DEBUG, "Statement: " + statementString); //$NON-NLS-1$
      if (!statement.execute(statementString)) {
        return false;
      }
      try (ResultSet resultSet = statement.getResultSet()) {
        if (!resultSet.next()) {
          return false;
        }
        if (resultSet.getInt(1) == 0) {
          return false;
        }
        return true;
      }
    } catch (final SQLException exception) {
      throw new SQLException("Executing statement '" + statementString + "' faild", exception); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static String getAsString(final Statement statement, final String statementString) {
    try {
      if (statement.execute(statementString)) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          if (resultSet.next()) {
            return String.valueOf(resultSet.getObject(1));
          }
        }
      }

    } catch (final SQLException exception) {
      logger.log(ILevel.WARNING, exception.getMessage());
    }
    return null;
  }

  public static String createSelectStatement(
      final String tableName,
      final Iterable<String> columnNames,
      final Iterable<String> valueColumnNames) {
    return createSelectStatement(tableName, columnNames, null, valueColumnNames);
  }

  @SuppressWarnings("nls")
  public static String createSelectStatement(
      final String tableName,
      final Iterable<String> columnNames,
      final String orderByColumnName,
      final Iterable<String> valueColumnNames) {
    boolean flag = false;
    final StringBuilder builder = new StringBuilder();
    for (final String columnName : valueColumnNames) {
      if (!flag) {
        builder.append("select ");
        builder.append(columnName);
        builder.append("\n");
        flag = true;
        continue;
      }
      builder.append("     , ");
      builder.append(columnName);
      builder.append("\n");
    }
    builder.append("  from ");
    builder.append(tableName);
    builder.append("\n");
    boolean clauseFlag = false;
    for (final String columnName : columnNames) {
      if (!clauseFlag) {
        builder.append(" where ");
        clauseFlag = true;
      } else {
        builder.append(" and ");
      }
      builder.append(columnName);
      builder.append(" = ?");
    }
    builder.append("\n");
    if (!StringUtilities.isNullOrEmpty(orderByColumnName)) {
      builder.append(" order by ");
      builder.append(orderByColumnName);
      builder.append("\n");
    }
    return builder.toString();
  }

  public static String createIdentifierSelectStatement(
      final String tableName,
      final String identifierColumnName,
      final Iterable<String> valueColumnNames) {
    return createIdentifierSelectStatement(tableName, identifierColumnName, null, valueColumnNames);
  }

  @SuppressWarnings("nls")
  public static String createIdentifierSelectStatement(
      final String tableName,
      final String identifierColumnName,
      final String orderByColumnName,
      final Iterable<String> valueColumnNames) {
    boolean flag = false;
    final StringBuilder builder = new StringBuilder();
    for (final String columnName : valueColumnNames) {
      if (!flag) {
        builder.append("select ");
        builder.append(columnName);
        builder.append("\n");
        flag = true;
        continue;
      }
      builder.append("     , ");
      builder.append(columnName);
      builder.append("\n");
    }
    builder.append("  from ");
    builder.append(tableName);
    builder.append("\n");
    builder.append(" where ");
    builder.append(identifierColumnName);
    builder.append(" = ?");
    builder.append("\n");
    if (!StringUtilities.isNullOrEmpty(orderByColumnName)) {
      builder.append(" order by ");
      builder.append(orderByColumnName);
      builder.append("\n");
    }
    return builder.toString();
  }

  public static String[] getTableTypes(final DatabaseMetaData metaData, final IApplicable<String> applicableType)
      throws SQLException {
    final List<String> types = new ArrayList<>();
    try (ResultSet resultSet = metaData.getTableTypes()) {
      while (resultSet.next()) {
        final String type = resultSet.getString(1);
        if (!applicableType.isApplicable(type)) {
          continue;
        }
        types.add(type);
      }
    }
    return types.stream().toArray(String[]::new);
  }

  public static boolean create(final Connection connection, final String statementString) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      return statement.execute(statementString);
    }
  }

  public static String toString(final ResultSet resultSet) throws SQLException {
    try {
      final ResultSetMetaData metaData = resultSet.getMetaData();
      final List<Object> values = new ArrayList<>();
      for (int i = 0; i < metaData.getColumnCount(); i++) {
        values.add(toDebugString(resultSet.getObject(i + 1)));
      }
      return IterableUtilities.toString(values, ", ", s -> toDebugString(s)); //$NON-NLS-1$
    } catch (final ConversionException exception) {
      throw new SQLException(exception);
    }
  }

  public static IBatchTransfer batchTransfer(
      final Connection connection,
      final String tableName,
      final String[] identifierNames,
      final String[] valueNames) {
    final String selectExistsStatement = createSelectExistsStatement(tableName, identifierNames);
    final String updateStatement = createUpdateStatement(tableName, identifierNames, valueNames);
    final String insertStatement = createInsertStatement(tableName, identifierNames, valueNames);
    return batchTransfer(connection, selectExistsStatement, insertStatement, updateStatement);
  }

  public static IBatchTransfer batchTransfer(
      final Connection connection,
      final String selectExistsStatement,
      final String insertStatement,
      final String updateStatement) {
    final int numberOfIdentifiers = numberOfQuestionMarks(selectExistsStatement);
    final int numberOfColumns = numberOfQuestionMarks(insertStatement);
    return new BatchTransfer(
        connection,
        numberOfIdentifiers,
        numberOfColumns,
        selectExistsStatement,
        insertStatement,
        updateStatement);
  }

  @SuppressWarnings("nls")
  private static String createInsertStatement(
      final String tableName,
      final String[] identifiers,
      final String[] values) {
    final StringBuilder builder = new StringBuilder();
    builder.append("insert into ");
    builder.append("'");
    builder.append(tableName);
    builder.append("'");
    builder.append(" (");
    boolean nameFlag = false;
    for (final String identifier : identifiers) {
      if (nameFlag) {
        builder.append(", ");
      } else {
        nameFlag = true;
      }
      builder.append("'");
      builder.append(identifier);
      builder.append("'");
    }
    for (final String value : values) {
      if (nameFlag) {
        builder.append(", ");
      } else {
        nameFlag = true;
      }
      builder.append("'");
      builder.append(value);
      builder.append("'");
    }
    builder.append(" )");
    builder.append(" values ");
    builder.append(" (");
    boolean valueflags = false;
    for (@SuppressWarnings("unused")
    final String identifier : identifiers) {
      if (valueflags) {
        builder.append(", ");
      } else {
        valueflags = true;
      }
      builder.append("?");
    }
    for (@SuppressWarnings("unused")
    final String value : values) {
      if (valueflags) {
        builder.append(", ");
      } else {
        valueflags = true;
      }
      builder.append("?");
    }
    builder.append(" )");
    return builder.toString();
  }

  @SuppressWarnings("nls")
  private static String createUpdateStatement(
      final String tableName,
      final String[] identifiers,
      final String[] values) {
    final StringBuilder builder = new StringBuilder();
    builder.append("update ");
    builder.append("'");
    builder.append(tableName);
    builder.append("'");

    builder.append(" set ");
    boolean valueflags = false;
    for (final String value : values) {
      if (valueflags) {
        builder.append(", ");
      } else {
        valueflags = true;
      }
      builder.append("'");
      builder.append(value);
      builder.append("' = ?");
    }
    builder.append(" )");

    boolean clauseFlag = false;
    for (final String identifier : identifiers) {
      if (clauseFlag) {
        builder.append(" and ");
      } else {
        builder.append(" where ");
        clauseFlag = true;
      }
      builder.append("'");
      builder.append(identifier);
      builder.append("' = ?");
    }
    return builder.toString();
  }

  @SuppressWarnings("nls")
  private static String createSelectExistsStatement(final String tableName, final String[] identifiers) {
    final StringBuilder builder = new StringBuilder();
    builder.append("select count(*) ");
    builder.append("'");
    builder.append(tableName);
    builder.append("'");
    boolean flag = false;
    for (final String identifier : identifiers) {
      if (flag) {
        builder.append(" and ");
      } else {
        builder.append(" where ");
        flag = true;
      }
      builder.append("'");
      builder.append(identifier);
      builder.append("' = ?");
    }
    return builder.toString();
  }

  private static int numberOfQuestionMarks(final String string) {
    return (int) string.chars().filter(ch -> ch == '?').count();
  }
}
