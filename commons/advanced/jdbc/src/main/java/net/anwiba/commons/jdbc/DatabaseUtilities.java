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
package net.anwiba.commons.jdbc;

import net.anwiba.commons.jdbc.connection.ConnectionUtilities;
import net.anwiba.commons.jdbc.connection.IDatabaseConnector;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.connection.WrappedResultSet;
import net.anwiba.commons.jdbc.connection.WrappedStatement;
import net.anwiba.commons.jdbc.constraint.Constraint;
import net.anwiba.commons.jdbc.constraint.ConstraintType;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.result.IResults;
import net.anwiba.commons.jdbc.result.ResultSetToResultAdapter;
import net.anwiba.commons.jdbc.result.ResultSetToResultsAdapter;
import net.anwiba.commons.jdbc.value.IDatabaseValue;
import net.anwiba.commons.lang.counter.Counter;
import net.anwiba.commons.lang.counter.ICounter;
import net.anwiba.commons.lang.counter.IntCounter;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.Throwables;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IBiFunction;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IClosableIterator;
import net.anwiba.commons.lang.functional.ICloseable;
import net.anwiba.commons.lang.functional.ICloseableConsumer;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.functional.IInterruptableBiFunction;
import net.anwiba.commons.lang.functional.IInterruptableFunction;
import net.anwiba.commons.lang.functional.IInterruptableProcedure;
import net.anwiba.commons.lang.functional.IObserver;
import net.anwiba.commons.lang.functional.IObserverFactory;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.number.ComparableNumber;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.primitive.BooleanContainer;
import net.anwiba.commons.lang.primitive.IBooleanContainer;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.time.ZonedDateTimeUtilities;
import net.anwiba.commons.version.IVersion;
import net.anwiba.commons.version.VersionBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class DatabaseUtilities {

  private static ILogger logger = Logging.getLogger(DatabaseUtilities.class);

  public static SQLException close(final AutoCloseable closeable) {
    try {
      if (closeable instanceof Connection connection) {
        if (connection.isClosed()) {
          return null;
        }
        closeable.close();
        return null;
      }
      if (closeable instanceof Statement statement) {
        if (statement.isClosed()) {
          return null;
        }
        closeable.close();
        return null;
      }
      if (closeable instanceof ResultSet resultSet) {
        if (resultSet.isClosed()) {
          return null;
        }
        closeable.close();
        return null;
      }
      Optional.of(Exception.class, closeable).consume(AutoCloseable::close).get();
      return null;
    } catch (final Exception e) {
      return asSQLException(e);
    }
  }

  public static SQLException close(final SQLException exception,
      final AutoCloseable closeable,
      final AutoCloseable other,
      final AutoCloseable... others) {
    return Optional.of(close(closeable, other, others))
        .convert(e -> {
          if (exception != null) {
            exception.addSuppressed(e);
            return exception;
          }
          return e;
        })
        .getOr(() -> exception);
  }

  public static SQLException close(final AutoCloseable closeable,
      final AutoCloseable other,
      final AutoCloseable... others) {
    List<Throwable> throwables = new LinkedList<>();
    Optional.of(close(closeable)).consume(throwables::add);
    Optional.of(close(other)).consume(throwables::add);
    for (AutoCloseable value : others) {
      Optional.of(close(value)).consume(throwables::add);
    }
    return Throwables.concat(DatabaseUtilities::asSQLException, throwables);
  }

  public static SQLException close(final SQLException exception,
      final AutoCloseable closeable) {
    return Throwables.concat(DatabaseUtilities::asSQLException, exception, close(closeable));
  }

  public static WrappedResultSet wrapWithUnclosableStatement(final ResultSet resultSet) throws SQLException {
    return new WrappedResultSet(resultSet, new WrappedStatement(resultSet.getStatement(), closable -> {}));
  }

  public static void throwIfNotNull(final Throwable throwable) throws SQLException {
    Throwables.throwIfNotNull(DatabaseUtilities::asSQLException, throwable);
  }

  public static void throwIfNotEmpty(final List<Throwable> throwables) throws SQLException {
    Throwables.throwIfNotEmpty(DatabaseUtilities::asSQLException, throwables);
  }

  public static SQLException asSQLException(final Throwable throwable) {
    return Optional.of(throwable)
        .convert(e -> (e instanceof SQLException ioe) ? ioe : new SQLException(e.getMessage(), e))
        .get();
  }

  public static void execute(final Connection connection, final File file)
      throws FileNotFoundException,
      IOException,
      SQLException {
    execute(connection, file, ";");
  }

  public static void execute(final Connection connection, final File file, final String commandEnd)
      throws FileNotFoundException,
      IOException,
      SQLException {
    StringBuilder builder = new StringBuilder();
    ICounter counter = new Counter(0);
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        String trimed = line.trim();
        if (trimed.isBlank() || "COMMIT;".equalsIgnoreCase(trimed)) {
          continue;
        }
        builder.append(trimed);
        if (trimed.endsWith(commandEnd)) {
          String statementString = builder.toString();
          try {
            execute(connection, statementString.substring(0, statementString.length() - 1));
            counter.increment();
            if (!connection.getAutoCommit() && counter.value() == 500) {
              counter.set(0);
              connection.commit();
            }
            builder = new StringBuilder();
            continue;
          } catch (SQLException exception) {
            String message = exception.getCause().getMessage();
            if (message.startsWith("[SQLITE_ERROR] SQL error or missing database (unrecognized token:")) {
              continue;
            }
            throw exception;
          }
        }
        builder.append("\n");
      }
    }
    if (!connection.getAutoCommit() && counter.value() > 0) {
      connection.commit();
    }
  }

  public static boolean execute(final Connection connection, final String statementString)
      throws SQLException {
    return execute(
        connection,
        statementString,
        (IProcedure<PreparedStatement, SQLException>) statement -> {
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

  public static final boolean execute(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return execute(connection, statementString, setter(values), each -> {
      // nothing to do
    });
  }

  public static boolean execute(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IProcedure<ResultSet, SQLException> resultProcedure)
      throws SQLException {
    return execute(b -> () -> {},
        connection,
        statementString,
        prepareProcedure,
        (IBiFunction<PreparedStatement, IBooleanContainer, Boolean, SQLException>) (statement, flag) -> {
          if (statement.execute()) {
            try (final ResultSet resultSet = statement.getResultSet()) {
              resultProcedure.execute(resultSet);
              return true;
            }
          }
          return statement.getUpdateCount() > 0;
        });
  }

  public static <T> T execute(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final IFunction<Connection, T, SQLException> function)
      throws SQLException {
    try (Connection connection = connector.connectWritable(connectionDescription, true)) {
      return function.execute(connection);
    }
  }

  public static <T> T execute(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final IInterruptableFunction<Connection, T, SQLException> function)
      throws SQLException,
      CanceledException {
    try (Connection connection = connector.connectWritable(connectionDescription, true)) {
      return function.execute(connection);
    } finally {
    }
  }

  public static void execute(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final IProcedure<Connection, SQLException> procedure)
      throws SQLException {
    try (Connection connection = connector.connectWritable(connectionDescription, true)) {
      procedure.execute(connection);
    }
  }

  public static <T> T execute(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IBiFunction<PreparedStatement, IBooleanContainer, T, SQLException> function)
      throws SQLException {
    return execute(canceler.observerFactory(), connection, statementString, setter(), function);
  }

  public static <T> T execute(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IBiFunction<PreparedStatement, IBooleanContainer, T, SQLException> function)
      throws SQLException {
    return execute(canceler.observerFactory(), connection, statementString, prepareProcedure, function);
  }

  public static <T> T execute(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IBiFunction<PreparedStatement, IBooleanContainer, T, SQLException> function)
      throws SQLException {
    String connectionHash = ConnectionUtilities.hash(connection);
    String statementHash = ConnectionUtilities.nullHash();
    IBooleanContainer flag = new BooleanContainer(true);
    try (
        final PreparedStatement statement = connection.prepareStatement(statementString);
        final ICloseable<RuntimeException> cancler = cancelObserverFactory.create(silent(() -> {
          flag.set(false);
          statement.cancel();
        }, () -> connectionHash + " " + ConnectionUtilities.hash(statement) + " statement canceled"))) {
      statementHash = ConnectionUtilities.hash(statement);
      prepareProcedure.execute(statement);
      return function.execute(statement, flag);
    } catch (final SQLException exception) {
      throw new SQLException(connectionHash + " " + statementHash + " statement failed", exception);
    }
  }

  public static <T> T execute(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IInterruptableBiFunction<PreparedStatement, IBooleanContainer, T, SQLException> function)
      throws SQLException,
      CanceledException {
    return execute(canceler.observerFactory(), connection, statementString, interruptableStetter(), function);
  }

  public static <T> T execute(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IInterruptableProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IInterruptableBiFunction<PreparedStatement, IBooleanContainer, T, SQLException> function)
      throws SQLException,
      CanceledException {
    return execute(canceler.observerFactory(), connection, statementString, prepareProcedure, function);
  }

  public static <T> T execute(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IInterruptableProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IInterruptableBiFunction<PreparedStatement, IBooleanContainer, T, SQLException> function)
      throws SQLException,
      CanceledException {
    String connectionHash = ConnectionUtilities.hash(connection);
    String statementHash = ConnectionUtilities.nullHash();
    IBooleanContainer flag = new BooleanContainer(true);
    try (
        final PreparedStatement statement = connection.prepareStatement(statementString);
        final ICloseable<RuntimeException> cancler = cancelObserverFactory.create(silent(() -> {
          flag.set(false);
          statement.cancel();
        }, () -> connectionHash + " " + ConnectionUtilities.hash(statement) + " statement canceled"))) {
      statementHash = ConnectionUtilities.hash(statement);
      prepareProcedure.execute(statement);
      return function.execute(statement, flag);
    } catch (final SQLException exception) {
      throw new SQLException(connectionHash + " " + statementHash + " statement failed", exception);
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
    final int mayor = getMajorVersion(connection);
    final int minor = getMinorVersion(connection);
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
      final String statementString,
      final String schemaName,
      final String tableName)
      throws SQLException {
    final Map<String, Constraint> constraints = new HashMap<>();
    foreach(ICanceler.dummy().observerFactory(),
        connection,
        statementString,
        setter(schemaName, tableName),
        result -> {
          final String columnName = result.getString(1);
          final String constraintName = result.getString(2);
          final ConstraintType constraintType = ConstraintType
              .getTypeById(result.getString(3));
          final String condition = result.getString(4);
          final Constraint constraint = getConstraint(
              constraints,
              constraintName,
              constraintType,
              condition);
          constraint.add(columnName);
        });
    return constraints;
  }

  public static Constraint getConstraint(
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

  public static void dropTable(
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException {
    final String qualifiedTableName = schemaName == null ? tableName : schemaName + "." + tableName; //$NON-NLS-1$
    final String statementString = "DROP TABLE " + qualifiedTableName; //$NON-NLS-1$
    execute(connection, statementString);
  }

  public static void dropIndex(
      final Connection connection,
      final String schemaName,
      final String indexName)
      throws SQLException {
    final String qualifiedIndexName = schemaName == null ? indexName : schemaName + "." + indexName; //$NON-NLS-1$
    final String statementString = "DROP INDEX " + qualifiedIndexName; //$NON-NLS-1$
    execute(connection, statementString);
  }

  public static String getOwner(final Connection connection, final String owner)
      throws SQLException {
    return owner == null ? connection.getMetaData().getUserName() : owner;
  }

  public static ResultSet resultSet(final Connection connection, final String string)
      throws SQLException {
    return resultSet(connection, string, value -> {});
  }

  public static ResultSet resultSet(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return resultSet(ICanceler.dummy(), connection, statementString, prepareProcedure);
  }

  public static ResultSet resultSet(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return resultSet(canceler.observerFactory(), connection, statementString, prepareProcedure);
  }

  public static ResultSet resultSet(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return resultSet(cancelObserverFactory,
        connection,
        statementString,
        prepareProcedure,
        (IBiFunction<PreparedStatement, IBooleanContainer, ResultSet, SQLException>) (statement, flag) -> {
          if (flag.isTrue() && statement.execute()) {
            return statement.getResultSet();
          }
          return null;
        });
  }

  public static ResultSet resultSet(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IBiFunction<PreparedStatement, IBooleanContainer, ResultSet, SQLException> function)
      throws SQLException {
    final String connectionHash = ConnectionUtilities.hash(connection);
    final IBooleanContainer flag = new BooleanContainer(true);
    // attention do not close the statement
    final PreparedStatement statement = connection.prepareStatement(statementString);
    final String statementHash = ConnectionUtilities.hash(statement);
    try (
        final ICloseable<RuntimeException> cancler = cancelObserverFactory.create(silent(() -> {
          flag.set(false);
          statement.cancel();
        }, () -> connectionHash + " " + ConnectionUtilities.hash(statement) + " statement canceled"))) {
      prepareProcedure.execute(statement);
      final ResultSet resultSet = function.execute(statement, flag);
      //      statement.isCloseOnCompletion(); // isn't supported by sap hana
      return resultSet;
    } catch (final SQLException exception) {
      throw new SQLException(connectionHash + " " + statementHash + " statement failed", exception);
    }
  }

  public static String stringResult(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, String,
            SQLException>) connection -> stringResult(connection, statementString, prepareProcedure));
  }

  public static String stringResult(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return stringResult(connection, statementString, setter(values));
  }

  public static String stringResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    final IFunction<IResult, String, SQLException> resultFunction = new IFunction<>() {

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

  public static Double doubleResult(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return doubleResult(connection, statementString, setter(values));
  }

  public static Double doubleResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return result(connection,
        statementString,
        prepareProcedure,
        (IConverter<IOptional<IResult, SQLException>, Double,
            SQLException>) optional -> optional.convert(result -> result.getDouble(1)).get());
  }

  public static Long longResult(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, Long,
            SQLException>) connection -> longResult(connection, statementString, prepareProcedure));
  }

  public static Long longResult(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return longResult(connection, statementString, setter(values));
  }

  public static Long longResult(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return result(connection,
        statementString,
        prepareProcedure,
        (IConverter<IOptional<IResult, SQLException>, Long,
            SQLException>) optional -> optional.convert(result -> result.getLong(1)).get());
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
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, Boolean,
            SQLException>) connection -> booleanResult(connection, statementString, prepareProcedure));
  }

  public static boolean booleanResult(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return booleanResult(connection, statementString, setter(values));
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
        (IConverter<IOptional<IResult, SQLException>, Boolean,
            SQLException>) value -> value.convert(v -> v.getBoolean(1, false)).getOr(() -> Boolean.FALSE))
                .booleanValue();
  }

  public static <T> T result(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultFunction)
      throws SQLException {
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, T,
            SQLException>) connection -> result(connection, statementString, prepareProcedure, resultFunction));
  }

  public static <T> T result(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultConverter)
      throws SQLException {
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, T, SQLException>) connection -> result(connection, statementString, resultConverter));
  }

  public static <T> T result(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> converter)
      throws SQLException {
    return result(
        canceler,
        connection,
        statementString,
        (IProcedure<PreparedStatement, SQLException>) statement -> {
          // nothing to do
        },
        converter);
  }

  public static <T> T result(
      final Connection connection,
      final String statementString,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> converter)
      throws SQLException {
    return result(
        connection,
        statementString,
        (IProcedure<PreparedStatement, SQLException>) statement -> {
          // nothing to do
        },
        converter);
  }

  public static <T> T result(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultConverter)
      throws SQLException {
    return result(ICanceler.dummy(),
        connection,
        statementString,
        prepareProcedure,
        resultConverter);
  }

  public static <T> T result(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultFunction)
      throws SQLException {
    return result(canceler.observerFactory(), connection, statementString, prepareProcedure, resultFunction);
  }

  public static <T> T result(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IOptional<IResult, SQLException>, T, SQLException> resultFunction)
      throws SQLException {
    return execute(cancelObserverFactory,
        connection,
        statementString,
        prepareProcedure,
        (IBiFunction<PreparedStatement, IBooleanContainer, T, SQLException>) (statement, flag) -> {
          if (flag.isTrue() && statement.execute()) {
            try (final ResultSet resultSet = statement.getResultSet()) {
              final IResult result = new ResultSetToResultAdapter(resultSet, (c, i, o, d) -> o);
              if (flag.isTrue() && resultSet.next()) {
                final T value = resultFunction.convert(Optional.of(SQLException.class, result));
                if (resultSet.next()) {
                  throw new SQLException(ConnectionUtilities.hash(connection) + " "
                      + ConnectionUtilities.hash(statement) + " statement result isn't unique");
                }
                return value;
              }
            }
          }
          return resultFunction.convert(Optional.<IResult, SQLException>empty(SQLException.class));
        });
  }

  public static <T> List<T> results(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IConverter<IResult, T, SQLException> function)
      throws SQLException {
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, List<T>, SQLException>) connection -> results(connection, statementString, function));
  }

  public static <T> List<T> results(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> function)
      throws SQLException {
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, List<T>,
            SQLException>) connection -> results(connection, statementString, prepareProcedure, function));
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IConverter<IResult, T, SQLException> function)
      throws SQLException {
    return results(
        connection,
        statementString,
        (IProcedure<PreparedStatement, SQLException>) statement -> {
          // nothing to do
        },
        function);
  }

  public static <T> List<T> results(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IConverter<IResult, T, SQLException> resultConverter)
      throws SQLException {
    return results(canceler.observerFactory(), connection, statementString, s -> {}, resultConverter);
  }

  public static <T> List<T> results(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IConverter<IResult, T, SQLException> resultConverter)
      throws SQLException {
    return results(cancelObserverFactory, connection, statementString, s -> {}, resultConverter);
  }

  public static <T> List<T> results(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> resultConverter)
      throws SQLException {
    return results(canceler.observerFactory(), connection, statementString, prepareProcedure, resultConverter);
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IInterruptableFunction<IResult, T, SQLException> resultFunction)
      throws SQLException,
      CanceledException {
    return results(b -> () -> {}, connection, statementString, s -> {}, resultFunction);
  }

  public static <T> List<T> results(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IInterruptableFunction<IResult, T, SQLException> resultFunction)
      throws SQLException,
      CanceledException {
    return execute(connector,
        connectionDescription,
        (IInterruptableFunction<Connection, List<T>,
            SQLException>) connection -> results(b -> () -> {}, connection, statementString, s -> {}, resultFunction));
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IInterruptableProcedure<PreparedStatement, SQLException> prepareClosure,
      final IInterruptableFunction<IResult, T, SQLException> resultFunction)
      throws SQLException,
      CanceledException {
    return results(b -> () -> {}, connection, statementString, prepareClosure, resultFunction);
  }

  public static <T> List<T> results(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IInterruptableProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IInterruptableFunction<IResult, T, SQLException> resultProcedure)
      throws SQLException,
      CanceledException {
    return execute(cancelObserverFactory,
        connection,
        statementString,
        prepareProcedure,
        (IInterruptableBiFunction<PreparedStatement, IBooleanContainer, List<T>, SQLException>) (statement, flag) -> {
          final List<T> resultList = new ArrayList<>();
          if (flag.isTrue() && statement.execute()) {
            try (final ResultSet resultSet = statement.getResultSet()) {
              final IResult result = new ResultSetToResultAdapter(resultSet, (c, i, o, d) -> o);
              while (flag.isTrue() && resultSet.next()) {
                final T object = resultProcedure.execute(result);
                if (object != null) {
                  resultList.add(object);
                }
              }
            }
          }
          return resultList;
        });
  }

  public static <T> List<T> results(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> resultProcedure)
      throws SQLException {
    return results(b -> () -> {}, connection, statementString, prepareProcedure, resultProcedure);
  }

  public static <T> List<T> results(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> resultConverter)
      throws SQLException {
    return execute(cancelObserverFactory,
        connection,
        statementString,
        prepareProcedure,
        (IBiFunction<PreparedStatement, IBooleanContainer, List<T>, SQLException>) (statement, flag) -> {
          final List<T> resultList = new ArrayList<>();
          if (flag.isTrue() && statement.execute()) {
            try (final ResultSet resultSet = statement.getResultSet()) {
              final IResult result = new ResultSetToResultAdapter(resultSet, (c, i, o, d) -> o);
              while (flag.isTrue() && resultSet.next()) {
                final T object = resultConverter.convert(result);
                if (object == null) {
                  continue;
                }
                resultList.add(object);
              }
            }
          }
          return resultList;
        });
  }

  public static <T> T aggregate(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String statementString,
      final IConverter<Iterable<IResult>, T, SQLException> function)
      throws SQLException {
    try (Connection connection = connector.connectReadOnly(connectionDescription)) {
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
    return aggregate(b -> () -> {}, connection, statementString, prepareProcedure, resultProcedure);
  }

  public static <T> T aggregate(
      final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<Iterable<IResult>, T, SQLException> resultProcedure)
      throws SQLException {
    return aggregate(canceler.observerFactory(), connection, statementString, prepareProcedure, resultProcedure);
  }

  public static <T> T aggregate(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<Iterable<IResult>, T, SQLException> resultProcedure)
      throws SQLException {
    return execute(cancelObserverFactory,
        connection,
        statementString,
        prepareProcedure,
        (IBiFunction<PreparedStatement, IBooleanContainer, T, SQLException>) (statement, flag) -> {
          if (statement.execute()) {
            try (final ResultSet resultSet = statement.getResultSet()) {
              final List<SQLException> exceptions = new ArrayList<>(4);
              final IResult result = new ResultSetToResultAdapter(resultSet, (c, i, o, d) -> o);
              final Iterable<IResult> iterable = () -> new Iterator<>() {

                @Override
                public boolean hasNext() {
                  try {
                    return flag.isTrue() && resultSet.next();
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
        });
  }

  public static final boolean call(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return call(connection, statementString, setter(values), each -> {
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
    String statementHash = ConnectionUtilities.nullHash();
    try (CallableStatement statement = connection.prepareCall(statementString)) {
      statementHash = ConnectionUtilities.hash(statement);
      prepareProcedure.execute(statement);
      if (statement.execute()) {
        try (final ResultSet resultSet = statement.getResultSet()) {
          resultProcedure.execute(resultSet);
          return true;
        }
      }
      return statement.getUpdateCount() > 0;
    } catch (final SQLException exception) {
      throw new SQLException(ConnectionUtilities.hash(connection) + " " + statementHash + " statement failed",
          exception);
    }
  }

  @SuppressWarnings("nls")
  public static IProcedure<PreparedStatement, SQLException> setter(
      final Object... objects) {
    return setter(Arrays.asList(objects));
  }

  public static IProcedure<PreparedStatement, SQLException> setter(final List objects) {
    return statement -> {
      for (int i = 0; i < objects.size(); ++i) {
        final Object adjustValue = adjustValue(objects.get(i));
        //        if (adjustValue == null) {
        //          statement.setNull(i + 1, 0);
        //          continue;
        //        }
        statement.setObject(i + 1, adjustValue);
      }
    };
  }

  @SuppressWarnings("nls")
  public static IInterruptableProcedure<PreparedStatement, SQLException> interruptableStetter(
      final Object... objects) {
    return interruptableStetter(Arrays.asList(objects));
  }

  public static IInterruptableProcedure<PreparedStatement, SQLException> interruptableStetter(final List objects) {
    return statement -> {
      for (int i = 0; i < objects.size(); ++i) {
        final Object adjustValue = adjustValue(objects.get(i));
        //        if (adjustValue == null) {
        //          statement.setNull(i + 1, 0);
        //          continue;
        //        }
        statement.setObject(i + 1, adjustValue);
      }
    };
  }

  public static Object adjustValue(final Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof java.util.Date date) {
      return new java.sql.Timestamp(date.getTime());
    }
    if (value instanceof ZonedDateTime zonedDateTime) {
      return Timestamp.valueOf(zonedDateTime
          .toInstant()
          .atZone(ZonedDateTimeUtilities.getCoordinatedUniversalTimeZone())
          .toLocalDateTime());
    }
    if (value instanceof LocalDateTime localDateTime) {
      final ZonedDateTime dateTime = localDateTime.atZone(ZonedDateTimeUtilities.getUserZone())
          .toInstant()
          .atZone(
              ZonedDateTimeUtilities.getCoordinatedUniversalTimeZone());
      return Timestamp.valueOf(dateTime.toLocalDateTime());
    }
    if (value instanceof LocalDate localDate) {
      return java.sql.Date.valueOf(localDate);
    }
    if (value instanceof LocalTime localTime) {
      return java.sql.Time.valueOf(localTime);
    }
    if (value instanceof ComparableNumber) {
      return adjustValue(value);
    }
    if (value instanceof Double doubleValue) {
      if (Double.isNaN(doubleValue.doubleValue())) {
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
    return execute(connector,
        connectionDescription,
        (IFunction<Connection, Integer,
            SQLException>) connection -> count(connection, statementString, values));
  }

  public static int count(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return count(connection, statementString, setter(values));
  }

  public static Long next(final Connection connection, final String statementString)
      throws SQLException {
    return execute(b -> () -> {},
        connection,
        statementString,
        setter(),
        (IBiFunction<PreparedStatement, IBooleanContainer, Long, SQLException>) (statement, flag) -> {
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
        });
  }

  public static int count(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return execute(b -> () -> {},
        connection,
        statementString,
        prepareProcedure,
        (IBiFunction<PreparedStatement, IBooleanContainer, Integer, SQLException>) (statement, flag) -> {
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
        });
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
    return update(connection, statementString, returnColumns, setter(values));
  }

  public static List<Object> update(
      final Connection connection,
      final String statementString,
      final String[] returnColumns,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    String connectionHash = ConnectionUtilities.hash(connection);
    String statementHash = ConnectionUtilities.nullHash();
    try (PreparedStatement statement =
        returnColumns == null || returnColumns.length == 0
            ? connection.prepareStatement(statementString, Statement.RETURN_GENERATED_KEYS)
            : connection.prepareStatement(statementString, returnColumns)) {
      statementHash = ConnectionUtilities.hash(statement);
      prepareProcedure.execute(statement);
      final int numberOfChangedRows = statement.executeUpdate();
      if (numberOfChangedRows == 0) {
        return new ArrayList<>();
      }
      final ArrayList<Object> keys = new ArrayList<>();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        while (generatedKeys.next()) {
          final Object key = generatedKeys.getObject(1);
          logger.debug(ConnectionUtilities.hash(connection) + " " + ConnectionUtilities.hash(statement) //$NON-NLS-1$
              + " statement generated key: " + ConnectionUtilities.toDebugString(key));
          keys.add(key);
        }
      }
      return keys;
    } catch (final SQLException exception) {
      throw new SQLException(connectionHash + " " + statementHash + " statement failed", exception);
    }
  }

  public static List<Object> update(
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return update(connection, statementString, null, prepareProcedure);
  }

  public static List<Object> update(
      final Connection connection,
      final String statementString,
      final Object... values)
      throws SQLException {
    return update(connection, statementString, setter(values));
  }

  public static List<Object> update(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String updatetStatement,
      final String[] returnColumns,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return execute(
        connector,
        connectionDescription,
        (IFunction<Connection, List<Object>,
            SQLException>) connection -> update(connection, updatetStatement, returnColumns, prepareProcedure));
  }

  public static List<Object> update(
      final IDatabaseConnector connector,
      final IJdbcConnectionDescription connectionDescription,
      final String updatetStatement,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure)
      throws SQLException {
    return execute(
        connector,
        connectionDescription,
        (IFunction<Connection, List<Object>,
            SQLException>) connection -> update(connection, updatetStatement, prepareProcedure));
  }

  public static void foreach(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConsumer<IResult, SQLException> resultConsumer)
      throws SQLException {
    execute(cancelObserverFactory,
        connection,
        statementString,
        prepareProcedure,
        (IBiFunction<PreparedStatement, IBooleanContainer, Void, SQLException>) (statement, flag) -> {
          if (flag.isTrue() && statement.execute()) {
            try (final ResultSet resultSet = statement.getResultSet()) {
              final IResult result = new ResultSetToResultAdapter(resultSet, (c, i, o, d) -> o);
              while (flag.isTrue() && resultSet.next()) {
                resultConsumer.consume(result);
              }
            }
          }
          return null;
        });
  }

  public static <I> ICloseableConsumer<I, Boolean, SQLException> update(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IConverter<I, List<IDatabaseValue>, SQLException> converter) {

    final IAggregator<PreparedStatement, I, Boolean, SQLException> aggregator = new IAggregator<>() {

      @Override
      public Boolean aggregate(final PreparedStatement statement, final I object)
          throws SQLException {
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
          logger.warning("Couldn't set value on column '" + i + "'", exception);
          logger.debug(exception.getMessage(), exception);
          if (value.getTypeName() != null) {
            statement.setNull(i, value.getType(), value.getTypeName());
          } else {
            statement.setNull(i, value.getType());
          }
        }
      }
    };
    return update(cancelObserverFactory, connection, statementString, aggregator);
  }

  public static <I> ICloseableConsumer<I, Boolean, SQLException> update(
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IAggregator<PreparedStatement, I, Boolean, SQLException> aggregator) {

    return new ICloseableConsumer<>() {

      private final String connectionHash = ConnectionUtilities.hash(connection);
      private String statementHash = ConnectionUtilities.nullHash();
      private IObserver statementCancelObserver;
      private boolean isClosed = false;
      private PreparedStatement statement;
      private final IBooleanContainer cancelFlag = new BooleanContainer(false);

      @Override
      public void close() throws SQLException {
        if (this.isClosed) {
          throw new SQLException(this.connectionHash + " " + this.statementHash + "consumer is closed");
        }
        this.isClosed = true;
        final SQLException exception = DatabaseUtilities.close(this.statementCancelObserver, this.statement);
        if (exception != null) {
          throw exception;
        }
      }

      @Override
      public Boolean consume(final I object) throws SQLException {
        if (this.cancelFlag.isTrue()) {
          return false;
        }
        if (this.isClosed) {
          throw new SQLException(this.connectionHash + " " + this.statementHash + "consumer is closed");
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
        this.statement = connection.prepareStatement(statementString);
        this.statementCancelObserver = cancelObserverFactory.create(silent(() -> {
          this.cancelFlag.set(true);
          this.statement.cancel();
        }, () -> this.connectionHash + " " + ConnectionUtilities.hash(this.statement) + " statement canceled"));
        this.statementHash = ConnectionUtilities.hash(this.statement);
      }
    };
  }

  public static void add(final PreparedStatement statement, final Object... values)
      throws SQLException {
    add(statement, setter(values));
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
      final IObserverFactory cancelObserverFactory,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> converter) {
    return new IClosableIterator<>() {

      private final String connectionHash = ConnectionUtilities.hash(connection);
      private boolean isClosed = false;
      private final IBooleanContainer cancelFlag = new BooleanContainer(false);
      private IResults results;
      private T value;
      private IObserver statementCancleObserver;
      private PreparedStatement statement;

      @Override
      public void close() throws SQLException {
        if (this.isClosed) {
          throw new SQLException("iterator is closed"); //$NON-NLS-1$
        }
        try {
          SQLException exception = DatabaseUtilities.close(this.statementCancleObserver, this.results, this.statement);
          DatabaseUtilities.throwIfNotNull(exception);
        } finally {
          this.isClosed = true;
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
          if (this.cancelFlag.isTrue()) {
            return false;
          }
          final IResult result = this.results.next();
          this.value = converter.convert(result);
          if (this.value != null) {
            return true;
          }
        }
        return false;
      }

      private IResults initialize() throws SQLException {
        this.statement = connection.prepareStatement(statementString);
        this.statementCancleObserver = cancelObserverFactory.create(silent(() -> {
          this.cancelFlag.set(true);
          this.statement.cancel();
        }, () -> this.connectionHash + " " + ConnectionUtilities.hash(this.statement) + " statement canceled"));
        prepareProcedure.execute(this.statement);
        if (this.statement.execute()) {
          return new ResultSetToResultsAdapter(
              this.statement.getResultSet(),
              (c, i, o, d) -> o);
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

  public static boolean exists(
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException {
    try (ResultSet tables = connection
        .getMetaData()
        .getTables(
            connection.getCatalog(), //
            schemaName,
            tableName,
            new String[] {
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

  public static boolean execute(final Statement statement, final String statementString)
      throws SQLException {
    try {
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
      throw new SQLException(
          ConnectionUtilities.hash(statement.getConnection()) + " " + ConnectionUtilities.hash(statement)
              + " statement failed",
          exception);
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
      logger.warning(exception.getMessage());
      logger.fine(exception.getMessage(), exception);
    }
    return null;
  }

  public static String createSelectStatement(
      final String tableName,
      final Iterable<String> columnNames,
      final Iterable<String> valueColumnNames) {
    return createSelectStatement(tableName, columnNames, null, valueColumnNames);
  }

  public static String createSelectStatement(
      final String tableName,
      final Iterable<String> conditionColumnNames,
      final String orderByColumnName,
      final Iterable<String> resultColumnNames) {
    boolean flag = false;
    final StringBuilder builder = new StringBuilder();
    for (final String columnName : resultColumnNames) {
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
    for (final String columnName : conditionColumnNames) {
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

  public static String[] getTableTypes(
      final DatabaseMetaData metaData,
      final IApplicable<String> applicableType)
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

  public static boolean create(final Connection connection, final String statementString)
      throws SQLException {
    String statementHash = ConnectionUtilities.nullHash();
    try (Statement statement = connection.createStatement()) {
      statementHash = ConnectionUtilities.hash(statement);
      return statement.execute(statementString);
    } catch (final SQLException exception) {
      throw new SQLException(ConnectionUtilities.hash(connection) + " " + statementHash + " statement failed",
          exception);
    }
  }

  public static String toString(final ResultSet resultSet) throws SQLException {
    try {
      final ResultSetMetaData metaData = resultSet.getMetaData();
      final List<Object> values = new ArrayList<>();
      for (int i = 0; i < metaData.getColumnCount(); i++) {
        values.add(ConnectionUtilities.toDebugString(resultSet.getObject(i + 1)));
      }
      return IterableUtilities.toString(values, ", ", s -> ConnectionUtilities.toDebugString(s)); //$NON-NLS-1$
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

  private static String createSelectExistsStatement(
      final String tableName,
      final String[] identifiers) {
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

  public static IVersion version(final Connection connection) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    int majorVersion = metaData.getDatabaseMajorVersion();
    int minorVersion = metaData.getDatabaseMinorVersion();
    return new VersionBuilder().setMajor(majorVersion).setMinor(minorVersion).build();
  }

  public static IClosableIterator<IResult, SQLException> iterator(final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure) {
    return iterator(canceler, connection, statementString, prepareProcedure, r -> r);
  }

  public static <T> IClosableIterator<T, SQLException> iterator(final ICanceler canceler,
      final Connection connection,
      final String statementString,
      final IProcedure<PreparedStatement, SQLException> prepareProcedure,
      final IConverter<IResult, T, SQLException> resultConverter) {
    return new IClosableIterator<T, SQLException>() {

      private final String connectionHash = ConnectionUtilities.hash(connection);
      private IClosableIterator<T, SQLException> delegator;
      private IObserver observer;
      private PreparedStatement statement;

      @Override
      public void close() throws SQLException {
        DatabaseUtilities.close(this.delegator, this.statement, this.observer);
      }

      @Override
      public boolean hasNext() throws SQLException {
        if (canceler.isCanceled()) {
          return false;
        }
        if (this.delegator == null) {
          this.statement = connection.prepareStatement(statementString);
          this.observer = canceler.observer(silent(() -> {
            this.statement.cancel();
          }, () -> this.connectionHash + " " + ConnectionUtilities.hash(this.statement) + " statement canceled"));
          prepareProcedure.execute(this.statement);
          if (!this.statement.execute()) {
            return false;
          }
          this.delegator = DatabaseUtilities.iterator(canceler, this.statement, resultConverter);
        }
        return this.delegator.hasNext();
      }

      @Override
      public T next() throws SQLException {
        return this.delegator.next();
      }

    };
  }

  public static IClosableIterator<IResult, SQLException> iterator(final ICanceler canceler,
      final PreparedStatement statement) {
    return iterator(canceler, statement, r -> r);
  }

  public static <T> IClosableIterator<T, SQLException> iterator(final ICanceler canceler,
      final PreparedStatement statement,
      final IConverter<IResult, T, SQLException> resultConverter) {
    return new IClosableIterator<T, SQLException>() {

      private IClosableIterator<T, SQLException> delegator;
      private ResultSet resultSet;

      @Override
      public void close() throws SQLException {
        DatabaseUtilities.close(this.delegator, this.resultSet);
      }

      @Override
      public boolean hasNext() throws SQLException {
        if (this.delegator == null) {
          this.resultSet = statement.getResultSet();
          this.delegator =
              DatabaseUtilities.iterator(canceler, this.resultSet, resultConverter);
        }
        return this.delegator.hasNext();
      }

      @Override
      public T next() throws SQLException {
        return this.delegator.next();
      }
    };
  }

  public static <T> IClosableIterator<T, SQLException> iterator(final ICanceler canceler,
      final ResultSet resultSet,
      final IConverter<IResult, T, SQLException> resultConverter) {
    return new IClosableIterator<T, SQLException>() {

      T object;

      @Override
      public void close() throws SQLException {
      }

      @Override
      public boolean hasNext() throws SQLException {
        if (this.object != null) {
          return true;
        }
        while (!canceler.isCanceled() && resultSet.next()) {
          final IResult result = new ResultSetToResultAdapter(resultSet, (c, i, o, d) -> o);
          T value = resultConverter.convert(result);
          if (value != null) {
            this.object = value;
            return true;
          }
        }
        return false;
      }

      @Override
      public T next() throws SQLException {
        try {
          return this.object;
        } finally {
          this.object = null;
        }
      }
    };
  }

  public static <T> List<T> metadatas(final ICanceler canceler,
      final Connection connection,
      final IFactory<DatabaseMetaData, ResultSet, SQLException> resultSetFactory,
      final IConverter<ResultSet, T, SQLException> resultConverter) throws SQLException {
    final DatabaseMetaData metaData = connection.getMetaData();
    final Set<T> result = new LinkedHashSet<>();
    try (final ResultSet resultSet = resultSetFactory.create(metaData)) {
      while (!canceler.isCanceled() && resultSet.next()) {
        T value = resultConverter.convert(resultSet);
        Optional.of(value).consume(v -> result.add(v));
      }
    }
    return List.copyOf(result);
  }

  public static Integer getInteger(final ResultSet resultSet, final int index) throws SQLException {
    int value = resultSet.getInt(index);
    return resultSet.wasNull() ? null : Integer.valueOf(value);
  }

  public static Long getLong(final ResultSet resultSet, final int index) throws SQLException {
    long value = resultSet.getLong(index);
    return resultSet.wasNull() ? null : Long.valueOf(value);
  }

  public static Double getDouble(final ResultSet resultSet, final int index) throws SQLException {
    double value = resultSet.getDouble(index);
    return resultSet.wasNull() ? null : Double.valueOf(value);
  }

  public static Runnable silent(final IBlock<SQLException> block) {
    return () -> {
      try {
        block.execute();
      } catch (SQLException e) {
        logger.fine(e.getMessage(), e);
      }
    };
  }

  public static Runnable silent(final IBlock<SQLException> block, final Supplier<String> loggerMessage) {
    return () -> {
      try {
        logger.debug(loggerMessage.get());
        block.execute();
      } catch (SQLException e) {
        logger.fine(e.getMessage(), e);
      }
    };
  }

  public static void
      commit(final Connection connection, final IntCounter executeCounter, final SQLException exception)
          throws SQLException {
    try {
      if (executeCounter.value() > 0) {
        if (!connection.getAutoCommit()) {
          connection.commit();
        }
      }
      throwIfNotNull(exception);
    } catch (final SQLException commitException) {
      if (exception == null) {
        throw commitException;
      }
      exception.addSuppressed(commitException);
      throw exception;
    }
  }

  public static SQLException rollback(final Connection connection,
      final IntCounter executeCounter,
      final SQLException exception) {
    if (executeCounter.value() > 0) {
      try {
        if (!connection.getAutoCommit()) {
          connection.rollback();
        }
        return exception;
      } catch (final SQLException rollbackException) {
        if (exception == null) {
          return rollbackException;
        }
        exception.addSuppressed(rollbackException);
        return exception;
      }
    }
    return exception;
  }

  public static void setValueToStatement(final Connection connection,
      final PreparedStatement statement,
      final int i,
      final IDatabaseValue value) throws SQLException {
    try {
      if (value.getObject(connection) == null) {
        setNullToStatement(statement, i, value);
        return;
      }
      statement.setObject(i, value.getObject(connection));
    } catch (final SQLException exception) {
      logger.debug(
          () -> ConnectionUtilities.hash(connection) + " " + ConnectionUtilities.hash(statement)
              + " statement, couldn't set value on column '" + i + "'",
          exception);
      setNullToStatement(statement, i, value);
    }
  }

  private static void setNullToStatement(final PreparedStatement statement, final int i, final IDatabaseValue value)
      throws SQLException {
    if (value.getTypeName() != null) {
      statement.setNull(i, value.getType(), value.getTypeName());
    } else {
      statement.setNull(i, value.getType());
    }
  }

  public static int executeBatch(
      final IntCounter insertCounter,
      final IntCounter executeCounter,
      final PreparedStatement statement) throws SQLException {
    final int[] result = statement.executeBatch();
    executeCounter.next();
    insertCounter.reset();
    int sum = 0;
    for (final int i : result) {
      if (PreparedStatement.EXECUTE_FAILED == i) {
        final SQLWarning warnings = statement.getWarnings();
        Optional.of(warnings).consume(w -> logger.warning(() -> w.getMessage(), w));
        return -1;
      }
      if (PreparedStatement.SUCCESS_NO_INFO == i) {
        return -1;
      }
      sum += i;
    }
    return sum;
  }

  public static PreparedStatement createStatement(final Connection connection, final String statementString)
      throws SQLException {
    return connection.prepareStatement(statementString, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
  }

  public static boolean isSupported(final String url) {
    return DriverManager.drivers().anyMatch(d -> {
      try {
        return d.acceptsURL(url);
      } catch (SQLException exception) {
        return false;
      }
    });
  }

  public static void openLogWriter() {
    String logTargetFileName = System.getProperty("net.anwiba.jdbc.logtarget", null);
    if (logTargetFileName == null || logTargetFileName.isBlank()) {
      return;
    }
    if (Objects.equals(logTargetFileName.toLowerCase(), "sysout")) {
      DriverManager.setLogWriter(new PrintWriter(System.out));
      return;
    }
    if (Objects.equals(logTargetFileName.toLowerCase(), "syserr")) {
      DriverManager.setLogWriter(new PrintWriter(System.err));
      return;
    }
    try {
      final File file = new File(logTargetFileName);
      if (!Files.exists(file.toPath())) {
        Files.createFile(file.toPath());
      }
      if (Files.isDirectory(file.toPath())) {
        throw new IOException("JDBC-log targe '" + logTargetFileName + "', is a directory");
      }
      PrintWriter logWriter = new PrintWriter(file);
      DriverManager.setLogWriter(logWriter);
    } catch (IOException exception) {
      logger.debug("Couldn't set JDBC-log file '" + logTargetFileName + "', " + exception.getMessage(), exception);
    }
  }

  public static void closeLogWriter() {
    String logTargetFileName = System.getProperty("net.anwiba.jdbc.log.target", null);
    if (Objects.equals(logTargetFileName.toLowerCase(), "sysout")
        || Objects.equals(logTargetFileName.toLowerCase(), "syserr")) {
      DriverManager.setLogWriter(null);
      return;
    }
    @SuppressWarnings("resource")
    PrintWriter logWriter = DriverManager.getLogWriter();
    if (logWriter == null) {
      return;
    }
    DriverManager.setLogWriter(null);
    logWriter.close();
  }
}
