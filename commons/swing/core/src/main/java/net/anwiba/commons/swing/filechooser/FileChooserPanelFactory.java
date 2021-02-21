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
package net.anwiba.commons.swing.filechooser;

import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.preferences.UserPreferencesFactory;

import javax.swing.JFileChooser;

public class FileChooserPanelFactory {

  final private IPreferences preferences;

  public FileChooserPanelFactory() {
    this(new UserPreferencesFactory().create(FileChoosers.FILECHOOSER_PREFERENCES_PATH));
  }

  public FileChooserPanelFactory(final IPreferences preferences) {
    this.preferences = preferences.node(FileChoosers.DEFAULT_PREFERENCE_NODE);
  }

  public IFileChooserPanel create(final IOpenFileChooserConfiguration configuration) {
    final JFileChooser fileChooser = FileChoosers.createOpenFileChooser(this.preferences, configuration);
    fileChooser.setControlButtonsAreShown(false);
    if (configuration.isMultiSelectionEnabled()) {
      return new MultiFileChooserPanel(fileChooser, this.preferences);
    }
    return new SingleFileChooserPanel(fileChooser, this.preferences);
  }

  public IFileChooserPanel create(final ISaveFileChooserConfiguration configuration) {
    final JFileChooser fileChooser = FileChoosers.createSaveFileChooser(this.preferences, configuration);
    fileChooser.setControlButtonsAreShown(false);
    if (configuration.isMultiSelectionEnabled()) {
      return new MultiFileChooserPanel(fileChooser, this.preferences);
    }
    return new SingleFileChooserPanel(fileChooser, this.preferences);
  }
}