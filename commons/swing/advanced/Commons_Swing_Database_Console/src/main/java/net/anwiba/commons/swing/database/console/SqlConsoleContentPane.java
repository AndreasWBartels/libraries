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
package net.anwiba.commons.swing.database.console;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.anwiba.commons.jdbc.connection.IDatabaseConnector;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.IDatabaseFacade;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.AndAggregatedBooleanDistributor;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.model.OrAggregatedBooleanDistributor;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.preferences.StringListPreference;
import net.anwiba.commons.reference.utilities.FileUtilities;
import net.anwiba.commons.reference.utilities.IFileExtensions;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.database.console.result.DataBaseTableCellValueRenderFactory;
import net.anwiba.commons.swing.database.console.result.IDataBaseTableCellValueToStringConverterProvider;
import net.anwiba.commons.swing.database.console.result.ResultReseter;
import net.anwiba.commons.swing.database.console.result.ResultSetTableModel;
import net.anwiba.commons.swing.database.console.tree.DatabaseNamesTreeRenderer;
import net.anwiba.commons.swing.database.console.tree.SchemaTreeFactory;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.progress.ProgressDialogLauncher;
import net.anwiba.commons.swing.filechooser.FileChoosers;
import net.anwiba.commons.swing.filechooser.IFileChooserResult;
import net.anwiba.commons.swing.filechooser.OpenFileChooserConfiguration;
import net.anwiba.commons.swing.filechooser.SaveFileChooserConfiguration;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.StringFieldBuilder;
import net.anwiba.commons.swing.table.Table;
import net.anwiba.commons.swing.tree.FilteredDefaultTreeModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.process.IProcessManager;
import net.anwiba.commons.thread.process.ProcessBuilder;
import net.anwiba.commons.utilities.string.StringUtilities;

public final class SqlConsoleContentPane extends AbstractContentPane {

  static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(SqlConsoleContentPane.class);
  private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

  private final IJdbcConnectionDescription description;
  private final IObjectModel<Connection> connectionModel;
  private final IBooleanModel isDisconnectedModel;
  private final IBooleanModel isConnectedModel;
  private final IDatabaseConnector databaseConnector;
  private final IPreferences preferences;
  private final IBooleanDistributor createStatementActionsEnabledDistributor;
  private final IBooleanDistributor propertiesActionsEnabledDistributor;
  private final IBooleanDistributor tableActionsEnabledDistributor;
  private final IBooleanDistributor textActionsEnabledDistributor;
  private final StatementExecutor statementExecutor;
  private final ResultReseter resultReseter;
  private final IObjectModel<Statement> statementModel = new ObjectModel<>();
  private final IObjectModel<ResultSet> resultSetModel = new ObjectModel<>();
  private final IObjectModel<IDatabaseTableName> selectedTable = new ObjectModel<>();
  private final IObjectModel<IDatabaseSequenceName> selectedSequence = new ObjectModel<>();
  private final IObjectModel<IDatabaseTriggerName> selectedTrigger = new ObjectModel<>();
  private final IObjectModel<IDatabaseIndexName> selectedIndex = new ObjectModel<>();
  private final IObjectModel<IDatabaseConstraintName> selectedConstraint = new ObjectModel<>();
  private final IBooleanModel isTextAvailableModel = new BooleanModel(false);
  private final IBooleanModel isTableSelectedModel = new BooleanModel(false);
  private final IBooleanModel isSequenceSelectedModel = new BooleanModel(false);
  private final IBooleanModel isTriggerSelectedModel = new BooleanModel(false);
  private final IBooleanModel isIndexSelectedModel = new BooleanModel(false);
  private final IBooleanModel isConstraintSelectedModel = new BooleanModel(false);
  private final IObjectModel<String> statusModel = new ObjectModel<>();
  private final DefaultComboBoxModel<String> historyComboBoxModel = new DefaultComboBoxModel<>();

  private JPanel contentPane;
  private final IDataBaseTableCellValueToStringConverterProvider dataBaseTableCellValueToStringConverterProvider;

  private final DataBaseTableCellValueRenderFactory dataBaseTableCellValueRenderFactory;
  private final IDatabaseFacade databaseFacade;
  private final SchemaTreeFactory schemaTreeFactory;
  private final String schema;

