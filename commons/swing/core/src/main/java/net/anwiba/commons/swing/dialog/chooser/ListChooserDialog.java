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
import javax.swing.SwingConstants;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.message.IMessageConstants;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageBuilder;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.component.IInputListener;
import net.anwiba.commons.swing.dialog.AbstractMessageDialog;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.dialog.IValueDialog;
import net.anwiba.commons.swing.dialog.progress.ProgressDialog;
import net.anwiba.commons.swing.icon.GuiIconSize;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.list.ObjectListComponent;
import net.anwiba.commons.swing.list.ObjectListComponentModel;
import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.utilities.validation.IValidationResult;

@SuppressWarnings("serial")
public class ListChooserDialog<T> extends AbstractMessageDialog implements IValueDialog<T> {

  private IChooserPanelConfiguration<T> chooserPanelConfiguration;
  private IObjectModel<T> valueModel = new ObjectModel<>();
  IObjectDistributor<IValidationResult> validStateModel = new ObjectModel<>(IValidationResult.valid());
  private IChooserPanel<T> chooserPanel;

  IInputListener inputListener = new IInputListener() {

    @Override
    public void inputHappened() {
      setMessage(ListChooserDialog.this.chooserPanel.getMessage());
    }
  };

  private final IChangeableObjectListener valueChangeListener = new IChangeableObjectListener() {

    @Override
    public void objectChanged() {
      if (!ListChooserDialog.this.validStateModel.get().isValid()) {
        setMessage(
            new MessageBuilder().setText(ListChooserDialog.this.validStateModel.get().getMessage()).setError().build());
        setTryEnabled(false);
        setOkEnabled(false);
        return;
      }
      setMessage(ListChooserDialog.this.chooserPanel.getMessage());
      setTryEnabled(!(ListChooserDialog.this.chooserPanelConfiguration.getTryTaskFactory() == null));
      setOkEnabled(true);
    }
  };

  private final IChangeableObjectListener validateStateListener = new IChangeableObjectListener() {

    @Override
    public void objectChanged() {
      setMessage(ListChooserDialog.this.chooserPanel.getMessage());
      if (!ListChooserDialog.this.validStateModel.get().isValid()) {
        setTryEnabled(false);
        setOkEnabled(false);
        return;
      }
      setTryEnabled(!(ListChooserDialog.this.chooserPanelConfiguration.getTryTaskFactory() == null));
      setOkEnabled(true);
    }
  };

  public ListChooserDialog(final Window owner, final String title, final IChooserDialogConfiguration<T> configuration) {
    super(
        owner,
        title,
        IMessageConstants.EMPTY_MESSAGE,
        GuiIcons.EMPTY_ICON.getLargeIcon(),
        configuration.getDialogType(),
        true);
    this.valueModel.set(configuration.getPresetValue());
    createGui(configuration);
  }

