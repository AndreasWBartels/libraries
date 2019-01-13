/*
 * #%L
 *
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
package net.anwiba.commons.thread.program;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.cancel.ICancelerListener;
import net.anwiba.commons.utilities.OperationSystemUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class ProgramLauncher implements IProgramLauncher {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ProgramLauncher.class.getName());

  private IConsumer<InputStream, IOException> inputStreamConsumer = null;
  private IConsumer<InputStream, IOException> errorStreamConsumer = null;
  private boolean isWaitFor = false;
  private String command = null;
  private final List<String> arguments = new ArrayList<>();

  public ProgramLauncher() {
    super();
  }

  @Override
  public IProgramLauncher waitNot() {
    this.isWaitFor = false;
    return this;
  }

  @Override
  public IProgramLauncher waitFor() {
    this.isWaitFor = true;
    return this;
  }

  @Override
  public IProgramLauncher streamToLogger() {
    inputStreamConsumer(createStreamConsumer(ILevel.DEBUG));
    errorStreamConsumer(createStreamConsumer(ILevel.ERROR));
    return this;
  }

  private IConsumer<InputStream, IOException> createStreamConsumer(final ILevel level) {
    return input -> {
      StringBuffer buffer = new StringBuffer();
      try {
        int value = 0;
        while ((value = input.read()) > 0) {
          switch (value) {
            case '\n': {
              logger.log(level, buffer.toString());
              buffer = new StringBuffer();
              break;
            }
            default: {
              buffer.append((char) value);
              break;
            }
          }
        }
        logger.log(level, buffer.toString());
      } catch (final IOException exception1) {
        logger.log(level, buffer.toString());
        logger.log(ILevel.DEBUG, exception1.getMessage(), exception1);
      } finally {
        try {
          input.close();
        } catch (final IOException exception) {
          logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        }
      }
    };
  }

  @Override
  @SuppressWarnings("hiding")
  public IProgramLauncher command(final String command) {
    this.command = command;
    return this;
  }

  @Override
  public IProgramLauncher argument(final String argument) {
    this.arguments.add(argument);
    return this;
  }

  @Override
  @SuppressWarnings("hiding")
  public IProgramLauncher errorStreamConsumer(final IConsumer<InputStream, IOException> errorStreamConsumer) {
    if (errorStreamConsumer == null) {
      return this;
    }
    this.errorStreamConsumer = errorStreamConsumer;
    return this;
  }

  @Override
  @SuppressWarnings("hiding")
  public IProgramLauncher inputStreamConsumer(final IConsumer<InputStream, IOException> inputStreamConsumer) {
    if (inputStreamConsumer == null) {
      return this;
    }
    this.inputStreamConsumer = inputStreamConsumer;
    return this;
  }

  @Override
  public Process launch() throws IOException, CanceledException {
    return launch(Canceler.DummyCanceler);
  }

  @Override
  public Process launch(final ICanceler canceler) throws IOException, CanceledException {
    @SuppressWarnings("hiding")
    final List<String> command = new ArrayList<>();
    if (OperationSystemUtilities.isLinux() || OperationSystemUtilities.isBSD() || OperationSystemUtilities.isUnix()) {
      command.add("sh"); //$NON-NLS-1$
      command.add("-c"); //$NON-NLS-1$
    } else if (OperationSystemUtilities.isWindows()) {
      command.add("cmd"); //$NON-NLS-1$
      command.add("/c"); //$NON-NLS-1$
    }
    final String commandString = create(this.command, this.arguments);
    Optional.of(commandString).consume(c -> command.add(c));
    final ProcessBuilder processBuilder = new ProcessBuilder(command);
    Optional.of(this.inputStreamConsumer).consume(consumer -> processBuilder.redirectError(Redirect.PIPE));
    Optional.of(this.errorStreamConsumer).consume(consumer -> processBuilder.redirectInput(Redirect.PIPE));
    final Process process = processBuilder.start();
    Optional.of(IOException.class, this.inputStreamConsumer).consume(
        consumer -> consumer.consume(process.getInputStream()));
    Optional.<IConsumer<InputStream, IOException>, IOException> of(IOException.class, this.errorStreamConsumer).consume(
        consumer -> consumer.consume(process.getErrorStream()));
    if (this.isWaitFor) {
      final ICancelerListener listener = () -> process.destroy();
      try {
        canceler.addCancelerListener(listener);
        process.waitFor();
      } catch (final InterruptedException exception) {
        throw new CanceledException();
      } finally {
        canceler.removeCancelerListener(listener);
      }
    }
    return process;
  }

  @SuppressWarnings("hiding")
  private String create(final String command, final List<String> arguments) {
    if (StringUtilities.isNullOrEmpty(command) && arguments.isEmpty()) {
      return null;
    }
    final StringBuilder builder = new StringBuilder();
    Optional.of(command).consume(c -> {
      builder.append(c);
      builder.append(" "); //$NON-NLS-1$
    });
    boolean flag = false;
    for (final String argument : arguments) {
      if (flag) {
        builder.append(" "); //$NON-NLS-1$
      }
      builder.append(argument);
      flag = true;
    }
    return builder.toString();
  }
}
