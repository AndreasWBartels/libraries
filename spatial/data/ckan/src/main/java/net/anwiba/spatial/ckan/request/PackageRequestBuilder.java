/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.spatial.ckan.request;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.http.Authentication;
import net.anwiba.commons.http.IAuthentication;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.RequestBuilder;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.If;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.request.sort.ISortOrder;
import net.anwiba.spatial.ckan.request.sort.ISortOrderTerm;
import net.anwiba.spatial.ckan.request.sort.ISortOrderVisitor;
import net.anwiba.spatial.ckan.request.time.Event;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.ckan.values.Envelope;

@SuppressWarnings("nls")
public class PackageRequestBuilder {

  enum Type {
    SHOW, SEARCH
  }

  static final class Builder {

    Envelope maximumEnvelope = new Envelope(-180, -90, 180, 90);

    private Type type = null;
    private String key = null;
    private final String url;
    private int rows = 1000;
    private int start = 0;
    private final Map<String, SearchCondition> searchConditions = new HashMap<>();
    private IAuthentication authentication;
    private final List<IParameter> parameters = new ArrayList<>();
    private String query;
    private Envelope envelope;
    private final List<String> fields = new LinkedList<>();
    private String identifier;
    private boolean useDefaultSchema = false;
    // sort (string) – sorting of the search results. Optional. Default: 'relevance asc, metadata_modified desc'.
    // As per the solr documentation, this is a comma-separated string of field names and sort-orderings.
    private ISortOrder sortOrder = null;

    public Builder(final String url) {
      this.url = url;
    }

    public void setSortOrder(final ISortOrder sortOrder) {
      this.sortOrder = sortOrder;
    }

    public void setEvent(final Event event, final LocalDateTime from, final LocalDateTime to) {
      if (event == null) {
        setModified(from, to);
        return;
      }
      switch (event) {
        case CREATED: {
          setCreated(from, to);
          return;
        }
        case MODIFIED: {
          setModified(from, to);
          return;
        }
      }
    }

    public void setCreated(final LocalDateTime from, final LocalDateTime to) {
      final String value = convertToString(from, to);
      if (value == null) {
        return;
      }
      final SearchCondition searchCondition = new DateSearchCondition("metadata_created", new ArrayList<String>());
      searchCondition.getValues().add(value);
      addSearchCondition(searchCondition);
    }

    public void setModified(final LocalDateTime from, final LocalDateTime to) {
      final String value = convertToString(from, to);
      if (value == null) {
        return;
      }
      final SearchCondition searchCondition = new DateSearchCondition("metadata_modified", new ArrayList<String>());
      searchCondition.getValues().add(value);
      addSearchCondition(searchCondition);
    }

    private String convertToString(final LocalDateTime from, final LocalDateTime to) {
      if (from == null && to != null) {
        return "[* TO " + toString(to) + "]";
      }
      if (from != null && to == null) {
        return "[" + toString(from) + " TO NOW]";
      }
      if (from != null && to != null) {
        return "[" + toString(from) + " TO " + toString(to) + "]";
      }
      return null;
    }

    private String toString(final LocalDateTime from) {
      return ZonedDateTime.of(from, ZoneId.of("Z")).format(DateTimeFormatter.ISO_INSTANT);
    }

    public void setUseDefaultSchema(final boolean useDefaultSchema) {
      this.useDefaultSchema = useDefaultSchema;
    }

    public void setType(final Type type) {
      this.type = type;
    }

    public void setKey(final String key) {
      this.key = key;
    }

    public void setStart(final int start) {
      this.start = start;
    }

    public void setRows(final int rows) {
      this.rows = rows;
    }

    public void setAuthentication(final IAuthentication authentication) {
      this.authentication = authentication;
    }

    public void addParameter(final IParameter parameter) {
      this.parameters.add(parameter);
    }

    public void addSearchCondition(final SearchCondition searchCondition) {
      if (!this.searchConditions.containsKey(searchCondition.getField())) {
        this.searchConditions.put(searchCondition.getField(), searchCondition);
        return;
      }
      this.searchConditions.get(searchCondition.getField()).getValues().addAll(searchCondition.getValues());
    }

    public void setOperator(final String field, final Operator value) {
      if (!this.searchConditions.containsKey(field)) {
        final SearchCondition searchCondition = new SearchCondition(field, Collections.emptyList());
        this.searchConditions.put(field, searchCondition);
        return;
      }
      this.searchConditions.get(field).setOperator(value);
    }

    public void setQuery(final String query) {
      this.query = query;
    }

