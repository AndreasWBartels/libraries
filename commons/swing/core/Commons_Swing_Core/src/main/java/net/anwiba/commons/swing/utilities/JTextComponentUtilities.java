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

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class JTextComponentUtilities {

  private static ILogger logger = Logging.getLogger(JTextComponentUtilities.class.getName());

  static final class SetTextRunnable implements Runnable {
    private final String text;
    private final JTextComponent textPane;

    SetTextRunnable(final String text, final JTextComponent textPane) {
      this.text = text;
      this.textPane = textPane;
    }

    @Override
    public void run() {
      this.textPane.setText(this.text);
      this.textPane.setCaretPosition(0);
    }
  }

  public static void setTextAndMoveToTop(final JTextComponent textPane, final String text) {
    GuiUtilities.invokeLater(new SetTextRunnable(text, textPane));
  }

  public static void setTextAndMoveToTop(final Document document, final String text) {
    GuiUtilities.invokeLater(new Runnable() {

      @SuppressWarnings("synthetic-access")
      @Override
      public void run() {
        try {
          if (document instanceof AbstractDocument) {
            ((AbstractDocument) document).replace(0, document.getLength(), text, null);
            return;
          }
          document.remove(0, document.getLength());
          document.insertString(0, text, null);
        } catch (final BadLocationException exception) {
          logger.log(ILevel.FATAL, exception.getLocalizedMessage(), exception);
        }
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
