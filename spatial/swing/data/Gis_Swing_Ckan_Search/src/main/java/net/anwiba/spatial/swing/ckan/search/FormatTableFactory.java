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

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectModel;
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
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.progress.IProgressMonitor;
import net.anwiba.commons.thread.progress.IProgressTask;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.StringListResultResponse;
import net.anwiba.spatial.ckan.marshaller.CkanJsonResponseUnmarshallerFactory;
import net.anwiba.spatial.ckan.request.ResourceFormatRequestBuilder;
import net.anwiba.spatial.ckan.request.ResourceFormatRequestBuilder.ResourceFormatListRequestBuilder;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public class FormatTableFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(FormatTableFactory.class);
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;

  public FormatTableFactory(final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
  }

  public ObjectListTable<String> create(
      final IPreferences preferences,
      final IHttpConnectionDescription description,
      final List<String> formatStrings) {

    final List<String> formats = new ArrayList<>();

    return new ObjectTableBuilder<String>()
        .setValues(formatStrings)
        .addSortableStringColumn(Messages.name, value -> value, 10)
        .addTextFieldActionFactory(
            (tableModel, selectionIndicesProvider, selectionModel, sortStateModel, model, clearblock) -> {
              final IBooleanModel enabledModel = new BooleanModel(false);
              model.addChangeListener(() -> enabledModel.set(!StringUtilities.isNullOrTrimmedEmpty(model.get())));
              return new ConfigurableActionBuilder()
                  .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.ADD)
                  .setEnabledDistributor(enabledModel)
                  .setProcedure(component -> {
                    final String value = model.get();
                    clearblock.execute();
                    Streams.of(tableModel.values()).first(v -> value.equalsIgnoreCase(v)).or(() -> {
                      tableModel.add(value);
                    });
                  })
                  .build();
            })
        .addTextFieldActionFactory(
            (tableModel, selectionIndicesProvider, selectionModel, sortStateModel, model, clearblock) -> {
              final IBooleanModel enabledModel = new BooleanModel(false);
              model.addChangeListener(() -> enabledModel.set(!StringUtilities.isNullOrTrimmedEmpty(model.get())));
              return new ConfigurableActionBuilder()
                  .setIcon(net.anwiba.commons.swing.icon.GuiIcons.ADVANCED_SEARCH_ICON)
                  .setEnabledDistributor(enabledModel)
                  .setProcedure(
                      createSearchFormatActionProcedure(
                          description,
                          preferences,
                          formats,
                          tableModel,
                          clearblock,
                          model))
                  .build();
            })
        .setTextFieldKeyListenerFactory(
            (tableModel, selectionIndicesProvider, selectionModel, model, clearBlock) -> new KeyAdapter() {

              @Override
              public void keyTyped(final KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                  final String value = model.get();
                  if (StringUtilities.isNullOrTrimmedEmpty(value)) {
                    return;
                  }
                  clearBlock.execute();
                  Streams.of(tableModel.values()).first(v -> value.equalsIgnoreCase(v)).or(() -> {
                    tableModel.add(value);
                  });
                }
              }
            })
        .addActionFactory(
            (tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icon.GuiIcons.EDIT_ICON)
                .setProcedure(createSelectFormatActionProcedure(description, preferences, formats, tableModel))
                .build())
        .addRemoveObjectsAction()
        .build();
  }

  private IActionProcedure createSearchFormatActionProcedure(
      final IHttpConnectionDescription description,
      final IPreferences preferences,
      final List<String> formats,
      final IObjectTableModel<String> tableModel,
      final IBlock<RuntimeException> clearblock,
      final IObjectModel<String> model) {
    return new ActionProcedurBuilder<List<String>, List<String>>().setInitializer(parentComponent -> {
      final String string = model.get();
      clearblock.execute();
      return getValues(parentComponent, description, formats, string);
    }).setConsumer((parentComponent, values) -> {
      if (values == null) {
        return;
      }
      final List<String> selections = new ArrayList<>();
      final IDialogResult result = new ConfigurableDialogLauncher()
          .setPreferences(preferences.node("formats")) //$NON-NLS-1$
          .setTitle(Messages.formats)
          .enableCloseOnEscape()
          .setContentPaneFactory((o, p) -> new SelectTableContentPane<>(selections, tableModel, values, v -> v))
          .setCancleOkButtonDialog()
          .launch(parentComponent);
      if (result != DialogResult.OK) {
        return;
      }
      final List<String> filtered = Streams
          .of(selections)
          .filter(f -> !StringUtilities.isNullOrTrimmedEmpty(f))
          .filter(f -> !Streams.of(tableModel.values()).first(v -> f.equalsIgnoreCase(v)).isAccepted())
          .asList();
      GuiUtilities.invokeLater(() -> tableModel.add(filtered));
    }).build();
  }

  private IActionProcedure createSelectFormatActionProcedure(
      final IHttpConnectionDescription description,
      final IPreferences preferences,
      final List<String> formats,
      final IObjectTableModel<String> tableModel) {

    return new ActionProcedurBuilder<List<String>, List<String>>().setInitializer(parentComponent -> {
      final List<String> values = getValues(parentComponent, description, formats, null);
      if (formats.isEmpty() && values != null && !values.isEmpty()) {
        formats.addAll(values);
      }
      return values;
    }).setConsumer((parentComponent, value) -> {
      if (value == null) {
        return;
      }
      final List<String> selections = new ArrayList<>();
      final IDialogResult result = new ConfigurableDialogLauncher()
          .setPreferences(preferences.node("formats")) //$NON-NLS-1$
          .setTitle(Messages.formats)
          .enableCloseOnEscape()
          .setContentPaneFactory((o, p) -> new SelectTableContentPane<>(selections, tableModel, value, v -> v))
          .setCancleOkButtonDialog()
          .launch(parentComponent);
      if (result != DialogResult.OK) {
        return;
      }
      tableModel.set(selections);
    }).build();
  }

  private List<String> getValues(
      final Component parentComponent,
      final IHttpConnectionDescription description,
      final List<String> values,
      final String value) {
    try {
      return new ProgressDialogLauncher<>(new IProgressTask<List<String>, IOException>() {

        @Override
        public List<String> run(final IProgressMonitor progressMonitor, final ICanceler canceler)
            throws InterruptedException,
            IOException {
          final List<String> result = new ArrayList<>();
          if (values.isEmpty()) {
            getValues(canceler, description, value)
                .stream()
                .filter(s -> !StringUtilities.isNullOrTrimmedEmpty(s))
                .sorted()
                .forEachOrdered(s -> result.add(s));
            return result;
          }
          if (value != null) {
            return Streams.of(values).filter(f -> f.equalsIgnoreCase(value)).asList();
          }
          return values;
        }
      }).launch(parentComponent);
    } catch (final InterruptedException exception1) {
      return null;
    } catch (final IOException exception) {
      logger.log(ILevel.DEBUG, Messages.format_query_faild, exception);
      new MessageDialogLauncher()
          .error()
          .title(Messages.formats)
          .text(Messages.format_query_faild)
          .description(exception.getMessage())
          .throwable(exception)
          .launch(parentComponent);
      return null;
    }
  }

  private List<String> getValues(
      final ICanceler canceler,
      final IHttpConnectionDescription description,
      final String string)
      throws InterruptedException,
      IOException {
    final IResultProducer<StringListResultResponse> responseProducer = (
        c,
        statusCode,
        statusMessage,
        contentType,
        contentEncoding,
        inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(StringListResultResponse.class).unmarshal(
            inputStream);

    final ResourceFormatListRequestBuilder builder = ResourceFormatRequestBuilder
        .list(description.getUrl())
        .authentication(description.getUserName(), description.getPassword())
        .setLimit(200);
    Optional.of(string).consume(s -> builder.setSearchStringPart(string));
    final IRequest request = builder.build();

    try (final IObjectRequestExecutor<StringListResultResponse> executor = this.requestExecutorBuilderFactory
        .<StringListResultResponse> create()
        .setResultProducer(responseProducer)
        .build()) {
      final StringListResultResponse response = executor.execute(canceler, request);
      return response.isSuccess() ? Arrays.asList(response.getResult()) : Collections.emptyList();
    } catch (SocketException | InterruptedIOException exception) {
      if (canceler.isCanceled()) {
        throw new InterruptedException();
      }
      throw exception;
    }
  }

}
