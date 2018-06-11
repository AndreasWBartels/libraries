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
  private int layoutOrientation = JList.VERTICAL;
  private MouseListener mouseListener;
  private ISelectionModel<T> selectionModel;
  private TransferHandler transferHandler;
  private boolean isDragEnabled = false;
  private DropMode dropMode = DropMode.USE_SELECTION;
  private T prototype = null;

  private final ObjectUiCellRendererConfigurationBuilder objectUiCellRendererConfigurationBuilder = new ObjectUiCellRendererConfigurationBuilder();

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
    this.objectUiCellRendererConfigurationBuilder.setIconTextGap(iconTextGap);
    return this;
  }

  public ObjectListConfigurationBuilder<T> setVerticalTextPosition(final int verticalTextPosition) {
    this.objectUiCellRendererConfigurationBuilder.setVerticalTextPosition(verticalTextPosition);
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalTextPosition(final int horizontalTextPosition) {
    this.objectUiCellRendererConfigurationBuilder.setHorizontalTextPosition(horizontalTextPosition);
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignment(final int horizontalAlignment) {
    this.objectUiCellRendererConfigurationBuilder.setHorizontalAlignment(horizontalAlignment);
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentLeft() {
    this.objectUiCellRendererConfigurationBuilder.setHorizontalAlignmentLeft();
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentRight() {
    this.objectUiCellRendererConfigurationBuilder.setHorizontalAlignmentRight();
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentCenter() {
    this.objectUiCellRendererConfigurationBuilder.setHorizontalAlignmentCenter();
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentLeading() {
    this.objectUiCellRendererConfigurationBuilder.setHorizontalAlignmentLeading();
    return this;
  }

  public ObjectListConfigurationBuilder<T> setHorizontalAlignmentTrailing() {
    this.objectUiCellRendererConfigurationBuilder.setHorizontalAlignmentTrailing();
    return this;
  }

  public ObjectListConfigurationBuilder<T> setBorder(final Border border) {
    this.objectUiCellRendererConfigurationBuilder.setBorder(border);
    return this;
  }

  public ObjectListConfigurationBuilder<T> setVerticalAlignment(final int verticalAlignment) {
    this.objectUiCellRendererConfigurationBuilder.setVerticalAlignment(verticalAlignment);
    return this;
  }

  public ObjectListConfigurationBuilder<T> setVisibleRowCount(final int visibleRowCount) {
    this.visibleRowCount = visibleRowCount;
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

  public ObjectListConfigurationBuilder<T> setPrototype(final T prototype) {
    this.prototype = prototype;
    return this;
  }

  public IObjectListConfiguration<T> build() {
    final IObjectUiCellRendererConfiguration objectUiCellRendererConfiguration = this.objectUiCellRendererConfigurationBuilder
        .build();
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
        return objectUiCellRendererConfiguration;
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

      @Override
      public T getPrototype() {
        return ObjectListConfigurationBuilder.this.prototype;
      }
    };
  }
}
