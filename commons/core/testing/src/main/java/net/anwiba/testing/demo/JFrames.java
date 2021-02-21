/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.testing.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.awt.BorderLayout;
import java.awt.Font;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import net.anwiba.testing.demo.component.IconComponent;
import net.anwiba.testing.demo.component.SwingTextPanel;

public class JFrames {

  public static class Viewer {

    private Duration timeout = DemoUtilities.DEFAULT_TIMEOUT_MILLIS;
    private Supplier<JFrame> jFrameSupplier = () -> DemoUtilities.createJFrame();
    private Consumer<JFrame> guiBuilderTask = f -> {};
    private Consumer<JFrame> afterOpenedTask = f -> DemoUtilities.pause();

    Viewer() {
    }

    public Viewer setTimeout(final Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public Viewer setjFrameSupplier(final Supplier<JFrame> jFrameSupplier) {
      this.jFrameSupplier = jFrameSupplier;
      return this;
    }

    public Viewer setFrame(final JFrame frame) {
      assertNotNull(frame, "The frame to show must not be null.");
      this.jFrameSupplier = () -> {
        return frame;
      };
      return this;
    }

    public Viewer setGuiBuilderTask(final Consumer<JFrame> guiBuilderTask) {
      this.guiBuilderTask = guiBuilderTask;
      return this;
    }

    public Viewer setComponent(final JComponent component) {
      assertNotNull(component, "The component to show must not be null.");
      this.guiBuilderTask = frame -> {
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(component, BorderLayout.CENTER);
        frame.pack();
      };
      return this;
    }

    public Viewer setJMenuBar(final JMenuBar menuBar) {
      assertNotNull(menuBar, "The component to show must not be null.");
      this.guiBuilderTask = frame -> {
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setJMenuBar(menuBar);
        frame.pack();
      };
      return this;
    }

    public Viewer setIcon(final Icon icon) {
      assertNotNull(icon, "The icon to show must not be null.");
      setComponent(new IconComponent(icon));
      return this;
    }

    public Viewer setText(final CharSequence text) {
      assertNotNull(text, "The text to show must not be null.");
      setComponent(new SwingTextPanel(text).getContent());
      return this;
    }

    public Viewer setText(final CharSequence text, final Font font) {
      assertNotNull(text, "The text to show must not be null.");
      assertNotNull(font, "The font to show must not be null.");
      setComponent(new SwingTextPanel(text, font).getContent());
      return this;
    }

    public Viewer setAfterOpenedTask(final Consumer<JFrame> afterOpenedTask) {
      this.afterOpenedTask = afterOpenedTask;
      return this;
    }

    public void show() {
      show(new Configuration(this.timeout, this.jFrameSupplier, this.guiBuilderTask, this.afterOpenedTask));
    }

    private static void show(final Configuration configuration) {
      final Function<Configuration, JFrame> windowFactory = c -> {
        JFrame frame = c.getJFrameSupplier().get();
        c.getGuiBuilderTask().accept(frame);
        DemoUtilities.centerOnScreen(frame);
        return frame;
      };
      final Supplier<JFrame> windowSupplier = () -> windowFactory.apply(configuration);
      Optional.ofNullable(configuration.getTimeout())
          .ifPresentOrElse(t -> assertTimeoutPreemptively(t, () -> {
            DemoUtilities.showAndDispose(windowSupplier, configuration.getAfterOpenedTask());
          }), () -> {
            DemoUtilities.showAndDispose(windowSupplier, configuration.getAfterOpenedTask());
          });
    }
  }

  private static class Configuration {

    private final Duration timeout;
    private final Supplier<JFrame> jFrameSupplier;
    private final Consumer<JFrame> guiBuilderTask;
    private final Consumer<JFrame> afterOpenedTask;

    public Configuration(final Duration timeout,
        final Supplier<JFrame> jFrameSupplier,
        final Consumer<JFrame> guiBuilderTask,
        final Consumer<JFrame> afterOpenedTask) {
      super();
      this.timeout = timeout;
      this.jFrameSupplier = jFrameSupplier;
      this.afterOpenedTask = afterOpenedTask;
      this.guiBuilderTask = guiBuilderTask;
    }

    public Duration getTimeout() {
      return this.timeout;
    }

    public Consumer<JFrame> getGuiBuilderTask() {
      return this.guiBuilderTask;
    }

    public Consumer<JFrame> getAfterOpenedTask() {
      return this.afterOpenedTask;
    }

    public Supplier<JFrame> getJFrameSupplier() {
      return this.jFrameSupplier;
    }

  }

  public static void show(final Icon icon) {
    viewer().setIcon(icon).show();
  }

  public static void show(final JMenuBar menuBar) {
    viewer().setJMenuBar(menuBar).show();
  }

  public static void show(final JMenuBar menuBar, final Consumer<JFrame> afterOpenedTask) {
    viewer().setJMenuBar(menuBar).setAfterOpenedTask(afterOpenedTask).show();
  }

  public static void show(final Duration timeout, final JMenuBar menuBar, final Consumer<JFrame> afterOpenedTask) {
    viewer().setTimeout(timeout).setJMenuBar(menuBar).setAfterOpenedTask(afterOpenedTask).show();
  }

  public static void show(final JComponent component) {
    viewer().setComponent(component).show();
  }

  public static void show(final Duration timeout, final JComponent component) {
    viewer().setTimeout(timeout).setComponent(component).show();
  }

  public static void show(final JComponent component, final Consumer<JFrame> afterOpenedTask) {
    viewer().setComponent(component).setAfterOpenedTask(afterOpenedTask).show();
  }

  public static void show(final Duration timeout, final JComponent component, final Consumer<JFrame> afterOpenedTask) {
    viewer().setTimeout(timeout).setComponent(component).setAfterOpenedTask(afterOpenedTask).show();
  }

  public static void
      show(final Duration timeout, final Consumer<JFrame> guiBuilderTask, final Consumer<JFrame> afterOpenedTask) {
    viewer().setTimeout(timeout).setGuiBuilderTask(guiBuilderTask).setAfterOpenedTask(afterOpenedTask).show();
  }

  public static void show(final JFrame frame) {
    viewer().setFrame(frame).show();
  }

  public static void show(final Duration timeout, final JFrame frame) {
    viewer().setTimeout(timeout).setFrame(frame).show();
  }

  public static void show(final Duration timeout, final JFrame frame, final Consumer<JFrame> afterOpenedTask) {
    viewer().setTimeout(timeout).setFrame(frame).setAfterOpenedTask(afterOpenedTask).show();
  }

  public static Viewer viewer() {
    return new Viewer();
  }
}
