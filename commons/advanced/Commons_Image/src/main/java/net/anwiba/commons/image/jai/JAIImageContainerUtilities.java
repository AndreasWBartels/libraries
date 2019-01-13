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
package net.anwiba.commons.image.jai;

import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.ImageLayout;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ScaleDescriptor;

@SuppressWarnings("nls")
public class JAIImageContainerUtilities {

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
    return JAI.create("Crop", pb);
  }

  public static RenderedOp translate(final RenderedImage renderedImage, final float x, final float y) {
    final ParameterBlock params = new ParameterBlock();
    params.addSource(renderedImage);
    params.add(x);
    params.add(y);
    return JAI.create("Translate", params);
  }

  public static RenderedOp scale(final RenderedImage renderedOp, final float factor) {
    return scale(renderedOp, factor, factor);
  }

  public static RenderedOp scale(final RenderedImage renderedImage, final float xFactor, final float yFactor) {
    return ScaleDescriptor.create(renderedImage, xFactor, yFactor, 0.0f, 0.0f, new InterpolationNearest(), null);
  }

  public static PlanarImage toGrayScale(final RenderedImage image) {

    final ColorModel colorModel = new ComponentColorModel(
        ColorSpace.getInstance(ColorSpace.CS_GRAY),
        false,
        false,
        Transparency.OPAQUE,
        DataBuffer.TYPE_BYTE);
    final ImageLayout imageLayout = new ImageLayout(image);
    imageLayout.setColorModel(colorModel);
    imageLayout.setSampleModel(colorModel.createCompatibleSampleModel(image.getWidth(), image.getHeight()));
    final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);

    final int expandedNumBands = OpImage.getExpandedNumBands(image.getSampleModel(), image.getColorModel()) + 1;

    final double[][] matrix;
    switch (expandedNumBands) {
      case 5: {
        matrix = new double[][]{ { .114D, 0.587D, 0.299D, 0.0D, 0.0D } };
        break;
      }
      case 4: {
        matrix = new double[][]{ { .114D, 0.587D, 0.299D, 0.0D } };
        break;
      }
      case 3: {
        matrix = new double[][]{ { .114D, 0.587D, 0.299D } };
        break;
      }
      case 2: {
        matrix = new double[][]{ { 1D, 0.0D } };
        break;
      }
      default: {
        matrix = new double[][]{ { 1D } };
      }
    }

    final ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(matrix);

    return JAI.create("BandCombine", pb, renderingHints);
  }
}