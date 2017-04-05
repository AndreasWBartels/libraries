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
package net.anwiba.commons.swing.dialog.chooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.anwiba.commons.message.ExceptionMessage;
import net.anwiba.commons.message.IMessageConstants;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.combobox.ObjectComboBoxComponent;
import net.anwiba.commons.swing.combobox.ObjectComboBoxComponentModel;
import net.anwiba.commons.swing.component.IInputListener;
import net.anwiba.commons.swing.dialog.IValueDialog;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.dialog.progress.ProgressDialog;
import net.anwiba.commons.swing.icon.GuiIconSize;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.swing.preference.WindowPreferences;
import net.anwiba.commons.utilities.validation.IValidationResult;

@SuppressWarnings("serial")
public class ComboBoxChooserDialog<T> extends MessageDialog implements IValueDialog<T> {

  private IChooserPanelConfiguration<T> chooserPanelConfiguration;
  private IObjectModel<T> valueModel = new ObjectModel<>();
  IObjectDistributor<IValidationResult> validStateModel = new ObjectModel<>(IValidationResult.valid());
  private IChooserPanel<T> chooserPanel;

  IInputListener inputListener = new IInputListener() {

    @SuppressWarnings("synthetic-access")
    @Override
    public void inputHappened() {
      setMessage(ComboBoxChooserDialog.this.chooserPanel.getMessage());
    }
  };

  private final IChangeableObjectListener valueChangeListener = new IChangeableObjectListener() {

    @SuppressWarnings("synthetic-access")
    @Override
    public void objectChanged() {
      if (!ComboBoxChooserDialog.this.validStateModel.get().isValid()) {
        return;
      }
      final T object = ComboBoxChooserDialog.this.valueModel.get();
      if (object == null) {
        setTryEnabled(false);
        setOkEnabled(false);
        return;
      }
      setTryEnabled(!(ComboBoxChooserDialog.this.chooserPanelConfiguration.getTryTaskFactory() == null));
      setOkEnabled(true);
    }
  };

  private final IChangeableObjectListener validateStateListener = new IChangeableObjectListener() {

    @SuppressWarnings("synthetic-access")
    @Override
    public void objectChanged() {
      setMessage(ComboBoxChooserDialog.this.chooserPanel.getMessage());
      if (!ComboBoxChooserDialog.this.validStateModel.get().isValid()) {
        setTryEnabled(false);
        setOkEnabled(false);
        return;
      }
      setTryEnabled(!(ComboBoxChooserDialog.this.chooserPanelConfiguration.getTryTaskFactory() == null));
      setOkEnabled(true);
    }
  };

  public ComboBoxChooserDialog(
      final Window owner,
      final String title,
      final IChooserDialogConfiguration<T> configuration,
      final IPreferences preferences) {
    super(owner, new WindowPreferences(preferences), title, IMessageConstants.EMPTY_MESSAGE, GuiIcons.EMPTY_ICON
        .getLargeIcon(), configuration.getDialogType(), true);
    this.valueModel.set(configuration.getPresetValue());
    createGui(configuration);
    locate();
  }

