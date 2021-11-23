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
package net.anwiba.commons.swing.combobox;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;

import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.object.AbstractObjectTextField;

public final class ComboBoxObjectFieldEditor<T> implements ComboBoxEditor {
  private final AbstractObjectTextField<T> field;

  public ComboBoxObjectFieldEditor(final AbstractObjectTextField<T> field) {
    this.field = field;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setItem(final Object anObject) {
    this.field.getModel().set((T) anObject);
  }

  @Override
  public void selectAll() {
    this.field.selectAll();
    this.field.getComponent().requestFocus();
  }

  @Override
  public void removeActionListener(final ActionListener l) {
    this.field.getActionNotifier().removeActionListener(l);
  }

  @Override
  public Object getItem() {
    return this.field.getModel().get();
  }

  @Override
  public Component getEditorComponent() {
    return this.field.getComponent();
  }

  @Override
  public void addActionListener(final ActionListener l) {
    this.field.getActionNotifier().addActionListener(l);
  }

  public IObjectModel<T> getModel() {
    return this.field.getModel();
  }
}