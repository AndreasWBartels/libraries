/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.program;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.resource.utilities.IoUtilities;
import net.anwiba.commons.utilities.OperationSystemUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class ProgramLauncher {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ProgramLauncher.class.getName());

  private IConsumer<InputStream, RuntimeException> inputStreamConsumer = null;
  private IConsumer<InputStream, RuntimeException> errorStreamConsumer = null;
  private boolean isWaitFor = false;
  private String command = null;
  private final List<String> arguments = new ArrayList<>();

  public ProgramLauncher() {
    super();
  }

  public ProgramLauncher waitNot() {
    this.isWaitFor = false;
    return this;
  }

  public ProgramLauncher waitFor() {
    this.isWaitFor = true;
    return this;
  }

  public ProgramLauncher streamToLogger() {
    inputStreamConsumer(createStreamConsumer(ILevel.DEBUG));
    errorStreamConsumer(createStreamConsumer(ILevel.ERROR));
    return this;
  }

  private IConsumer<InputStream, RuntimeException> createStreamConsumer(final Level level) {
    return new IConsumer<InputStream, RuntimeException>() {

      @Override
      public void consume(final InputStream input) throws RuntimeException {
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
        } catch (final IOException exception) {
          logger.log(level, buffer.toString());
          logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        } finally {
          IoUtilities.close(input);
        }
      }
    };
  }

  @SuppressWarnings("hiding")
  public ProgramLauncher command(final String command) {
    this.command = command;
    return this;
  }

  public ProgramLauncher argument(final String argument) {
    this.arguments.add(argument);
    return this;
  }

  @SuppressWarnings("hiding")
  public ProgramLauncher errorStreamConsumer(final IConsumer<InputStream, RuntimeException> errorStreamConsumer) {
    if (errorStreamConsumer == null) {
      return this;
    }
    this.errorStreamConsumer = errorStreamConsumer;
    return this;
  }

  @SuppressWarnings("hiding")
  public ProgramLauncher inputStreamConsumer(final IConsumer<InputStream, RuntimeException> inputStreamConsumer) {
    if (inputStreamConsumer == null) {
      return this;
    }
    this.inputStreamConsumer = inputStreamConsumer;
    return this;
  }

  public void launch() {
    try {
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
      Optional.of(commandString).consum(c -> command.add(c));
      final ProcessBuilder processBuilder = new ProcessBuilder(command);
      Optional.of(this.inputStreamConsumer).consum(consumer -> processBuilder.redirectError(Redirect.PIPE));
      Optional.of(this.errorStreamConsumer).consum(consumer -> processBuilder.redirectInput(Redirect.PIPE));
      final Process process = processBuilder.start();
      Optional.of(this.inputStreamConsumer).consum(consumer -> consumer.consume(process.getInputStream()));
      Optional.of(this.errorStreamConsumer).consum(consumer -> consumer.consume(process.getErrorStream()));
      if (this.isWaitFor) {
        process.waitFor();
      }
    } catch (final InterruptedException exception) {
      // nothing to do
    } catch (final IOException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
    }
  }

  @SuppressWarnings("hiding")
  private String create(final String command, final List<String> arguments) {
    if (StringUtilities.isNullOrEmpty(command) && arguments.isEmpty()) {
      return null;
    }
    final StringBuilder builder = new StringBuilder();
    Optional.of(command).consum(c -> {
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
