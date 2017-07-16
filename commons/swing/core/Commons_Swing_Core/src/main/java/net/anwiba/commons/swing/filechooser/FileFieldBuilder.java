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

import java.awt.Window;
import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.anwiba.commons.swing.object.AbstractObjectFieldBuilder;
import net.anwiba.commons.swing.object.AbstractObjectTextField;
import net.anwiba.commons.swing.object.IObjectFieldConfiguration;

public class FileFieldBuilder
    extends
    AbstractObjectFieldBuilder<File, FileFieldConfigurationBuilder, FileFieldBuilder> {

  public FileFieldBuilder() {
    super(new FileFieldConfigurationBuilder());
  }

  @Override
  protected AbstractObjectTextField<File> create(final IObjectFieldConfiguration<File> configuration) {
    return new FileField(configuration);
  }

  public FileFieldBuilder setFileValidator() {
    getConfigurationBuilder().setFileValidator();
    return this;
  }

  public FileFieldBuilder setFolderValidator() {
    getConfigurationBuilder().setFolderValidator();
    return this;
  }

  public FileFieldBuilder addFileSaveChooser(final Window owner) {
    getConfigurationBuilder().addFileSaveChooser(owner);
    return this;
  }

  public FileFieldBuilder addFolderSaveChooserAction(final Window owner) {
    getConfigurationBuilder().addFolderSaveChooserAction(owner);
    return this;
  }

  public FileFieldBuilder addFileFilter(final FileFilter fileFilter) {
    getConfigurationBuilder().addFileFilter(fileFilter);
    return this;
  }

  public FileFieldBuilder addFileOpenChooserAction(final Window owner) {
    getConfigurationBuilder().addFileOpenChooserAction(owner);
    return this;
  }

  public FileFieldBuilder addFolderOpenChooserAction(final Window owner) {
    getConfigurationBuilder().addFolderOpenChooserAction(owner);
    return this;
  }
}
