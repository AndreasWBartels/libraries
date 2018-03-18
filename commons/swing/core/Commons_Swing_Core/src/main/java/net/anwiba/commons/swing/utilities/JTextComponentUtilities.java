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
package net.anwiba.commons.swing.utilities;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

public class JTextComponentUtilities {

  private static ILogger logger = Logging.getLogger(JTextComponentUtilities.class.getName());

  public static void setTextAndMoveToTop(final JTextComponent textPane, final String text) {
    final Document document = textPane.getDocument();
    if (document instanceof PlainDocument) {
      setTextToDocument((PlainDocument) document, text);
    } else {
      GuiUtilities.invokeLater(() -> textPane.setText(text));
    }
    GuiUtilities.invokeLater(() -> textPane.setCaretPosition(0));
  }

  public static void setTextToDocument(final PlainDocument document, final String text) {
    GuiUtilities.invokeLater(() -> {
      try {
        ((AbstractDocument) document).replace(0, document.getLength(), text, null);
      } catch (final BadLocationException exception) {
        logger.log(ILevel.FATAL, exception.getLocalizedMessage(), exception);
      }
    });
  }

  public static double getValueWidth(final Component component, final String value) {
    final Font font = component.getFont();
    final FontRenderContext fontRenderContext = component.getFontMetrics(font).getFontRenderContext();
    return font.getStringBounds(value, fontRenderContext).getWidth();
  }

  public static double getValueWidth(final Graphics graphics, final String value) {
    final Font font = graphics.getFont();
    final FontRenderContext fontRenderContext = graphics.getFontMetrics(font).getFontRenderContext();
    return font.getStringBounds(value, fontRenderContext).getWidth();
  }
}