    public void setEnvelope(final Envelope envelope) {
      if (envelope == null) {
        this.envelope = envelope;
        return;
      }
      if (!this.maximumEnvelope.intersects(envelope)) {
        this.envelope = null;
        return;
      }
      this.envelope = new Envelope(
          envelope.getMinX() < -180
              ? -180
              : envelope.getMinX(),
          envelope.getMinY() < -90
              ? -90
              : envelope.getMinY(),
          envelope.getMaxX() > 180
              ? 180
              : envelope.getMaxX(),
          envelope.getMaxY() > 90
              ? 90
              : envelope.getMaxY())

      ;
    }

    public IRequest build() throws CreationException {
      switch (this.type) {
        case SEARCH: {

          // sort (string) – sorting of the search results. Optional.
          // Default: 'relevance asc, metadata_modified desc'.
          // As per the solr documentation, this is a comma-separated string of
          // field names and sort-orderings.

          // Do not use the REST API, it's old and deprecated. Use the Action API.
          // You can get the results you need using the package_search action, eg for datasets modified since 1st August
          // 2026:
          // http://demo.ckan.org/api/action/package_search?fq=metadata_modified:[2016-08-01T00:00:00.000Z TO NOW]
          // For datasets created since then:
          // http://demo.ckan.org/api/action/package_search?fq=metadata_created:[2016-08-01T00:00:00.000Z TO NOW]
          // You can of course combine these with any other filters, pagination, sorting, etc. Check the docs to find
          // more.
          //
          // Links:
          // http://docs.ckan.org/en/latest/api/index.html
          // http://docs.ckan.org/en/latest/api/index.html#ckan.logic.action.get.package_search
          // http://demo.ckan.org/api/action/package_search?fq=metadata_modified:[2016-08-01T00:00:00.000Z%20TO%20NOW]
          // http://demo.ckan.org/api/action/package_search?fq=metadata_created:[2016-08-01T00:00:00.000Z%20TO%20NOW]

          // https://www.europeandataportal.eu/data/en/api/3/action/package_search?start=0&rows=11&fq=(metadata_created:([2018-01-01T13:51:18.954Z+TO+NOW])+OR+metadata_modified:([2018-01-01T13:51:18.954Z+TO+NOW]))+AND+res_format:(WMS+OR+wms)&q=quellen

          // +spatial_geom:"Intersects(ENVELOPE(-180.0, -197.067608389, 46.937575538, 27.0033887294))"

          final RequestBuilder builder = RequestBuilder
              .get(CkanUtilities.getBaseUrl(this.url, "package_search"))
              .query("start", String.valueOf(this.start))
              .query("rows", String.valueOf(this.rows));
          If
              .isTrue(this.useDefaultSchema)
              .excecute(() -> builder.query("use_default_schema", Boolean.valueOf(this.useDefaultSchema).toString()));
          If
              .isTrue(!this.searchConditions.isEmpty())
              .excecute(() -> builder.query("fq", toString(this.searchConditions.values())));
          Optional.of(this.query).convert(q -> builder.query("q", q));
          Optional.of(this.envelope).consume(a -> builder.query("ext_bbox", toString(a)));
          If.isTrue(!this.fields.isEmpty()).excecute(() -> builder.query("fl", toString(this.fields)));
          Optional.of(this.sortOrder).consume(o -> builder.query("sort", toString(o)));
          Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k));
          Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
          return builder.build();
        }
        case SHOW: {
          final RequestBuilder builder =
              RequestBuilder.get(CkanUtilities.getBaseUrl(this.url, "package_search")).query(this.parameters);
          If
              .isTrue(this.useDefaultSchema)
              .excecute(() -> builder.query("use_default_schema", Boolean.valueOf(this.useDefaultSchema).toString()));
          Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k));
          Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
          Optional.of(this.identifier).consume(a -> builder.query("id", this.identifier));
          return builder.build();
        }
      }
      throw new IllegalStateException();
    }

    @SuppressWarnings("hiding")
    private String toString(final ISortOrder sortOrder) {
      final ISortOrderVisitor<String, RuntimeException> visitor = new ISortOrderVisitor<>() {

        @Override
        public String visitTerm(final ISortOrderTerm term) {
          return term.getAspect() + " " + term.getOrder().name();
        }

        @Override
        public String visitList(final Iterable<ISortOrderTerm> terms) {
          return Streams
              .of(terms)
              .aggregate(
                  (String) null,
                  (i, t) -> i == null
                      ? visitTerm(t)
                      : i + ", " + visitTerm(t))
              .getOr(() -> "relevance asc, metadata_modified desc");
        }
      };
      return sortOrder.accept(visitor);
    }

    private String toString(final Envelope envelop) {
      final StringBuilder builder = new StringBuilder();
      builder.append(envelop.getMinX());
      builder.append(",");
      builder.append(envelop.getMinY());
      builder.append(",");
      builder.append(envelop.getMaxX());
      builder.append(",");
      builder.append(envelop.getMaxY());
      return builder.toString();
    }

    private String toString(@SuppressWarnings("hiding") final List<String> fields) {
      final StringBuilder builder = new StringBuilder();
      boolean flag = false;
      builder.append("[");
      for (final String field : fields) {
        if (flag) {
          builder.append(", ");
        }
        builder.append("'");
        builder.append(field);
        builder.append("'");
        flag = true;
      }
      builder.append("]");
      return builder.toString();
    }

    private String toString(final Collection<SearchCondition> conditions) {
      final StringBuilder builder = new StringBuilder();
      boolean flag = false;
      for (final SearchCondition searchCondition : conditions) {
        if (flag) {
          builder.append(" AND ");
        }
        builder.append(toString(searchCondition));
        flag = true;
      }
      return builder.toString();
    }

    private String toString(final SearchCondition searchCondition) {
      if (searchCondition instanceof DateSearchCondition) {
        return toString((DateSearchCondition) searchCondition);
      }
      final StringBuilder builder = new StringBuilder();
      boolean flag = false;
      builder.append(searchCondition.getField());
      builder.append(":(");
      final List<String> values = searchCondition.getValues();
      for (final String value : values) {
        if (flag) {
          builder.append(" ");
          builder.append(searchCondition.getOperator());
          builder.append(" ");
        }
        if (value.contains(" ") || value.contains(":")) {
          builder.append("\"");
          builder.append(value);
          builder.append("\"");
        } else {
          builder.append(value);
        }
        flag = true;
      }
      builder.append(")");
      return builder.toString();
    }

    private String toString(final DateSearchCondition searchCondition) {
      final StringBuilder builder = new StringBuilder();
      boolean flag = false;
      builder.append(searchCondition.getField());
      builder.append(":(");
      final List<String> values = searchCondition.getValues();
      for (final String value : values) {
        if (flag) {
          builder.append(" ");
          builder.append(searchCondition.getOperator());
          builder.append(" ");
        }
        builder.append(value);
        flag = true;
      }
      builder.append(")");
      return builder.toString();
    }

    public void addResultField(final String field) {
      this.fields.add(field);
    }

    public void setIdentifier(final String identifier) {
      this.identifier = identifier;
    }
  }

  public enum Operator {
    AND, OR
  }

  public static Operator and() {
    return Operator.AND;
  }

  public static Operator or() {
    return Operator.OR;
  }

  private static class SearchCondition {

    private Operator operator = Operator.OR;
    final private String field;
    final private List<String> values = new ArrayList<>();

    private SearchCondition(final String field, final List<String> values) {
      this.field = field;
      this.values.addAll(values);
    }

    public void setOperator(final Operator operator) {
      this.operator = operator;
    }

    public Operator getOperator() {
      return this.operator;
    }

    public List<String> getValues() {
      return this.values;
    }

    public String getField() {
      return this.field;
    }

  }

  private static class DateSearchCondition extends SearchCondition {

    private DateSearchCondition(final String field, final List<String> values) {
      super(field, values);
    }
  }

  private static class PackageSearchConditionBuilder {

    private final String fieldName;
    private final List<String> values = new LinkedList<>();
    private final Builder builder;

    PackageSearchConditionBuilder(final Builder builder, final String fieldName) {
      this.builder = builder;
      this.fieldName = fieldName;
    }

    public PackageSearchConditionBuilder value(final String value) {
      this.values.add(value);
      return this;
    }

    public PackageSearchConditionBuilder operator(final Operator value) {
      this.builder.setOperator(this.fieldName, value);
      return this;
    }

    public PackageSearchRequestBuilder build() {
      final SearchCondition searchCondition = new SearchCondition(this.fieldName, new ArrayList<String>());
      searchCondition.getValues().addAll(this.values);
      this.builder.addSearchCondition(searchCondition);
      return new PackageSearchRequestBuilder(this.builder);
    }

  }

  public static class PackageSearchRequestBuilder {

    private final Builder builder;
    // https://offenedaten.de/api/3/action/package_search?fq=+(res_format:(GeoJSON%20OR%20SHP)%20AND%20tags:api)&rows=1000

    public PackageSearchRequestBuilder(final Builder builder) {
      this.builder = builder;
    }

    public PackageSearchRequestBuilder parameter(final IParameter parameter) {
      this.builder.addParameter(parameter);
      return this;
    }

    public PackageSearchRequestBuilder sortOrder(final ISortOrder sortOrder) {
      this.builder.setSortOrder(sortOrder);
      return this;
    }

    public PackageSearchRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.builder.setAuthentication(new Authentication(userName, password));
      return this;
    }

    public PackageSearchRequestBuilder key(final String key) {
      this.builder.setKey(key);
      return this;
    }

    public PackageSearchRequestBuilder rows(final int rows) {
      this.builder.setRows(rows);
      return this;
    }

    public PackageSearchRequestBuilder start(final int start) {
      this.builder.setStart(start);
      return this;
    }

    public PackageSearchRequestBuilder result(final String field) {
      this.builder.addResultField(field);
      return this;
    }

    public PackageSearchRequestBuilder query(final String query) {
      this.builder.setQuery(query);
      return this;
    }

    private PackageSearchConditionBuilder field(final String field) {
      return new PackageSearchConditionBuilder(this.builder, field);
    }

    public PackageSearchRequestBuilder field(final String field, final String value) {
      return field(field).value(value).build();
    }

    public PackageSearchRequestBuilder operator(final String field, final Operator operator) {
      return field(field).operator(operator).build();
    }

    private PackageSearchConditionBuilder group() {
      return field("groups");
    }

    public PackageSearchRequestBuilder group(final Operator operator) {
      return group().operator(operator).build();
    }

    public PackageSearchRequestBuilder group(final String value) {
      return group().value(value).build();
    }

    private PackageSearchConditionBuilder license() {
      return field("license_id");
    }

    public PackageSearchRequestBuilder license(final String value) {
      return license().value(value).build();
    }

    public PackageSearchRequestBuilder license(final Operator operator) {
      return license().operator(operator).build();
    }

    private PackageSearchConditionBuilder resourceFormat() {
      return field("res_format");
    }

    public PackageSearchRequestBuilder resourceFormat(final String value) {
      return resourceFormat().value(value).build();
    }

    public PackageSearchRequestBuilder resourceFormat(final Operator operator) {
      return resourceFormat().operator(operator).build();
    }

    private PackageSearchConditionBuilder organization() {
      return field("organization");
    }

    public PackageSearchRequestBuilder organization(final String value) {
      return organization().value(value).build();
    }

    public PackageSearchRequestBuilder organization(final Operator operator) {
      return organization().operator(operator).build();
    }

    private PackageSearchConditionBuilder tags() {
      return field("tags");
    }

    public PackageSearchRequestBuilder tags(final String value) {
      return tags().value(value).build();
    }

    public PackageSearchRequestBuilder tags(final Operator operator) {
      return tags().operator(operator).build();
    }

    public PackageSearchRequestBuilder setUseDefaultSchema(final boolean useDefaultSchema) {
      this.builder.setUseDefaultSchema(useDefaultSchema);
      return this;
    }

    public PackageSearchRequestBuilder created(final LocalDateTime from, final LocalDateTime to) {
      this.builder.setCreated(from, to);
      return this;
    }

    public PackageSearchRequestBuilder modified(final LocalDateTime from, final LocalDateTime to) {
      this.builder.setModified(from, to);
      return this;
    }

    public PackageSearchRequestBuilder event(final Event event, final LocalDateTime from, final LocalDateTime to) {
      this.builder.setEvent(event, from, to);
      return this;
    }

    public IRequest build() throws CreationException {
      return this.builder.build();
    }

    public void envelope(final Envelope envelope) {
      this.builder.setEnvelope(envelope);
    }

  }

  public static class PackageShowRequestBuilder {

    private final Builder builder;

    public PackageShowRequestBuilder(final Builder builder) {
      this.builder = builder;
    }

    public PackageShowRequestBuilder identifier(final String identifier) {
      this.builder.setIdentifier(identifier);
      return this;
    }

    public IRequest build() throws CreationException {
      return this.builder.build();
    }

    public PackageShowRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.builder.setAuthentication(new Authentication(userName, password));
      return this;
    }

    public PackageShowRequestBuilder parameter(final IParameter parameter) {
      this.builder.addParameter(parameter);
      return this;
    }

    public PackageShowRequestBuilder parameters(final IParameters parameters) {
      parameters.forEach(p -> this.builder.addParameter(p));
      return this;
    }

    public void setUseDefaultSchema(final boolean useDefaultSchema) {
      this.builder.setUseDefaultSchema(useDefaultSchema);
    }

  }

  public static PackageShowRequestBuilder show(final String url) {
    return new PackageRequestBuilder(url).show();
  }

  public static PackageSearchRequestBuilder search(final String url) {
    return new PackageRequestBuilder(url).search();
  }

  private final Builder builder;

  private PackageRequestBuilder(final String url) {
    this.builder = new Builder(url);
  }

  private PackageShowRequestBuilder show() {
    this.builder.setType(Type.SHOW);
    return new PackageShowRequestBuilder(this.builder);
  }

  private PackageSearchRequestBuilder search() {
    this.builder.setType(Type.SEARCH);
    return new PackageSearchRequestBuilder(this.builder);
  }

}
