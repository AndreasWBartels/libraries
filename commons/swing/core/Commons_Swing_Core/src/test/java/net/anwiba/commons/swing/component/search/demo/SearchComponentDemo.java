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
package net.anwiba.commons.swing.component.search.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.Caret;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.component.search.ISearchEngine;
import net.anwiba.commons.swing.component.search.SearchComponent;
import net.anwiba.commons.swing.component.search.text.DocumentSearchEngine;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.string.IStringPart;
import net.anwiba.commons.utilities.string.StringUtilities;

@RunWith(DemoAsTestRunner.class)
@SuppressWarnings("nls")
public class SearchComponentDemo extends SwingDemoCase {

  @SuppressWarnings("serial")
  public static final class ActivatSearchAction extends AbstractAction {
    private final SearchComponent<String, IStringPart> searchComponent;
    private final JPanel contentPane;

    public ActivatSearchAction(
        final String name,
        final Icon icon,
        final SearchComponent<String, IStringPart> searchComponent,
        final JPanel contentPane) {
      super(name, icon);
      this.searchComponent = searchComponent;
      this.contentPane = contentPane;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      final JComponent component = this.searchComponent.getComponent();
      if (component.getParent() != null) {
        return;
      }
      final JPanel contentPane = this.contentPane;
      GuiUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          contentPane.add(component, BorderLayout.SOUTH);
          component.grabFocus();
          contentPane.validate();
        }
      });
    }
  }

  public static final class ResultMarker implements IChangeableObjectListener {
    private final ISearchEngine<String, IStringPart> engine;
    private final Caret caret;

    public ResultMarker(final ISearchEngine<String, IStringPart> engine, final Caret caret) {
      this.engine = engine;
      this.caret = caret;
    }

    @Override
    public void objectChanged() {
      final IStringPart part = this.engine.getResultCursorModel().get();
      if (part == null) {
        return;
      }
      GuiUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          ResultMarker.this.caret.setVisible(true);
          ResultMarker.this.caret.setSelectionVisible(true);
          ResultMarker.this.caret.setDot(part.getPosition());
          ResultMarker.this.caret.moveDot(part.getPosition() + part.getLength());
        }
      });
    }
  }

  public static final class StringConditonFactory implements IFactory<String, String, RuntimeException> {
    @Override
    public String create(final String string) {
      return StringUtilities.isNullOrEmpty(string) ? null : string;
    }
  }

  public static final class ComponentRemover implements IProcedure<Component, RuntimeException> {
    private final JPanel contentPane;

    public ComponentRemover(final JPanel contentPane) {
      this.contentPane = contentPane;
    }

    @Override
    public void execute(final Component component) {
      if (component == null || component.getParent() == null) {
        return;
      }
      final JPanel contentPane = this.contentPane;
      GuiUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          contentPane.remove(component);
          contentPane.validate();
        }
      });
    }
  }

  @Demo
  public void demo() {
    final JPanel contentPane = new JPanel(new BorderLayout());
    final String text = getText();
    final JTextArea textArea = new JTextArea(text);
    textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);
    textArea.setCaretColor(Color.BLACK);
    final Caret caret = textArea.getCaret();
    caret.setVisible(true);
    caret.setSelectionVisible(true);

    final JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));
    contentPane.add(scrollPane, BorderLayout.CENTER);
    final ISearchEngine<String, IStringPart> engine = new DocumentSearchEngine(textArea.getDocument());
    final IFactory<String, String, RuntimeException> stringToConditionFactory = new StringConditonFactory();

    final SearchComponent<String, IStringPart> searchComponent = new SearchComponent<>(
        new BooleanModel(true),
        (validationResult, context) -> "search condition",
        engine,
        stringToConditionFactory);
    final JToolBar toolbar = new JToolBar();
    toolbar.add(new ActivatSearchAction(null, GuiIcons.SEARCH_ICON.getSmallIcon(), searchComponent, contentPane));
    engine.getResultCursorModel().addChangeListener(new ResultMarker(engine, caret));

    contentPane.add(toolbar, BorderLayout.NORTH);
    show(contentPane);
  }

  private String getText() {
    return "Warburg [ˈvaː.buɐk] (niederdeutsch: Warb(e)rich: lateinisch: Warburgum oder Varburgum) "
        + "ist eine Stadt im ostwestfälischen Kreis Höxter im Osten Nordrhein-Westfalens (Deutschland). "
        + "Es ist ein Mittelzentrum und mit rund 24.000 Einwohnern die größte Stadt der Warburger Börde. "
        + "Die Stadt wurde um 1010 erstmals schriftlich erwähnt, die erste Nennung der Stadt als "
        + "geschlossene Ortschaft stammt aus dem Jahr 1036. Zudem gehörte Warburg zur westfälischen Hanse. "
        + "Das Stadtbild ist durch die historischen Bauten, Stein- und Fachwerkhäuser, und die Lage auf "
        + "einem Bergrücken geprägt. Warburg wird auch als Rothenburg Westfalens bezeichnet."
        + "\n"
        + "Warburg liegt im Osten des Landes Nordrhein-Westfalen und im Süden des Kreises Höxter sowie "
        + "etwa 27 Kilometer südwestlich des Dreiländerecks Hessen–Niedersachsen–Nordrhein-Westfalen. "
        + "Im Süden grenzt das Stadtgebiet an das Land Hessen. Naturräumlich liegt Warburg im Süden der "
        + "Warburger Börde, einer fruchtbaren Niederung mit fruchtbarem Lößboden und geringen Baumbeständen. "
        + "Die Börde wird überwiegend landwirtschaftlich genutzt. Im Südwesten berührt das Stadtgebiet "
        + "die Ausläufer des Sauerlandes, im Nordwesten die des Eggegebirges. Im Norden und Nordosten schließt "
        + "sich die eigentliche Warburger Börde an, südlich das Westhessische Bergland. Die nächstgelegenen "
        + "Oberzentren sind Paderborn (nordwestlich: 27 km Landstraße, 40 km Autobahn) und Kassel "
        + "(südöstlich: 35 km Landstraße/Autobahn) und Göttingen (östlich: 76 km Landstraße, 98 km Autobahn)."; //$NON-NLS-1$
  }

}