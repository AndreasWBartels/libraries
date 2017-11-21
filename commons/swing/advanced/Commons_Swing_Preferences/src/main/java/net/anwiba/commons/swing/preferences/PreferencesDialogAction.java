/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.swing.preferences;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.preferences.PreferenceUtilities;
import net.anwiba.commons.swing.dialog.ConfigurableDialogLauncher;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.IContentPaneFactory;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;
import net.anwiba.commons.swing.preferences.editor.IPreferenceNodeEditorFactoryRegistry;
import net.anwiba.commons.swing.preferences.editor.ParametersEditorFactoryRegistry;
import net.anwiba.commons.swing.preferences.tree.IPreferenceNode;
import net.anwiba.commons.utilities.parameter.IParameter;

public final class PreferencesDialogAction extends AbstractAction {

  private static final long serialVersionUID = 1L;
  private final boolean isEditingEnabled;
  private final String title;
  private final IPreferences contentPreferences;
  private final IPreferences dialogPreferences;

  public PreferencesDialogAction(
      final IPreferences dialogPreferences,
      final String title,
      final boolean isEditingEnabled,
      final IPreferences contentPreferences) {
    super(title);
    this.isEditingEnabled = isEditingEnabled;
    this.title = title;
    this.contentPreferences = contentPreferences;
    this.dialogPreferences = dialogPreferences;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    final IPreferenceNodeEditorFactoryRegistry editorFactoryRegistry = new ParametersEditorFactoryRegistry();
    final IFunction<IPreferenceNode, Boolean, RuntimeException> storeFunction = new IFunction<IPreferenceNode, Boolean, RuntimeException>() {

      @Override
      public Boolean execute(final IPreferenceNode preferenceNode) throws RuntimeException {
        if (preferenceNode == null) {
          return Boolean.TRUE;
        }
        final String[] path = preferenceNode.getPath();
        final Iterable<IParameter> parameters = preferenceNode.getParameters().parameters();
        PreferenceUtilities.store(path, parameters);
        return Boolean.TRUE;
      }
    };
    final PreferencesPane preferencesPane = new PreferencesPane(
        this.dialogPreferences,
        this.contentPreferences,
        this.isEditingEnabled,
        editorFactoryRegistry,
        storeFunction);
    show((Component) e.getSource(), this.dialogPreferences, preferencesPane, storeFunction);
  }

  public void show(
      final Component component,
      final IPreferences preferences,
      final PreferencesPane preferencesPane,
      final IFunction<IPreferenceNode, Boolean, RuntimeException> storeFunction) {
    final IContentPaneFactory contentPaneFactory = new IContentPaneFactory() {

      @Override
      public IContentPanel create(final Window owner, final IPreferences preferences) {
        return new AbstractContentPane() {

          @Override
          public JComponent getComponent() {
            return preferencesPane.getComponent();
          }
        };
      }
    };
    new ConfigurableDialogLauncher()
        .setPreferences(preferences)
        .setTitle(this.title)
        .setMessage(Message.create(this.title))
        .setContentPaneFactory(contentPaneFactory)
        .setMessagePanelDisabled()
        .addOnCloseExecutable(() -> {
          if (this.isEditingEnabled) {
            storeFunction.execute(preferencesPane.getPreferenceNode());
          }
        })
        .setDialogType(DialogType.CLOSE)
        .enableCloseOnEscape()
        .launch(component);
  }
}
