/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.datasource.connection;

import java.net.URI;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.utilities.io.url.IAuthentication;

public class FileConnectionDescription extends AbstractConnectionDescription implements IFileConnectionDescription {

  private static final long serialVersionUID = -931879424583598936L;
  private final URI uri;

  public FileConnectionDescription(final URI uri) {
    super(DataSourceType.FILE);
    this.uri = uri;
  }

  @Override
  public IResourceReference getResourceReference() {
    return new ResourceReferenceFactory().create(this.uri);
  }

  @Override
  public FileConnectionDescription adapt(final IAuthentication authentication) {
    return new FileConnectionDescription(this.uri);
  }

  @Override
  public URI getURI() {
    return this.uri;
  }

  @Override
  public String getUrl() {
    return this.uri.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof IConnectionDescription)) {
      return false;
    }
    final IConnectionDescription other = (IConnectionDescription) obj;
    return ObjectUtilities.equals(getURI(), other.getURI()) //
        && ObjectUtilities.equals(getDataSourceType(), other.getDataSourceType());
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(getURI());
  }

  @Override
  public DataSourceType getDataSourceType() {
    return DataSourceType.FILE;
  }

  @Override
  public String getFormat() {
    return "File"; //$NON-NLS-1$
  }

  @Override
  public IAuthentication getAuthentication() {
    return null;
  }
}
