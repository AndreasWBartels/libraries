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
package net.anwiba.commons.swing.dialog.demo;

import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.icons.GuiIcons;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

@RunWith(DemoAsTestRunner.class)
public class MessageDialogDemo extends SwingDemoCase {

  @Demo
  public void demoDefault() {
    show(new MessageDialog(createJFrame(), "Title", Message.create( //$NON-NLS-1$
        "Information Label", //$NON-NLS-1$
        "Description Text", MessageType.DEFAULT), null, DialogType.CLOSE)); //$NON-NLS-1$
  }

  @Demo
  public void demoDefaultWithIcon() {
    show(new MessageDialog(createJFrame(), "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text", MessageType.DEFAULT), //$NON-NLS-1$//$NON-NLS-2$
        GuiIcons.GLOBE_ICON.getLargeIcon(),
        DialogType.CLOSE));
  }

  @Demo
  public void demoInfo() {
    show(new MessageDialog(createJFrame(), "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text", MessageType.INFO), null, DialogType.CLOSE)); //$NON-NLS-1$//$NON-NLS-2$
  }

  @Demo
  public void demoInfoWithLongDescription() {
    show(new MessageDialog(createJFrame(), "Title", //$NON-NLS-1$
        Message.create("Information Label", //$NON-NLS-1$
            "Description Text, mit ganz lange Text. Eigentlich nur ein Text der �ber mehrere Spalten gehen soll.", //$NON-NLS-1$
            MessageType.INFO),
        null,
        DialogType.CLOSE));
  }

  @Demo
  public void demoInfoWithEmptyDescription() {
    show(new MessageDialog(createJFrame(), "Title", //$NON-NLS-1$
        Message.create("Information Label", null, MessageType.INFO), null, DialogType.CLOSE)); //$NON-NLS-1$
  }

  @Demo
  public void demoWarning() {
    show(new MessageDialog(
        createJFrame(),
        "Title", Message.create("Information Label", "Description Text", MessageType.WARNING), null, DialogType.CLOSE)); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
  }

  @Demo
  public void demoError() {
    show(new MessageDialog(
        createJFrame(),
        "Title", Message.create("Information Label", "Description Text", MessageType.ERROR), null, DialogType.CLOSE)); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
  }

  @Demo
  public void demoCancelOk() {
    show(new MessageDialog(
        createJFrame(),
        "Title", Message.create("Information Label", "Description Text"), null, DialogType.CANCEL_OK)); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
  }

  @Demo
  public void demoCancelApplyOk() {
    show(new MessageDialog(
        createJFrame(),
        "Title", Message.create("Information Label", "Description Text"), null, DialogType.CANCEL_APPLY_OK)); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
  }

  @Demo
  public void demoQuery() {
    show(new MessageDialog(createJFrame(), "Title", //$NON-NLS-1$
        Message.create("Query Label", "Description Text", MessageType.QUERY), null, DialogType.YES_NO)); //$NON-NLS-1$//$NON-NLS-2$
  }

  @Demo
  public void demoDetails() {
    show(new MessageDialog(createJFrame(), "Title", //$NON-NLS-1$
        Message.create("Query Label", "Description Text", MessageType.QUERY), null, DialogType.CLOSE_DETIALS)); //$NON-NLS-1$//$NON-NLS-2$
  }
}
