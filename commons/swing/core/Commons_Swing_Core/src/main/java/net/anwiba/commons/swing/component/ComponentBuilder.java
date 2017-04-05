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

package net.anwiba.commons.swing.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.anwiba.commons.lang.object.ObjectPair;

public class ComponentBuilder implements IComponentAdder {

  private Dimension size = new Dimension();
  private Dimension preferredSize = null;
  private Dimension minimumSize = null;
  private Dimension maximumSize = null;
  private Border border = null;
  private Color backGroundColor = null;
  private LayoutManager layoutManager = null;
  private final List<ObjectPair<Component, Object>> components = new ArrayList<>();
  private boolean isOptimizedDrawingEnabled = true;

  private void reset() {
    this.size = new Dimension();
    this.minimumSize = null;
    this.maximumSize = null;
    this.preferredSize = null;
    this.border = null;
    this.backGroundColor = null;
    this.layoutManager = null;
    this.components.clear();
    this.isOptimizedDrawingEnabled = true;
  }

  @Override
  public ComponentBuilder add(final Component component) {
    this.components.add(new ObjectPair<>(component, null));
    return this;
  }

  @Override
  public ComponentBuilder add(final Component component, final Object constaints) {
    this.components.add(new ObjectPair<>(component, constaints));
    return this;
  }

  public ComponentBuilder setSize(final int width, final int height) {
    this.size = new Dimension(width, height);
    return this;
  }

  public ComponentBuilder setEmptyBorder() {
    this.border = BorderFactory.createEmptyBorder();
    return this;
  }

  public ComponentBuilder setLineBorder(final Color color, final int width) {
    this.border = BorderFactory.createLineBorder(color, width);
    return this;
  }

  public ComponentBuilder setMinimumSize(final int width, final int height) {
    this.minimumSize = new Dimension(width, height);
    return this;
  }

  public ComponentBuilder setMaximumSize(final int width, final int height) {
    this.maximumSize = new Dimension(width, height);
    return this;
  }

  public ComponentBuilder setPreferredSize(final int width, final int height) {
    this.preferredSize = new Dimension(width, height);
    return this;
  }

  public ComponentBuilder setBackGroundColor(final Color backGroundColor) {
    this.backGroundColor = backGroundColor;
    return this;
  }

  public ComponentBuilder setLayoutManager(final LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    return this;
  }

  public JComponent build() {
    final JPanel panel = this.isOptimizedDrawingEnabled ? new JPanel() : new JPanel() {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isOptimizedDrawingEnabled() {
        return false;
      }
    };
    panel.setSize(this.size);
    panel.setMinimumSize(this.minimumSize);
    panel.setMaximumSize(this.maximumSize);
    panel.setPreferredSize(this.preferredSize);
    panel.setBorder(this.border);
    if (this.backGroundColor != null) {
      panel.setBackground(this.backGroundColor);
    }
    if (this.layoutManager != null) {
      panel.setLayout(this.layoutManager);
    }
    if (this.layoutManager == null && this.components.size() == 1) {
      panel.setLayout(new GridLayout(1, 1));
    }
    for (final ObjectPair<Component, Object> pair : this.components) {
      panel.add(pair.getFirstObject(), pair.getSecondObject());
    }
    reset();
    return panel;
  }

  public ComponentBuilder setOptimizedDrawing(final boolean isOptimizedDrawingEnabled) {
    this.isOptimizedDrawingEnabled = isOptimizedDrawingEnabled;
    return this;
  }
}
