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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutor;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.IResultProducer;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.PackageSearchResultResponse;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.ckan.marshaller.CkanJsonResponseUnmarshallerFactory;
import net.anwiba.spatial.ckan.request.PackageRequestBuilder;
import net.anwiba.spatial.ckan.request.PackageRequestBuilder.PackageSearchRequestBuilder;

public class DatasetQueryExecutor {

  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;
  private final IFormatsNameConverter formatsNameConverter;

  public DatasetQueryExecutor(
      final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory,
      final IFormatsNameConverter formatsNameConverter) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
    this.formatsNameConverter = formatsNameConverter;
  }

  public ObjectPair<List<Dataset>, Integer> query(
      final ICanceler canceler,
      final IHttpConnectionDescription description,
      final List<String> formats,
      final List<Group> groups,
      final List<Tag> tags,
      final List<Organization> organizations,
      final List<License> licenses,
      final String queryString,
      final int start,
      final int rows)
      throws InterruptedException,
      IOException {
    final IResultProducer<PackageSearchResultResponse> responseProducer = (
        c,
        statusCode,
        statusMessage,
        contentType,
        contentEncoding,
        inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(PackageSearchResultResponse.class).unmarshal(
            inputStream);

    final PackageSearchRequestBuilder requestBuilder = PackageRequestBuilder
        .search(description.getUrl()) //
        .authentication(description.getUserName(), description.getPassword());
    Streams.of(description.getParameters().parameters()).foreach(p -> requestBuilder.parameter(p));
    requestBuilder //
        .start(start)
        .rows(rows);

    organizations.forEach(v -> requestBuilder.organization(v.getName().toString()));
    licenses.forEach(v -> requestBuilder.license(v.getName().toString()));
    tags.forEach(v -> requestBuilder.tags(v.getName().toString()));
    groups.forEach(v -> requestBuilder.group(v.getName().toString()));
    formats.forEach(v -> {
      this.formatsNameConverter.convert(v).forEach(f -> requestBuilder.resourceFormat(f));
    });

    requestBuilder.query(StringUtilities.isNullOrEmpty(queryString) ? null : queryString);

    final IRequest request = requestBuilder.build();

    try (final IObjectRequestExecutor<PackageSearchResultResponse> executor = this.requestExecutorBuilderFactory
        .<PackageSearchResultResponse> create()
        .setResultProducer(responseProducer)
        .build()) {
      final PackageSearchResultResponse response = executor.execute(canceler, request);
      return response.isSuccess()
          ? new ObjectPair<>(Arrays.asList(response.getResult().getResults()), response.getResult().getCount())
          : new ObjectPair<>(Collections.emptyList(), 0);
    }
  }
}
