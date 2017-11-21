/*
 * #%L anwiba commons core %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.crypto;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PasswordCoderTest {

  private static final String ENCODED_PASSWORD = "1fB1StEBxiJB0Prqy0UBHg=="; //$NON-NLS-1$
  private static final String PLAIN_PASSWORD = "password"; //$NON-NLS-1$

  @Test
  public void testEncode() throws Exception {
    assertThat(new PasswordCoder("g3r96zt2".getBytes()).encode(PLAIN_PASSWORD), equalTo(ENCODED_PASSWORD)); //$NON-NLS-1$
  }

  @Test
  public void testDecode() throws Exception {
    assertThat(new PasswordCoder("g3r96zt2".getBytes()).decode(ENCODED_PASSWORD), equalTo(PLAIN_PASSWORD)); //$NON-NLS-1$
  }
}
