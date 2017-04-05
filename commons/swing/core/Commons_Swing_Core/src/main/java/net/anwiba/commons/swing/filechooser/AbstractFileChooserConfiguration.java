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

import javax.swing.filechooser.FileFilter;

public abstract class AbstractFileChooserConfiguration implements IFileChooserConfiguration {
  private final Iterable<FileFilter> fileFilters;
  private final int fileSelectionMode;
  private final boolean isMultiSelectionEnabled;
  private final boolean isAllFilterEnabled;
  private final File presetFile;

  public AbstractFileChooserConfiguration(
      final File presetFile,
      final Iterable<FileFilter> fileFilters,
      final int fileSelectionMode,
      final boolean isAllFilterEnabled,
      final boolean isMultiSelectionEnabled) {
    this.fileFilters = fileFilters;
    this.fileSelectionMode = fileSelectionMode;
    this.isAllFilterEnabled = isAllFilterEnabled;
    this.isMultiSelectionEnabled = isMultiSelectionEnabled;
    this.presetFile = presetFile;
  }

  @Override
  public File getPresetFile() {
    return this.presetFile;
  }

  @Override
  public Iterable<FileFilter> getFileFilters() {
    return this.fileFilters;
  }

  @Override
  public int getFileSelectionMode() {
    return this.fileSelectionMode;
  }

  @Override
  public boolean isMultiSelectionEnabled() {
    return this.isMultiSelectionEnabled;
  }

  @Override
  public boolean isAllFilterEnabled() {
    return this.isAllFilterEnabled;
  }
}
