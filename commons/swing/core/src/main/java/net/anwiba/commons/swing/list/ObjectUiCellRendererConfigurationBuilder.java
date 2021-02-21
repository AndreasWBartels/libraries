/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.swing.list;

import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.anwiba.commons.swing.ui.IObjectUiCellRendererConfiguration;

public class ObjectUiCellRendererConfigurationBuilder {

  private Border border = null;
  private int iconTextGap = 2;
  private int verticalAlignment = SwingConstants.CENTER;
  private int verticalTextPosition = SwingConstants.CENTER;
  private int horizontalTextPosition = SwingConstants.TRAILING;
  private int horizontalAlignment = SwingConstants.LEADING;

  public ObjectUiCellRendererConfigurationBuilder setIconTextGap(final int iconTextGap) {
    this.iconTextGap = iconTextGap;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setVerticalTextPosition(final int verticalTextPosition) {
    this.verticalTextPosition = verticalTextPosition;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setHorizontalTextPosition(final int horizontalTextPosition) {
    this.horizontalTextPosition = horizontalTextPosition;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setHorizontalAlignment(final int horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setHorizontalAlignmentLeft() {
    this.horizontalAlignment = SwingConstants.LEFT;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setHorizontalAlignmentRight() {
    this.horizontalAlignment = SwingConstants.RIGHT;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setHorizontalAlignmentCenter() {
    this.horizontalAlignment = SwingConstants.CENTER;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setHorizontalAlignmentLeading() {
    this.horizontalAlignment = SwingConstants.LEADING;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setHorizontalAlignmentTrailing() {
    this.horizontalAlignment = SwingConstants.TRAILING;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setBorder(final Border border) {
    this.border = border;
    return this;
  }

  public ObjectUiCellRendererConfigurationBuilder setVerticalAlignment(final int verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
    return this;
  }

  public IObjectUiCellRendererConfiguration build() {
    return new IObjectUiCellRendererConfiguration() {

      @Override
      public int getVerticalAlignment() {
        return ObjectUiCellRendererConfigurationBuilder.this.verticalAlignment;
      }

      @Override
      public int getVerticalTextPosition() {
        return ObjectUiCellRendererConfigurationBuilder.this.verticalTextPosition;
      }

      @Override
      public int getIconTextGap() {
        return ObjectUiCellRendererConfigurationBuilder.this.iconTextGap;
      }

      @Override
      public int getHorizontalTextPosition() {
        return ObjectUiCellRendererConfigurationBuilder.this.horizontalTextPosition;
      }

      @Override
      public int getHorizontalAlignment() {
        return ObjectUiCellRendererConfigurationBuilder.this.horizontalAlignment;
      }

      @Override
      public Border getBorder() {
        return ObjectUiCellRendererConfigurationBuilder.this.border;
      }
    };
  }

}
