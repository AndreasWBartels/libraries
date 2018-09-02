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

public class ClosableGraphicsBuider {

  private final Graphics2D graphics;
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
  private boolean dispose = true;

  public ClosableGraphicsBuider(final Graphics2D graphics) {
    this(graphics, new RenderingHints(null));
  }

  public ClosableGraphicsBuider(final Graphics2D graphics, final RenderingHints hints) {
    this.graphics = graphics;
    this.antiAliasing = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    this.alphaInterpolation = graphics.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION);
    this.colorRendering = graphics.getRenderingHint(RenderingHints.KEY_COLOR_RENDERING);
    this.dithering = graphics.getRenderingHint(RenderingHints.KEY_DITHERING);
    this.fractionalmetrics = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
    this.interpolation = graphics.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
    this.rendering = graphics.getRenderingHint(RenderingHints.KEY_RENDERING);
    this.strokeControl = graphics.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
    this.textAntialiasing = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
    this.textLcdContrast = graphics.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
    hints.forEach((k, v) -> graphics.setRenderingHint((RenderingHints.Key) k, v));
  }

  public IClosableGraphics build() {
    return new ClosableGraphics(
        this.dispose,
        this.graphics,
        this.antiAliasing,
        this.alphaInterpolation,
        this.colorRendering,
        this.dithering,
        this.fractionalmetrics,
        this.interpolation,
        this.rendering,
        this.strokeControl,
        this.textAntialiasing,
        this.textLcdContrast);
  }

  public ClosableGraphicsBuider setDispose(final boolean dispose) {
    this.dispose = dispose;
    return this;
  }

  public ClosableGraphicsBuider setFractionalmetricsDefault() {
    this.graphics
        .setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    return this;
  }

  public ClosableGraphicsBuider setFractionalmetricsOn() {
    this.graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    return this;
  }

  public ClosableGraphicsBuider setFractionalmetricsOff() {
    this.graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    return this;
  }

  public ClosableGraphicsBuider setRenderingDefault() {
    this.graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
    return this;
  }

  public ClosableGraphicsBuider setRenderingSpeed() {
    this.graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    return this;
  }

  public ClosableGraphicsBuider setRenderingQuality() {
    this.graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    return this;
  }

  public ClosableGraphicsBuider setAntiAliasing(final boolean isAntiAliased) {
    this.graphics.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        isAntiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    return this;
  }

  public ClosableGraphicsBuider setTextAntiAliasing(final boolean isAntiAliased) {
    this.graphics.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        isAntiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    return this;
  }

  public ClosableGraphicsBuider setDitheringEnabled() {
    this.graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    return this;
  }

  public ClosableGraphicsBuider setDitheringDisabled() {
    this.graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
    return this;
  }

  public ClosableGraphicsBuider setDitheringDefault() {
    this.graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
    return this;
  }

  public ClosableGraphicsBuider setAntiAliasingOn() {
    this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return this;
  }

  public ClosableGraphicsBuider setAntiAliasingOff() {
    this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    return this;
  }

  public ClosableGraphicsBuider setInterpolationBicubic() {
    this.graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    return this;
  }

  public ClosableGraphicsBuider setInterpolationBiLinear() {
    this.graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    return this;
  }

  public ClosableGraphicsBuider setInterpolationNearestNeighbor() {
    this.graphics
        .setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    return this;
  }

  public ClosableGraphicsBuider setAlphaInterpolationDefault() {
    this.graphics
        .setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
    return this;
  }

  public ClosableGraphicsBuider setAlphaInterpolationQuality() {
    this.graphics
        .setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    return this;
  }

  public ClosableGraphicsBuider setAlphaInterpolationSpeed() {
    this.graphics
        .setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    return this;
  }

  public ClosableGraphicsBuider setStrokeControlDefault() {
    this.graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    return this;
  }

  public ClosableGraphicsBuider setStrokeControlNormalize() {
    this.graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    return this;
  }

  public ClosableGraphicsBuider setStrokeControlPure() {
    this.graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    return this;
  }

  public ClosableGraphicsBuider setColorRenderDefault() {
    this.graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
    return this;
  }

  public ClosableGraphicsBuider setColorRenderQuality() {
    this.graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    return this;
  }

  public ClosableGraphicsBuider setColorRenderSpeed() {
    this.graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
    return this;
  }

  public ClosableGraphicsBuider setTextLcdContrastHBGR() {
    this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
    return this;
  }

  public ClosableGraphicsBuider setTextLcdContrastVBGR() {
    this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
    return this;
  }

  public ClosableGraphicsBuider setTextLcdContrastHRGB() {
    this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    return this;
  }

  public ClosableGraphicsBuider setTextLcdContrastVRGB() {
    this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
    return this;
  }

}
