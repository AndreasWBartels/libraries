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

import java.awt.Container;
import java.util.List;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.JPanel;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.icons.GuiIcons;

public abstract class AbstractWizardController implements IWizardController {

  private final Object mutex = new Object();
  private final BooleanModel nextEnabledModel = new BooleanModel(true);
  private final IChangeableObjectListener nextEnableListener = new IChangeableObjectListener() {

    @Override
    public void objectChanged() {
      AbstractWizardController.this.nextEnabledModel
          .set(AbstractWizardController.this.currentPage.getNextEnabledModel().isTrue());
    }
  };

  private final BooleanModel backEnabledModel = new BooleanModel(true);
  private final IChangeableObjectListener backEnableListener = new IChangeableObjectListener() {

    @Override
    public void objectChanged() {
      AbstractWizardController.this.backEnabledModel
          .set(AbstractWizardController.this.currentPage.getBackEnabledModel().isTrue());
    }
  };

  private final IObjectModel<DataState> dataStateModel;
  private final IChangeableObjectListener dataStateListener;
  private final IObjectModel<String> messageModel = new ObjectModel<>();
  private final IChangeableObjectListener messageListener = new IChangeableObjectListener() {

    @Override
    public void objectChanged() {
      if (AbstractWizardController.this.currentPage == null) {
        AbstractWizardController.this.messageModel.set(null);
        return;
      }
      AbstractWizardController.this.messageModel
          .set(AbstractWizardController.this.currentPage.getMessageDistributor().get());
    }
  };
  private final List<IWizardPage> container;
  private final Stack<Integer> visitedPages = new Stack<>();
  private final IObjectModel<IWizardState> wizardStateModel = new ObjectModel<>();

  private IWizardPage currentPage;
  private int index = -1;

  public AbstractWizardController(final List<IWizardPage> container, final IObjectModel<DataState> dataStateModel) {
    this(container, dataStateModel, new ObjectModel<>());
  }

  public AbstractWizardController(
      final List<IWizardPage> container,
      final IObjectModel<DataState> dataStateModel,
      final IObjectModel<IWizardAction> wizardActionModel) {
    this.container = container;
    this.dataStateModel = dataStateModel;
    this.dataStateListener = new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        final IWizardPage page = AbstractWizardController.this.currentPage;
        AbstractWizardController.this.dataStateModel.set(page.getDataStateModel().get());
      }
    };
    wizardActionModel.addChangeListener(() -> Optional.of(wizardActionModel.get()).consume(a -> a.execute(this)));
    next();
  }

  @Override
  public boolean hasNext() {
    if (this.index < 0 && !this.container.isEmpty()) {
      return true;
    }
    final IWizardPage page = this.container.get(this.index);
    for (int i = this.index + 1; i < this.container.size(); i++) {
      if (this.container.get(i).isApplicable(page)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasPrevious() {
    return !this.visitedPages.isEmpty();
  }

  protected IWizardPage getPrevious() {
    if (this.visitedPages.isEmpty()) {
      return this.container.get(this.index);
    }
    this.index = this.visitedPages.pop();
    return this.container.get(this.index);
  }

  protected IWizardPage getNext() {
    if (this.index < 0) {
      this.index = 0;
      return this.container.get(this.index);
    }
    final IWizardPage page = this.container.get(this.index);
    for (int i = this.index + 1; i < this.container.size(); i++) {
      if (this.container.get(i).isApplicable(page)) {
        this.visitedPages.add(this.index);
        this.index = i;
        return this.container.get(this.index);
      }
    }
    return this.container.get(this.index);
  }

  @Override
  public void previous() {
    if (hasPrevious()) {
      updateWizardState(getPrevious());
    }
  }

  @Override
  public void next() {
    if (hasNext()) {
      updateWizardState(getNext());
    }
  }

  @Override
  public boolean cancel() {
    return true;
  }

  @Override
  public boolean finish() {
    synchronized (this.mutex) {
      return this.currentPage.finishable();
    }
  }

  private void updateWizardState(final IWizardPage page) {
    synchronized (this.mutex) {
      if (this.currentPage != null) {
        this.currentPage.getNextEnabledModel().removeChangeListener(this.nextEnableListener);
        this.currentPage.getBackEnabledModel().removeChangeListener(this.backEnableListener);
        this.currentPage.getDataStateModel().removeChangeListener(this.dataStateListener);
        this.currentPage.getMessageDistributor().removeChangeListener(this.messageListener);
      }
      this.currentPage = page;
      this.nextEnabledModel.set(this.currentPage.getNextEnabledModel().isTrue() && hasNext());
      this.backEnabledModel.set(this.currentPage.getBackEnabledModel().isTrue() && hasPrevious());
      this.dataStateModel.set(this.currentPage.getDataStateModel().get());
      this.currentPage.getNextEnabledModel().addChangeListener(this.nextEnableListener);
      this.currentPage.getBackEnabledModel().addChangeListener(this.backEnableListener);
      this.currentPage.getDataStateModel().addChangeListener(this.dataStateListener);
      this.currentPage.getMessageDistributor().addChangeListener(this.messageListener);
    }
    this.wizardStateModel.set(new WizardState());
  }

  @Override
  public void addChangeListener(final IChangeableObjectListener changeableObjectListener) {
    this.wizardStateModel.addChangeListener(changeableObjectListener);
  }

  @Override
  public void removeChangeListener(final IChangeableObjectListener changeableObjectListener) {
    this.wizardStateModel.removeChangeListener(changeableObjectListener);
  }

  @Override
  public IWizardState getWizardState() {
    return this.wizardStateModel.get();
  }

  @Override
  public boolean isFinishable() {
    return this.currentPage.finishable();
  }

  @Override
  public IBooleanDistributor getNextEnabledDistributor() {
    return this.nextEnabledModel;
  }

  @Override
  public IBooleanDistributor getBackEnabledDistributor() {
    return this.backEnabledModel;
  }

  @Override
  public IMessage getMessage() {
    synchronized (this.mutex) {
      if (this.currentPage == null) {
        return null;
      }
      final String message = this.currentPage.getMessage();
      if (DataState.INVALIDE.equals(this.dataStateModel.get())) {
        return Message.create(this.currentPage.getTitle(), message, MessageType.ERROR);
      }
      return Message.create(this.currentPage.getTitle(), message);
    }
  }

  @Override
  public Icon getIcon() {
    synchronized (this.mutex) {
      if (this.currentPage == null) {
        return GuiIcons.EMPTY_ICON.getLargeIcon();
      }
      return this.currentPage.getIcon();
    }
  }

  @Override
  public Container getContentPane() {
    synchronized (this.mutex) {
      if (this.currentPage == null) {
        return new JPanel();
      }
      return this.currentPage.getComponent();
    }
  }

  @Override
  public IObjectDistributor<DataState> getDataStateDistributor() {
    return this.dataStateModel;
  }

  @Override
  public IObjectDistributor<String> getMessageDistributor() {
    return this.messageModel;
  }

  @Override
  public boolean apply() {
    return true;
  }

}