  public SqlConsoleContentPane(
      final IPreferences preferences,
      final IProcessManager processManager,
      final IDatabaseConnector databaseConnector,
      final IDatabaseFacade databaseFacade,
      final IJdbcConnectionDescription description,
      final String schema,
      final IDataBaseTableCellValueToStringConverterProvider dataBaseTableCellValueToStringConverterProvider,
      final IObjectModel<Connection> connectionModel,
      final IBooleanModel isDisconnectedModel,
      final IBooleanModel isConnectedModel) {
    this.databaseFacade = databaseFacade;
    this.schema = schema;
    this.dataBaseTableCellValueRenderFactory = new DataBaseTableCellValueRenderFactory(
        dataBaseTableCellValueToStringConverterProvider);
    this.historyComboBoxModel.addElement(null);
    final StringListPreference statementListPreference = new StringListPreference(
        preferences.node(description.getProtocol()).node("statements")); //$NON-NLS-1$
    statementListPreference.get().stream().forEach(v -> this.historyComboBoxModel.addElement(v));
    this.historyComboBoxModel.setSelectedItem(null);
    this.historyComboBoxModel.addListDataListener(new ListDataListener() {

      @Override
      public void intervalRemoved(final ListDataEvent e) {
        // nothing to do
      }

      @Override
      public void intervalAdded(final ListDataEvent e) {
        final int size = SqlConsoleContentPane.this.historyComboBoxModel.getSize();
        final List<String> values = Streams
            .of()
            .until(size)
            .convert(p -> SqlConsoleContentPane.this.historyComboBoxModel.getElementAt(p))
            .asList();
        processManager.execute(new ProcessBuilder().setExecutable((m, c, i) -> {
          statementListPreference.set(values);
        }).build());
      }

      @Override
      public void contentsChanged(final ListDataEvent e) {
        // nothing to do
      }
    });
    this.databaseConnector = databaseConnector;
    this.preferences = preferences;
    this.description = description;
    this.dataBaseTableCellValueToStringConverterProvider = dataBaseTableCellValueToStringConverterProvider;
    this.connectionModel = connectionModel;
    this.isDisconnectedModel = isDisconnectedModel;
    this.isConnectedModel = isConnectedModel;
    this.tableActionsEnabledDistributor = new AndAggregatedBooleanDistributor(
        Arrays.asList(isConnectedModel, this.isTableSelectedModel));
    this.textActionsEnabledDistributor = new AndAggregatedBooleanDistributor(
        Arrays.asList(isConnectedModel, this.isTextAvailableModel));
    this.propertiesActionsEnabledDistributor = new AndAggregatedBooleanDistributor(
        Arrays.asList(
            isConnectedModel,
            new OrAggregatedBooleanDistributor(
                Arrays.asList(
                    this.isTableSelectedModel,
                    this.isSequenceSelectedModel,
                    this.isIndexSelectedModel,
                    this.isConstraintSelectedModel,
                    this.isTriggerSelectedModel))));
    this.createStatementActionsEnabledDistributor = new AndAggregatedBooleanDistributor(
        Arrays
            .asList(isConnectedModel, new OrAggregatedBooleanDistributor(Arrays.asList(this.isTriggerSelectedModel))));
    this.resultReseter = new ResultReseter(this.statementModel, this.resultSetModel);
    this.schemaTreeFactory = new SchemaTreeFactory(
        databaseFacade,
        connectionModel,
        this.statusModel,
        isDisconnectedModel,
        isConnectedModel);
    this.statementExecutor = new StatementExecutor(
        connectionModel,
        this.resultReseter,
        this.statementModel,
        this.resultSetModel,
        this.statusModel,
        this.historyComboBoxModel,
        isDisconnectedModel,
        isConnectedModel);
  }

