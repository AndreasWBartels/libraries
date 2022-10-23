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
package net.anwiba.database.swing.console.tree;

import net.anwiba.commons.jdbc.connection.IDatabaseConnector;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.IDatabaseFacade;
import net.anwiba.commons.jdbc.database.INamedTableFilter;
import net.anwiba.commons.jdbc.name.DatabaseSchemaName;
import net.anwiba.commons.jdbc.name.IDatabaseColumnName;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSchemaName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.jdbc.name.IDatabaseViewName;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.tree.ReloadableFolderTreeNode;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.database.swing.console.SqlConsoleMessages;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public final class SchemaTreeFactory {

  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(SchemaTreeFactory.class);
  private final IDatabaseFacade databaseFacade;
  private final IObjectModel<String> statusModel;
  private final IDatabaseConnector databaseConnector;

  public SchemaTreeFactory(
      final IDatabaseConnector databaseConnector,
      final IDatabaseFacade databaseFacade,
      final IObjectModel<String> statusModel) {
    this.databaseConnector = databaseConnector;
    this.databaseFacade = databaseFacade;
    this.statusModel = statusModel;
  }

  public DefaultMutableTreeNode
      create(final ICanceler canceler, final IJdbcConnectionDescription description, final String schema)
          throws CanceledException {
    this.statusModel.set(SqlConsoleMessages.working);
    final DefaultMutableTreeNode root = new DefaultMutableTreeNode(description);
    try (final Connection connection = this.databaseConnector.connectReadOnly(description)) {
      final String catalog = getCatalog(connection);
      //      final DatabaseMetaData metaData = connection.getMetaData();
      final Map<String, DefaultMutableTreeNode> schemas = new LinkedHashMap<>();
      if (schema != null) {
        addSchema(description, new DatabaseSchemaName(catalog, schema), schemas, root);
      } else {
        if (canceler.isCanceled()) {
          return null;
        }
        final Set<IDatabaseSchemaName> schemaNames =
            new LinkedHashSet<>(this.databaseFacade.getSchemaNames(canceler, connection, catalog));
        for (IDatabaseSchemaName schemaName : schemaNames) {
          addSchema(description, schemaName, schemas, root);
        }
      }
      if (schemas.isEmpty()) {
        addSchema(description, new DatabaseSchemaName(catalog, schema), schemas, root);
      }
      this.statusModel.set(SqlConsoleMessages.done);
      return root;
    } catch (final SQLException exception) {
      logger.debug(exception.getMessage(), exception);
      this.statusModel.set(exception.getMessage());
      return root;
    }
  }

  private void addSchema(
      final IJdbcConnectionDescription description,
      final IDatabaseSchemaName schemaName,
      final Map<String, DefaultMutableTreeNode> schemas,
      final DefaultMutableTreeNode root) {
    final DefaultMutableTreeNode schemaNode = schemaName.getSchemaName() == null
        ? root
        : new DefaultMutableTreeNode(schemaName);
    schemaNode.add(createTablesNode(description, schemaName));
    schemaNode.add(createViewsNode(description, schemaName));
    if (this.databaseFacade.supportsSequences()) {
      schemaNode.add(createSequencesNode(description, schemaName));
    }
    for (final INamedTableFilter filter : this.databaseFacade.getTableFilters()) {
      schemaNode.add(createOtherNodes(description, schemaName, filter));
    }
    if (root != schemaNode) {
      root.add(schemaNode);
    }
    schemas.put(schemaName.getSchemaName(), schemaNode);
  }

  private ReloadableFolderTreeNode<Object> createSequencesNode(final IJdbcConnectionDescription description,
      final IDatabaseSchemaName schemaName) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<IDatabaseSequenceName> sequences =
                SchemaTreeFactory.this.databaseFacade.getSequences(ICanceler.DummyCanceler, connection, schemaName);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (final IDatabaseSequenceName sequence : sequences) {
              nodes.add(new DefaultMutableTreeNode(sequence));
            }
            SchemaTreeFactory.this.statusModel.set(SqlConsoleMessages.done);
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.sequences);
  }

  private ReloadableFolderTreeNode<Object> createViewsNode(
      final IJdbcConnectionDescription description,
      final IDatabaseSchemaName schemaName) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<IDatabaseViewName> viewNames =
                getViewNames(ICanceler.DummyCanceler, connection, schemaName);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (IDatabaseViewName view : viewNames) {
              if (!SchemaTreeFactory.this.databaseFacade.isView(view)) {
                continue;
              }
              final DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(view);
              tableNode.add(createViewColumnsNode(description, view));
              nodes.add(tableNode);
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.views);
  }

  private ReloadableFolderTreeNode<Object> createTablesNode(
      final IJdbcConnectionDescription description,
      final IDatabaseSchemaName schemaName) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<IDatabaseTableName> tableNames =
                getTableNames(ICanceler.DummyCanceler, connection, schemaName);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (IDatabaseTableName table : tableNames) {
              if (!SchemaTreeFactory.this.databaseFacade.isTable(table)) {
                continue;
              }
              final DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
              tableNode.add(createTableColumnsNode(description, table));
              if (SchemaTreeFactory.this.databaseFacade.supportsConstaints()) {
                tableNode.add(createConstraintsNode(description, table));
              }
              tableNode.add(createIndiciesNode(description, table));
              if (SchemaTreeFactory.this.databaseFacade.supportsTrigger()) {
                tableNode.add(createTriggersNode(description, table));
              }
              nodes.add(tableNode);
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.tables);
  }

  private List<IDatabaseTableName> getTableNames(
      final ICanceler canceler,
      final Connection connection,
      final IDatabaseSchemaName schemaName) throws SQLException,
      CanceledException {
    return this.databaseFacade.getTables(canceler, connection, schemaName);
  }

  private List<IDatabaseViewName> getViewNames(
      final ICanceler canceler,
      final Connection connection,
      final IDatabaseSchemaName schemaName) throws SQLException,
      CanceledException {
    return this.databaseFacade.getViews(canceler, connection, schemaName);
  }

  private ReloadableFolderTreeNode<Object> createOtherNodes(
      final IJdbcConnectionDescription description,
      final IDatabaseSchemaName schemaName,
      final INamedTableFilter filter) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<IDatabaseTableName> tableNames =
                getTableNames(ICanceler.DummyCanceler, connection, schemaName);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (IDatabaseTableName table : tableNames) {
              if (!filter.accept(table)) {
                continue;
              }
              final DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
              tableNode.add(createTableColumnsNode(description, table));
              if (SchemaTreeFactory.this.databaseFacade.supportsConstaints()) {
                tableNode.add(createConstraintsNode(description, table));
              }
              tableNode.add(createIndiciesNode(description, table));
              if (SchemaTreeFactory.this.databaseFacade.supportsTrigger()) {
                tableNode.add(createTriggersNode(description, table));
              }
              nodes.add(tableNode);
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        filter.getName());
  }

  private MutableTreeNode createIndiciesNode(
      final IJdbcConnectionDescription description,
      final IDatabaseTableName table) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<IDatabaseIndexName> names =
                SchemaTreeFactory.this.databaseFacade.getIndicies(ICanceler.DummyCanceler, connection, table);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (final IDatabaseIndexName name : names) {
              nodes.add(new DefaultMutableTreeNode(name));
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.indicies);
  }

  private MutableTreeNode createTriggersNode(
      final IJdbcConnectionDescription description,
      final IDatabaseTableName table) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<IDatabaseTriggerName> names =
                SchemaTreeFactory.this.databaseFacade.getTriggers(ICanceler.DummyCanceler, connection, table);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (final IDatabaseTriggerName name : names) {
              nodes.add(new DefaultMutableTreeNode(name));
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.triggers);
  }

  private MutableTreeNode createTableColumnsNode(
      final IJdbcConnectionDescription description,
      final IDatabaseTableName table) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            final List<IDatabaseColumnName> names =
                SchemaTreeFactory.this.databaseFacade.getTableColumns(ICanceler.DummyCanceler, connection, table);
            for (final IDatabaseColumnName name : names) {
              nodes.add(new DefaultMutableTreeNode(name));
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.columns);
  }

  private MutableTreeNode createViewColumnsNode(
      final IJdbcConnectionDescription description,
      final IDatabaseViewName view) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            final List<IDatabaseColumnName> names =
                SchemaTreeFactory.this.databaseFacade.getViewColumns(ICanceler.DummyCanceler, connection, view);
            for (final IDatabaseColumnName name : names) {
              nodes.add(new DefaultMutableTreeNode(name));
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.columns);
  }

  private MutableTreeNode createConstraintsNode(
      final IJdbcConnectionDescription description,
      final IDatabaseTableName tableName) {
    return new ReloadableFolderTreeNode<>(
        value -> execute(description, connection -> {
          try {
            List<IDatabaseConstraintName> names = SchemaTreeFactory.this.databaseFacade
                .getConstraints(ICanceler.DummyCanceler, connection, tableName);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (final IDatabaseConstraintName name : names) {
              nodes.add(new DefaultMutableTreeNode(name));
            }
            return nodes;
          } catch (CanceledException exception) {
            return List.of();
          }
        }),
        SqlConsoleMessages.constraints);
  }

  private List<DefaultMutableTreeNode> execute(final IJdbcConnectionDescription description,
      final IFunction<Connection, List<DefaultMutableTreeNode>, SQLException> function) {
    SchemaTreeFactory.this.statusModel.set(SqlConsoleMessages.working);
    try (final Connection connection = this.databaseConnector.connectReadOnly(description)) {
      List<DefaultMutableTreeNode> nodes = function.execute(connection);
      SchemaTreeFactory.this.statusModel.set(SqlConsoleMessages.done);
      return nodes;
    } catch (final Exception exception) {
      logger.debug(exception.getMessage(), exception);
      SchemaTreeFactory.this.statusModel.set(exception.getMessage());
      return Collections.emptyList();
    }
  }

  private String getCatalog(final Connection connection) throws SQLException {
    return connection.getCatalog();
  }
}
