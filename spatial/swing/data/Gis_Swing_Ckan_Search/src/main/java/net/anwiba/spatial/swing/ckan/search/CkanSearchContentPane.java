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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.HttpRequestException;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.lang.optional.If;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.message.MessageBuilder;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.IntegerModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.dialog.ConfigurableDialogLauncher;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.pane.LocalDateTimeContentPane;
import net.anwiba.commons.swing.dialog.pane.TextContentPane;
import net.anwiba.commons.swing.dialog.progress.ProgressDialogLauncher;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.object.GenericObjectFieldBuilder;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.StringFieldBuilder;
import net.anwiba.commons.swing.table.FilterableObjectTableModel;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.table.ObjectListTableMessages;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.time.SystemDateTimeUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.Resource;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.ckan.json.types.DateString;
import net.anwiba.spatial.ckan.json.types.I18String;
import net.anwiba.spatial.ckan.query.IPackageQueryExecutor;
import net.anwiba.spatial.ckan.query.IPackageSearchCondition;
import net.anwiba.spatial.ckan.query.IPackageSearchResult;
import net.anwiba.spatial.ckan.query.PackageSearchConditionBuilder;
import net.anwiba.spatial.ckan.request.sort.ISortOrder;
import net.anwiba.spatial.ckan.request.sort.Order;
import net.anwiba.spatial.ckan.request.sort.SortOrderList;
import net.anwiba.spatial.ckan.request.sort.SortOrderTerm;
import net.anwiba.spatial.ckan.request.time.Event;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.ckan.values.Envelope;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public final class CkanSearchContentPane extends AbstractContentPane {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(CkanSearchContentPane.class);
  private final IPreferences preferences;
  private final IBooleanModel isQueryEnabledModel = new BooleanModel(true);
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;
  private final IResourceOpenConsumer resourceOpenConsumer;
  private final IHttpConnectionDescription description;
  private final IPackageQueryExecutor packageQueryExecutor;
  private final List<Dataset> datasets;
  private final int results;
  private final int numberOfResultRows;
  private final IObjectDistributor<Envelope> envelopeDistributor;
  private final IZoomToConsumer zoomToConsumer;

  final IObjectModel<Envelope> envelopeModel = new ObjectModel<>();
  final IBooleanModel isEnvelopeLinkActiveModel = new BooleanModel(false);
  final IBooleanModel isEnvelopeLinkEnabledModel;
  private final IChangeableObjectListener envelopListener;
  final IBooleanDistributor useEnvelopDistributor;
  private final IDataSetResultsConsumer dataSetResultsConsumer;
  private final IDataSetConsumer dataSetConsumer;
  private final IObjectDistributor<String> datasetIdentifierDistributor;
  private final IObjectModel<IPackageSearchCondition> packageQueryConditionModel;
  private final IObjectReceiver<Envelope> envelopeReceiver;

  public CkanSearchContentPane(
      final IPreferences preferences,
      final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory,
      final IObjectModel<IPackageSearchCondition> packageQueryConditionModel,
      final IDataSetResultsConsumer dataSetResultsConsumer,
      final IDataSetConsumer dataSetConsumer,
      final IResourceOpenConsumer resourceOpenConsumer,
      final IZoomToConsumer zoomToConsumer,
      final IObjectDistributor<String> datasetIdentifierDistributor,
      final IObjectDistributor<Envelope> envelopeDistributor,
      final IObjectReceiver<Envelope> envelopeReceiver,
      final IObjectDistributor<Envelope> envelopeSetter,
      final IPackageQueryExecutor packageQueryExecutor,
      final IHttpConnectionDescription description,
      final int numberOfResultRows,
      final List<Dataset> datasets,
      final int results) {
    this.preferences = preferences;
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
    this.packageQueryConditionModel = packageQueryConditionModel;
    this.dataSetResultsConsumer = dataSetResultsConsumer;
    this.dataSetConsumer = dataSetConsumer;
    this.resourceOpenConsumer = resourceOpenConsumer;
    this.zoomToConsumer = zoomToConsumer;
    this.datasetIdentifierDistributor = datasetIdentifierDistributor;
    this.envelopeDistributor = envelopeDistributor;
    this.envelopeReceiver = envelopeReceiver;
    this.packageQueryExecutor = packageQueryExecutor;
    this.datasets = datasets;
    this.numberOfResultRows = numberOfResultRows;
    this.results = results;
    this.description = description;
    this.isEnvelopeLinkEnabledModel = new BooleanModel(this.envelopeDistributor.get() != null);
    this.useEnvelopDistributor = this.isEnvelopeLinkActiveModel.and(this.isEnvelopeLinkEnabledModel);
    this.envelopListener = () -> {
      this.isEnvelopeLinkEnabledModel.set(this.envelopeDistributor.get() != null);
      final boolean isUseEnvelope = this.useEnvelopDistributor.get();
      if (isUseEnvelope) {
        this.envelopeModel.set(this.envelopeDistributor.get());
      }
    };
    envelopeSetter.addChangeListener(() -> this.envelopeModel.set(envelopeSetter.get()));
  }

  @Override
  public JComponent getComponent() {

    final int columnWidth = 20;

    final JPanel contentPanel = new JPanel();

    final IPackageSearchCondition condition = new PackageSearchConditionBuilder(this.packageQueryConditionModel.get())
        .build();

    final IObjectModel<Dataset> datasetModel = new ObjectModel<>();
    final IntegerModel offsetModel = new IntegerModel(0);
    final IObjectModel<DateString> datasetCreateDateModel = new ObjectModel<>();
    final IObjectModel<DateString> resourceCreateDateModel = new ObjectModel<>();
    final IObjectModel<License> datasetLicenceModel = new ObjectModel<>();
    final IObjectModel<String> queryModel = new ObjectModel<>(condition.getQueryString());
    final IObjectModel<ISortOrder> sortOrderModel = new ObjectModel<>(
        new SortOrderList(
            Arrays.asList(
                new SortOrderTerm(Order.asc, "relevance"),
                new SortOrderTerm(Order.desc, "metadata_modified"))));
    final IObjectModel<Event> eventModel = new ObjectModel<>(Event.MODIFIED);
    final IObjectModel<LocalDateTime> fromDateModel = new ObjectModel<>(condition.getFromDate());
    final IObjectModel<LocalDateTime> toDateModel = new ObjectModel<>(condition.getToDate());
    final IntegerModel resultCountModel = new IntegerModel(this.results);
    this.envelopeModel.set(condition.getEnvelope());

    final IBooleanModel isTakeCurrentEnvelopeEnabledModel = new BooleanModel(true);
    final IChangeableObjectListener isTakeCurrentEnabledModelController = () -> isTakeCurrentEnvelopeEnabledModel.set(
        this.envelopeDistributor.get() != null
            && !Objects.equals(this.envelopeDistributor.get(), this.envelopeModel.get()));
    isTakeCurrentEnabledModelController.objectChanged();
    this.envelopeModel.addChangeListener(isTakeCurrentEnabledModelController);
    this.envelopeModel.addChangeListener(() -> this.envelopeReceiver.set(this.envelopeModel.get()));
    this.envelopeDistributor.addChangeListener(isTakeCurrentEnabledModelController);

    final ObjectListTable<String> formatsTable = new FormatTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, condition.getFormats());

    final JPanel formatsPanel = new JPanel();
    formatsPanel.setLayout(new BorderLayout());
    formatsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    formatsPanel.add(createLabelPanel(Messages.formats), BorderLayout.NORTH);
    formatsPanel.add(formatsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<Group> groupsTable = new GroupTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, condition.getGroups(), columnWidth);

    final JPanel groupsPanel = new JPanel();
    groupsPanel.setLayout(new BorderLayout());
    groupsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    groupsPanel.add(createLabelPanel(Messages.categories), BorderLayout.NORTH);
    groupsPanel.add(groupsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<Tag> tagsTable = new TagTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, condition.getTags(), columnWidth);

    final JPanel tagsPanel = new JPanel();
    tagsPanel.setLayout(new BorderLayout());
    tagsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    tagsPanel.add(createLabelPanel(Messages.tags), BorderLayout.NORTH);
    tagsPanel.add(tagsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<Organization> organizationsTable = new OrganizationTableFactory(
        this.requestExecutorBuilderFactory).create(this.preferences, this.description, condition.getOrganizations());

    final JPanel organizationsPanel = new JPanel();
    organizationsPanel.setLayout(new BorderLayout());
    organizationsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    organizationsPanel.add(createLabelPanel(Messages.organizations), BorderLayout.NORTH);
    organizationsPanel.add(organizationsTable.getComponent(), BorderLayout.CENTER);

    final ObjectListTable<License> licensesTable = new LicenseTableFactory(this.requestExecutorBuilderFactory)
        .create(this.preferences, this.description, condition.getLicenses(), columnWidth);

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
        .create(this.description, offsetModel, resultCountModel, selectedIndexModel, sortOrderModel, this.datasets);
    final ISelectionModel<Dataset> dataSetSelectionModel = datasetTable.getSelectionModel();

    final JPanel datasetsPanel = new JPanel();
    datasetsPanel.setLayout(new BorderLayout());
    datasetsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    datasetsPanel.add(datasetLabelsPanel, BorderLayout.NORTH);
    datasetsPanel.add(datasetTable.getComponent(), BorderLayout.CENTER);
    datasetsPanel.setMinimumSize(new Dimension(200, 221));
    datasetsPanel.setPreferredSize(new Dimension(1000, 221));
    datasetsPanel.setMaximumSize(new Dimension(2000, 221));

    final DescriptionPanelFactory descriptionPanelFactory = new DescriptionPanelFactory();
    final JPanel datasetDescriptionPanel = descriptionPanelFactory.create(
        datasetModel,
        d -> "<font size=\"-1\">" //$NON-NLS-1$
            + new DataSetDescriptionTextFactory().create(d)
            + "</font></body></html>", //$NON-NLS-1$
        contentPanel.getBackground(),
        180,
        datasetCreateDateModel,
        datasetLicenceModel);

    if (!this.datasets.isEmpty()) {
      final Dataset dataset = this.datasets.get(0);
      dataSetSelectionModel.setSelectedObject(dataset);
      datasetModel.set(dataset);
      datasetCreateDateModel.set(dataset.getMetadata_created());
      datasetLicenceModel.set(createLicense(dataset));
    }

    datasetTable.getTableModel().addListModelListener(new IChangeableListListener<Dataset>() {

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<Dataset> object) {
        IChangeableListListener.super.objectsAdded(indeces, object);
      }

      @Override
      public void objectsChanged(final Iterable<Dataset> oldObjects, final Iterable<Dataset> newObjects) {
        CkanSearchContentPane.this.dataSetResultsConsumer.consume(newObjects);
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

    this.datasetIdentifierDistributor.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        Optional
            .of(CkanSearchContentPane.this.datasetIdentifierDistributor.get())
            .convert(
                i -> Streams.of(datasetTable.getTableModel().values()).first(d -> Objects.equals(i, d.getId())).get())
            .consume(d -> datasetTable.getSelectionModel().setSelectedObject(d));
      }
    });

    final ObjectListTable<Resource> resourcesTable = new ResourceTableFactory(
        this.isQueryEnabledModel,
        this.resourceOpenConsumer,
        this.zoomToConsumer).create(this.description, datasetModel);

    final IObjectTableModel<Resource> resourceTableModel = resourcesTable.getTableModel();
    final ISelectionModel<Resource> resourceSelectionModel = resourcesTable.getSelectionModel();

    final JPanel resourcesPanel = new JPanel();
    resourcesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    resourcesPanel.setLayout(new BorderLayout());
    resourcesPanel.add(createLabelPanel(Messages.resources), BorderLayout.NORTH);
    resourcesPanel.add(resourcesTable.getComponent(), BorderLayout.CENTER);
    resourcesPanel.setMinimumSize(new Dimension(200, 158));
    resourcesPanel.setPreferredSize(new Dimension(1000, 158));
    resourcesPanel.setMaximumSize(new Dimension(2000, 158));

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

    final IObjectModel<Resource> resourceModel = new ObjectModel<>();

    final JPanel resourceDescriptionPanel = descriptionPanelFactory.create(
        resourceModel,
        r -> "<font size=\"-1\">" //$NON-NLS-1$
            + new ResourceDescriptionTextFactory().create(r)
            + "</font></body></html>", //$NON-NLS-1$
        contentPanel.getBackground(),
        100,
        resourceCreateDateModel,
        null);

    if (!resourcesTable.getTableModel().isEmpty()) {
      final Resource resource = resourcesTable.getTableModel().get(0);
      resourceSelectionModel.setSelectedObject(resource);
      resourceModel.set(resource);
      resourceCreateDateModel.set(resource.getCreated());
    }

    final JPanel choosePane = new JPanel();
    choosePane.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
    choosePane.setLayout(new BoxLayout(choosePane, BoxLayout.PAGE_AXIS));
    choosePane.add(datasetsPanel);
    choosePane.add(datasetDescriptionPanel);
    choosePane.add(resourcesPanel);
    choosePane.add(resourceDescriptionPanel);

    final IObjectField<String> queryField = new StringFieldBuilder()
        .setModel(queryModel)
        .setColumns(30)
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
                    CkanSearchContentPane.this.envelopeModel,
                    formatsTable.getTableModel(),
                    groupsTable.getTableModel(),
                    tagsTable.getTableModel(),
                    organizationsTable.getTableModel(),
                    licensesTable.getTableModel(),
                    datasetTable.getTableModel(),
                    sortOrderModel,
                    eventModel,
                    fromDateModel,
                    toDateModel);
                return;
              }
              offsetModel.setValue(0);
            }
          }
        })
        .addActionFactory(
            (model, document, enabledDistributor, clearBlock) -> new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icons.GuiIcons.ADVANCED_SEARCH_ICON)
                .setProcedure(component -> {
                  if (offsetModel.getValue() == 0) {
                    query(
                        contentPanel,
                        this.description,
                        offsetModel,
                        resultCountModel,
                        model,
                        this.envelopeModel,
                        formatsTable.getTableModel(),
                        groupsTable.getTableModel(),
                        tagsTable.getTableModel(),
                        organizationsTable.getTableModel(),
                        licensesTable.getTableModel(),
                        datasetTable.getTableModel(),
                        sortOrderModel,
                        eventModel,
                        fromDateModel,
                        toDateModel);
                    return;
                  }
                  offsetModel.setValue(0);
                })
                .build())
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
                      this.envelopeModel,
                      formatsTable.getTableModel(),
                      groupsTable.getTableModel(),
                      tagsTable.getTableModel(),
                      organizationsTable.getTableModel(),
                      licensesTable.getTableModel(),
                      datasetTable.getTableModel(),
                      sortOrderModel,
                      eventModel,
                      fromDateModel,
                      toDateModel);
                  return;
                }
                offsetModel.setValue(0);
              })
              .build();
        })
        .build();

    this.envelopeDistributor.addChangeListener(this.envelopListener);

    this.useEnvelopDistributor.addChangeListener(
        () -> If.isTrue(this.useEnvelopDistributor.get()).excecute(
            () -> this.envelopeModel.set(this.envelopeDistributor.get())));

    final ObjectModel<IGuiIcon> envelopeLinkIconModel = new ObjectModel<>();

    final IChangeableObjectListener envelopeLinkIconController = () -> {
      if (this.isEnvelopeLinkEnabledModel.get()) {
        if (this.isEnvelopeLinkActiveModel.get()) {
          envelopeLinkIconModel
              .set(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.NETWORK_TRANSMIT_RECEIVE);
        } else {
          envelopeLinkIconModel.set(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.NETWORK_IDLE);
        }
      } else {
        envelopeLinkIconModel
            .set(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.NETWORK_OFFLINE);
      }
    };
    envelopeLinkIconController.objectChanged();
    this.isEnvelopeLinkEnabledModel.addChangeListener(envelopeLinkIconController);
    this.isEnvelopeLinkActiveModel.addChangeListener(envelopeLinkIconController);
    final IBooleanModel isRemoveEnvelopeEnabledModel = new BooleanModel(this.envelopeModel.get() != null);
    final JPanel queryPanel = new JPanel();
    queryPanel.add(queryField.getComponent());
    queryPanel.add(createGap());
    queryPanel.add(
        new GenericObjectFieldBuilder<Envelope>()
            .setColumns(5)
            .setToolTip("envelope")
            .setModel(this.envelopeModel)
            .setToStringConverter(c -> Optional.of(c).convert(v -> "# #").get())
            .setToolTipFactory(
                (validationResult, text) -> Optional
                    .of(this.envelopeModel.get())
                    .convert(e -> CkanUtilities.toString(e))
                    .getOr(() -> "Envelope"))
            .addActionFactory(
                (model, document, enabledDistributor, clearBlock) -> new ConfigurableActionBuilder()
                    .setIcon(net.anwiba.commons.swing.icons.GuiIcons.UNDO_ZOOM_ICON)
                    .setTooltip("Set current map envelope")
                    .setEnabledDistributor(isTakeCurrentEnvelopeEnabledModel)
                    .setProcedure(c -> {
                      try {
                        this.isQueryEnabledModel.set(false);
                        this.isEnvelopeLinkActiveModel.set(false);
                      } finally {
                        this.isQueryEnabledModel.set(true);
                      }
                      this.envelopeModel.set(this.envelopeDistributor.get());
                    })
                    .build())
            .addActionFactory((model, document, enabledDistributor, clearBlock) -> {
              return new ConfigurableActionBuilder()
                  .setIcon(envelopeLinkIconModel.get())
                  .setIconModel(envelopeLinkIconModel)
                  .setEnabledDistributor(this.isEnvelopeLinkEnabledModel)
                  .setTooltip("Link with map")
                  .setProcedure(c -> {
                    this.isEnvelopeLinkActiveModel.set(!this.isEnvelopeLinkActiveModel.get());
                  })
                  .build();
            })
            .addClearAction(ObjectListTableMessages.clear)
            .build()
            .getComponent());
    queryPanel.add(createGap());

    queryPanel.add(
        new GenericObjectFieldBuilder<Event>()
            .setColumns(7)
            .setToolTip("event")
            .setModel(eventModel)
            .setToStringConverter(c -> Optional.of(c).convert(v -> v.name().toLowerCase()).get())
            .addActionFactory(
                (model, document, enabledDistributor, clearBlock) -> new ConfigurableActionBuilder()
                    .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.VIEW_WRAPPED)
                    .setProcedure(component -> {
                      final Event event = model.get();
                      final Event[] values = Event.values();
                      final int ordinal = (event.ordinal() + 1) % values.length;
                      model.set(values[ordinal]);
                    })
                    .build())
            .build()
            .getComponent());

    queryPanel.add(
        new GenericObjectFieldBuilder<LocalDateTime>()
            .setColumns(12)
            .setToolTip("form")
            .setModel(fromDateModel)
            .setToStringConverter(
                c -> Optional.of(c).convert(v -> SystemDateTimeUtilities.toStringAtUserTimeZone(v)).get())
            .addActionFactory(
                (model, document, enabledDistributor, clearBlock) -> new ConfigurableActionBuilder()
                    .setIcon(net.anwiba.commons.swing.icons.GuiIcons.DATE_ICON)
                    .setProcedure(
                        component -> new ConfigurableDialogLauncher()
                            .setDocumentModality()
                            .setCancleOkButtonDialog()
                            .enableCloseOnEscape()
                            .setMessage(new MessageBuilder().setText("From").build())
                            .setIcon(net.anwiba.commons.swing.icons.GuiIcons.DATE_ICON)
                            .setTitle("From")
                            .setPreferences(this.preferences.node("date"))
                            .setContentPaneFactory((o, p) -> new LocalDateTimeContentPane(model))
                            .launch(component))
                    .build())
            .addClearAction(ObjectListTableMessages.clear)
            .build()
            .getComponent());
    queryPanel.add(
        new GenericObjectFieldBuilder<LocalDateTime>()
            .setColumns(12)
            .setToolTip("until")
            .setModel(toDateModel)
            .setToStringConverter(
                c -> Optional.of(c).convert(v -> SystemDateTimeUtilities.toStringAtUserTimeZone(v)).get())
            .addActionFactory(
                (model, document, enabledDistributor, clearBlock) -> new ConfigurableActionBuilder()
                    .setIcon(net.anwiba.commons.swing.icons.GuiIcons.DATE_ICON)
                    .setProcedure(
                        component -> new ConfigurableDialogLauncher()
                            .setDocumentModality()
                            .setCancleOkButtonDialog()
                            .enableCloseOnEscape()
                            .setMessage(new MessageBuilder().setText("Until").build())
                            .setIcon(net.anwiba.commons.swing.icons.GuiIcons.DATE_ICON)
                            .setTitle("Until")
                            .setPreferences(this.preferences.node("date"))
                            .setContentPaneFactory((o, p) -> new LocalDateTimeContentPane(model))
                            .launch(component))
                    .build())
            .addClearAction(ObjectListTableMessages.clear)
            .build()
            .getComponent());
    queryPanel.add(createGap());
    queryPanel.add(
        new JButton(
            new ConfigurableActionBuilder()
                .setIcon(net.anwiba.commons.swing.icons.GuiIcons.EDIT_CLEAR_LIST)
                .setTooltip(Messages.reset)
                .setProcedure(c -> {
                  this.isQueryEnabledModel.set(false);
                  try {
                    queryField.getModel().set(null);
                    fromDateModel.set(null);
                    toDateModel.set(null);
                    this.envelopeModel.set(null);
                    formatsTable.getTableModel().removeAll();
                    groupsTable.getTableModel().removeAll();
                    tagsTable.getTableModel().removeAll();
                    organizationsTable.getTableModel().removeAll();
                    licensesTable.getTableModel().removeAll();
                    this.isEnvelopeLinkActiveModel.set(false);
                  } finally {
                    this.isQueryEnabledModel.set(true);
                  }
                  if (offsetModel.getValue() == 0) {
                    query(
                        contentPanel,
                        this.description,
                        offsetModel,
                        resultCountModel,
                        queryField.getModel(),
                        this.envelopeModel,
                        formatsTable.getTableModel(),
                        groupsTable.getTableModel(),
                        tagsTable.getTableModel(),
                        organizationsTable.getTableModel(),
                        licensesTable.getTableModel(),
                        datasetTable.getTableModel(),
                        sortOrderModel,
                        eventModel,
                        fromDateModel,
                        toDateModel);
                    return;
                  }
                  offsetModel.setValue(0);
                })
                .build()));

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
            queryModel,
            sortOrderModel,
            eventModel,
            fromDateModel,
            toDateModel,
            this.envelopeModel));

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
            queryModel,
            sortOrderModel,
            eventModel,
            fromDateModel,
            toDateModel,
            this.envelopeModel));

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
                queryModel,
                sortOrderModel,
                eventModel,
                fromDateModel,
                toDateModel,
                this.envelopeModel));

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
                queryModel,
                sortOrderModel,
                eventModel,
                fromDateModel,
                toDateModel,
                this.envelopeModel));

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
            queryModel,
            sortOrderModel,
            eventModel,
            fromDateModel,
            toDateModel,
            this.envelopeModel));

    offsetModel.addChangeListener(() -> {
      if (!this.isQueryEnabledModel.get()) {
        return;
      }
      query(
          contentPanel,
          this.description,
          offsetModel,
          resultCountModel,
          queryModel,
          this.envelopeModel,
          formatsTable.getTableModel(),
          groupsTable.getTableModel(),
          tagsTable.getTableModel(),
          organizationsTable.getTableModel(),
          licensesTable.getTableModel(),
          datasetTable.getTableModel(),
          sortOrderModel,
          eventModel,
          fromDateModel,
          toDateModel);
    });

    eventModel.addChangeListener(() -> {
      if (!this.isQueryEnabledModel.get() || (fromDateModel.get() == null && toDateModel.get() == null)) {
        return;
      }
      query(
          contentPanel,
          this.description,
          offsetModel,
          resultCountModel,
          queryModel,
          this.envelopeModel,
          formatsTable.getTableModel(),
          groupsTable.getTableModel(),
          tagsTable.getTableModel(),
          organizationsTable.getTableModel(),
          licensesTable.getTableModel(),
          datasetTable.getTableModel(),
          sortOrderModel,
          eventModel,
          fromDateModel,
          toDateModel);
    });

    sortOrderModel.addChangeListener(() -> {
      if (!this.isQueryEnabledModel.get()) {
        return;
      }
      query(
          contentPanel,
          this.description,
          offsetModel,
          resultCountModel,
          queryModel,
          this.envelopeModel,
          formatsTable.getTableModel(),
          groupsTable.getTableModel(),
          tagsTable.getTableModel(),
          organizationsTable.getTableModel(),
          licensesTable.getTableModel(),
          datasetTable.getTableModel(),
          sortOrderModel,
          eventModel,
          fromDateModel,
          toDateModel);
    });

    final IChangeableObjectListener queryExecutingListener = () -> {
      if (!this.isQueryEnabledModel.get()) {
        return;
      }
      if (offsetModel.getValue() == 0) {
        query(
            contentPanel,
            this.description,
            offsetModel,
            resultCountModel,
            queryModel,
            this.envelopeModel,
            formatsTable.getTableModel(),
            groupsTable.getTableModel(),
            tagsTable.getTableModel(),
            organizationsTable.getTableModel(),
            licensesTable.getTableModel(),
            datasetTable.getTableModel(),
            sortOrderModel,
            eventModel,
            fromDateModel,
            toDateModel);
        return;
      }
      offsetModel.setValue(0);
    };

    fromDateModel.addChangeListener(queryExecutingListener);
    toDateModel.addChangeListener(queryExecutingListener);

    this.envelopeModel.addChangeListener(() -> {
      isRemoveEnvelopeEnabledModel.set(this.envelopeModel.get() != null);
      if (!this.isQueryEnabledModel.get()) {
        return;
      }
      if (offsetModel.getValue() == 0) {
        query(
            contentPanel,
            this.description,
            offsetModel,
            resultCountModel,
            queryModel,
            this.envelopeModel,
            formatsTable.getTableModel(),
            groupsTable.getTableModel(),
            tagsTable.getTableModel(),
            organizationsTable.getTableModel(),
            licensesTable.getTableModel(),
            datasetTable.getTableModel(),
            sortOrderModel,
            eventModel,
            fromDateModel,
            toDateModel);
        return;
      }
      offsetModel.setValue(0);
    });

    dataSetSelectionModel.addSelectionListener(event -> {
      if (event.getSource().isEmpty()) {
        datasetModel.set(null);
        resourceTableModel.set(new ArrayList<>());
        return;
      }
      final Dataset dataset = event.getSource().getSelectedObjects().iterator().next();
      datasetModel.set(dataset);
      resourceTableModel.set(new ArrayList<>(Arrays.asList(dataset.getResources())));
    });

    datasetModel.addChangeListener(() -> {
      this.dataSetConsumer.consume(datasetModel.get());
      Optional
          .of(datasetModel.get()) //
          .consume(d -> {
            datasetCreateDateModel.set(d.getMetadata_created());
            datasetLicenceModel.set(createLicense(d));
          })
          .or(() -> {
            datasetCreateDateModel.set(null);
            datasetLicenceModel.set(null);
          });
    });

    resourceSelectionModel.addSelectionListener(event -> {
      if (event.getSource().isEmpty()) {
        resourceModel.set(null);
        return;
      }
      resourceModel.set(event.getSource().getSelectedObjects().iterator().next());
    });

    resourceModel.addChangeListener(() -> {
      final Resource resource = resourceModel.get();
      Optional
          .of(resource) //
          .consume(r -> resourceCreateDateModel.set(r.getCreated()))
          .or(() -> resourceCreateDateModel.set(null));
    });

    return contentPanel;
  }

  private License createLicense(final Dataset dataset) {
    return Optional.of(dataset).convert(d -> {
      final License license = new License();
      license.setId(dataset.getLicense_id());
      license.setTitle(new I18String(dataset.getLicense_title()));
      license.setUrl(dataset.getLicense_url());
      return license;
    }).get();
  }

  private Component createGap() {
    final JPanel gap = new JPanel();
    gap.setSize(2, 1);
    return gap;
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
      @SuppressWarnings("hiding") final IObjectModel<Envelope> envelopeModel,
      final IObjectTableModel<String> formatsTableModel,
      final IObjectTableModel<Group> groupTableModel,
      final IObjectTableModel<Tag> tagTableModel,
      final IObjectTableModel<Organization> organizationTableModel,
      final IObjectTableModel<License> licenseTableModel,
      final IObjectTableModel<Dataset> datasetTableModel,
      final IObjectModel<ISortOrder> sortOrderModel,
      final IObjectModel<Event> eventModel,
      final IObjectModel<LocalDateTime> fromDateModel,
      final IObjectModel<LocalDateTime> toDateModel) {
    final int offset = offsetModel.getValue();
    try {
      final IPackageSearchCondition condition = new PackageSearchConditionBuilder()
          .setQuery(queryModel.get())
          .setEvent(eventModel.get())
          .setFromDate(fromDateModel.get())
          .setToDate(toDateModel.get())
          .setEnvelope(envelopeModel.get())
          .setOrganizations(IterableUtilities.asList(organizationTableModel.values()))
          .setLicenses(IterableUtilities.asList(licenseTableModel.values()))
          .setTags(IterableUtilities.asList(tagTableModel.values()))
          .setGroups(IterableUtilities.asList(groupTableModel.values()))
          .setFormats(IterableUtilities.asList(formatsTableModel.values()))
          .setOffset(offset)
          .setRows(this.numberOfResultRows)
          .setSortOrder(sortOrderModel.get())
          .build();
      final IPackageSearchResult result = new ProgressDialogLauncher<>(
          (progressMonitor, canceler) -> this.packageQueryExecutor.query(canceler, connectionDescription, condition))
              .launch(parentCompoment);
      this.packageQueryConditionModel.set(condition);
      resultCountModel.setValue(result.getCount());
      datasetTableModel.set(result.getResults());
    } catch (final HttpRequestException exception) {
      logger.log(ILevel.DEBUG, Messages.format_query_faild, exception);
      new ConfigurableDialogLauncher() //
          .setTitle(Messages.formats)
          .setMessage(
              new MessageBuilder()
                  .setText(Messages.format_query_faild)
                  .setDescription(exception.getStatusCode() + " " + exception.getStatusText()) //$NON-NLS-1$
                  .setError()
                  .build())
          .setContentPaneFactory((o, p) -> new TextContentPane(exception.getContentAsString()))
          .setCloseButtonDialog()
          .launch(parentCompoment);
    } catch (final IOException exception) {
      logger.log(ILevel.DEBUG, Messages.format_query_faild, exception);
      new MessageDialogLauncher()
          .error()
          .title(Messages.formats)
          .text(Messages.format_query_faild)
          .description(exception.getMessage())
          .throwable(exception)
          .launch(parentCompoment);
    } catch (final CanceledException exception) {
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
      final IObjectModel<String> queryModel,
      final IObjectModel<ISortOrder> sortOrderModel,
      final IObjectModel<Event> eventModel,
      final IObjectModel<LocalDateTime> fromDateModel,
      final IObjectModel<LocalDateTime> toDateModel,
      @SuppressWarnings("hiding") final IObjectModel<Envelope> envelopeModel) {
    return new IChangeableListListener<T>() {

      private void execute() {
        if (!CkanSearchContentPane.this.isQueryEnabledModel.get()) {
          return;
        }
        if (offsetModel.getValue() == 0) {
          query(
              contentPanel,
              connectionDescription,
              offsetModel,
              resultCountModel,
              queryModel,
              envelopeModel,
              formatsTable.getTableModel(),
              groupsTable.getTableModel(),
              tagsTable.getTableModel(),
              organizationsTable.getTableModel(),
              licensesTable.getTableModel(),
              datasetsTable.getTableModel(),
              sortOrderModel,
              eventModel,
              fromDateModel,
              toDateModel);
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

  @Override
  public void dispose() {
    this.envelopeDistributor.removeChangeListener(this.envelopListener);
  }
}
