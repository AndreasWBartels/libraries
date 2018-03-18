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
package net.anwiba.commons.swing.image;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.anwiba.commons.image.BufferedImageContainer;
import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageReader;
import net.anwiba.commons.image.ImageFileFilter;
import net.anwiba.commons.image.graphic.ClosableGraphicsBuider;
import net.anwiba.commons.image.graphic.IClosableGraphics;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.resource.reference.IResourceReference;
import net.anwiba.commons.resource.reference.ResourceReferenceUtilities;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.cancel.Canceler;

@SuppressWarnings("serial")
public class ImagePanel extends JComponent implements Scrollable {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImagePanel.class);
  private final IObjectModel<IImageContainer> imageContainerModel = new ObjectModel<>();
  private BufferedImage thumbnail = null;
  private Thread thread = null;
  private final ImageScaleBehavior scaleUp;
  private Rectangle bound;
  private int maxUnitIncrement = 2;
  private final IImageReader imageReader;

  public ImagePanel(final IImageReader imageReader, final IObjectModel<IResourceReference> imageFileModel) {
    this(imageReader, imageFileModel, ImageScaleBehavior.FIT);
  }

  public ImagePanel(
      final IImageReader imageReader,
      final IObjectModel<IResourceReference> imageFileModel,
      final ImageScaleBehavior scaleUp) {
    setAutoscrolls(true);
    this.imageReader = imageReader;
    this.scaleUp = scaleUp;
    final ImageFileFilter fileFilter = new ImageFileFilter();
    setPreferredSize(new Dimension(100, 100));
    setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    imageFileModel.addChangeListener(() -> {
      reset();
      load(imageFileModel, fileFilter);
    });
    addComponentListener(new ComponentListener() {

      @Override
      public void componentShown(final ComponentEvent e) {
        load(imageFileModel, fileFilter);
      }

      @Override
      public void componentResized(final ComponentEvent e) {
        load(imageFileModel, fileFilter);
      }

      @Override
      public void componentMoved(final ComponentEvent e) {
        // nothing to do
      }

      @Override
      public void componentHidden(final ComponentEvent e) {
        reset();
      }
    });
  }

  protected void load(final IObjectModel<IResourceReference> imageFileModel, final ImageFileFilter fileFilter) {
    final IResourceReference imageFile = imageFileModel.get();
    if (this.thread != null) {
      this.thread.interrupt();
    }
    if (isAccepted(fileFilter, imageFile)) {
      this.thread = new Thread(() -> {
        try {
          if (loadImage(imageFile)) {
            GuiUtilities.invokeLater(() -> {
              repaint();
            });
          }
        } catch (final IOException e) {
          logger.log(ILevel.DEBUG, e.getMessage(), e);
          final Image image = net.anwiba.commons.swing.icon.GuiIcons.ERROR_ICON.getLargeIcon().getImage();
          final BufferedImage bufferedImage = image instanceof BufferedImage
              ? (BufferedImage) image
              : new IConverter<Image, BufferedImage, RuntimeException>() {

                @Override
                public BufferedImage convert(final Image input) throws RuntimeException {
                  final BufferedImage bufferdImage = new BufferedImage(100, 60, BufferedImage.TYPE_INT_ARGB);
                  final Graphics g = bufferdImage.createGraphics();
                  try {
                    g.drawImage(image, 50 - (image.getWidth(null) / 2), 0, null);
                    g.setColor(Color.BLACK);
                    final String errorMessage = "reading faild";
                    final Rectangle2D bounds = getFontMetrics(getFont()).getStringBounds(errorMessage, g);
                    g.drawString(errorMessage, (int) (50 - bounds.getWidth() / 2), (int) (60 - bounds.getHeight() - 2));
                  } finally {
                    g.dispose();
                  }
                  return bufferdImage;
                }
              }.convert(image);;
          this.imageContainerModel.set(new BufferedImageContainer(bufferedImage));
          if (update()) {
            GuiUtilities.invokeLater(() -> {
              repaint();
            });
          }
        }
      });
      this.thread.start();
    }
  }

  private boolean isAccepted(final ImageFileFilter fileFilter, final IResourceReference imageFile) {
    try {
      if (ResourceReferenceUtilities.isFileSystemResource(imageFile)) {
        final File file = ResourceReferenceUtilities.getFile(imageFile);
        return file != null && file.isFile() && fileFilter.accept(file);
      }
      return imageFile != null;
    } catch (final URISyntaxException exception) {
      return false;
    }
  }

  protected void reset() {
    synchronized (this) {
      if (this.imageContainerModel.get() != null) {
        this.imageContainerModel.get().dispose();
      }
      this.imageContainerModel.set(null);
      this.thumbnail = null;
    }
    GuiUtilities.invokeLater(() -> repaint());
  }

  protected boolean loadImage(final IResourceReference imageFile) throws IOException {
    synchronized (this) {
      if (this.imageContainerModel.get() == null) {
        try {
          final IImageContainer container = this.imageReader.read(Canceler.DummyCanceler, imageFile);
          if (container == null) {
            return false;
          }
          this.imageContainerModel.set(container);
        } catch (final InterruptedException exception) {
          return false;
        }
      }
      return update();
    }
  }

  private boolean update() {
    try {
      final IImageContainer imageContainer = this.imageContainerModel.get();
      final int imageWidth = imageContainer.getWidth();
      final int imageHeight = imageContainer.getHeight();
      this.bound = imageBound(imageWidth, imageHeight);
      if (this.thumbnail != null
          && this.thumbnail.getWidth() == this.bound.width
          && this.thumbnail.getHeight() == this.bound.height) {
        return true;
      }
      final BufferedImage bufferImage = imageContainer.fit(this.bound.width, this.bound.height).asBufferImage();
      this.thumbnail = bufferImage;
      return true;
    } catch (final Exception exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return false;
    }
  }

  @Override
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);
    if (this.bound != null && this.thumbnail != null) {
      try (IClosableGraphics graphic = new ClosableGraphicsBuider((Graphics2D) g.create())
          .setColorRenderQuality()
          .setStrokeControlNormalize()
          .setAntiAliasingOn()
          .setAlphaInterpolationQuality()
          .setTextAntiAliasing(true)
          .setDitheringEnabled()
          .setRenderingQuality()
          .build()) {
        graphic.drawImage(this.thumbnail, this.bound.x, this.bound.y, this);
      }
    }
  }

  private synchronized Rectangle imageBound(final int imageWidth, final int imageHeight) {
    final Border border = getBorder();
    final Insets insets = (border != null) ? border.getBorderInsets(this) : new Insets(0, 0, 0, 0);
    final double imageAspectRatio = (double) imageWidth / (double) imageHeight;
    final double panelAspectRatio = (double) getWidth() / (double) getHeight();
    int x = 0;
    int y = 0;

    final Container parent = getParent();
    final Dimension size = parent != null ? parent.getSize() : new Dimension(256, 256);

    int width = size.width - insets.left - insets.right;
    int height = size.height - insets.top - insets.bottom;

    if (!ObjectUtilities.equals(this.scaleUp, ImageScaleBehavior.FIT) && imageWidth < width && imageHeight < height) {
      setMinimumSize(new Dimension(width, height));
      setPreferredSize(new Dimension(width, height));
      setMaximumSize(new Dimension(width, height));
      if (getParent() != null) {
        GuiUtilities.invokeLater(() -> getParent().doLayout());
      }
      x = (width - imageWidth) / 2;
      y = (height - imageHeight) / 2;
      return new Rectangle(x, y, imageWidth, imageHeight);
    }

    if (ObjectUtilities.equals(this.scaleUp, ImageScaleBehavior.ORGIN)) {
      setMinimumSize(new Dimension(imageWidth, imageHeight));
      setPreferredSize(new Dimension(imageWidth, imageHeight));
      setMaximumSize(new Dimension(imageWidth, imageHeight));
      if (getParent() != null) {
        GuiUtilities.invokeLater(() -> getParent().doLayout());
      }
      return new Rectangle(0, 0, imageWidth, imageHeight);
    }

    setMinimumSize(new Dimension(width, height));
    setPreferredSize(new Dimension(width, height));
    setMaximumSize(new Dimension(width, height));
    if (getParent() != null) {
      GuiUtilities.invokeLater(() -> getParent().doLayout());
    }
    if (imageAspectRatio < panelAspectRatio) {
      height = getHeight() - insets.top - insets.bottom;
      width = (int) (height * imageAspectRatio);
      x = (getWidth() - insets.left - insets.right - width) / 2;
      y = insets.top;
    } else {
      width = getWidth() - insets.left - insets.right;
      height = (int) (width / imageAspectRatio);
      x = insets.left;
      y = (getHeight() - insets.top - insets.bottom - height) / 2;
    }
    return new Rectangle(x, y, width, height);
  }

  public IObjectModel<IImageContainer> getImageContainerModel() {
    return this.imageContainerModel;
  }

  @Override
  public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
    if (orientation == SwingConstants.HORIZONTAL) {
      return visibleRect.width - this.maxUnitIncrement;
    }
    return visibleRect.height - this.maxUnitIncrement;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  public void setMaxUnitIncrement(final int pixels) {
    this.maxUnitIncrement = pixels;
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    final Container parent = getParent();
    final Dimension size = parent != null ? parent.getSize() : new Dimension(256, 256);
    if (this.imageContainerModel.get() != null) {
      switch (this.scaleUp) {
        case ORGIN: {
          final IImageContainer imageContainer = this.imageContainerModel.get();
          final int imageWidth = imageContainer.getWidth();
          final int imageHeight = imageContainer.getHeight();
          return size.width > imageWidth && size.height > imageHeight ? size : new Dimension(imageWidth, imageHeight);
        }
        default:
          break;
      }
    }
    return size;
  }

  @Override
  public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
    int currentPosition = 0;
    if (orientation == SwingConstants.HORIZONTAL) {
      currentPosition = visibleRect.x;
    } else {
      currentPosition = visibleRect.y;
    }
    if (direction < 0) {
      final int newPosition = currentPosition - (currentPosition / this.maxUnitIncrement) * this.maxUnitIncrement;
      return (newPosition == 0) ? this.maxUnitIncrement : newPosition;
    }
    return ((currentPosition / this.maxUnitIncrement) + 1) * this.maxUnitIncrement - currentPosition;
  }
}