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
import java.util.Objects;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutor;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.IResultProducer;
import net.anwiba.commons.lang.functional.IBlock;
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
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.ckan.json.schema.v1_0.TagListResultResponse;
import net.anwiba.spatial.ckan.json.schema.v1_0.TagSearchResultResponse;
import net.anwiba.spatial.ckan.json.types.I18String;
import net.anwiba.spatial.ckan.marshaller.CkanJsonResponseUnmarshallerFactory;
import net.anwiba.spatial.ckan.request.TagRequestBuilder;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public final class TagTableFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(TagTableFactory.class);
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;

  public TagTableFactory(final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
  }

  public ObjectListTable<Tag> create(
      final IPreferences preferences,
      final IHttpConnectionDescription description,
      final List<Tag> values,
      final int columnWitdh) {
    final List<Tag> tags = new ArrayList<>();
    return new ObjectTableBuilder<Tag>()
        .setValues(values)
        .addSortableStringColumn(Messages.name, value -> CkanUtilities.toString(value), columnWitdh)
        .addActionFactory(
            (tableModel, selectionIndicesProvider, selectionModel, sortStateModel) -> new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icon.GuiIcons.EDIT_ICON)
                .setProcedure(createSelectTagActionProcedure(description, preferences, tags, tableModel))
                .build())
        .addRemoveObjectsAction()
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
                    Streams.of(tableModel.values()).first(v -> value.equalsIgnoreCase(CkanUtilities.toString(v))).or(
                        () -> {
                          tableModel.add(createTag(value));
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
                      createSearchTagActionProcedure(description, preferences, tags, tableModel, clearblock, model))
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
                  Streams.of(tableModel.values()).first(v -> value.equalsIgnoreCase(CkanUtilities.toString(v))).or(
                      () -> {
                        tableModel.add(createTag(value));
                      });
                }
              }

            })
        .build();
  }

  private Tag createTag(final String value) {
    final Tag tag = new Tag();
    final I18String i18String = new I18String(value);
    tag.setId(value);
    tag.setName(i18String);
    tag.setDisplay_name(i18String);
    tag.setTitle(i18String);
    return tag;
  }

  private IActionProcedure createSelectTagActionProcedure(
      final IHttpConnectionDescription description,
      final IPreferences preferences,
      final List<Tag> tags,
      final IObjectTableModel<Tag> tableModel) {
    return new ActionProcedurBuilder<List<Tag>, List<Tag>>().setInitializer(parentComponent -> {
      final List<Tag> values = getValues(parentComponent, description, tags, null);
      if (tags.isEmpty() && values != null && !values.isEmpty()) {
        tags.addAll(values);
      }
      return values;
    }).setConsumer((parentComponent, value) -> {
      if (value == null) {
        return;
      }
      final List<Tag> selections = new ArrayList<>();
      final IDialogResult result = new ConfigurableDialogLauncher()
          .setPreferences(preferences.node("tag")) //$NON-NLS-1$
          .setTitle(Messages.tags)
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

  private IActionProcedure createSearchTagActionProcedure(
      final IHttpConnectionDescription description,
      final IPreferences preferences,
      final List<Tag> tags,
      final IObjectTableModel<Tag> tableModel,
      final IBlock<RuntimeException> clearblock,
      final IObjectModel<String> model) {
    return new ActionProcedurBuilder<List<Tag>, List<Tag>>().setInitializer(parentComponent -> {
      final String string = model.get();
      clearblock.execute();
      return getValues(parentComponent, description, tags, string);
    }).setConsumer((parentComponent, value) -> {
      if (value == null) {
        return;
      }
      final List<Tag> selections = new ArrayList<>();
      final IDialogResult result = new ConfigurableDialogLauncher()
          .setPreferences(preferences.node("tag")) //$NON-NLS-1$
          .setTitle(Messages.tags)
          .enableCloseOnEscape()
          .setContentPaneFactory(
              (o, p) -> new SelectTableContentPane<>(selections, tableModel, value, v -> CkanUtilities.toString(v)))
          .setCancleOkButtonDialog()
          .launch(parentComponent);
      if (result != DialogResult.OK) {
        return;
      }
      final List<Tag> filtered = Streams
          .of(selections)
          .filter(
              f -> !Streams
                  .of(tableModel.values())
                  .first(v -> Objects.equals(CkanUtilities.toString(v.getName()), CkanUtilities.toString(f.getName())))
                  .isAccepted())
          .asList();
      GuiUtilities.invokeLater(() -> tableModel.add(filtered));
    }).build();
  }

  private List<Tag> getValues(
      final Component parentComponent,
      final IHttpConnectionDescription description,
      final List<Tag> tags,
      final String string) {
    try {
      return new ProgressDialogLauncher<>((progressMonitor, canceler) -> {
        if (tags.isEmpty()) {
          final List<Tag> result = new ArrayList<>();
          if (string == null) {
            executeTagListQuery(canceler, description)
                .stream()
                .filter(s1 -> s1 != null)
                .sorted((o1, o2) -> CkanUtilities.toString(o1).compareTo(CkanUtilities.toString(o2)))
                .forEachOrdered(s2 -> result.add(s2));
            return result;
          }
          executeTagSearchQuery(canceler, description, string)
              .stream()
              .filter(s1 -> s1 != null)
              .sorted((o1, o2) -> CkanUtilities.toString(o1).compareTo(CkanUtilities.toString(o2)))
              .forEachOrdered(s2 -> result.add(s2));
          return result;
        }
        return tags;
      }).launch(parentComponent);
    } catch (final InterruptedException exception1) {
      return null;
    } catch (final IOException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      new MessageDialogLauncher()
          .error()
          .title(Messages.tags)
          .text(Messages.tag_query_faild)
          .description(exception.getMessage())
          .throwable(exception)
          .launch(parentComponent);
      return null;
    }
  }

  private List<Tag> executeTagListQuery(final ICanceler canceler, final IHttpConnectionDescription description)
      throws InterruptedException,
      IOException {
    final IResultProducer<TagListResultResponse> responseProducer = (
        c,
        statusCode,
        statusMessage,
        contentType,
        contentEncoding,
        inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(TagListResultResponse.class).unmarshal(
            inputStream);

    final IRequest request = TagRequestBuilder
        .list(description.getUrl())
        .setAllFields()
        .authentication(description.getUserName(), description.getPassword())
        .build();

    try (final IObjectRequestExecutor<TagListResultResponse> executor = this.requestExecutorBuilderFactory
        .<TagListResultResponse> create()
        .setResultProducer(responseProducer)
        .build()) {
      final TagListResultResponse response = executor.execute(canceler, request);
      return response.isSuccess() ? Arrays.asList(response.getResult()) : Collections.emptyList();
    } catch (SocketException | InterruptedIOException exception) {
      if (canceler.isCanceled()) {
        throw new InterruptedException();
      }
      throw exception;
    }
  }

  private List<Tag> executeTagSearchQuery(
      final ICanceler canceler,
      final IHttpConnectionDescription description,
      final String string)
      throws InterruptedException,
      IOException {
    final IResultProducer<TagSearchResultResponse> responseProducer = (
        c,
        statusCode,
        statusMessage,
        contentType,
        contentEncoding,
        inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(TagSearchResultResponse.class).unmarshal(
            inputStream);

    final IRequest request = TagRequestBuilder
        .search(description.getUrl())
        .query(string)
        .authentication(description.getUserName(), description.getPassword())
        .build();

    try (final IObjectRequestExecutor<TagSearchResultResponse> executor = this.requestExecutorBuilderFactory
        .<TagSearchResultResponse> create()
        .setResultProducer(responseProducer)
        .build()) {
      final TagSearchResultResponse response = executor.execute(canceler, request);
      return response.isSuccess() ? Arrays.asList(response.getResult().getResults()) : Collections.emptyList();
    } catch (SocketException | InterruptedIOException exception) {
      if (canceler.isCanceled()) {
        throw new InterruptedException();
      }
      throw exception;
    }
  }
}
