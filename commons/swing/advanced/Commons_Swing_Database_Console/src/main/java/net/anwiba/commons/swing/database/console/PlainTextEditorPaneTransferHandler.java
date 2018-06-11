/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.swing.database.console;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.reference.utilities.IoUtilities;

public final class PlainTextEditorPaneTransferHandler extends TransferHandler {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(PlainTextEditorPaneTransferHandler.class);
  private final PlainDocument document;
  private final JEditorPane editorPane;
  private static final long serialVersionUID = 1L;
  final DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();

  public PlainTextEditorPaneTransferHandler(final PlainDocument document, final JEditorPane editorPane) {
    this.document = document;
    this.editorPane = editorPane;
  }

  @Override
  public boolean canImport(final JComponent comp, final DataFlavor[] transferFlavors) {
    for (final DataFlavor flavor : transferFlavors) {
      if (DataFlavor.stringFlavor.equals(flavor)) {
        return true;
      }
      if (this.textPlainUnicodeFlavor.equals(flavor)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean importData(final JComponent comp, final Transferable transferable) {
    final int start = this.editorPane.getSelectionStart();
    final int end = this.editorPane.getSelectionEnd();
    if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        final String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
        insertEditorContent(data, start, end);
      } catch (UnsupportedFlavorException | IOException exception) {
        return false;
      }
      return true;
    }
    if (transferable.isDataFlavorSupported(this.textPlainUnicodeFlavor)) {
      try (final InputStream data = (InputStream) transferable.getTransferData(this.textPlainUnicodeFlavor);) {
        final String string = IoUtilities.toString(data, this.textPlainUnicodeFlavor.getParameter("charset")); //$NON-NLS-1$
        insertEditorContent(string, start, end);
      } catch (UnsupportedFlavorException | IOException exception) {
        return false;
      }
      return true;
    }
    return false;
  }

  private void insertEditorContent(final String string, final int start, final int end) {
    try {
      if (start == end) {
        this.document.insertString(start, string, null);
      } else {
        this.document.replace(start, end - start, string, null);
      }
    } catch (final BadLocationException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
    }
  }
}
