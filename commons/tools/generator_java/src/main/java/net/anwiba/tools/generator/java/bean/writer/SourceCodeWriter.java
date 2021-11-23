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
package net.anwiba.tools.generator.java.bean.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FileCodeWriter;

public class SourceCodeWriter extends FileCodeWriter {

  private final String comment;

  public SourceCodeWriter(final File target, final String comment) throws IOException {
    super(target);
    this.comment = comment;
  }

  @Override
  public OutputStream openBinary(final JPackage pkg, final String fileName) throws IOException {
    final FileOutputStream outputStream = new FileOutputStream(getFile(pkg, fileName));
    if (this.comment != null) {
      outputStream.write(this.comment.getBytes());
    }
    return outputStream;
  }

}
