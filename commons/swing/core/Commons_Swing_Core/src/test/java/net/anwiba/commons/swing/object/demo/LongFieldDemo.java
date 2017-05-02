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
package net.anwiba.commons.swing.object.demo;

import de.jdemo.annotation.Demo;
import de.jdemo.junit.DemoAsTestRunner;

import javax.swing.JPanel;

import net.anwiba.commons.swing.object.LongField;

import org.junit.runner.RunWith;

@RunWith(DemoAsTestRunner.class)
public class LongFieldDemo extends AbstractObjectFieldDemo {

  @Demo
  public void demoLongField() {
    final LongField field = new LongField();
    final JPanel panel = createPanel(field);
    show(panel);
  }

  @Demo
  public void demoPreSetLongField() {
    final LongField field = new LongField();
    final JPanel panel = createPanel(field);
    field.getModel().set(Long.valueOf(10));
    show(panel);
  }

}