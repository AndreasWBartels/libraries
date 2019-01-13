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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.ckan.request.sort.ISortOrder;
import net.anwiba.spatial.ckan.request.sort.Order;
import net.anwiba.spatial.ckan.request.sort.SortOrderTerm;
import net.anwiba.spatial.ckan.request.time.Event;
import net.anwiba.spatial.ckan.values.Envelope;

public class PackageSearchConditionBuilder implements IPackageSearchConditionBuilder {

  private int offset = 0;
  private int rows = 10;
  private String queryString;
  private Envelope envelope;
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
  private final List<Organization> organizations = new ArrayList<>();
  private final List<Group> groups = new ArrayList<>();
  private final List<Tag> tags = new ArrayList<>();
  private final List<String> formats = new ArrayList<>();
  private final List<License> licenses = new ArrayList<>();
  private ISortOrder sortOrder;
  private Event event;

  public PackageSearchConditionBuilder() {
  }

  public PackageSearchConditionBuilder(final IPackageSearchCondition condition) {
    if (condition == null) {
      return;
    }
    this.queryString = condition.getQueryString();
    this.envelope = condition.getEnvelope();
    this.fromDate = condition.getFromDate();
    this.toDate = condition.getToDate();
    condition.getOrganizations().forEach(v -> this.organizations.add(v));
    condition.getGroups().forEach(v -> this.groups.add(v));
    condition.getTags().forEach(v -> this.tags.add(v));
    condition.getFormats().forEach(v -> this.formats.add(v));
    condition.getLicenses().forEach(v -> this.licenses.add(v));
  }

  @Override
  public IPackageSearchCondition build() {
    return new PackageSearchCondition(
        this.queryString,
        this.envelope,
        this.event,
        this.fromDate,
        this.toDate,
        this.organizations,
        this.groups,
        this.tags,
        this.formats,
        this.licenses,
        this.offset,
        this.rows,
        this.sortOrder);
  }

  @Override
  public IPackageSearchConditionBuilder setOffset(final int offset) {
    this.offset = offset;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setRows(final int rows) {
    this.rows = rows;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setFormats(final List<String> formats) {
    this.formats.addAll(formats);
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setGroups(final List<Group> groups) {
    this.groups.addAll(groups);
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setTags(final List<Tag> tags) {
    this.tags.addAll(tags);
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setOrganizations(final List<Organization> organizations) {
    this.organizations.addAll(organizations);
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setLicenses(final List<License> licenses) {
    this.licenses.addAll(licenses);
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setQuery(final String queryString) {
    this.queryString = queryString;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setEvent(final Event event) {
    this.event = event;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setCreated() {
    this.event = Event.CREATED;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setModified() {
    this.event = Event.MODIFIED;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setFromDate(final LocalDateTime fromDate) {
    this.fromDate = fromDate;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setToDate(final LocalDateTime toDate) {
    this.toDate = toDate;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setEnvelope(
      final double minX,
      final double minY,
      final double maxX,
      final double maxY) {
    this.envelope = new Envelope(minX, minY, maxX, maxY);
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setEnvelope(final Envelope envelope) {
    this.envelope = envelope;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setSortOrder(final ISortOrder sortOrder) {
    this.sortOrder = sortOrder;
    return this;
  }

  @Override
  public IPackageSearchConditionBuilder setCreatedDescentOrder() {
    return setSortOrder(new SortOrderTerm(Order.asc, "metadata_created"));
  }

  @Override
  public IPackageSearchConditionBuilder setCreatedAscentOrder() {
    return setSortOrder(new SortOrderTerm(Order.asc, "metadata_created"));
  }

  @Override
  public IPackageSearchConditionBuilder setModifiedDescentOrder() {
    return setSortOrder(new SortOrderTerm(Order.desc, "metadata_modified"));
  }

  @Override
  public IPackageSearchConditionBuilder setModifiedAscentOrder() {
    return setSortOrder(new SortOrderTerm(Order.asc, "metadata_modified"));
  }

}
