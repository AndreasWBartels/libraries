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

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class OpenFileChooserConfiguration extends AbstractFileChooserConfiguration
    implements
    IOpenFileChooserConfiguration {

  private final IAccessoryFactory accessoryFactory;

  public OpenFileChooserConfiguration(
      final Iterable<FileFilter> fileFilters,
      final int fileSelectionMode,
      final boolean isMultiSelectionEnabled) {
    this(fileFilters, fileSelectionMode, isMultiSelectionEnabled, new IAccessoryFactory() {

      @Override
      public JComponent create(final JFileChooser fileChooser) {
        return null;
      }
    });
  }

  public OpenFileChooserConfiguration(
      final Iterable<FileFilter> fileFilters,
      final int fileSelectionMode,
      final boolean isMultiSelectionEnabled,
      final IAccessoryFactory accessoryFactory) {
    this(null, fileFilters, fileSelectionMode, isMultiSelectionEnabled, accessoryFactory);
  }

  public OpenFileChooserConfiguration(
      final File presetFile,
      final Iterable<FileFilter> fileFilters,
      final int fileSelectionMode,
      final boolean isMultiSelectionEnabled) {
    this(presetFile, fileFilters, fileSelectionMode, isMultiSelectionEnabled, new IAccessoryFactory() {

      @Override
      public JComponent create(final JFileChooser fileChooser) {
        return null;
      }
    });
  }

  public OpenFileChooserConfiguration(
      final File presetFile,
      final Iterable<FileFilter> fileFilters,
      final int fileSelectionMode,
      final boolean isMultiSelectionEnabled,
      final IAccessoryFactory accessoryFactory) {
    this(presetFile, fileFilters, fileSelectionMode, true, isMultiSelectionEnabled, accessoryFactory);
  }

  public OpenFileChooserConfiguration(
      final File presetFile,
      final Iterable<FileFilter> fileFilters,
      final int fileSelectionMode,
      final boolean isAllFilterEnabled,
      final boolean isMultiSelectionEnabled,
      final IAccessoryFactory accessoryFactory) {
    super(presetFile, fileFilters, fileSelectionMode, isAllFilterEnabled, isMultiSelectionEnabled);
    this.accessoryFactory = accessoryFactory;
  }

  @Override
  public IAccessoryFactory getFileViewFactory() {
    return this.accessoryFactory;
  }
}
