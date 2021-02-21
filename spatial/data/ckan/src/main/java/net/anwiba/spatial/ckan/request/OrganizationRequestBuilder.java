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

import net.anwiba.commons.http.Authentication;
import net.anwiba.commons.http.IAuthentication;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.RequestBuilder;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.If;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;

public class OrganizationRequestBuilder {

  public static OrganizationShowRequestBuilder show(final String url) {
    return new OrganizationShowRequestBuilder(url);
  }

  public static OrganizationListRequestBuilder list(final String url) {
    return new OrganizationListRequestBuilder(url);
  }

  public static class OrganizationShowRequestBuilder {

    private final String url;
    private String identifier;
    private Authentication authentication;
    private String key;

    public OrganizationShowRequestBuilder(final String url) {
      this.url = url;
    }

    public OrganizationShowRequestBuilder identifier(final String identifier) {
      this.identifier = identifier;
      return this;
    }

    public OrganizationShowRequestBuilder key(@SuppressWarnings("hiding") final String key) {
      this.key = key;
      return this;
    }

    public OrganizationShowRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.authentication = new Authentication(userName, password);
      return this;
    }

    public IRequest build() throws CreationException {
      final RequestBuilder builder = RequestBuilder.get(CkanUtilities.getBaseUrl(this.url, "organization_show")); //$NON-NLS-1$
      Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k)); //$NON-NLS-1$
      Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
      Optional.of(this.identifier).consume(a -> builder.query("id", this.identifier));
      return builder.build();
    }
  }

  public static class OrganizationListRequestBuilder {

    private final String url;
    private String key = null;
    private IAuthentication authentication = null;
    private int limit = -1;
    private boolean allFields = false;
    private boolean extras = false;

    private OrganizationListRequestBuilder(final String url) {
      this.url = url;
    }

    public OrganizationListRequestBuilder key(@SuppressWarnings("hiding") final String key) {
      this.key = key;
      return this;
    }

    public OrganizationListRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.authentication = new Authentication(userName, password);
      return this;
    }

    public OrganizationListRequestBuilder setLimit(final int limit) {
      this.limit = limit;
      return this;
    }

    public OrganizationListRequestBuilder setOnlyNameField() {
      this.allFields = false;
      this.extras = false;
      return this;
    }

    public OrganizationListRequestBuilder setAllFields() {
      this.allFields = true;
      return this;
    }

    public OrganizationListRequestBuilder setExtraFields() {
      this.extras = true;
      return this;
    }

    public IRequest build() throws CreationException {
      final RequestBuilder builder = RequestBuilder.get(CkanUtilities.getBaseUrl(this.url, "organization_list")); //$NON-NLS-1$
      If.isTrue(this.limit > 0).excecute(() -> builder.query("limit", String.valueOf(this.limit))); //$NON-NLS-1$
      If.isTrue(this.allFields).excecute(() -> builder.query("all_fields", "True")); //$NON-NLS-1$ //$NON-NLS-2$
      If.isTrue(this.extras).excecute(() -> builder.query("include_extras", "True")); //$NON-NLS-1$ //$NON-NLS-2$
      Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k)); //$NON-NLS-1$
      Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
      return builder.build();
    }

  }

}
