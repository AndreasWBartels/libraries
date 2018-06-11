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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.dialog.ConfigurableDialogLauncher;
import net.anwiba.commons.swing.dialog.IDialogLauncher;
import net.anwiba.commons.swing.dialog.IDialogsContainer;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;
import net.anwiba.commons.swing.dialog.progress.ProgressDialogLauncher;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public class CkanSearchDialogLauncherFactory {

  private static final int RESTULT_ROWS = 11;
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;
  private final IResourceOpenConsumer resourceOpenConsumer;
  private final IDialogsContainer dialogsContainer;
  private final DatasetQueryExecutor datasetQueryExecutor;

  public CkanSearchDialogLauncherFactory(
      final IDialogsContainer dialogsContainer,
      final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory,
      final IResourceOpenConsumer resourceOpenconsumer,
      final IFormatsNameConverter formatsNameConverter) {
    this.dialogsContainer = dialogsContainer;
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
    this.resourceOpenConsumer = resourceOpenconsumer;
    this.datasetQueryExecutor = new DatasetQueryExecutor(requestExecutorBuilderFactory, formatsNameConverter);
  }

  public IDialogLauncher create(
      final Component parentComponent,
      final IPreferences preferences,
      final IHttpConnectionDescription description)
      throws InterruptedException {

    try {
      return new ProgressDialogLauncher<IDialogLauncher, IOException>((progressMonitor, canceler) -> {

        final ObjectPair<List<Dataset>, Integer> datasetsResponse = this.datasetQueryExecutor.query(
            canceler,
            description,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            null,
            0,
            RESTULT_ROWS);
        final int results = datasetsResponse.getSecondObject();
        final List<Dataset> datasets = datasetsResponse.getFirstObject();

        return new ConfigurableDialogLauncher() //
            .addBeforeShowExecutable(value -> {
              CkanSearchDialogLauncherFactory.this.dialogsContainer.add(description, value);
              value.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(final WindowEvent e) {
                  value.removeWindowListener(this);
                  CkanSearchDialogLauncherFactory.this.dialogsContainer.remove(description);
                }

              });
            })
            .setApplicationModalExclusionType()
            .setModelessModality()
            .setTitle(MessageFormat.format(Messages.ckan_search_t0, description.toString()))
            .setProgressDialogDisabled()
            .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.SYSTEM_SEARCH)
            .enableCloseOnEscape()
            //            .setCloseButtonDialog()
            .setPreferences(preferences.node("ckan", "search")) //$NON-NLS-1$ //$NON-NLS-2$
            .setContentPaneFactory(
                new CkanSearchContentPaneFactory(
                    this.requestExecutorBuilderFactory,
                    this.resourceOpenConsumer,
                    this.datasetQueryExecutor,
                    description,
                    RESTULT_ROWS,
                    datasets,
                    results));
      }).launch(parentComponent);
    } catch (final IOException exception) {
      new MessageDialogLauncher()
          .error()
          .title(Messages.ckan)
          .text(Messages.dataset_query_faild)
          .description(exception.getMessage())
          .throwable(exception)
          .launch(parentComponent);
      return null;
    }
  }
}
