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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.accessibility.AccessibleContext;
import javax.swing.ImageIcon;

import net.anwiba.commons.model.ObjectModel;

public class MutableImageIcon extends ImageIcon {

  private static final long serialVersionUID = 1L;
  final ObjectModel<ImageIcon> model = new ObjectModel<>();

  public MutableImageIcon(final ImageIcon icon) {
    this.model.set(icon);
  }

  public ObjectModel<ImageIcon> getModel() {
    return this.model;
  }

  @Override
  public int getIconHeight() {
    if (this.model.get() == null) {
      return 0;
    }
    return this.model.get().getIconHeight();
  }

  @Override
  public int getIconWidth() {
    if (this.model.get() == null) {
      return 0;
    }
    return this.model.get().getIconWidth();
  }

  @Override
  public void setImage(final Image image) {
    this.model.get().setImage(image);
  }

  @Override
  public void setDescription(final String description) {
    this.model.get().setDescription(description);
  }

  @Override
  public void setImageObserver(final ImageObserver observer) {
    this.model.get().setImageObserver(observer);
  }

  @Override
  public String getDescription() {
    return this.model.get().getDescription();
  }

  @Override
  public int getImageLoadStatus() {
    return this.model.get().getImageLoadStatus();
  }

  @Override
  public Image getImage() {
    return this.model.get().getImage();
  }

  @Override
  public ImageObserver getImageObserver() {
    return this.model.get().getImageObserver();
  }

  @Override
  public AccessibleContext getAccessibleContext() {
    return this.model.get().getAccessibleContext();
  }

  @Override
  public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y) {
    if (this.model.get() == null) {
      return;
    }
    this.model.get().paintIcon(c, g, x, y);
  }
}