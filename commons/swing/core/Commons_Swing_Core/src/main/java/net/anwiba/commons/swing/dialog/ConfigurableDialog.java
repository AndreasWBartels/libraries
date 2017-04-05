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
package net.anwiba.commons.swing.dialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;

@SuppressWarnings("serial")
public class ConfigurableDialog extends AbstractDialog {

  private final IContentPanel contentPane;
  private final IObjectModel<DataState> dataStateModel;

  public ConfigurableDialog(final Window owner, final IDialogConfiguration configuration) {
    super(
        owner,
        configuration.getWindowPreferences(),
        configuration.getPreferdSize(),
        configuration.getTitle(),
        configuration.getMessage(),
        configuration.getIcon(),
        configuration.isMessagePanelEnabled(),
        configuration.getDialogType(),
        configuration.getActionButtonTextFactory(),
        configuration.getAdditionalActionFactories(),
        configuration.getModalityType());
    this.contentPane = configuration.getContentPaneBuilder().setOwner(owner).build();
    setContentPane(this.contentPane.getComponent());
    this.dataStateModel = this.contentPane.getDataStateModel();
    this.dataStateModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        checkButton(ConfigurableDialog.this.dataStateModel.get());
      }
    });
    checkButton(this.dataStateModel.get());
    final IObjectModel<IMessage> messageModel = this.contentPane.getMessageModel();
    messageModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        setMessage(messageModel.get());
      }
    });
    this.dataStateModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        setMessage(messageModel.get());
      }
    });
    setMessage(messageModel.get());
    setResizable(configuration.isResizeable());

    final int dialogCloseKeyEvent = configuration.getDialogCloseKeyEvent();
    if (dialogCloseKeyEvent != KeyEvent.KEY_LOCATION_UNKNOWN) {
      final KeyStroke stroke = KeyStroke.getKeyStroke(dialogCloseKeyEvent, 0);
      final JRootPane pane = getRootPane();
      pane.registerKeyboardAction(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
          setVisible(false);
        }
      }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    locate();
  }

  @Override
  protected Action[] getActions(
      final DialogType dialogType,
      final IObjectModel<IDialogResult> dialogResultModel,
      final List<IAdditionalActionFactory> additionalActionFactories) {
    final Action[] result = super.getActions(dialogType, dialogResultModel, additionalActionFactories);
    final List<Action> actions = new ArrayList<>();
    for (final IAdditionalActionFactory additionalActionFactory : additionalActionFactories) {
      actions.add(additionalActionFactory.create(dialogResultModel, this.dataStateModel, () -> close()));
    }
    for (final Action action : result) {
      actions.add(action);
    }
    return actions.toArray(new Action[actions.size()]);
  }

  @Override
  protected boolean apply() {
    return this.contentPane.apply();
  }

  @Override
  protected boolean tryOut() {
    return this.contentPane.tryOut();
  }

  @Override
  protected boolean cancel() {
    return this.contentPane.cancel();
  }

  @Override
  protected void close() {
    this.contentPane.close();
    super.close();
  }
}
