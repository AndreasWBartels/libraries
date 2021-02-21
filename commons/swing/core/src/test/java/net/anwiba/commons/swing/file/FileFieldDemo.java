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
package net.anwiba.commons.swing.file;

import static net.anwiba.testing.demo.JFrames.show;

import java.io.File;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.swing.filechooser.FileField;
import net.anwiba.commons.swing.filechooser.FileFieldConfigurationBuilder;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.IObjectFieldConfiguration;
import net.anwiba.commons.swing.object.demo.AbstractObjectFieldDemo;

public class FileFieldDemo extends AbstractObjectFieldDemo {

  @Test
  public void demoSimlpeFileField() {
    final IObjectField<?> field = new FileField();
    final JPanel panel = createPanel(field);
    show(panel);
  }

  @Test
  public void demoFileOpenField() {
    final FileFieldConfigurationBuilder builder = new FileFieldConfigurationBuilder();
    final IObjectFieldConfiguration<File> configuration = builder
        .addFileOpenChooserAction(null)
        .addClearAction(null)
        .setFileValidator()
        .build();
    final IObjectField<?> field = new FileField(configuration);
    final JPanel panel = createPanel(field);
    show(panel);
  }

}