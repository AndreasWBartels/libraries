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
package net.anwiba.commons.swing.icon.demo;

import net.anwiba.commons.swing.icon.DecorationPosition;
import net.anwiba.commons.swing.icon.GuiIconDecorator;
import net.anwiba.commons.swing.icon.GuiIconSize;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.ui.GuiIconDecoration;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

@RunWith(DemoAsTestRunner.class)
public class GuiIconDecoratorDemo extends SwingDemoCase {

  @Demo
  public void demoIconSmall() {
    show(GuiIconDecorator.decorate(GuiIconSize.SMALL, GuiIcons.COLORIZE_ICON, GuiIcons.ERROR_ICON));
  }

  @Demo
  public void demoSmall() {
    show(GuiIconDecorator.decorate(GuiIconSize.SMALL, GuiIcons.COLORIZE_ICON, GuiIconDecoration.WARNING));
  }

  @Demo
  public void demoIconMedium() {
    show(GuiIconDecorator.decorate(GuiIconSize.MEDIUM, GuiIcons.COLORIZE_ICON, GuiIcons.ERROR_ICON));
  }

  @Demo
  public void demoMedium() {
    show(GuiIconDecorator.decorate(GuiIconSize.MEDIUM, GuiIcons.COLORIZE_ICON, GuiIconDecoration.WARNING));
  }

  @Demo
  public void demoIconLargeLowerLeft() {
    show(GuiIconDecorator.decorate(
        GuiIconSize.LARGE,
        DecorationPosition.LowerLeft,
        GuiIcons.COLORIZE_ICON,
        GuiIcons.ERROR_ICON));
  }

  @Demo
  public void demoIconLargeLowerRight() {
    show(GuiIconDecorator.decorate(
        GuiIconSize.LARGE,
        DecorationPosition.LowerRight,
        GuiIcons.COLORIZE_ICON,
        GuiIcons.ERROR_ICON));
  }

  @Demo
  public void demoIconLargeUpperLeft() {
    show(GuiIconDecorator.decorate(
        GuiIconSize.LARGE,
        DecorationPosition.UpperLeft,
        GuiIcons.COLORIZE_ICON,
        GuiIcons.ERROR_ICON));
  }

  @Demo
  public void demoIconLargeUpperRight() {
    show(GuiIconDecorator.decorate(
        GuiIconSize.LARGE,
        DecorationPosition.UpperRight,
        GuiIcons.COLORIZE_ICON,
        GuiIcons.ERROR_ICON));
  }

  @Demo
  public void demoLarge() {
    show(GuiIconDecorator.decorate(GuiIconSize.LARGE, GuiIcons.COLORIZE_ICON, GuiIconDecoration.WARNING));
  }
}
