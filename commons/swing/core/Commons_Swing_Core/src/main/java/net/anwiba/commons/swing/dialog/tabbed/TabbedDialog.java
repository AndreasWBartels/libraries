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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.IMessageConstants;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.IAdditionalActionFactory;
import net.anwiba.commons.swing.dialog.IDataStateListener;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.preference.IWindowPreferences;

public class TabbedDialog extends MessageDialog {

  private static final long serialVersionUID = 1L;
  final JTabbedPane tabbedPanel = new JTabbedPane();
  final List<IDialogTab> tabs = new ArrayList<>();

  public TabbedDialog(final Window owner, final String title) {
    this(
        owner,
        title,
        IMessageConstants.EMPTY_MESSAGE,
        GuiIcons.EMPTY_ICON.getLargeIcon(),
        DialogType.CANCEL_APPLY_OK,
        true);
  }

  public TabbedDialog(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType) {
    this(owner, title, message, icon, dialogType, true);
  }

  public TabbedDialog(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final boolean modal) {
    super(owner, title, message, icon, dialogType, modal);
    createTabbedView(i -> new ArrayList<>());
    locate();
  }

  public TabbedDialog(
      final Window owner,
      final IWindowPreferences preferences,
      final String title,
      final IFunction<Void, Iterable<IDialogTab>, RuntimeException> tabsFactory) {
    this(
        owner,
        preferences,
        title,
        IMessageConstants.EMPTY_MESSAGE,
        GuiIcons.EMPTY_ICON.getLargeIcon(),
        DialogType.CANCEL_APPLY_OK,
        Collections.emptyList(),
        new ObjectModel<>(),
        true,
        tabsFactory);
  }

  public TabbedDialog(
      final Window owner,
      final IWindowPreferences preferences,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final IFunction<Void, Iterable<IDialogTab>, RuntimeException> tabsFactory) {
    this(
        owner,
        preferences,
        title,
        message,
        icon,
        dialogType,
        Collections.emptyList(),
        new ObjectModel<>(),
        true,
        tabsFactory);
  }

  public TabbedDialog(
      final Window owner,
      final IWindowPreferences preferences,
      final String title,
      final List<IAdditionalActionFactory> actionFactories,
      final IObjectModel<DataState> dataStateModel) {
    this(
        owner,
        preferences,
        title,
        IMessageConstants.EMPTY_MESSAGE,
        GuiIcons.EMPTY_ICON.getLargeIcon(),
        DialogType.CANCEL_APPLY_OK,
        actionFactories,
        dataStateModel,
        true,
        v -> Collections.emptyList());
  }

  public TabbedDialog(
      final Window owner,
      final IWindowPreferences preferences,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final List<IAdditionalActionFactory> actionFactories,
      final IObjectModel<DataState> dataStateModel,
      final boolean modal,
      final IFunction<Void, Iterable<IDialogTab>, RuntimeException> tabsFactory) {
    super(owner, preferences, title, message, icon, dialogType, actionFactories, dataStateModel, modal);
    createTabbedView(tabsFactory);
    locate();
  }

  private void createTabbedView(final IFunction<Void, Iterable<IDialogTab>, RuntimeException> tabsFactory) {
    this.tabbedPanel.setMinimumSize(new Dimension(100, 100));
    final JPanel contentPanel = (JPanel) getContentPane();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.add(BorderLayout.CENTER, this.tabbedPanel);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    for (final IDialogTab tab : tabsFactory.execute(null)) {
      addTab(tab);
    }
    this.tabbedPanel.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(final ChangeEvent e) {
        final int selectedIndex = TabbedDialog.this.tabbedPanel.getSelectedIndex();
        if (selectedIndex < 0) {
          setIcon(GuiIcons.EMPTY_ICON.getLargeIcon());
          setMessage(IMessageConstants.EMPTY_MESSAGE);
          return;
        }
        final IDialogTab currentTab = TabbedDialog.this.tabs.get(selectedIndex);
        currentTab.updateView();
        currentTab.checkFieldValues();
        setIcon(currentTab.getIcon());
        setMessage(currentTab.getMessage());
        checkButton(currentTab);
      }
    });
  }

  @Override
  protected boolean apply() {
    final int selectedIndex = TabbedDialog.this.tabbedPanel.getSelectedIndex();
    if (selectedIndex < 0) {
      return true;
    }
    final IDialogTab tab = TabbedDialog.this.tabs.get(selectedIndex);
    return tab.apply();
  }

  public void addTab(final IDialogTab dialogTab) {
    this.tabs.add(dialogTab);
    dialogTab.addDataStateListener(new IDataStateListener() {

      @Override
      public void dataStateChanged() {
        setMessage(dialogTab.getMessage());
        checkButton(dialogTab);
      }

    });
    dialogTab.setOwnerWindow(getOwner());
    this.tabbedPanel.addTab(dialogTab.getTitle(), dialogTab.getComponent());
  }

  void checkButton(final IDialogTab dialogTab) {
    final DataState dataState = dialogTab.getDataState();
    checkButton(dataState);
  }

  public void setSelectedTab(final int index) {
    this.tabbedPanel.setSelectedIndex(index);
  }

  protected void setSelectedTab(final IDialogTab tab) {
    this.tabbedPanel.setSelectedComponent(tab.getComponent());
  }

}
