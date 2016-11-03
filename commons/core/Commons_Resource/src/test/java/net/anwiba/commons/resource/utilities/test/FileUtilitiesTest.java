/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.resource.utilities.test;

import net.anwiba.commons.resource.utilities.FileUtilities;
import net.anwiba.commons.resource.utilities.IFileExtensions;

import java.io.File;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

public class FileUtilitiesTest {

  @Test
  public void getExtension() {
    assertThat((String) null, equalTo(FileUtilities.getExtension(new File("")))); //$NON-NLS-1$
    assertThat((String) null, equalTo(FileUtilities.getExtension(new File("file")))); //$NON-NLS-1$
    assertThat((String) null, equalTo(FileUtilities.getExtension(new File(".file")))); //$NON-NLS-1$
    assertThat(
        IFileExtensions.BMP,
        equalTo(FileUtilities.getExtension(FileUtilities.addExtension(new File("file"), IFileExtensions.BMP)))); //$NON-NLS-1$
  }

  @Test
  public void getFileWithoutExtension() {
    assertThat(new File("name"), equalTo(FileUtilities.getFileWithoutExtension(new File("name")))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(new File("name"), equalTo(FileUtilities.getFileWithoutExtension(new File("name.et")))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(new File("name"), equalTo(FileUtilities.getFileWithoutExtension(new File("name.ext")))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(new File("name"), equalTo(FileUtilities.getFileWithoutExtension(new File("name.extt")))); //$NON-NLS-1$ //$NON-NLS-2$
  }

}
