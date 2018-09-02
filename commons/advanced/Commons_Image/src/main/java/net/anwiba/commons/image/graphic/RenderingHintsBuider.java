/*
 * #%L
 *
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

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

public class RenderingHintsBuider {

  private final Map<RenderingHints.Key, Object> hints = new HashMap<>();

  public RenderingHintsBuider() {
    this(new RenderingHints(null));
  }

  public RenderingHintsBuider(final RenderingHints hints) {
    hints.forEach((k, v) -> hints.put(k, v));
  }

  public RenderingHints build() {
    final RenderingHints result = new RenderingHints(null);
    this.hints.forEach((k, v) -> result.put(k, v));
    return result;
  }

  public RenderingHintsBuider setFractionalmetricsDefault() {
    this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    return this;
  }

  public RenderingHintsBuider setFractionalmetricsOn() {
    this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    return this;
  }

  public RenderingHintsBuider setFractionalmetricsOff() {
    this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    return this;
  }

  public RenderingHintsBuider setRenderingDefault() {
    this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
    return this;
  }

  public RenderingHintsBuider setRenderingSpeed() {
    this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    return this;
  }

  public RenderingHintsBuider setRenderingQuality() {
    this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    return this;
  }

  public RenderingHintsBuider setAntiAliasing(final boolean isAntiAliased) {
    this.hints.put(
        RenderingHints.KEY_ANTIALIASING,
        isAntiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    return this;
  }

  public RenderingHintsBuider setTextAntiAliasing(final boolean isAntiAliased) {
    this.hints.put(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        isAntiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    return this;
  }

  public RenderingHintsBuider setDitheringEnabled() {
    this.hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    return this;
  }

  public RenderingHintsBuider setDitheringDisabled() {
    this.hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
    return this;
  }

  public RenderingHintsBuider setDitheringDefault() {
    this.hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
    return this;
  }

  public RenderingHintsBuider setAntiAliasingOn() {
    this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return this;
  }

  public RenderingHintsBuider setAntiAliasingOff() {
    this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    return this;
  }

  public RenderingHintsBuider setInterpolationBicubic() {
    this.hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    return this;
  }

  public RenderingHintsBuider setInterpolationBiLinear() {
    this.hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    return this;
  }

  public RenderingHintsBuider setInterpolationNearestNeighbor() {
    this.hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    return this;
  }

  public RenderingHintsBuider setAlphaInterpolationDefault() {
    this.hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
    return this;
  }

  public RenderingHintsBuider setAlphaInterpolationQuality() {
    this.hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    return this;
  }

  public RenderingHintsBuider setAlphaInterpolationSpeed() {
    this.hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    return this;
  }

  public RenderingHintsBuider setStrokeControlDefault() {
    this.hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    return this;
  }

  public RenderingHintsBuider setStrokeControlNormalize() {
    this.hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    return this;
  }

  public RenderingHintsBuider setStrokeControlPure() {
    this.hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    return this;
  }

  public RenderingHintsBuider setColorRenderDefault() {
    this.hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
    return this;
  }

  public RenderingHintsBuider setColorRenderQuality() {
    this.hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    return this;
  }

  public RenderingHintsBuider setColorRenderSpeed() {
    this.hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
    return this;
  }

  public RenderingHintsBuider setTextLcdContrastHBGR() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
    return this;
  }

  public RenderingHintsBuider setTextLcdContrastVBGR() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
    return this;
  }

  public RenderingHintsBuider setTextLcdContrastHRGB() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    return this;
  }

  public RenderingHintsBuider setTextLcdContrastVRGB() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
    return this;
  }

}
