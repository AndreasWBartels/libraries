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
package net.anwiba.database.oracle;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

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
import net.anwiba.commons.reference.utilities.IoUtilities;

public class OracleDatabaseFacade extends DatabaseFacade implements IRegistrableDatabaseFacade {

  public OracleDatabaseFacade() {
  }

//  select dbms_metadata.get_ddl( 'TABLE', 'EMP', 'SCOTT' ) from dual

  @Override
  public String getTableStatement(final Connection connection, final IDatabaseTableName tableName) throws SQLException {
    return DatabaseUtilities.result(
        connection,
        "select dbms_metadata.get_ddl( 'TABLE', ?, ? ) from dual", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getTableName());
          value.setObject(2, tableName.getSchemaName());
        },
        (IConverter<IOptional<IResult, SQLException>, String, SQLException>) optional -> optional
            .convert(v -> v.getObject(1))
            .instanceOf(Clob.class)
            .convert(c -> {
              try (Reader reader = c.getCharacterStream()) {
                return IoUtilities.toString(reader);
              } catch (IOException exception) {
                throw new SQLException(exception.getMessage(), exception);
              }
            })
            .get());
  }

  @Override
  public boolean supportsTableStatement() {
    return true;
  }

  @Override
  public List<IDatabaseConstraintName> getConstraints(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "SELECT OWNER, CONSTRAINT_NAME FROM SYS.ALL_CONSTRAINTS WHERE OWNER = ? AND TABLE_NAME = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseConstraintName, SQLException>) value -> new DatabaseConstraintName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getConstraintMetadata(final Connection connection, final IDatabaseConstraintName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        // "SELECT OWNER, CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME, R_OWNER, R_CONSTRAINT_NAME, DELETE_RULE, STATUS,
        // DEFERRABLE, DEFERRED, VALIDATED, GENERATED, BAD, RELY, LAST_CHANGE, INDEX_OWNER, INDEX_NAME, INVALID,
        // VIEW_RELATED FROM SYS.ALL_CONSTRAINTS WHERE OWNER = ? AND CONSTRAINT_NAME = ?", //$NON-NLS-1$
        "SELECT * FROM SYS.ALL_CONSTRAINTS WHERE OWNER = ? AND CONSTRAINT_NAME = ?", //$NON-NLS-1$
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
  public List<IDatabaseIndexName> getIndicies(final Connection connection, final IDatabaseTableName tableName)
      throws SQLException {
    return DatabaseUtilities.results(
        connection,
        "SELECT OWNER, INDEX_NAME FROM SYS.ALL_INDEXES WHERE TABLE_OWNER = ? AND TABLE_NAME = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseIndexName, SQLException>) value -> new DatabaseIndexName(
            value.getString(1),
            value.getString(2)));
  }

  // @Override
  // public List<IDatabaseIndexName> getIndicies(final Connection connection, final String schema) throws SQLException {
  // return DatabaseUtilities.results(
  // connection,
  // "SELECT OWNER, INDEX_NAME FROM SYS.ALL_INDEXES WHERE TABLE_OWNER = ?", //$NON-NLS-1$
  // (IProcedure<PreparedStatement, SQLException>) value -> {
  // value.setObject(1, schema);
  // },
  // new IFunction<IResult, IDatabaseIndexName, SQLException>() {
  //
  // @Override
  // public IDatabaseIndexName execute(final IResult value) throws SQLException {
  // return new DatabaseIndexName(value.getString(1), value.getString(2));
  // }
  // });
  // }

  @Override
  public ResultSet getIndexMetadata(final Connection connection, final IDatabaseIndexName name) throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "SELECT * FROM SYS.ALL_INDEXES WHERE OWNER = ? AND INDEX_NAME = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getIndexName());
        });
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
        "SELECT OWNER, TRIGGER_NAME FROM SYS.ALL_TRIGGERS WHERE TABLE_OWNER = ? AND TABLE_NAME = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, tableName.getSchemaName());
          value.setObject(2, tableName.getTableName());
        },
        (IConverter<IResult, IDatabaseTriggerName, SQLException>) value -> new DatabaseTriggerName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getTriggerMetadata(final Connection connection, final IDatabaseTriggerName name)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "SELECT OWNER, TRIGGER_NAME, TRIGGER_TYPE, TRIGGERING_EVENT, TABLE_OWNER, BASE_OBJECT_TYPE, TABLE_NAME, COLUMN_NAME, REFERENCING_NAMES, WHEN_CLAUSE, STATUS, DESCRIPTION, ACTION_TYPE, TRIGGER_BODY FROM SYS.ALL_TRIGGERS WHERE OWNER = ? AND TRIGGER_NAME = ?", //$NON-NLS-1$
        // "SELECT OWNER, TRIGGER_NAME, TRIGGER_TYPE, TRIGGERING_EVENT, TABLE_OWNER, BASE_OBJECT_TYPE, TABLE_NAME,
        // COLUMN_NAME, REFERENCING_NAMES, WHEN_CLAUSE, STATUS, DESCRIPTION, ACTION_TYPE FROM SYS.ALL_TRIGGERS WHERE
        // OWNER = ? AND TRIGGER_NAME = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        });
  }

  @Override
  public String getTriggerStatement(final Connection connection, final IDatabaseTriggerName name) throws SQLException {
    return DatabaseUtilities.result(
        connection,
        "SELECT TRIGGER_BODY, DESCRIPTION FROM SYS.ALL_TRIGGERS WHERE OWNER = ? AND TRIGGER_NAME = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, name.getSchemaName());
          value.setObject(2, name.getTriggerName());
        },
        (IConverter<IOptional<IResult, SQLException>, String, SQLException>) value -> value.convert(v -> {
          final String body;
          try (InputStream stream = v.getAsciiStream(1)) {
            body = IoUtilities.toString(stream, "UTF-8"); //$NON-NLS-1$

          } catch (final IOException exception) {
            throw new SQLException(exception);
          }
          final String description = v.getString(2);
          return "CREATE OR REPLACE TRIGGER " + description + body; //$NON-NLS-1$
        }).get());
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
        "SELECT SEQUENCE_OWNER, SEQUENCE_NAME FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_OWNER = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> value.setObject(1, schema),
        (IConverter<IResult, IDatabaseSequenceName, SQLException>) value -> new DatabaseSequenceName(
            value.getString(1),
            value.getString(2)));
  }

  @Override
  public ResultSet getSequenceMetadata(final Connection connection, final IDatabaseSequenceName sequence)
      throws SQLException {
    return DatabaseUtilities.resultSet(
        connection,
        "SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_OWNER = ? AND SEQUENCE_NAME = ?", //$NON-NLS-1$
        (IProcedure<PreparedStatement, SQLException>) value -> {
          value.setObject(1, sequence.getSchemaName());
          value.setObject(2, sequence.getSequenceName());
        });
  }

  @Override
  public boolean supportsSequences() {
    return true;
  }

  @Override
  public boolean isApplicable(final IJdbcConnectionDescription context) {
    return Objects.equals(ServiceDatabaseSoftware.ORACLE.getDriverName(), context.getDriverName());
  }

}
