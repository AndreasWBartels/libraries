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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;

import javax.accessibility.AccessibleContext;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.UIManager;

import net.anwiba.commons.swing.preference.IWindowPreferences;
import net.anwiba.commons.swing.preference.WindowPrefereneceUpdatingListener;
import net.anwiba.commons.swing.utilities.ContainerUtilities;

public final class FileChooserDialog extends JFileChooser {

  private static final long serialVersionUID = 1L;
  private final IWindowPreferences windowPreferences;

  public FileChooserDialog(final IWindowPreferences windowPreferences) {
    this.windowPreferences = windowPreferences;
  }

  @Override
  protected JDialog createDialog(final Component parent) throws HeadlessException {
    final String title = getUI().getDialogTitle(this);
    putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, title);
    final JDialog dialog;
    final Window window = ContainerUtilities.getParentWindow(parent);
    if (window instanceof Frame) {
      dialog = new JDialog((Frame) window, title, true);
    } else {
      dialog = new JDialog((Dialog) window, title, true);
    }
    dialog.setComponentOrientation(getComponentOrientation());
    final Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(this, BorderLayout.CENTER);
    if (JDialog.isDefaultLookAndFeelDecorated()) {
      final boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (supportsWindowDecorations) {
        dialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
      }
    }
    final WindowPrefereneceUpdatingListener updater =
        new WindowPrefereneceUpdatingListener(dialog, this.windowPreferences);
    if (this.windowPreferences.getBounds() == null) {
      dialog.pack();
      dialog.setLocationRelativeTo(parent);
      dialog.addComponentListener(updater);
      dialog.addWindowListener(updater);
      return dialog;
    }
    dialog.setBounds(this.windowPreferences.getBounds());
    dialog.addComponentListener(updater);
    dialog.addWindowListener(updater);
    return dialog;
  }

}