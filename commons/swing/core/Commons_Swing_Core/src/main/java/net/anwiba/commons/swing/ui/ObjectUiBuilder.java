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
package net.anwiba.commons.swing.ui;

import java.util.Optional;

import javax.swing.Icon;

import net.anwiba.commons.lang.functional.IConverter;

public class ObjectUiBuilder<T> {

  private IConverter<T, String, RuntimeException> toTextConverter;
  private IConverter<T, String, RuntimeException> toToolTipConverter;
  private IConverter<T, Icon, RuntimeException> toIconConverter;

  public ObjectUiBuilder<T> text(final IConverter<T, String, RuntimeException> toTextConverter) {
    this.toTextConverter = toTextConverter;
    return this;
  }

  public ObjectUiBuilder<T> tooltip(final IConverter<T, String, RuntimeException> toToolTipConverter) {
    this.toToolTipConverter = toToolTipConverter;
    return this;
  }

  public ObjectUiBuilder<T> icon(final IConverter<T, Icon, RuntimeException> toIconConverter) {
    this.toIconConverter = toIconConverter;
    return this;
  }

  public IObjectUi<T> build() {
    if (this.toTextConverter == null) {
      this.toTextConverter = input -> Optional.ofNullable(input).map(i -> i.toString()).orElse(null);
    }
    return new IObjectUi<T>() {

      @Override
      public String getText(final T value) {
        return ObjectUiBuilder.this.toTextConverter.convert(value);
      }

      @Override
      public Icon getIcon(final T object) {
        if (ObjectUiBuilder.this.toIconConverter == null) {
          return null;
        }
        return ObjectUiBuilder.this.toIconConverter.convert(object);
      }

      @Override
      public String getToolTipText(final T object) {
        if (ObjectUiBuilder.this.toToolTipConverter == null) {
          return getText(object);
        }
        return ObjectUiBuilder.this.toToolTipConverter.convert(object);
      }
    };
  }
}
