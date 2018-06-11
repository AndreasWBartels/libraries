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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

public abstract class AbstractRenderedImageContainer implements IImageContainer {

  private final RenderedImage image;

  public AbstractRenderedImageContainer(final RenderedImage image) {
    super();
    this.image = image;
  }

  @Override
  public int getWidth() {
    return this.image.getWidth();
  }

  @Override
  public int getHeight() {
    return this.image.getHeight();
  }

  @Override
  public IImageContainer crop(final float x, final float y, final float width, final float height) {
    final RenderedOp cropedRenderOp = ImageContainerUtilities.crop(this.image, x, y, width, height);
    final RenderedOp translateedRenderOp = ImageContainerUtilities.translate(cropedRenderOp, -x, -y);
    return new PlanarImageContainer(translateedRenderOp);
  }

  @Override
  public IImageContainer scale(final float factor) {
    return scale(factor, factor);
  }

  @Override
  public IImageContainer scale(final float widthFactor, final float heightFactor) {
    final RenderedOp scaledRenderOp = ImageContainerUtilities.scale(this.image, widthFactor, heightFactor);
    return new PlanarImageContainer(scaledRenderOp);
  }

  //  @Override
  //  public IImageContainer rotate(float angle) {
  //    final RenderedOp scaledRenderOp = ImageContainerUtilities.scale(this.image, widthFactor, heightFactor);
  //    return new PlanarImageContainer(scaledRenderOp);
  //  }
  //
  //  @Override
  //  public IImageContainer rotate(float x, float y) {
  //    // TODO_NOW (andreas) Jul 3, 2017: Auto-generated method stub
  //    return null;
  //  }

  @Override
  public IImageContainer mapBands(final int[] mapping) {
    if (mapping == null || mapping.length == 0 || isSorted(mapping)) {
      return this;
    }
    final RenderedOp renderedOp = JAI.create("bandselect", this.image, mapping); //$NON-NLS-1$
    return new PlanarImageContainer(renderedOp);
  }

  @Override
  public IImageContainer invert() {
    final ParameterBlock pb = new ParameterBlock();
    pb.addSource(this.image);
    final RenderedOp renderedOp = JAI.create("invert", pb, null); //$NON-NLS-1$
    return new PlanarImageContainer(renderedOp);
  }

  @Override
  public IImageContainer toGrayScale() {
    return new PlanarImageContainer(ImageContainerUtilities.toGrayScale(this.image));
  }

  private boolean isSorted(final int[] mapping) {
    for (int i = 0; i < mapping.length; i++) {
      if (i != mapping[i]) {
        return false;
      }
    }
    return true;
  }

  @Override
  public IImageContainer fit(final int width, final int height) {
    final float factor = fitFactor(width, height);
    return scale(factor);
  }

  @Override
  public BufferedImage asBufferImage(final Rectangle rectangle) {
    return asBufferImage(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }

  private float fitFactor(final int width, final int height) {
    final float withFactor = factor(width, getWidth());
    final float heightfactor = factor(height, getHeight());
    return Math.max(withFactor, heightfactor);
  }

  private float factor(final int numerator, final int denominator) {
    return (float) numerator / (float) denominator;
  }

  @Override
  public int getColorSpaceType() {
    return this.image.getColorModel().getColorSpace().getType();
  }
}
