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

import static net.anwiba.tools.generator.java.bean.JavaConstants.*;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.tools.generator.java.bean.configuration.Argument;
import net.anwiba.tools.generator.java.bean.configuration.Bean;
import net.anwiba.tools.generator.java.bean.configuration.Creator;
import net.anwiba.tools.generator.java.bean.configuration.Member;

@SuppressWarnings("nls")
public class CreatorFactory extends AbstractSourceFactory {

  public static final Set<String> stringTypes = new HashSet<>();
  private final MemberFactory memberFactory;
  static {
    stringTypes.add(java.lang.String.class.getName());
    stringTypes.add(java.lang.String.class.getSimpleName());
  }

  public CreatorFactory(final JCodeModel codeModel) {
    super(codeModel);
    this.memberFactory = new MemberFactory(codeModel);
  }

  public void creator(
      final Bean configuration,
      final JDefinedClass bean,
      @SuppressWarnings("unused") final Iterable<JFieldVar> fields) throws CreationException {
    final Creator creator = configuration.creator();
    if (creator == null) {
      return;
    }
    final Member member = configuration.member(creator.parameter());
    if (member != null && !stringTypes.contains(member.type().name())) {
      throw new CreationException(MessageFormat.format(
          "parameter name ''{0}'' is not from type java.lang.String.", //$NON-NLS-1$
          member.name()));
    }
    if (creator.factory() != null) {
      createFactoryCreateMethod(creator, bean);
      return;
    }
    final JFieldVar classes = this.memberFactory.mapStaticMember(
        bean,
        _classByNames(JAVA_UTIL_HASHMAP, JAVA_LANG_STRING, JAVA_LANG_CLASS),
        "_classes",
        new String[]{ JAVA_LANG_STRING, JAVA_LANG_CLASS });
    createTypeCreateMethod(configuration, creator, bean);
    createValidateBeanMethod(configuration, bean);
    createCreateBeanMethod(bean);
    createCreateClassMethod(bean, classes);
  }

  private void createFactoryCreateMethod(final Creator creator, final JDefinedClass bean) {
    final JMethod method = bean.method(JMod.PUBLIC | JMod.STATIC, bean, creator.name());
    annotate(method, creator.annotations());
    final Iterable<Argument> arguments = creator.arguments();
    final JVar factory = method.param(_class(creator.factory().type(), true), creator.factory().name());
    annotate(factory, creator.factory().annotations());
    final JInvocation invoke = factory.invoke("create");
    for (final Argument argument : arguments) {
      final JVar type = method.param(_class(argument.type(), true), argument.name());
      annotate(type, argument.annotations());
      invoke.arg(type);
    }
    method.body()._return(invoke);
  }

  private void createTypeCreateMethod(final Bean configuration, final Creator creator, final JDefinedClass bean) {
    final JMethod method = bean.method(JMod.PUBLIC | JMod.STATIC, bean, creator.name());
    annotate(method, creator.annotations());
    final Iterable<Argument> arguments = creator.arguments();
    final Argument argument = arguments.iterator().next();
    final JVar type = method.param(_class(argument.type(), true), argument.name());
    annotate(type, argument.annotations());

    method.body()._if(isNullOrTrimmedEmpty(type))._then()._return(JExpr._new(bean));

    final JVar clazz = method.body().decl(
        _classByNames(java.lang.Class.class.getName(), MessageFormat.format("? extends {0}", bean.name())), //$NON-NLS-1$
        "clazz",
        _createClass(type));
    JBlock block = method.body()._if(clazz.ne(JExpr._null()))._then();
    block._return(_createBean(configuration, clazz));
    method.body().assign(clazz, _createClass(lowerCase(type)));
    block = method.body()._if(clazz.ne(JExpr._null()))._then();
    block._return(_createBean(configuration, clazz));
    final JVar className = method.body().decl(
        _classByNames(java.lang.String.class.getName()),
        "className", //$NON-NLS-1$
        format("{0}{1}", type, JExpr.lit(bean.name())));
    method.body().assign(clazz, _createClass(className));
    block = method.body()._if(clazz.ne(JExpr._null()))._then();
    block._return(_createBean(configuration, clazz));
    method.body().assign(className, format("{0}{1}", lowerCase(type), JExpr.lit(bean.name()))); //$NON-NLS-1$
    method.body().assign(clazz, _createClass(className));
    block = method.body()._if(clazz.ne(JExpr._null()))._then();
    block._return(_createBean(configuration, clazz));
    method.body()._return(JExpr._new(bean));
  }

