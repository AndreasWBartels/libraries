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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutor;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.IResultProducer;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.action.ActionProcedurBuilder;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.action.IActionProcedure;
import net.anwiba.commons.swing.dialog.ConfigurableDialogLauncher;
import net.anwiba.commons.swing.dialog.DialogResult;
import net.anwiba.commons.swing.dialog.IDialogResult;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;
import net.anwiba.commons.swing.dialog.progress.ProgressDialogLauncher;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.table.ObjectTableBuilder;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.LicenseListResultResponse;
import net.anwiba.spatial.ckan.marshaller.CkanJsonResponseUnmarshallerFactory;
import net.anwiba.spatial.ckan.request.LicenseRequestBuilder;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public final class LicenseTableFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(LicenseTableFactory.class);
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;

  public LicenseTableFactory(final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
  }

  public ObjectListTable<License> create(
      final IPreferences preferences,
      final IHttpConnectionDescription description,
      final List<License> formatStrings,
      final int columnWitdh) {
    final List<License> licenses = new ArrayList<>();
    return new ObjectTableBuilder<License>()
        .setValues(formatStrings)
        .addSortableStringColumn(Messages.name, value -> CkanUtilities.toString(value), columnWitdh)
        .addActionFactory(
            (tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icons.GuiIcons.EDIT_ICON)
                .setProcedure(createSelectTagActionProcedure(description, preferences, licenses, tableModel))
                .build())
        .addRemoveObjectsAction()
        .addActionFactory((tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> {
          final IBooleanModel enabledModel = new BooleanModel(!tableModel.isEmpty());
          tableModel.addTableModelListener(e -> enabledModel.set(!tableModel.isEmpty()));
          return new ConfigurableActionBuilder()
              .setIcon(net.anwiba.commons.swing.icons.GuiIcons.EDIT_CLEAR_LIST)
              .setEnabledDistributor(enabledModel)
              .setProcedure(component -> tableModel.removeAll())
              .build();
        })
        .build();
  }

  private IActionProcedure createSelectTagActionProcedure(
      final IHttpConnectionDescription description,
      final IPreferences preferences,
      final List<License> licenses,
      final IObjectTableModel<License> tableModel) {
    return new ActionProcedurBuilder<List<License>, List<License>>().setInitializer(parentComponent -> {
      try {
        return new ProgressDialogLauncher<>((progressMonitor, canceler) -> {
          if (licenses.isEmpty()) {
            getValues(canceler, description)
                .stream()
                .filter(s1 -> s1 != null)
                .sorted((o1, o2) -> CkanUtilities.toString(o1).compareTo(CkanUtilities.toString(o2)))
                .forEachOrdered(s2 -> licenses.add(s2));
          }
          return licenses;
        }).launch(parentComponent);
      } catch (final InterruptedException exception1) {
        return null;
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        new MessageDialogLauncher()
            .error()
            .title(Messages.licenses)
            .text(Messages.license_query_faild)
            .description(exception.getMessage())
            .throwable(exception)
            .launch(parentComponent);
        return null;
      }
    }).setConsumer((parentComponent, value) -> {
      if (value == null) {
        return;
      }
      final List<License> selections = new ArrayList<>();
      final IDialogResult result = new ConfigurableDialogLauncher()
          .setPreferences(preferences.node("license")) //$NON-NLS-1$
          .setTitle(Messages.licenses)
          .enableCloseOnEscape()
          .setContentPaneFactory(
              (o, p) -> new SelectTableContentPane<>(selections, tableModel, value, v -> CkanUtilities.toString(v)))
          .setCancleOkButtonDialog()
          .launch(parentComponent);
      if (result != DialogResult.OK) {
        return;
      }
      tableModel.set(selections);
    }).build();
  }

  private List<License> getValues(final ICanceler canceler, final IHttpConnectionDescription description)
      throws InterruptedException,
      IOException {
    final IResultProducer<LicenseListResultResponse> responseProducer = (
        c,
        statusCode,
        statusMessage,
        contentType,
        contentEncoding,
        inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(LicenseListResultResponse.class).unmarshal(
            inputStream);

    final IRequest request = LicenseRequestBuilder
        .list(description.getUrl())
        .authentication(description.getUserName(), description.getPassword())
        .build();

    try (final IObjectRequestExecutor<LicenseListResultResponse> executor = this.requestExecutorBuilderFactory
        .<LicenseListResultResponse> create()
        .setResultProducer(responseProducer)
        .addResultProducer(
            (statusCode, contentType) -> new HashSet<>(Arrays.asList(409, 400, 500)).contains(statusCode)
                && contentType != null
                && contentType.startsWith("application/json"), //$NON-NLS-1$
            responseProducer)
        .build()) {
      final LicenseListResultResponse response = executor.execute(canceler, request);
      return response.isSuccess() ? Arrays.asList(response.getResult()) : Collections.emptyList();
    } catch (SocketException | InterruptedIOException exception) {
      if (canceler.isCanceled()) {
        throw new InterruptedException();
      }
      throw exception;
    }
  }
}
