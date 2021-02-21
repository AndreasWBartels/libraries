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
package net.anwiba.commons.swing.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.anwiba.commons.swing.frame.view.IView;
import net.anwiba.commons.swing.frame.view.ViewManager;
import net.anwiba.commons.swing.menu.MenuManager;
import net.anwiba.commons.swing.statebar.StateBarManager;
import net.anwiba.commons.swing.toolbar.ToolBarManager;

@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame {

  private final JPanel contentPane = new JPanel();
  private final JPanel toolBar = new JPanel();
  private final JPanel viewPane = new JPanel();
  private final JPanel buttomPane = new JPanel();
  private final JPanel functionPane = new JPanel();
  private final JPanel stateBar = new JPanel();

  private final MenuManager menuManager;
  private final ToolBarManager toolBarManager;
  private final StateBarManager stateBarManager;

  private final ViewManager viewManager;
  private IView view;
  private final KeyActionManager keyActionManager;

  public ApplicationFrame(
      final MenuManager menuManager,
      final ToolBarManager toolBarManager,
      final ViewManager viewManager,
      final StateBarManager stateBarManager,
      final KeyActionManager keyActionManager) {
    this.menuManager = menuManager;
    this.toolBarManager = toolBarManager;
    this.viewManager = viewManager;
    this.stateBarManager = stateBarManager;
    this.keyActionManager = keyActionManager;
    this.contentPane.setLayout(new BorderLayout());
    this.toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    this.viewPane.setLayout(new GridLayout(1, 1));
    this.stateBar.setMinimumSize(new Dimension(28, 30));
    this.buttomPane.setLayout(new BorderLayout());
    this.stateBar.setLayout(new GridLayout(1, 1));
    this.stateBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    this.functionPane.setLayout(new GridLayout(1, 1));
    this.buttomPane.add(this.functionPane, BorderLayout.CENTER);
    this.buttomPane.add(this.stateBar, BorderLayout.SOUTH);
    this.contentPane.add(this.toolBar, BorderLayout.NORTH);
    this.contentPane.add(this.viewPane, BorderLayout.CENTER);
    this.contentPane.add(this.buttomPane, BorderLayout.SOUTH);
    setContentPane(this.contentPane);
  }

  public void initialize() {
    this.view = this.viewManager.getCurrentView();
    this.menuManager.add(this.view.getMenuItemConfigurations());
    resetMenuBar();
    this.toolBarManager.add(this.view.getToolBarItemConfigurations());
    resetToolBar();
    this.viewPane.add(this.view.getComponent());
    this.viewPane.revalidate();
    this.stateBarManager.add(this.view.getStateBarComponentConfigurations());
    resetStateBar();
    this.keyActionManager.add(this.view.getKeyActionConfigurations());
    for (final IKeyActionConfiguration configuration : this.keyActionManager.configurations()) {
      addKeyAction(configuration);
    }
  }

  public MenuManager getMenuManager() {
    return this.menuManager;
  }

  public ToolBarManager getToolBarManager() {
    return this.toolBarManager;
  }

  public ViewManager getViewManager() {
    return this.viewManager;
  }

  public StateBarManager getStateBarManager() {
    return this.stateBarManager;
  }

  public KeyActionManager getKeyActionManager() {
    return this.keyActionManager;
  }

  public void setFunctionPane(final Component component) {
    this.functionPane.removeAll();
    if (component == null) {
      return;
    }
    this.functionPane.add(component);
  }

  private void addKeyAction(final IKeyActionConfiguration configuration) {
    getLayeredPane()
        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(configuration.getKeyStroke(), configuration.getActionKey());
    getLayeredPane().getActionMap().put(configuration.getActionKey(), configuration.getAction());
  }

  private void resetStateBar() {
    this.stateBar.removeAll();
    this.stateBar.add(this.stateBarManager.getStateBar());
  }

  private synchronized void resetMenuBar() {
    setJMenuBar(this.menuManager.getMenuBar());
  }

  private synchronized void resetToolBar() {
    final JToolBar[] toolBars = this.toolBarManager.getJToolBars();
    this.toolBar.removeAll();
    for (@SuppressWarnings("hiding")
    final JToolBar toolBar : toolBars) {
      this.toolBar.add(toolBar);
    }
  }
}