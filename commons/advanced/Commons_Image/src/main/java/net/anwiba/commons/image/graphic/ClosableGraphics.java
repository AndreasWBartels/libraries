/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.image.graphic;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import net.anwiba.commons.lang.optional.Optional;

class ClosableGraphics extends AbstractGraphics implements IClosableGraphics {

  private final Object antiAliasing;
  private final Object alphaInterpolation;
  private final Object colorRendering;
  private final Object dithering;
  private final Object fractionalmetrics;
  private final Object interpolation;
  private final Object rendering;
  private final Object strokeControl;
  private final Object textAntialiasing;
  private final Object textLcdContrast;
  private final Graphics2D graphics;

  ClosableGraphics(
      final Graphics2D graphics,
      final Object antiAliasing,
      final Object alphaInterpolation,
      final Object colorRendering,
      final Object dithering,
      final Object fractionalmetrics,
      final Object interpolation,
      final Object rendering,
      final Object strokeControl,
      final Object textAntialiasing,
      final Object textLcdContrast) {
    super(graphics);
    this.graphics = graphics;
    this.antiAliasing = antiAliasing;
    this.alphaInterpolation = alphaInterpolation;
    this.colorRendering = colorRendering;
    this.dithering = dithering;
    this.fractionalmetrics = fractionalmetrics;
    this.interpolation = interpolation;
    this.rendering = rendering;
    this.strokeControl = strokeControl;
    this.textAntialiasing = textAntialiasing;
    this.textLcdContrast = textLcdContrast;
  }

  @Override
  public void close() throws RuntimeException {
    Optional
        .of(this.antiAliasing)
        .consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, value));
    Optional.of(this.textAntialiasing).consume(
        value -> this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, value));
    Optional
        .of(this.alphaInterpolation)
        .consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, value))
        .or(
            () -> this.graphics.setRenderingHint(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT));
    Optional
        .of(this.colorRendering)
        .consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, value))
        .or(
            () -> this.graphics
                .setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT));
    Optional
        .of(this.interpolation)
        .consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, value))
        .or(
            () -> this.graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
    Optional //
        .of(this.dithering)
        .consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_DITHERING, value))
        .or(() -> this.graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT));
    Optional //
        .of(this.fractionalmetrics)
        .consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, value))
        .or(
            () -> this.graphics.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
    Optional.of(this.textLcdContrast).consume(
        value -> this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, value));
    Optional
        .of(this.strokeControl)
        .consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, value))
        .or(
            () -> this.graphics
                .setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT));
    Optional.of(this.rendering).consume(value -> this.graphics.setRenderingHint(RenderingHints.KEY_RENDERING, value)).or(
        () -> this.graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT));
    this.graphics.dispose();
  }
}
