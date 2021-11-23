/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels
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
package net.anwiba.commons.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.thread.cancel.ICanceler;

public final class ImageReader implements IImageReader {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageReader.class);
  private final IImageContainerFactory imageContainerFactory;

  public ImageReader(final IImageContainerFactory imageContainerFactory) {
    super();
    this.imageContainerFactory = imageContainerFactory;
  }

  @Override
  public IImageContainer
      read(final ICanceler canceler, final IResourceReference resourceReference)
          throws CanceledException,
          IOException {
    canceler.check();
    return this.imageContainerFactory.create(resourceReference);
  }

  @Override
  public IImageContainer read(final ICanceler canceler, final InputStream inputStream)
      throws CanceledException,
      IOException {
    canceler.check();
    return this.imageContainerFactory.create(inputStream);
  }

  @Override
  public IImageContainer read(final ICanceler canceler, final File file)
      throws CanceledException,
      IOException {
    canceler.check();
    return this.imageContainerFactory.create(file);
  }
}
