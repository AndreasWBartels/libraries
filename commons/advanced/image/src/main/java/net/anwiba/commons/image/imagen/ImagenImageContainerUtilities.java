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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.image.imagen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.util.Objects;

import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.OpImage;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedImageAdapter;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.operator.BandCombineDescriptor;
import org.eclipse.imagen.operator.BandSelectDescriptor;
import org.eclipse.imagen.operator.ColorQuantizerDescriptor;
import org.eclipse.imagen.operator.CropDescriptor;
import org.eclipse.imagen.operator.InvertDescriptor;
import org.eclipse.imagen.operator.LookupDescriptor;
import org.eclipse.imagen.operator.ScaleDescriptor;
import org.eclipse.imagen.operator.TranslateDescriptor;

import net.anwiba.commons.image.ImageUtilities;

public class ImagenImageContainerUtilities {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImagenImageContainerUtilities.class);

  public static RenderedOp crop(
      final RenderingHints hints,
      final RenderedImage renderedImage,
      final float x,
      final float y,
      final float width,
      final float height) {
    Rectangle imageBounds =
        new Rectangle(renderedImage.getMinX(),
            renderedImage.getMinY(),
            renderedImage.getWidth(),
            renderedImage.getHeight());
    final Rectangle cropBounds = new Rectangle2D.Float(x, y, width, height).getBounds();
    if (cropBounds.isEmpty()) {
      return null;
    }
    if (!imageBounds.contains(cropBounds)) {
      if (!imageBounds.intersects(cropBounds)) {
        return null;
      }
      Rectangle intersection = imageBounds.intersection(cropBounds);
      return CropDescriptor
          .create(
              renderedImage,
              toFloat(intersection.x),
              toFloat(intersection.y),
              toFloat(intersection.width),
              toFloat(intersection.height),
              hints);
    }
    return CropDescriptor
        .create(
            renderedImage,
            toFloat(x),
            toFloat(y),
            toFloat(width),
            toFloat(height),
            hints);
  }

  public static RenderedOp translate(
      final RenderingHints hints,
      final RenderedImage renderedImage,
      final float x,
      final float y,
      final Interpolation interpolation) {
    return TranslateDescriptor.create(renderedImage, toFloat(x), toFloat(y), interpolation, hints);
  }

  public static RenderedOp scale(
      final RenderingHints hints,
      final RenderedImage renderedImage,
      final float xFactor,
      final float yFactor,
      final Interpolation interpolation) {
    return ScaleDescriptor
        .create(
            renderedImage,
            toFloat(xFactor),
            toFloat(yFactor),
            toFloat(0.0f),
            toFloat(0.0f),
            interpolation,
            hints);
  }

  public static PlanarImage toInverted(final RenderingHints hints, final RenderedImage source) {
    RenderedImage image = source.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY
        ? ImageUtilities.toRGBA(hints, source)
        : source;
    return InvertDescriptor.create(image, hints);
  }

  public static PlanarImage toGrayScale(final RenderingHints hints, final RenderedImage source) {
    if (source.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY) {
      return toPlanarImage(source);
    }
    RenderedImage image =
        source.getColorModel() instanceof IndexColorModel
            ? convertIndexColorModelToARGB(hints, source, (IndexColorModel) source.getColorModel())
            : source;
    final ColorModel colorModel = new ComponentColorModel(
        ColorSpace.getInstance(ColorSpace.CS_GRAY),
        false,
        false,
        Transparency.OPAQUE,
        DataBuffer.TYPE_BYTE);
    final ImageLayout imageLayout = new ImageLayout(image);
    imageLayout.setColorModel(colorModel);
    imageLayout
        .setSampleModel(
            colorModel.createCompatibleSampleModel(image.getWidth(), image.getHeight()));
    final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);
    renderingHints.add(hints);
    @SuppressWarnings("deprecation")
    final int expandedNumBands = OpImage
        .getExpandedNumBands(image.getSampleModel(), image.getColorModel())
        + 1;

    final double[][] matrix;
    switch (expandedNumBands) {
      case 5: {
        matrix = new double[][] { { .114D, 0.587D, 0.299D, 0.0D, 0.0D } };
        break;
      }
      case 4: {
        matrix = new double[][] { { .114D, 0.587D, 0.299D, 0.0D } };
        break;
      }
      case 3: {
        matrix = new double[][] { { .114D, 0.587D, 0.299D } };
        break;
      }
      case 2: {
        matrix = new double[][] { { 1D, 0.0D } };
        break;
      }
      default: {
        matrix = new double[][] { { 1D } };
      }
    }
    return BandCombineDescriptor.create(image, matrix, renderingHints);
  }

  public static PlanarImage toOpacity(
      final RenderingHints hints,
      final RenderedImage source,
      final float factor) {
    if (factor >= 1.0 || factor < 0) {
      return toPlanarImage(source);
    }
    final ColorModel sourceColorModel = source.getColorModel();
    // http://iihm.imag.fr/Docs/java/jai1_0guide/Color.doc.html
    if (sourceColorModel instanceof IndexColorModel) {
      final IndexColorModel indexColorModel = (IndexColorModel) sourceColorModel;
      final int mapSize = indexColorModel.getMapSize();
      final byte[][] data = new byte[4][mapSize];
      indexColorModel.getReds(data[0]);
      indexColorModel.getGreens(data[1]);
      indexColorModel.getBlues(data[2]);
      indexColorModel.getAlphas(data[3]);
      for (int i = 0; i < mapSize; i++) {
        int alpha = (data[3][i] < 0) ? (data[3][i] + 256) : data[3][i];
        alpha = (int) (alpha * (factor));
        data[3][i] = (byte) ((alpha > 128) ? (alpha - 256) : alpha);
      }
      final LookupTableJAI lut = new LookupTableJAI(data);
      return LookupDescriptor.create(source, lut, hints);
    }
    return toPlanarImage(ImageUtilities.toOpacity(hints, source, factor));
//    final int transferType = sourceColorModel.getTransferType();
//    if (transferType == DataBuffer.TYPE_DOUBLE
//        || transferType == DataBuffer.TYPE_FLOAT
//        || transferType == DataBuffer.TYPE_UNDEFINED) {
//      logger.log(ILevel.WARNING, "Unsupported data type: " + transferType);
//      return toPlanarImage(source);
//    }
//    if (sourceColorModel instanceof MinMaxSingleBandColorModel) {
//    }
//    RenderedImage image = sourceColorModel.getColorSpace().getType() == ColorSpace.TYPE_GRAY
//        || sourceColorModel instanceof DirectColorModel
//            ? toRGBA(hints, source)
//            : source;
//    final ColorModel colorModel = new ComponentColorModel(
//        ColorSpace.getInstance(ColorSpace.CS_sRGB),
//        true,
//        false,
//        Transparency.TRANSLUCENT,
//        DataBuffer.TYPE_BYTE);
//    final ImageLayout imageLayout = new ImageLayout(image);
//    imageLayout.setColorModel(colorModel);
//    imageLayout
//        .setSampleModel(
//            colorModel.createCompatibleSampleModel(image.getWidth(), image.getHeight()));
//    final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);
//    renderingHints.add(hints);
//    @SuppressWarnings("deprecation")
//    final int expandedNumBands = OpImage
//        .getExpandedNumBands(image.getSampleModel(), image.getColorModel())
//        + 1;
//    double[][] matrix;
//    if (expandedNumBands == 5) {
//      matrix = new double[][] {
//          { 1.0D, 0.0D, 0.0D, 0.0D, 0 },
//          { 0.0D, 1.0D, 0.0D, 0.0D, 0 },
//          { 0.0D, 0.0D, 1.0D, 0.0D, 0 },
//          { 0.0D, 0.0D, 0.0D, factor, 0 },
//      };
//    } else if (expandedNumBands == 4) {
//      matrix = new double[][] {
//          { 1.0D, 0.0D, 0.0D, 0.0D },
//          { 0.0D, 1.0D, 0.0D, 0.0D },
//          { 0.0D, 0.0D, 1.0D, 0.0D },
//          { 1.0D, 1.0D, 1.0D, factor },
//      };
//    } else if (expandedNumBands == 2) {
//      matrix = new double[][] {
//          { 1.0D - factor, 0.0D },
//          { 1.0D - factor, 0.0D },
//          { 1.0D - factor, 0.0D },
//          { factor, 0.0D },
//      };
//    } else {
//      return toPlanarImage(source);
//    }
//    try {
//      ColorModel model = BandCombineDescriptor.create(image, matrix, renderingHints).getColorModel();
//      return BandCombineDescriptor.create(image, matrix, renderingHints);
//    } catch (RuntimeException exception) {
//      logger.log(ILevel.WARNING, exception.getMessage(), exception);
//      return toPlanarImage(source);
//    }
  }

  public static PlanarImage toMapped(final RenderingHints hints, final PlanarImage source, final int[] mapping) {
    if (mapping == null || mapping.length == 0) {
      return source;
    }
    if (source.getColorModel() instanceof IndexColorModel) {
      return source;
    }
    if (mapping.length == source.getColorModel().getNumColorComponents() && isIdentity(mapping)) {
      return source;
    }
    return BandSelectDescriptor.create(source, mapping, hints);
  }

  private static boolean isIdentity(final int[] mapping) {
    for (int i = 0; i < mapping.length; i++) {
      if (i >= mapping.length || i != mapping[i]) {
        return false;
      }
    }
    return true;
  }

  public static PlanarImage toPlanarImage(final RenderedImage image) {
    if (image instanceof PlanarImage) {
      return (PlanarImage) image;
    }
    return new RenderedImageAdapter(image);
  }

  public static PlanarImage toTransparent(final RenderingHints hints, final RenderedImage source, final Color color) {
    final RenderedImage image = source.getColorModel() instanceof IndexColorModel ? source
        : ColorQuantizerDescriptor.create(
            source,
            ColorQuantizerDescriptor.OCTTREE,
            Integer.valueOf(255),
            Integer.valueOf(300),
            null,
            Integer.valueOf(2),
            Integer.valueOf(2),
            null);
    //
    final ColorModel colorModel = image.getColorModel();
    //
    final IndexColorModel indexColorModel = (IndexColorModel) colorModel;
    final int mapSize = indexColorModel.getMapSize();
    final byte[][] data = new byte[4][mapSize];
    indexColorModel.getReds(data[0]);
    indexColorModel.getGreens(data[1]);
    indexColorModel.getBlues(data[2]);
    indexColorModel.getAlphas(data[3]);

    final int red = (color.getRed() > 128) ? (color.getRed() - 256) : color.getRed();
    final int green = (color.getGreen() > 128) ? (color.getGreen() - 256) : color.getGreen();
    final int blue = (color.getBlue() > 128) ? (color.getBlue() - 256) : color.getBlue();

    // change color map
    for (int i = 0; i < mapSize; i++) {
      if (data[0][i] == red && data[1][i] == green && data[2][i] == blue) {
        data[3][i] = 0;
      }
    }

    final LookupTableJAI lut = new LookupTableJAI(data);
    return LookupDescriptor.create(image, lut, hints);
  }

  public static RenderedImage convertIndexColorModelToARGB(
      final RenderingHints hints,
      final RenderedImage source,
      final IndexColorModel indexColorModel) {

    int numberOfBands = indexColorModel.hasAlpha() ? 4 : 3;

    byte[][] data = new byte[numberOfBands][indexColorModel.getMapSize()];
    if (indexColorModel.hasAlpha()) {
      indexColorModel.getAlphas(data[3]);
    }
    indexColorModel.getReds(data[0]);
    indexColorModel.getGreens(data[1]);
    indexColorModel.getBlues(data[2]);
    LookupTableJAI lookupTable = new LookupTableJAI(data);
    return LookupDescriptor.create(source, lookupTable, hints);
  }

  private static Float floor(final float x) {
    return Float.valueOf(Double.valueOf(Math.floor(x)).floatValue());
  }

  private static Float ceil(final float x) {
    return Float.valueOf(Double.valueOf(Math.ceil(x)).floatValue());
  }

  private static Float toFloat(final float value) {
    return Float.valueOf(value);
  }

  public static Interpolation getInterpolation(final RenderingHints hints) {
    Object object = hints.get(RenderingHints.KEY_INTERPOLATION);
    if (Objects.equals(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, object)) {
      return Interpolation.getInstance(Interpolation.INTERP_NEAREST);
    }
    if (Objects.equals(RenderingHints.VALUE_INTERPOLATION_BILINEAR, object)) {
      return Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
    }
    if (Objects.equals(RenderingHints.VALUE_INTERPOLATION_BICUBIC, object)) {
      return Interpolation.getInstance(Interpolation.INTERP_BICUBIC);
    }
    return Interpolation.getInstance(Interpolation.INTERP_NEAREST);
  }

  public static BufferedImage convertTo256ColorIndexColorModelImage(final BufferedImage image) {
    final PlanarImage colorQuantizedImage = createColorQuantizedImage(
        createWhiteBackgroundedImage(image),
        255);
    final IndexColorModel colorModel = (IndexColorModel) colorQuantizedImage.getColorModel();
    final byte[] reds = new byte[256];
    colorModel.getReds(reds);
    final byte[] greens = new byte[256];
    colorModel.getGreens(greens);
    final byte[] blues = new byte[256];
    colorModel.getBlues(blues);
    final int transparentPixelIndex = 255;
    final IndexColorModel transparentColorModel = new IndexColorModel(
        colorModel.getPixelSize(),
        256,
        reds,
        greens,
        blues,
        transparentPixelIndex);
    final BufferedImage transparentImage = new BufferedImage(
        transparentColorModel,
        colorQuantizedImage.copyData(),
        false,
        new Hashtable<>());

    final WritableRaster raster = transparentImage.getRaster();
    for (int y = 0; y < image.getHeight(); ++y) {
      for (int x = 0; x < image.getWidth(); ++x) {
        final int rgb = image.getRGB(x, y);
        final boolean transparent = (rgb >> 24 & 0xFF) == 0x00;
        if (transparent) {
          raster.setDataElements(x, y, new byte[] { (byte) transparentPixelIndex });
        }
      }
    }
    return transparentImage;
  }

  public static BufferedImage createWhiteBackgroundedImage(final BufferedImage image) {
    final BufferedImage bufferedImage = new BufferedImage(
        image.getWidth(),
        image.getHeight(),
        BufferedImage.TYPE_INT_RGB);
    final Graphics graphics = bufferedImage.getGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, image.getWidth(), image.getWidth());
    graphics.drawImage(image, 0, 0, null);
    graphics.dispose();
    return bufferedImage;
  }

  private static RenderedOp createColorQuantizedImage(
      final BufferedImage image,
      final int colorCount) {
    RenderedOp op = colorQuantize(image, colorCount);
    return expandColorMap(op);
  }

  public static RenderedOp colorQuantize(final RenderedImage image, final int colorCount) {
    final int numComponents = image.getColorModel().getNumComponents();
    final int[] bOffs = new int[numComponents];
    for (int i = 0; i < numComponents; i++) {
      bOffs[i] = numComponents - i - 1;
    }
    final SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
        image
            .getWidth(),
        image.getHeight(),
        numComponents,
        image.getWidth() * numComponents,
        bOffs);

    final ImageLayout layout = new ImageLayout();
    layout.setSampleModel(sampleModel);
    final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
    return ColorQuantizerDescriptor.create(image,
        ColorQuantizerDescriptor.MEDIANCUT,
        colorCount,
        null,
        null,
        null,
        null,
        renderingHints);
  }

  private static RenderedOp expandColorMap(final RenderedOp op) {
    final IndexColorModel indexColorModel = (IndexColorModel) op.getColorModel();
    final ParameterBlockJAI parameterBlock = new ParameterBlockJAI("format"); //$NON-NLS-1$
    parameterBlock.addSource(op);
    parameterBlock.setParameter("dataType", Integer.valueOf(DataBuffer.TYPE_BYTE)); //$NON-NLS-1$

    final ImageLayout layout = new ImageLayout();
    final byte[] reds = new byte[256];
    indexColorModel.getReds(reds);
    final byte[] greens = new byte[256];
    indexColorModel.getGreens(greens);
    final byte[] blues = new byte[256];
    indexColorModel.getBlues(blues);

    final ColorModel colorModel = new IndexColorModel(8, 256, reds, greens, blues);
    layout.setColorModel(colorModel);

    final SampleModel sampleModel = colorModel.createCompatibleSampleModel(op.getWidth(),
        op
            .getHeight());
    layout.setSampleModel(sampleModel);
    final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);

    return JAI.create("format", parameterBlock, renderingHints); //$NON-NLS-1$
  }

}