/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.graphic.color;

import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Colors {

  private final static List<NamedColor> namedColors = new LinkedList<>();

  private static IColor rgb(String name, final int r, final int g, final int b) {
    IColor color = rgb(r,g,b);
    namedColors.add(new NamedColor(name, color));
    return color; 
  }
  
  public static List<NamedColor> getNamedColors() {
    return Collections.unmodifiableList(namedColors);
  }

  public static final IColor WHITE = rgb("White", 255, 255, 255);
  public static final IColor LIGHT_GRAY = rgb("Light gray", 192, 192, 192);
  public static final IColor GRAY = rgb("Gray", 128, 128, 128);
  public static final IColor DARK_GRAY = rgb("Dark gray", 64, 64, 64);
  public static final IColor BLACK = rgb("Black", 0, 0, 0);
  public static final IColor RED = rgb("Red", 255, 0, 0);
  public static final IColor PINK = rgb("Pink", 255, 175, 175);
  public static final IColor ORANGE = rgb("Orange", 255, 200, 0);
  public static final IColor YELLOW = rgb("Yellow", 255, 255, 0);
  public static final IColor GREEN = rgb("Green", 0, 255, 0);
  public static final IColor MAGENTA = rgb("Magenta", 255, 0, 255);
  public static final IColor CYAN = rgb("Cyan", 0, 255, 255);
  public static final IColor BLUE = rgb("Blue", 0, 0, 255);
  public static final IColor SILVER = rgb("Silver", 192, 192, 192);
  public static final IColor MAROON = rgb("Maroon", 128, 0, 0);
  public static final IColor PURPLE = rgb("Purple", 128, 0, 128);
  public static final IColor FUCHSIA = rgb("Fuchsia", 255, 0, 255);
  public static final IColor LIME = rgb("Lime", 0, 128, 0);
  public static final IColor OLIVE = rgb("Olive", 128, 128, 0);
  public static final IColor NAVY = rgb("Navy", 0, 0, 128);
  public static final IColor TEAL = rgb("Teal", 0, 128, 128);
  public static final IColor AQUA = rgb("Aqua", 0, 255, 255);

  public static String toName(final IColor color) {
    // https://www.w3.org/TR/css-color-3/#SRGB
    if (color instanceof NamedColor namedColor) {
      return namedColor.name();
    }
    IColor rgbColor = color.adaptTo(ColorSpaces.CS_RGB);
    for (NamedColor namedColor : namedColors) {
      if (Objects.equals(namedColor, rgbColor)) {
        return namedColor.name();
      }
    }
    String buf = Integer.toHexString(rgbColor.rgb());
    String hexString = "00000000".substring(0, 8 - buf.length()) + buf.toUpperCase();
    return "#" + (hexString.startsWith("FF") ? hexString.substring(2) : hexString);
  }

  public static String toRgbString(final IColor color) {
    // https://www.w3.org/TR/css-color-3/#SRGB
    String buf = Integer.toHexString(color.toColor().getRGB());
    String hexString = "00000000".substring(0, 8 - buf.length()) + buf.toUpperCase();
    return "#" + (hexString.startsWith("FF") ? hexString.substring(2) : hexString);
  }

  public static IColor of(final java.awt.Color color) {
    return color == null
        ? new RgbColorBuilder().build()
        : new RgbColorBuilder(color).build();
  }

  public static IColor of(final ColorSpace colorSpace, final float[] colorComponmets, final float opacity) {
    return new Color(colorSpace, colorComponmets, opacity);
  }

  public static CmykColorBuilder cmyk() {
    return new CmykColorBuilder();
  }

  public static IColor cmyk(final java.awt.Color color) {
    return new CmykColorBuilder(color).build();
  }

  public static IColor cmyk(final float c, final float m, final float y, final float k) {
    return cmyk(c, m, y, k, 1.0f);
  }

  public static IColor cmyk(final float c, final float m, final float y, final float k, final float a) {
    return new Color(ColorSpaces.CS_CMYK, new float[] { c, m, y, k }, a);
  }

  public static IColor cmyk(final int c, final int m, final int y, final int k) {
    return new CmykColorBuilder()
        .cyan(c)
        .magenta(m)
        .yellow(y)
        .black(k)
        .build();
  }

  public static IColor cmyk(final int c, final int m, final int y, final int k, final int a) {
    return new CmykColorBuilder()
        .cyan(c)
        .magenta(m)
        .yellow(y)
        .black(k)
        .opacity(a)
        .build();
  }

  public static IColorBuilder builder(final ColorSpace colorSpace) {
    return builder(colorSpace.getType());
  }

  public static IColorBuilder builder(final IColor color) {
    final ColorSpace colorSpace = color.getColorSpace();
    final IColorBuilder builder = builder(colorSpace);
    builder.components(color.getCompoments());
    return builder;
  }

  public static IColorBuilder builder(final int type) {
    return switch (type) {
      case ColorSpace.TYPE_CMYK -> new CmykColorBuilder();
      case ColorSpace.TYPE_GRAY -> new GrayColorBuilder();
      case ColorSpace.TYPE_HSV -> new HsvColorBuilder();
      case ColorSpace.TYPE_HLS -> new HlsColorBuilder();
      case ColorSpace.TYPE_RGB -> new RgbColorBuilder();
      default -> throw new IllegalArgumentException("Unexpected colerspace type " + type);
    };
  }

  public static IColorBuilder builder(final int type, final java.awt.Color color) {
    return switch (type) {
      case ColorSpace.TYPE_CMYK -> new CmykColorBuilder(color);
      case ColorSpace.TYPE_GRAY -> new GrayColorBuilder(color);
      case ColorSpace.TYPE_HSV -> new HsvColorBuilder(color);
      case ColorSpace.TYPE_HLS -> new HlsColorBuilder(color);
      case ColorSpace.TYPE_RGB -> new RgbColorBuilder(color);
      default -> throw new IllegalArgumentException("Unexpected colerspace type " + type);
    };
  }

  public static GrayColorBuilder gray() {
    return new GrayColorBuilder();
  }

  public static IColor gray(final java.awt.Color color) {
    return new GrayColorBuilder(color).build();
  }

  public static IColor gray(final float g) {
    return gray(g, 1.0f);
  }

  public static IColor gray(final float g, final float a) {
    return new Color(ColorSpaces.CS_GRAY, new float[] { g }, a);
  }

  public static IColor gray(final int g) {
    return new GrayColorBuilder().gray(g).build();
  }

  public static IColor gray(final int g, final int a) {
    return new GrayColorBuilder().gray(g).opacity(a).build();
  }

  public static HsvColorBuilder hsv() {
    return new HsvColorBuilder();
  }

  public static IColor hsv(final float h, final float s, final float v) {
    return hsv(h, s, v, 1.0f);
  }

  public static IColor hsv(final float h, final float s, final float v, final float a) {
    return new Color(ColorSpaces.CS_HSV, new float[] { h, s, v }, a);
  }

  public static IColor hsv(final int h, final int s, final int v) {
    return new HsvColorBuilder()
        .hue(h)
        .saturation(s)
        .value(v)
        .build();
  }

  public static IColor hsv(final int h, final int s, final int v, final int a) {
    return new HsvColorBuilder()
        .hue(h)
        .saturation(s)
        .value(v)
        .opacity(a)
        .build();
  }

  public static IColor hsv(final java.awt.Color color) {
    return new HsvColorBuilder(color).build();
  }

  public static HlsColorBuilder hls() {
    return new HlsColorBuilder();
  }

  public static IColor hls(final float h, final float l, final float s) {
    return hls(h, l, s, 1.0f);
  }

  public static IColor hls(final float h, final float l, final float s, final float a) {
    return new Color(ColorSpaces.CS_HLS, new float[] { h, l, s }, a);
  }

  public static IColor hls(final int h, final int l, final int s) {
    return new HlsColorBuilder()
        .hue(h)
        .lightness(l)
        .saturation(s)
        .build();
  }

  public static IColor hls(final int h, final int l, final int s, final int a) {
    return new HlsColorBuilder()
        .hue(h)
        .lightness(l)
        .saturation(s)
        .opacity(a)
        .build();
  }

  public static IColor hls(final java.awt.Color color) {
    return new HlsColorBuilder(color).build();
  }

  public static RgbColorBuilder rgb() {
    return new RgbColorBuilder();
  }

  public static IColor rgb(final int r, final int g, final int b) {
    return new RgbColorBuilder()
        .red(r)
        .green(g)
        .blue(b)
        .build();
  }

  public static IColor rgb(final int r, final int g, final int b, final int a) {
    return new RgbColorBuilder()
        .red(r)
        .green(g)
        .blue(b)
        .opacity(a)
        .build();
  }

  public static IColor rgb(final float r, final float g, final float b) {
    return rgb(r, g, b, 1.0f);
  }

  public static IColor rgb(final float r, final float g, final float b, final float a) {
    // https://www.w3.org/TR/css-color-3/#SRGB
    return new Color(ColorSpaces.CS_RGB, new float[] { r, g, b }, a);
  }

  public static IColor rgb(final String hexString) {
    String string = hexString.replace("#", "");
    java.awt.Color color = switch (string.length()) {
      case 6 -> new java.awt.Color(
          Integer.valueOf(string.substring(0, 2), 16).intValue(),
          Integer.valueOf(string.substring(2, 4), 16).intValue(),
          Integer.valueOf(string.substring(4, 6), 16).intValue());
      case 8 -> new java.awt.Color(
          Integer.valueOf(string.substring(0, 2), 16).intValue(),
          Integer.valueOf(string.substring(2, 4), 16).intValue(),
          Integer.valueOf(string.substring(4, 6), 16).intValue(),
          Integer.valueOf(string.substring(6, 8), 16).intValue());
      default -> throw new IllegalArgumentException();
    };
    return of(color);
  }

  public static float hue(final float red, final float green, final float blue, final float max, final float min) {
    float hue = max - min;
    if (hue > 0.0f) {
      if (max == red) {
        hue = (green - blue) / hue;
        if (hue < 0.0f) {
          hue += 6.0f;
        }
      } else if (max == green) {
        hue = 2.0f + (blue - red) / hue;
      } else /* max == blue */ {
        hue = 4.0f + (red - green) / hue;
      }
      hue /= 6.0f;
    }
    return hue;
  }

  public static float max(final float red, final float green, final float blue) {
    float max = (red > green) ? red : green;
    return (max > blue) ? max : blue;
  }

  public static float min(final float red, final float green, final float blue) {
    float min = (red < green) ? red : green;
    return (min < blue) ? min : blue;
  }

  public static float normalize(final float q, final float p, final float color) {
    if (color < 1.0f) {
      return p + (q - p) * color;
    }
    if (color < 3.0f) {
      return q;
    }
    if (color < 4.0f) {
      return p + (q - p) * (4.0f - color);
    }
    return p;
  }

  public static float toFloat(final int value, final int maximum) {
    if (value > maximum) {
      throw new IllegalArgumentException("'" + value + "' > '" + maximum + "'");
    }
    return (value) / (float) maximum;
  }

  public static int toInteger(final float value, final int maximum) {
    return (int) (maximum * value + 0.5f);
  }

  public static String toString(final IColor color) {
    float[] compoments = color.getCompoments();
    return ColorSpaces.name(color.getColorSpace()) + " " + String.join(", ",
        IntStream.range(0, compoments.length)
            .mapToObj(i -> toString(color, i))
            .toList());
  }

  private static String toString(final IColor color, final int i) {
    return Integer.valueOf(toInteger(color.getComponent(i),
        color.getColorSpace().getNumComponents() == i
            ? 255
            : ColorSpaces.maximumValues(color.getColorSpace())[i]))
        .toString();
  }

  public static IColor color(final IColor min, final IColor max, final float factor) {
    if (!Objects.equals(min.getColorSpace(), max.getColorSpace())) {
      throw new IllegalArgumentException();
    }
    float[] minimumColorCompoments = min.getColorCompoments();
    float[] maximumColorCompoments = max.getColorCompoments();
    float[] colorCompoments = Arrays.copyOf(maximumColorCompoments, maximumColorCompoments.length);
    ColorSpace colorSpace = min.getColorSpace();
    for (int i = 0; i < colorCompoments.length; i++) {
      colorCompoments[i] = value(minimumColorCompoments[i], maximumColorCompoments[i], factor);
    }
    return new Color(colorSpace, colorCompoments, value(min.getOpacity(), max.getOpacity(), factor));
  }

  private static float value(final float startValue, final float endValue, final float factor) {
    if (startValue == endValue) {
      return startValue;
    }
    final float minimumValue = Math.min(startValue, endValue);
    final float maximumValue = Math.max(startValue, endValue);
    float difference = maximumValue - minimumValue;
    return startValue < endValue
        ? startValue + difference * factor
        : startValue - difference * factor;
  }

}
