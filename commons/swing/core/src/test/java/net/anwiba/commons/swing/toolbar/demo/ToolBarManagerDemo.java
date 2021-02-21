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
package net.anwiba.commons.swing.toolbar.demo;

import static net.anwiba.testing.demo.JFrames.show;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.swing.action.JComboBoxActionProvider;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.toolbar.ToolBarDescription;
import net.anwiba.commons.swing.toolbar.ToolBarItemConfiguration;
import net.anwiba.commons.swing.toolbar.ToolBarItemDescription;
import net.anwiba.commons.swing.toolbar.ToolBarItemGroupDescription;
import net.anwiba.commons.swing.toolbar.ToolBarManager;

public class ToolBarManagerDemo {

  public static final class NullActionListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      // nothing to do
    }
  }

  public final static class EmptyAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public EmptyAction(final String name, final Icon icon) {
      super(name, icon);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      // nothing to do
    }
  }

  @Test
  public void demo() {
    final ToolBarManager toolBarManager = new ToolBarManager();
    final ToolBarDescription toolBarDescription0 = new ToolBarDescription("toolbar0", 4, true); //$NON-NLS-1$
    final ToolBarItemGroupDescription toolBarGroupDescription = new ToolBarItemGroupDescription("toolbargroup", 1); //$NON-NLS-1$
    final ToolBarItemGroupDescription toolBarToggelGroupDescription = new ToolBarItemGroupDescription(
        "toolbargroup", //$NON-NLS-1$
        1,
        true);
    toolBarManager.add(
        new ToolBarItemConfiguration(
            new ToolBarItemDescription(toolBarDescription0, 2),
            new EmptyAction(null, GuiIcons.GLOBE_ICON.getSmallIcon())));
    toolBarManager.add(
        new ToolBarItemConfiguration(
            new ToolBarItemDescription(toolBarDescription0, 1),
            new EmptyAction(null, GuiIcons.COLORIZE_ICON.getSmallIcon())));
    toolBarManager.add(
        new ToolBarItemConfiguration(
            new ToolBarItemDescription(toolBarDescription0, toolBarGroupDescription, 1),
            new EmptyAction(null, GuiIcons.ERROR_ICON.getSmallIcon())));
    final ToolBarDescription toolBarDescription1 = new ToolBarDescription("toolbar1", 0, true); //$NON-NLS-1$
    toolBarManager.add(
        createToolBarItemConfiguration(
            toolBarToggelGroupDescription,
            toolBarDescription1,
            GuiIcons.GLOBE_ICON.getSmallIcon()));
    toolBarManager.add(
        createToolBarItemConfiguration(
            toolBarToggelGroupDescription,
            toolBarDescription1,
            GuiIcons.COLORIZE_ICON.getSmallIcon()));
    toolBarManager.add(
        createToolBarItemConfiguration(
            toolBarToggelGroupDescription,
            toolBarDescription1,
            GuiIcons.INFORMATION_ICON.getSmallIcon()));
    final ToolBarItemConfiguration toolBarItemConfiguration = getToolBarItemConfiguration();
    toolBarManager.add(toolBarItemConfiguration);
    final JToolBar[] toolBars = toolBarManager.getJToolBars();
    final JComponent container = new JPanel();
    for (final JToolBar toolBar : toolBars) {
      container.add(toolBar);
    }
    show(container);
  }

  private ToolBarItemConfiguration createToolBarItemConfiguration(
      final ToolBarItemGroupDescription toolBarToggelGroupDescription,
      final ToolBarDescription toolBarDescription1,
      final ImageIcon smallIcon) {
    return new ToolBarItemConfiguration(
        new ToolBarItemDescription(toolBarDescription1, toolBarToggelGroupDescription, 4),
        new EmptyAction(null, smallIcon));
  }

  private ToolBarItemConfiguration getToolBarItemConfiguration() {
    final AbstractAction action = new EmptyAction(null, GuiIcons.INFORMATION_ICON.getSmallIcon());
    final ComboBoxModel<Double> comboBoxModel = new DefaultComboBoxModel<>(
        new Double[] {
            Double.valueOf(1.1),
            Double.valueOf(1.25),
            Double.valueOf(1.5),
            Double.valueOf(1.75),
            Double.valueOf(2.0) });
    comboBoxModel.setSelectedItem(Double.valueOf(1.5));
    final JComboBoxActionProvider<Double> actionContainerProvider = new JComboBoxActionProvider<>(
        comboBoxModel,
        action,
        "Test"); //$NON-NLS-1$
    final JComboBox<Double> comboBox = actionContainerProvider.getContainer();
    comboBox.addActionListener(new NullActionListener());
    final ToolBarDescription toolBarDescription2 = new ToolBarDescription("toolbar2", 2, false); //$NON-NLS-1$
    final ToolBarItemConfiguration toolBarItemConfiguration = new ToolBarItemConfiguration(
        new ToolBarItemDescription(toolBarDescription2, 4),
        actionContainerProvider,
        action);
    return toolBarItemConfiguration;
  }
}
