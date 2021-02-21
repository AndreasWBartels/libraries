package net.anwiba.commons.internal.eclipse.logging;

import net.anwiba.commons.eclipse.logging.ILevel;
import net.anwiba.commons.eclipse.logging.ILogger;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

public class Logger implements ILogger {

  private final ILog log;
  private final String pluginId;

  public Logger(final ILog log, final String pluginId) {
    this.log = log;
    this.pluginId = pluginId;
  }

  @Override
  public void log(final ILevel level, final String message) {
    this.log.log(new Status(level.getCode(), this.pluginId, message));
  }

  @Override
  public void log(final ILevel level, final Throwable throwable) {
    log(level, throwable.getLocalizedMessage(), throwable);
  }

  @Override
  public void log(final ILevel level, final String message, final Throwable throwable) {
    this.log.log(new Status(level.getCode(), this.pluginId, message, throwable));
  }

}
