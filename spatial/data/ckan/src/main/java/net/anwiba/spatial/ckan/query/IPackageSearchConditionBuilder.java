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

public interface IPackageSearchConditionBuilder {

  IPackageSearchCondition build();

  IPackageSearchConditionBuilder setOffset(int offset);

  IPackageSearchConditionBuilder setRows(int rows);

  IPackageSearchConditionBuilder setFormats(List<String> formats);

  IPackageSearchConditionBuilder setGroups(List<Group> groups);

  IPackageSearchConditionBuilder setTags(List<Tag> tags);

  IPackageSearchConditionBuilder setOrganizations(List<Organization> organizations);

  IPackageSearchConditionBuilder setLicenses(List<License> licenses);

  IPackageSearchConditionBuilder setQuery(String queryString);

  IPackageSearchConditionBuilder setModified();

  IPackageSearchConditionBuilder setCreated();

  IPackageSearchConditionBuilder setEvent(Event event);

  IPackageSearchConditionBuilder setFromDate(LocalDateTime fromDate);

  IPackageSearchConditionBuilder setToDate(LocalDateTime toDate);

  IPackageSearchConditionBuilder setEnvelope(Envelope envelope);

  IPackageSearchConditionBuilder setSortOrder(ISortOrder sortOrder);

  IPackageSearchConditionBuilder setCreatedDescentOrder();

  IPackageSearchConditionBuilder setCreatedAscentOrder();

  IPackageSearchConditionBuilder setModifiedDescentOrder();

  IPackageSearchConditionBuilder setModifiedAscentOrder();

  IPackageSearchConditionBuilder setEnvelope(double minX, double minY, double maxX, double maxY);

}
