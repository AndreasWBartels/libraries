/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.swing.combobox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.border.Border;

import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.swing.ui.IObjectUi;

public class ObjectComboBoxBuilder<T> {

  final ObjectListConfigurationBuilder<T> configurationBuilder = new ObjectListConfigurationBuilder<>();
  private IComboBoxModel<T> model;
  private T value = null;

  public ObjectComboBoxBuilder<T> setObjectUi(final IObjectUi<T> objectUi) {
    this.configurationBuilder.setObjectUi(objectUi);
    return this;
  }

  public ObjectComboBoxBuilder<T> setPrototype(final T prototype) {
    this.configurationBuilder.setPrototype(prototype);
    return this;
  }

  public ObjectComboBoxBuilder<T> setSingleSelectionMode() {
    this.configurationBuilder.setSingleSelectionMode();
    return this;
  }

  public ObjectComboBoxBuilder<T> setSingleIntervalSelectionMode() {
    this.configurationBuilder.setSingleIntervalSelectionMode();
    return this;
  }

  public ObjectComboBoxBuilder<T> setMultiSelectionMode() {
    this.configurationBuilder.setMultiSelectionMode();
    return this;
  }

  public ObjectComboBoxBuilder<T> setIconTextGap(final int iconTextGap) {
    this.configurationBuilder.setIconTextGap(iconTextGap);
    return this;
  }

  public ObjectComboBoxBuilder<T> setVerticalTextPosition(final int verticalTextPosition) {
    this.configurationBuilder.setVerticalTextPosition(verticalTextPosition);
    return this;
  }

  public ObjectComboBoxBuilder<T> setHorizontalTextPosition(final int horizontalTextPosition) {
    this.configurationBuilder.setHorizontalTextPosition(horizontalTextPosition);
    return this;
  }

  public ObjectComboBoxBuilder<T> setVerticalAlignment(final int verticalAlignment) {
    this.configurationBuilder.setVerticalAlignment(verticalAlignment);
    return this;
  }

  public ObjectComboBoxBuilder<T> setHorizontalAlignment(final int horizontalAlignment) {
    this.configurationBuilder.setHorizontalAlignment(horizontalAlignment);
    return this;
  }

  public ObjectComboBoxBuilder<T> setBorder(final Border border) {
    this.configurationBuilder.setBorder(border);
    return this;
  }

  public ObjectComboBoxBuilder<T> setVisibleRowCount(final int visibleRowCount) {
    this.configurationBuilder.setVisibleRowCount(visibleRowCount);
    return this;
  }

  public ObjectComboBoxBuilder<T> setModel(final IComboBoxModel<T> model) {
    this.model = model;
    return this;
  }

  public ObjectComboBoxBuilder<T> setValues(final T[] values) {
    setValues(Arrays.asList(values));
    return this;
  }

  public ObjectComboBoxBuilder<T> setValues(final List<T> values) {
    this.model = new ObjectComboBoxModel<>(values);
    return this;
  }

  public ObjectComboBoxBuilder<T> setSelectedValue(final T value) {
    this.value = value;
    return this;
  }

  public ObjectComboBoxBuilder<T> setEnabledModel(final IBooleanDistributor enabledModel) {
    this.configurationBuilder.setEnabledModel(enabledModel);
    return this;
  }

  public ObjectComboBoxBuilder<T> setEditable(final boolean isEditable) {
    this.configurationBuilder.setEditable(isEditable);
    return this;
  }

  public ObjectComboBoxBuilder<T> setEditableEnabled() {
    this.configurationBuilder.setEditable(true);
    return this;
  }

  public ObjectComboBoxBuilder<T> setEditableDisabled() {
    this.configurationBuilder.setEditable(false);
    return this;
  }

  public ObjectComboBox<T> build() {
    IComboBoxModel model =
        Optional.ofNullable(this.model).orElseGet(() -> new ObjectComboBoxModel<>(new ArrayList<T>()));
    model.setSelectedItem(this.value);
    return new ObjectComboBox<>(this.configurationBuilder.build(), model);
  }
}