  @Override
  public JComponent getComponent() {
    if (this.contentPane == null) {
      final DefaultMutableTreeNode root = this.schemaTreeFactory.create(this.description, this.schema);
      final FilteredDefaultTreeModel schemaTreeModel = new FilteredDefaultTreeModel(root);
      final JEditorPane editorPane = new JEditorPane("text/plain", ""); //$NON-NLS-1$//$NON-NLS-2$
      final PlainDocument document = (PlainDocument) editorPane.getDocument();
      final JTree treePane = new JTree(schemaTreeModel);
      treePane.setCellRenderer(new DatabaseNamesTreeRenderer());
      final JToolBar treeToolBar = new JToolBar();
      treeToolBar.setFloatable(false);
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.connect)
              .setTooltip(SqlConsoleMessages.connect)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GTK_CONNECT)
              .setEnabledDistributor(this.isDisconnectedModel)
              .setProcedure(c -> connect(c))
              .build());
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.disconnect)
              .setTooltip(SqlConsoleMessages.disconnect)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GTK_DISCONNECT)
              .setEnabledDistributor(this.isConnectedModel)
              .setProcedure(c -> disconnect(c))
              .build());
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.reload)
              .setTooltip(SqlConsoleMessages.reloadDatabaseSchema)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.VIEW_REFRESH)
              .setEnabledDistributor(this.isConnectedModel)
              .setTask(() -> schemaTreeModel.setRoot(this.schemaTreeFactory.create(this.description, this.schema)))
              .build());
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.properties)
              .setTooltip(SqlConsoleMessages.properties)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.DIALOG_INFORMATION)
              .setEnabledDistributor(this.propertiesActionsEnabledDistributor)
              .setTask(() -> {
                if (this.isTableSelectedModel.get()) {
                  getMetadataResult(
                      (metaData, tableName) -> metaData
                          .getColumns(getCatalog(), tableName.getSchemaName(), tableName.getTableName(), null));
                }
                if (this.isSequenceSelectedModel.get()) {
                  querySequenceMetadata(this.selectedSequence.get());
                }
                if (this.isTriggerSelectedModel.get()) {
                  queryTriggerMetadata(this.selectedTrigger.get());
                }
                if (this.isIndexSelectedModel.get()) {
                  queryIndexMetadata(this.selectedIndex.get());
                }
                if (this.isConstraintSelectedModel.get()) {
                  queryConstraintMetadata(this.selectedConstraint.get());
                }
              })
              .build());
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.tablePrivileges)
              .setTooltip(SqlConsoleMessages.tablePrivileges)
              .setEnabledDistributor(this.tableActionsEnabledDistributor)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.SECURITY_MEDIUM)
              .setTask(
                  () -> getMetadataResult(
                      (metaData, tableName) -> metaData
                          .getTablePrivileges(getCatalog(), tableName.getSchemaName(), tableName.getTableName())))
              .build());
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.columnPrivileges)
              .setTooltip(SqlConsoleMessages.columnPrivileges)
              .setEnabledDistributor(this.tableActionsEnabledDistributor)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.SECURITY_HIGH)
              .setTask(
                  () -> getMetadataResult(
                      (metaData, tableName) -> metaData.getColumnPrivileges(
                          getCatalog(),
                          tableName.getSchemaName(),
                          tableName.getTableName(),
                          null)))
              .build());
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.content)
              .setTooltip(SqlConsoleMessages.content)
              .setEnabledDistributor(this.tableActionsEnabledDistributor)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.VIEW_LIST_TEXT)
              .setTask(
                  () -> this.statementExecutor.executeStatement(createTableSelectStatement(this.selectedTable.get())))
              .build());
      treeToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.createStatement)
              .setTooltip(SqlConsoleMessages.createStatement)
              .setEnabledDistributor(this.createStatementActionsEnabledDistributor)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.FORMAT_JUSTIFY_LEFT)
              .setTask(() -> {

                try {
                  this.statusModel.set(SqlConsoleMessages.working);
                  String statement = null;
                  if (this.isTriggerSelectedModel.get()) {
                    statement = this.databaseFacade
                        .getTriggerStatement(this.connectionModel.get(), this.selectedTrigger.get());
                  }
                  if (statement == null) {
                    this.statusModel.set(SqlConsoleMessages.emptyResult);
                    return;
                  }
                  setEditorContent(document, statement);
                  this.statusModel.set(SqlConsoleMessages.done);
                } catch (final SQLException exception) {
                  logger.log(ILevel.DEBUG, "Couldn't close connection", exception); //$NON-NLS-1$
                  this.statusModel.set(exception.getMessage());
                }
              })
              .build());
      final TreeSelectionModel schemaTreeSelectionModel = treePane.getSelectionModel();
      schemaTreeSelectionModel.addTreeSelectionListener(new TreeSelectionListener() {

        @Override
        public void valueChanged(final TreeSelectionEvent e) {
          final TreePath treePath = e.getNewLeadSelectionPath();
          consumeObject(
              treePath, //
              o -> true,
              input -> {
                SqlConsoleContentPane.this.selectedTable.set(null);
                SqlConsoleContentPane.this.selectedSequence.set(null);
                SqlConsoleContentPane.this.selectedTrigger.set(null);
                SqlConsoleContentPane.this.selectedIndex.set(null);
                SqlConsoleContentPane.this.selectedConstraint.set(null);
                if (input instanceof IDatabaseTableName) {
                  SqlConsoleContentPane.this.selectedTable.set((IDatabaseTableName) input);
                } else if (input instanceof IDatabaseSequenceName) {
                  SqlConsoleContentPane.this.selectedSequence.set((IDatabaseSequenceName) input);
                } else if (input instanceof IDatabaseTriggerName) {
                  SqlConsoleContentPane.this.selectedTrigger.set((IDatabaseTriggerName) input);
                } else if (input instanceof IDatabaseIndexName) {
                  SqlConsoleContentPane.this.selectedIndex.set((IDatabaseIndexName) input);
                } else if (input instanceof IDatabaseConstraintName) {
                  SqlConsoleContentPane.this.selectedConstraint.set((IDatabaseConstraintName) input);
                }
                SqlConsoleContentPane.this.isTableSelectedModel.set(input instanceof IDatabaseTableName);
                SqlConsoleContentPane.this.isSequenceSelectedModel.set(input instanceof IDatabaseSequenceName);
                SqlConsoleContentPane.this.isTriggerSelectedModel.set(input instanceof IDatabaseTriggerName);
                SqlConsoleContentPane.this.isIndexSelectedModel.set(input instanceof IDatabaseIndexName);
                SqlConsoleContentPane.this.isConstraintSelectedModel.set(input instanceof IDatabaseConstraintName);
              });
        }
      });
      treePane.addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(final MouseEvent e) {
          if (e.getClickCount() != 2) {
            return;
          }
          consumeObject(
              treePane.getSelectionPath(),
              o -> o instanceof IDatabaseTableName
                  || o instanceof IDatabaseIndexName
                  || o instanceof IDatabaseConstraintName
                  || o instanceof IDatabaseSequenceName
                  || o instanceof IDatabaseTriggerName,
              t -> Optional.ofNullable(t).ifPresent(
                  o -> run(e.getComponent(), SqlConsoleMessages.SQLConsole, SqlConsoleMessages.loadTableContent, () -> {
                    if (o instanceof IDatabaseTableName) {
                      SqlConsoleContentPane.this.statementExecutor
                          .executeStatement(createTableSelectStatement((IDatabaseTableName) o));
                    }
                    if (o instanceof IDatabaseSequenceName) {
                      querySequenceMetadata((IDatabaseSequenceName) o);
                    }
                    if (o instanceof IDatabaseTriggerName) {
                      queryTriggerMetadata((IDatabaseTriggerName) o);
                    }
                    if (o instanceof IDatabaseIndexName) {
                      queryIndexMetadata((IDatabaseIndexName) o);
                    }
                    if (o instanceof IDatabaseConstraintName) {
                      queryConstraintMetadata((IDatabaseConstraintName) o);
                    }
                  })));
        }

      });
      final JScrollPane treeScrollPane = new JScrollPane(treePane);
      treeScrollPane.setMinimumSize(new Dimension(220, 300));

      final JPanel treeHeadComponent = new JPanel(new BorderLayout());
      treeHeadComponent.add(treeToolBar, BorderLayout.NORTH);
      final JPanel treeContainerPane = new JPanel(new BorderLayout());
      treeContainerPane.setMinimumSize(new Dimension(240, 320));
      treeContainerPane.add(treeHeadComponent, BorderLayout.NORTH);
      treeContainerPane.add(treeScrollPane, BorderLayout.CENTER);

      final JComboBox<String> historyComboBox = new JComboBox<>(this.historyComboBoxModel);
      historyComboBox.addActionListener(e -> {
        if (this.historyComboBoxModel.getSelectedItem() != null) {
          setEditorContent(document, (String) this.historyComboBoxModel.getSelectedItem());
        }
      });
      document.addDocumentListener(new DocumentListener() {

        @Override
        public void removeUpdate(final DocumentEvent e) {
          check(document, historyComboBox);
        }

        @Override
        public void insertUpdate(final DocumentEvent e) {
          check(document, historyComboBox);
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
          check(document, historyComboBox);
        }

        private void check(
            @SuppressWarnings("hiding") final PlainDocument document,
            @SuppressWarnings("hiding") final JComboBox<String> historyComboBox) {
          try {
            SqlConsoleContentPane.this.isTextAvailableModel.set(document.getLength() > 0);
            final String string = document.getText(0, document.getLength());
            if (StringUtilities.isNullOrTrimmedEmpty(string)
                || ObjectUtilities.equals(string, SqlConsoleContentPane.this.historyComboBoxModel.getSelectedItem())) {
              return;
            }
            historyComboBox.setSelectedItem(null);
          } catch (final BadLocationException exception) {
            logger.log(ILevel.DEBUG, exception.getMessage(), exception);
          }
        }
      });
      final JToolBar editorToolBar = new JToolBar();
      editorToolBar.setFloatable(false);
      editorToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.open)
              .setTooltip(SqlConsoleMessages.open)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.DOCUMENT_OPEN)
              .setProcedure(component -> setEditorContent(document, open(component, getEditiorContent(document))))
              .build());
      editorToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.save)
              .setTooltip(SqlConsoleMessages.save)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.DOCUMENT_SAVE)
              .setEnabledDistributor(this.isTextAvailableModel)
              .setProcedure(component -> save(component, getEditiorContent(document)))
              .build());
      editorToolBar.add(
          new ConfigurableActionBuilder()
              .setName(SqlConsoleMessages.execute)
              .setTooltip(SqlConsoleMessages.execute)
              .setEnabledDistributor(this.textActionsEnabledDistributor)
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.MEDIA_PLAYBACK_START)
              .setTask(() -> this.statementExecutor.executeStatement(getEditiorContent(document)))
              .build());
      editorPane.setTransferHandler(new PlainTextEditorPaneTransferHandler(document, editorPane));
      editorPane.addKeyListener(new KeyAdapter() {

        @Override
        public void keyReleased(final KeyEvent event) {
          if (event.isControlDown()) {
            if (event.getKeyCode() == KeyEvent.VK_C) { // Copy
              final int start = editorPane.getSelectionStart();
              final int end = editorPane.getSelectionEnd();
              final String editorContent = getEditorContent(document, start, end);
              if (editorContent == null) {
                return;
              }
              final StringSelection selection = new StringSelection(editorContent);
              CLIPBOARD.setContents(selection, selection);
              return;
            }
            if (event.getKeyCode() == KeyEvent.VK_X) { // Copy
              final int start = editorPane.getSelectionStart();
              final int end = editorPane.getSelectionEnd();
              final String editorContent = getEditorContent(document, start, end);
              if (editorContent == null) {
                return;
              }
              insertEditorContent(document, "", start, end); //$NON-NLS-1$
              final StringSelection selection = new StringSelection(editorContent);
              CLIPBOARD.setContents(selection, selection);
              return;
            }
          }
        }

      });

      final JScrollPane editorScrollPane = new JScrollPane(editorPane);
      editorScrollPane.setMinimumSize(new Dimension(400, 300));
      final JPanel editorHeadPane = new JPanel(new BorderLayout());
      editorHeadPane.add(editorToolBar, BorderLayout.NORTH);
      editorHeadPane.add(historyComboBox, BorderLayout.CENTER);
      final JPanel editorContainerPane = new JPanel(new BorderLayout());
      editorContainerPane.setMinimumSize(new Dimension(400, 320));
      editorContainerPane.add(editorHeadPane, BorderLayout.NORTH);
      editorContainerPane.add(editorScrollPane, BorderLayout.CENTER);
      final ResultSetTableModel tableModel = new ResultSetTableModel(this.statusModel, this.resultSetModel);
      final JTable resultTable = new Table(tableModel, s -> StringUtilities.substitute(s, 8, 120));
      final DragSource source = DragSource.getDefaultDragSource();

      resultTable.addKeyListener(new KeyAdapter() {

        @Override
        public void keyReleased(final KeyEvent event) {
          if (event.isControlDown()) {
            if (event.getKeyCode() == KeyEvent.VK_C) { // Copy
              final StringSelection selection = new StringSelection(getSelectedRowsAsString(tableModel, resultTable));
              CLIPBOARD.setContents(selection, selection);
            }
          }
        }

      });

      source.createDefaultDragGestureRecognizer(resultTable, DnDConstants.ACTION_COPY, new DragGestureListener() {

        @Override
        public void dragGestureRecognized(final DragGestureEvent dge) {
          @SuppressWarnings("resource")
          final ResultSet result = SqlConsoleContentPane.this.resultSetModel.get();
          if (result == null) {
            return;
          }

          final Transferable transferable = new Transferable() {

            final DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();

            @Override
            public boolean isDataFlavorSupported(final DataFlavor flavor) {
              return DataFlavor.stringFlavor.equals(flavor) || this.textPlainUnicodeFlavor.equals(flavor);
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
              return new DataFlavor[]{ DataFlavor.stringFlavor, this.textPlainUnicodeFlavor };
            }

            @Override
            public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
              final String string = getSelectedRowsAsString(tableModel, resultTable);
              if (this.textPlainUnicodeFlavor.equals(flavor)) {
                return new ByteArrayInputStream(string.getBytes(this.textPlainUnicodeFlavor.getParameter("charset"))); //$NON-NLS-1$
              }
              return string;
            }
          };
          dge.startDrag(null, transferable);
        }
      });

      resultTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

        @Override
        public void columnSelectionChanged(final ListSelectionEvent e) {
        }

        @Override
        public void columnRemoved(final TableColumnModelEvent e) {
        }

        @Override
        public void columnMoved(final TableColumnModelEvent e) {
        }

        @Override
        public void columnMarginChanged(final ChangeEvent e) {
        }

        @Override
        public void columnAdded(final TableColumnModelEvent e) {
          GuiUtilities.invokeLater(() -> {
            @SuppressWarnings("hiding")
            final TableColumnModel source = (TableColumnModel) e.getSource();
            final int index = e.getToIndex();
            final TableColumn column = source.getColumn(index);
            final Object headerValue = column.getHeaderValue();
            if (headerValue == null || SqlConsoleMessages.empty.equals(headerValue)) {
              return;
            }
            column.setCellRenderer(
                SqlConsoleContentPane.this.dataBaseTableCellValueRenderFactory
                    .create(SqlConsoleContentPane.this.description, tableModel.getColumnTypeName(index)));
          });
        }
      });

      resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      final JScrollPane resultTableScrollPane = new JScrollPane(resultTable);
      resultTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      resultTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      final JPanel resultPane = new JPanel(new GridLayout(1, 1, 2, 2));
      resultPane.add(resultTableScrollPane);
      final JPanel panel = new JPanel(new BorderLayout(2, 2));
      final IObjectField<String> statusField = new StringFieldBuilder()
          .setEditable(false)
          .setBackgroundColor(panel.getBackground())
          .setModel(this.statusModel)
          .build();

      final JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorContainerPane, resultPane);
      verticalSplitPane.setDividerLocation(0.4);
      final JSplitPane horizontalSplitPane = new JSplitPane(
          JSplitPane.HORIZONTAL_SPLIT,
          treeContainerPane,
          verticalSplitPane);
      horizontalSplitPane.setDividerLocation(0.6);
      panel.add(horizontalSplitPane, BorderLayout.CENTER);
      panel.add(statusField.getComponent(), BorderLayout.SOUTH);
      this.contentPane = panel;
    }
    return this.contentPane;
  }

  private String createTableSelectStatement(final IDatabaseTableName tableName) {
    return tableName.getSchemaName() == null
        ? String.format("select * from \"%1$s\"", tableName.getTableName())//$NON-NLS-1$
        : String.format("select * from \"%1$s\".\"%2$s\"", tableName.getSchemaName(), tableName.getTableName()); //$NON-NLS-1$
  }

  private void consumeObject(
      final TreePath treePath,
      final IApplicable<Object> applicable,
      final IConsumer<Object, RuntimeException> consumer) {
    if (treePath == null) {
      consumer.consume(null);
      return;
    }
    final Object lastPathComponent = ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject();
    if (applicable.isApplicable(lastPathComponent)) {
      consumer.consume(lastPathComponent);
    }
  }

  private void disconnect(@SuppressWarnings("unused") final Component parentComponent) {
    try {
      this.statusModel.set(SqlConsoleMessages.working);
      if (!this.connectionModel.get().isClosed()) {
        this.connectionModel.get().close();
      }
      final boolean isClosed = this.connectionModel.get().isClosed();
      this.isConnectedModel.set(!isClosed);
      this.isDisconnectedModel.set(isClosed);
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, "Couldn't close connection", exception); //$NON-NLS-1$
      this.statusModel.set(exception.getMessage());
    }
  }

  private void connect(@SuppressWarnings("unused") final Component parentComponent) {
    try {
      this.statusModel.set(SqlConsoleMessages.working);
      if (!this.connectionModel.get().isClosed()) {
        this.connectionModel.get().close();
      }
      this.connectionModel.set(this.databaseConnector.connectWritable(this.description));
      final boolean isClosed = this.connectionModel.get().isClosed();
      this.isConnectedModel.set(!isClosed);
      this.isDisconnectedModel.set(isClosed);
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, "Couldn't close connection", exception); //$NON-NLS-1$
      this.statusModel.set(exception.getMessage());
    }
  }

  private void setEditorContent(final PlainDocument document, final String string) {
    GuiUtilities.invokeLater(() -> {
      try {
        document.replace(0, document.getLength(), string, null);
      } catch (final BadLocationException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      }
    });
  }

  private void insertEditorContent(final PlainDocument document, final String string, final int start, final int end) {
    GuiUtilities.invokeLater(() -> {
      try {
        if (start == end) {
          document.insertString(start, string, null);
        } else {
          document.replace(start, end - start, string, null);
        }
      } catch (final BadLocationException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      }
    });
  }

  private String getEditorContent(final PlainDocument document, final int start, final int end) {
    try {
      if (start == end) {
        return null;
      }
      return document.getText(start, end - start);
    } catch (final BadLocationException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return null;
    }
  }

  private String getEditiorContent(final PlainDocument document) {
    try {
      return document.getText(0, document.getLength());
    } catch (final BadLocationException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.statusModel.set(exception.getMessage());
      return null;
    }
  }

  private String open(final Component component, final String string) {
    final Iterable<FileFilter> filters = Arrays.asList(new FileFilter() {

      @Override
      public boolean accept(final File pathname) {
        return FileUtilities.hasExtension(pathname, IFileExtensions.SQL);
      }

      @Override
      public String getDescription() {
        return "SQL File (*.sql)"; //$NON-NLS-1$
      }
    });
    final IFileChooserResult result = FileChoosers.show(
        GuiUtilities.getParentWindow(component),
        SqlConsoleContentPane.this.preferences.node(FileChoosers.DEFAULT_PREFERENCE_NODE),
        new OpenFileChooserConfiguration(filters, JFileChooser.FILES_ONLY, false));
    if (result.getReturnState() == JFileChooser.APPROVE_OPTION) {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      final File file = result.getSelectedFile();
      try (InputStream stream = new FileInputStream(file)) {
        final String content = IoUtilities.toString(stream, Charset.defaultCharset().name());
        this.statusModel.set(SqlConsoleMessages.done);
        return content;
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        SqlConsoleContentPane.this.statusModel.set(exception.getMessage());
        return string;
      }
    }
    return string;
  }

  private void save(final Component component, final String string) {
    if (string == null) {
      return;
    }
    final Iterable<FileFilter> filters = Arrays.asList(new FileFilter() {

      @Override
      public boolean accept(final File pathname) {
        return FileUtilities.hasExtension(pathname, IFileExtensions.SQL);
      }

      @Override
      public String getDescription() {
        return "SQL File (*.sql)"; //$NON-NLS-1$
      }
    });
    final IFileChooserResult result = FileChoosers.show(
        GuiUtilities.getParentWindow(component),
        SqlConsoleContentPane.this.preferences.node(FileChoosers.DEFAULT_PREFERENCE_NODE),
        new SaveFileChooserConfiguration(filters, JFileChooser.FILES_ONLY, false, false));
    if (result.getReturnState() == JFileChooser.APPROVE_OPTION) {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      final File file = result.getSelectedFile();
      try (OutputStream stream = new FileOutputStream(file)) {
        IoUtilities.pipe(new ByteArrayInputStream(string.getBytes(Charset.defaultCharset())), stream);
        SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.done);
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        SqlConsoleContentPane.this.statusModel.set(exception.getMessage());
      }
    }
  }

  private void getMetadataResult(
      final IAggregator<DatabaseMetaData, IDatabaseTableName, ResultSet, SQLException> metadataProvider) {
    try {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      final DatabaseMetaData metaData = this.connectionModel.get().getMetaData();
      final IDatabaseTableName tableName = this.selectedTable.get();
      @SuppressWarnings("resource")
      final ResultSet result = metadataProvider.aggregate(metaData, tableName);
      if (result == null) {
        SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.resultReseter.reset();
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.statusModel.set(exception.getMessage());
    }
  }

  @SuppressWarnings("resource")
  private void querySequenceMetadata(final IDatabaseSequenceName name) {
    try {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      final ResultSet result = SqlConsoleContentPane.this.databaseFacade
          .getSequenceMetadata(SqlConsoleContentPane.this.connectionModel.get(), name);
      if (result == null) {
        SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.resultReseter.reset();
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      SqlConsoleContentPane.this.statusModel.set(exception.getMessage());
    }
  }

  private void queryTriggerMetadata(final IDatabaseTriggerName name) {
    try {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      @SuppressWarnings("resource")
      final ResultSet result = SqlConsoleContentPane.this.databaseFacade
          .getTriggerMetadata(SqlConsoleContentPane.this.connectionModel.get(), name);
      if (result == null) {
        SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.resultReseter.reset();
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      SqlConsoleContentPane.this.statusModel.set(exception.getMessage());
    }
  }

  @SuppressWarnings("resource")
  private void queryIndexMetadata(final IDatabaseIndexName name) {
    try {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      final ResultSet result = SqlConsoleContentPane.this.databaseFacade
          .getIndexMetadata(SqlConsoleContentPane.this.connectionModel.get(), name);
      if (result == null) {
        SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.resultReseter.reset();
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      SqlConsoleContentPane.this.statusModel.set(exception.getMessage());
    }
  }

  private void queryConstraintMetadata(final IDatabaseConstraintName name) {
    try {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      @SuppressWarnings("resource")
      final ResultSet result = SqlConsoleContentPane.this.databaseFacade
          .getConstraintMetadata(SqlConsoleContentPane.this.connectionModel.get(), name);
      if (result == null) {
        SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.resultReseter.reset();
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      SqlConsoleContentPane.this.statusModel.set(exception.getMessage());
    }
  }

  private String getCatalog() {
    try {
      return this.connectionModel.get().getCatalog();
    } catch (final AbstractMethodError | Exception exception) {
      return null;
    }
  }

  private <I> void run(
      final Component component,
      final String title,
      final String text,
      final IBlock<RuntimeException> block) {
    try {
      new ProgressDialogLauncher<>((progressMonitor, canceler) -> {
        block.execute();
        return null;
      }).setTitle(title).setText(text).setDescription("").launch(component); //$NON-NLS-1$
    } catch (final InterruptedException exception) {
      // // nothing to do
    }
  }

  private String getSelectedRowsAsString(final ResultSetTableModel tableModel, final JTable resultTable) {
    final StringBuilder builder = new StringBuilder();
    boolean rowFlag = false;
    boolean columnFlag = false;

    for (int column = 0; column < tableModel.getColumnCount(); column++) {
      if (columnFlag) {
        builder.append(", "); //$NON-NLS-1$
      }
      builder.append("'"); //$NON-NLS-1$
      builder.append(tableModel.getColumnName(column));
      builder.append("'"); //$NON-NLS-1$
      columnFlag = true;
    }
    builder.append("\n"); //$NON-NLS-1$

    columnFlag = false;

    for (final int row : resultTable.getSelectedRows()) {
      if (rowFlag) {
        builder.append("\n"); //$NON-NLS-1$
      }

      for (int column = 0; column < tableModel.getColumnCount(); column++) {
        if (columnFlag) {
          builder.append(", "); //$NON-NLS-1$
        }
        columnFlag = true;
        final Object value = tableModel.getValueAt(row, column);
        if (value == null) {
          builder.append("null"); //$NON-NLS-1$
          continue;
        }
        final IObjectToStringConverter<Object> toStringConverter = SqlConsoleContentPane.this.dataBaseTableCellValueToStringConverterProvider
            .get(SqlConsoleContentPane.this.description, tableModel.getColumnTypeName(column));
        if (value instanceof Number) {
          builder.append(value.toString());
          continue;
        }
        builder.append("'"); //$NON-NLS-1$
        if (toStringConverter != null) {
          builder.append(toStringConverter.toString(value));
        } else {
          builder.append(value.toString());
        }
        builder.append("'"); //$NON-NLS-1$
      }
      rowFlag = true;
      columnFlag = false;
    }
    return builder.toString();
  }
}
