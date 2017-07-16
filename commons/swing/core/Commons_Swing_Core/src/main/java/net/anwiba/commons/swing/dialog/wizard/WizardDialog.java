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
package net.anwiba.commons.swing.dialog.wizard;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.message.IMessageConstants;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.IAdditionalActionFactory;
import net.anwiba.commons.swing.dialog.IDataStateVisitor;
import net.anwiba.commons.swing.dialog.IDialogResult;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.preference.IWindowPreferences;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class WizardDialog extends MessageDialog {

  private static final long serialVersionUID = 1L;
  private final IWizardController controller;
  static ILogger logger = Logging.getLogger(WizardDialog.class.getName());
  private AbstractAction nextAction;
  private AbstractAction backAction;

  public WizardDialog(
      final Window owner,
      final IWindowPreferences preferences,
      final String title,
      final IWizardController controller) {
    super(
        owner,
        preferences,
        title,
        IMessageConstants.EMPTY_MESSAGE,
        GuiIcons.EMPTY_ICON.getLargeIcon(),
        DialogType.NONE,
        true);
    this.controller = controller;
    this.controller.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        updateState();
      }
    });
    this.controller.getNextEnabledDistributor().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        updateNextAction(getNextAction());
      }
    });
    this.controller.getBackEnabledDistributor().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        updateBackAction(getBackAction());
      }
    });
    this.controller.getDataStateDistributor().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        setIcon(controller.getIcon());
        updateOkAction();
      }
    });
    this.controller.getMessageDistributor().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        setMessage(controller.getMessage());
      }
    });
    updateState();
    locate();
  }

  @Override
  protected Action[] getActions(
      final DialogType dialogType,
      final IObjectModel<IDialogResult> dialogResultModel,
      final List<IAdditionalActionFactory> additionalActionFactories) {
    final Action[] actions;
    actions = new Action[4];
    actions[0] = getBackAction();
    actions[1] = getNextAction();
    actions[2] = getCancelAction();
    actions[3] = getOkAction(DialogMessages.FINISH);
    return actions;
  }

  final public Action getNextAction() {
    if (this.nextAction == null) {
      final AbstractAction action = new AbstractAction(DialogMessages.NEXT) {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent event) {
          try {
            next();
          } catch (final Exception exception) {
            logger.log(ILevel.ERROR, "", exception); //$NON-NLS-1$
          }
        }
      };
      this.nextAction = action;
    }
    return this.nextAction;
  }

  final public Action getBackAction() {
    if (this.backAction == null) {
      final AbstractAction action = new AbstractAction(DialogMessages.BACK) {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent event) {
          try {
            back();
          } catch (final Exception exception) {
            logger.log(ILevel.ERROR, "", exception); //$NON-NLS-1$
          }
        }
      };
      this.backAction = action;
    }
    return this.backAction;
  }

  @Override
  final protected boolean cancel() {
    return this.controller.cancel();
  }

  @Override
  final protected boolean apply() {
    return this.controller.apply();
  }

  final protected void next() {
    this.controller.next();
  }

  final protected void back() {
    this.controller.previous();
  }

  final protected void updateState() {
    final IWizardState wizardState = this.controller.getWizardState();
    setIcon(this.controller.getIcon());
    setMessage(this.controller.getMessage());
    setContentPane(this.controller.getContentPane());
    updateOkAction();
    updateNextAction(getNextAction());
    updateBackAction(getBackAction());
  }

  final protected void updateOkAction() {
    checkButton(this.controller.getDataStateDistributor().get());
  }

  @Override
  protected void checkButton(final DataState dataState) {
    if (!this.controller.isFinishable()) {
      setChangeButtonsEnabled(false, false);
      return;
    }
    dataState.accept(new IDataStateVisitor() {

      @Override
      public void visitInvalide() {
        setChangeButtonsEnabled(false, false);
      }

      @Override
      public void visitModified() {
        setChangeButtonsEnabled(true, true);
      }

      @Override
      public void visitUnknown() {
        setChangeButtonsEnabled(false, false);
      }

      @Override
      public void visitValide() {
        setChangeButtonsEnabled(false, true);
      }
    });
  }

  final protected void updateNextAction(final Action action) {
    GuiUtilities
        .invokeLater(() -> action.setEnabled(this.controller.hasNext() && this.controller.getNextEnabledDistributor().get()));
  }

  final protected void updateBackAction(final Action action) {
    GuiUtilities.invokeLater(
        () -> action.setEnabled(this.controller.hasPrevious() && this.controller.getBackEnabledDistributor().get()));
  }
}
