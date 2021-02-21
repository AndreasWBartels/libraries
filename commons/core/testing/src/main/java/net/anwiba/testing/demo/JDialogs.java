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
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class JDialogs {

  public static class Viewer<T extends JDialog> {

    private Duration timeout = DemoUtilities.DEFAULT_TIMEOUT_MILLIS;
    private final Supplier<JFrame> jFrameSupplier = () -> DemoUtilities.createJFrame();
    private Consumer<T> afterOpenedTask = f -> DemoUtilities.pause();
    private final Function<JFrame, T> dialogFactory;

    Viewer(final Function<JFrame, T> dialogFactory) {
      assertNotNull(dialogFactory, "The dialog factory must not be null.");
      this.dialogFactory = dialogFactory;
    }

    public Viewer<T> setTimeout(final Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public Viewer<T> setAfterOpenedTask(final Consumer<T> afterOpenedTask) {
      this.afterOpenedTask = afterOpenedTask;
      return this;
    }

    public void show() {
      show(new Configuration<T>(this.timeout, this.dialogFactory, this.jFrameSupplier, this.afterOpenedTask));
    }

    private void show(final Configuration<T> configuration) {
      final Function<Configuration<T>, T> windowFactory = c -> {
        JFrame frame = c.getJFrameSupplier().get();
        frame.getContentPane().setLayout(new BorderLayout());
        DemoUtilities.centerOnScreen(frame);
        return c.getDialogFactory().apply(frame);
      };
      final Supplier<T> windowSupplier = () -> windowFactory.apply(configuration);
      Optional.ofNullable(configuration.getTimeout())
          .ifPresentOrElse(t -> assertTimeoutPreemptively(t, () -> {
            DemoUtilities.showAndDispose(windowSupplier, configuration.getAfterOpenedTask());
          }), () -> {
            DemoUtilities.showAndDispose(windowSupplier, configuration.getAfterOpenedTask());
          });
    }

  }

  private static class Configuration<T extends JDialog> {

    private final Duration timeout;
    private final Supplier<JFrame> jFrameSupplier;
    private final Consumer<T> afterOpenedTask;
    private final Function<JFrame, T> dialogFactory;

    public Configuration(final Duration timeout,
        final Function<JFrame, T> dialogFactory,
        final Supplier<JFrame> jFrameSupplier,
        final Consumer<T> afterOpenedTask) {
      super();
      this.timeout = timeout;
      this.dialogFactory = dialogFactory;
      this.jFrameSupplier = jFrameSupplier;
      this.afterOpenedTask = afterOpenedTask;
    }

    public Duration getTimeout() {
      return this.timeout;
    }

    public Consumer<T> getAfterOpenedTask() {
      return this.afterOpenedTask;
    }

    public Supplier<JFrame> getJFrameSupplier() {
      return this.jFrameSupplier;
    }

    public Function<JFrame, T> getDialogFactory() {
      return this.dialogFactory;
    }
  }

  public static <T extends JDialog> void show(final Function<JFrame, T> dialogFactory) {
    viewer(dialogFactory).show();
  }

  public static <T extends JDialog> void show(final Duration timeout,
      final Function<JFrame, T> dialogFactory) {
    viewer(dialogFactory).setTimeout(timeout).show();
  }

  public static <T extends JDialog> void show(final Function<JFrame, T> dialogFactory,
      final Consumer<T> afterOpenedTask) {
    viewer(dialogFactory)
        .setAfterOpenedTask(afterOpenedTask)
        .show();
  }

  public static <T extends JDialog> Viewer<T> viewer(final Function<JFrame, T> dialogFactory) {
    return new Viewer<T>(dialogFactory);
  }
}
