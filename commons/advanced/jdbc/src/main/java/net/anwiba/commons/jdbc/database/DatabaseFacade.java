/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.jdbc.database;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.metadata.Property;
import net.anwiba.commons.jdbc.name.DatabaseColumnName;
import net.anwiba.commons.jdbc.name.DatabaseIndexName;
import net.anwiba.commons.jdbc.name.DatabaseSchemaName;
import net.anwiba.commons.jdbc.name.DatabaseSequenceName;
import net.anwiba.commons.jdbc.name.DatabaseTableName;
import net.anwiba.commons.jdbc.name.DatabaseViewName;
import net.anwiba.commons.jdbc.name.IDatabaseColumnName;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSchemaName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.jdbc.name.IDatabaseViewName;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.ISupplier;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.thread.cancel.ICanceler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;

public class DatabaseFacade implements IDatabaseFacade {

  @Override
  public String quoted(final IDatabaseTableName name) {
    return name.getSchemaName() == null
        ? quoted(name.getTableName())
        : quoted(name.getSchemaName())
            + "."
            + quoted(name.getTableName());
  }

  @Override
  public String quoted(final String name) {
    if (name == null || name.isEmpty()) { // maybe blank can be a valid name in case sensitive or quoted names
      return null;
    }
    return quotedCharacter() + name + quotedCharacter();
  }

  protected String quotedCharacter() {
    return "\"";
  }

  @Override
  public List<IDatabaseSchemaName> getSchemaNames(final ICanceler canceler,
      final Connection connection,
      final String catalog)
      throws SQLException {
    return DatabaseUtilities
        .metadatas(canceler,
            connection,
            metadata -> metadata.getSchemas(),
            resultSet -> {
              final String schemaName = resultSet.getString(1);
              return new DatabaseSchemaName(catalog, schemaName);
            });
  }

  @Override
  public List<IDatabaseTableName> getTables(
      final ICanceler canceler,
      final Connection connection,
      final IDatabaseSchemaName name) throws SQLException {
    return getTableNames(
        canceler,
        connection,
        name,
        new String[] { "TABLE", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM" },
        (schema, tableName) -> new DatabaseTableName(name.getCatalogName(), name.getSchemaName(), tableName));
  }

  private <T> List<T> getTableNames(
      final ICanceler canceler,
      final Connection connection,
      final IDatabaseSchemaName name,
      final String[] tableTypes,
      final BiFunction<String, String, T> factory) throws SQLException {
    return DatabaseUtilities
        .metadatas(canceler,
            connection,
            metadata -> metadata.getTables(name.getCatalogName(), name.getSchemaName(), null, tableTypes),
            resultSet -> {
              return factory.apply(resultSet.getString(2), resultSet.getString(3));
            });
  }

  @Override
  public boolean supportsSequences() {
    return true;
  }

  @Override
  public List<IDatabaseSequenceName> getSequences(final ICanceler canceler,
      final Connection connection,
      final IDatabaseSchemaName name)
      throws SQLException {
    return getTableNames(
        canceler,
        connection,
        name,
        new String[] { "SEQUENCE" },
        (schema, sequenceName) -> new DatabaseSequenceName(schema, sequenceName));
  }

  @Override
  public ResultSet getSequenceMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseSequenceName name)
      throws SQLException {
    return null;
  }

