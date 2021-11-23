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
package net.anwiba.commons.swing.action;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.dialog.progress.ProgressDialogUtilities;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class ActionProcedurBuilder<I, O> implements IActionProcedurBuilder<I, O> {

  public static final class ActionProcedur<I, O> implements IActionProcedure {
    private final String title;
    private final IActionConsumer<O> consumer;
    private final IActionInitializer<I> initializer;
    private final String description;
    private final IActionTask<I, O> task;
    private final String errorMessage;

    public ActionProcedur(
        final String title,
        final String descrition,
        final String errorMessage,
        final IActionInitializer<I> initializer,
        final IActionTask<I, O> task,
        final IActionConsumer<O> consumer) {
      this.title = title;
      this.errorMessage = errorMessage;
      this.consumer = consumer;
      this.initializer = initializer;
      this.description = descrition;
      this.task = task;
    }

    @Override
    public void execute(final Component component) throws RuntimeException {
      final Window owner = GuiUtilities.getParentWindow(component);
      try {
        final I value = Optional
            .of(InvocationTargetException.class, this.initializer)
            .convert(i -> i.initialize(component))
            .get();
        @SuppressWarnings("unchecked")
        final O result = this.task == null
            ? (O) value
            : ProgressDialogUtilities
                .setTask((progressMonitor, canceler) -> this.task.excecute(progressMonitor, canceler, value))
                .setTitle(this.title)
                .setDescription(this.description)
                .launch(owner);
        Optional.of(InvocationTargetException.class, this.consumer).consume(c -> c.consume(component, result));
      } catch (final InvocationTargetException exception) {
        final Throwable throwable = exception.getCause();
        logger.log(ILevel.DEBUG, throwable.getMessage(), throwable);
        MessageDialog.launcher()
            .title(this.title)
            .description(this.errorMessage)
            .text(throwable.getMessage())
            .throwable(throwable)
            .error()
            .launch(owner);
      } catch (final RuntimeException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        MessageDialog.launcher()
            .title(this.title)
            .description(this.errorMessage)
            .text(exception.getMessage())
            .throwable(exception)
            .error()
            .launch(owner);
      } catch (final CanceledException exception) {
        // nothing to do
      }
    }
  }

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ActionProcedurBuilder.class.getName());

  private IActionInitializer<I> initializer;
  private IActionTask<I, O> task;
  private IActionConsumer<O> consumer;
  private String title;
  private String description;

  private String errorMessage;

  @Override
  public IActionProcedurBuilder<I, O> setTitle(final String title) {
    this.title = title;
    return this;
  }

  @Override
  public IActionProcedurBuilder<I, O> setDescrition(final String description) {
    this.description = description;
    return this;
  }

  @Override
  public IActionProcedurBuilder<I, O> setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  @Override
  public IActionProcedurBuilder<I, O> setInitializer(final IActionInitializer<I> initializer) {
    this.initializer = initializer;
    return this;
  }

  @Override
  public IActionProcedurBuilder<I, O> setTask(final IActionTask<I, O> task) {
    this.task = task;
    return this;
  }

  @Override
  public IActionProcedurBuilder<I, O> setConsumer(final IActionConsumer<O> consumer) {
    this.consumer = consumer;
    return this;
  }

  @Override
  @SuppressWarnings("hiding")
  public IActionProcedure build() {
    final String descrition = StringUtilities.isNullOrTrimmedEmpty(this.description) ? this.title : this.description;
    final String errorMessage = StringUtilities.isNullOrTrimmedEmpty(this.errorMessage) ? //
        this.description + ", faild" : this.errorMessage; //$NON-NLS-1$
    return new ActionProcedur<>(
        this.title, //
        descrition,
        errorMessage,
        this.initializer,
        this.task,
        this.consumer);
  }
}
