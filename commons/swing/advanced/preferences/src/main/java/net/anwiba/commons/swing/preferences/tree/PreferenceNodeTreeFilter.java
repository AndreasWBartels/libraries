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
package net.anwiba.commons.swing.preferences.tree;

import java.util.List;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.swing.tree.ITreeNodeFilter;
import net.anwiba.commons.utilities.collection.ListUtilities;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.string.StringUtilities;

public class PreferenceNodeTreeFilter implements ITreeNodeFilter<PreferenceNode> {

  IAcceptor<PreferenceNode> acceptor;

  public PreferenceNodeTreeFilter(final String string) {
    this.acceptor = new IAcceptor<PreferenceNode>() {

      @Override
      public boolean accept(final PreferenceNode value) {
        if (StringUtilities.isNullOrTrimmedEmpty(string)) {
          return true;
        }
        return evaluate(value);
      }

      private boolean evaluate(final PreferenceNode node) {
        return contains(node.toString(), string) || evaluate(node.getParameters()) || evaluate(node.children());
      }

      private boolean evaluate(final IParameters parameters) {
        for (final IParameter parameter : parameters.parameters()) {
          if (contains(parameter.getName(), string) || contains(parameter.getValue(), string)) {
            return true;
          }
        }
        return false;
      }

      private boolean contains(@SuppressWarnings("hiding") final String string, final String value) {
        return value == null ? false : string.toString().toUpperCase().contains(value.toUpperCase());
      }

      private boolean evaluate(final Iterable<PreferenceNode> children) {
        for (final PreferenceNode child : children) {
          if (evaluate(child)) {
            return true;
          }
        }
        return false;
      }
    };
  }

  @Override
  public PreferenceNode getChild(final PreferenceNode parent, final int index) {
    final List<PreferenceNode> list = ListUtilities.filter(parent.children(), this.acceptor);
    return list.get(index);
  }

  @Override
  public int getIndexOfChild(final PreferenceNode parent, final PreferenceNode child) {
    final List<PreferenceNode> list = ListUtilities.filter(parent.children(), this.acceptor);
    return list.indexOf(child);
  }

  @Override
  public int getChildCount(final PreferenceNode parent) {
    final List<PreferenceNode> list = ListUtilities.filter(parent.children(), this.acceptor);
    return list.size();
  }

}
