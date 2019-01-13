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

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.image.operation.ImageCropOperation;
import net.anwiba.commons.image.operation.ImageInvertOperation;
import net.anwiba.commons.image.operation.ImageMapBandsOperation;
import net.anwiba.commons.image.operation.ImageScaleOperation;
import net.anwiba.commons.image.operation.ImageToGrayScaleOperation;
import net.anwiba.commons.lang.collection.IObjectList;

public class BufferedImageOperatorFactory {

  public static final class AggregatedBufferedImageOperator implements IBufferedImageOperator {
    private final IObjectList<IBufferedImageOperator> collection;

    public AggregatedBufferedImageOperator(final IObjectList<IBufferedImageOperator> collection) {
      this.collection = collection;
    }

    @Override
    public BufferedImage execute(final BufferedImage source) {
      return this.collection.stream().aggregate(source, (s, o) -> o.execute(s)).get();
    }
  }

  static final class BufferedImageOpOperator implements IBufferedImageOperator {

    private final BufferedImageOp bufferedImageOp;

    public BufferedImageOpOperator(final BufferedImageOp bufferedImageOp) {
      this.bufferedImageOp = bufferedImageOp;
    }

    @Override
    public BufferedImage execute(final BufferedImage source) {
      return this.bufferedImageOp.filter(source, null);
    }

  }

  IBufferedImageOperator create(
      final BufferedImageMetadata metadata,
      final IImageOperation operation,
      final RenderingHints hints) {
    if (operation instanceof ImageScaleOperation) {
      final ImageScaleOperation o = (ImageScaleOperation) operation;
      return new BufferedImageOpOperator(
          new AffineTransformOp(AffineTransform.getScaleInstance(o.getWidthFactor(), o.getHeightFactor()), hints));
    } else if (operation instanceof ImageCropOperation) {
      final ImageCropOperation o = (ImageCropOperation) operation;
      return new IBufferedImageOperator() {

        @Override
        public BufferedImage execute(final BufferedImage source) {
          return source.getSubimage(
              Math.round(o.getX()),
              Math.round(o.getX()),
              Math.round(o.getWidth()),
              Math.round(o.getHeight()));
        }
      };
    } else if (operation instanceof ImageMapBandsOperation) {
      final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
      final LookupTable lookupTable = new LookupTable(0, metadata.getNumberOfBands()) {

        @Override
        public int[] lookupPixel(final int[] src, final int[] dest) {
          final int[] bandMapping = o.getBandMapping();
          for (int i = 0; i < dest.length; i++) {
            dest[i] = src[bandMapping[i]];
          }
          return dest;
        }
      };
      return new BufferedImageOpOperator(new LookupOp(lookupTable, hints));
    } else if (operation instanceof ImageInvertOperation) {
      final LookupTable lookupTable = new LookupTable(0, metadata.getNumberOfBands()) {

        @Override
        public int[] lookupPixel(final int[] src, final int[] dest) {
          for (int i = 0; i < dest.length; i++) {
            dest[i] = 255 - src[i];
          }
          return dest;
        }
      };
      return new BufferedImageOpOperator(new LookupOp(lookupTable, hints));
    } else if (operation instanceof ImageToGrayScaleOperation) {
      return new BufferedImageOpOperator(new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), hints));
    }
    return new IBufferedImageOperator() {

      @Override
      public BufferedImage execute(final BufferedImage source) {
        return source;
      }
    };
  }

  IBufferedImageOperator create(
      final BufferedImageMetadata metadata,
      final IObjectList<IImageOperation> operations,
      final RenderingHints hints) {
    final IObjectList<IBufferedImageOperator> collection = operations
        .stream()
        .convert(o -> create(metadata, o, hints))
        .asObjectList();
    return new AggregatedBufferedImageOperator(collection);
  }

}
