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

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.preferences.PreferenceUtilities;
import net.anwiba.commons.swing.tree.ITreeNode;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public class PreferenceNode implements ITreeNode<PreferenceNode>, IPreferenceNode {

  private final IPreferences preferences;
  private final PreferenceNode parent;

  public PreferenceNode(final PreferenceNode parent, final IPreferences preferences) {
    this.parent = parent;
    this.preferences = preferences;
  }

  @Override
  public PreferenceNode getChildAt(final int childIndex) {
    return new PreferenceNode(this, this.preferences.nodes().get(childIndex));
  }

  @Override
  public int getChildCount() {
    return this.preferences.nodes().size();
  }

  @Override
  public PreferenceNode getParent() {
    return this.parent;
  }

  @Override
  public int getIndex(final PreferenceNode node) {
    return this.preferences.nodes().indexOf((node).preferences);
  }

  @Override
  public boolean getAllowsChildren() {
    return true;
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public Iterable<PreferenceNode> children() {
    @SuppressWarnings("hiding")
    final PreferenceNode parent = this;
    return IterableUtilities
        .convert(this.preferences.nodes(), new IConverter<IPreferences, PreferenceNode, RuntimeException>() {
          @Override
          public PreferenceNode convert(@SuppressWarnings("hiding") final IPreferences preferences) {
            return new PreferenceNode(parent, preferences);
          }
        });
  }

  public void remove(final PreferenceNode node) {
    this.preferences.node(node.getName()).delete();
  }

  public String getName() {
    return this.preferences.getName();
  }

  @Override
  public String toString() {
    return this.preferences.getName();
  }

  @Override
  public IParameters getParameters() {
    return PreferenceUtilities.getParameters(this.preferences);
  }

  @Override
  public String[] getPath() {
    return this.preferences.getPath();
  }

}
