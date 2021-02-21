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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import javax.swing.ImageIcon;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

public class GuiIcon implements IGuiIcon {

  private static ILogger logger = Logging.getLogger(GuiIcon.class.getName());
  private final IIconSize large;
  private final IIconSize medium;
  private final IIconSize small;
  private final boolean isDecorator;
  private final Function<String, URL> urlResolver;

  private static class UrlResolver implements Function<String, URL> {

    private final Function<String, URL> urlResolver;

    public UrlResolver() {
      this(null);
    }

    public UrlResolver(final Function<String, URL> urlResolver) {
      this.urlResolver = urlResolver == null
          ? path -> getClass().getResource(path)
          : urlResolver;
    }

    @Override
    public URL apply(final String path) {
      if (path.startsWith("file:")
          || path.startsWith("http:")
          || path.startsWith("https:")) {
        try {
          return new URL(path);
        } catch (MalformedURLException exception) {
          logger.log(ILevel.DEBUG, exception.getMessage(), exception);
          return null;
        }
      }
      final Class<?> clazz = this.urlResolver.getClass();
      String resourceUrl = getResourceUrl(clazz, path);
      if (resourceUrl == null) {
        logger.log(ILevel.FATAL, "Missing image resource url"); //$NON-NLS-1$
        return null;
      }

      return Optional.of(resourceUrl)
          .map(r -> this.urlResolver.apply(r))
          .orElseGet(() -> {
            logger.log(ILevel.FATAL,
                "Cannot find image resource: " + path
                    + " class: " + clazz
                    + " search path: " + resourceUrl); //$NON-NLS-1$
            return null;
          });
    }

    private String getResourceUrl(final Class<?> helper, final String name) {
      final String[] pakkage = helper.getPackage().getName().split("\\.");
      final Path iconsPath = Paths.get(name).normalize();
      final Path packagePath = Paths.get("", pakkage);
      if (iconsPath.startsWith(packagePath)) {
        return toString(packagePath.relativize(iconsPath));
      }
      return toString(iconsPath);
    }

    private String toString(final Path path) {
      String string = null;
      for (Path item : path) {
        if (string == null) {
          string = item.toString();
        } else {
          string += "/" + item.toString();
        }
      }
      return string;
    }
  }

  public static GuiIcon of(final String small, final String medium, final String large) {
    return new GuiIcon(new UrlResolver(), IconSize.small(small), IconSize.medium(medium), IconSize.large(large), false);
  }

  public static GuiIcon
      of(final Function<String, URL> urlResolver, final String small, final String medium, final String large) {
    return new GuiIcon(new UrlResolver(
        urlResolver), IconSize.small(small), IconSize.medium(medium), IconSize.large(large), false);
  }

  public static GuiIcon
      of(final IIconSize small, final IIconSize medium, final IIconSize large, final boolean isDecorator) {
    return new GuiIcon(new UrlResolver(), small, medium, large, isDecorator);
  }

  public static GuiIcon of(final Class clazz,
      final IIconSize small,
      final IIconSize medium,
      final IIconSize large) {
    return new GuiIcon(new UrlResolver(r -> clazz.getResource(r)), small, medium, large, false);
  }

  public static GuiIcon of(final Class clazz,
      final IIconSize small,
      final IIconSize medium,
      final IIconSize large,
      final boolean isDecorator) {
    return new GuiIcon(new UrlResolver(r -> clazz.getResource(r)), small, medium, large, isDecorator);
  }

  public static GuiIcon of(final Function<String, URL> urlResolver,
      final IIconSize small,
      final IIconSize medium,
      final IIconSize large) {
    return new GuiIcon(new UrlResolver(urlResolver), small, medium, large, false);
  }

  public static GuiIcon of(final Function<String, URL> urlResolver,
      final IIconSize small,
      final IIconSize medium,
      final IIconSize large,
      final boolean isDecorator) {
    return new GuiIcon(new UrlResolver(urlResolver), small, medium, large, isDecorator);
  }

  private GuiIcon(final Function<String, URL> urlResolver,
      final IIconSize small,
      final IIconSize medium,
      final IIconSize large,
      final boolean isDecorator) {
    this.urlResolver = urlResolver;
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
    final URL resource = this.urlResolver.apply(name);
    if (resource != null) {
      return new ImageIcon(resource);
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
    final IGuiIconSizeVisitor<ImageIcon> visitor = new IGuiIconSizeVisitor<>() {

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