/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.logging;

public class ExceptionLoggingTest {

  private static ILogger logger = Logging.getLogger(ExceptionLoggingTest.class);

  //  @BeforeAll
  //  public static void beforeAll() {
  //    LoggingUtilities.initialize("debug", "net.anwiba.commons.logging");
  //    Throwables.register(URISyntaxException.class, throwable -> {
  //      URISyntaxException exception = (URISyntaxException) throwable;
  //      StringBuilder sb = new StringBuilder();
  //      sb.append(exception.getReason());
  //      if (exception.getIndex() > -1) {
  //        sb.append(" at index ");
  //        sb.append(exception.getIndex());
  //      }
  //      sb.append(": dekorated ");
  //      sb.append(exception.getInput());
  //      return sb.toString();
  //    });
  //  }

  //  @Test
  //  public void log() {
  //    URISyntaxException uriSyntaxException = new URISyntaxException("http://user:password@host:ff/", "test", 12);
  //    IOException ioException = new IOException(uriSyntaxException.getMessage(), uriSyntaxException);
  //    logger.error(ioException.getMessage(), ioException);
  //  }
}
