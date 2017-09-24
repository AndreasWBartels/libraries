/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.TransferHandler;
import javax.swing.border.Border;

import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.swing.combobox.ObjectComboBoxComponentModel;
import net.anwiba.commons.swing.ui.IObjectUi;

public class ObjectListComponentBuilder<T> {

  final ObjectListConfigurationBuilder<T> configurationBuilder = new ObjectListConfigurationBuilder<>();
  private IListModel<T> model;

  public ObjectListComponentBuilder<T> setObjectUi(final IObjectUi<T> objectUi) {
    this.configurationBuilder.setObjectUi(objectUi);
    return this;
  }

  public ObjectListComponentBuilder<T> setSingleSelectionMode() {
    this.configurationBuilder.setSingleSelectionMode();
    return this;
  }

  public ObjectListComponentBuilder<T> setSingleIntervalSelectionMode() {
    this.configurationBuilder.setSingleIntervalSelectionMode();
    return this;
  }

  public ObjectListComponentBuilder<T> setMultiSelectionMode() {
    this.configurationBuilder.setMultiSelectionMode();
    return this;
  }

  public ObjectListComponentBuilder<T> setIconTextGap(final int iconTextGap) {
    this.configurationBuilder.setIconTextGap(iconTextGap);
    return this;
  }

  public ObjectListComponentBuilder<T> setVerticalTextPosition(final int verticalTextPosition) {
    this.configurationBuilder.setVerticalTextPosition(verticalTextPosition);
    return this;
  }

  public ObjectListComponentBuilder<T> setHorizontalTextPosition(final int horizontalTextPosition) {
    this.configurationBuilder.setHorizontalTextPosition(horizontalTextPosition);
    return this;
  }

  public ObjectListComponentBuilder<T> setHorizontalAlignment(final int horizontalAlignment) {
    this.configurationBuilder.setHorizontalAlignment(horizontalAlignment);
    return this;
  }

  public ObjectListComponentBuilder<T> setBorder(final Border border) {
    this.configurationBuilder.setBorder(border);
    return this;
  }

  public ObjectListComponentBuilder<T> setVisibleRowCount(final int visibleRowCount) {
    this.configurationBuilder.setVisibleRowCount(visibleRowCount);
    return this;
  }

  public ObjectListComponentBuilder<T> setModel(final IListModel<T> model) {
    this.model = model;
    return this;
  }

  public ObjectListComponentBuilder<T> setValues(final T[] values) {
    setValues(Arrays.asList(values));
    return this;
  }

  public ObjectListComponentBuilder<T> setValues(final List<T> values) {
    this.model = new ObjectComboBoxComponentModel<>(values);
    return this;
  }

  public ObjectListComponentBuilder<T> setVerticalWrapOrientation() {
    this.configurationBuilder.setVerticalWrapOrientation();
    return this;
  }

  public ObjectListComponentBuilder<T> setHorizontalWrapOrientation() {
    this.configurationBuilder.setHorizontalWrapOrientation();
    return this;
  }

  public ObjectListComponentBuilder<T> setVerticalOrientation() {
    this.configurationBuilder.setVerticalOrientation();
    return this;
  }

  public ObjectListComponentBuilder<T> setMouseListener(final MouseListener mouseListener) {
    this.configurationBuilder.setMouseListener(mouseListener);
    return this;
  }

  public ObjectListComponentBuilder<T> setSelectionModel(final ISelectionModel<T> selectionModel) {
    this.configurationBuilder.setSelectionModel(selectionModel);
    return this;
  }

  public ObjectListComponentBuilder<T> setTransferHandler(final TransferHandler transferHandler) {
    this.configurationBuilder.setTransferHandler(transferHandler);
    return this;
  }

  public ObjectListComponentBuilder<T> setDragDisnabled() {
    this.configurationBuilder.setDragDisabled();
    return this;
  }

  public ObjectListComponentBuilder<T> setDragEnabled() {
    this.configurationBuilder.setDragEnabled();
    return this;
  }

  public ObjectListComponentBuilder<T> setDropReplaceEnabled() {
    this.configurationBuilder.setDropReplaceEnabled();
    return this;
  }

  public ObjectListComponentBuilder<T> setDropReplaceOrInsertEnabled() {
    this.configurationBuilder.setDropReplaceOrInsertEnabled();
    return this;
  }

  public ObjectListComponentBuilder<T> setDropToSelectedEnabled() {
    this.configurationBuilder.setDropToSelectedEnabled();
    return this;
  }

  public ObjectListComponent<T> build() {
    this.model = Optional.ofNullable(this.model).orElseGet(() -> new ObjectListComponentModel<>(new ArrayList<T>()));
    return new ObjectListComponent<>(this.configurationBuilder.build(), this.model);
  }

}
