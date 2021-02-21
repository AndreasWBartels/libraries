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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.IDatabaseFacade;
import net.anwiba.commons.jdbc.database.INamedTableFilter;
import net.anwiba.commons.jdbc.name.DatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.IWatcher;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.tree.LazyFolderTreeNode;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.database.swing.console.SqlConsoleMessages;

public final class SchemaTreeFactory {

  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(SchemaTreeFactory.class);
  private final IDatabaseFacade databaseFacade;
  private final IObjectModel<Connection> connectionModel;
  private final IObjectModel<String> statusModel;
  private final IBooleanModel isDisconnectedModel;
  private final IBooleanModel isConnectedModel;

  public SchemaTreeFactory(
      final IDatabaseFacade databaseFacade,
      final IObjectModel<Connection> connectionModel,
      final IObjectModel<String> statusModel,
      final IBooleanModel isDisconnectedModel,
      final IBooleanModel isConnectedModel) {
    this.databaseFacade = databaseFacade;
    this.connectionModel = connectionModel;
    this.statusModel = statusModel;
    this.isDisconnectedModel = isDisconnectedModel;
    this.isConnectedModel = isConnectedModel;
  }

  @SuppressWarnings("resource")
  public DefaultMutableTreeNode
      create(final ICanceler canceler, final IJdbcConnectionDescription description, final String schema) {
    this.statusModel.set(SqlConsoleMessages.working);
    final DefaultMutableTreeNode root = new DefaultMutableTreeNode(description.getUrl());
    try {
      final Connection connection = this.connectionModel.get();
      final boolean isClosed = connection.isClosed();
      if (isClosed) {
        this.isConnectedModel.set(!isClosed);
        this.isDisconnectedModel.set(isClosed);
        this.statusModel.set(SqlConsoleMessages.connectionIsClosed);
        return root;
      }
      final String catalog = getCatalog();
      final Set<String> schemaNames = new HashSet<>(this.databaseFacade.getSchemaNames(connection, catalog));
      final DatabaseMetaData metaData = connection.getMetaData();
      final Map<String, DefaultMutableTreeNode> schemas = new LinkedHashMap<>();
      if (schema != null) {
        addSchema(connection, metaData, root, catalog, schema, schemas);
      } else {
        if (canceler.isCanceled()) {
          return null;
        }
        try (final ResultSet resultSet = metaData.getSchemas()) {
          try (IWatcher watcher = canceler.watcherFactory().create(() -> {
            try {
              resultSet.close();
            } catch (SQLException e) {
              // nothing to do
            }
          })) {
            while (resultSet.next()) {
              if (canceler.isCanceled()) {
                return null;
              }
              final String schemaName = resultSet.getString(1);
              if (!schemaNames.contains(schemaName)) {
                continue;
              }
              addSchema(connection, metaData, root, catalog, schemaName, schemas);
            }
          }
        }
      }
      if (schemas.isEmpty()) {
        addSchema(connection, metaData, root, catalog, schema, schemas);
      }
      this.statusModel.set(SqlConsoleMessages.done);
      return root;
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.statusModel.set(exception.getMessage());
      return root;
    }
  }

  private void addSchema(
      final Connection connection,
      final DatabaseMetaData metaData,
      final DefaultMutableTreeNode root,
      final String catalog,
      final String schemaName,
      final Map<String, DefaultMutableTreeNode> schemas) {
    final DefaultMutableTreeNode schemaNode = schemaName == null
        ? root
        : new DefaultMutableTreeNode(schemaName);
    schemaNode.add(createTablesNode(connection, metaData, catalog, schemaName));
    schemaNode.add(createViewsNode(metaData, catalog, schemaName));
    if (this.databaseFacade.supportsSequences()) {
      schemaNode.add(createSequencesNode(connection, schemaName));
    }
    for (final INamedTableFilter filter : this.databaseFacade.getTableFilters()) {
      schemaNode.add(createOtherNodes(connection, metaData, catalog, schemaName, filter));
    }
    if (schemaName != null) {
      root.add(schemaNode);
    }
    schemas.put(schemaName, schemaNode);
  }

