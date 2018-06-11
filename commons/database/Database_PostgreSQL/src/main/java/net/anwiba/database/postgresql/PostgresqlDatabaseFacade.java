/*
 * #%L
 * *
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.DatabaseFacade;
import net.anwiba.commons.jdbc.database.IRegistrableDatabaseFacade;
import net.anwiba.commons.jdbc.name.DatabaseConstraintName;
import net.anwiba.commons.jdbc.name.DatabaseIndexName;
import net.anwiba.commons.jdbc.name.DatabaseSequenceName;
import net.anwiba.commons.jdbc.name.DatabaseTriggerName;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.software.ServiceDatabaseSoftware;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.utilities.string.StringUtilities;

public class PostgresqlDatabaseFacade extends DatabaseFacade implements IRegistrableDatabaseFacade {

  public PostgresqlDatabaseFacade() {
  }

  @Override
  public List<IDatabaseSequenceName> getSequences(final Connection connection, final String schema)
      throws SQLException {
    final List<IDatabaseSequenceName> names = new ArrayList<>();
    try (ResultSet resultSet = connection.getMetaData().getTables(null, schema, null, new String[]{ "SEQUENCE" }); //$NON-NLS-1$
    ) {
      while (resultSet.next()) {
        names.add(new DatabaseSequenceName(resultSet.getString(2), resultSet.getString(3)));
      }
    }
    return names;
  }

  @Override
  public boolean supportsSequences() {
    return true;
  }

  @SuppressWarnings("nls")
  @Override
  public List<String> getSchemaNames(final Connection connection, final String catalog) throws SQLException {
    final Set<String> schemaNamesBlackList = new HashSet<>(Arrays.asList("pg_catalog", "information_schema"));
    final DatabaseMetaData metaData = connection.getMetaData();
    final LinkedList<String> result = new LinkedList<>();
    try (final ResultSet resultSet = metaData.getSchemas()) {
      while (resultSet.next()) {
        final String schemaName = resultSet.getString(1);
        if (schemaNamesBlackList.contains(schemaName)) {
          continue;
        }
        result.add(schemaName);
      }
    }
    return result;
  }

  @SuppressWarnings("nls")
  @Override
  public ResultSet getSequenceMetadata(final Connection connection, final IDatabaseSequenceName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "select * \n"
            + "  from information_schema.sequences\n"
            + " where sequence_schema = ?\n"
            + "   and sequence_name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getSequenceName());
        });
  }

  @Override
  public boolean isApplicable(final IJdbcConnectionDescription context) {
    return Objects.equals(ServiceDatabaseSoftware.POSTGRES.getDriverName(), context.getDriverName());
  }

  @SuppressWarnings("nls")
  @Override
  public List<IDatabaseTriggerName> getTriggers(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "select ns.nspname\n"
            + "        , tg.tgname \n"
            + "  from pg_trigger tg,\n"
            + "          pg_class rc,\n"
            + "          pg_namespace ns\n"
            + "where rc.oid = tg.tgrelid\n"
            + "   and ns.oid = rc.relnamespace\n"
            + "   and ns.nspname = ?\n"
            + "   and rc.relname = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseTriggerName, SQLException>) value -> new DatabaseTriggerName(
            value.getString(1),
            value.getString(2)));
  }

  @SuppressWarnings("nls")
  @Override
  public ResultSet getTriggerMetadata(final Connection connection, final IDatabaseTriggerName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "select * \n"
            + "  from information_schema.triggers\n"
            + " where trigger_schema = ?\n"
            + "   and trigger_name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        });
  }

  @SuppressWarnings("nls")
  @Override
  public String getTriggerStatement(final Connection connection, final IDatabaseTriggerName name) throws SQLException {
    final String triggerFunction = DatabaseUtilities.result(
        connection,
        " select pr.proname\n"
            + "         ,pr.prosrc \n"
            + "   from pg_trigger tg\n"
            + "         , pg_class rc\n"
            + "         , pg_namespace ns\n"
            + "         , pg_proc pr\n"
            + " where pr.oid = tg.tgfoid\n"
            + "    and rc.oid = tg.tgrelid\n"
            + "    and ns.oid = rc.relnamespace\n"
            + "    and ns.nspname = ?\n"
            + "    and tg.tgname = ?", //$NON-NLS-1$
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
        connection,
        "select event_manipulation\n"
            + "  from information_schema.triggers\n"
            + " where trigger_schema = ?\n"
            + "   and trigger_name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        },
        (IConverter<IResult, String, SQLException>) value -> value.getString(1));

    final String trigger = DatabaseUtilities.result(
        connection,
        "select distinct action_timing \n"
            + "        , event_object_table\n"
            + "        , action_orientation\n"
            + "        , action_statement\n"
            + "  from information_schema.triggers\n"
            + " where trigger_schema = ?\n"
            + "   and trigger_name = ?", //$NON-NLS-1$
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

  @SuppressWarnings("nls")
  @Override
  public List<IDatabaseIndexName> getIndicies(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "select distinct s.nspname as schema,\n"
            + "       i.relname as index\n"
            + "  from pg_index ki,\n"
            + "       pg_namespace s,\n"
            + "       pg_class t,\n"
            + "       pg_class i,\n"
            + "       pg_attribute a\n"
            + " where ki.indrelid = t.oid\n"
            + "   and ki.indexrelid = i.oid\n"
            + "   and s.oid = t.relnamespace\n"
            + "   and t.oid = a.attrelid\n"
            + "   and a.attnum = ANY (ki.indkey)\n"
            + "   and s.nspname = ?\n"
            + "   and t.relname = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseIndexName, SQLException>) value -> new DatabaseIndexName(
            value.getString(1),
            value.getString(2)));
  }

  @SuppressWarnings("nls")
  @Override
  public ResultSet getIndexMetadata(final Connection connection, final IDatabaseIndexName name) throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "select s.nspname as schema,\n"
            + "       i.relname as index,\n"
            + "       a.attname as column,\n"
            + "       ki.*\n"
            + "  from pg_index ki,\n"
            + "       pg_namespace s,\n"
            + "       pg_class t,\n"
            + "       pg_class i,\n"
            + "       pg_attribute a\n"
            + " where ki.indrelid = t.oid\n"
            + "   and ki.indexrelid = i.oid\n"
            + "   and s.oid = t.relnamespace\n"
            + "   and t.oid = a.attrelid\n"
            + "   and a.attnum = ANY (ki.indkey)\n"
            + "   and s.nspname = ?\n"
            + "   and i.relname = ?",
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getIndexName());
        });
  }

  @Override
  public boolean supportsIndicies() {
    return true;
  }

  @SuppressWarnings("nls")
  @Override
  public List<IDatabaseConstraintName> getConstraints(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "select s.nspname,\n"
            + "       c.conname\n"
            + "  from pg_namespace s,\n"
            + "       pg_class t,\n"
            + "       pg_constraint c\n"
            + " where s.nspname = ?\n"
            + "   and s.oid = t.relnamespace\n"
            + "   and t.relname = ?\n"
            + "   and t.oid = c.conrelid",
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseConstraintName, SQLException>) value -> new DatabaseConstraintName(
            value.getString(1),
            value.getString(2)));
  }

  @SuppressWarnings("nls")
  @Override
  public ResultSet getConstraintMetadata(final Connection connection, final IDatabaseConstraintName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "select * \n"
            + "  from information_schema.table_constraints\n"
            + " where constraint_schema = ?\n"
            + "   and constraint_name = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getConstraintName());
        });
  }

  @Override
  public boolean supportsConstaints() {
    return true;
  }

}
