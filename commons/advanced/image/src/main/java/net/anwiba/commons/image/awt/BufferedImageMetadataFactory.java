/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.image.awt;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

public class BufferedImageMetadataFactory {

  public BufferedImageMetadata create(final BufferedImage image) {
    final BufferedImageMetadata metadata = new BufferedImageMetadata(
        image.getWidth(),
        image.getHeight(),
        image.getColorModel().getNumColorComponents(),
        image.getColorModel().getNumComponents(),
        image.getColorModel().getColorSpace().getType(),
        image.getColorModel().getTransferType(),
        image.getColorModel().getTransparency(),
        image.getColorModel() instanceof IndexColorModel);
    return metadata;
  }
}
