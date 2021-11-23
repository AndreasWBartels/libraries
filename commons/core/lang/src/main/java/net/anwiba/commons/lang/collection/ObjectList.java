/*
 * #%L
 * anwiba commons core
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

package net.anwiba.commons.lang.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class ObjectList<T> extends AbstractObjectList<T> {

  public ObjectList() {
    this(new LinkedList<>());
  }

  public ObjectList(final IObjectCollection<T> objects) {
    super(objects.toCollection());
  }

  public ObjectList(final Collection<T> objects) {
    super(objects);
  }

  public static <T> ObjectListBuilder<T> builder() {
    return new ObjectListBuilder<>();
  }

  public static <T> IObjectList<T> empty() {
    return new ObjectList<>();
  }

  public static <T> IObjectList<T> of(final T value) {
    return new ObjectList<>(Arrays.asList(value));
  }

}
