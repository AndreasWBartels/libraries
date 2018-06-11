/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.icon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.swing.ImageIcon;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.reference.utilities.UriUtilities;
import net.anwiba.commons.utilities.lang.ClassLoaderUtilities;

public class GuiIcon implements IGuiIcon {

  private static ILogger logger = Logging.getLogger(GuiIcon.class.getName());
  private final IIconSize large;
  private final IIconSize medium;
  private final IIconSize small;
  private final boolean isDecorator;

  public GuiIcon(final String small, final String medium, final String large) {
    this(IconSize.small(small), IconSize.medium(medium), IconSize.large(large), false);
  }

  public GuiIcon(final IIconSize small, final IIconSize medium, final IIconSize large, final boolean isDecorator) {
    this.small = small;
    this.medium = medium;
    this.large = large;
    this.isDecorator = isDecorator;
  }

  @Override
  public boolean isDecorator() {
    return this.isDecorator;
  }

  @Override
  public ImageIcon getSmallIcon() {
    return getIcon(this.small.getPath(), this.small.getSize());
  }

  @Override
  public ImageIcon getMediumIcon() {
    return getIcon(this.medium.getPath(), this.medium.getSize());
  }

  @Override
  public ImageIcon getLargeIcon() {
    return getIcon(this.large.getPath(), this.large.getSize());
  }

  private ImageIcon getIcon(final String name, final int size) {
    final URL resource = getClass().getClassLoader().getResource(name);
    if (resource != null) {
      return new ImageIcon(resource);
    }
    final URI[] uris = ClassLoaderUtilities.getClassPathUris(getClass().getClassLoader());
    for (final URI uri : uris) {
      if (UriUtilities.isFileUri(uri)) {
        final File file = new File(new File(uri), name);
        if (file.canRead()) {
          try (InputStream inputStream = new FileInputStream(file)) {
            final byte[] array = IoUtilities.toByteArray(inputStream);
            return new ImageIcon(array);
          } catch (final IOException exception) {
            // nothing to do
          }
        }
      }
    }
    logger.log(ILevel.FATAL, "Cannot find image resource: " + name); //$NON-NLS-1$
    final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphic = (Graphics2D) image.getGraphics();
    try {
      graphic.setBackground(Color.RED);
      return new ImageIcon(image);
    } finally {
      graphic.dispose();
    }
  }

  @Override
  public ImageIcon getIcon(final GuiIconSize size) {
    final IGuiIconSizeVisitor<ImageIcon> visitor = new IGuiIconSizeVisitor<ImageIcon>() {

      @Override
      public ImageIcon vistSmall() {
        return getSmallIcon();
      }

      @Override
      public ImageIcon vistMedium() {
        return getMediumIcon();
      }

      @Override
      public ImageIcon vistLarge() {
        return getLargeIcon();
      }
    };
    return size.accept(visitor);
  }
}
