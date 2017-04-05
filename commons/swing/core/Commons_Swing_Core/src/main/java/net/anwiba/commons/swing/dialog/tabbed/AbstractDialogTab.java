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
package net.anwiba.commons.swing.dialog.tabbed;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.IDataStateListener;

public abstract class AbstractDialogTab implements IDialogTab {

  private DataState dataState = DataState.VALIDE;
  List<IDataStateListener> dataStateListeners = new ArrayList<>();
  private final Icon icon;
  private final String title;
  private Component component;
  private IMessage currentMessage;
  private final IMessage defaultMessage;
  private Window owner;

  public AbstractDialogTab(final String title, final IMessage defaultMessage, final Icon defaultIcon) {
    super();
    Ensure.ensureArgumentNotNull(title);
    Ensure.ensureArgumentNotNull(defaultIcon);
    this.title = title;
    this.defaultMessage = defaultMessage;
    this.currentMessage = defaultMessage;
    this.icon = defaultIcon;
  }

  @Override
  public void setOwnerWindow(final Window owner) {
    this.owner = owner;
  }

  public Window getOwner() {
    return this.owner;
  }

  protected void setComponent(final Component component) {
    this.component = component;
  }

  @Override
  public Component getComponent() {
    if (this.component == null) {
      setComponent(new JPanel());
    }
    return this.component;
  }

  @Override
  public Icon getIcon() {
    return this.icon;
  }

  @Override
  public IMessage getMessage() {
    if (this.currentMessage == null) {
      return this.defaultMessage;
    }
    return this.currentMessage;
  }

  @Override
  public String getTitle() {
    return this.title;
  }

  public void setCurrentMessage(final IMessage message) {
    this.currentMessage = message;
  }

  @Override
  public DataState getDataState() {
    return this.dataState;
  }

  @Override
  public void addDataStateListener(final IDataStateListener listener) {
    this.dataStateListeners.add(listener);
  }

  @Override
  public void removeDataStateListener(final IDataStateListener listener) {
    this.dataStateListeners.remove(listener);
  }

  public void setDataState(final DataState dataState) {
    if (dataState == this.dataState) {
      return;
    }
    this.dataState = dataState;
    fireDataStateChanged();
  }

  private void fireDataStateChanged() {
    for (final IDataStateListener listener : this.dataStateListeners) {
      listener.dataStateChanged();
    }
  }

  private final KeyListener keyListener = new KeyListener() {

    @Override
    public void keyPressed(final KeyEvent event) {
      // nothing todo
    }

    @Override
    public void keyReleased(final KeyEvent event) {
      checkFieldValues();
    }

    @Override
    public void keyTyped(final KeyEvent event) {
      // nothing todo
    }
  };

  private final ActionListener actionListener = new ActionListener() {

    @Override
    public void actionPerformed(final ActionEvent e) {
      checkFieldValues();
    }
  };

  private final ChangeListener changeListener = new ChangeListener() {

    @Override
    public void stateChanged(final ChangeEvent e) {
      checkFieldValues();
    }
  };

  public ActionListener getActionListener() {
    return this.actionListener;
  }

  protected KeyListener getKeyListener() {
    return this.keyListener;
  }

  protected ChangeListener getChangeListener() {
    return this.changeListener;
  }

  @Override
  public abstract void checkFieldValues();

  protected IMessage getDefaultMessage() {
    return this.defaultMessage;
  }
}