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

import java.util.Collections;
import java.util.List;

import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;

public final class OrganizationSearchResult implements IOrganizationSearchResult {

  private final List<Organization> results;
  private final String message;
  private final Integer resultCount;
  private final boolean isSuccessful;

  public OrganizationSearchResult(final List<Organization> results, final Integer resultCount) {
    this.results = results;
    this.message = null;
    this.resultCount = resultCount;
    this.isSuccessful = true;
  }

  public OrganizationSearchResult(final String message) {
    this.results = Collections.emptyList();
    this.message = message;
    this.resultCount = 0;
    this.isSuccessful = false;
  }

  @Override
  public List<Organization> getResults() {
    return this.results;
  }

  @Override
  public int getCount() {
    return this.resultCount;
  }

  @Override
  public boolean isSuccessful() {
    return this.isSuccessful;
  }

  @Override
  public String getMessage() {
    return this.message;
  }
}
