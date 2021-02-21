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

import static net.anwiba.testing.demo.JDialogs.show;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.icons.GuiIcons;

public class MessageDialogDemo {

  @Test
  public void demoDefault() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create(
            "Information Label",
            "Description Text",
            MessageType.DEFAULT),
        null,
        DialogType.CLOSE));
  }

  @Test
  public void demoDefaultWithIcon() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text", MessageType.DEFAULT), //$NON-NLS-1$//$NON-NLS-2$
        GuiIcons.GLOBE_ICON.getLargeIcon(),
        DialogType.CLOSE));
  }

  @Test
  public void demoInfo() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text", MessageType.INFO), //$NON-NLS-1$//$NON-NLS-2$
        null,
        DialogType.CLOSE));
  }

  @Test
  public void demoInfoWithLongDescription() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", //$NON-NLS-1$
            "Description Text, mit ganz lange Text. Eigentlich nur ein Text der �ber mehrere Spalten gehen soll.", //$NON-NLS-1$
            MessageType.INFO),
        null,
        DialogType.CLOSE));
  }

  @Test
  public void demoInfoWithEmptyDescription() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", null, MessageType.INFO), //$NON-NLS-1$
        null,
        DialogType.CLOSE));
  }

  @Test
  public void demoWarning() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text", MessageType.WARNING), //$NON-NLS-1$//$NON-NLS-2$
        null,
        DialogType.CLOSE));
  }

  @Test
  public void demoError() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text", MessageType.ERROR), //$NON-NLS-1$//$NON-NLS-2$
        null,
        DialogType.CLOSE));
  }

  @Test
  public void demoCancelOk() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text"), //$NON-NLS-1$//$NON-NLS-2$
        null,
        DialogType.CANCEL_OK));
  }

  @Test
  public void demoCancelApplyOk() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Information Label", "Description Text"), //$NON-NLS-1$//$NON-NLS-2$
        null,
        DialogType.CANCEL_APPLY_OK));
  }

  @Test
  public void demoQuery() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Query Label", "Description Text", MessageType.QUERY), //$NON-NLS-1$//$NON-NLS-2$
        null,
        DialogType.YES_NO));
  }

  @Test
  public void demoDetails() {
    show(frame -> new MessageDialog(frame,
        "Title", //$NON-NLS-1$
        Message.create("Query Label", "Description Text", MessageType.QUERY), //$NON-NLS-1$//$NON-NLS-2$
        null,
        DialogType.CLOSE_DETIALS));
  }
}
