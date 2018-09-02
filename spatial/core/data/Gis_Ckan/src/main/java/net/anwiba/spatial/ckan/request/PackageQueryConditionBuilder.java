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
import java.util.ArrayList;
import java.util.List;

import net.anwiba.spatial.ckan.json.schema.v1_0.Group;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.Tag;
import net.anwiba.spatial.ckan.values.Envelope;

public class PackageQueryConditionBuilder {

  private String queryString;
  private Envelope envelope;
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
  private final List<Organization> organizations = new ArrayList<>();
  private final List<Group> groups = new ArrayList<>();
  private final List<Tag> tags = new ArrayList<>();
  private final List<String> formats = new ArrayList<>();
  private final List<License> licenses = new ArrayList<>();

  public PackageQueryConditionBuilder() {
  }

  public PackageQueryConditionBuilder(final IPackageQueryCondition condition) {
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

  public IPackageQueryCondition build() {
    return new PackageQueryCondition(
        this.queryString,
        this.envelope,
        this.fromDate,
        this.toDate,
        this.organizations,
        this.groups,
        this.tags,
        this.formats,
        this.licenses);
  }

  public PackageQueryConditionBuilder setFormats(final List<String> formats) {
    this.formats.addAll(formats);
    return this;
  }

  public PackageQueryConditionBuilder setGroups(final List<Group> groups) {
    this.groups.addAll(groups);
    return this;
  }

  public PackageQueryConditionBuilder setTags(final List<Tag> tags) {
    this.tags.addAll(tags);
    return this;
  }

  public PackageQueryConditionBuilder setOrganizations(final List<Organization> organizations) {
    this.organizations.addAll(organizations);
    return this;
  }

  public PackageQueryConditionBuilder setLicenses(final List<License> licenses) {
    this.licenses.addAll(licenses);
    return this;
  }

  public PackageQueryConditionBuilder setQuery(final String queryString) {
    this.queryString = queryString;
    return this;
  }

  public PackageQueryConditionBuilder setFromDate(final LocalDateTime fromDate) {
    this.fromDate = fromDate;
    return this;
  }

  public PackageQueryConditionBuilder setToDate(final LocalDateTime toDate) {
    this.toDate = toDate;
    return this;
  }

  public PackageQueryConditionBuilder setEnvelope(final Envelope envelope) {
    this.envelope = envelope;
    return this;
  }
}
