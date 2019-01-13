/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.image.renderable;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Vector;
import java.util.stream.Collectors;

public class RenderableImageAdapter implements RenderableImage {

  private final RenderedImage renderedImage;

  public RenderableImageAdapter(final RenderedImage renderedImage) {
    this.renderedImage = renderedImage;
  }

  @Override
  public Vector<RenderableImage> getSources() {
    final Vector<RenderedImage> sources = this.renderedImage.getSources();
    return new Vector<>(
        sources.stream().map(i -> new RenderableImageAdapter(this.renderedImage)).collect(Collectors.toList()));
  }

  @Override
  public Object getProperty(final String name) {
    return this.renderedImage.getProperty(name);
  }

  @Override
  public String[] getPropertyNames() {
    return this.renderedImage.getPropertyNames();
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

  @Override
  public float getWidth() {
    return this.renderedImage.getWidth();
  }

  @Override
  public float getHeight() {
    return this.renderedImage.getHeight();
  }

  @Override
  public float getMinX() {
    return this.renderedImage.getMinX();
  }

  @Override
  public float getMinY() {
    return this.renderedImage.getMinY();
  }

  @Override
  public RenderedImage createScaledRendering(final int w, final int h, final RenderingHints hints) {
    if ((w == 0) && (h == 0)) {
      throw new IllegalArgumentException();
    }
    final int width = w == 0 ? Math.round(h * (getWidth() / getHeight())) : w;
    final int height = h == 0 ? Math.round(w * (getHeight() / getWidth())) : h;
    final AffineTransform scaleTransformer = AffineTransform.getScaleInstance(width / getWidth(), height / getHeight());
    return createRendering(new RenderContext(scaleTransformer, hints));
  }

  @Override
  public RenderedImage createDefaultRendering() {
    return this.renderedImage;
  }

  @Override
  public RenderedImage createRendering(final RenderContext renderContext) {
    // TODO_NOW (bartels) Nov 13, 2018: Auto-generated method stub
    final RenderedImageFactory factory = new RenderedImageFactory() {

      @Override
      public RenderedImage create(final ParameterBlock paramBlock, final RenderingHints hints) {
        // TODO_NOW (bartels) Nov 13, 2018: Auto-generated method stub
        return null;
      }
    };

    final ParameterBlock parameterBlock = new ParameterBlock();

    return factory.create(parameterBlock, renderContext.getRenderingHints());
  }

}
