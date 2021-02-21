package net.anwiba.commons.eclipse.logging;

public interface ILogger {

  void log(ILevel level, String message);

  void log(ILevel level, Throwable throwable);

  void log(ILevel level, String message, Throwable throwable);

}