  private LazyFolderTreeNode<String> createSequencesNode(final Connection connection, final String schemaName) {
    return new LazyFolderTreeNode<>(
        new IFactory<String, List<DefaultMutableTreeNode>, RuntimeException>() {

          @Override
          public List<DefaultMutableTreeNode> create(final String value) throws RuntimeException {
            try {
              final List<IDatabaseSequenceName> sequences =
                  SchemaTreeFactory.this.databaseFacade.getSequences(connection, schemaName);
              final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
              for (final IDatabaseSequenceName sequence : sequences) {
                nodes.add(new DefaultMutableTreeNode(sequence));
              }
              return nodes;
            } catch (final Exception exception) {
              return Collections.emptyList();
            }
          }
        },
        SqlConsoleMessages.sequences);
  }

  private LazyFolderTreeNode<String> createViewsNode(
      final DatabaseMetaData metaData,
      final String catalog,
      final String schemaName) {
    return new LazyFolderTreeNode<>(
        new IFactory<String, List<DefaultMutableTreeNode>, RuntimeException>() {

          @Override
          public List<DefaultMutableTreeNode> create(final String value) throws RuntimeException {
            try (final ResultSet tablesResultSet =
                metaData.getTables(catalog, schemaName, null, new String[] { "VIEW" })) { //$NON-NLS-1$
              final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
              while (tablesResultSet.next()) {
                final String schema = tablesResultSet.getString(2);
                final String name = tablesResultSet.getString(3);
                final IDatabaseTableName table = new DatabaseTableName(schema, name);
                nodes.add(new DefaultMutableTreeNode(table));
              }
              return nodes;
            } catch (final Exception exception) {
              return Collections.emptyList();
            }
          }
        },
        SqlConsoleMessages.views);
  }

  private LazyFolderTreeNode<String> createTablesNode(
      final Connection connection,
      final DatabaseMetaData metaData,
      final String catalog,
      final String schemaName) {
    return new LazyFolderTreeNode<>(
        new IFactory<String, List<DefaultMutableTreeNode>, RuntimeException>() {

          @Override
          public List<DefaultMutableTreeNode> create(final String value) throws RuntimeException {
            List<IDatabaseTableName> tableNames = getTableNames(connection, catalog, schemaName);
            final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (IDatabaseTableName table : tableNames) {
              if (!SchemaTreeFactory.this.databaseFacade.isTable(table)) {
                continue;
              }
              final DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
              if (SchemaTreeFactory.this.databaseFacade.supportsConstaints()) {
                tableNode.add(createConstraintsNode(connection, table));
              }
              if (SchemaTreeFactory.this.databaseFacade.supportsIndicies()) {
                tableNode.add(createIndiciesNode(connection, table));
              }
              if (SchemaTreeFactory.this.databaseFacade.supportsTrigger()) {
                tableNode.add(createTriggersNode(connection, table));
              }
              nodes.add(tableNode);
            }
            return nodes;
          }

          private List<IDatabaseTableName>
              getTableNames(final Connection connection, final String catalog, final String schemaName) {
            try {
              if (SchemaTreeFactory.this.databaseFacade.supportsTables()) {
                return SchemaTreeFactory.this.databaseFacade.getTables(connection, schemaName);
              }
            } catch (SQLException exception) {
              exception.printStackTrace();
            }
            try (final ResultSet tablesResultSet =
                metaData.getTables(catalog, schemaName, null, new String[] { "TABLE" })) { //$NON-NLS-1$
              List<IDatabaseTableName> names = new ArrayList<IDatabaseTableName>();
              final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
              while (tablesResultSet.next()) {
                final String schema = tablesResultSet.getString(2);
                final String name = tablesResultSet.getString(3);
                names.add(new DatabaseTableName(schema, name));
              }
              return names;
            } catch (SQLException exception) {
              return List.of();
            }
          }
        },
        SqlConsoleMessages.tables);
  }

