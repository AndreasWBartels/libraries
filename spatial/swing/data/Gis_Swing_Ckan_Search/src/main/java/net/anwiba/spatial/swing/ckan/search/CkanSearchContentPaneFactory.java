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

import java.awt.Window;
import java.util.List;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.dialog.IContentPaneFactory;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.ckan.query.IPackageQueryExecutor;
import net.anwiba.spatial.ckan.query.IPackageSearchCondition;
import net.anwiba.spatial.ckan.values.Envelope;

public final class CkanSearchContentPaneFactory implements IContentPaneFactory {
  private final List<Dataset> datasets;
  private final int results;
  private final IHttpConnectionDescription description;
  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;
  private final IResourceOpenConsumer resourceOpenConsumer;
  private final IPackageQueryExecutor datasetQueryExecutor;
  private final int numberOfResultRows;
  private final IObjectDistributor<Envelope> envelopeDistributor;
  private final IZoomToConsumer zoomToConsumer;
  private final IDataSetResultsConsumer dataSetResultsConsumer;
  private final IDataSetConsumer dataSetConsumer;
  private final IObjectDistributor<String> datasetIdentifierDistributor;
  private final IObjectModel<IPackageSearchCondition> packageQueryConditionModel;
  private final IObjectReceiver<Envelope> envelopeReceiver;
  private final IObjectDistributor<Envelope> envelopeSetter;

  public CkanSearchContentPaneFactory(
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
      final IPackageQueryExecutor datasetQueryExecutor,
      final IHttpConnectionDescription description,
      final int numberOfResultRows,
      final List<Dataset> datasets,
      final int results) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
    this.packageQueryConditionModel = packageQueryConditionModel;
    this.dataSetResultsConsumer = dataSetResultsConsumer;
    this.dataSetConsumer = dataSetConsumer;
    this.resourceOpenConsumer = resourceOpenConsumer;
    this.zoomToConsumer = zoomToConsumer;
    this.datasetIdentifierDistributor = datasetIdentifierDistributor;
    this.envelopeDistributor = envelopeDistributor;
    this.envelopeReceiver = envelopeReceiver;
    this.envelopeSetter = envelopeSetter;
    this.datasetQueryExecutor = datasetQueryExecutor;
    this.numberOfResultRows = numberOfResultRows;
    this.datasets = datasets;
    this.results = results;
    this.description = description;
  }

  @Override
  public IContentPanel create(final Window owner, final IPreferences preferences) {
    return new CkanSearchContentPane(
        preferences,
        this.requestExecutorBuilderFactory,
        this.packageQueryConditionModel,
        this.dataSetResultsConsumer,
        this.dataSetConsumer,
        this.resourceOpenConsumer,
        this.zoomToConsumer,
        this.datasetIdentifierDistributor,
        this.envelopeDistributor,
        this.envelopeReceiver,
        this.envelopeSetter,
        this.datasetQueryExecutor,
        this.description,
        this.numberOfResultRows,
        this.datasets,
        this.results);
  }
}
