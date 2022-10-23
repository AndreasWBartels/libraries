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
import java.util.List;
import java.util.Objects;

public class ColorSpaces {

  public static final String CMYK = "CMYK";
  public static final String GRAY = "GRAY";
  public static final String HLS = "HLS";
  public static final String HSV = "HSV";
  public static final String RGB = "RGB";

  public static final String red = "Red";
  public static final String green = "Green";
  public static final String blue = "Blue";

  public static final String grey = "Gray";

  public static final String hue = "Hue";
  public static final String lightness = "Lightness";
  public static final String saturation = "Saturation";
  public static final String value = "Value";

  public static final String cyan = "Cyan";
  public static final String magenta = "Magenta";
  public static final String yellow = "Yellow";
  public static final String black = "Black";

  public static final ColorSpace CS_CMYK = CmykColorSpace.getInstance();
  public static final ColorSpace CS_GRAY = ColorSpace.getInstance(ColorSpace.CS_GRAY);
  public static final ColorSpace CS_HLS = HlsColorSpace.getInstance();
  public static final ColorSpace CS_HSV = HsvColorSpace.getInstance();
  public static final ColorSpace CS_RGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);

  private ColorSpaces() {
  }

  public static List<ColorSpace> colorSpaces() {
    return List.of(ColorSpaces.CS_RGB //
        , ColorSpaces.CS_CMYK //
        , ColorSpaces.CS_HLS //
        , ColorSpaces.CS_HSV //
        , ColorSpaces.CS_GRAY //
    );
  }

  public static List<String> names() {
    return List.of(CMYK, GRAY, HLS, HSV, RGB);
  }

  public static String name(final ColorSpace colorSpace) {
    return name(colorSpace.getType());
  }

  private static String name(final int type) {
    return switch (type) {
      case ColorSpace.TYPE_CMYK -> CMYK;
      case ColorSpace.TYPE_GRAY -> GRAY;
      case ColorSpace.TYPE_HLS -> HLS;
      case ColorSpace.TYPE_HSV -> HSV;
      case ColorSpace.TYPE_RGB -> RGB;
      default -> throw new IllegalArgumentException("Unexpected value: " + type);
    };
  }

  public static String acronym(final ColorSpace colorSpace, final int index) {
    return acronym(colorSpace.getType(), index);
  }

  private static String acronym(final int type, final int index) {
    return switch (type) {
      case ColorSpace.TYPE_CMYK -> "CMYK".substring(index, index + 1);
      case ColorSpace.TYPE_GRAY -> "G".substring(index, index + 1);
      case ColorSpace.TYPE_HLS -> "HLS".substring(index, index + 1);
      case ColorSpace.TYPE_HSV -> "HSV".substring(index, index + 1);
      case ColorSpace.TYPE_RGB -> "RGB".substring(index, index + 1);
      default -> throw new IllegalArgumentException("Unexpected value: " + type);
    };
  }

  public static int[] types() {
    return new int[] { ColorSpace.TYPE_CMYK,
        ColorSpace.TYPE_GRAY,
        ColorSpace.TYPE_HLS,
        ColorSpace.TYPE_HSV,
        ColorSpace.TYPE_RGB };
  }

  public static ColorSpace colorSpace(final int type) {
    return switch (type) {
      case ColorSpace.TYPE_CMYK -> CS_CMYK;
      case ColorSpace.TYPE_GRAY -> CS_GRAY;
      case ColorSpace.TYPE_HLS -> CS_HLS;
      case ColorSpace.TYPE_HSV -> CS_HSV;
      case ColorSpace.TYPE_RGB -> CS_RGB;
      default -> throw new IllegalArgumentException("Unexpected value: " + type);
    };
  }

  public static ColorSpace colorSpace(final String name) {
    return switch (name.toUpperCase()) {
      case CMYK -> CS_CMYK;
      case GRAY -> CS_GRAY;
      case HLS -> CS_HLS;
      case HSV -> CS_HSV;
      case RGB -> CS_RGB;
      default -> throw new IllegalArgumentException("Unexpected value: " + name);
    };
  }

  public static int[] maximumValues(final ColorSpace colorSpace) {
    return maximumValues(colorSpace.getType());
  }

  private static int[] maximumValues(final int type) {
    return switch (type) {
      case ColorSpace.TYPE_CMYK -> new int[] { 255, 255, 255, 255 };
      case ColorSpace.TYPE_GRAY -> new int[] { 255 };
      case ColorSpace.TYPE_HLS -> new int[] { 360, 100, 100 };
      case ColorSpace.TYPE_HSV -> new int[] { 360, 100, 100 };
      case ColorSpace.TYPE_RGB -> new int[] { 255, 255, 255 };
      default -> throw new IllegalArgumentException("Unexpected value: " + type);
    };
  }

  public static int maximumValue(final ColorSpace colorSpace, final int index) {
    return maximumValue(colorSpace.getType(), index);
  }

  private static int maximumValue(final int type, final int index) {
    int[] maximumValues = maximumValues(type);
    return index >= maximumValues.length ? 255 : maximumValues[index];
  }

  public static IColor maximumColor(final ColorSpace colorSpace, final int index) {
    int maximumValue = ColorSpaces.maximumValue(colorSpace, index);
    final IColorBuilder builder = Colors.builder(colorSpace)
        .min()
        .component(index, maximumValue);
    if (Objects.equals(colorSpace, CS_HLS)) {
      if (index == 0) {
        builder.component(1, 0.5f);
        builder.component(2, 1f);
      }
      if (index == 2) {
        builder.component(0, 0f);
        builder.component(1, 0f);
      }
    }
    if (Objects.equals(colorSpace, CS_HSV)) {
      if (index == 0) {
        builder.component(1, 1f);
        builder.component(2, 1f);
      }
      if (index == 1) {
        builder.component(0, 0f);
        builder.component(2, 0f);
      }
    }
    return builder.build();
  }

  public static IColor minimumColor(final ColorSpace colorSpace, final int index) {
    final IColorBuilder builder = Colors.builder(colorSpace).min();
    if (Objects.equals(colorSpace, CS_HLS)) {
      if (index == 0) {
        builder.component(1, 0.5f);
        builder.component(2, 1f);
      }
      if (index == 2) {
        builder.component(0, 0f);
        builder.component(1, 1f);
      }
    }
    if (Objects.equals(colorSpace, CS_HSV)) {
      if (index == 0) {
        builder.component(1, 1f);
        builder.component(2, 1f);
      }
      if (index == 1) {
        builder.component(0, 0f);
        builder.component(2, 1f);
      }
    }
    if (isAlpha(colorSpace, index)) {
      builder.component(index, 0f);
    }
    return builder.build();
  }

  public static int minimumValue(final ColorSpace colorSpace, final int index) {
    return 0;
  }

  public static boolean isAlpha(final ColorSpace colorSpace, final int index) {
    return index == colorSpace.getNumComponents();
  }

}
