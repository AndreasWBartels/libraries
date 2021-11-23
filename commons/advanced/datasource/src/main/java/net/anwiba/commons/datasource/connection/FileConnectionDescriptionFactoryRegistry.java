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

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.utilities.registry.AbstractApplicableRegistry;

public class FileConnectionDescriptionFactoryRegistry
    extends
    AbstractApplicableRegistry<File, IRegisterableFileConnectionDescriptionFactory>
    implements
    IFileConnectionDescriptionFactory, IFileConnectionDescriptionFactoryRegistry {

  public FileConnectionDescriptionFactoryRegistry() {
    super(null);
  }

  @Override
  public boolean isApplicable(final File context) {
    return get(context) != null;
  }

  @Override
  public IConnectionDescription create(final File file) throws CreationException {
    final IFileConnectionDescriptionFactory connectionDescriptionFactory = get(file);
    if (connectionDescriptionFactory == null) {
      throw new IllegalArgumentException();
    }
    return connectionDescriptionFactory.create(file);
  }

} 
