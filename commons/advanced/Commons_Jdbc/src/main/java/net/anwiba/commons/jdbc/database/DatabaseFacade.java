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
import java.util.Collections;
import java.util.List;

import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;

public class DatabaseFacade implements IDatabaseFacade {

  @Override
  public List<IDatabaseSequenceName> getSequences(final Connection connection, final String schema)
      throws SQLException {
    return Collections.emptyList();
  }

  @Override
  public ResultSet getSequenceMetadata(final Connection connection, final IDatabaseSequenceName schema)
      throws SQLException {
    return null;
  }

  @Override
  public boolean supportsSequences() {
    return false;
  }

  @Override
  public ResultSet getIndexMetadata(final Connection connection, final IDatabaseIndexName schema) throws SQLException {
    return null;
  }

  @Override
  public ResultSet getTriggerMetadata(final Connection connection, final IDatabaseTriggerName schema)
      throws SQLException {
    return null;
  }

  @Override
  public String getTriggerStatement(final Connection connection, final IDatabaseTriggerName schema)
      throws SQLException {
    return null;
  }

  @Override
  public boolean supportsTrigger() {
    return false;
  }

  @Override
  public boolean supportsIndicies() {
    return false;
  }

  @Override
  public List<IDatabaseIndexName> getIndicies(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return Collections.emptyList();
  }

  @Override
  public List<IDatabaseTriggerName> getTriggers(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return Collections.emptyList();
  }

  @Override
  public List<IDatabaseConstraintName> getConstraints(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return Collections.emptyList();
  }

  @Override
  public ResultSet getConstraintMetadata(final Connection connection, final IDatabaseConstraintName schema)
      throws SQLException {
    return null;
  }

  @Override
  public boolean supportsConstaints() {
    return false;
  }

  @Override
  public boolean isTable(final IDatabaseTableName table) {
    return true;
  }

  @Override
  public Iterable<INamedTableFilter> getTableFilters() {
    return Collections.emptyList();
  }

}
