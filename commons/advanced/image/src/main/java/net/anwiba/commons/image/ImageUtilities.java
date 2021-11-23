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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package net.anwiba.commons.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import net.anwiba.commons.image.codec.IImageCodecVisitor;
import net.anwiba.commons.image.codec.ImageCodec;
import net.anwiba.commons.reference.utilities.FileUtilities;

public class ImageUtilities {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageUtilities.class);

  private static final Dimension DEFAULT_IMAGE_SIZE = new Dimension(20, 20);
  private static final String MISSING_IMAGE_DATA =
      "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAOCAYAAAASVl2WAAAAMElEQVR42mNgoAr4D0T/wRQOMXQFMD6KJmQBbCai6MKqAKfRRJuA1w0EfUEwHGgLAHhnTbO/SQfEAAAAAElFTkSuQmCC";

  public static BufferedImage createCompatibleImage(final BufferedImage image, final int width, final int height) {
    ColorModel colorModel = image.getColorModel();
    return new BufferedImage(
        colorModel,
        image.getRaster().createCompatibleWritableRaster(width, height),
        colorModel.isAlphaPremultiplied(),
        new Hashtable<>());
  }

  public static BufferedImage create(final int width, final int height) {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  }

  public static BufferedImage create(final int width, final int height, final Color color) {
    Graphics2D graphic = null;
    try {
      BufferedImage image = create(width, height);
      final Graphics graphics = image.getGraphics();
      graphics.setColor(color);
      graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
      return image;
    } finally {
      if (graphic != null) {
        graphic.dispose();
      }
    }
  }

  public static BufferedImage getEmptyImage(final Dimension size, final ImageCodec imageCodec) {
    if (imageCodec == null) {
      return getTransparentImage(size);
    }
    final IImageCodecVisitor<BufferedImage, RuntimeException> visitor = new IImageCodecVisitor<>() {

      @Override
      public BufferedImage visitUnknown() {
        return getTransparentImage(size);
      }

      @Override
      public BufferedImage visitPng() {
        return getTransparentImage(size);
      }

      @Override
      public BufferedImage visitJpeg() {
        return getNonTransparentImage(size);
      }

      @Override
      public BufferedImage visitBmp() throws RuntimeException {
        return getNonTransparentImage(size);
      }

      @Override
      public BufferedImage visitTiff() throws RuntimeException {
        return getNonTransparentImage(size);
      }

      @Override
      public BufferedImage visitGif() throws RuntimeException {
        return getTransparentImage(size);
      }

    };
    return imageCodec.accept(visitor);
  }

  public static BufferedImage createOpaqueImage(final BufferedImage transparentImage) {
    Graphics2D graphic = null;
    try {
      final BufferedImage opaqueImage = new BufferedImage(transparentImage.getWidth(),
          transparentImage.getHeight(),
          BufferedImage.TYPE_INT_RGB);
      final Graphics graphics = opaqueImage.getGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, opaqueImage.getWidth(), opaqueImage.getHeight());
      graphics.drawImage(transparentImage, 0, 0, null);
      return opaqueImage;
    } finally {
      if (graphic != null) {
        graphic.dispose();
      }
    }
  }

  public static BufferedImage getTransparentImage(final Dimension size) {
    return create(size.width, size.height);
  }

  public static BufferedImage getNonTransparentImage(final Dimension size) {
    Graphics2D graphic = null;
    try {
      final BufferedImage image = new BufferedImage(
          size.width,
          size.height,
          BufferedImage.TYPE_INT_RGB);
      graphic = (Graphics2D) image.getGraphics();
      graphic.setColor(Color.WHITE);
      graphic.fillRect(0, 0, size.width, size.height);
      return image;
    } finally {
      if (graphic != null) {
        graphic.dispose();
      }
    }
  }

  public static BufferedImage toOpacity(final RenderingHints hints, final RenderedImage source, final float factor) {
    BufferedImage image = toRGBA(hints, source);
    applyOpacity(hints, image, AlphaComposite.DST_IN, factor);
    return drawInto(hints,
        image,
        getTransparentImage(new Dimension(source.getWidth(), source.getHeight())));
  }

  private static void applyOpacity(final RenderingHints hints,
      final BufferedImage image,
      final int rule,
      final float factor) {
    image.coerceData(true);
    Graphics2D graphics = image.createGraphics();
    try {
      graphics.setRenderingHints(hints);
      graphics.setClip(0, 0, image.getWidth(), image.getHeight());
      graphics.setComposite(AlphaComposite.getInstance(rule, factor));
      graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    } finally {
      graphics.dispose();
    }
  }

  public static BufferedImage toRGBA(final RenderingHints hints, final RenderedImage source) {
    if (isRGBA(source)) {
      return (BufferedImage) source;
    }
    return drawInto(hints, source, getTransparentImage(new Dimension(source.getWidth(), source.getHeight())));
  }

  private static BufferedImage drawInto(final RenderingHints hints,
      final RenderedImage source,
      final BufferedImage target) {
    Graphics2D graphics = target.createGraphics();
    try {
      graphics.setRenderingHints(hints);
      graphics.drawRenderedImage(source, new AffineTransform());
      return target;
    } finally {
      graphics.dispose();
    }
  }

  private static boolean isRGBA(final RenderedImage source) {
    ColorModel colorModel = source.getColorModel();
    return source instanceof BufferedImage
        && colorModel.hasAlpha()
        && colorModel.isAlphaPremultiplied()
        && colorModel.getColorSpace().isCS_sRGB();
  }

  public static String getMimeType(byte[] bytes, String name) {
    if (bytes != null) {
      try {
        ImageInfo info = Imaging.getImageInfo(bytes);
        if (info != null) {
          return info.getMimeType();
        }
      } catch (ImageReadException | IOException e) {
        logger.warning("Unable to determine mimeType for image file " + name, e);
      }
    }
    String extension = FileUtilities.getExtension(name).toLowerCase();
    return "image/" + extension;
  }

  public static String convertToBase64(byte[] bytes, String mimeType) {
    if (bytes == null) {
      return MISSING_IMAGE_DATA;
    }
    String base64Image = Base64.getEncoder().encodeToString(bytes);
    return "data:" + mimeType + ";base64," + base64Image;
  }

  public static String convertToBase64(BufferedImage image, String imageCodec) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try (OutputStream b64 = Base64.getEncoder().wrap(os)) {
      ImageIO.write(image, imageCodec, b64);
      b64.flush();
      return "data:image/"
          + imageCodec
          + ";base64,"
          + os.toString(StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      logger.error("Unable to convert image to base64!", e);
      return MISSING_IMAGE_DATA;
    }
  }

  public static Dimension getImageSize(byte[] bytes, String name) {
    if (bytes == null) {
      return DEFAULT_IMAGE_SIZE;
    }
    try {
      return Imaging.getImageSize(bytes);
    } catch (IOException | ImageReadException e) {
      logger.error("Unable to get image dimensions for file " + name, e);
      return DEFAULT_IMAGE_SIZE;
    }
  }

  public static ImageIcon toImageIcon(Icon icon) {
    if (icon instanceof ImageIcon) {
      return (ImageIcon) icon;
    } else {
      BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics graphics = image.getGraphics();
      try {
        icon.paintIcon(null, graphics, 0, 0);
        return new ImageIcon(image);
      } finally {
        graphics.dispose();
      }
    }
  }
}
