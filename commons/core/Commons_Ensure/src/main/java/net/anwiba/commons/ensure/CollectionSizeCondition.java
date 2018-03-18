/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels 
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
package net.anwiba.commons.ensure;

import java.util.Collection;

@SuppressWarnings("rawtypes")
public class CollectionSizeCondition<T extends Collection> extends AbstractCondition<T> {

  private final int size;

  public CollectionSizeCondition(final int size) {
    this.size = size;
  }

  @Override
  public boolean accept(final T value) {
    Ensure.ensureArgumentNotNull(value);
    return value.size() == this.size;
  }

  @Override
  public String toText() {
    return "size == " + this.size; //$NON-NLS-1$
  }

}
