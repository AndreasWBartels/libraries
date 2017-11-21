/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;

public interface IDatabaseFacade {

  //  List<IDatabaseIndexName> getIndicies(Connection connection, String schema) throws SQLException;

  List<IDatabaseIndexName> getIndicies(Connection connection, IDatabaseTableName tableName) throws SQLException;

  ResultSet getIndexMetadata(Connection connection, IDatabaseIndexName schema) throws SQLException;

  List<IDatabaseTriggerName> getTriggers(Connection connection, IDatabaseTableName tableName) throws SQLException;

  ResultSet getTriggerMetadata(Connection connection, IDatabaseTriggerName schema) throws SQLException;

  String getTriggerStatement(Connection connection, IDatabaseTriggerName schema) throws SQLException;

  List<IDatabaseSequenceName> getSequences(Connection connection, String schema) throws SQLException;

  ResultSet getSequenceMetadata(Connection connection, IDatabaseSequenceName schema) throws SQLException;

  boolean supportsSequences();

  boolean supportsTrigger();

  boolean supportsIndicies();

  List<IDatabaseConstraintName> getConstraints(Connection connection, IDatabaseTableName tableName) throws SQLException;

  ResultSet getConstraintMetadata(Connection connection, IDatabaseConstraintName schema) throws SQLException;

  boolean supportsConstaints();

  boolean isTable(IDatabaseTableName table);

  Iterable<INamedTableFilter> getTableFilters();

}
