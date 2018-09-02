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
package net.anwiba.spatial.ckan.request;

import java.time.LocalDateTime;
import java.util.List;

import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.ckan.values.Envelope;

public final class PackageQueryCondition implements IPackageQueryCondition {
  private final String queryString;
  private final Envelope envelope;
  private final LocalDateTime fromDate;
  private final LocalDateTime toDate;
  private final List<Organization> organizations;
  private final List<Group> groups;
  private final List<Tag> tags;
  private final List<String> formats;
  private final List<License> licenses;

  public PackageQueryCondition(
      final String queryString,
      final Envelope envelope,
      final LocalDateTime fromDate,
      final LocalDateTime toDate,
      final List<Organization> organizations,
      final List<Group> groups,
      final List<Tag> tags,
      final List<String> formats,
      final List<License> licenses) {
    this.queryString = queryString;
    this.envelope = envelope;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.organizations = organizations;
    this.groups = groups;
    this.tags = tags;
    this.formats = formats;
    this.licenses = licenses;
  }

  @Override
  public LocalDateTime getToDate() {
    return this.toDate;
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
  public LocalDateTime getFromDate() {
    return this.fromDate;
  }

  @Override
  public List<String> getFormats() {
    return this.formats;
  }

  @Override
  public Envelope getEnvelope() {
    return this.envelope;
  }
}
