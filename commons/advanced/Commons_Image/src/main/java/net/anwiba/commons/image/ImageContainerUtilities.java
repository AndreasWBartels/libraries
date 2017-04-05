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
package net.anwiba.commons.image;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ScaleDescriptor;

public class ImageContainerUtilities {

  public static RenderedOp crop(
      final RenderedImage renderedImage,
      final float x,
      final float y,
      final float width,
      final float height) {
    final ParameterBlock pb = new ParameterBlock();
    pb.addSource(renderedImage); // The source image
    pb.add(x);
    pb.add(y);
    pb.add(width);
    pb.add(height);
    return JAI.create("Crop", pb); //$NON-NLS-1$
  }

  public static RenderedOp translate(final RenderedImage renderedImage, final float x, final float y) {
    final ParameterBlock params = new ParameterBlock();
    params.addSource(renderedImage);
    params.add(x);
    params.add(y);
    return JAI.create("Translate", params); //$NON-NLS-1$
  }

  public static RenderedOp scale(final RenderedImage renderedOp, final float factor) {
    return scale(renderedOp, factor, factor);
  }

  public static RenderedOp scale(final RenderedImage renderedImage, final float xFactor, final float yFactor) {
    return ScaleDescriptor.create(renderedImage, xFactor, yFactor, 0.0f, 0.0f, new InterpolationNearest(), null);
  }
}