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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.IntegerModel;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.table.IKeyListenerFactory;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.table.ObjectTableBuilder;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
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
      final List<Dataset> datasets) {
    return new ObjectTableBuilder<Dataset>()
        .setSingleSelectionMode()
        .setValues(datasets)
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
