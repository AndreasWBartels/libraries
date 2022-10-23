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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.database.swing.console;

import net.anwiba.commons.jdbc.connection.IDatabaseConnector;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.IDatabaseFacade;
import net.anwiba.commons.jdbc.metadata.Property;
import net.anwiba.commons.jdbc.name.IDatabaseColumnName;
import net.anwiba.commons.jdbc.name.IDatabaseConstraintName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseSchemaName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IBiConsumer;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.registry.HierarchicalClassKeyRegistry;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.AndAggregatedBooleanDistributor;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IObjectListModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectListModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.model.OrAggregatedBooleanDistributor;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.preferences.StringListPreference;
import net.anwiba.commons.reference.utilities.FileUtilities;
import net.anwiba.commons.reference.utilities.IFileExtensions;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.swing.action.ActionProcedurBuilder;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.action.IActionTask;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.progress.ProgressDialogLauncher;
import net.anwiba.commons.swing.filechooser.FileChoosers;
import net.anwiba.commons.swing.filechooser.IFileChooserResult;
import net.anwiba.commons.swing.filechooser.OpenFileChooserConfiguration;
import net.anwiba.commons.swing.filechooser.SaveFileChooserConfiguration;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.StringFieldBuilder;
import net.anwiba.commons.swing.table.ObjectTable;
import net.anwiba.commons.swing.table.ObjectTableBuilder;
import net.anwiba.commons.swing.table.Table;
import net.anwiba.commons.swing.table.WrappedTableModel;
import net.anwiba.commons.swing.table.renderer.ObjectTableCellRenderer;
import net.anwiba.commons.swing.tree.FilteredDefaultTreeModel;
import net.anwiba.commons.swing.tree.ReloadableFolderTreeNode;
import net.anwiba.commons.swing.ui.ObjectUiBuilder;
import net.anwiba.commons.swing.ui.ObjectUiListCellRenderer;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.swing.utilities.TableUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.process.IProcessManager;
import net.anwiba.commons.thread.process.ProcessBuilder;
import net.anwiba.commons.thread.progress.IProgressTask;
import net.anwiba.commons.utilities.property.IProperties;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.database.swing.console.converter.IDataBaseTableCellValueToStringConverterProvider;
import net.anwiba.database.swing.console.result.DataBaseTableCellValueRenderFactory;
import net.anwiba.database.swing.console.result.PropertyTableModel;
import net.anwiba.database.swing.console.result.ResultReseter;
import net.anwiba.database.swing.console.result.ResultSetTableModel;
import net.anwiba.database.swing.console.tree.DatabaseNamesTreeRenderer;
import net.anwiba.database.swing.console.tree.SchemaTreeFactory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public final class SqlConsoleContentPane extends AbstractContentPane {

  public static final class InternalDatabaseConnector implements IDatabaseConnector {
    private final IObjectModel<Connection> connectionModel;
    private final IDatabaseConnector databaseConnector;
    private final IJdbcConnectionDescription description;

    public InternalDatabaseConnector(final IObjectModel<Connection> connectionModel,
        final IDatabaseConnector databaseConnector,
        final IJdbcConnectionDescription description) {
      this.connectionModel = connectionModel;
      this.databaseConnector = databaseConnector;
      this.description = description;
    }

    @Override
    public boolean isConnectable(final String url,
        final String userName,
        final String password,
        final IProperties properties) {
      return equals(this.description, url, userName, password, properties)
          ? this.connectionModel.get() != null
          : this.databaseConnector.isConnectable(url, userName, password, properties);
    }

    private boolean equals(final IJdbcConnectionDescription description,
        final String url,
        final String userName,
        final String password,
        final IProperties properties) {
      return Objects.equals(url, description.getUrl())
          && Objects.equals(userName, description.getUserName())
          && Objects.equals(password, description.getPassword())
          && Objects.equals(properties, description.getProperties());
    }

    @Override
    public Connection
        connectWritable(final String url,
            final String userName,
            final String password,
            final boolean isAutoCommitEnabled,
            final int timeout,
            final IProperties properties)
            throws SQLException {

      if (equals(this.description, url, userName, password, properties)) {
        Connection connection = this.connectionModel.get();
        if (connection == null) {
          throw new SQLException("databese isn't connected");
        }
      }
      return this.databaseConnector.connectWritable(url, userName, password, isAutoCommitEnabled, timeout, properties);
    }

    @Override
    public Connection connectReadOnly(final String url,
        final String userName,
        final String password,
        final int timeout,
        final IProperties properties)
        throws SQLException {
      return equals(this.description, url, userName, password, properties)
          ? connectWritable(url, userName, password, false, timeout, properties)
          : this.databaseConnector.connectReadOnly(url, userName, password, timeout, properties);
    }
  }

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
  private final IObjectListModel<String> statementValuesModel = new ObjectListModel<>();

  private final IObjectModel<ResultSet> resultSetModel = new ObjectModel<>();
  private final IObjectModel<IDatabaseTableName> selectedTable = new ObjectModel<>();
  private final IObjectModel<IDatabaseColumnName> selectedColumn = new ObjectModel<>();
  private final IObjectModel<IDatabaseSequenceName> selectedSequence = new ObjectModel<>();
  private final IObjectModel<IDatabaseTriggerName> selectedTrigger = new ObjectModel<>();
  private final IObjectModel<IDatabaseIndexName> selectedIndex = new ObjectModel<>();
  private final IObjectModel<IDatabaseConstraintName> selectedConstraint = new ObjectModel<>();
  private final IBooleanModel isConnectionDescriptionSelectedModel = new BooleanModel(false);
  private final IBooleanModel isTextAvailableModel = new BooleanModel(false);
  private final IBooleanModel isTableSelectedModel = new BooleanModel(false);
  private final IBooleanModel isColumnsSelectedModel = new BooleanModel(false);
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
  private final ResultSetTableModel resultSetTableModel = new ResultSetTableModel(
      this.statusModel,
      this.resultSetModel);
  private final WrappedTableModel resultDataTableModel = new WrappedTableModel(this.resultSetTableModel);
  private final WrappedTableModel resultMetaDataTableModel =
      new WrappedTableModel(this.resultSetTableModel.getResultSetMetaDataTableModel());

  public SqlConsoleContentPane(
      final IObjectModel<DataState> dataStateModel,
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
    super(dataStateModel);
    this.databaseFacade = databaseFacade;
    this.schema = schema;
    this.dataBaseTableCellValueRenderFactory = new DataBaseTableCellValueRenderFactory(
        dataBaseTableCellValueToStringConverterProvider);
    this.historyComboBoxModel.addElement(null);
    final StringListPreference statementListPreference = new StringListPreference(
        preferences.node(description.getProtocol()).node("statements")); //$NON-NLS-1$
    statementListPreference.get().stream().forEach(v -> this.historyComboBoxModel.addElement(v));
    this.historyComboBoxModel.setSelectedItem(null);
    this.historyComboBoxModel.addListDataListener(
        createStatementHistoryPreferenceSynchroniceListener(processManager, statementListPreference));
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
        Arrays
            .asList(
                isConnectedModel,
                new OrAggregatedBooleanDistributor(
                    Arrays
                        .asList(
                            this.isConnectionDescriptionSelectedModel,
                            this.isTableSelectedModel,
                            this.isColumnsSelectedModel,
                            this.isSequenceSelectedModel,
                            this.isIndexSelectedModel,
                            this.isConstraintSelectedModel,
                            this.isTriggerSelectedModel))));
    this.createStatementActionsEnabledDistributor = new AndAggregatedBooleanDistributor(
        Arrays
            .asList(
                isConnectedModel,
                new OrAggregatedBooleanDistributor(Arrays.asList(this.isTriggerSelectedModel,
                    new AndAggregatedBooleanDistributor(Arrays.asList(this.isTableSelectedModel,
                        new BooleanModel(databaseFacade.supportsTableStatement())))))));
    this.resultReseter = new ResultReseter(this.statementModel, this.resultSetModel);
    this.schemaTreeFactory = new SchemaTreeFactory(
        new InternalDatabaseConnector(connectionModel, databaseConnector, description),
        databaseFacade,
        this.statusModel);
    this.statementExecutor = new StatementExecutor(
        connectionModel,
        this.resultReseter,
        this.statementModel,
        this.statementValuesModel,
        this.resultSetModel,
        this.statusModel,
        statement -> {
          for (int i = this.historyComboBoxModel.getSize() - 1; i > -1; i--) {
            if (ObjectUtilities.equals(this.historyComboBoxModel.getElementAt(i), statement)) {
              this.historyComboBoxModel.setSelectedItem(statement);
              return;
            }
          }
          GuiUtilities.invokeLater(() -> {
            this.historyComboBoxModel.setSelectedItem(null);
            if (this.historyComboBoxModel.getSize() > 30) {
              this.historyComboBoxModel.removeElementAt(0);
            }
            this.historyComboBoxModel.addElement(statement);
            this.historyComboBoxModel.setSelectedItem(statement);
          });
        },
        isDisconnectedModel,
        isConnectedModel);
    this.resultSetModel.addChangeListener(() -> {
      this.resultDataTableModel.wrap(this.resultSetTableModel);
    });
  }

  @Override
  public JComponent getComponent() {
    if (this.contentPane == null) {

      final JEditorPane editorPane = new JEditorPane("text/plain", ""); //$NON-NLS-1$//$NON-NLS-2$
      Font font = editorPane.getFont();
      editorPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, font.getSize()));

      final PlainDocument document = (PlainDocument) editorPane.getDocument();
      editorPane.setTransferHandler(new PlainTextEditorPaneTransferHandler(document, editorPane));
      editorPane.addKeyListener(createStatementEditorShortCutListener(editorPane, document));

      DefaultMutableTreeNode root = createSchemaTree();
      final FilteredDefaultTreeModel schemaTreeModel = new FilteredDefaultTreeModel(root);
      final JTree schemaTreePane = new JTree(schemaTreeModel);
      schemaTreePane.setDragEnabled(true);
      Optional.of(createTransferableHandler()).consume(h -> schemaTreePane.setTransferHandler(h));
      schemaTreePane.setCellRenderer(new DatabaseNamesTreeRenderer());
      schemaTreePane.addTreeWillExpandListener(createTreeNodeExpandeListener(schemaTreeModel));
      schemaTreePane.addMouseListener(createDoubleClickSchemaTreeListener(schemaTreePane));
      final TreeSelectionModel schemaTreeSelectionModel = schemaTreePane.getSelectionModel();
      schemaTreeSelectionModel.addTreeSelectionListener(createTreeNodeSelectionListener());

      final JToolBar treeToolBar = new JToolBar();
      treeToolBar.setFloatable(false);
      treeToolBar.add(createReconnectAction());
      treeToolBar.add(createDisconnectAction());
      treeToolBar.add(createReloadSchemaAction(schemaTreeModel));
      treeToolBar.add(createShowPropertiesAction());
      treeToolBar.add(createShowCapabilitiesAction());
      treeToolBar.add(createShowTypesAction());
      treeToolBar.add(createShowTablePrivilegsAction());
      treeToolBar.add(createShwoColumnPrivilegesAction());
      treeToolBar.add(createShowTableContentAction());
      treeToolBar.add(createCreateStatementAction(document));

      final JComboBox<String> historyComboBox = new JComboBox<>(this.historyComboBoxModel);
      historyComboBox.setRenderer(new ObjectUiListCellRenderer<String>(new ObjectUiBuilder<String>()
          .tooltip(t -> toHtml(t))
          .build()));
      historyComboBox.addActionListener(e -> {
        if (this.historyComboBoxModel.getSelectedItem() != null) {
          setEditorContent(document, (String) this.historyComboBoxModel.getSelectedItem());
        }
      });
      document.addDocumentListener(createStatementHistoryComboBoxSynchroniceListener(document, historyComboBox));

      final JToolBar editorToolBar = new JToolBar();
      editorToolBar.setFloatable(false);
      editorToolBar.add(createOpenSqlStatementFromFileAction(document));
      editorToolBar.add(createSaveSqlStatementToFileAction(document));
      editorToolBar.add(createExecuteSqlStatementAction(document));
      final JScrollPane editorScrollPane = new JScrollPane(editorPane);
      editorScrollPane.setMinimumSize(new Dimension(400, 300));
      final JPanel editorHeadPane = new JPanel(new BorderLayout());
      editorHeadPane.add(editorToolBar, BorderLayout.NORTH);
      editorHeadPane.add(historyComboBox, BorderLayout.CENTER);
      final JPanel editorContainerPane = new JPanel(new BorderLayout());
      editorContainerPane.setMinimumSize(new Dimension(400, 320));
      editorContainerPane.setPreferredSize(new Dimension(400, 320));
      editorContainerPane.add(editorHeadPane, BorderLayout.NORTH);
      editorContainerPane.add(editorScrollPane, BorderLayout.CENTER);

      final DragSource source = DragSource.getDefaultDragSource();
      final JTable resultTable = new Table(this.resultDataTableModel, s -> StringUtilities.reduce(s, 8, 120));
      resultTable.addKeyListener(createResultTableShortCutListener(this.resultDataTableModel, resultTable));
      resultTable.getColumnModel()
          .addColumnModelListener(createTableColumnAdjustmentListener(this.resultDataTableModel));
      resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      source.createDefaultDragGestureRecognizer(
          resultTable,
          DnDConstants.ACTION_COPY,
          createResultTableGestureListener(this.resultDataTableModel, resultTable));

      final JScrollPane resultTableScrollPane = new JScrollPane(resultTable);
      resultTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      resultTableScrollPane
          .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      final JPanel resultPane = new JPanel(new GridLayout(1, 1, 2, 2));
      resultPane.add(resultTableScrollPane);

      final JTable resultMetaDataTable =
          new Table(this.resultMetaDataTableModel, s -> StringUtilities.reduce(s, 8, 120));
      resultMetaDataTable.addKeyListener(createResultTableShortCutListener(this.resultMetaDataTableModel, resultTable));
      resultMetaDataTable.getColumnModel()
          .addColumnModelListener(createTableColumnAdjustmentListener(this.resultMetaDataTableModel));
      resultMetaDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      source.createDefaultDragGestureRecognizer(
          resultMetaDataTable,
          DnDConstants.ACTION_COPY,
          createResultTableGestureListener(this.resultMetaDataTableModel, resultMetaDataTable));

      final JScrollPane resultMetaDataScrollPane = new JScrollPane(resultMetaDataTable);
      resultMetaDataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      resultMetaDataScrollPane
          .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      final JPanel resultMetaDataPane = new JPanel(new GridLayout(1, 1, 2, 2));
      resultMetaDataPane.add(resultMetaDataScrollPane);

      final JPanel panel = new JPanel(new BorderLayout(2, 2));
      final IObjectField<String> statusField = new StringFieldBuilder()
          .setEditable(false)
          .setBackgroundColor(panel.getBackground())
          .setModel(this.statusModel)
          .build();

      final JScrollPane treeScrollPane = new JScrollPane(schemaTreePane);
      treeScrollPane.setMinimumSize(new Dimension(220, 300));
      final JPanel treeHeadComponent = new JPanel(new BorderLayout());
      treeHeadComponent.add(treeToolBar, BorderLayout.NORTH);
      final JPanel treeContainerPane = new JPanel(new BorderLayout());
      treeContainerPane.setMinimumSize(new Dimension(240, 380));
      treeContainerPane.add(treeHeadComponent, BorderLayout.NORTH);
      treeContainerPane.add(treeScrollPane, BorderLayout.CENTER);
      final JTabbedPane resultTabbedPane = new JTabbedPane();
      resultTabbedPane.add("data", resultPane);
      resultTabbedPane.add("metadata", resultMetaDataPane);
      final ObjectTable<String> preparedStatementValueTable = createPreparedStatementValueTable();
      final JComponent preparedStatementValueTableComponent = preparedStatementValueTable.getComponent();
      preparedStatementValueTable.getTableModel().addListModelListener(new IChangeableListListener<String>() {

        @Override
        public void objectsChanged(final Iterable<String> objects) {
          SqlConsoleContentPane.this.statementValuesModel.set(objects);
        }
      });
      final JSplitPane editorSplitPane = new JSplitPane(
          JSplitPane.HORIZONTAL_SPLIT,
          editorContainerPane,
          preparedStatementValueTableComponent);
      editorSplitPane.setDividerLocation(0.7);
      editorSplitPane.setResizeWeight(0.7);
      final JSplitPane verticalSplitPane = new JSplitPane(
          JSplitPane.VERTICAL_SPLIT,
          editorSplitPane,
          resultTabbedPane);
      verticalSplitPane.setDividerLocation(0.6);
      verticalSplitPane.setResizeWeight(0.6);
      final JSplitPane horizontalSplitPane = new JSplitPane(
          JSplitPane.HORIZONTAL_SPLIT,
          treeContainerPane,
          verticalSplitPane);
      horizontalSplitPane.setDividerLocation(284);
      panel.add(horizontalSplitPane, BorderLayout.CENTER);
      panel.add(statusField.getComponent(), BorderLayout.SOUTH);
      this.contentPane = panel;
      queryDataBaseProperties(ICanceler.DummyCanceler);
    }
    return this.contentPane;
  }

  private ObjectTable<String> createPreparedStatementValueTable() {
    return new ObjectTableBuilder<String>()
        .setSingleSelectionMode()
        .addEditableStringColumn("Statement value", s -> s, (o, n) -> n, 50)
        .addAddObjectAction((parent, value) -> "")
        .addRemoveObjectsAction()
        .addClearTableAction()
        .addMoveObjectUpAction()
        .addMoveObjectDownAction()
        .build();
  }

  private DragGestureListener createResultTableGestureListener(final WrappedTableModel tableModel,
      final JTable resultTable) {
    return new DragGestureListener() {

      @Override
      public void dragGestureRecognized(final DragGestureEvent dge) {
        if (tableModel.getRowCount() == 0 || resultTable.getSelectionModel().isSelectionEmpty()) {
          return;
        }

        final Transferable transferable = new Transferable() {

          final DataFlavor textPlainUnicodeFlavor = DataFlavor
              .getTextPlainUnicodeFlavor();

          @Override
          public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return DataFlavor.stringFlavor.equals(flavor)
                || this.textPlainUnicodeFlavor.equals(flavor);
          }

          @Override
          public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {
                DataFlavor.stringFlavor,
                this.textPlainUnicodeFlavor };
          }

          @Override
          public Object getTransferData(final DataFlavor flavor)
              throws UnsupportedFlavorException,
              IOException {
            final String string = getSelectedRowsAsString(tableModel, resultTable);
            if (this.textPlainUnicodeFlavor.equals(flavor)) {
              return new ByteArrayInputStream(
                  string.getBytes(this.textPlainUnicodeFlavor.getParameter("charset"))); //$NON-NLS-1$
            }
            return string;
          }
        };
        dge.startDrag(null, transferable);
      }
    };
  }

  private KeyAdapter createResultTableShortCutListener(final WrappedTableModel tableModel, final JTable resultTable) {
    return new KeyAdapter() {

      @Override
      public void keyReleased(final KeyEvent event) {
        if (event.isControlDown()) {
          if (event.getKeyCode() == KeyEvent.VK_C) { // Copy
            final StringSelection selection = new StringSelection(
                getSelectedRowsAsString(tableModel, resultTable));
            CLIPBOARD.setContents(selection, selection);
          }
        }
      }
    };
  }

  private KeyAdapter createStatementEditorShortCutListener(final JEditorPane editorPane, final PlainDocument document) {
    return new KeyAdapter() {

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
    };
  }

  private MouseAdapter createDoubleClickSchemaTreeListener(final JTree schemaTreePane) {
    return new MouseAdapter() {

      @Override
      public void mouseClicked(final MouseEvent e) {
        if (e.getClickCount() != 2) {
          return;
        }
        consumeObject(
            schemaTreePane.getSelectionPath(),
            o -> o instanceof IDatabaseTableName // table, view, synonym
                || o instanceof IDatabaseIndexName
                || o instanceof IDatabaseConstraintName
                || o instanceof IDatabaseSequenceName
                || o instanceof IDatabaseTriggerName
                || o instanceof IDatabaseColumnName,
            (p, t) -> Optional
                .of(t)
                .consume(
                    o -> run(e.getComponent(),
                        SqlConsoleMessages.SQLConsole,
                        SqlConsoleMessages.loadTableContent,
                        (progressMonitor, canceler) -> {
                          if (o instanceof IJdbcConnectionDescription) {
                            queryDataBaseProperties(canceler);
                            return null;
                          }
                          if (o instanceof IDatabaseTableName name) { // table, view, synonym
                            SqlConsoleContentPane.this.statementExecutor
                                .executeStatement(
                                    canceler,
                                    createTableSelectStatement(name));
                            return null;
                          }
                          if (o instanceof IDatabaseSequenceName name) {
                            querySequenceMetadata(canceler, name);
                            return null;
                          }
                          if (o instanceof IDatabaseTriggerName name) {
                            queryTriggerMetadata(canceler, name);
                            return null;
                          }
                          if (o instanceof IDatabaseIndexName name) {
                            queryIndexMetadata(canceler, name);
                            return null;
                          }
                          if (o instanceof IDatabaseConstraintName name) {
                            IDatabaseTableName table = select(p, IDatabaseTableName.class);
                            queryConstraintMetadata(canceler, table, name);
                            return null;
                          }
                          if (o instanceof IDatabaseColumnName name) {
                            queryColumnMetadata(canceler, name);
                            return null;
                          }
                          return null;
                        })));
      }
    };
  }

  private TreeSelectionListener createTreeNodeSelectionListener() {
    return e -> {
      final TreePath treePath = e.getNewLeadSelectionPath();
      consumeObject(
          treePath, //
          o -> true,
          (path, input) -> {
            this.selectedTable.set(null);
            this.selectedColumn.set(null);
            this.selectedSequence.set(null);
            this.selectedTrigger.set(null);
            this.selectedIndex.set(null);
            this.selectedConstraint.set(null);
            if (input instanceof IDatabaseTableName name) { // table, view, synonym
              this.selectedTable.set(name);
            } else if (input instanceof IDatabaseColumnName name) {
              this.selectedColumn.set(name);
            } else if (input instanceof IDatabaseSequenceName name) {
              this.selectedSequence.set(name);
            } else if (input instanceof IDatabaseTriggerName name) {
              this.selectedTrigger.set(name);
            } else if (input instanceof IDatabaseIndexName name) {
              this.selectedIndex.set(name);
            } else if (input instanceof IDatabaseConstraintName name) {
              this.selectedConstraint.set(name);
            }
            this.isConnectionDescriptionSelectedModel
                .set(input instanceof IJdbcConnectionDescription);
            this.isTableSelectedModel
                .set(input instanceof IDatabaseTableName); // table, view, synonym
            this.isColumnsSelectedModel
                .set(input instanceof IDatabaseColumnName);
            this.isSequenceSelectedModel
                .set(input instanceof IDatabaseSequenceName);
            this.isTriggerSelectedModel
                .set(input instanceof IDatabaseTriggerName);
            this.isIndexSelectedModel
                .set(input instanceof IDatabaseIndexName);
            this.isConstraintSelectedModel
                .set(input instanceof IDatabaseConstraintName);
          });
    };
  }

  private TreeWillExpandListener createTreeNodeExpandeListener(final FilteredDefaultTreeModel schemaTreeModel) {
    return new TreeWillExpandListener() {

      @Override
      public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
        TreePath path = event.getPath();
        if (path.getLastPathComponent() instanceof ReloadableFolderTreeNode node) {
          node.load(schemaTreeModel);
        }
      }

      @Override
      public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException {

      }
    };
  }

  private TableColumnModelListener createTableColumnAdjustmentListener(final WrappedTableModel tableModel) {
    return new TableColumnModelListener() {

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
          Class columnClass = tableModel.getColumnCount() > index
              ? tableModel.getColumnClass(index)
              : Object.class;
          final int columnWithFor = TableUtilities.getColumnWithFor(columnClass);
          column.setPreferredWidth(columnWithFor);
          column.setWidth(columnWithFor);
          final TableCellRenderer renderer = tableModel.isWrapperFor(ResultSetTableModel.class)
              ? SqlConsoleContentPane.this.dataBaseTableCellValueRenderFactory
                  .create(
                      SqlConsoleContentPane.this.description,
                      tableModel.unwrap(ResultSetTableModel.class).getColumnTypeName(index))
              : new ObjectTableCellRenderer();
          column.setCellRenderer(renderer);
        });
      }
    };
  }

  private ListDataListener createStatementHistoryPreferenceSynchroniceListener(final IProcessManager processManager,
      final StringListPreference statementListPreference) {
    return new ListDataListener() {

      @Override
      public void intervalRemoved(final ListDataEvent e) {
        // nothing to do
      }

      @Override
      public void intervalAdded(final ListDataEvent e) {
        final int size = SqlConsoleContentPane.this.historyComboBoxModel.getSize();
        final List<String> values = Streams
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
    };
  }

  private DocumentListener createStatementHistoryComboBoxSynchroniceListener(final PlainDocument document,
      final JComboBox<String> historyComboBox) {
    return new DocumentListener() {

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
              || ObjectUtilities
                  .equals(
                      string,
                      SqlConsoleContentPane.this.historyComboBoxModel.getSelectedItem())) {
            return;
          }
          historyComboBox.setSelectedItem(null);
        } catch (final BadLocationException exception) {
          logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        }
      }
    };
  }

  private AbstractAction createExecuteSqlStatementAction(final PlainDocument document) {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.execute)
        .setTooltip(SqlConsoleMessages.execute)
        .setEnabledDistributor(this.textActionsEnabledDistributor)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.MEDIA_PLAYBACK_START)
        .setProcedure(
            new ActionProcedurBuilder<String, Void>()
                .setInitializer(parentComponent -> getEditiorContent(document))
                .setTask((monitor, canceler, statementString) -> {
                  if (statementString == null || statementString.isBlank()) {
                    return null;
                  }
                  this.statementExecutor.executeStatement(canceler, statementString);
                  return null;
                })
                .build())
        .build();
  }

  private AbstractAction createSaveSqlStatementToFileAction(final PlainDocument document) {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.save)
        .setTooltip(SqlConsoleMessages.save)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.DOCUMENT_SAVE)
        .setEnabledDistributor(this.isTextAvailableModel)
        .setProcedure(component -> save(component, getEditiorContent(document)))
        .build();
  }

  private AbstractAction createOpenSqlStatementFromFileAction(final PlainDocument document) {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.open)
        .setTooltip(SqlConsoleMessages.open)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.DOCUMENT_OPEN)
        .setProcedure(
            component -> setEditorContent(
                document,
                open(component, getEditiorContent(document))))
        .build();
  }

  private AbstractAction createReconnectAction() {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.connect)
        .setTooltip(SqlConsoleMessages.connect)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GTK_CONNECT)
        .setEnabledDistributor(this.isDisconnectedModel)
        .setProcedure(c -> connect(c))
        .build();
  }

  private AbstractAction createDisconnectAction() {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.disconnect)
        .setTooltip(SqlConsoleMessages.disconnect)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GTK_DISCONNECT)
        .setEnabledDistributor(this.isConnectedModel)
        .setProcedure(c -> disconnect(c))
        .build();
  }

  private AbstractAction createReloadSchemaAction(final FilteredDefaultTreeModel schemaTreeModel) {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.reload)
        .setTooltip(SqlConsoleMessages.reloadDatabaseSchema)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.VIEW_REFRESH)
        .setEnabledDistributor(this.isConnectedModel)
        .setTask((IActionTask<Void, Void>) (monitor, canceler, value) -> {
          final DefaultMutableTreeNode node =
              this.schemaTreeFactory.create(canceler, this.description, this.schema);
          if (node == null) {
            return null;
          }
          schemaTreeModel.setRoot(node);
          return null;
        })
        .build();
  }

  private AbstractAction createShowPropertiesAction() {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.properties)
        .setTooltip(SqlConsoleMessages.properties)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.DIALOG_INFORMATION)
        .setEnabledDistributor(this.propertiesActionsEnabledDistributor)
        .setTask((IActionTask<Void, Void>) (monitor, canceler, value) -> {
          if (this.isConnectionDescriptionSelectedModel.isTrue()) {
            queryDataBaseProperties(canceler);
          }
          if (this.isTableSelectedModel.isTrue()) { // table, view, synonym
            queryTableMetadata(canceler, this.selectedTable.get());
          }
          if (this.isColumnsSelectedModel.isTrue()) { // table, view, synonym
            queryColumnMetadata(canceler, this.selectedColumn.get());
          }
          if (this.isSequenceSelectedModel.isTrue()) {
            querySequenceMetadata(canceler, this.selectedSequence.get());
          }
          if (this.isTriggerSelectedModel.isTrue()) {
            queryTriggerMetadata(canceler, this.selectedTrigger.get());
          }
          if (this.isIndexSelectedModel.isTrue()) {
            queryIndexMetadata(canceler, this.selectedIndex.get());
          }
          if (this.isConstraintSelectedModel.isTrue()) {
            queryConstraintMetadata(canceler, null, this.selectedConstraint.get());
          }
          return null;
        })
        .build();
  }

  private Action createShowCapabilitiesAction() {
    return new ConfigurableActionBuilder()
        .setName("Capabilities")
        .setTooltip("Capabilities")
        .setIcon(net.anwiba.commons.swing.icons.GuiIcons.QUERY_ICON)
        .setEnabledDistributor(this.isConnectionDescriptionSelectedModel)
        .setTask((IActionTask<Void, Void>) (monitor, canceler, value) -> {
          if (this.isConnectionDescriptionSelectedModel.isTrue()) {
            queryDataBaseCapabilities(canceler);
          }
          return null;
        })
        .build();
  }

  private Action createShowTypesAction() {
    return new ConfigurableActionBuilder()
        .setName("Datatypes")
        .setTooltip("Datatypes")
        .setIcon(net.anwiba.commons.swing.icons.GuiIcons.ZOOM_IN_ICON)
        .setEnabledDistributor(this.isConnectionDescriptionSelectedModel)
        .setTask((IActionTask<Void, Void>) (monitor, canceler, value) -> {
          if (this.isConnectionDescriptionSelectedModel.isTrue()) {
            queryDataBaseDatatypes(canceler);
          }
          return null;
        })
        .build();
  }

  private AbstractAction createShowTablePrivilegsAction() {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.tablePrivileges)
        .setTooltip(SqlConsoleMessages.tablePrivileges)
        .setEnabledDistributor(this.tableActionsEnabledDistributor)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.SECURITY_MEDIUM)
        .setTask(
            () -> getResultSet(
                (connection, tableName) -> this.databaseFacade
                    .getTablePrivileges(connection, tableName)))
        .build();
  }

  private AbstractAction createShwoColumnPrivilegesAction() {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.columnPrivileges)
        .setTooltip(SqlConsoleMessages.columnPrivileges)
        .setEnabledDistributor(this.tableActionsEnabledDistributor)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.SECURITY_HIGH)
        .setTask(
            () -> getResultSet(
                (connection, tableName) -> this.databaseFacade
                    .getColumnPrivileges(connection, tableName, null)))
        .build();
  }

  private AbstractAction createShowTableContentAction() {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.content)
        .setTooltip(SqlConsoleMessages.content)
        .setEnabledDistributor(this.tableActionsEnabledDistributor)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.VIEW_LIST_TEXT)
        .setTask(
            () -> this.statementExecutor
                .executeStatement(
                    ICanceler.DummyCanceler,
                    createTableSelectStatement(this.selectedTable.get())))
        .build();
  }

  private AbstractAction createCreateStatementAction(final PlainDocument document) {
    return new ConfigurableActionBuilder()
        .setName(SqlConsoleMessages.createStatement)
        .setTooltip(SqlConsoleMessages.createStatement)
        .setEnabledDistributor(this.createStatementActionsEnabledDistributor)
        .setIcon(
            net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.FORMAT_JUSTIFY_LEFT)
        .setTask((IActionTask<Void, Void>) (monitor, canceler, value) -> {
          try {
            this.statusModel.set(SqlConsoleMessages.working);
            String statement = null;
            if (this.isTriggerSelectedModel.isTrue()) {
              statement = this.databaseFacade
                  .getTriggerStatement(canceler,
                      this.connectionModel.get(),
                      this.selectedTrigger.get());
            }
            if (this.isTableSelectedModel.isTrue()) {
              statement = this.databaseFacade
                  .getTableStatement(canceler,
                      this.connectionModel.get(),
                      this.selectedTable.get());
            }
            if (statement == null) {
              this.statusModel.set(SqlConsoleMessages.emptyResult);
              return null;
            }
            setEditorContent(document, statement);
            this.statusModel.set(SqlConsoleMessages.done);
            return null;
          } catch (final SQLException exception) {
            logger.log(ILevel.DEBUG, "Couldn't close connection", exception); //$NON-NLS-1$
            this.statusModel.set(exception.getMessage());
            return null;
          }
        })
        .build();
  }

  private DefaultMutableTreeNode createSchemaTree() {
    try {
      return this.schemaTreeFactory
          .create(ICanceler.DummyCanceler, this.description, this.schema);
    } catch (CanceledException exception) {
      throw new UnreachableCodeReachedException(exception);
    }
  }

  private TransferHandler createTransferableHandler() {
    final HierarchicalClassKeyRegistry<Function<Object, Transferable>> registry = new HierarchicalClassKeyRegistry<>();
    register(registry,
        IDatabaseSchemaName.class,
        o -> Optional.of(o).instanceOf(IDatabaseSchemaName.class).convert(n -> n.getName()).get());
    register(registry,
        IDatabaseTableName.class, // table, view, synonym
        o -> Optional.of(o).instanceOf(IDatabaseTableName.class).convert(n -> n.getName()).get());
    register(registry,
        IDatabaseColumnName.class,
        o -> Optional.of(o).instanceOf(IDatabaseColumnName.class).convert(n -> n.getName()).get());
    register(registry,
        IDatabaseTriggerName.class,
        o -> Optional.of(o).instanceOf(IDatabaseTriggerName.class).convert(n -> n.getName()).get());
    register(registry,
        IDatabaseIndexName.class,
        o -> Optional.of(o).instanceOf(IDatabaseIndexName.class).convert(n -> n.getName()).get());
    register(registry,
        IDatabaseSequenceName.class,
        o -> Optional.of(o).instanceOf(IDatabaseSequenceName.class).convert(n -> n.getName()).get());
    register(registry,
        IDatabaseConstraintName.class,
        o -> Optional.of(o).instanceOf(IDatabaseConstraintName.class).convert(n -> n.getName()).get());

    return new TransferHandler() {

      @Override
      public int getSourceActions(final JComponent c) {
        return COPY;
      }

      @Override
      protected Transferable createTransferable(final JComponent c) {
        if (c instanceof JTree) {
          final JTree tree = (JTree) c;
          final TreePath[] paths = tree.getSelectionPaths();
          if (paths.length == 1) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            final Object userObject = node.getUserObject();
            return Optional.of(registry.get(userObject.getClass()))
                .convert(f -> f.apply(userObject))
                .getOr(() -> {
                  return super.createTransferable(c);
                });
          }
        }
        return super.createTransferable(c);
      }

    };
  }

  private static <T> void register(final HierarchicalClassKeyRegistry<Function<Object, Transferable>> registry,
      final Class<T> clazz,
      final Function<Object, String> converter) {
    final String mimeType = MessageFormat.format(
        "{0};class=\"{1}\"", //$NON-NLS-1$
        DataFlavor.javaJVMLocalObjectMimeType,
        clazz.getName());
    final List<DataFlavor> dataFlavors = createSupportedDataFlavors(clazz, mimeType);
    registry.add(clazz,
        o -> Optional.of(o)
            .instanceOf(clazz)
            .convert(c -> new Transferable() {

              @Override
              public DataFlavor[] getTransferDataFlavors() {
                return dataFlavors.toArray(DataFlavor[]::new);
              }

              @Override
              public boolean isDataFlavorSupported(final DataFlavor flavor) {
                return dataFlavors.contains(flavor);
              }

              @Override
              public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (Objects.equals(DataFlavor.stringFlavor, flavor)) {
                  return Optional.of(converter.apply(c))
                      .get();
                }
                if (Objects.equals(DataFlavor.plainTextFlavor, flavor)) {
                  return Optional.of(converter.apply(c))
                      .get();
                }
                if (Objects.equals(DataFlavor.getTextPlainUnicodeFlavor(), flavor)) {
                  return Optional.of(converter.apply(c))
                      .convert(
                          string -> new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8"))))
                      .get();
                }
                if (flavor.isMimeTypeEqual(mimeType)) {
                  return c;
                }
                return null;
              }
            })
            .get());
  }

  protected static <T> List<DataFlavor> createSupportedDataFlavors(final Class<T> clazz, final String mimeType) {
    try {
      if (Serializable.class.isAssignableFrom(clazz)) {
        return List.of(
            new DataFlavor(mimeType),
            DataFlavor.stringFlavor,
            DataFlavor.getTextPlainUnicodeFlavor(),
            DataFlavor.plainTextFlavor);
      }
      return List
          .of(DataFlavor.stringFlavor, DataFlavor.getTextPlainUnicodeFlavor(), DataFlavor.plainTextFlavor);
    } catch (final ClassNotFoundException exception) {
      return List
          .of(DataFlavor.stringFlavor, DataFlavor.getTextPlainUnicodeFlavor(), DataFlavor.plainTextFlavor);
    }
  }

  private String toHtml(final String string) {
    if (StringUtilities.isNullOrTrimmedEmpty(string)) {
      return null;
    }
    final StringBuilder builder = new StringBuilder();
    builder.append("<html><body>");
    builder.append(string.replaceAll("\\n", "<br>"));
    builder.append("</html></body>");
    return builder.toString();
  }

  private String createTableSelectStatement(final IDatabaseTableName tableName) {
    return String.format("select * from %1$s", this.databaseFacade.quoted(tableName));
  }

  private void consumeObject(
      final TreePath treePath,
      final IApplicable<Object> applicable,
      final IBiConsumer<TreePath, Object, RuntimeException> consumer) {
    if (treePath == null) {
      consumer.consume(treePath, null);
      return;
    }
    final Object lastPathComponent = ((DefaultMutableTreeNode) treePath.getLastPathComponent())
        .getUserObject();
    if (applicable.isApplicable(lastPathComponent)) {
      consumer.consume(treePath, lastPathComponent);
    }
  }

  private IDatabaseTableName select(final TreePath path, final Class<IDatabaseTableName> clazz) {
    final Object[] objects = path.getPath();
    return Streams.of(objects)
        .instanceOf(DefaultMutableTreeNode.class)
        .convert(node -> node.getUserObject())
        .instanceOf(clazz)
        .first()
        .get();
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
      this.connectionModel.set(this.databaseConnector.connectWritable(this.description, true));
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

  private void insertEditorContent(
      final PlainDocument document,
      final String string,
      final int start,
      final int end) {
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
    final IFileChooserResult result = FileChoosers
        .show(
            GuiUtilities.getParentWindow(component),
            SqlConsoleContentPane.this.preferences.node(FileChoosers.DEFAULT_PREFERENCE_NODE),
            new OpenFileChooserConfiguration(filters, JFileChooser.FILES_ONLY, false));
    if (result.getReturnState() == JFileChooser.APPROVE_OPTION) {
      this.statusModel.set(SqlConsoleMessages.working);
      final File file = result.getSelectedFile();
      try (InputStream stream = new FileInputStream(file)) {
        final String content = IoUtilities.toString(stream, Charset.defaultCharset().name());
        this.statusModel.set(SqlConsoleMessages.done);
        return content;
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        this.statusModel.set(exception.getMessage());
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
    final IFileChooserResult result = FileChoosers
        .show(
            GuiUtilities.getParentWindow(component),
            SqlConsoleContentPane.this.preferences.node(FileChoosers.DEFAULT_PREFERENCE_NODE),
            new SaveFileChooserConfiguration(filters, JFileChooser.FILES_ONLY, false, false));
    if (result.getReturnState() == JFileChooser.APPROVE_OPTION) {
      SqlConsoleContentPane.this.statusModel.set(SqlConsoleMessages.working);
      final File file = result.getSelectedFile();
      try (OutputStream stream = new FileOutputStream(file)) {
        IoUtilities
            .pipe(new ByteArrayInputStream(string.getBytes(Charset.defaultCharset())), stream);
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
      this.statusModel.set(SqlConsoleMessages.working);
      final DatabaseMetaData metaData = this.databaseFacade.getMetaData(this.connectionModel.get());
      final IDatabaseTableName tableName = this.selectedTable.get();
      this.resultReseter.reset();
      final ResultSet result = metadataProvider.aggregate(metaData, tableName);
      if (result == null) {
        this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.resultReseter.reset();
      this.statusModel.set(exception.getMessage());
    }
  }

  private void getResultSet(
      final IAggregator<Connection, IDatabaseTableName, ResultSet, SQLException> resultSetProvider) {
    try {
      this.statusModel.set(SqlConsoleMessages.working);
      this.resultReseter.reset();
      final ResultSet result = resultSetProvider.aggregate(this.connectionModel.get(), this.selectedTable.get());
      if (result == null) {
        this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.resultReseter.reset();
      this.statusModel.set(exception.getMessage());
    }
  }

  private <T> void queryMetadata(final IFunction<Connection, ResultSet, SQLException> function) {
    try {
      this.statusModel.set(SqlConsoleMessages.working);
      final ResultSet result = function.execute(this.connectionModel.get());
      if (result == null) {
        this.resultReseter.reset();
        this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.resultReseter.reset();
      this.statementModel.set(result.getStatement());
      this.resultSetModel.set(result);
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.resultReseter.reset();
      this.statusModel.set(exception.getMessage());
    }
  }

  private void queryProperties(final IFunction<Connection, List<Property>, SQLException> function) {
    try {
      this.statusModel.set(SqlConsoleMessages.working);
      List<Property> properties = function.execute(this.connectionModel.get());
      if (properties == null) {
        this.resultReseter.reset();
        this.statusModel.set(SqlConsoleMessages.noResult);
        return;
      }
      this.resultReseter.reset();
      this.resultDataTableModel.wrap(new PropertyTableModel(properties));
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.resultReseter.reset();
      this.statusModel.set(exception.getMessage());
    }
  }

  private void queryDataBaseProperties(final ICanceler canceler) {
    queryProperties(connection -> {
      try {
        return this.databaseFacade.getClientProperties(canceler, connection);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void queryDataBaseCapabilities(final ICanceler canceler) {
    queryProperties(connection -> {
      try {
        return this.databaseFacade.getCapabilities(canceler, connection);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void queryDataBaseDatatypes(final ICanceler canceler) {
    queryMetadata(connection -> {
      try {
        return this.databaseFacade.getDataTypes(canceler, connection);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void queryTableMetadata(final ICanceler canceler, final IDatabaseTableName name) {
    queryMetadata(connection -> {
      try {
        return this.databaseFacade.getTableMetadata(canceler, connection, name);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void queryColumnMetadata(final ICanceler canceler, final IDatabaseColumnName name) {
    queryMetadata(connection -> {
      try {
        return this.databaseFacade.getTableColumnMetadata(canceler, connection, name);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void querySequenceMetadata(final ICanceler canceler, final IDatabaseSequenceName name) {
    queryMetadata(connection -> {
      try {
        return this.databaseFacade.getSequenceMetadata(canceler, connection, name);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void queryTriggerMetadata(final ICanceler canceler, final IDatabaseTriggerName name) {
    queryMetadata(connection -> {
      try {
        return this.databaseFacade.getTriggerMetadata(canceler, connection, name);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void queryIndexMetadata(final ICanceler canceler, final IDatabaseIndexName name) {
    queryMetadata(connection -> {
      try {
        return this.databaseFacade.getIndexMetadata(canceler, connection, name);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private void queryConstraintMetadata(final ICanceler canceler,
      final IDatabaseTableName table,
      final IDatabaseConstraintName constraint) {
    queryMetadata(connection -> {
      try {
        return this.databaseFacade.getConstraintMetadata(canceler, connection, table, constraint);
      } catch (CanceledException exception) {
        return null;
      }
    });
  }

  private String getCatalog() {
    try {
      return this.connectionModel.get().getCatalog();
    } catch (final AbstractMethodError | Exception exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return null;
    }
  }

  private <I> void run(
      final Component component,
      final String title,
      final String text,
      final IProgressTask<Void, RuntimeException> task) {
    try {
      new ProgressDialogLauncher<>(task)
          .setTitle(title)
          .setText(text)
          .setDescription("") //$NON-NLS-1$
          .launch(component);
    } catch (final CanceledException exception) {
      // // nothing to do
    }
  }

  private String getSelectedRowsAsString(
      final WrappedTableModel tableModel,
      final JTable resultTable) {
    final StringBuilder builder = new StringBuilder();
    boolean rowFlag = false;
    boolean columnFlag = false;

    for (int column = 0; column < tableModel.getColumnCount(); column++) {
      if (columnFlag) {
        builder.append(", "); //$NON-NLS-1$
      }
      builder.append("\""); //$NON-NLS-1$
      builder.append(tableModel.getColumnName(column));
      builder.append("\""); //$NON-NLS-1$
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
          builder.append(""); //$NON-NLS-1$
          continue;
        }
        final IObjectToStringConverter<Object> toStringConverter =
            tableModel.isWrapperFor(ResultSetTableModel.class)
                ? this.dataBaseTableCellValueToStringConverterProvider
                    .get(SqlConsoleContentPane.this.description,
                        tableModel
                            .unwrap(ResultSetTableModel.class)
                            .getColumnTypeName(column))
                : o -> Optional
                    .of(o)
                    .convert(v -> v.toString())
                    .get();
        if (value instanceof Number) {
          builder.append(value.toString());
          continue;
        }
        builder.append("\""); //$NON-NLS-1$
        if (toStringConverter != null) {
          builder.append(toStringConverter.toString(value));
        } else {
          builder.append(value.toString());
        }
        builder.append("\""); //$NON-NLS-1$
      }
      rowFlag = true;
      columnFlag = false;
    }
    return builder.toString();
  }
}
