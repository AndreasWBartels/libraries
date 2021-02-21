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
package net.anwiba.database.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.DatabaseFacade;
import net.anwiba.commons.jdbc.database.INamedTableFilter;
import net.anwiba.commons.jdbc.database.IRegistrableDatabaseFacade;
import net.anwiba.commons.jdbc.name.DatabaseIndexName;
import net.anwiba.commons.jdbc.name.DatabaseSequenceName;
import net.anwiba.commons.jdbc.name.DatabaseTriggerName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.software.FileDatabaseSoftware;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.optional.IOptional;

public class SqliteDatabaseFacade extends DatabaseFacade implements IRegistrableDatabaseFacade {

  public SqliteDatabaseFacade() {
  }

  @SuppressWarnings("nls")
  @Override
  public boolean isTable(final IDatabaseTableName table) {
    if (isSpatialMetaData(table) || isSpatialIndex(table) || Objects.equals(table.getTableName(), "sqlite_sequence")) {
      return false;
    }
    return super.isTable(table);
  }

  @Override
  public String getTableStatement(final Connection connection, final IDatabaseTableName tableName) throws SQLException {
    return DatabaseUtilities.result(
        connection,
        "SELECT sql FROM sqlite_master WHERE type = 'table' and name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getTableName());
        },
        (IConverter<IOptional<IResult, SQLException>, String, SQLException>) optional -> optional
            .convert(v -> v.getString(1))
            .get());
  }

  @Override
  public boolean supportsTableStatement() {
    return true;
  }

  @SuppressWarnings("nls")
  private boolean isSpatialMetaData(final IDatabaseTableName table) {
    return table.getTableName().startsWith("virts_")
        || table.getTableName().startsWith("vector_")
        || table.getTableName().startsWith("geometry_columns")
        || table.getTableName().startsWith("views_geometry_columns")
        || table.getTableName().startsWith("gpkg_")
        || Objects.equals(table.getTableName(), "geo_col_ref_sys")
        || Objects.equals(table.getTableName(), "spatial_ref_sys")
        || Objects.equals(table.getTableName(), "SpatialIndex")
        || Objects.equals(table.getTableName(), "spatialite_history");
  }

  @SuppressWarnings("nls")
  private boolean isSpatialIndex(final IDatabaseTableName table) {
    return table.getTableName().startsWith("idx_") || table.getTableName().startsWith("rtree_");
  }

  @Override
  public Iterable<INamedTableFilter> getTableFilters() {
    return Arrays.asList(new INamedTableFilter() {

      @SuppressWarnings("nls")
      @Override
      public String getName() {
        return "SpatialMetadata";
      }

      @Override
      public boolean accept(final IDatabaseTableName table) {
        return isSpatialMetaData(table);
      }
    }, new INamedTableFilter() {

      @SuppressWarnings("nls")
      @Override
      public String getName() {
        return "SpatialIndex";
      }

      @Override
      public boolean accept(final IDatabaseTableName table) {
        return isSpatialIndex(table);
      }
    });
  }

  @Override
  public List<IDatabaseIndexName> getIndicies(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "SELECT null, name FROM sqlite_master WHERE type = 'index' and tbl_name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseIndexName, SQLException>) value -> new DatabaseIndexName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getIndexMetadata(final Connection connection, final IDatabaseIndexName name) throws SQLException {
    final ResultSet resultSet = DatabaseUtilities.resultSet(
        connection,
        "PRAGMA index_xinfo('" + name.getIndexName() + "')", //$NON-NLS-1$ //$NON-NLS-2$
        (IProcedure<PreparedStatement, SQLException>) value -> {});
    return resultSet;
  }

  @Override
  public boolean supportsIndicies() {
    return true;
  }

  @Override
  public List<IDatabaseTriggerName> getTriggers(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "SELECT null, name FROM sqlite_master WHERE type = 'trigger' and tbl_name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseTriggerName, SQLException>) value -> new DatabaseTriggerName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public String getTriggerStatement(final Connection connection, final IDatabaseTriggerName triggerName)
      throws SQLException {
    return DatabaseUtilities.result(
        connection,
        "SELECT sql FROM sqlite_master WHERE type = 'trigger' and name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, triggerName.getTriggerName());
        },
        (IConverter<IOptional<IResult, SQLException>, String, SQLException>) optional -> optional
            .convert(v -> v.getString(1))
            .get());
  }

  @Override
  public boolean supportsTrigger() {
    return true;
  }

  @Override
  public List<IDatabaseSequenceName> getSequences(final Connection connection, final String schema)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "SELECT null, name FROM sqlite_sequence", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {},
        (IConverter<IResult, IDatabaseSequenceName, SQLException>) value -> new DatabaseSequenceName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getSequenceMetadata(final Connection connection, final IDatabaseSequenceName sequence)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "SELECT * FROM sqlite_sequence WHERE name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, sequence.getSequenceName());
        });
  }

  @Override
  public boolean supportsSequences() {
    return true;
  }

  @Override
  public boolean isApplicable(final IJdbcConnectionDescription context) {
    return Objects.equals(FileDatabaseSoftware.SQLITE.getDriverName(), context.getDriverName());
  }

}
