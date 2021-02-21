/*
 * #%L
 *
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
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.CreationException;
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
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.OrganizationListResultResponse;
import net.anwiba.spatial.ckan.marshaller.CkanJsonResponseUnmarshallerFactory;
import net.anwiba.spatial.ckan.request.OrganizationRequestBuilder;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public class OrganizationTableFactory {

  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(OrganizationTableFactory.class);
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;

  public OrganizationTableFactory(final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
  }

  public ObjectListTable<Organization> create(
      final IPreferences preferences,
      final IHttpConnectionDescription description,
      final List<Organization> formatStrings) {

    final List<Organization> organizations = new ArrayList<>();
    return new ObjectTableBuilder<Organization>()
        .setValues(formatStrings)
        .addSortableStringColumn(Messages.name, value -> CkanUtilities.toString(value), 10)
        .addActionFactory(
            (tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icons.GuiIcons.EDIT_ICON)
                .setProcedure(createSelectActionProcedure(description, preferences, organizations, tableModel))
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

  private IActionProcedure createSelectActionProcedure(
      final IHttpConnectionDescription description,
      final IPreferences preferences,
      final List<Organization> organizations,
      final IObjectTableModel<Organization> tableModel) {
    return new ActionProcedurBuilder<List<Organization>, List<Organization>>().setInitializer(parentComponent -> {
      try {
        return new ProgressDialogLauncher<>((progressMonitor, canceler) -> {
          if (organizations.isEmpty()) {
            getOrganizations(canceler, description)
                .stream()
                .filter(s1 -> s1 != null)
                .sorted((o1, o2) -> CkanUtilities.toString(o1).compareTo(CkanUtilities.toString(o2)))
                .forEachOrdered(s2 -> organizations.add(s2));
          }
          return organizations;
        }).launch(parentComponent);
      } catch (final CanceledException exception1) {
        return null;
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        new MessageDialogLauncher()
            .error()
            .title(Messages.organizations)
            .text(Messages.organization_query_faild)
            .description(exception.getMessage())
            .throwable(exception)
            .launch(parentComponent);
        return null;
      }
    }).setConsumer((parentComponent, value) -> {
      if (value == null) {
        return;
      }
      final List<Organization> selections = new ArrayList<>();
      final IDialogResult result = new ConfigurableDialogLauncher()
          .setPreferences(preferences.node("organization")) //$NON-NLS-1$
          .setTitle(Messages.organizations)
          .enableCloseOnEscape()
          .setContentPaneFactory(
              (
                  o,
                  p,
                  d) -> new SelectTableContentPane<>(d, selections, tableModel, value, v -> CkanUtilities.toString(v)))
          .setCancleOkButtonDialog()
          .launch(parentComponent);
      if (result != DialogResult.OK) {
        return;
      }
      tableModel.set(selections);
    }).build();
  }

  private List<Organization> getOrganizations(final ICanceler canceler, final IHttpConnectionDescription description)
      throws CanceledException,
      IOException {
    final IResultProducer<OrganizationListResultResponse> responseProducer = (
        c,
        statusCode,
        statusMessage,
        contentType,
        contentEncoding,
        inputStream) -> new CkanJsonResponseUnmarshallerFactory()
            .create(OrganizationListResultResponse.class)
            .unmarshal(inputStream);
    try {
      final IRequest request = OrganizationRequestBuilder
          .list(description.getUrl())
          .setAllFields()
          .setExtraFields()
          .authentication(description.getUserName(), description.getPassword())
          .build();

      try (final IObjectRequestExecutor<OrganizationListResultResponse> executor = this.requestExecutorBuilderFactory
          .<OrganizationListResultResponse>create()
          .setResultProducer(responseProducer)
          .addResultProducer(
              (statusCode, contentType) -> new HashSet<>(Arrays.asList(409, 400, 500)).contains(statusCode)
                  && contentType != null && contentType.startsWith("application/json"), //$NON-NLS-1$
              responseProducer)
          .build()) {
        final OrganizationListResultResponse response = executor.execute(canceler, request);
        return response.isSuccess()
            ? Arrays.asList(response.getResult())
            : Collections.emptyList();
      } catch (SocketException | InterruptedIOException exception) {
        if (canceler.isCanceled()) {
          throw new CanceledException();
        }
        throw exception;
      }
    } catch (CreationException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }
}