  @Override
  public ResultSet getIndexMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseIndexName name) throws SQLException {
    return null;
  }

  @Override
  public ResultSet getTriggerMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTriggerName name)
      throws SQLException {
    return null;
  }

  @Override
  public String getTriggerStatement(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTriggerName name)
      throws SQLException {
    return null;
  }

  @Override
  public List<IDatabaseIndexName> getIndicies(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName name)
      throws SQLException {
    return DatabaseUtilities
        .metadatas(canceler,
            connection,
            metadata -> metadata.getIndexInfo(name.getCatalogName(),
                name.getSchemaName(),
                name.getTableName(),
                false,
                false),
            resultSet -> {
              return new DatabaseIndexName(name.getCatalogName(),
                  name.getSchemaName(),
                  resultSet.getString(6));
            });
  }

  @Override
  public boolean supportsTrigger() {
    return false;
  }

  @Override
  public List<IDatabaseTriggerName> getTriggers(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName name)
      throws SQLException {
    return Collections.emptyList();
  }

  @Override
  public boolean supportsConstaints() {
    return false;
  }

  @Override
  public List<IDatabaseConstraintName> getConstraints(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName name)
      throws SQLException {
    return Collections.emptyList();
  }

  @Override
  public ResultSet getConstraintMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName table,
      final IDatabaseConstraintName name)
      throws SQLException {
    return null;
  }

  @Override
  public ResultSet getTableMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName name)
      throws CanceledException,
      SQLException {
    return Optional.of(SQLException.class, getMetaData(connection))
        .convert(metadata -> metadata
            .getColumns(name.getCatalogName(),
                name.getSchemaName(),
                name.getTableName(),
                null))
        .get();
  }

  @Override
  public ResultSet getDataTypes(final ICanceler canceler, final Connection connection) throws CanceledException,
      SQLException {
    return connection.getMetaData().getTypeInfo();
  }

  @Override
  public List<Property> getCapabilities(final ICanceler canceler,
      final Connection connection)
      throws CanceledException,
      SQLException {
    final DatabaseMetaData metadata = connection.getMetaData();
    Set<String> names = new HashSet<>();
    List<Property> properties = new ArrayList<>();
    addToProperties(properties,
        names,
        "Product Name",
        () -> metadata.getDatabaseProductName(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Product Version",
        () -> metadata.getDatabaseProductVersion(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Driver Name",
        () -> metadata.getDriverName(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Driver Version",
        () -> metadata.getDriverVersion(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "JDBC Version",
        () -> metadata.getJDBCMajorVersion() + "." + metadata.getJDBCMinorVersion(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MaxCatalogNameLength",
        () -> metadata.getMaxCatalogNameLength(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MaxSchemaNameLength",
        () -> metadata.getMaxSchemaNameLength(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MaxTableNameLength",
        () -> metadata.getMaxTableNameLength(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MaxColumnNameLength",
        () -> metadata.getMaxColumnNameLength(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MaxStatementLength",
        () -> metadata.getMaxStatementLength(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CatalogSeparator",
        () -> metadata.getCatalogSeparator(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "IdentifierQuoteString",
        () -> metadata.getIdentifierQuoteString(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "ExtraNameCharacters",
        () -> metadata.getExtraNameCharacters(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SearchStringEscape",
        () -> metadata.getSearchStringEscape(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SQLKeywords",
        () -> metadata.getSQLKeywords(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "AlterTableWithAddColumn",
        () -> metadata.supportsAlterTableWithAddColumn(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "AlterTableWithDropColumn",
        () -> metadata.supportsAlterTableWithDropColumn(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "ANSI92EntryLevelSQL",
        () -> metadata.supportsANSI92EntryLevelSQL(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "ANSI92IntermediateSQL",
        () -> metadata.supportsANSI92IntermediateSQL(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "BatchUpdates",
        () -> metadata.supportsBatchUpdates(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CatalogsInDataManipulation",
        () -> metadata.supportsCatalogsInDataManipulation(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CatalogsInIndexDefinitions",
        () -> metadata.supportsCatalogsInIndexDefinitions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CatalogsInPrivilegeDefinitions",
        () -> metadata.supportsCatalogsInPrivilegeDefinitions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CatalogsInProcedureCalls",
        () -> metadata.supportsCatalogsInProcedureCalls(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CatalogsInTableDefinitions",
        () -> metadata.supportsCatalogsInTableDefinitions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "ColumnAliasing",
        () -> metadata.supportsColumnAliasing(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Convert",
        () -> metadata.supportsConvert(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CoreSQLGrammar",
        () -> metadata.supportsCoreSQLGrammar(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "CorrelatedSubqueries",
        () -> metadata.supportsCorrelatedSubqueries(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "DataDefinitionAndDataManipulationTransactions",
        () -> metadata.supportsDataDefinitionAndDataManipulationTransactions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "DataManipulationTransactionsOnly",
        () -> metadata.supportsDataManipulationTransactionsOnly(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "DifferentTableCorrelationNames",
        () -> metadata.supportsDifferentTableCorrelationNames(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "ExpressionsInOrderBy",
        () -> metadata.supportsExpressionsInOrderBy(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "ExtendedSQLGrammar",
        () -> metadata.supportsExtendedSQLGrammar(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "FullOuterJoins",
        () -> metadata.supportsFullOuterJoins(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "GetGeneratedKeys",
        () -> metadata.supportsGetGeneratedKeys(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "GroupBy",
        () -> metadata.supportsGroupBy(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "GroupByBeyondSelect",
        () -> metadata.supportsGroupByBeyondSelect(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "GroupByUnrelated",
        () -> metadata.supportsGroupByUnrelated(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "IntegrityEnhancementFacility",
        () -> metadata.supportsIntegrityEnhancementFacility(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "LikeEscapeClause",
        () -> metadata.supportsLikeEscapeClause(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "LimitedOuterJoins",
        () -> metadata.supportsLimitedOuterJoins(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MinimumSQLGrammar",
        () -> metadata.supportsMinimumSQLGrammar(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MixedCaseIdentifiers",
        () -> metadata.supportsMixedCaseIdentifiers(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MixedCaseQuotedIdentifiers",
        () -> metadata.supportsMixedCaseQuotedIdentifiers(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MultipleOpenResults",
        () -> metadata.supportsMultipleOpenResults(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MultipleResultSets",
        () -> metadata.supportsMultipleResultSets(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "MultipleTransactions",
        () -> metadata.supportsMultipleTransactions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "NamedParameters",
        () -> metadata.supportsNamedParameters(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "NonNullableColumns",
        () -> metadata.supportsNonNullableColumns(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "OpenCursorsAcrossCommit",
        () -> metadata.supportsOpenCursorsAcrossCommit(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "OpenCursorsAcrossRollback",
        () -> metadata.supportsOpenCursorsAcrossRollback(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "OpenStatementsAcrossCommit",
        () -> metadata.supportsOpenStatementsAcrossCommit(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "OpenStatementsAcrossRollback",
        () -> metadata.supportsOpenStatementsAcrossRollback(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "PositionedDelete",
        () -> metadata.supportsPositionedDelete(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "PositionedUpdate",
        () -> metadata.supportsPositionedUpdate(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "RefCursors",
        () -> metadata.supportsRefCursors(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Savepoints",
        () -> metadata.supportsSavepoints(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SchemasInDataManipulation",
        () -> metadata.supportsSchemasInDataManipulation(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SchemasInIndexDefinitions",
        () -> metadata.supportsSchemasInIndexDefinitions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SchemasInPrivilegeDefinitions",
        () -> metadata.supportsSchemasInPrivilegeDefinitions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SchemasInProcedureCalls",
        () -> metadata.supportsSchemasInProcedureCalls(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SchemasInTableDefinitions",
        () -> metadata.supportsSchemasInTableDefinitions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SelectForUpdate",
        () -> metadata.supportsSelectForUpdate(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Sharding",
        () -> metadata.supportsSharding(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "StatementPooling",
        () -> metadata.supportsStatementPooling(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "StoredFunctionsUsingCallSyntax",
        () -> metadata.supportsStoredFunctionsUsingCallSyntax(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "StoredProcedures",
        () -> metadata.supportsStoredProcedures(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SubqueriesInComparisons",
        () -> metadata.supportsSubqueriesInComparisons(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SubqueriesInExists",
        () -> metadata.supportsSubqueriesInExists(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SubqueriesInIns",
        () -> metadata.supportsSubqueriesInIns(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "SubqueriesInQuantifieds",
        () -> metadata.supportsSubqueriesInQuantifieds(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "TableCorrelationNames",
        () -> metadata.supportsTableCorrelationNames(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Transactions",
        () -> metadata.supportsTransactions(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "TransactionIsolationLevel - none",
        () -> metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "TransactionIsolationLevel - read committed",
        () -> metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "TransactionIsolationLevel - read uncommitted",
        () -> metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "TransactionIsolationLevel - repeatable read",
        () -> metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "TransactionIsolationLevel - serializable",
        () -> metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "Union",
        () -> metadata.supportsUnion(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "UnionAll",
        () -> metadata.supportsUnionAll(),
        null,
        null,
        null,
        false);
    return properties;
  }

  @Override
  public List<Property> getDatabaseProperties(final ICanceler canceler,
      final Connection connection)
      throws CanceledException,
      SQLException {
    Set<String> names = new HashSet<>();
    List<Property> properties = new ArrayList<>();
    addToProperties(properties,
        names,
        "time zone",
        getTimeZone(canceler, connection),
        null,
        null,
        null);
    return properties;
  }

  @Override
  public List<Property> getClientProperties(final ICanceler canceler,
      final Connection connection)
      throws CanceledException,
      SQLException {
    Set<String> names = new HashSet<>();
    List<Property> properties = new ArrayList<>();

    addToProperties(properties,
        names,
        "read only",
        connection.isReadOnly(),
        "read-only mode as a hint to the driver to enable database optimizations.",
        null,
        false);
    addToProperties(properties,
        names,
        "read only",
        connection.isReadOnly(),
        "read-only mode as a hint to the driver to enable database optimizations.",
        null,
        false);
    addToProperties(properties,
        names,
        "auto-commit mode",
        connection.getAutoCommit(),
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "transaction isolation level",
        () -> transactionIsolationToString(connection.getTransactionIsolation()),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "result set cursour holdability",
        () -> resultSetCursourHoldability(connection.getHoldability()),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "network timeout",
        () -> connection.getNetworkTimeout(),
        "the number of milliseconds the driver will wait for a database request to complete",
        null,
        null,
        null);

    final DatabaseMetaData metaData = connection.getMetaData();

    addToProperties(properties,
        names,
        "autoCommitFailureClosesAllResultSets",
        () -> metaData.autoCommitFailureClosesAllResultSets(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "allProceduresAreCallable",
        () -> metaData.allProceduresAreCallable(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "allTablesAreSelectable",
        () -> metaData.allTablesAreSelectable(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "dataDefinitionCausesTransactionCommit",
        () -> metaData.dataDefinitionCausesTransactionCommit(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "dataDefinitionIgnoredInTransactions",
        () -> metaData.dataDefinitionIgnoredInTransactions(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "doesMaxRowSizeIncludeBlobs",
        () -> metaData.doesMaxRowSizeIncludeBlobs(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "generatedKeyAlwaysReturned",
        () -> metaData.generatedKeyAlwaysReturned(),
        null,
        null,
        null,
        false);
    addToProperties(properties,
        names,
        "isCatalogAtStart",
        () -> metaData.isCatalogAtStart(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "locatorsUpdateCopy",
        () -> metaData.locatorsUpdateCopy(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "nullPlusNonNullIsNull",
        () -> metaData.nullPlusNonNullIsNull(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "nullsAreSortedAtEnd",
        () -> metaData.nullsAreSortedAtEnd(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "nullsAreSortedAtStart",
        () -> metaData.nullsAreSortedAtStart(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "nullsAreSortedHigh",
        () -> metaData.nullsAreSortedHigh(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "nullsAreSortedLow",
        () -> metaData.nullsAreSortedLow(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "storesLowerCaseIdentifiers",
        () -> metaData.storesLowerCaseIdentifiers(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "storesLowerCaseQuotedIdentifiers",
        () -> metaData.storesLowerCaseQuotedIdentifiers(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "storesMixedCaseIdentifiers",
        () -> metaData.storesMixedCaseIdentifiers(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "storesMixedCaseQuotedIdentifiers",
        () -> metaData.storesMixedCaseQuotedIdentifiers(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "storesUpperCaseIdentifiers",
        () -> metaData.storesUpperCaseIdentifiers(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "storesUpperCaseQuotedIdentifiers",
        () -> metaData.storesUpperCaseQuotedIdentifiers(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "usesLocalFiles",
        () -> metaData.usesLocalFiles(),
        null,
        null,
        null,
        null);
    addToProperties(properties,
        names,
        "usesLocalFilePerTable",
        () -> metaData.usesLocalFilePerTable(),
        null,
        null,
        null,
        null);
    try (ResultSet resultSet = metaData.getClientInfoProperties()) {
      while (resultSet.next()) {
        String name = resultSet.getString(1);
        names.add(name);
        Object value = connection.getClientInfo(name);
        String description = resultSet.getString(4);
        Integer maximumLength = DatabaseUtilities.getInteger(resultSet, 2);
        Object defaultValue = resultSet.getObject(3);
        properties.add(new Property(name, value, description, maximumLength, defaultValue));
      }
    } catch (SQLFeatureNotSupportedException exception) {
      // Nothing to do
    }
    final Properties clientInfo = connection.getClientInfo();
    if (clientInfo != null) {
      clientInfo.forEach((k, v) -> {
        if (names.contains(k)) {
          return;
        }
        properties.add(new Property(k.toString(), v, null, null, null));
      });
    }
    return properties;
  }

  private void addToProperties(final List<Property> properties,
      final Set<String> names,
      final String name,
      final Object value,
      final String description,
      final Integer maximumLength,
      final Object defaultValue) {
    if (names.contains(name)) {
      return;
    }
    properties.add(new Property(name, value, description, maximumLength, defaultValue));
    names.add(name);
  }

  private void addToProperties(final List<Property> properties,
      final Set<String> names,
      final String name,
      final ISupplier<Object, SQLException> supplier,
      final String description,
      final Integer maximumLength,
      final Object defaultValue,
      final Object failedValue) {
    if (names.contains(name)) {
      return;
    }
    try {
      properties.add(new Property(name, supplier.supply(), description, maximumLength, defaultValue));
      names.add(name);
    } catch (SQLFeatureNotSupportedException exception) {
      if (failedValue != null) {
        properties.add(new Property(name, failedValue, description, maximumLength, defaultValue));
        names.add(name);
      }
    } catch (SQLException exception) {
    }
  }

  private Object resultSetCursourHoldability(final int holdability) {
    return switch (holdability) {
      case 0 -> "hold cursors over commit";
      case 1 -> "close cursors at commit";
      default -> "unkown";
    };
  }

  private String transactionIsolationToString(final int transactionIsolation) {
    return switch (transactionIsolation) {
      case 0 -> "transactions are not supported";
      case 1 -> "read uncommited";
      case 2 -> "read commited";
      case 4 -> "repeatable read";
      case 8 -> "serializable";
      default -> "unkown";
    };
  }

  @Override
  public boolean supportsTableStatement() {
    return false;
  }

  @Override
  public String getTableStatement(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName tableName)
      throws SQLException {
    return null;
  }

  @Override
  public boolean isTable(final IDatabaseTableName name) {
    return name instanceof DatabaseTableName;
  }

  @Override
  public Iterable<INamedTableFilter> getTableFilters() {
    return Collections.emptyList();
  }

  @Override
  public List<IDatabaseColumnName> getTableColumns(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName name)
      throws SQLException {
    return DatabaseUtilities
        .metadatas(canceler,
            connection,
            metadata -> metadata.getColumns(
                name.getCatalogName(),
                name.getSchemaName(),
                name.getTableName(),
                null),
            resultSet -> {
              final String columnName = resultSet.getString(4);
              return new DatabaseColumnName(name, columnName);
            });
  }

  @Override
  public List<IDatabaseViewName>
      getViews(final ICanceler canceler,
          final Connection connection,
          final IDatabaseSchemaName name)
          throws SQLException {
    return getTableNames(
        canceler,
        connection,
        name,
        new String[] { "VIEW" },
        (schema, viewName) -> new DatabaseViewName(schema, viewName));
  }

  @Override
  public boolean isView(final IDatabaseTableName name) {
    return name instanceof DatabaseViewName;
  }

  @Override
  public List<IDatabaseColumnName> getViewColumns(final ICanceler canceler,
      final Connection connection,
      final IDatabaseViewName name)
      throws SQLException {
    return getTableColumns(canceler, connection, name);
  }

  @Override
  public DatabaseMetaData getMetaData(final Connection connection) throws SQLException {
    return connection == null ? null : connection.getMetaData();
  }

  @Override
  public ResultSet getTableColumnMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseColumnName name)
      throws CanceledException,
      SQLException {
    IDatabaseTableName tableName = name.getDatabaseTable();
    return Optional.of(SQLException.class, getMetaData(connection))
        .convert(metadata -> metadata
            .getColumns(tableName.getCatalogName(),
                tableName.getSchemaName(),
                tableName.getTableName(),
                name.getColumnName()))
        .get();
  }

  @Override
  public ResultSet
      getColumnPrivileges(final Connection connection, final IDatabaseTableName name, final String column)
          throws SQLException {
    return Optional.of(SQLException.class, getMetaData(connection))
        .convert(metadata -> metadata
            .getColumnPrivileges(name.getCatalogName(),
                name.getSchemaName(),
                name.getTableName(),
                column))
        .get();
  }

  @Override
  public ResultSet getTablePrivileges(final Connection connection, final IDatabaseTableName name) throws SQLException {
    return Optional.of(SQLException.class, getMetaData(connection))
        .convert(metadata -> metadata
            .getTablePrivileges(name.getCatalogName(),
                name.getSchemaName(),
                name.getTableName()))
        .get();
  }

}
