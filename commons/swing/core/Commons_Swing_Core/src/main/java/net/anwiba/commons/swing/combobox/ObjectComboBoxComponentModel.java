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

import java.util.List;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.list.ObjectListComponentModel;

public class ObjectComboBoxComponentModel<T> extends ObjectListComponentModel<T>
    implements
    IComboBoxModel<T>,
    IObjectModel<T> {

  private final ObjectModel<T> model = new ObjectModel<>();

  public ObjectComboBoxComponentModel(final List<T> objects) {
    super(objects);
    if (!objects.isEmpty()) {
      this.model.set(objects.get(0));
    }
    this.model.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        fireContentsChanged(ObjectComboBoxComponentModel.this, -1, -1);
      }
    });
  }

  private static final long serialVersionUID = 1L;

  @Override
  public void setSelectedItem(final Object item) {
    this.model.set((T) item);
  }

  @Override
  public Object getSelectedItem() {
    return this.model.get();
  }

  @Override
  public void addChangeListener(final IChangeableObjectListener listener) {
    this.model.addChangeListener(listener);
  }

  @Override
  public void removeChangeListener(final IChangeableObjectListener listener) {
    this.model.removeChangeListener(listener);
  }

  @Override
  public T get() {
    return this.model.get();
  }

  @Override
  public void set(final T object) {
    this.model.set(object);
  }

}
