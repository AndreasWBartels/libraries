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

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.preferences.DummyPreferences;
import net.anwiba.commons.preferences.UserPreferencesFactory;
import net.anwiba.commons.swing.preferences.PreferencesPane;
import net.anwiba.commons.swing.preferences.editor.IPreferenceNodeEditorFactoryRegistry;
import net.anwiba.commons.swing.preferences.editor.ParametersEditorFactoryRegistry;
import net.anwiba.commons.swing.preferences.tree.IPreferenceNode;

@RunWith(DemoAsTestRunner.class)
public class PreferencesPaneDemo extends SwingDemoCase {

  @Demo
  public void demo() {
    final UserPreferencesFactory factory = new UserPreferencesFactory();
    final IPreferenceNodeEditorFactoryRegistry editorFactoryRegistry = new ParametersEditorFactoryRegistry();
    show(
        new PreferencesPane(
            new DummyPreferences(),
            factory.create(),
            false,
            editorFactoryRegistry,
            new IFunction<IPreferenceNode, Boolean, RuntimeException>() {

              @Override
              public Boolean execute(final IPreferenceNode value) throws RuntimeException {
                return Boolean.TRUE;
              }
            }).getComponent());
  }
}
