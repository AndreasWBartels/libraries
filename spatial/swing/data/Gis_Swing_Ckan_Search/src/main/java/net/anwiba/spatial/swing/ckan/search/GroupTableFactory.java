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
import java.util.List;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutor;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.IResultProducer;
import net.anwiba.commons.logging.ILevel;
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
import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.GroupListResultResponse;
import net.anwiba.spatial.ckan.marshaller.CkanJsonResponseUnmarshallerFactory;
import net.anwiba.spatial.ckan.request.GroupRequestBuilder;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public class GroupTableFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(GroupTableFactory.class);
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;

  public GroupTableFactory(final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
  }

  public ObjectListTable<Group> create(
      final IPreferences preferences,
      final IHttpConnectionDescription description,
      final List<Group> formatStrings,
      final int columnWidth) {

    final List<Group> groups = new ArrayList<>();

    return new ObjectTableBuilder<Group>()
        .setValues(formatStrings)
        .addSortableStringColumn(Messages.name, value -> CkanUtilities.toString(value), columnWidth)
        .addActionFactory(
            (tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icon.GuiIcons.EDIT_ICON)
                .setProcedure(createSelectGroupActionProcedure(description, preferences, groups, tableModel))
                .build())
        .addRemoveObjectsAction()
        .build();
  }

  private IActionProcedure createSelectGroupActionProcedure(
      final IHttpConnectionDescription description,
      final IPreferences preferences,
      final List<Group> groups,
      final IObjectTableModel<Group> tableModel) {
    return new ActionProcedurBuilder<List<Group>, List<Group>>().setInitializer(parentComponent -> {
      try {
        return new ProgressDialogLauncher<>((progressMonitor, canceler) -> {
          if (groups.isEmpty()) {
            getGroupStrings(canceler, description)
                .stream()
                .filter(s1 -> s1 != null)
                .sorted((o1, o2) -> CkanUtilities.toString(o1).compareTo(CkanUtilities.toString(o2)))
                .forEachOrdered(s2 -> groups.add(s2));
          }
          return groups;
        }).launch(parentComponent);
      } catch (final InterruptedException exception1) {
        return null;
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        new MessageDialogLauncher()
            .error()
            .title(Messages.categories)
            .text(Messages.group_query_faild)
            .description(exception.getMessage())
            .throwable(exception)
            .launch(parentComponent);
        return null;
      }
    }).setConsumer((parentComponent, value) -> {
      if (value == null) {
        return;
      }
      final List<Group> selections = new ArrayList<>();
      final IDialogResult result = new ConfigurableDialogLauncher()
          .setPreferences(preferences.node("group")) //$NON-NLS-1$
          .setTitle(Messages.categories)
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

  private List<Group> getGroupStrings(final ICanceler canceler, final IHttpConnectionDescription description)
      throws InterruptedException,
      IOException {
    final IResultProducer<GroupListResultResponse> responseProducer = (
        c,
        statusCode,
        statusMessage,
        contentType,
        contentEncoding,
        inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(GroupListResultResponse.class).unmarshal(
            inputStream);

    final IRequest request = GroupRequestBuilder
        .list(description.getUrl())
        .setAllFields()
        .authentication(description.getUserName(), description.getPassword())
        .build();

    try (final IObjectRequestExecutor<GroupListResultResponse> executor = this.requestExecutorBuilderFactory
        .<GroupListResultResponse> create()
        .setResultProducer(responseProducer)
        .build()) {
      final GroupListResultResponse response = executor.execute(canceler, request);
      return response.isSuccess() ? Arrays.asList(response.getResult()) : Collections.emptyList();
    } catch (SocketException | InterruptedIOException exception) {
      if (canceler.isCanceled()) {
        throw new InterruptedException();
      }
      throw exception;
    }
  }
}