  private JExpression lowerCase(final JExpression type) {
    return type.invoke("toLowerCase");
  }

  private JExpression _createClass(final JExpression type) {
    return JExpr.invoke("_createClass").arg(type);
  }

  private JExpression _validateBean(final JExpression type) {
    return JExpr.invoke("_validateBean").arg(type);
  }

  private JExpression _createBean(final Bean configuration, final JExpression type) {
    //    final Member unkownMembers = configuration.member("_unknownMembers");
    //    if (unkownMembers != null) {
    //      return _validateBean(JExpr.invoke("_createBean").arg(type));
    //    }
    return JExpr.invoke("_createBean").arg(type);
  }

  private JMethod createValidateBeanMethod(final Bean configuration, final JDefinedClass bean) {
    if (configuration.member("_unknownMembers") != null) {
      final JMethod method = bean.method(JMod.PRIVATE | JMod.STATIC, bean, "_validateBean");
      final JVar param = method.param(_classByNames(bean.name()), "bean");
      final JTryBlock _try = method.body()._try();
      final JVar clazz = _try.body().decl(_type(java.lang.Class.class.getName()), "clazz", param.invoke("getClass"));
      final JClass mapClass = _classByNames(
          java.util.Map.class.getName(),
          String.class.getName(),
          Object.class.getName());
      final JClass methodInvokerClass = _class("net.anwiba.commons.reflection.ReflectionMethodInvoker", bean, mapClass);
      final JVar methodInvoker = _try
          .body()
          .decl(methodInvokerClass, "methodInvoker", JExpr._new(methodInvokerClass).arg(clazz).arg("get")); //$NON-NLS-1$
      final JVar unkownMembers = _try.body().decl(
          mapClass,
          "unkownMembers", //$NON-NLS-1$
          methodInvoker.invoke("invoke").arg(param));
      final JClass setterInvokerClass = _classByNames("net.anwiba.commons.reflection.ReflectionFieldSetter");
      final JVar setterInvoker = _try
          .body()
          .decl(setterInvokerClass, "setterInvoker", JExpr._new(setterInvokerClass).arg(param)); //$NON-NLS-1$
      _try.body()._if(unkownMembers.eq(JExpr._null()))._then()._return(param);
      final JForEach _for = _try
          .body()
          .forEach(_type(java.lang.String.class.getName()), "member", unkownMembers.invoke("keySet"));
      _setMemberValues(clazz, unkownMembers, setterInvoker, _for);
      _try.body()._return(param);
      final JCatchBlock _invokationCatch = _try
          ._catch(_classByNames(java.lang.reflect.InvocationTargetException.class.getName()));
      final JVar invokationException = _invokationCatch.param("exception"); //$NON-NLS-1$
      _invokationCatch.body()._throw(
          JExpr._new(_classByNames(java.lang.RuntimeException.class.getName())).arg(invokationException));
      return method;
    }
    return null;
  }

  private void _setMemberValues(
      final JVar clazz,
      final JVar unkownMembers,
      final JVar setterInvoker,
      final JForEach _for) {

    final JTryBlock _try = _for.body()._try();

    final JBlock body = _try.body();
    final JVar value = body
        .decl(_type(java.lang.Object.class.getName()), "value", unkownMembers.invoke("get").arg(JExpr.ref("member")));
    body._if(value.eq(JExpr._null()))._then()._continue();
    body._if(clazz.invoke("getDeclaredField").arg(JExpr.ref("member")).eq(JExpr._null()))._then()._continue();
    body.invoke(setterInvoker, "invoke").arg(JExpr.ref("member")).arg(value);

    final JCatchBlock _noSuchFieldCatch = _try._catch(_classByNames(java.lang.NoSuchFieldException.class.getName()));
    final JVar noSuchFieldException = _noSuchFieldCatch.param("exception"); //$NON-NLS-1$
    _noSuchFieldCatch
        .body()
        ._throw(JExpr._new(_classByNames(java.lang.RuntimeException.class.getName())).arg(noSuchFieldException));
    final JCatchBlock _securityCatch = _try._catch(_classByNames(java.lang.SecurityException.class.getName()));
    final JVar securityException = _securityCatch.param("exception"); //$NON-NLS-1$
    _securityCatch
        .body()
        ._throw(JExpr._new(_classByNames(java.lang.RuntimeException.class.getName())).arg(securityException));

  }

