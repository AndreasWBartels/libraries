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
package net.anwiba.commons.graphic;

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

public class RenderingHintsBuilder {

  private final Map<RenderingHints.Key, Object> hints = new HashMap<>();

  public RenderingHintsBuilder() {
    this(new RenderingHints(null));
  }

  public RenderingHintsBuilder(final RenderingHints hints) {
    hints.forEach((k, v) -> hints.put(k, v));
  }

  public RenderingHints build() {
    final RenderingHints result = new RenderingHints(null);
    this.hints.forEach((k, v) -> result.put(k, v));
    return result;
  }

  public RenderingHintsBuilder setFractionalmetricsDefault() {
    this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    return this;
  }

  public RenderingHintsBuilder setFractionalmetricsOn() {
    this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    return this;
  }

  public RenderingHintsBuilder setFractionalmetricsOff() {
    this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    return this;
  }

  public RenderingHintsBuilder setRenderingDefault() {
    this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
    return this;
  }

  public RenderingHintsBuilder setRenderingSpeed() {
    this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    return this;
  }

  public RenderingHintsBuilder setRenderingQuality() {
    this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    return this;
  }

  public RenderingHintsBuilder setAntiAliasing(final boolean isAntiAliased) {
    this.hints.put(
        RenderingHints.KEY_ANTIALIASING,
        isAntiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    return this;
  }

  public RenderingHintsBuilder setTextAntiAliasing(final boolean isAntiAliased) {
    this.hints.put(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        isAntiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    return this;
  }

  public RenderingHintsBuilder setTextAntiAliasingOn() {
    this.hints.put(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    return this;
  }

  public RenderingHintsBuilder setTextAntiAliasingOff() {
    this.hints.put(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    return this;
  }

  public RenderingHintsBuilder setDitheringEnabled() {
    this.hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    return this;
  }

  public RenderingHintsBuilder setDitheringDisabled() {
    this.hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
    return this;
  }

  public RenderingHintsBuilder setDitheringDefault() {
    this.hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
    return this;
  }

  public RenderingHintsBuilder setAntiAliasingOn() {
    this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return this;
  }

  public RenderingHintsBuilder setAntiAliasingOff() {
    this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    return this;
  }

  public RenderingHintsBuilder setInterpolationBicubic() {
    this.hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    return this;
  }

  public RenderingHintsBuilder setInterpolationBiLinear() {
    this.hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    return this;
  }

  public RenderingHintsBuilder setInterpolationNearestNeighbor() {
    this.hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    return this;
  }

  public RenderingHintsBuilder setAlphaInterpolationDefault() {
    this.hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
    return this;
  }

  public RenderingHintsBuilder setAlphaInterpolationQuality() {
    this.hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    return this;
  }

  public RenderingHintsBuilder setAlphaInterpolationSpeed() {
    this.hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    return this;
  }

  public RenderingHintsBuilder setStrokeControlDefault() {
    this.hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    return this;
  }

  public RenderingHintsBuilder setStrokeControlNormalize() {
    this.hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    return this;
  }

  public RenderingHintsBuilder setStrokeControlPure() {
    this.hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    return this;
  }

  public RenderingHintsBuilder setColorRenderDefault() {
    this.hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
    return this;
  }

  public RenderingHintsBuilder setColorRenderQuality() {
    this.hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    return this;
  }

  public RenderingHintsBuilder setColorRenderSpeed() {
    this.hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
    return this;
  }

  public RenderingHintsBuilder setTextLcdContrastHBGR() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
    return this;
  }

  public RenderingHintsBuilder setTextLcdContrastVBGR() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
    return this;
  }

  public RenderingHintsBuilder setTextLcdContrastHRGB() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    return this;
  }

  public RenderingHintsBuilder setTextLcdContrastVRGB() {
    this.hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
    return this;
  }

}
