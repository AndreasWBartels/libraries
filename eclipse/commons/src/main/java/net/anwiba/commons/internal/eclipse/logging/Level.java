package net.anwiba.commons.internal.eclipse.logging;

import net.anwiba.commons.eclipse.logging.ILevel;

import org.eclipse.core.runtime.IStatus;

public enum Level implements ILevel {

  OK(IStatus.OK), ERROR(IStatus.ERROR), INFO(IStatus.INFO), WARNING(IStatus.WARNING), CANCEL(IStatus.CANCEL);

  private final int code;

  private Level(final int code) {
    this.code = code;
  }

  @Override
  public int getCode() {
    return this.code;
  }
}