  public JMethod createCreateBeanMethod(final JDefinedClass bean) {

    final JMethod method = bean.method(JMod.PRIVATE | JMod.STATIC, bean, "_createBean");
    final JVar param = method.param(
        _classByNames(java.lang.Class.class.getName(), MessageFormat.format("? extends {0}", bean.name())),
        "clazz");
    final JClass invokerClass = _classByNames(
        "net.anwiba.commons.reflection.ReflectionConstructorInvoker",
        bean.name());
    final JTryBlock _try = method.body()._try();
    final JVar invoker = _try.body().decl(invokerClass, "invoker", JExpr._new(invokerClass).arg(param)); //$NON-NLS-1$
    _try.body()._return(invoker.invoke("invoke"));
    final JCatchBlock _catch = _try._catch(_classByNames(java.lang.reflect.InvocationTargetException.class.getName()));
    final JVar exception = _catch.param("exception"); //$NON-NLS-1$
    _catch.body()._throw(JExpr._new(_classByNames(java.lang.RuntimeException.class.getName())).arg(exception));
    return method;
  }

  public JMethod createCreateClassMethod(final JDefinedClass bean, final JFieldVar classes) {

    final JClass returnClazz = _classByNames(
        java.lang.Class.class.getName(),
        MessageFormat.format("? extends {0}", bean.name()));
    final JMethod method = bean.method(JMod.SYNCHRONIZED | JMod.PRIVATE | JMod.STATIC, returnClazz, "_createClass");
    final JVar type = method.param(java.lang.String.class, "type");

    method.body()._if(classes.invoke("containsKey").arg(type))._then()._return(
        JExpr.cast(returnClazz, classes.invoke("get").arg(type)));

    final JTryBlock _try = method.body()._try();
    final JBlock body = _try.body();
    final JVar packageName = body
        .decl(_classByNames(java.lang.String.class.getName()), "packageName", packageName(bean));
    final JVar typeName = body
        .decl(_classByNames(java.lang.String.class.getName()), "typeName", setFirstCharacterToUpperCase(type));
    final JVar className = body
        .decl(_classByNames(java.lang.String.class.getName()), "className", format("{0}.{1}", packageName, typeName));
    final JVar clazz = body.decl(_classByNames(java.lang.Class.class.getName(), "?"), "clazz", classForName(className));
    final JExpression condition = isAssignableFrom(bean, clazz).not();
    final JBlock block = body._if(condition)._then().block();
    block.invoke(classes, "put").arg(type).arg(JExpr._null());
    block._return(JExpr._null());
    body.invoke(classes, "put").arg(type).arg(clazz);
    body._return(JExpr.cast(returnClazz, clazz));
    final JCatchBlock _catch = _try._catch(_classByNames(java.lang.ClassNotFoundException.class.getName()));
    _catch.param("exception");
    _catch.body().invoke(classes, "put").arg(type).arg(JExpr._null());
    _catch.body()._return(JExpr._null());
    return method;
  }

  private JExpression isNullOrTrimmedEmpty(final JVar className) {
    return _classByNames(net.anwiba.commons.utilities.string.StringUtilities.class.getName())
        .staticInvoke("isNullOrTrimmedEmpty")
        .arg(className);
  }

  private JExpression setFirstCharacterToUpperCase(final JVar className) {
    return _classByNames(net.anwiba.commons.utilities.string.StringUtilities.class.getName())
        .staticInvoke("setFirstTrimedCharacterToUpperCase") //$NON-NLS-1$
        .arg(className);
  }

  private JExpression classForName(final JVar className) {
    return _classByNames(java.lang.Class.class.getName()).staticInvoke("forName").arg(className);
  }

  private JInvocation isAssignableFrom(final JDefinedClass bean, final JExpression clazz) {
    return JExpr.dotclass(bean).invoke("isAssignableFrom").arg(clazz);
  }

  private JInvocation packageName(final JDefinedClass bean) {
    return JExpr.dotclass(bean).invoke("getPackage").invoke("getName");
  }
}
