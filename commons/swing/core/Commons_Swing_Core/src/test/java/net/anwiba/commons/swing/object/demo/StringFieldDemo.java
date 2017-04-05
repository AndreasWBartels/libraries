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

import javax.swing.JPanel;

import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.IToolTipFactory;
import net.anwiba.commons.swing.object.StringField;
import net.anwiba.commons.swing.object.StringObjectFieldConfigurationBuilder;
import net.anwiba.commons.utilities.validation.IValidationResult;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.junit.DemoAsTestRunner;

@RunWith(DemoAsTestRunner.class)
public class StringFieldDemo extends AbstractObjectFieldDemo {

  @Demo
  public void demoStringField() {
    final IObjectField<?> field = new StringField();
    final JPanel panel = createPanel(field);
    show(panel);
  }

  @Demo
  public void demoToolTippedStringField() {
    final StringObjectFieldConfigurationBuilder builder = new StringObjectFieldConfigurationBuilder();
    builder.setToolTipFactory(new IToolTipFactory() {

      @Override
      public String create(final IValidationResult validationResult, final String context) {
        return context;
      }
    });
    final IObjectField<?> field = new StringField(builder.build());
    final JPanel panel = createPanel(field);
    show(panel);
  }

  @Demo
  public void demoPreSetStringField() {
    final StringField field = new StringField();
    final JPanel panel = createPanel(field);
    field.getModel().set("value"); //$NON-NLS-1$
    show(panel);
  }
}