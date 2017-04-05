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

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import net.anwiba.commons.swing.ui.IObjectUi;

public class ObjectUiCellRenderer<T> extends DefaultListCellRenderer {

  private final IObjectUi<T> objectUi;
  private final IObjectUiCellRendererConfiguration configuration;

  private static final long serialVersionUID = 1L;

  public ObjectUiCellRenderer(final IObjectUiCellRendererConfiguration configuration, final IObjectUi<T> objectUi) {
    this.configuration = configuration;
    this.objectUi = objectUi;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Component getListCellRendererComponent(
      final JList list,
      final Object value,
      final int index,
      final boolean isSelected,
      final boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    try {
      final T object = (T) value;
      final String text = this.objectUi.getText(object);
      final Icon icon = this.objectUi.getIcon(object);
      setText(icon == null && (text == null || text.length() == 0) ? " " : text); //$NON-NLS-1$
      setIcon(icon);
      setToolTipText(this.objectUi.getToolTipText(object));
      setVerticalTextPosition(this.configuration.getVerticalTextPosition());
      setVerticalAlignment(this.configuration.getVerticalAlignment());
      setHorizontalTextPosition(this.configuration.getHorizontalTextPosition());
      setHorizontalAlignment(this.configuration.getHorizontalAlignment());
      setIconTextGap(this.configuration.getIconTextGap());
      setBorder(this.configuration.getBorder());
      return this;
    } catch (final ClassCastException exception) {
      return this;
    }
  }
}