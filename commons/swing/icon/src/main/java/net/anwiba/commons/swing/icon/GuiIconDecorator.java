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
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

import net.anwiba.commons.lang.functional.IAcceptor;

public class GuiIconDecorator {

  private final GuiIconSize size;
  private ImageIcon icon;

  public GuiIconDecorator(final GuiIconSize size, final ImageIcon icon) {
    this.size = size;
    this.icon = icon;
  }

  public GuiIconDecorator add(final IAcceptor<Void> acceptor, @SuppressWarnings("hiding") final GuiIcon icon) {
    if (acceptor.accept(null)) {
      this.icon = decorate(this.size, this.icon, icon);
    }
    return this;
  }

  public ImageIcon decorate() {
    return this.icon;
  }

  public static IGuiIcon icon(final GuiIcon icon, final GuiIcon decorationIcon, final DecorationPosition position) {
    return new DecoratedGuiIcon(icon, decorationIcon, position);
  }

  public static ImageIcon decorate(final GuiIconSize size, final IGuiIcon icon, final IGuiIconDecoration decoration) {
    return decorate(size, icon, decoration.getGuiIcon());
  }

  public static ImageIcon decorate(final GuiIconSize size, final IGuiIcon icon, final IGuiIcon decorationIcon) {
    return decorate(size, DecorationPosition.LowerLeft, icon, decorationIcon);
  }

  public static ImageIcon decorate(
      final GuiIconSize size,
      final DecorationPosition position,
      final ImageIcon icon,
      final String extention,
      final Color fontColor,
      final Color backGroundColor) {
    final IGuiIcon decorationIcon = new StringDecorationIcon(backGroundColor, fontColor, extention);
    return decorate(size, position, icon, decorationIcon);
  }

  public static ImageIcon decorate(
      final GuiIconSize size,
      final DecorationPosition position,
      final IGuiIcon icon,
      final IGuiIcon decorationIcon) {
    final IGuiIconSizeVisitor<ImageIcon> visitor = new IGuiIconSizeVisitor<>() {

      @Override
      public ImageIcon vistSmall() {
        return decorate(size, position, icon.getSmallIcon(), decorationIcon);
      }

      @Override
      public ImageIcon vistMedium() {
        return decorate(size, position, icon.getMediumIcon(), decorationIcon);
      }

      @Override
      public ImageIcon vistLarge() {
        return decorate(size, position, icon.getLargeIcon(), decorationIcon);
      }

    };
    return size.accept(visitor);
  }

  public static ImageIcon decorate(final GuiIconSize size, final ImageIcon icon, final IGuiIconDecoration decoration) {
    final IGuiIcon decorationIcon = decoration.getGuiIcon();
    return decorate(size, icon, decorationIcon);
  }

  public static ImageIcon decorate(final GuiIconSize size, final ImageIcon icon, final IGuiIcon decorationIcon) {
    return decorate(size, DecorationPosition.LowerLeft, icon, decorationIcon);
  }

  public static ImageIcon decorate(
      final GuiIconSize size,
      final DecorationPosition position,
      final ImageIcon icon,
      final IGuiIcon decorationIcon) {
    final IGuiIconSizeVisitor<ImageIcon> visitor = new IGuiIconSizeVisitor<>() {

      @Override
      public ImageIcon vistSmall() {
        if (decorationIcon.isDecorator()) {
          return add(icon, decorationIcon.getSmallIcon());
        }
        return add(16, position, icon, decorationIcon.getSmallIcon());
      }

      @Override
      public ImageIcon vistMedium() {
        if (decorationIcon.isDecorator()) {
          return add(icon, decorationIcon.getMediumIcon());
        }
        return add(22, position, icon, decorationIcon.getSmallIcon());
      }

      @Override
      public ImageIcon vistLarge() {
        if (decorationIcon.isDecorator()) {
          return add(icon, decorationIcon.getLargeIcon());
        }
        return add(32, position, icon, decorationIcon.getSmallIcon());
      }

    };
    return size.accept(visitor);
  }

