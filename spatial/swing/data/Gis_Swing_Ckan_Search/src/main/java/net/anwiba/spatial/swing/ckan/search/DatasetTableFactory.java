/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.spatial.swing.ckan.search;

import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.lang.collection.IObjectIterable;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.message.MessageBuilder;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.IntegerModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.combobox.ObjectComboBoxModel;
import net.anwiba.commons.swing.dialog.ConfigurableDialogLauncher;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.DialogResult;
import net.anwiba.commons.swing.dialog.IContentPaneFactory;
import net.anwiba.commons.swing.dialog.IDialogResult;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;
import net.anwiba.commons.swing.table.IColumnValueProvider;
import net.anwiba.commons.swing.table.IKeyListenerFactory;
import net.anwiba.commons.swing.table.IMouseListenerFactory;
import net.anwiba.commons.swing.table.IObjectListColumnConfiguration;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;
import net.anwiba.commons.swing.table.ObjectListColumnConfiguration;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.table.ObjectTableBuilder;
import net.anwiba.commons.swing.table.renderer.BooleanRenderer;
import net.anwiba.commons.swing.table.renderer.ObjectUiTableCellRenderer;
import net.anwiba.commons.swing.ui.IObjectUi;
import net.anwiba.commons.swing.ui.ObjectUiBuilder;
import net.anwiba.commons.swing.ui.ObjectUiListCellRenderer;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.ckan.request.sort.ISortOrder;
import net.anwiba.spatial.ckan.request.sort.ISortOrderTerm;
import net.anwiba.spatial.ckan.request.sort.ISortOrderVisitor;
import net.anwiba.spatial.ckan.request.sort.Order;
import net.anwiba.spatial.ckan.request.sort.SortOrderList;
import net.anwiba.spatial.ckan.request.sort.SortOrderTerm;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public class DatasetTableFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(DatasetTableFactory.class);
  private final int numberOfResultRows;

  public DatasetTableFactory(final int numberOfResultRows) {
    this.numberOfResultRows = numberOfResultRows;
  }

  ObjectListTable<Dataset> create(
      final IHttpConnectionDescription description,
      final IntegerModel offsetModel,
      final IntegerModel resultCountModel,
      final IntegerModel selectedIndexModel,
      final IObjectModel<ISortOrder> sortOrderModel,
      final List<Dataset> datasets) {
    return new ObjectTableBuilder<Dataset>()
        .setSingleSelectionMode()
        .setValues(datasets)
        .setHeaderMouseListenerFactory(new IMouseListenerFactory<Dataset>() {

          @Override
          public MouseListener create(
              final IObjectTableModel<Dataset> tableModel,
              final ISelectionIndexModel<Dataset> selectionIndexModel,
              final ISelectionModel<Dataset> selectionModel,
              final IBooleanDistributor sortStateModel) {
            return new MouseAdapter() {

              @Override
              public void mouseClicked(final MouseEvent e) {
                final List<ObjectPair<Boolean, ISortOrderTerm>> sortOrderTerms = getSortOrderTerms(sortOrderModel);
                final ObjectListTable<ObjectPair<Boolean, ISortOrderTerm>> table = new ObjectTableBuilder<ObjectPair<Boolean, ISortOrderTerm>>()
                    .setValues(sortOrderTerms)
                    .setPreferredVisibleRowCount(3)
                    .addColumn(createEnabledColumn())
                    .addColumn(createAspectColumn())
                    .addColumn(createOrderTypeColumn())
                    .addMoveObjectDownAction()
                    .addMoveObjectUpAction()
                    .build();
                final IDialogResult result = new ConfigurableDialogLauncher()
                    .setCancleOkButtonDialog()
                    .enableCloseOnEscape()
                    .setTitle("Datasets")
                    .setMessage(
                        new MessageBuilder().setText("Sort order").setDescription("Please, set sort order").build())
                    .setContentPaneFactory(new IContentPaneFactory() {

                      @Override
                      public IContentPanel create(final Window owner, final IPreferences preferences) {
                        return new AbstractContentPane() {

                          @Override
                          public JComponent getComponent() {
                            table.getTableModel().addTableModelListener(new TableModelListener() {

                              @Override
                              public void tableChanged(final TableModelEvent e) {
                                getDataStateModel().set(DataState.MODIFIED);
                              }
                            });
                            table.getTableModel().addListModelListener(
                                new IChangeableListListener<ObjectPair<Boolean, ISortOrderTerm>>() {
                                  @Override
                                  public void objectsChanged(
                                      final Iterable<ObjectPair<Boolean, ISortOrderTerm>> oldObjects,
                                      final Iterable<ObjectPair<Boolean, ISortOrderTerm>> newObjects) {
                                    getDataStateModel().set(DataState.MODIFIED);
                                  }
                                });
                            return table.getComponent();
                          }
                        };
                      }
                    })
                    .launch(e.getComponent());
                if (!DialogResult.OK.equals(result)) {
                  return;
                }
                sortOrderModel.set(create(table.getTableModel().values()));
              }

              private IObjectListColumnConfiguration<ObjectPair<Boolean, ISortOrderTerm>> createOrderTypeColumn() {
                final ObjectComboBoxModel<Order> comboBoxComponentModel = new ObjectComboBoxModel<>(
                    Arrays.asList(Order.values()));
                final JComboBox<Order> comboBox = new JComboBox<>(comboBoxComponentModel);
                final IObjectUi<Order> objectUi = new ObjectUiBuilder<Order>().icon(o -> {
                  switch (o) {
                    case asc: {
                      return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.PAN_UP
                          .getSmallIcon();
                    }
                    case desc: {
                      return net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.PAN_DOWN
                          .getSmallIcon();
                    }
                  }
                  return net.anwiba.commons.swing.icons.GuiIcons.EMPTY_ICON.getSmallIcon();
                }).text(o -> null).build();
                comboBox.setRenderer(new ObjectUiListCellRenderer<>(objectUi));
                final DefaultCellEditor cellEditor = new DefaultCellEditor(comboBox);
                cellEditor.setClickCountToStart(2);
                return new ObjectListColumnConfiguration<>(
                    "Order",
                    (IColumnValueProvider<ObjectPair<Boolean, ISortOrderTerm>>) object -> {
                      if (object == null) {
                        return null;
                      }
                      return object.getSecondObject().getOrder();
                    },
                    new ObjectUiTableCellRenderer<>(objectUi, Order.class, SwingConstants.CENTER),
                    (object, value) -> {
                      if (object == null) {
                        return object;
                      }
                      return new ObjectPair<>(
                          object.getFirstObject(),
                          new SortOrderTerm((Order) value, object.getSecondObject().getAspect()));
                    },
                    cellEditor,
                    30,
                    false,
                    null);
              }

              private IObjectListColumnConfiguration<ObjectPair<Boolean, ISortOrderTerm>> createAspectColumn() {
                final IObjectUi<String> objectUi = new ObjectUiBuilder<String>().text(o -> {
                  if ("metadata_created".equals(o)) {
                    return "created";
                  }
                  if ("metadata_modified".equals(o)) {
                    return "modified";
                  }
                  return o;
                }).build();
                return new ObjectListColumnConfiguration<>(
                    "Aspect",
                    (IColumnValueProvider<ObjectPair<Boolean, ISortOrderTerm>>) object -> {
                      if (object == null) {
                        return null;
                      }
                      return object.getSecondObject().getAspect();
                    },
                    new ObjectUiTableCellRenderer<>(objectUi, String.class, SwingConstants.LEFT),
                    100,
                    String.class,
                    false,
                    null);
              }

              private IObjectListColumnConfiguration<ObjectPair<Boolean, ISortOrderTerm>> createEnabledColumn() {
                final JCheckBox checkBox = new JCheckBox();
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                final DefaultCellEditor cellEditor = new DefaultCellEditor(checkBox);
                return new ObjectListColumnConfiguration<>(
                    "Enabled",
                    (IColumnValueProvider<ObjectPair<Boolean, ISortOrderTerm>>) object -> {
                      if (object == null) {
                        return null;
                      }
                      return object.getFirstObject();
                    },
                    new BooleanRenderer(),
                    (object, value) -> {
                      if (object == null) {
                        return object;
                      }
                      return new ObjectPair<>(
                          (Boolean) value,
                          new SortOrderTerm(object.getSecondObject().getOrder(), object.getSecondObject().getAspect()));
                    },
                    cellEditor,
                    30,
                    false,
                    null);
              }

              private ISortOrder create(final IObjectIterable<ObjectPair<Boolean, ISortOrderTerm>> values) {
                final Collection<ISortOrderTerm> collection = Streams
                    .of(values)
                    .filter(v -> v.getFirstObject())
                    .convert(v -> v.getSecondObject())
                    .asCollection();
                if (collection.size() == 1) {
                  return collection.iterator().next();
                }
                return new SortOrderList(collection);
              }

              private List<ObjectPair<Boolean, ISortOrderTerm>> getSortOrderTerms(
                  @SuppressWarnings("hiding") final IObjectModel<ISortOrder> sortOrderModel) {
                final ISortOrderVisitor<List<ISortOrderTerm>, RuntimeException> visitor = new ISortOrderVisitor<List<ISortOrderTerm>, RuntimeException>() {

                  @Override
                  public List<ISortOrderTerm> visitList(final Iterable<ISortOrderTerm> terms) {
                    return IterableUtilities.asList(terms);
                  }

                  @Override
                  public List<ISortOrderTerm> visitTerm(final ISortOrderTerm term) {
                    return Arrays.asList(term);
                  }
                };
                final List<ISortOrderTerm> terms = sortOrderModel.get().accept(visitor);
                ISortOrderTerm relevanceSortOrderTerm = null;
                ISortOrderTerm metadataCreatedSortOrderTerm = null;
                ISortOrderTerm metadataModifiedSortOrderTerm = null;
                final List<ObjectPair<Boolean, ISortOrderTerm>> enabledStateTermList = new ArrayList<>();
                for (final ISortOrderTerm term : terms) {
                  if ("relevance".equals(term.getAspect())) {
                    relevanceSortOrderTerm = term;
                    enabledStateTermList.add(new ObjectPair<>(Boolean.TRUE, term));
                  }
                  if ("metadata_created".equals(term.getAspect())) {
                    metadataCreatedSortOrderTerm = term;
                    enabledStateTermList.add(new ObjectPair<>(Boolean.TRUE, term));
                  }
                  if ("metadata_modified".equals(term.getAspect())) {
                    metadataModifiedSortOrderTerm = term;
                    enabledStateTermList.add(new ObjectPair<>(Boolean.TRUE, term));
                  }
                }
                if (relevanceSortOrderTerm == null) {
                  enabledStateTermList.add(
                      new ObjectPair<Boolean, ISortOrderTerm>(
                          Boolean.FALSE,
                          new SortOrderTerm(Order.asc, "relevance")));
                }
                if (metadataCreatedSortOrderTerm == null) {
                  enabledStateTermList.add(
                      new ObjectPair<Boolean, ISortOrderTerm>(
                          Boolean.FALSE,
                          new SortOrderTerm(Order.desc, "metadata_created")));
                }
                if (metadataModifiedSortOrderTerm == null) {
                  enabledStateTermList.add(
                      new ObjectPair<Boolean, ISortOrderTerm>(
                          Boolean.FALSE,
                          new SortOrderTerm(Order.desc, "metadata_modified")));
                }
                return enabledStateTermList;
              }
            };
          }
        })
        .addStringColumn(Messages.title, value -> value == null ? null : CkanUtilities.toString(value), 40)
        .addActionFactory((tableModel, selectionIndexModel, selectionModel, sortStateModel) -> {
          final BooleanModel enabledModel = new BooleanModel(false);
          tableModel.addTableModelListener(e -> enabledModel.set(offsetModel.getValue() > 0));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GO_FIRST)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> {
                if (selectionIndexModel.isEmpty()) {
                  selectedIndexModel.setValue(-1);
                } else {
                  selectedIndexModel.setValue(selectionIndexModel.getMinimum());
                }
                offsetModel.setValue(0);
              })
              .build();
        })
        .addActionFactory((tableModel, selectionIndexModel, selectionModel, sortStateModel) -> {
          final BooleanModel enabledModel = new BooleanModel(false);
          tableModel.addTableModelListener(e -> enabledModel.set(offsetModel.getValue() > 0));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GO_PREVIOUS)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> {
                if (selectionIndexModel.isEmpty()) {
                  selectedIndexModel.setValue(-1);
                } else {
                  selectedIndexModel.setValue(selectionIndexModel.getMinimum());
                }
                offsetModel.setValue(
                    offsetModel.getValue() > this.numberOfResultRows
                        ? offsetModel.getValue() - this.numberOfResultRows
                        : 0);
              })
              .build();
        })
        .addActionFactory((tableModel, selectionIndexModel, selectionModel, sortStateModel) -> {
          final BooleanModel enabledModel = new BooleanModel(
              offsetModel.getValue() + tableModel.size() < resultCountModel.getValue());
          tableModel.addTableModelListener(
              e -> enabledModel.set(offsetModel.getValue() + tableModel.size() < resultCountModel.getValue()));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GO_NEXT)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> {
                if (selectionIndexModel.isEmpty()) {
                  selectedIndexModel.setValue(-1);
                } else {
                  selectedIndexModel.setValue(selectionIndexModel.getMinimum());
                }
                offsetModel.setValue(
                    offsetModel.getValue() + this.numberOfResultRows > resultCountModel.getValue()
                        ? resultCountModel.getValue() - this.numberOfResultRows
                        : offsetModel.getValue() + this.numberOfResultRows);
              })
              .build();
        })
        .addActionFactory((tableModel, selectionIndexModel, selectionModel, sortStateModel) -> {
          final BooleanModel enabledModel = new BooleanModel(
              offsetModel.getValue() + tableModel.size() < resultCountModel.getValue());
          tableModel.addTableModelListener(
              e -> enabledModel.set(offsetModel.getValue() + tableModel.size() < resultCountModel.getValue()));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.GO_LAST)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> {
                if (selectionIndexModel.isEmpty()) {
                  selectedIndexModel.setValue(-1);
                } else {
                  selectedIndexModel.setValue(selectionIndexModel.getMinimum());
                }
                offsetModel.setValue(resultCountModel.getValue() - this.numberOfResultRows);
              })
              .build();
        })
        .addActionFactory((tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> {
          final BooleanModel enabledModel = new BooleanModel(false);
          selectionModel
              .addSelectionListener(e -> enabledModel.set(!e.getSource().isEmpty() && Desktop.isDesktopSupported()));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.NETWORK_TRANSMIT)
              .setTooltip(Messages.ckan_json_respone)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> {
                final Desktop desktop = Desktop.getDesktop();
                final Dataset dataset = selectionModel.getSelectedObjects().iterator().next();
                final String urlString = description.getUrl() + "/3/action/package_show?id=" + dataset.getId(); //$NON-NLS-1$
                try {
                  final URI uri = new URI(urlString);
                  desktop.browse(uri);
                } catch (URISyntaxException | IOException exception) {
                  logger.log(ILevel.ERROR, exception.getMessage(), exception);
                }
              })
              .build();
        })
        .addActionFactory((tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> {
          final BooleanModel enabledModel = new BooleanModel(false);
          selectionModel.addSelectionListener(
              e -> enabledModel.set(
                  Desktop.isDesktopSupported()
                      && !e.getSource().isEmpty()
                      && !StringUtilities
                          .isNullOrEmpty(e.getSource().getSelectedObjects().iterator().next().getUrl())));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.WEB_BROWSER)
              .setTooltip(Messages.browse_dataset)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> {
                final Desktop desktop = Desktop.getDesktop();
                final Dataset dataset = selectionModel.getSelectedObjects().iterator().next();
                try {
                  final String url = dataset.getUrl().trim();
                  final URI uri = new URI(url);
                  desktop.browse(uri);
                } catch (URISyntaxException | IOException exception) {
                  logger.log(ILevel.ERROR, exception.getMessage(), exception);
                }
              })
              .build();
        })
        .setKeyListenerFactory(new IKeyListenerFactory<Dataset>() {

          @Override
          public KeyListener create(
              final IObjectTableModel<Dataset> tableModel,
              final ISelectionIndexModel<Dataset> selectionIndexModel,
              final ISelectionModel<Dataset> selectionModel,
              final IBooleanDistributor sortStateModel) {

            return new KeyAdapter() {

              @Override
              public void keyPressed(final KeyEvent event) {
                final int keyCode = event.getKeyCode();
                switch (keyCode) {
                  case KeyEvent.VK_PAGE_UP: {
                    event.consume();
                    if (tableModel.isEmpty()) {
                      selectedIndexModel.setValue(-1);
                    } else {
                      selectedIndexModel.setValue(tableModel.size() - 1);
                    }
                    offsetModel.setValue(
                        offsetModel.getValue() > DatasetTableFactory.this.numberOfResultRows
                            ? offsetModel.getValue() - DatasetTableFactory.this.numberOfResultRows
                            : 0);
                    return;
                  }
                  case KeyEvent.VK_PAGE_DOWN: {
                    event.consume();
                    if (!(offsetModel.getValue() + tableModel.size() < resultCountModel.getValue())) {
                      return;
                    }
                    if (tableModel.isEmpty()) {
                      selectedIndexModel.setValue(-1);
                    } else {
                      selectedIndexModel.setValue(0);
                    }
                    offsetModel.setValue(
                        offsetModel.getValue() + DatasetTableFactory.this.numberOfResultRows > resultCountModel
                            .getValue()
                                ? resultCountModel.getValue() - DatasetTableFactory.this.numberOfResultRows
                                : offsetModel.getValue() + DatasetTableFactory.this.numberOfResultRows);
                    return;
                  }
                }
              }
            };
          }
        })
        .build();
  }

}
