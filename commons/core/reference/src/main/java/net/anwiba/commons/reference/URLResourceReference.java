/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.reference;

import java.net.URL;
import java.util.Objects;

public class URLResourceReference implements IResourceReference {

  private static final long serialVersionUID = 1L;
  private final URL url;

  URLResourceReference(final URL url) {
    this.url = Objects.requireNonNull(url);;
  }

  @Override
  public <O, E extends Exception> O accept(final IResourceReferenceVisitor<O, E> visitor) throws E {
    return visitor.visitURLResource(this);
  }

  @Override
  public String toString() {
    return this.url.toString();
  }

  public URL getUrl() {
    return this.url;
  }

  @Override
  public int hashCode() {
    return ((this.url == null) ? 0 : this.url.toString().toUpperCase().hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof URLResourceReference)) {
      return false;
    }
    final URLResourceReference other = (URLResourceReference) obj;
    return this.url.toString().equals(other.url.toString());
  }

}