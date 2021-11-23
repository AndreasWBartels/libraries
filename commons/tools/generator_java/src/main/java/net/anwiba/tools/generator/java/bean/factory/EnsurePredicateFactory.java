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
package net.anwiba.tools.generator.java.bean.factory;

import static net.anwiba.commons.ensure.Conditions.notNull;
import static net.anwiba.commons.ensure.Ensure.ensureThatArgument;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;

public class EnsurePredicateFactory extends AbstractSourceFactory {

  private final JClass ensureClass;

  public EnsurePredicateFactory(final JCodeModel codeModel) {
    super(codeModel);
    this.ensureClass = _classByNames(java.util.Objects.class.getName());
  }

  public JStatement ensureArgumentNotNull(final JVar param) {
    ensureThatArgument(param, notNull());
    return this.ensureClass
        .staticInvoke("requireNonNull") //$NON-NLS-1$
        .arg(param);
  }

}
