/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.IntegerModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.progress.ProgressDialogLauncher;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.StringFieldBuilder;
import net.anwiba.commons.swing.table.FilterableObjectTableModel;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.Resource;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public final class CkanSearchContentPane extends AbstractContentPane {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(CkanSearchContentPane.class);
  private final IPreferences preferences;
  private boolean isQueryEnabled = true;
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;
  private final IResourceOpenConsumer resourceOpenConsumer;
  private final IHttpConnectionDescription description;
  private final DatasetQueryExecutor datasetQueryExecutor;
  private final List<Dataset> datasets;
  private final int results;
  private final int numberOfResultRows;

  public CkanSearchContentPane(
      final IPreferences preferences,
      final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory,
      final IResourceOpenConsumer resourceOpenConsumer,
      final DatasetQueryExecutor datasetQueryExecutor,
      final IHttpConnectionDescription description,
      final int numberOfResultRows,
      final List<Dataset> datasets,
      final int results) {
    this.preferences = preferences;
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
    this.resourceOpenConsumer = resourceOpenConsumer;
    this.datasetQueryExecutor = datasetQueryExecutor;
    this.datasets = datasets;
    this.numberOfResultRows = numberOfResultRows;
    this.results = results;
    this.description = description;
  }

  @Override
  public JComponent getComponent() {

    final int columnWidth = 20;

    final JPanel contentPanel = new JPanel();

    final IObjectModel<Dataset> datasetModel = new ObjectModel<>();

    final IntegerModel offsetModel = new IntegerModel();
    offsetModel.setValue(0);

    final IntegerModel resultCountModel = new IntegerModel();
    resultCountModel.setValue(this.results);

    final ObjectListTable<String> formatsTable = new FormatTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, new ArrayList<>());

    final JPanel formatsPanel = new JPanel();
    formatsPanel.setLayout(new BorderLayout());
    formatsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    formatsPanel.add(createLabelPanel(Messages.formats), BorderLayout.NORTH);
    formatsPanel.add(formatsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<Group> groupsTable = new GroupTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, new ArrayList<>(), columnWidth);

    final JPanel groupsPanel = new JPanel();
    groupsPanel.setLayout(new BorderLayout());
    groupsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    groupsPanel.add(createLabelPanel(Messages.categories), BorderLayout.NORTH);
    groupsPanel.add(groupsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<Tag> tagsTable = new TagTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, new ArrayList<>(), columnWidth);

    final JPanel tagsPanel = new JPanel();
    tagsPanel.setLayout(new BorderLayout());
    tagsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    tagsPanel.add(createLabelPanel(Messages.tags), BorderLayout.NORTH);
    tagsPanel.add(tagsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<Organization> organizationsTable = new OrganizationTableFactory(
        this.requestExecutorBuilderFactory).create(this.preferences, this.description, new ArrayList<>());

    final JPanel organizationsPanel = new JPanel();
    organizationsPanel.setLayout(new BorderLayout());
    organizationsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    organizationsPanel.add(createLabelPanel(Messages.organizations), BorderLayout.NORTH);
    organizationsPanel.add(organizationsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<License> licensesTable = new LicenseTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, new ArrayList<>(), columnWidth);

    final JPanel licensesPanel = new JPanel();
    licensesPanel.setLayout(new BorderLayout());
    licensesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    licensesPanel.add(createLabelPanel(Messages.licenses), BorderLayout.NORTH);
    licensesPanel.add(licensesTable.getComponent(), BorderLayout.CENTER);

    final JPanel filterPane = new JPanel();
    filterPane.setLayout(new BoxLayout(filterPane, BoxLayout.PAGE_AXIS));
    filterPane.add(groupsPanel);
    filterPane.add(organizationsPanel);
    filterPane.add(formatsPanel);
    filterPane.add(licensesPanel);
    filterPane.add(tagsPanel);
    filterPane.setMinimumSize(new Dimension(250, 200));
    filterPane.setPreferredSize(new Dimension(250, 200));

    final JPanel datasetLabelsPanel = new JPanel();
    datasetLabelsPanel.setLayout(new BoxLayout(datasetLabelsPanel, BoxLayout.LINE_AXIS));
    datasetLabelsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    datasetLabelsPanel.add(new JLabel(Messages.datasets));
    datasetLabelsPanel.add(Box.createHorizontalGlue());
    final JLabel resultsLable = new JLabel(
        MessageFormat.format(
            Messages.i0_to_i1_of_i2,
            (offsetModel.getValue() + 1),
            (offsetModel.getValue() + this.datasets.size()),
            resultCountModel.getValue()));
    datasetLabelsPanel.add(resultsLable);

    final IntegerModel selectedIndexModel = new IntegerModel();

    final ObjectListTable<Dataset> datasetTable = new DatasetTableFactory(this.numberOfResultRows)
        .create(this.description, offsetModel, resultCountModel, selectedIndexModel, this.datasets);
    final ISelectionModel<Dataset> dataSetSelectionModel = datasetTable.getSelectionModel();

    final JPanel datasetsPanel = new JPanel();
    datasetsPanel.setLayout(new BorderLayout());
    datasetsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    datasetsPanel.add(datasetLabelsPanel, BorderLayout.NORTH);
    datasetsPanel.add(datasetTable.getComponent(), BorderLayout.CENTER);
    datasetsPanel.setMinimumSize(new Dimension(200, 221));
    datasetsPanel.setPreferredSize(new Dimension(1000, 221));
    datasetsPanel.setMaximumSize(new Dimension(2000, 221));

    final JEditorPane datasetDescriptionTextArea = createTextArea(contentPanel.getBackground());
    final JPanel datasetDescriptionPanel = createDescriptionPanel(datasetDescriptionTextArea, 200);

    if (!this.datasets.isEmpty()) {
      final Dataset dataset = this.datasets.get(0);
      dataSetSelectionModel.setSelectedObject(dataset);
      datasetModel.set(dataset);
      datasetDescriptionTextArea.setText(
          "<html><body><font size=\"-1\">" //$NON-NLS-1$
              + new DataSetDescriptionTextFactory().create(dataset)
              + "</font></body></html>"); //$NON-NLS-1$
      datasetDescriptionTextArea.setCaretPosition(0);
    }

    datasetTable.getTableModel().addListModelListener(new IChangeableListListener<Dataset>() {

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<Dataset> object) {
        IChangeableListListener.super.objectsAdded(indeces, object);
      }

      @Override
      public void objectsChanged(final Iterable<Dataset> oldObjects, final Iterable<Dataset> newObjects) {
        if (newObjects.iterator().hasNext()) {
          if (selectedIndexModel.getValue() != -1) {
            final List<Dataset> values = IterableUtilities.asList(newObjects);
            if ((values.size() - 1) > selectedIndexModel.getValue()) {
              datasetTable.getSelectionModel().setSelectedObject(values.get(selectedIndexModel.getValue()));
            } else {
              datasetTable.getSelectionModel().setSelectedObject(values.get(values.size() - 1));
            }
            selectedIndexModel.setValue(-1);
            return;
          }
          final Dataset dataSet = newObjects.iterator().next();
          GuiUtilities.invokeLater(() -> {
            try {
              datasetTable.getSelectionModel().setSelectedObject(dataSet);
            } catch (final Throwable exception) {
              logger.log(ILevel.DEBUG, exception.getMessage(), exception);
            }
          });
        }
      }
    });

    final ObjectListTable<Resource> resourcesTable = new ResourceTableFactory(this.resourceOpenConsumer)
        .create(this.description, datasetModel);

    final IObjectTableModel<Resource> resourceTableModel = resourcesTable.getTableModel();
    final ISelectionModel<Resource> resourceSelectionModel = resourcesTable.getSelectionModel();

    final JPanel resourcesPanel = new JPanel();
    resourcesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    resourcesPanel.setLayout(new BorderLayout());
    resourcesPanel.add(createLabelPanel(Messages.resources), BorderLayout.NORTH);
    resourcesPanel.add(resourcesTable.getComponent(), BorderLayout.CENTER);
    resourcesPanel.setMinimumSize(new Dimension(200, 124));
    resourcesPanel.setPreferredSize(new Dimension(1000, 124));
    resourcesPanel.setMaximumSize(new Dimension(2000, 124));

    resourcesTable.getTableModel().addListModelListener(new IChangeableListListener<Resource>() {

      @Override
      public void objectsChanged(final Iterable<Resource> oldObjects, final Iterable<Resource> newObjects) {
        if (newObjects.iterator().hasNext()) {
          final Resource resource = newObjects.iterator().next();
          GuiUtilities.invokeLater(() -> {
            try {
              resourcesTable.getSelectionModel().setSelectedObject(resource);
            } catch (final Throwable exception) {
              logger.log(ILevel.DEBUG, exception.getMessage(), exception);
            }
          });
        }
      }
    });

    final JEditorPane resourceDescriptionTextArea = createTextArea(contentPanel.getBackground());
    final JPanel resourceDescriptionPanel = createDescriptionPanel(resourceDescriptionTextArea, 100);

    if (!resourcesTable.getTableModel().isEmpty()) {
      final Resource resource = resourcesTable.getTableModel().get(0);
      resourceSelectionModel.setSelectedObject(resource);
      resourceDescriptionTextArea.setText(
          "<html><body><font size=\"-1\">" //$NON-NLS-1$
              + new ResourceDescriptionTextFactory().create(resource)
              + "</font></body></html>"); //$NON-NLS-1$
      resourceDescriptionTextArea.setCaretPosition(0);
    }

    final JPanel choosePane = new JPanel();
    choosePane.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
    choosePane.setLayout(new BoxLayout(choosePane, BoxLayout.PAGE_AXIS));
    choosePane.add(datasetsPanel);
    choosePane.add(datasetDescriptionPanel);
    choosePane.add(resourcesPanel);
    choosePane.add(resourceDescriptionPanel);

    final IObjectField<String> queryField = new StringFieldBuilder()
        .setColumns(40)
        .setToolTip(Messages.query_string)
        .setKeyListenerFactory((model, document, clearBlock) -> new KeyAdapter() {
          @Override
          public void keyTyped(final KeyEvent e) {
            if (e.getKeyChar() == '\n') {
              if (offsetModel.getValue() == 0) {
                query(
                    contentPanel,
                    CkanSearchContentPane.this.description,
                    offsetModel,
                    resultCountModel,
                    model,
                    formatsTable.getTableModel(),
                    groupsTable.getTableModel(),
                    tagsTable.getTableModel(),
                    organizationsTable.getTableModel(),
                    licensesTable.getTableModel(),
                    datasetTable.getTableModel());
                return;
              }
              offsetModel.setValue(0);
            }
          }
        })
        .addActionFactory((model, document, enabledDistributor, clearBlock) -> {
          final IBooleanModel booleanModel = new BooleanModel(false);
          model.addChangeListener(() -> booleanModel.set(!StringUtilities.isNullOrTrimmedEmpty(model.get())));
          return new ConfigurableActionBuilder()
              .setIcon(GuiIcons.EDIT_CLEAR_LOCATIONBAR_ICON)
              .setEnabledDistributor(enabledDistributor.and(booleanModel))
              .setTooltip(Messages.clear_query)
              .setProcedure(value -> {
                clearBlock.execute();
                if (offsetModel.getValue() == 0) {
                  query(
                      contentPanel,
                      this.description,
                      offsetModel,
                      resultCountModel,
                      model,
                      formatsTable.getTableModel(),
                      groupsTable.getTableModel(),
                      tagsTable.getTableModel(),
                      organizationsTable.getTableModel(),
                      licensesTable.getTableModel(),
                      datasetTable.getTableModel());
                  return;
                }
                offsetModel.setValue(0);
              })
              .build();
        })
        .addActionFactory(
            (model, document, enabledDistributor, clearBlock) -> new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icon.GuiIcons.ADVANCED_SEARCH_ICON)
                .setProcedure(component -> {
                  if (offsetModel.getValue() == 0) {
                    query(
                        contentPanel,
                        this.description,
                        offsetModel,
                        resultCountModel,
                        model,
                        formatsTable.getTableModel(),
                        groupsTable.getTableModel(),
                        tagsTable.getTableModel(),
                        organizationsTable.getTableModel(),
                        licensesTable.getTableModel(),
                        datasetTable.getTableModel());
                    return;
                  }
                  offsetModel.setValue(0);
                })
                .build())
        .build();

    final JPanel queryPanel = new JPanel();
    queryPanel.add(queryField.getComponent());
    queryPanel.add(
        new JButton(
            new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icon.GuiIcons.EDIT_CLEAR_LIST)
                .setProcedure(c -> {
                  this.isQueryEnabled = false;
                  queryField.getModel().set(null);
                  formatsTable.getTableModel().removeAll();
                  groupsTable.getTableModel().removeAll();
                  tagsTable.getTableModel().removeAll();
                  organizationsTable.getTableModel().removeAll();
                  licensesTable.getTableModel().removeAll();
                  this.isQueryEnabled = true;
                  if (offsetModel.getValue() == 0) {
                    query(
                        contentPanel,
                        this.description,
                        offsetModel,
                        resultCountModel,
                        queryField.getModel(),
                        formatsTable.getTableModel(),
                        groupsTable.getTableModel(),
                        tagsTable.getTableModel(),
                        organizationsTable.getTableModel(),
                        licensesTable.getTableModel(),
                        datasetTable.getTableModel());
                    return;
                  }
                  offsetModel.setValue(0);
                })
                .setTooltip(Messages.reset)
                .build()));

    final IObjectModel<String> queryModel = queryField.getModel();

    contentPanel.setLayout(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    contentPanel.add(queryPanel, BorderLayout.NORTH);
    final JSplitPane bodyPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPane, choosePane);
    bodyPane.setBorder(BorderFactory.createEmptyBorder());
    bodyPane.setDividerSize(2);
    contentPanel.add(bodyPane, BorderLayout.CENTER);

    offsetModel.addChangeListener(
        () -> resultsLable.setText(
            MessageFormat.format(
                Messages.i0_to_i1_of_i2,
                (offsetModel.getValue() + 1),
                (offsetModel.getValue() + this.datasets.size()),
                resultCountModel.getValue())));

    resultCountModel.addChangeListener(
        () -> resultsLable.setText(
            MessageFormat.format(
                Messages.i0_to_i1_of_i2,
                (offsetModel.getValue() + 1),
                (offsetModel.getValue() + this.datasets.size()),
                resultCountModel.getValue())));

    ((FilterableObjectTableModel<String>) formatsTable.getTableModel()).getObjectTableModel().addListModelListener(
        createListListener(
            this.description,
            offsetModel,
            resultCountModel,
            formatsTable,
            groupsTable,
            tagsTable,
            organizationsTable,
            licensesTable,
            datasetTable,
            contentPanel,
            queryModel));

    ((FilterableObjectTableModel<Group>) groupsTable.getTableModel()).getObjectTableModel().addListModelListener(
        createListListener(
            this.description,
            offsetModel,
            resultCountModel,
            formatsTable,
            groupsTable,
            tagsTable,
            organizationsTable,
            licensesTable,
            datasetTable,
            contentPanel,
            queryModel));

    ((FilterableObjectTableModel<Tag>) tagsTable.getTableModel()) //
        .getObjectTableModel()
        .addListModelListener(
            createListListener(
                this.description,
                offsetModel,
                resultCountModel,
                formatsTable,
                groupsTable,
                tagsTable,
                organizationsTable,
                licensesTable,
                datasetTable,
                contentPanel,
                queryModel));

    ((FilterableObjectTableModel<Organization>) organizationsTable.getTableModel())
        .getObjectTableModel()
        .addListModelListener(
            createListListener(
                this.description,
                offsetModel,
                resultCountModel,
                formatsTable,
                groupsTable,
                tagsTable,
                organizationsTable,
                licensesTable,
                datasetTable,
                contentPanel,
                queryModel));

    ((FilterableObjectTableModel<License>) licensesTable.getTableModel()).getObjectTableModel().addListModelListener(
        createListListener(
            this.description,
            offsetModel,
            resultCountModel,
            formatsTable,
            groupsTable,
            tagsTable,
            organizationsTable,
            licensesTable,
            datasetTable,
            contentPanel,
            queryModel));

    offsetModel.addChangeListener(() -> {
      if (!this.isQueryEnabled) {
        return;
      }
      query(
          contentPanel,
          this.description,
          offsetModel,
          resultCountModel,
          queryModel,
          formatsTable.getTableModel(),
          groupsTable.getTableModel(),
          tagsTable.getTableModel(),
          organizationsTable.getTableModel(),
          licensesTable.getTableModel(),
          datasetTable.getTableModel());
    });

    dataSetSelectionModel.addSelectionListener(event -> {
      if (event.getSource().isEmpty()) {
        datasetModel.set(null);
        resourceTableModel.set(new ArrayList<>());
        GuiUtilities.invokeLater(() -> {
          datasetDescriptionTextArea.setText("<html><body><body></html>"); //$NON-NLS-1$
          datasetDescriptionTextArea.setCaretPosition(0);
        });
        return;
      }
      final Dataset dataset = event.getSource().getSelectedObjects().iterator().next();
      GuiUtilities.invokeLater(() -> {
        datasetDescriptionTextArea.setText(
            "<html><body><font size=\"-1\">" //$NON-NLS-1$
                + new DataSetDescriptionTextFactory().create(dataset)
                + "</font></body></html>"); //$NON-NLS-1$
        datasetDescriptionTextArea.setCaretPosition(0);
      });
      datasetModel.set(dataset);
      resourceTableModel.set(new ArrayList<>(Arrays.asList(dataset.getResources())));
    });

    resourceSelectionModel.addSelectionListener(event -> {
      if (event.getSource().isEmpty()) {
        GuiUtilities.invokeLater(() -> {
          resourceDescriptionTextArea.setText("<html><body><body></html>"); //$NON-NLS-1$
          resourceDescriptionTextArea.setCaretPosition(0);
        });
        return;
      }
      final Resource resource = event.getSource().getSelectedObjects().iterator().next();
      GuiUtilities.invokeLater(() -> {
        resourceDescriptionTextArea.setText(
            "<html><body><font size=\"-1\">" //$NON-NLS-1$
                + new ResourceDescriptionTextFactory().create(resource)
                + "</font></body></html>"); //$NON-NLS-1$
        resourceDescriptionTextArea.setCaretPosition(0);
      });
    });

    return contentPanel;
  }

  private JPanel createDescriptionPanel(final JEditorPane textArea, final int height) {
    final JPanel panel = new JPanel();
    final JScrollPane scrollPanel = new JScrollPane(textArea);
    scrollPanel.setBorder(BorderFactory.createEtchedBorder());
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    panel.add(createLabelPanel(Messages.description), BorderLayout.NORTH);
    panel.add(scrollPanel, BorderLayout.CENTER);
    panel.setMinimumSize(new Dimension(200, height));
    panel.setPreferredSize(new Dimension(1000, height));
    panel.setMaximumSize(new Dimension(2000, 500));
    return panel;
  }

  private JEditorPane createTextArea(final Color background) {
    final JEditorPane textArea = new JEditorPane("text/html", "<html><body><body></html>"); //$NON-NLS-1$//$NON-NLS-2$
    textArea.setEditable(false);
    textArea.setBackground(background);
    if (Desktop.isDesktopSupported() && textArea.getDocument() instanceof HTMLDocument) {
      final Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.OPEN)) {
        textArea.addHyperlinkListener(hyperlinkEvent -> {
          if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            final String descriptionString = hyperlinkEvent.getDescription();
            logger.log(ILevel.DEBUG, "href '" + descriptionString + "'"); //$NON-NLS-1$//$NON-NLS-2$
            try {
              final URL url = hyperlinkEvent.getURL();
              if (url != null) {
                logger.log(ILevel.DEBUG, "href '" + url + "'"); //$NON-NLS-1$//$NON-NLS-2$
                desktop.browse(url.toURI());
              } else if (descriptionString != null) {
                final File file = new File(descriptionString);
                final URI uri = file.getAbsoluteFile().toURI();
                desktop.browse(uri);
              }
            } catch (final IOException | URISyntaxException exception) {
              logger.log(ILevel.WARNING, "Couldn't browse '" + descriptionString + "'"); //$NON-NLS-1$//$NON-NLS-2$
              logger.log(ILevel.WARNING, exception.getMessage(), exception);
            }
          }
        });
      }
    }
    return textArea;
  }

  private JPanel createLabelPanel(final String text) {
    final JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
    labelPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    labelPanel.add(new JLabel(text));
    return labelPanel;
  }

  private void query(
      final Component parentCompoment,
      final IHttpConnectionDescription connectionDescription,
      final IntegerModel offsetModel,
      final IntegerModel resultCountModel,
      final IObjectModel<String> queryModel,
      final IObjectTableModel<String> formatsTableModel,
      final IObjectTableModel<Group> groupTableModel,
      final IObjectTableModel<Tag> tagTableModel,
      final IObjectTableModel<Organization> organizationTableModel,
      final IObjectTableModel<License> licenseTableModel,
      final IObjectTableModel<Dataset> datasetTableModel) {
    final List<String> formats = IterableUtilities.asList(formatsTableModel.values());
    final List<Group> groups = IterableUtilities.asList(groupTableModel.values());
    final List<Tag> tags = IterableUtilities.asList(tagTableModel.values());
    final List<Organization> organizations = IterableUtilities.asList(organizationTableModel.values());
    final List<License> licenses = IterableUtilities.asList(licenseTableModel.values());
    final int offset = offsetModel.getValue();
    final String string = queryModel.get();
    try {
      final ObjectPair<List<Dataset>, Integer> result = new ProgressDialogLauncher<>(
          (progressMonitor, canceler) -> this.datasetQueryExecutor.query(
              canceler,
              connectionDescription,
              formats,
              groups,
              tags,
              organizations,
              licenses,
              string,
              offset,
              this.numberOfResultRows)).launch(parentCompoment);
      resultCountModel.setValue(result.getSecondObject());
      datasetTableModel.set(result.getFirstObject());
    } catch (final IOException exception) {
      logger.log(ILevel.DEBUG, Messages.format_query_faild, exception);
      new MessageDialogLauncher()
          .error()
          .title(Messages.formats)
          .text(Messages.format_query_faild)
          .description(exception.getMessage())
          .throwable(exception)
          .launch(parentCompoment);
    } catch (final InterruptedException exception) {
      // nothing to do
    }
  }

  private <T> IChangeableListListener<T> createListListener(
      final IHttpConnectionDescription connectionDescription,
      final IntegerModel offsetModel,
      final IntegerModel resultCountModel,
      final ObjectListTable<String> formatsTable,
      final ObjectListTable<Group> groupsTable,
      final ObjectListTable<Tag> tagsTable,
      final ObjectListTable<Organization> organizationsTable,
      final ObjectListTable<License> licensesTable,
      final ObjectListTable<Dataset> datasetsTable,
      final JPanel contentPanel,
      final IObjectModel<String> queryModel) {
    return new IChangeableListListener<T>() {

      private void execute() {
        if (!CkanSearchContentPane.this.isQueryEnabled) {
          return;
        }
        if (offsetModel.getValue() == 0) {
          query(
              contentPanel,
              connectionDescription,
              offsetModel,
              resultCountModel,
              queryModel,
              formatsTable.getTableModel(),
              groupsTable.getTableModel(),
              tagsTable.getTableModel(),
              organizationsTable.getTableModel(),
              licensesTable.getTableModel(),
              datasetsTable.getTableModel());
          return;
        }
        offsetModel.setValue(0);
      }

      @Override
      public void objectsUpdated(
          final Iterable<Integer> indeces,
          final Iterable<T> oldObjects,
          final Iterable<T> newObjects) {
        execute();
      }

      @Override
      public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<T> object) {
        execute();
      }

      @Override
      public void objectsChanged(final Iterable<T> oldObjects, final Iterable<T> newObjects) {
        execute();
      }

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<T> object) {
        execute();
      }
    };
  }
}
