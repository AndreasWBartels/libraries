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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.ImageFileFilter;
import net.anwiba.commons.image.ImageReaderUtilities;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.process.cancel.Canceler;
import net.anwiba.commons.resource.reference.IResourceReference;
import net.anwiba.commons.resource.reference.IResourceReferenceHandler;
import net.anwiba.commons.swing.utilities.GuiUtilities;

@SuppressWarnings("serial")
public class ImagePanel extends JComponent {

  private IImageContainer imageContainer = null;
  private BufferedImage thumbnail = null;
  private Thread thread = null;
  private final IResourceReferenceHandler resourceReferenceHandler;

  public ImagePanel(
      final IResourceReferenceHandler resourceReferenceHandler,
      final IObjectModel<IResourceReference> imageFileModel) {
    this.resourceReferenceHandler = resourceReferenceHandler;
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
    if (isAccepted(fileFilter, imageFile)) {
      if (this.thread != null) {
        this.thread.interrupt();
      }
      this.thread = new Thread(() -> {
        try {
          if (loadImage(imageFile)) {
            GuiUtilities.invokeLater(() -> {
              repaint();
            });
          }
        } catch (final IOException e) {
          // nothing to do
        }
      });
      this.thread.start();
    }
  }

  private boolean isAccepted(final ImageFileFilter fileFilter, final IResourceReference imageFile) {
    try {
      if (this.resourceReferenceHandler.isFileSystemResource(imageFile)) {
        final File file = this.resourceReferenceHandler.getFile(imageFile);
        return file != null && file.isFile() && fileFilter.accept(file);
      }
      return imageFile != null;
    } catch (final URISyntaxException exception) {
      return false;
    }
  }

  protected void reset() {
    if (this.imageContainer != null) {
      this.imageContainer.dispose();
    }
    this.imageContainer = null;
    this.thumbnail = null;
    GuiUtilities.invokeLater(() -> repaint());
  }

  protected synchronized boolean loadImage(final IResourceReference imageFile) throws IOException {
    if (this.imageContainer == null) {
      try {
        final IImageContainer container = ImageReaderUtilities.read(Canceler.DummyCancler, imageFile);
        if (container == null) {
          return false;
        }
        this.imageContainer = container;
      } catch (final InterruptedException exception) {
        return false;
      }
    }
    final Rectangle bound = imageBound();
    if (this.thumbnail != null
        && this.thumbnail.getWidth() == bound.width
        && this.thumbnail.getHeight() == bound.height) {
      return true;
    }
    this.thumbnail = this.imageContainer.fit(bound.width, bound.height).asBufferImage();
    return true;
  }

  @Override
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);
    if (this.thumbnail != null) {
      final Rectangle bound = imageBound();
      g.drawImage(this.thumbnail, bound.x, bound.y, this);
    }
  }

  private Rectangle imageBound() {
    Insets insets = new Insets(0, 0, 0, 0);
    final Border border = getBorder();
    if (border != null) {
      insets = border.getBorderInsets(this);
    }
    final double imageAspectRatio = (double) this.imageContainer.getWidth() / (double) this.imageContainer.getHeight();
    final double panelAspectRatio = (double) getWidth() / (double) getHeight();
    int height = 0;
    int width = 0;
    int x = 0;
    int y = 0;
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
}