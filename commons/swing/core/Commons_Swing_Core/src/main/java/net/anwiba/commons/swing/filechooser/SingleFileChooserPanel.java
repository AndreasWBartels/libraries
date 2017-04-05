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

import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.preferences.IPreferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

public final class SingleFileChooserPanel implements IFileChooserPanel {
  private final JFileChooser fileChooser;
  private final IPreferences preferences;
  final IObjectModel<File> model = new ObjectModel<>();

  public SingleFileChooserPanel(final JFileChooser fileChooser, final IPreferences preferences) {
    this.fileChooser = fileChooser;
    this.preferences = preferences;
    fileChooser.addPropertyChangeListener(JFileChooser.APPROVE_SELECTION, new PropertyChangeListener() {

      @Override
      public void propertyChange(final PropertyChangeEvent event) {
        final File file = (File) event.getNewValue();
        getModel().set(file);
      }
    });

    fileChooser.addPropertyChangeListener(
        JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY,
        new PropertyChangeListener() {

          @Override
          public void propertyChange(final PropertyChangeEvent event) {
            final File file = (File) event.getNewValue();
            getModel().set(file);
          }
        });
    fileChooser.addPropertyChangeListener(JFileChooser.APPROVE_SELECTION, new PropertyChangeListener() {

      @Override
      public void propertyChange(final PropertyChangeEvent event) {
        final File file = (File) event.getNewValue();
        getModel().set(file);
      }
    });
    fileChooser.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, new PropertyChangeListener() {

      @Override
      public void propertyChange(final PropertyChangeEvent event) {
        final File file = (File) event.getNewValue();
        getModel().set(file);
      }
    });
    if (fileChooser.getSelectedFile() != null) {
      getModel().set(fileChooser.getSelectedFile());
      return;
    }
    if (fileChooser.getFileSelectionMode() != JFileChooser.DIRECTORIES_ONLY) {
      return;
    }
    fileChooser.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY, new PropertyChangeListener() {

      @Override
      public void propertyChange(final PropertyChangeEvent event) {
        final File file = (File) event.getNewValue();
        getModel().set(file);
      }
    });
    getModel().set(fileChooser.getCurrentDirectory());
  }

  @Override
  public JComponent getComponent() {
    return this.fileChooser;
  }

  @Override
  public void savePreferences() {
    final File currentDirectory = this.fileChooser.getCurrentDirectory();
    if (currentDirectory == null) {
      return;
    }
    this.preferences.put("folder", currentDirectory.getAbsolutePath()); //$NON-NLS-1$
  }

  public IObjectModel<File> getModel() {
    return this.model;
  }
}