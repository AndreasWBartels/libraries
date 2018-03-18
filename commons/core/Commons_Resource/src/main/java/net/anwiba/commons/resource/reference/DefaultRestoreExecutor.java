/*
 * #%L
 * *
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
package net.anwiba.commons.resource.reference;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class DefaultRestoreExecutor implements IRestoreExecutor {

  @Override
  public IResourceReference restore(final IResourceReference resourceReference) throws IOException {
    try {
      if (!ResourceReferenceUtilities.isFileSystemResource(resourceReference)) {
        throw new IOException("Unsupported resource"); //$NON-NLS-1$
      }
      final File file = ResourceReferenceUtilities.getFile(resourceReference);
      final File backup = new File(file.getPath() + "~"); //$NON-NLS-1$
      if (backup.exists()) {
        Files.copy(backup.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        if (!file.exists()) {
          throw new IOException("Coudn't restore file " + file); //$NON-NLS-1$
        }
        return new ResourceReferenceFactory().create(backup);
      }
      return null;
    } catch (final URISyntaxException exception) {
      throw new IOException(exception.getLocalizedMessage(), exception);
    }
  }
}
