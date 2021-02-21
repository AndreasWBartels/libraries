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
package net.anwiba.commons.lang.tree.converter;

import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.lang.tree.ITreeItem;

public class ItemToStringConverter<K, V> implements IObjectToStringConverter<ITreeItem<K, V>> {

  public final static int SIMPLE = 0;
  public final static int COMPLEX = 1;
  private final int format;

  public ItemToStringConverter() {
    this(COMPLEX);
  }

  public ItemToStringConverter(final int format) {
    this.format = format;
  }

  @SuppressWarnings("nls")
  @Override
  public String toString(final ITreeItem<K, V> item) {
    final StringBuffer buffer = new StringBuffer();
    if (this.format == SIMPLE) {
      buffer.append(item.getKey());
      return buffer.toString();
    }
    buffer.append(item.getKey());
    buffer.append(" : ");
    buffer.append(item.getElement());
    buffer.append(" ( ");
    if (item.getParent() != null) {
      buffer.append("p:");
      buffer.append(item.getParent().getKey());
    } else {
      buffer.append("null");
    }
    buffer.append(", ");
    if (item.getLeft() != null) {
      buffer.append("l:");
      buffer.append(item.getLeft().getKey());
    } else {
      buffer.append("null");
    }
    buffer.append(", ");
    if (item.getRight() != null) {
      buffer.append("r:");
      buffer.append(item.getRight().getKey());
    } else {
      buffer.append("null");
    }
    buffer.append("; ");
    if (item.getPrevious() != null) {
      buffer.append("f:");
      buffer.append(item.getPrevious().getKey());
    } else {
      buffer.append("null");
    }
    buffer.append(", ");
    if (item.getNext() != null) {
      buffer.append("n:");
      buffer.append(item.getNext().getKey());
    } else {
      buffer.append("null");
    }
    buffer.append(") [ ").append(item.getBalanced()).append(" ]");
    return buffer.toString();
  }

}
