/*
 * #%L
 * anwiba commons tools
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

package net.anwiba.tools.icons.configuration;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import net.anwiba.commons.resource.utilities.FileUtilities;
import net.anwiba.commons.utilities.lang.ClassLoaderUtilities;

public final class ImageExistsValidator implements IImageExistsValidator {

  private final IOutput output;
  private final List<File> imageResources;

  public ImageExistsValidator(final List<File> imageResources, final IOutput output) {
    this.imageResources = imageResources;
    this.output = output;
  }

  @Override
  public boolean exists(final String parent, final IIconSizesConfiguration configuration, final String image) {

    final boolean isSmallIconAvailable = exists(parent, configuration.getFolder(), configuration.small(), image);
    final boolean isMediumIconAvailable = exists(parent, configuration.getFolder(), configuration.medium(), image);
    final boolean isLargeIconAvailable = exists(parent, configuration.getFolder(), configuration.large(), image);
    if (!isLargeIconAvailable || !isMediumIconAvailable || !isSmallIconAvailable) {
      this.output
          .info("checked '" + new File(new File(configuration.getFolder(), configuration.small().path()), image) + "' found: " + isSmallIconAvailable); //$NON-NLS-1$//$NON-NLS-2$
      this.output
          .info("checked '" + new File(new File(configuration.getFolder(), configuration.medium().path()), image) + "' found: " + isMediumIconAvailable); //$NON-NLS-1$//$NON-NLS-2$
      this.output
          .info("checked '" + new File(new File(configuration.getFolder(), configuration.large().path()), image) + "' found: " + isLargeIconAvailable); //$NON-NLS-1$//$NON-NLS-2$
    }
    if (!(isLargeIconAvailable || isMediumIconAvailable || isSmallIconAvailable)) {
      final String message = MessageFormat.format("no image {0} available", image); //$NON-NLS-1$
      this.output.warn(message);
      return false;
    }
    if (!isLargeIconAvailable) {
      MessageFormat.format("no image {0} available", image); //$NON-NLS-1$
      final String message = MessageFormat.format("large image {0} is not available", image); //$NON-NLS-1$
      this.output.warn(message);
      return true;
    }
    if (!isMediumIconAvailable) {
      final String message = MessageFormat.format("medium image {0} is not available", image); //$NON-NLS-1$
      this.output.warn(message);
      return true;
    }
    if (!isSmallIconAvailable) {
      final String message = MessageFormat.format("small image {0} is not available", image); //$NON-NLS-1$
      this.output.warn(message);
      return true;
    }
    return true;
  }

  private boolean exists(
      final String parent,
      final String folder,
      final IIconSizeConfiguration configuration,
      final String image) {
    final File imageFile = new File(new File(baseFolder(parent, folder), configuration.path()), image);
    if (imageFile.exists()) {
      return true;
    }
    for (final File imageResource : this.imageResources) {
      if (imageResource.isDirectory()) {
        if (new File(new File(baseFolder(imageResource, folder), configuration.path()), image).exists()) {
          return true;
        }
        continue;
      }
      if (FileUtilities.hasExtension(imageResource, "jar")) { //$NON-NLS-1$
        if (ClassLoaderUtilities.contains(
            imageResource.toURI(),
            basePathString("icons", folder) + configuration.path() + "/" + image)) { //$NON-NLS-1$ //$NON-NLS-2$
          return true;
        }
        continue;
      }

    }
    return false;
  }

  private String basePathString(final String parent, final String folder) {
    if (folder == null) {
      return parent;
    }
    return parent + "/" + folder + "/"; //$NON-NLS-1$//$NON-NLS-2$
  }

  public File baseFolder(final File imageResource, final String folder) {
    if (folder == null) {
      return imageResource;
    }
    return new File(imageResource, folder);
  }

  public File baseFolder(final String parent, final String folder) {
    if (folder == null) {
      return new File(parent);
    }
    return new File(parent, folder);
  }
}
