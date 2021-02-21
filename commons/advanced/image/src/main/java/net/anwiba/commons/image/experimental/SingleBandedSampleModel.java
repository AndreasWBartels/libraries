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

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;

import org.eclipse.imagen.ComponentSampleModelJAI;

/**
 * This class represents image data which is composed of a single band so that (a pixel comprises a single sample and
 * occupies one data element of the DataBuffer). It subclasses ComponentSampleModelJAI but provides a more efficent
 * implementation for accessing pixel interleaved image data than is provided by ComponentSampleModelJAI. This class
 * stores sample data in a single bank of the DataBuffer. Accessor methods are provided so that image data can be
 * manipulated directly. This class supports {@link DataBuffer#TYPE_BYTE TYPE_BYTE}, {@link DataBuffer#TYPE_USHORT
 * TYPE_USHORT}, {@link DataBuffer#TYPE_SHORT TYPE_SHORT}, {@link DataBuffer#TYPE_INT TYPE_INT},
 * {@link DataBuffer#TYPE_FLOAT TYPE_FLOAT} and {@link DataBuffer#TYPE_DOUBLE TYPE_DOUBLE} datatypes.
 */
public class SingleBandedSampleModel extends ComponentSampleModelJAI {
  /**
   * Constructs a SingleBandSampleModel with the specified parameters. The number of bands will be given by the length
   * of the bandOffsets array.
   *
   * @param dataType The data type for storing samples.
   * @param w        The width (in pixels) of the region of image data described.
   * @param h        The height (in pixels) of the region of image data described.
   * @throws IllegalArgumentException if <code>w</code> or <code>h</code> is not greater than 0 or if
   *                                  <code>dataType</code> is not one of the supported data types
   */
  public SingleBandedSampleModel(final int dataType, final int w, final int h) {
    super(dataType, w, h, 1, w, new int[] { 0 });
  }

  /**
   * Creates a new SingleBandSampleModel with the specified width and height. The new SingleBandSampleModel will have
   * the same storage data type as this SingleBandSampleModel.
   *
   * @param w the width of the resulting <code>SampleModel</code>
   * @param h the height of the resulting <code>SampleModel</code>
   * @return a new <code>SampleModel</code> with the specified width and height.
   * @throws IllegalArgumentException if <code>w</code> or <code>h</code> is not greater than 0
   */
  @Override
  public SampleModel createCompatibleSampleModel(final int w, final int h) {
    return new SingleBandedSampleModel(this.dataType, w, h);
  }

