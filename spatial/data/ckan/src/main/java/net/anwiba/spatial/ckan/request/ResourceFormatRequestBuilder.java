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
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;

public class ResourceFormatRequestBuilder {

  public static ResourceFormatListRequestBuilder list(final String url) {
    return new ResourceFormatListRequestBuilder(url);
  }

  public static class ResourceFormatListRequestBuilder {

    private final String url;
    private String key = null;
    private IAuthentication authentication = null;
    private String stringPart = ""; //$NON-NLS-1$
    private int limit = 5;

    private ResourceFormatListRequestBuilder(final String url) {
      this.url = url;
    }

    public ResourceFormatListRequestBuilder key(@SuppressWarnings("hiding") final String key) {
      this.key = key;
      return this;
    }

    public ResourceFormatListRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.authentication = new Authentication(userName, password);
      return this;
    }

    public ResourceFormatListRequestBuilder setLimit(final int limit) {
      this.limit = limit;
      return this;
    }

    public ResourceFormatListRequestBuilder setSearchStringPart(final String string) {
      this.stringPart = string;
      return this;
    }

    public IRequest build() throws CreationException {
      final RequestBuilder builder = RequestBuilder
          .get(CkanUtilities.getBaseUrl(this.url, "format_autocomplete")) //$NON-NLS-1$
          .query("q", this.stringPart) //$NON-NLS-1$
          .query("limit", String.valueOf(this.limit)); //$NON-NLS-1$
      Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k)); //$NON-NLS-1$
      Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
      return builder.build();
    }

  }

}