  private void createGui(final IChooserDialogConfiguration<T> configuration) {
    final ObjectListConfigurationBuilder<IChooserPanelConfiguration<T>> builder =
        new ObjectListConfigurationBuilder<>();
    builder.setObjectUi(new ChooserPanelConfigurationUi<T>(GuiIconSize.LARGE));
    builder.setSingleSelectionMode();
    builder.setHorizontalTextPosition(SwingConstants.CENTER);
    builder.setHorizontalAlignment(SwingConstants.CENTER);
    builder.setVerticalTextPosition(SwingConstants.BOTTOM);
    builder.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
    final List<IChooserPanelConfiguration<T>> optionPanelConfigurations = configuration.getOptionPanelConfigurations();
    optionPanelConfigurations.sort(new Comparator<IChooserPanelConfiguration<T>>() {

      @Override
      public int compare(final IChooserPanelConfiguration<T> o1, final IChooserPanelConfiguration<T> o2) {
        return Integer.compare(o1.order(), o2.order());
      }
    });
    final ObjectListComponent<IChooserPanelConfiguration<T>> list = new ObjectListComponent<>(
        builder.build(),
        new ObjectListComponentModel<>(optionPanelConfigurations));
    final JComponent listComponent = list.getComponent();
    final JPanel contentComponent = new JPanel(new GridLayout(1, 1));
    final Dimension minimumSize = new Dimension(300, 50);
    listComponent.setMinimumSize(new Dimension(80, 50));
    listComponent.setPreferredSize(new Dimension(80, 50));
    contentComponent.setMinimumSize(minimumSize);
    final JPanel contentPanel = (JPanel) getContentPane();
    contentPanel.setPreferredSize(new Dimension(480, 300));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    contentPanel.setLayout(new BorderLayout(4, 4));
    contentPanel.add(BorderLayout.WEST, listComponent);
    contentPanel.add(BorderLayout.CENTER, contentComponent);
    final ISelectionModel<IChooserPanelConfiguration<T>> selectionModel = list.getSelectionModel();
    if (!optionPanelConfigurations.isEmpty()) {
      selectionModel.setSelectedObject(optionPanelConfigurations.get(0));
      if (this.valueModel.get() != null) {
        for (@SuppressWarnings("hiding")
        final IChooserPanelConfiguration<T> chooserPanelConfiguration : optionPanelConfigurations) {
          if (!chooserPanelConfiguration.getOptionPanelFactory().isApplicable(this.valueModel.get())) {
            continue;
          }
          selectionModel.setSelectedObject(chooserPanelConfiguration);
          break;
        }
      }
    }
    selectionModel.addSelectionListener(new ISelectionListener<IChooserPanelConfiguration<T>>() {

      @Override
      public void selectionChanged(final SelectionEvent<IChooserPanelConfiguration<T>> event) {
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
      ProgressDialog.show(this, "try", Message.create(DialogMessages.TRY), tryTask); //$NON-NLS-1$
      if (tryTask.isSuccessful()) {
        setMessage(Message.create(this.chooserPanel.getMessage().getText(), "successful")); //$NON-NLS-1$
        return true;
      }
      setMessage(
          Message.create(
              this.chooserPanel.getMessage().getText(),
              "The connection attempt failed.", //$NON-NLS-1$
              MessageType.ERROR));
      setOkEnabled(false);
      return false;
    } catch (final CanceledException exception) {
      return false;
    } catch (final InvocationTargetException exception) {
      setOkEnabled(false);
      final Throwable targetException = exception.getTargetException();
      setMessage(
          Message.builder()
              .setText(this.chooserPanel.getMessage().getText())
              .setDescription(targetException.getMessage())
              .setThrowable(targetException)
              .setError()
              .build());
      return false;
    }
  }

  void update(final JPanel contentComponent, final ISelectionModel<IChooserPanelConfiguration<T>> selectionModel) {
    if (this.validStateModel != null) {
      this.validStateModel.removeChangeListener(this.validateStateListener);

    }
    if (this.valueModel != null) {
      this.valueModel.removeChangeListener(this.valueChangeListener);
    }
    if (this.chooserPanel != null) {
      this.chooserPanel.removeInputListener(this.inputListener);
    }
    if (selectionModel.isEmpty()) {
      ListChooserDialog.this.chooserPanelConfiguration = null;
      setTryEnabled(false);
      contentComponent.removeAll();
      setIcon(null);
      setMessage(null);
      setTryEnabled(false);
      setOkEnabled(false);
      return;
    }
    ListChooserDialog.this.chooserPanelConfiguration = selectionModel.getSelectedObjects().iterator().next();
    final IChooserPanelFactory<T> chooserPanelFactory = ListChooserDialog.this.chooserPanelConfiguration
        .getOptionPanelFactory();
    this.chooserPanel = chooserPanelFactory.create(getOwner(), this.valueModel.get());
    this.chooserPanel.addInputListener(this.inputListener);
    setIcon(ListChooserDialog.this.chooserPanelConfiguration.getGuiIcon().getLargeIcon());
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
    setTryEnabled(!(ListChooserDialog.this.chooserPanelConfiguration.getTryTaskFactory() == null));
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