/*
 * #%L anwiba commons tools %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.tools.definition.schema.json.generator.java.bean;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class JsonBeanGeneratorExecutorTest {

  @SuppressWarnings("nls")
  @Test
  public void excecute() throws IOException {
    final JsonBeanGeneratorExecutor excecutor = new JsonBeanGeneratorExecutor(
        new File("src/test/resources"),
        getClass().getPackage().getName(),
        null,
        new IOutput() {

          @Override
          public void warn(final String message) {
            System.out.println(message);
          }

          @Override
          public void info(final String message) {
            System.out.println(message);
          }

          @Override
          public void error(final String message, final Throwable throwable) {
            System.err.println(message);
          }
        });
    excecutor.excecute(new File("target/test/generated"));
    assertTrue(true);
  }

}
