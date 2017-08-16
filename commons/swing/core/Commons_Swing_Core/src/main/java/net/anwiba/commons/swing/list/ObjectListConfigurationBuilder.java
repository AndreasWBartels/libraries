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
package net.anwiba.commons.swing.list;

import java.awt.event.MouseListener;

import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.anwiba.commons.swing.ui.IObjectUi;
import net.anwiba.commons.swing.ui.ToStringUi;

public class ObjectListConfigurationBuilder<T> {

  int visibleRowCount = 8;
  int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  IObjectUi<T> objectUi = new ToStringUi<>();
  int iconTextGap = 2;
  int verticalAlignment = SwingConstants.CENTER;
  int verticalTextPosition = SwingConstants.CENTER;
  int horizontalTextPosition = SwingConstants.TRAILING;
  int horizontalAlignment = SwingConstants.LEADING;
  Border border = null;

  IObjectUiCellRendererConfiguration objectUiCellRendererConfiguration = new IObjectUiCellRendererConfiguration() {

    @Override
    public int getVerticalAlignment() {
      return ObjectListConfigurationBuilder.this.verticalAlignment;
    }

    @Override
    public int getVerticalTextPosition() {
      return ObjectListConfigurationBuilder.this.verticalTextPosition;
    }

    @Override
    public int getIconTextGap() {
      return ObjectListConfigurationBuilder.this.iconTextGap;
    }

    @Override
    public int getHorizontalTextPosition() {
      return ObjectListConfigurationBuilder.this.horizontalTextPosition;
    }

    @Override
    public int getHorizontalAlignment() {
      return ObjectListConfigurationBuilder.this.horizontalAlignment;
    }

    @Override
    public Border getBorder() {
      return ObjectListConfigurationBuilder.this.border;
    }
  };
  private MouseListener mouseListener;

  public ObjectListConfigurationBuilder<T> setObjectUi(final IObjectUi<T> objectUi) {
    this.objectUi = objectUi;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setSingleSelectionMode() {
    this.selectionMode = ListSelectionModel.SINGLE_SELECTION;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setSingleIntervalSelectionMode() {
    this.selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setMultiSelectionMode() {
    this.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setIconTextGap(final int iconTextGap) {
    this.iconTextGap = iconTextGap;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setVerticalTextPosition(final int verticalTextPosition) {
    this.verticalTextPosition = verticalTextPosition;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalTextPosition(final int horizontalTextPosition) {
    this.horizontalTextPosition = horizontalTextPosition;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignment(final int horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setBorder(final Border border) {
    this.border = border;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setVisibleRowCount(final int visibleRowCount) {
    this.visibleRowCount = visibleRowCount;
    return this;
  }

  public IObjectListConfiguration<T> build() {
    return new IObjectListConfiguration<T>() {

      @Override
      public MouseListener getMouseListener() {
        return ObjectListConfigurationBuilder.this.mouseListener;
      }

      @Override
      public int getVisibleRowCount() {
        return ObjectListConfigurationBuilder.this.visibleRowCount;
      }

      @Override
      public IObjectUi<T> getObjectUi() {
        return ObjectListConfigurationBuilder.this.objectUi;
      }

      @Override
      public int getSelectionMode() {
        return ObjectListConfigurationBuilder.this.selectionMode;
      }

      @Override
      public IObjectUiCellRendererConfiguration getObjectUiCellRendererConfiguration() {
        return ObjectListConfigurationBuilder.this.objectUiCellRendererConfiguration;
      }
    };
  }

  public ObjectListConfigurationBuilder<T> setVerticalAlignment(final int verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setMouseListener(final MouseListener mouseListener) {
    this.mouseListener = mouseListener;
    return this;
  }
}