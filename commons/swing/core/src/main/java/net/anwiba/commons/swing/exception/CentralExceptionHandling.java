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
package net.anwiba.commons.swing.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.MessageFormat;
import java.util.Objects;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.utilities.OperationSystemUtilities;

public class CentralExceptionHandling {

  private static ILogger logger = Logging.getLogger(CentralExceptionHandling.class.getName());
  private static final CentralExceptionHandling instance = new CentralExceptionHandling();

  private IExceptionHandler handler;

  private CentralExceptionHandling() {
    attachForEventDispatchExceptionHandling();
    attachForThreadUncaughtExceptionHandling();
  }

  private void attachForThreadUncaughtExceptionHandling() {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(final Thread t, final Throwable e) {
        // hack for gtk+ and html tooltip exception https://bugs.openjdk.java.net/browse/JDK-8262085
//      Caused by: java.lang.IllegalArgumentException: Width and height must be >= 0
//          at javax.swing.plaf.basic.BasicHTML.getHTMLBaseline(BasicHTML.java:91) ~[?:?]
//          at javax.swing.plaf.metal.MetalToolTipUI.paint(MetalToolTipUI.java:126) ~[?:?]
//          at javax.swing.plaf.ComponentUI.update(ComponentUI.java:161) ~[?:?]
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        if (OperationSystemUtilities.isLinux() && e instanceof IllegalArgumentException
            && t.getClass().getName().endsWith("EventDispatchThread")
            && Objects.equals(e.getMessage(), "Width and height must be >= 0")
            && stackTraceElements.length > 1
            && Objects.equals(stackTraceElements[0].getClassName(), "javax.swing.plaf.basic.BasicHTML")
            && Objects.equals(stackTraceElements[0].getMethodName(), "getHTMLBaseline")) {
          if (logger.isLoggable(ILevel.FINE)) {
            handle(new RuntimeException(MessageFormat.format("Uncaught exception on thread [{0}]", t.getName()), e)); //$NON-NLS-1$
          }
          return;
        }
        handle(new RuntimeException(MessageFormat.format("Uncaught exception on thread [{0}]", t.getName()), e)); //$NON-NLS-1$
      }
    });
  }

  public IExceptionHandler getHandler() {
    return this.handler;
  }

  public static void setHandler(final IExceptionHandler handler) {
    getInstance().handler = handler;
  }

  public static CentralExceptionHandling getInstance() {
    return instance;
  }

  public void handle(final Throwable exception) {
    if (this.handler != null) {
      this.handler.handle(exception);
    } else {
      logger.log(ILevel.FATAL, exception.getMessage(), exception); // $NON-NLS-1$
    }
  }

  private void attachForEventDispatchExceptionHandling() {
    System.setProperty("sun.awt.exception.handler", InternalExceptionHandler.class.getName()); //$NON-NLS-1$
  }
}