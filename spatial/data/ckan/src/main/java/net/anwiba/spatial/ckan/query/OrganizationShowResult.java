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

import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;

public final class OrganizationShowResult implements IOrganizationShowResult {

  private final Organization result;
  private final String message;
  private final boolean isSuccessful;

  public OrganizationShowResult(final Organization result) {
    this.result = result;
    this.message = null;
    this.isSuccessful = true;
  }

  public OrganizationShowResult(final String message) {
    this.result = null;
    this.message = message;
    this.isSuccessful = false;
  }

  @Override
  public Organization getResult() {
    return this.result;
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
