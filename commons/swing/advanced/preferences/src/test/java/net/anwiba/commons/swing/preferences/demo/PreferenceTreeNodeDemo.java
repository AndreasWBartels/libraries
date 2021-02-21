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
package net.anwiba.commons.swing.preferences.demo;

import static net.anwiba.testing.demo.JFrames.show;

import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.preferences.UserPreferencesFactory;
import net.anwiba.commons.swing.preferences.tree.PreferenceNode;
import net.anwiba.commons.swing.tree.TreeModel;

public class PreferenceTreeNodeDemo {

  @Test
  public void demo() {
    final UserPreferencesFactory factory = new UserPreferencesFactory("de"); //$NON-NLS-1$
    final TreeModel<PreferenceNode> model = new TreeModel<>(new PreferenceNode(null, factory.create()));
    show(new JScrollPane(new JTree(model)));
  }
}
