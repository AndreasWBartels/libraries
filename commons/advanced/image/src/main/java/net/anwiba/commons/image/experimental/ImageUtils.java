/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package net.anwiba.commons.image.experimental;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

// Important: make sure that we get no dependencies to
// other org.esa.snap packages here above org.esa.snap.util

/**
 * A utility class providing a set of static functions frequently used when working with images.
 * <p>
 * All functions have been implemented with extreme caution in order to provide a maximum performance.
 *
 * @author Norman Fomferra
 * @version $Revision$ $Date$
 */
public class ImageUtils {

  /**
   * Converts the given rendered image into an image of the given {#link java.awt.image.BufferedImage} type.
   *
   * @param image     the source image
   * @param imageType the {#link java.awt.image.BufferedImage} type
   * @return the buffered image of the given type
   */
  public static BufferedImage convertImage(final RenderedImage image, final int imageType) {
    final BufferedImage newImage;
    final int width = image.getWidth();
    final int height = image.getHeight();
    if (imageType != BufferedImage.TYPE_CUSTOM) {
      newImage = new BufferedImage(width, height, imageType);
    } else {
      // create custom image
      final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
      final ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
      final WritableRaster wr = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
          width,
          height,
          3 * width,
          3,
          new int[] { 2, 1, 0 },
          null);
      newImage = new BufferedImage(cm, wr, false, null);
    }
    final Graphics2D graphics = newImage.createGraphics();
    graphics.drawRenderedImage(image, null);
    graphics.dispose();
    return newImage;
  }

  /**
   * Returns an array containing the minimum and maximum value of the native data type used to store pixel values in the
   * given image.
   *
   * @param dataType a data type as defined in <code>DataBuffer</code>
   * @see java.awt.image.DataBuffer
   */
  public static double[] getDataTypeMinMax(final int dataType, double[] minmax) {
    if (minmax == null) {
      minmax = new double[2];
    }
    if (dataType == DataBuffer.TYPE_BYTE
        || dataType == DataBuffer.TYPE_INT) {
      minmax[0] = 0.0;
      minmax[1] = 255.0;
    } else if (dataType == DataBuffer.TYPE_SHORT) {
      minmax[0] = Short.MIN_VALUE;
      minmax[1] = Short.MAX_VALUE;
    } else if (dataType == DataBuffer.TYPE_USHORT) {
      minmax[0] = 0.0;
      minmax[1] = 2.0 * Short.MAX_VALUE - 1.0;
    } else {
      minmax[0] = 0.0;
      minmax[1] = 1.0;
    }
    return minmax;
  }

  public static BufferedImage createGreyscaleColorModelImage(final int width, final int height, final byte[] data) {
    ColorModel cm = create8BitGreyscaleColorModel();
    DataBufferByte db = new DataBufferByte(data, data.length);
    WritableRaster wr = WritableRaster.createBandedRaster(db,
        width,
        height,
        width,
        new int[] { 0 },
        new int[] { 0 },
        null);
    return new BufferedImage(cm, wr, false, null);
  }

  public static BufferedImage
      createIndexedImage(final int width, final int height, final byte[] data, final IndexColorModel cm) {
    final int numSamples = data.length;
    SampleModel sm = cm.createCompatibleSampleModel(width, height);
    DataBuffer db = new DataBufferByte(data, numSamples);
    WritableRaster wr = WritableRaster.createWritableRaster(sm, db, null);
    return new BufferedImage(cm, wr, false, null);
  }

  public static ColorModel create8BitGreyscaleColorModel() {
    final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
    return new ComponentColorModel(cs, // colorSpace
        new int[] { 8 }, // bits
        false, // hasAlpha
        false, // isAlphaPremultiplied
        Transparency.OPAQUE, // transparency
        DataBuffer.TYPE_BYTE);
  }

  public static Object getPrimitiveArray(final DataBuffer dataBuffer) {
    switch (dataBuffer.getDataType()) {
      case DataBuffer.TYPE_BYTE:
        return ((DataBufferByte) dataBuffer).getData();
      case DataBuffer.TYPE_SHORT:
        return ((DataBufferShort) dataBuffer).getData();
      case DataBuffer.TYPE_USHORT:
        return ((DataBufferUShort) dataBuffer).getData();
      case DataBuffer.TYPE_INT:
        return ((DataBufferInt) dataBuffer).getData();
      case DataBuffer.TYPE_FLOAT:
        return ((DataBufferFloat) dataBuffer).getData();
      case DataBuffer.TYPE_DOUBLE:
        return ((DataBufferDouble) dataBuffer).getData();
      default:
        throw new IllegalArgumentException("dataBuffer");
    }
  }

  public static Object createDataBufferArray(final int dataBufferType, final int size) {
    switch (dataBufferType) {
      case DataBuffer.TYPE_BYTE:
        return new byte[size];
      case DataBuffer.TYPE_SHORT:
      case DataBuffer.TYPE_USHORT:
        return new short[size];
      case DataBuffer.TYPE_INT:
        return new int[size];
      case DataBuffer.TYPE_FLOAT:
        return new float[size];
      case DataBuffer.TYPE_DOUBLE:
        return new double[size];
      default:
        throw new IllegalArgumentException("dataBuffer");
    }
  }

  public static SampleModel createSingleBandedSampleModel(final int dataBufferType, final int width, final int height) {
    // Note: The SingleBandSampleModel has shown to be about 2 times faster!
    // return RasterFactory.createPixelInterleavedSampleModel(dataBufferType,
    // width,
    // height,
    // 1);
    return new SingleBandedSampleModel(dataBufferType, width, height);
  }
}
