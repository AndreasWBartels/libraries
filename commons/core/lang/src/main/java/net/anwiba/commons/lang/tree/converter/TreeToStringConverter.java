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
import net.anwiba.commons.lang.tree.Tree;

@SuppressWarnings("nls")
public class TreeToStringConverter<K, V> implements IObjectToStringConverter<Tree<K, V>> {

  private final ItemToStringConverter<K, V> converter;
  private int format;

  public TreeToStringConverter() {
    this(ItemToStringConverter.COMPLEX);
  }

  public TreeToStringConverter(final int format) {
    this.format = format;
    this.converter = new ItemToStringConverter<>(format);
  }

  @Override
  public String toString(final Tree<K, V> tree) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("size: ");
    stringBuilder.append(tree.size());
    stringBuilder.append("\n");
    if (tree.getFirst() != null && this.format == ItemToStringConverter.COMPLEX) {
      stringBuilder.append("list: ");
      stringBuilder.append(list(tree.getFirst()));
    }
    if (tree.getRoot() != null) {
      stringBuilder.append("tree: ");
      stringBuilder.append(tree(tree.getRoot()));
    }
    return stringBuilder.toString();
  }

  private String list(final ITreeItem<K, V> first) {
    if (first == null) {
      return "";
    }
    final StringBuffer buffer = new StringBuffer();
    buffer.append("\n");
    ITreeItem<K, V> item = first;
    do {
      buffer.append("    ");
      buffer.append(this.converter.toString(item));
      buffer.append("\n");
      item = item.getNext();
    } while (item != null);
    return buffer.toString();
  }

  private String tree(final ITreeItem<K, V> item) {
    if (item == null) {
      return "";
    }
    final StringBuffer buffer = new StringBuffer();
    buffer.append("\n");
    tree(buffer, 0, item);
    return buffer.toString();
  }

  private void tree(final StringBuffer buffer, final int depth, final ITreeItem<K, V> item) {
    buffer.append("    ");
    if (depth > 0) {
      for (int i = 0; i < depth - 1; i++) {
        buffer.append("|    ");
      }
      buffer.append("+--> ");
    }
    buffer.append(this.converter.toString(item));
    buffer.append("\n");
    if (item.getLeft() != null) {
      tree(buffer, depth + 1, item.getLeft());
    }
    if (item.getRight() != null) {
      tree(buffer, depth + 1, item.getRight());
    }
  }

}
