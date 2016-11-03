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

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public final class BuildMethodFactory extends AbstractSourceFactory {

  public BuildMethodFactory(final JCodeModel codeModel) {
    super(codeModel);
  }

  public void create(final JDefinedClass beanBuilder, final String name, final Iterable<JFieldVar> fields) {
    final JClass bean = _class(name);
    final JMethod method = beanBuilder.method(JMod.PUBLIC, bean, "build"); //$NON-NLS-1$
    JInvocation instance = JExpr._new(bean);
    for (final JFieldVar field : fields) {
      instance = instance.arg(field);
    }
    method.body()._return(instance);
  }
}