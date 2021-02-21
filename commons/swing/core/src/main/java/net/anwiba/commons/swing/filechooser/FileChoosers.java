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

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.anwiba.commons.preferences.DummyPreferences;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.preferences.UserPreferencesFactory;
import net.anwiba.commons.swing.preference.IWindowPreferences;
import net.anwiba.commons.swing.preference.WindowPreferences;

public class FileChoosers {

  public static IFileChooserResult show(final Component owner, final IOpenFileChooserConfiguration configuration) {
    return show(
        owner,
        new UserPreferencesFactory()
            .create(FileChoosers.FILECHOOSER_PREFERENCES_PATH)
            .node(FileChoosers.DEFAULT_PREFERENCE_NODE),
        configuration);
  }

  public static IFileChooserResult show(
      final Component owner,
      final IPreferences preferences,
      final IOpenFileChooserConfiguration configuration) {
    final JFileChooser fileChooser = createOpenFileChooser(preferences, configuration);
    final int returnVal = fileChooser.showOpenDialog(owner);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      final File currentDirectory = fileChooser.getCurrentDirectory();
      preferences.put(FileChoosers.FOLDER, currentDirectory.getAbsolutePath());
    }
    return new IFileChooserResult() {

      @Override
      public int getReturnState() {
        return returnVal;
      }

      @Override
      public File getSelectedFile() {
        return fileChooser.getSelectedFile();
      }

      @Override
      public File[] getSelectedFiles() {
        return fileChooser.getSelectedFiles();
      }

      @Override
      public FileFilter getFileFilter() {
        return fileChooser.getFileFilter();
      }

    };
  }

  public static JFileChooser createSaveFileChooser(
      final IPreferences preferences,
      final ISaveFileChooserConfiguration configuration) {
    final JFileChooser fileChooser = createFileChooser(preferences, configuration);
    if (configuration.getPresetFile() != null) {
      fileChooser.setSelectedFile(configuration.getPresetFile());
    }
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    fileChooser.setSelectedFile(configuration.getPresetFile());
    return fileChooser;
  }

  public static JFileChooser createOpenFileChooser(
      final IPreferences preferences,
      final IOpenFileChooserConfiguration configuration) {
    final JFileChooser fileChooser = createFileChooser(preferences, configuration);
    final IAccessoryFactory accessoryFactory = configuration.getFileViewFactory();
    fileChooser.setAccessory(accessoryFactory.create(fileChooser));
    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    return fileChooser;
  }

  private static JFileChooser createFileChooser(
      final IPreferences preferences,
      final IFileChooserConfiguration configuration) {
    final IWindowPreferences windowPreferences = preferences == null
        ? new WindowPreferences(new DummyPreferences())
        : new WindowPreferences(preferences);
    final JFileChooser fileChooser = new FileChooserDialog(windowPreferences);
    final Iterable<FileFilter> fileFilters = configuration.getFileFilters();
    fileChooser
        .setAcceptAllFileFilterUsed(
            configuration.isAllFilterEnabled()
                && configuration.getFileSelectionMode() != JFileChooser.DIRECTORIES_ONLY);
    for (final FileFilter filter : fileFilters) {
      fileChooser.addChoosableFileFilter(filter);
    }
    if (!configuration.isAllFilterEnabled()) {
      fileChooser.setFileFilter(fileFilters.iterator().next());
    }
    final String path = preferences == null
        ? null
        : preferences.get(FileChoosers.FOLDER, null);
    fileChooser
        .setCurrentDirectory(
            path == null
                ? null
                : new File(path));
    fileChooser.setFileSelectionMode(configuration.getFileSelectionMode());
    fileChooser.setMultiSelectionEnabled(configuration.isMultiSelectionEnabled());
    return fileChooser;
  }

  public static IFileChooserResult show(final Component owner, final ISaveFileChooserConfiguration configuration) {
    return show(
        owner,
        new UserPreferencesFactory()
            .create(FileChoosers.FILECHOOSER_PREFERENCES_PATH)
            .node(FileChoosers.DEFAULT_PREFERENCE_NODE),
        configuration);
  }

  public static IFileChooserResult show(
      final Component owner,
      final IPreferences preferences,
      final ISaveFileChooserConfiguration configuration) {
    final JFileChooser fileChooser = createSaveFileChooser(preferences, configuration);
    final int returnVal = fileChooser.showSaveDialog(owner);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      final File currentDirectory = fileChooser.getCurrentDirectory();
      if (preferences != null) {
        preferences.put(FileChoosers.FOLDER, currentDirectory.getAbsolutePath());
      }
    }
    return new IFileChooserResult() {

      @Override
      public int getReturnState() {
        return returnVal;
      }

      @Override
      public File getSelectedFile() {
        return fileChooser.getSelectedFile();
      }

      @Override
      public File[] getSelectedFiles() {
        return fileChooser.getSelectedFiles();
      }

      @Override
      public FileFilter getFileFilter() {
        return fileChooser.getFileFilter();
      }

    };
  }

  public static final String FOLDER = "folder"; //$NON-NLS-1$
  public static final String DEFAULT_PREFERENCE_NODE = "filechooser"; //$NON-NLS-1$
  public static final String FILECHOOSER_PREFERENCES_PATH = "/net/anwiba/gui"; //$NON-NLS-1$
}
