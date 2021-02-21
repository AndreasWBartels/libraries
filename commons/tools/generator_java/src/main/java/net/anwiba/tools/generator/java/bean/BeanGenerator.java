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
package net.anwiba.tools.generator.java.bean;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.tools.generator.java.bean.configuration.Bean;
import net.anwiba.tools.generator.java.bean.factory.BeanBuilderFactory;
import net.anwiba.tools.generator.java.bean.factory.BeanFactory;
import net.anwiba.tools.generator.java.bean.factory.EnsurePredicateFactory;
import net.anwiba.tools.generator.java.bean.writer.SourceCodeWriter;

public class BeanGenerator {

  final JCodeModel codeModel = new JCodeModel();
  final EnsurePredicateFactory ensurePredicateFactory = new EnsurePredicateFactory(this.codeModel);
  final BeanFactory beanFactory = new BeanFactory(this.codeModel, this.ensurePredicateFactory);
  final BeanBuilderFactory beanBuilderFactory = new BeanBuilderFactory(this.codeModel, this.ensurePredicateFactory);

  public class ClosableSourceCodeWriter extends SourceCodeWriter implements AutoCloseable {

    public ClosableSourceCodeWriter(final File target, final String comment) throws IOException {
      super(target, comment);
    }
  }

  public void generate(final OutputStream ouputStream) throws IOException {
    this.codeModel.build(new SingleStreamCodeWriter(ouputStream));
  }

  public void generate(final File targetFolder, final String comment) throws IOException {
    if (!targetFolder.exists()) {
      targetFolder.mkdirs();
    }
    final CodeWriter sourceCodeWriter = new SourceCodeWriter(targetFolder, comment);
    final CodeWriter fileCodeWriter = new FileCodeWriter(targetFolder);
    this.codeModel.build(sourceCodeWriter, fileCodeWriter);
  }

  public void add(final Bean configuration) throws CreationException {
    this.beanFactory.create(configuration);
    if (configuration.isBuilderEnabled()) {
      this.beanBuilderFactory.create(configuration);
    }
  }
}