  /**
   * Simply calls {@link #createCompatibleSampleModel(int,int) createCompatibleSampleModel(width, height)}.
   *
   * @param bands Ignored.
   * @return a new <code>org.esa.snap.jai.SingleBandSampleModel</code>.
   */
  @Override
  public SampleModel createSubsetSampleModel(final int bands[]) {
    return createCompatibleSampleModel(this.width, this.height);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int getOffset(final int x, final int y) {
    return y * this.width + x;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int getOffset(final int x, final int y, final int b) {
    return y * this.width + x;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getDataElements(final int x, final int y, final int w, final int h, Object obj, final DataBuffer data) {
    if (obj == null) {
      obj = ImageUtils.createDataBufferArray(data.getDataType(), w * h);
    }
    getSamplesFast(x, y, w, h, obj, ImageUtils.getPrimitiveArray(data), data.getOffset());
    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void
      setDataElements(final int x, final int y, final int w, final int h, final Object obj, final DataBuffer data) {
    setSamplesFast(x, y, w, h, obj, ImageUtils.getPrimitiveArray(data), data.getOffset());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int[] getPixels(final int x, final int y, final int w, final int h, final int array[], final DataBuffer data) {
    return getSamples(x, y, w, h, 0, array, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float[]
      getPixels(final int x, final int y, final int w, final int h, final float array[], final DataBuffer data) {
    return getSamples(x, y, w, h, 0, array, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double[]
      getPixels(final int x, final int y, final int w, final int h, final double array[], final DataBuffer data) {
    return getSamples(x, y, w, h, 0, array, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPixels(final int x, final int y, final int w, final int h, final int array[], final DataBuffer data) {
    setSamples(x, y, w, h, 0, array, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void
      setPixels(final int x, final int y, final int w, final int h, final float array[], final DataBuffer data) {
    setSamples(x, y, w, h, 0, array, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void
      setPixels(final int x, final int y, final int w, final int h, final double array[], final DataBuffer data) {
    setSamples(x, y, w, h, 0, array, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int[] getPixel(final int x, final int y, int array[], final DataBuffer data) {
    if (array == null) {
      array = new int[1];
    }
    array[0] = getSample(x, y, 0, data);
    return array;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float[] getPixel(final int x, final int y, float array[], final DataBuffer data) {
    if (array == null) {
      array = new float[1];
    }
    array[0] = getSampleFloat(x, y, 0, data);
    return array;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double[] getPixel(final int x, final int y, double array[], final DataBuffer data) {
    if (array == null) {
      array = new double[1];
    }
    array[0] = getSampleDouble(x, y, 0, data);
    return array;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPixel(final int x, final int y, final int array[], final DataBuffer data) {
    setSample(x, y, 0, array[0], data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPixel(final int x, final int y, final float array[], final DataBuffer data) {
    setSample(x, y, 0, array[0], data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPixel(final int x, final int y, final double array[], final DataBuffer data) {
    setSample(x, y, 0, array[0], data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int[]
      getSamples(final int x, final int y, final int w, final int h, final int b, int array[], final DataBuffer data) {
    checkBounds(x, y, w, h);
    if (array == null) {
      array = new int[w * h];
    }
    if (this.dataType == DataBuffer.TYPE_INT) {
      getSamplesFast(x, y, w, h, array, ((DataBufferInt) data).getData(), data.getOffset());
    } else {
      super.getSamples(x, y, w, h, b, array, data);
    }
    return array;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float[] getSamples(final int x,
      final int y,
      final int w,
      final int h,
      final int b,
      float array[],
      final DataBuffer data) {
    checkBounds(x, y, w, h);
    if (array == null) {
      array = new float[w * h];
    }
    if (this.dataType == DataBuffer.TYPE_FLOAT) {
      getSamplesFast(x, y, w, h, array, ((DataBufferFloat) data).getData(), data.getOffset());
    } else {
      super.getSamples(x, y, w, h, b, array, data);
    }
    return array;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double[] getSamples(final int x,
      final int y,
      final int w,
      final int h,
      final int b,
      double array[],
      final DataBuffer data) {
    checkBounds(x, y, w, h);
    if (array == null) {
      array = new double[w * h];
    }
    if (this.dataType == DataBuffer.TYPE_DOUBLE) {
      getSamplesFast(x, y, w, h, array, ((DataBufferDouble) data).getData(), data.getOffset());
    } else {
      super.getSamples(x, y, w, h, b, array, data);
    }
    return array;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSamples(final int x,
      final int y,
      final int w,
      final int h,
      final int b,
      final int array[],
      final DataBuffer data) {
    checkBounds(x, y, w, h);
    if (this.dataType == DataBuffer.TYPE_INT) {
      setSamplesFast(x, y, w, h, array, ((DataBufferInt) data).getData(), data.getOffset());
    } else {
      super.setSamples(x, y, w, h, b, array, data);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSamples(final int x,
      final int y,
      final int w,
      final int h,
      final int b,
      final float array[],
      final DataBuffer data) {
    checkBounds(x, y, w, h);
    if (this.dataType == DataBuffer.TYPE_FLOAT) {
      setSamplesFast(x, y, w, h, array, ((DataBufferFloat) data).getData(), data.getOffset());
    } else {
      super.setSamples(x, y, w, h, b, array, data);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSamples(final int x,
      final int y,
      final int w,
      final int h,
      final int b,
      final double array[],
      final DataBuffer data) {
    checkBounds(x, y, w, h);
    if (this.dataType == DataBuffer.TYPE_DOUBLE) {
      setSamplesFast(x, y, w, h, array, ((DataBufferDouble) data).getData(), data.getOffset());
    } else {
      super.setSamples(x, y, w, h, b, array, data);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSample(final int x, final int y, final int b, final DataBuffer data) {
    checkBounds(x, y);
    return data.getElem(y * this.width + x);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float getSampleFloat(final int x, final int y, final int b, final DataBuffer data) {
    checkBounds(x, y);
    return data.getElemFloat(y * this.width + x);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getSampleDouble(final int x, final int y, final int b, final DataBuffer data) {
    checkBounds(x, y);
    return data.getElemDouble(y * this.width + x);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSample(final int x, final int y, final int b, final int s, final DataBuffer data) {
    checkBounds(x, y);
    data.setElem(y * this.width + x, s);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSample(final int x, final int y, final int b, final float s, final DataBuffer data) {
    checkBounds(x, y);
    data.setElemFloat(y * this.width + x, s);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSample(final int x, final int y, final int b, final double s, final DataBuffer data) {
    checkBounds(x, y);
    data.setElemDouble(y * this.width + x, s);
  }

  private void setSamplesFast(final int x,
      final int y,
      final int w,
      final int h,
      final Object sourceArray,
      final Object targetArray,
      final int targetOffset) {
    int targetIndex = y * this.width + x;
    if (w == this.width) {
      System.arraycopy(sourceArray, 0, targetArray, targetOffset + targetIndex, w * h);
    } else {
      for (int i = 0; i < h; i++) {
        System.arraycopy(sourceArray, i * w, targetArray, targetOffset + targetIndex, w);
        targetIndex += this.width;
      }
    }
  }

  private void getSamplesFast(final int x,
      final int y,
      final int w,
      final int h,
      final Object targetArray,
      final Object sourceArray,
      final int sourceOffset) {
    int sourceIndex = y * this.width + x;
    if (w == this.width) {
      System.arraycopy(sourceArray, sourceOffset + sourceIndex, targetArray, 0, w * h);
    } else {
      for (int i = 0; i < h; i++) {
        System.arraycopy(sourceArray, sourceOffset + sourceIndex, targetArray, i * w, w);
        sourceIndex += this.width;
      }
    }
  }

  private void checkBounds(final int x, final int y, final int w, final int h) {
    checkBounds(x, y);
    checkBounds(x + w - 1, y + h - 1);
  }

  private void checkBounds(final int x, final int y) {
    if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
      throw new ArrayIndexOutOfBoundsException(
          String.format("(x < 0) || (y < 0) || (x >= %d) || (y >= %d) for x=%d,y=%d", this.width, this.height, x, y));
    }
  }
}
