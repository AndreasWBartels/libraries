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
package net.anwiba.database.postgresql;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.DatabaseFacade;
import net.anwiba.commons.jdbc.database.IRegisterableDatabaseFacade;
import net.anwiba.commons.jdbc.name.DatabaseConstraintName;
import net.anwiba.commons.jdbc.name.DatabaseIndexName;
import net.anwiba.commons.jdbc.name.DatabaseTriggerName;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSchemaName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.software.ServiceDatabaseSoftware;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.time.TimeZoneUtilities;
import net.anwiba.database.postgresql.utilities.PostgresqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

public class PostgresqlDatabaseFacade extends DatabaseFacade implements IRegisterableDatabaseFacade {

  public PostgresqlDatabaseFacade() {
  }

  @Override
  public boolean supportsSequences() {
    return true;
  }

  // show timezone
  // show all

  @Override
  public TimeZone getTimeZone(final ICanceler canceler, final Connection connection) throws SQLException {
    return DatabaseUtilities.result(
        canceler,
        connection,
        "show timezone",
        (IConverter<IOptional<IResult, SQLException>, TimeZone, SQLException>) optional -> optional
            .convert(v -> TimeZoneUtilities.get(v.getString(1)))
            .get());
  }

  @Override
  public boolean isInformationSchema(final IDatabaseSchemaName schemaName) {
    return schemaName != null
        && schemaName.getSchemaName() != null
        && Set.of("information_schema", "pg_catalog").contains(schemaName.getSchemaName());
  }

  @Override
  public ResultSet getSequenceMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseSequenceName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        canceler,
        connection,
        """
            select *
              from information_schema.sequences
             where sequence_schema = ?
               and sequence_name = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getSequenceName());
        });
  }

  @Override
  public boolean isApplicable(final IJdbcConnectionDescription context) {
    return Objects.equals(ServiceDatabaseSoftware.POSTGRES.getDriverName(), context.getDriverName());
  }

  @Override
  public List<IDatabaseTriggerName> getTriggers(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        canceler,
        connection,
        """
            select ns.nspname
                    , tg.tgname
              from pg_trigger tg,
                      pg_class rc,
                      pg_namespace ns
            where rc.oid = tg.tgrelid
               and ns.oid = rc.relnamespace
               and ns.nspname = ?
               and rc.relname = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseTriggerName, SQLException>) value -> new DatabaseTriggerName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getTriggerMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTriggerName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        canceler,
        connection,
        """
            select *
              from information_schema.triggers
             where trigger_schema = ?
               and trigger_name = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        });
  }

  @Override
  public String getTriggerStatement(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTriggerName name) throws SQLException {
    final String triggerFunction = DatabaseUtilities.result(
        canceler,
        connection,
        """
            select pr.proname
                    ,pr.prosrc
              from pg_trigger tg
                    , pg_class rc
                    , pg_namespace ns
                    , pg_proc pr
            where pr.oid = tg.tgfoid
               and rc.oid = tg.tgrelid
               and ns.oid = rc.relnamespace
               and ns.nspname = ?
               and tg.tgname = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        },
        (IConverter<IOptional<IResult, SQLException>, String, SQLException>) value -> value.convert(v -> {
          final String name1 = v.getString(1);
          final String body = v.getString(2);
          return "CREATE OR REPLACE FUNCTION " + name1 + "() RETURNS TRIGGER AS $$ " + body + "$$ LANGUAGE plpgsql;"; //$NON-NLS-3$
        }).getOr(() -> ""));

    final List<String> events = DatabaseUtilities.results(
        canceler,
        connection,
        """
            select event_manipulation
              from information_schema.triggers
             where trigger_schema = ?
               and trigger_name = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        },
        (IConverter<IResult, String, SQLException>) value -> value.getString(1));

    final String trigger = DatabaseUtilities.result(
        canceler,
        connection,
        """
            select distinct action_timing
                    , event_object_table
                    , action_orientation
                    , action_statement
              from information_schema.triggers
             where trigger_schema = ?
               and trigger_name = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        },
        (IConverter<IOptional<IResult, SQLException>, String, SQLException>) value -> value.convert(v -> {
          return " "
              + "\n   "
              + v.getString(1)
              + " "
              + StringUtilities.concatenatedString(" OR ", events)
              + " ON "
              + v.getString(2)
              + "\n   FOR EACH "
              + v.getString(3)
              + " "
              + v.getString(4)
              + ";";
        }).getOr(() -> ""));

    final String createTrigger = "CREATE TRIGGER " + name.getTriggerName() + trigger;

    return triggerFunction + "\n\n" + createTrigger;
  }

  @Override
  public boolean supportsTrigger() {
    return true;
  }

  @Override
  public List<IDatabaseIndexName> getIndicies(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        canceler,
        connection,
        """
            select distinct s.nspname as schema,
                   i.relname as index
              from pg_index ki,
                   pg_namespace s,
                   pg_class t,
                   pg_class i,
                   pg_attribute a
             where ki.indrelid = t.oid
               and ki.indexrelid = i.oid
               and s.oid = t.relnamespace
               and t.oid = a.attrelid
               and a.attnum = ANY (ki.indkey)
               and s.nspname = ?
               and t.relname = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseIndexName, SQLException>) value -> new DatabaseIndexName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getIndexMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseIndexName name) throws SQLException {
    return DatabaseUtilities.resultSet(
        canceler,
        connection,
        """
            select s.nspname as schema,
                   i.relname as index,
                   a.attname as column,
                   ki.*
              from pg_index ki,
                   pg_namespace s,
                   pg_class t,
                   pg_class i,
                   pg_attribute a
             where ki.indrelid = t.oid
               and ki.indexrelid = i.oid
               and s.oid = t.relnamespace
               and t.oid = a.attrelid
               and a.attnum = ANY (ki.indkey)
               and s.nspname = ?
               and i.relname = ?""",
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getIndexName());
        });
  }

  @Override
  public List<IDatabaseConstraintName> getConstraints(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        canceler,
        connection,
        """
            select s.nspname,
                   c.conname
              from pg_namespace s,
                   pg_class t,
                   pg_constraint c
             where s.nspname = ?
               and s.oid = t.relnamespace
               and t.relname = ?
               and t.oid = c.conrelid""",
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseConstraintName, SQLException>) value -> new DatabaseConstraintName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getConstraintMetadata(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName table,
      final IDatabaseConstraintName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        canceler,
        connection,
        """
            select *
              from information_schema.table_constraints
             where constraint_schema = ?
               and constraint_name = ?""", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getConstraintName());
        });
  }

  @Override
  public boolean supportsConstaints() {
    return true;
  }

  @Override
  public String getTableStatement(final ICanceler canceler,
      final Connection connection,
      final IDatabaseTableName tableName)
      throws SQLException {
    return PostgresqlUtilities.createStatement(connection, tableName.getSchemaName(), tableName.getTableName());
  }

  @Override
  public boolean supportsTableStatement() {
    return true;
  }
}