  protected static ImageIcon add(
      final ImageIcon icon,
      final ImageIcon decoration,
      final int x,
      final int y,
      final int width,
      final int height) {
    final BufferedImage image =
        new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphic = (Graphics2D) image.getGraphics();
    try {
      graphic.drawImage(icon.getImage(), 0, 0, new ImageObserver() {

        @SuppressWarnings("hiding")
        @Override
        public boolean imageUpdate(
            final Image img,
            final int infoflags,
            final int x,
            final int y,
            final int width,
            final int height) {
          return true;
        }

      });
      graphic.drawImage(decoration.getImage(), x, y, width, height, new ImageObserver() {

        @SuppressWarnings("hiding")
        @Override
        public boolean imageUpdate(
            final Image img,
            final int infoflags,
            final int x,
            final int y,
            final int width,
            final int height) {
          return true;
        }

      });
      return new ImageIcon(image);
    } finally {
      graphic.dispose();
    }
  }

  public static ImageIcon add(final ImageIcon icon, final ImageIcon decoration) {
    final BufferedImage image =
        new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphic = (Graphics2D) image.getGraphics();
    try {
      graphic.drawImage(icon.getImage(), 0, 0, new ImageObserver() {
        
        @Override
        public boolean imageUpdate(
            final Image img,
            final int infoflags,
            final int x,
            final int y,
            final int width,
            final int height) {
          return true;
        }
        
      });
      graphic.drawImage(decoration.getImage(), 0, 0, new ImageObserver() {
        
        @Override
        public boolean imageUpdate(
            final Image img,
            final int infoflags,
            final int x,
            final int y,
            final int width,
            final int height) {
          return true;
        }
        
      });
      return new ImageIcon(image);
    } finally {
      graphic.dispose();
    }
  }

  public static ImageIcon add(final ImageIcon icon, final ImageIcon decoration, DecorationPosition position) {
    if (icon.getIconWidth() == decoration.getIconWidth() && icon.getIconHeight() == decoration.getIconHeight()) {
      int width = icon.getIconWidth()/2;
      int height = icon.getIconHeight()/2;
      return add(icon, resize(decoration, width, height), position);
    }
    int width = decoration.getIconWidth();
    int height = decoration.getIconHeight();
    final Point point = getPosition(icon.getIconWidth(), position, width, height);
    return add(icon, decoration, point.x, point.y, width, height);
  }

  private static ImageIcon resize(ImageIcon icon, int width, int height) {
    final BufferedImage image =
        new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphic = (Graphics2D) image.getGraphics();
    try {
      graphic.drawImage(icon.getImage(), 0, 0, width, height, new ImageObserver() {

        @Override
        public boolean imageUpdate(
            final Image img,
            final int infoflags,
            final int x,
            final int y,
            final int width,
            final int height) {
          return true;
        }

      });
      return new ImageIcon(image);
    } finally {
      graphic.dispose();
    }
  }

  private static ImageIcon add(
      final int iconSize,
      final DecorationPosition position,
      final ImageIcon icon,
      final ImageIcon decorationIcon) {
    final int width = getSize(iconSize, position);
    final int height = getSize(iconSize, position);
    final Point point = getPosition(iconSize, position, width, height);
    return add(icon, decorationIcon, point.x, point.y, width, height);
  }

  public static int getSize(final int iconSize, final DecorationPosition position) {
    switch (position) {
      case LowerLeft:
      case LowerRight:
      case UpperLeft:
      case UpperRight: {
        return (iconSize * 2) / 3;
      }
      case Fill: {
        return iconSize;
      }
    }
    return (iconSize * 2) / 3;
  }

  private static Point getPosition(
      final int size,
      final DecorationPosition position,
      final int width,
      final int height) {
    switch (position) {
      case LowerLeft: {
        return new Point(0, size - height);
      }
      case LowerRight: {
        return new Point(size - width, size - height);
      }
      case UpperLeft: {
        return new Point(0, 0);
      }
      case UpperRight: {
        return new Point(size - getSize(size, position), 0);
      }
      case Fill: {
        return new Point(0, 0);
      }
    }
    return new Point(0, 0);
  }
}
