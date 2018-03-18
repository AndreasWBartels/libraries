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
package net.anwiba.commons.lang.collection;

import java.util.LinkedList;

public class ObjectListBuilder<T> {

  private final IMutableObjectList<T> collection = new ObjectList<>(new LinkedList<T>());

  public IObjectList<T> build() {
    final ObjectList<T> objectList = new ObjectList<>();
    objectList.add(this.collection);
    return objectList;
  }

  public ObjectListBuilder<T> add(final Iterable<T> objects) {
    this.collection.add(objects);
    return this;
  }

  @SuppressWarnings("unchecked")
  public ObjectListBuilder<T> add(final T object) {
    this.collection.add(object);
    return this;
  }

}
