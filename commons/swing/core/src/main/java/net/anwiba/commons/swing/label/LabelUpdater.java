/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
// Copyright (c) 2016 by Andreas W. Bartels

package net.anwiba.commons.swing.label;

import javax.swing.JLabel;

import net.anwiba.commons.lang.object.IObjectProvider;
import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public final class LabelUpdater<T> implements IChangeableObjectListener {
  private final JLabel label;
  private final IObjectProvider<T> model;
  private final IObjectToStringConverter<T> converter;

  public LabelUpdater(final JLabel label, final IObjectToStringConverter<T> converter, final IObjectProvider<T> model) {
    this.label = label;
    this.converter = converter;
    this.model = model;
  }

  @Override
  public void objectChanged() {
    final T value = this.model.get();
    GuiUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        LabelUpdater.this.label.setText(LabelUpdater.this.converter.toString(value));
      }
    });
  }
}
