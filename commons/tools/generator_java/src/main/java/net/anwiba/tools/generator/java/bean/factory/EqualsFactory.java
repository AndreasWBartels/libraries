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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import net.anwiba.tools.generator.java.bean.configuration.Bean;

@SuppressWarnings("nls")
public class EqualsFactory extends AbstractSourceFactory {

  private final JCodeModel codeModel;

  public EqualsFactory(final JCodeModel codeModel) {
    super(codeModel);
    this.codeModel = codeModel;
  }

  public void create(final Bean configuration, final JDefinedClass bean, final Iterable<JFieldVar> fields) {
    if (!configuration.isEqualsEnabled()) {
      return;
    }
    createEquals(bean, fields);
    createHashCode(bean, fields);
  }

  private void createHashCode(final JDefinedClass bean, final Iterable<JFieldVar> fields) {
    final JMethod method = bean.method(JMod.PUBLIC, this.codeModel.INT, "hashCode");
    method.annotate(java.lang.Override.class);
    final JBlock block = method.body().block();
    final JClass objectUtilities = _classByNames(java.util.Objects.class.getName());
    JInvocation hashCode = objectUtilities.staticInvoke("hashCode");
    for (final JFieldVar field : fields) {
      hashCode = hashCode.arg(JExpr.refthis(field.name()));
    }
    block._return(hashCode);
  }

  public void createEquals(final JDefinedClass bean, final Iterable<JFieldVar> fields) {
    final JMethod method = bean.method(JMod.PUBLIC, this.codeModel.BOOLEAN, "equals");
    method.annotate(java.lang.Override.class);
    final JVar object = method.param(_type(java.lang.Object.class.getName()), "object");
    final JBlock block = method.body();
    block._if(JExpr._this().eq(object))._then()._return(JExpr.TRUE);
    block._if(object._instanceof(bean).not())._then()._return(JExpr.FALSE);
    JExpression result = JExpr.TRUE;
    final JExpression other = block.decl(bean, "other", JExpr.cast(bean, object));
    final JClass objectUtilities = _classByNames(java.util.Objects.class.getName());
    for (final JFieldVar field : fields) {
      result =
          result.cand(objectUtilities.staticInvoke("equals").arg(JExpr.refthis(field.name())).arg(other.ref(field)));
    }
    block._return(result);
  }
}