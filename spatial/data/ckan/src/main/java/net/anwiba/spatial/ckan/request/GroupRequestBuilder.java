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

public class GroupRequestBuilder {

  public static GroupListRequestBuilder list(final String url) {
    return new GroupListRequestBuilder(url);
  }

  public static class GroupListRequestBuilder {

    private final String url;
    private String key = null;
    private IAuthentication authentication = null;
    private int limit = -1;
    private boolean allFields = false;

    private GroupListRequestBuilder(final String url) {
      this.url = url;
    }

    public GroupListRequestBuilder key(@SuppressWarnings("hiding") final String key) {
      this.key = key;
      return this;
    }

    public GroupListRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.authentication = new Authentication(userName, password);
      return this;
    }

    public GroupListRequestBuilder setLimit(final int limit) {
      this.limit = limit;
      return this;
    }

    public GroupListRequestBuilder setOnlyNameField() {
      this.allFields = false;
      return this;
    }

    public GroupListRequestBuilder setAllFields() {
      this.allFields = true;
      return this;
    }

    public IRequest build() throws CreationException {
      final RequestBuilder builder = RequestBuilder.get(CkanUtilities.getBaseUrl(this.url, "group_list")); //$NON-NLS-1$
      If.isTrue(this.limit > 0).excecute(() -> builder.query("limit", String.valueOf(this.limit))); //$NON-NLS-1$
      If.isTrue(this.allFields).excecute(() -> builder.query("all_fields", "True")); //$NON-NLS-1$ //$NON-NLS-2$
      Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k)); //$NON-NLS-1$
      Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
      return builder.build();
    }

  }

}