  private void createGui(final IChooserDialogConfiguration<T> configuration) {
    final ObjectListConfigurationBuilder<IChooserPanelConfiguration<T>> builder = new ObjectListConfigurationBuilder<>();
    builder.setObjectUi(new ChooserPanelConfigurationUi<T>(GuiIconSize.SMALL));
    builder.setSingleSelectionMode();
    builder.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
    final List<IChooserPanelConfiguration<T>> optionPanelConfigurations = configuration.getOptionPanelConfigurations();
    optionPanelConfigurations.sort(new Comparator<IChooserPanelConfiguration<T>>() {

      @Override
      public int compare(final IChooserPanelConfiguration<T> o1, final IChooserPanelConfiguration<T> o2) {
        return Integer.compare(o1.order(), o2.order());
      }
    });
    final ObjectComboBoxComponent<IChooserPanelConfiguration<T>> list = new ObjectComboBoxComponent<>(
        builder.build(),
        new ObjectComboBoxComponentModel<>(optionPanelConfigurations));
    final JComponent comboBoxComponent = list.getComponent();
    final JPanel contentComponent = new JPanel(new GridLayout(1, 1));
    final Dimension minimumSize = new Dimension(300, 50);
    comboBoxComponent.setMinimumSize(new Dimension(150, 30));
    comboBoxComponent.setPreferredSize(new Dimension(150, 30));
    contentComponent.setMinimumSize(minimumSize);
    final JPanel comboBoxComponentContainer = new JPanel(new BorderLayout());
    comboBoxComponentContainer.add(comboBoxComponent, BorderLayout.NORTH);
    final JPanel contentPanel = (JPanel) getContentPane();
    contentPanel.setPreferredSize(new Dimension(480, 200));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    contentPanel.setLayout(new BorderLayout(4, 4));
    contentPanel.add(BorderLayout.NORTH, comboBoxComponentContainer);
    contentPanel.add(BorderLayout.CENTER, contentComponent);
    final IObjectModel<IChooserPanelConfiguration<T>> selectionModel = list.getSelectionModel();
    if (!optionPanelConfigurations.isEmpty()) {
      selectionModel.set(optionPanelConfigurations.get(0));
      if (this.valueModel.get() != null) {
        for (final IChooserPanelConfiguration<T> chooserPanelConfiguration : optionPanelConfigurations) {
          if (!chooserPanelConfiguration.getOptionPanelFactory().isApplicable(this.valueModel.get())) {
            continue;
          }
          selectionModel.set(chooserPanelConfiguration);
          break;
        }
      }
    }
    selectionModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        update(contentComponent, selectionModel);
      }
    });
    update(contentComponent, selectionModel);
  }

  @Override
  protected boolean tryOut() {
    if (this.chooserPanelConfiguration == null) {
      return super.tryOut();
    }
    final ITryTaskFactory<T> tryTaskFactory = this.chooserPanelConfiguration.getTryTaskFactory();
    setTryEnabled(false);
    if (tryTaskFactory == null) {
      return true;
    }
    final ITryTask tryTask = tryTaskFactory.create(this.valueModel.get());
    try {
      ProgressDialog.show(this, Message.create("try"), tryTask); //$NON-NLS-1$
      if (tryTask.isSuccessful()) {
        setMessage(Message.create(this.chooserPanel.getMessage().getText(), "successful")); //$NON-NLS-1$
        return true;
      }
      setMessage(Message.create(this.chooserPanel.getMessage().getText(), "The connection attempt failed.", //$NON-NLS-1$
          MessageType.ERROR));
      setOkEnabled(false);
      return false;
    } catch (final InterruptedException exception) {
      return false;
    } catch (final InvocationTargetException exception) {
      setOkEnabled(false);
      final Throwable targetException = exception.getTargetException();
      setMessage(new ExceptionMessage(
          this.chooserPanel.getMessage().getText(),
          targetException.getLocalizedMessage(),
          targetException));
      return false;
    }
  }

  void update(final JPanel contentComponent, final IObjectModel<IChooserPanelConfiguration<T>> selectionModel) {
    if (this.validStateModel != null) {
      this.validStateModel.removeChangeListener(this.validateStateListener);

    }
    if (this.valueModel != null) {
      this.valueModel.removeChangeListener(this.valueChangeListener);
    }
    if (this.chooserPanel != null) {
      this.chooserPanel.removeInputListener(this.inputListener);
    }
    if (selectionModel.get() == null) {
      ComboBoxChooserDialog.this.chooserPanelConfiguration = null;
      setTryEnabled(false);
      contentComponent.removeAll();
      setIcon(null);
      setMessage(null);
      setTryEnabled(false);
      setOkEnabled(false);
      return;
    }
    ComboBoxChooserDialog.this.chooserPanelConfiguration = selectionModel.get();
    final IChooserPanelFactory<T> chooserPanelFactory = ComboBoxChooserDialog.this.chooserPanelConfiguration
        .getOptionPanelFactory();
    this.chooserPanel = chooserPanelFactory.create(getOwner(), this.valueModel.get());
    this.chooserPanel.addInputListener(this.inputListener);
    setIcon(ComboBoxChooserDialog.this.chooserPanelConfiguration.getGuiIcon().getLargeIcon());
    setMessage(this.chooserPanel.getMessage());
    this.valueModel = this.chooserPanel.getModel();
    this.validStateModel = this.chooserPanel.getValidStateModel();
    this.valueModel.addChangeListener(this.valueChangeListener);
    this.validStateModel.addChangeListener(this.validateStateListener);
    contentComponent.removeAll();
    contentComponent.add(this.chooserPanel.getComponent());
    if (this.valueModel.get() == null || !this.validStateModel.get().isValid()) {
      setTryEnabled(false);
      setOkEnabled(false);
      return;
    }
    setTryEnabled(!(ComboBoxChooserDialog.this.chooserPanelConfiguration.getTryTaskFactory() == null));
    setOkEnabled(true);
  }

  @Override
  public T getValue() {
    if (this.valueModel == null) {
      return null;
    }
    this.chooserPanel.savePreferences();
    return this.valueModel.get();
  }
}