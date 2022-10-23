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

import net.anwiba.commons.jdbc.metadata.Property;
import net.anwiba.commons.jdbc.name.IDatabaseColumnName;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSchemaName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.jdbc.name.IDatabaseViewName;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.time.TimeZoneUtilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TimeZone;

public interface IDatabaseFacade {

  List<IDatabaseSchemaName> getSchemaNames(ICanceler canceler, Connection connection, String catalogName)
      throws SQLException,
      CanceledException;

  default boolean isInformationSchema(final IDatabaseSchemaName name) {
    return false;
  }

  List<IDatabaseTableName> getTables(ICanceler canceler, Connection connection, IDatabaseSchemaName name)
      throws SQLException,
      CanceledException;

  // table, view, synonym
  ResultSet getTableMetadata(ICanceler canceler, Connection connection, IDatabaseTableName name) throws SQLException,
      CanceledException;

  boolean isTable(IDatabaseTableName table);

  Iterable<INamedTableFilter> getTableFilters();

  boolean supportsTableStatement();

  String getTableStatement(ICanceler canceler, final Connection connection, final IDatabaseTableName name)
      throws SQLException,
      CanceledException;

  List<IDatabaseColumnName> getTableColumns(ICanceler canceler, Connection connection, IDatabaseTableName name)
      throws SQLException,
      CanceledException;

  // table, view, synonym
  ResultSet getTableColumnMetadata(ICanceler canceler, Connection connection, IDatabaseColumnName name)
      throws SQLException,
      CanceledException;

  List<IDatabaseViewName> getViews(ICanceler canceler, Connection connection, IDatabaseSchemaName name)
      throws SQLException,
      CanceledException;

  boolean isView(IDatabaseTableName name);

  List<IDatabaseColumnName> getViewColumns(ICanceler canceler, Connection connection, IDatabaseViewName name)
      throws SQLException,
      CanceledException;

  boolean supportsSequences();

  List<IDatabaseSequenceName> getSequences(ICanceler canceler, Connection connection, IDatabaseSchemaName name)
      throws SQLException,
      CanceledException;

  ResultSet getSequenceMetadata(ICanceler canceler, Connection connection, IDatabaseSequenceName name)
      throws SQLException,
      CanceledException;

  boolean supportsTrigger();

  List<IDatabaseTriggerName> getTriggers(ICanceler canceler, Connection connection, IDatabaseTableName name)
      throws SQLException,
      CanceledException;

  ResultSet getTriggerMetadata(ICanceler canceler, Connection connection, IDatabaseTriggerName name)
      throws SQLException,
      CanceledException;

  String getTriggerStatement(ICanceler canceler, Connection connection, IDatabaseTriggerName name) throws SQLException,
      CanceledException;

  List<IDatabaseIndexName> getIndicies(ICanceler canceler, Connection connection, IDatabaseTableName name)
      throws SQLException,
      CanceledException;

  ResultSet getIndexMetadata(ICanceler canceler, Connection connection, IDatabaseIndexName name) throws SQLException,
      CanceledException;

  boolean supportsConstaints();

  List<IDatabaseConstraintName> getConstraints(ICanceler canceler, Connection connection, IDatabaseTableName name)
      throws SQLException,
      CanceledException;

  ResultSet getConstraintMetadata(ICanceler canceler,
      Connection connection,
      IDatabaseTableName table,
      IDatabaseConstraintName name)
      throws SQLException,
      CanceledException;

  String quoted(IDatabaseTableName name);

  String quoted(String name);

  default TimeZone getTimeZone(final ICanceler canceler, final Connection connection) throws SQLException {
    return TimeZoneUtilities.getUniversalTimeZone();
  }

  List<Property> getClientProperties(ICanceler canceler,
      Connection connection)
      throws CanceledException,
      SQLException;

  List<Property> getDatabaseProperties(ICanceler canceler,
      Connection connection)
      throws CanceledException,
      SQLException;

  List<Property> getCapabilities(ICanceler canceler, Connection connection) throws CanceledException,
      SQLException;

  ResultSet getDataTypes(ICanceler canceler, Connection connection) throws CanceledException, SQLException;

  DatabaseMetaData getMetaData(Connection connection) throws SQLException;

  ResultSet getColumnPrivileges(Connection connection,
      IDatabaseTableName name,
      String column) throws SQLException;

  ResultSet getTablePrivileges(Connection connection, IDatabaseTableName tableName) throws SQLException;
}
