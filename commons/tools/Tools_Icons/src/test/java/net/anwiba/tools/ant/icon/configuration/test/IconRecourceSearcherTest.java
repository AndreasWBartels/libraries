/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.ant.icon.configuration.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.anwiba.tools.icons.configuration.IconRecourceSearcher;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.junit.Assert.assertThat;

public class IconRecourceSearcherTest {

  @Test
  @SuppressWarnings("boxing")
  public void search() throws IOException {
    final IconRecourceSearcher searcher = new IconRecourceSearcher(true);
    final List<File> list = searcher.search(new File("src/test/resources/ProjectA")); //$NON-NLS-1$
    assertThat(1, equalTo(list.size()));
    assertThat(
        new File("src/test/resources/ProjectB/resources/icons/icons.xml").getCanonicalFile(), equalTo(list.get(0).getCanonicalFile())); //$NON-NLS-1$
  }
}
