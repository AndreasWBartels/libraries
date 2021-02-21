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
import java.util.List;

import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.ckan.request.sort.ISortOrder;
import net.anwiba.spatial.ckan.request.time.Event;
import net.anwiba.spatial.ckan.values.Envelope;

public final class PackageSearchCondition implements IPackageSearchCondition {
  private final String queryString;
  private final Envelope envelope;
  private final LocalDateTime fromDate;
  private final LocalDateTime toDate;
  private final List<Organization> organizations;
  private final List<Group> groups;
  private final List<Tag> tags;
  private final List<String> formats;
  private final List<License> licenses;
  private final ISortOrder sortOrder;
  private final Event event;
  private final int offset;
  private final int rows;

  public PackageSearchCondition(
      final String queryString,
      final Envelope envelope,
      final Event event,
      final LocalDateTime fromDate,
      final LocalDateTime toDate,
      final List<Organization> organizations,
      final List<Group> groups,
      final List<Tag> tags,
      final List<String> formats,
      final List<License> licenses,
      final int offset,
      final int rows,
      final ISortOrder sortOrder) {
    this.queryString = queryString;
    this.envelope = envelope;
    this.event = event;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.organizations = organizations;
    this.groups = groups;
    this.tags = tags;
    this.formats = formats;
    this.licenses = licenses;
    this.offset = offset;
    this.rows = rows;
    this.sortOrder = sortOrder;
  }

  @Override
  public List<Tag> getTags() {
    return this.tags;
  }

  @Override
  public String getQueryString() {
    return this.queryString;
  }

  @Override
  public List<Organization> getOrganizations() {
    return this.organizations;
  }

  @Override
  public List<License> getLicenses() {
    return this.licenses;
  }

  @Override
  public List<Group> getGroups() {
    return this.groups;
  }

  @Override
  public Event getEvent() {
    return this.event;
  }

  @Override
  public LocalDateTime getFromDate() {
    return this.fromDate;
  }

  @Override
  public LocalDateTime getToDate() {
    return this.toDate;
  }

  @Override
  public List<String> getFormats() {
    return this.formats;
  }

  @Override
  public Envelope getEnvelope() {
    return this.envelope;
  }

  @Override
  public ISortOrder getSortOrder() {
    return this.sortOrder;
  }

  @Override
  public int getOffset() {
    return this.offset;
  }

  @Override
  public int getRows() {
    return this.rows;
  }
}
