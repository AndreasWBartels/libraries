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

import java.io.File;
import java.net.URI;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;

public class FileSystemConnectionDescription extends AbstractConnectionDescription
    implements
    IFileSystemConnectionDescription {

  private static final long serialVersionUID = 5088280043009120362L;
  private final File file;

  public FileSystemConnectionDescription(final File file) {
    super(DataSourceType.FILE);
    Ensure.ensureArgumentNotNull(file);
    if (!file.isDirectory()) {
      throw new IllegalArgumentException("file isn't a directory " + file.getAbsolutePath()); //$NON-NLS-1$
    }
    this.file = file;
  }

  @Override
  public IResourceReference getResourceReference() {
    return new ResourceReferenceFactory().create(this.file);
  }

  @Override
  public String getUrl() {
    return getURI().toASCIIString();
  }

  @Override
  public File getFile() {
    return this.file;
  }

  @Override
  public URI getURI() {
    return getFile().toURI();
  }

  @Override
  public String toString() {
    return getFile().getPath();
  }

  @Override
  public String getFormat() {
    return "Folder"; //$NON-NLS-1$
  }

  @Override
  public int hashCode() {
    return 31 * ((this.file == null) ? 0 : this.file.hashCode());
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof IFileSystemConnectionDescription)) {
      return false;
    }
    final IFileSystemConnectionDescription other = (IFileSystemConnectionDescription) object;
    return ObjectUtilities.equals(getURI(), other.getURI())
        && ObjectUtilities.equals(getDataSourceType(), other.getDataSourceType());
  }
}
