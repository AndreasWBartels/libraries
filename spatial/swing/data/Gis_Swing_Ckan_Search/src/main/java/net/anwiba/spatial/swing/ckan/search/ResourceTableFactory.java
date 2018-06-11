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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.table.ObjectTableBuilder;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.ckan.json.schema.v1_0.Resource;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public class ResourceTableFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ResourceTableFactory.class);
  private final IResourceOpenConsumer resourceOpenconsumer;

  public ResourceTableFactory(final IResourceOpenConsumer resourceOpenconsumer) {
    this.resourceOpenconsumer = resourceOpenconsumer;
  }

  public ObjectListTable<Resource> create(
      final IHttpConnectionDescription description,
      final IObjectModel<Dataset> datasetModel) {
    return new ObjectTableBuilder<Resource>()
        .setSingleSelectionMode()
        .setValues(
            Optional.of(datasetModel.get()).convert(d -> d.getResources()).convert(a -> Arrays.asList(a)).getOr(
                () -> new ArrayList<>()))
        .addSortableStringColumn(Messages.title, new IFunction<Resource, String, RuntimeException>() {
          @Override
          public String execute(final Resource value) throws RuntimeException {
            return value == null ? null : CkanUtilities.toString(value);
          }
        }, 350)
        .addSortableStringColumn(Messages.format, new IFunction<Resource, String, RuntimeException>() {
          @Override
          public String execute(final Resource value) throws RuntimeException {
            return value == null ? null : value.getFormat();
          }
        }, 50)
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
                final Resource resource = selectionModel.getSelectedObjects().iterator().next();
                final String urlString = description.getUrl() + "/3/action/resource_show?id=" + resource.getId(); //$NON-NLS-1$
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
                  !e.getSource().isEmpty()
                      && this.resourceOpenconsumer
                          .isApplicable(selectionModel.getSelectedObjects().iterator().next())));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icon.GuiIcons.OPEN_ICON)
              .setEnabledDistributor(enabledModel)
              .setProcedure(
                  c -> this.resourceOpenconsumer
                      .open(description, datasetModel.get(), selectionModel.getSelectedObjects().iterator().next()))
              .build();
        })
        .addActionFactory((tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> {
          final BooleanModel enabledModel = new BooleanModel(false);
          selectionModel.addSelectionListener(
              e -> enabledModel.set(
                  Desktop.isDesktopSupported()
                      && !e.getSource().isEmpty()
                      && isHttpResource(selectionModel.getSelectedObjects().iterator().next())));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.WEB_BROWSER)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> {
                final Desktop desktop = Desktop.getDesktop();
                final Resource resource = selectionModel.getSelectedObjects().iterator().next();
                try {
                  final URI uri = new URI(resource.getUrl());
                  desktop.browse(uri);
                } catch (URISyntaxException | IOException exception) {
                  logger.log(ILevel.ERROR, exception.getMessage(), exception);
                }
              })
              .build();
        })
        .build();
  }

  private boolean isHttpResource(final Resource resource) {
    final String url = resource.getUrl();
    return Optional
        .of(url)
        .convert(u -> u.toLowerCase())
        .accept(u -> u.startsWith("http:") || u.startsWith("https:")) //$NON-NLS-1$//$NON-NLS-2$
        .isAccepted();
  }
}
