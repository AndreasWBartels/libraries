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

import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.swing.ui.IObjectUi;

public class ObjectComboBoxComponentBuilder<T> {

  final ObjectListConfigurationBuilder<T> configurationBuilder = new ObjectListConfigurationBuilder<>();
  private IComboBoxModel<T> model;

  public ObjectComboBoxComponentBuilder<T> setObjectUi(final IObjectUi<T> objectUi) {
    this.configurationBuilder.setObjectUi(objectUi);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setPrototype(final T prototype) {
    this.configurationBuilder.setPrototype(prototype);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setSingleSelectionMode() {
    this.configurationBuilder.setSingleSelectionMode();
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setSingleIntervalSelectionMode() {
    this.configurationBuilder.setSingleIntervalSelectionMode();
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setMultiSelectionMode() {
    this.configurationBuilder.setMultiSelectionMode();
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setIconTextGap(final int iconTextGap) {
    this.configurationBuilder.setIconTextGap(iconTextGap);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setVerticalTextPosition(final int verticalTextPosition) {
    this.configurationBuilder.setVerticalTextPosition(verticalTextPosition);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setHorizontalTextPosition(final int horizontalTextPosition) {
    this.configurationBuilder.setHorizontalTextPosition(horizontalTextPosition);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setHorizontalAlignment(final int horizontalAlignment) {
    this.configurationBuilder.setHorizontalAlignment(horizontalAlignment);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setBorder(final Border border) {
    this.configurationBuilder.setBorder(border);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setVisibleRowCount(final int visibleRowCount) {
    this.configurationBuilder.setVisibleRowCount(visibleRowCount);
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setModel(final IComboBoxModel<T> model) {
    this.model = model;
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setValues(final T[] values) {
    setValues(Arrays.asList(values));
    return this;
  }

  public ObjectComboBoxComponentBuilder<T> setValues(final List<T> values) {
    this.model = new ObjectComboBoxComponentModel<>(values);
    return this;
  }

  public ObjectComboBoxComponent<T> build() {
    this.model = Optional.ofNullable(this.model).orElseGet(
        () -> new ObjectComboBoxComponentModel<>(new ArrayList<T>()));
    this.model.setSelectedItem(null);
    return new ObjectComboBoxComponent<>(this.configurationBuilder.build(), this.model);
  }
}
