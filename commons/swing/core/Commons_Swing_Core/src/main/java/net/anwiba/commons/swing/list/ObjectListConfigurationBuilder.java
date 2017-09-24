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

import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.Border;

import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.SelectionModel;
import net.anwiba.commons.swing.ui.IObjectUi;
import net.anwiba.commons.swing.ui.IObjectUiCellRendererConfiguration;
import net.anwiba.commons.swing.ui.ToStringUi;

public class ObjectListConfigurationBuilder<T> {

  private int visibleRowCount = 8;
  private int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  private IObjectUi<T> objectUi = new ToStringUi<>();
  private int iconTextGap = 2;
  private int verticalAlignment = SwingConstants.CENTER;
  private int verticalTextPosition = SwingConstants.CENTER;
  private int horizontalTextPosition = SwingConstants.TRAILING;
  private int horizontalAlignment = SwingConstants.LEADING;
  private Border border = null;
  private int layoutOrientation = JList.VERTICAL;
  private MouseListener mouseListener;
  private ISelectionModel<T> selectionModel;
  private TransferHandler transferHandler;
  private boolean isDragEnabled = false;
  private DropMode dropMode = DropMode.USE_SELECTION;

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

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentLeft() {
    this.horizontalAlignment = SwingConstants.LEFT;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentRight() {
    this.horizontalAlignment = SwingConstants.LEFT;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentCenter() {
    this.horizontalAlignment = SwingConstants.CENTER;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentLeading() {
    this.horizontalAlignment = SwingConstants.LEADING;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentTrailing() {
    this.horizontalAlignment = SwingConstants.TRAILING;
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

  public ObjectListConfigurationBuilder<T> setVerticalAlignment(final int verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setMouseListener(final MouseListener mouseListener) {
    this.mouseListener = mouseListener;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setVerticalOrientation() {
    this.layoutOrientation = JList.VERTICAL;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setVerticalWrapOrientation() {
    this.layoutOrientation = JList.VERTICAL_WRAP;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalWrapOrientation() {
    this.layoutOrientation = JList.HORIZONTAL_WRAP;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setTransferHandler(final TransferHandler transferHandler) {
    this.transferHandler = transferHandler;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setSelectionModel(final ISelectionModel<T> selectionModel) {
    this.selectionModel = selectionModel;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setDragDisabled() {
    this.isDragEnabled = false;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setDragEnabled() {
    this.isDragEnabled = true;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setDropInsertEnabled() {
    this.dropMode = DropMode.INSERT;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setDropReplaceEnabled() {
    this.dropMode = DropMode.ON;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setDropReplaceOrInsertEnabled() {
    this.dropMode = DropMode.ON_OR_INSERT;
    return this;
  }

  public ObjectListConfigurationBuilder<T> setDropToSelectedEnabled() {
    this.dropMode = DropMode.USE_SELECTION;
    return this;
  }

  public IObjectListConfiguration<T> build() {
    return new IObjectListConfiguration<T>() {

      @Override
      public int getLayoutOrientation() {
        return ObjectListConfigurationBuilder.this.layoutOrientation;
      }

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

      @Override
      public ISelectionModel<T> getSelectionModel() {
        return ObjectListConfigurationBuilder.this.selectionModel == null
            ? new SelectionModel<>()
            : ObjectListConfigurationBuilder.this.selectionModel;
      }

      @Override
      public TransferHandler getTransferHandler() {
        return ObjectListConfigurationBuilder.this.transferHandler;
      }

      @Override
      public DropMode getDropMode() {
        return ObjectListConfigurationBuilder.this.dropMode;
      }

      @Override
      public boolean isDragEnabled() {
        return ObjectListConfigurationBuilder.this.isDragEnabled;
      }
    };
  }
}