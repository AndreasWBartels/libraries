/*
 * #%L
 *
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
package net.anwiba.spatial.ckan.query;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import net.anwiba.commons.datasource.connection.IHttpConnectionDescription;
import net.anwiba.commons.http.IObjectRequestExecutor;
import net.anwiba.commons.http.IObjectRequestExecutorBuilderFactory;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.IResultProducer;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.PackageSearchResultResponse;
import net.anwiba.spatial.ckan.json.schema.v1_0.PackageShowResultResponse;
import net.anwiba.spatial.ckan.marshaller.CkanJsonResponseUnmarshallerFactory;
import net.anwiba.spatial.ckan.request.PackageRequestBuilder;
import net.anwiba.spatial.ckan.request.PackageRequestBuilder.PackageSearchRequestBuilder;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;

public class PackageQueryExecutor implements IPackageQueryExecutor {

  private final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory;
  private final IFormatsNameConverter formatsNameConverter;

  public PackageQueryExecutor(
      final IObjectRequestExecutorBuilderFactory requestExecutorBuilderFactory,
      final IFormatsNameConverter formatsNameConverter) {
    this.requestExecutorBuilderFactory = requestExecutorBuilderFactory;
    this.formatsNameConverter = formatsNameConverter;
  }

  @Override
  public IPackageSearchResult query(
      final ICanceler canceler,
      final IHttpConnectionDescription description,
      final IPackageSearchCondition condition)
      throws CanceledException,
      IOException {
    try {
      final IResultProducer<PackageSearchResultResponse> responseProducer = (
          c,
          url,
          statusCode,
          statusMessage,
          contentType,
          contentEncoding,
          inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(PackageSearchResultResponse.class)
              .unmarshal(
                  inputStream);

      final PackageSearchRequestBuilder requestBuilder = PackageRequestBuilder
          .search(description.getUrl()) //
          .authentication(description.getUserName(), description.getPassword());
      Streams.of(description.getParameters().parameters()).foreach(p -> requestBuilder.parameter(p));
      requestBuilder //
          .start(condition.getOffset())
          .rows(condition.getRows());

      condition.getOrganizations().forEach(v -> requestBuilder.organization(v.getName().toString()));
      condition.getLicenses().forEach(v -> Optional.of(getLicenseTitle(v)).consume(t -> requestBuilder.license(t)));
      condition.getTags().forEach(v -> requestBuilder.tags(v.getName().toString()));
      condition.getGroups().forEach(v -> requestBuilder.group(v.getName().toString()));
      condition.getFormats().forEach(v -> {
        this.formatsNameConverter.convert(v).forEach(f -> requestBuilder.resourceFormat(f));
      });
      requestBuilder.event(condition.getEvent(), condition.getFromDate(), condition.getToDate());
      Optional.of(condition.getEnvelope()).consume(e -> requestBuilder.envelope(e));
      Optional.of(condition.getSortOrder()).consume(e -> requestBuilder.sortOrder(e));
      requestBuilder
          .query(StringUtilities.isNullOrTrimmedEmpty(condition.getQueryString()) ? null : condition.getQueryString());
      final IRequest request = requestBuilder.build();
      try (final IObjectRequestExecutor<PackageSearchResultResponse> executor = this.requestExecutorBuilderFactory
          .<PackageSearchResultResponse>create()
          .setResultProducer(responseProducer)
          .addResultProducer(
              (statusCode1, contentType1) -> new HashSet<>(Arrays.asList(409, 400, 500)).contains(statusCode1)
                  && contentType1 != null
                  && contentType1.startsWith("application/json"), //$NON-NLS-1$
              responseProducer)
          .build()) {
        final PackageSearchResultResponse response = executor.execute(canceler, request);
        return response.isSuccess()
            ? new PackageSearchResult(Arrays.asList(response.getResult().getResults()), response.getResult().getCount())
            : new PackageSearchResult(response.getError().getMessage());
      }
    } catch (CreationException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }

  @Override
  public IPackageSearchResult query(
      final ICanceler canceler,
      final IHttpConnectionDescription description,
      final String identifier)
      throws CanceledException,
      IOException {
    try {
      final IResultProducer<PackageShowResultResponse> responseProducer = (
          c,
          url,
          statusCode,
          statusMessage,
          contentType,
          contentEncoding,
          inputStream) -> new CkanJsonResponseUnmarshallerFactory().create(PackageShowResultResponse.class)
              .unmarshal(
                  inputStream);
      final IRequest request = PackageRequestBuilder
          .show(description.getUrl())
          .identifier(identifier) //
          .authentication(description.getUserName(), description.getPassword())
          .build();
      try (final IObjectRequestExecutor<PackageShowResultResponse> executor = this.requestExecutorBuilderFactory
          .<PackageShowResultResponse>create()
          .setResultProducer(responseProducer)
          .addResultProducer(
              (statusCode1, contentType1) -> new HashSet<>(Arrays.asList(409, 400, 500)).contains(statusCode1)
                  && contentType1 != null
                  && contentType1.startsWith("application/json"), //$NON-NLS-1$
              responseProducer)
          .build()) {
        final PackageShowResultResponse response = executor.execute(canceler, request);
        return response.isSuccess()
            ? new PackageSearchResult(Arrays.asList(response.getResult()), 1)
            : new PackageSearchResult(response.getError().getMessage());
      }
    } catch (CreationException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }

  private String getLicenseTitle(final License license) {
    return Optional
        .of(license)
        .convert(l -> l.getId()) //
        .or(() -> CkanUtilities.toString(license))
        .get();
  }
}