  private LazyFolderTreeNode<String> createOtherNodes(
      final Connection connection,
      final DatabaseMetaData metaData,
      final String catalog,
      final String schemaName,
      final INamedTableFilter filter) {
    return new LazyFolderTreeNode<>(
        new IFactory<String, List<DefaultMutableTreeNode>, RuntimeException>() {

          @Override
          public List<DefaultMutableTreeNode> create(final String value) throws RuntimeException {
            try (final ResultSet tablesResultSet =
                metaData.getTables(catalog, schemaName, null, new String[] { "TABLE" })) { //$NON-NLS-1$
              final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
              while (tablesResultSet.next()) {
                final String schema = tablesResultSet.getString(2);
                final String name = tablesResultSet.getString(3);
                final IDatabaseTableName table = new DatabaseTableName(schema, name);
                if (!filter.accept(table)) {
                  continue;
                }
                final DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
                if (SchemaTreeFactory.this.databaseFacade.supportsConstaints()) {
                  tableNode.add(createConstraintsNode(connection, table));
                }
                if (SchemaTreeFactory.this.databaseFacade.supportsIndicies()) {
                  tableNode.add(createIndiciesNode(connection, table));
                }
                if (SchemaTreeFactory.this.databaseFacade.supportsTrigger()) {
                  tableNode.add(createTriggersNode(connection, table));
                }
                nodes.add(tableNode);
              }
              return nodes;
            } catch (final Exception exception) {
              return Collections.emptyList();
            }
          }
        },
        filter.getName());
  }

  private MutableTreeNode createIndiciesNode(final Connection connection, final IDatabaseTableName table) {
    return new LazyFolderTreeNode<>(
        new IFactory<String, List<DefaultMutableTreeNode>, RuntimeException>() {

          @Override
          public List<DefaultMutableTreeNode> create(final String value) throws RuntimeException {
            try {
              final List<IDatabaseIndexName> names =
                  SchemaTreeFactory.this.databaseFacade.getIndicies(connection, table);
              final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
              for (final IDatabaseIndexName name : names) {
                nodes.add(new DefaultMutableTreeNode(name));
              }
              return nodes;
            } catch (final Exception exception) {
              return Collections.emptyList();
            }
          }
        },
        SqlConsoleMessages.indicies);
  }

  private MutableTreeNode createTriggersNode(final Connection connection, final IDatabaseTableName table) {
    return new LazyFolderTreeNode<>(
        new IFactory<String, List<DefaultMutableTreeNode>, RuntimeException>() {

          @Override
          public List<DefaultMutableTreeNode> create(final String value) throws RuntimeException {
            try {
              final List<IDatabaseTriggerName> names =
                  SchemaTreeFactory.this.databaseFacade.getTriggers(connection, table);
              final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
              for (final IDatabaseTriggerName name : names) {
                nodes.add(new DefaultMutableTreeNode(name));
              }
              return nodes;
            } catch (final Exception exception) {
              return Collections.emptyList();
            }
          }
        },
        SqlConsoleMessages.triggers);
  }

  private MutableTreeNode createConstraintsNode(final Connection connection, final IDatabaseTableName tableName) {
    return new LazyFolderTreeNode<>(
        new IFactory<String, List<DefaultMutableTreeNode>, RuntimeException>() {

          @Override
          public List<DefaultMutableTreeNode> create(final String value) throws RuntimeException {
            try {
              final List<IDatabaseConstraintName> names =
                  SchemaTreeFactory.this.databaseFacade.getConstraints(connection, tableName);
              final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
              for (final IDatabaseConstraintName name : names) {
                nodes.add(new DefaultMutableTreeNode(name));
              }
              return nodes;
            } catch (final Exception exception) {
              return Collections.emptyList();
            }
          }

        },
        SqlConsoleMessages.constraints);
  }

  private String getCatalog() {
    try {
      return this.connectionModel.get().getCatalog();
    } catch (final AbstractMethodError | Exception exception) {
      return null;
    }
  }
}
