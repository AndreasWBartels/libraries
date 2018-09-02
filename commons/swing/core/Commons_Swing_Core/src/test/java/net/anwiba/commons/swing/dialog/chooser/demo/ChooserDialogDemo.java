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
package net.anwiba.commons.swing.dialog.chooser.demo;

import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.component.IInputListener;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.chooser.ChooserPanelConfiguration;
import net.anwiba.commons.swing.dialog.chooser.IChooserDialogConfiguration;
import net.anwiba.commons.swing.dialog.chooser.IChooserPanel;
import net.anwiba.commons.swing.dialog.chooser.IChooserPanelConfiguration;
import net.anwiba.commons.swing.dialog.chooser.IChooserPanelFactory;
import net.anwiba.commons.swing.dialog.chooser.ListChooserDialog;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.utilities.validation.IValidationResult;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

@RunWith(DemoAsTestRunner.class)
public class ChooserDialogDemo extends SwingDemoCase {

  @Demo
  public void demo() {
    final Dimension minimumSize = new Dimension(300, 50);
    final ArrayList<IChooserPanelConfiguration<Object>> list = new ArrayList<>();
    list.add(new ChooserPanelConfiguration<>(
        "Folder", GuiIcons.FOLDER_ICON, "Folder", new IChooserPanelFactory<Object>() { //$NON-NLS-1$ //$NON-NLS-2$

          @Override
          public IChooserPanel<Object> create(final Window owner, final Object value) {
            final JFileChooser contentComponent = new JFileChooser();
            contentComponent.setControlButtonsAreShown(false);
            contentComponent.setMinimumSize(minimumSize);
            contentComponent.setPreferredSize(new Dimension(300, 200));
            return new IChooserPanel<Object>() {
              ObjectModel<Object> objectModel = new ObjectModel<>();
              IObjectModel<IValidationResult> validStateModel = new ObjectModel<>(IValidationResult.valid());

              @Override
              public JComponent getComponent() {
                return contentComponent;
              }

              @Override
              public IMessage getMessage() {
                return Message.create("Folder", "Choose a folder", MessageType.DEFAULT); //$NON-NLS-1$ //$NON-NLS-2$
              }

              @Override
              public ObjectModel<Object> getModel() {
                return this.objectModel;
              }

              @Override
              public IObjectDistributor<IValidationResult> getValidStateModel() {
                return this.validStateModel;
              }

              @Override
              public void savePreferences() {
                // nothing to do
              }

              @Override
              public void addInputListener(final IInputListener listener) {
                // nothing to do
              }

              @Override
              public void removeInputListener(final IInputListener listener) {
                // nothing to do
              }
            };
          }

          @Override
          public boolean isApplicable(final Object value) {
            return false;
          }
        },
        null,
        0));
    list.add(new ChooserPanelConfiguration<>("DB", GuiIcons.DB_ICON, "Database", new IChooserPanelFactory<Object>() { //$NON-NLS-1$ //$NON-NLS-2$

          @Override
          public IChooserPanel<Object> create(final Window owner, final Object value) {
            final JPanel contentComponent = new JPanel();
            contentComponent.setMinimumSize(minimumSize);
            return new IChooserPanel<Object>() {
              ObjectModel<Object> objectModel = new ObjectModel<>();
              IObjectModel<IValidationResult> validStateModel = new ObjectModel<>(IValidationResult.valid());

              @Override
              public JComponent getComponent() {
                return contentComponent;
              }

              @Override
              public IMessage getMessage() {
                return Message.create("DB", "Choose a database", MessageType.DEFAULT); //$NON-NLS-1$ //$NON-NLS-2$
              }

              @Override
              public ObjectModel<Object> getModel() {
                return this.objectModel;
              }

              @Override
              public IObjectDistributor<IValidationResult> getValidStateModel() {
                return this.validStateModel;
              }

              @Override
              public void savePreferences() {
                // nothing to do
              }

              @Override
              public void addInputListener(final IInputListener listener) {
                // nothing to do
              }

              @Override
              public void removeInputListener(final IInputListener listener) {
                // nothing to do
              }
            };
          }

          @Override
          public boolean isApplicable(final Object value) {
            return false;
          }
        },
        null,
        1));
    final IChooserDialogConfiguration<Object> configuration = new IChooserDialogConfiguration<Object>() {

      @Override
      public List<IChooserPanelConfiguration<Object>> getOptionPanelConfigurations() {
        return list;
      }

      @Override
      public Object getPresetValue() {
        return null;
      }

      @Override
      public DialogType getDialogType() {
        return DialogType.CANCEL_OK;
      }
    };
    final ListChooserDialog<Object> dialog = new ListChooserDialog<>(createJFrame(), "Title", configuration); //$NON-NLS-1$
    show(dialog);
  }
}
