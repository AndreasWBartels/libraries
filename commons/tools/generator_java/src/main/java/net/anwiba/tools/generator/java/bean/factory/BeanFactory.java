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

import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.isInstanceOfMap;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.Argument;
import net.anwiba.tools.generator.java.bean.configuration.Bean;
import net.anwiba.tools.generator.java.bean.configuration.Builders;
import net.anwiba.tools.generator.java.bean.configuration.Getter;
import net.anwiba.tools.generator.java.bean.configuration.Member;
import net.anwiba.tools.generator.java.bean.configuration.NamedValueProvider;
import net.anwiba.tools.generator.java.bean.configuration.Setter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JVar;

public class BeanFactory extends AbstractSourceFactory {

  private final ConstructorFactory constructorFactory;
  private final CreatorFactory creatorFactory;
  private final MemberFactory memberFactory;
  private final GetterFactory getterFactory;
  private final SetterFactory setterFactory;
  private final NamedValueProviderFactory namedValueProviderFactory;
  private final EqualsFactory equalsFactory;

  public BeanFactory(final JCodeModel codeModel,
      final IFactory<String, Class<? extends java.lang.annotation.Annotation>,
          CreationException> annotationClassfactory,
      final EnsurePredicateFactory ensurePredicateFactory) {
    super(codeModel, annotationClassfactory);
    this.constructorFactory = new ConstructorFactory(codeModel, annotationClassfactory, ensurePredicateFactory, false);
    this.memberFactory = new MemberFactory(codeModel, annotationClassfactory);
    this.setterFactory = new SetterFactory(codeModel, annotationClassfactory, ensurePredicateFactory);
    this.getterFactory = new GetterFactory(codeModel, annotationClassfactory);
    this.namedValueProviderFactory = new NamedValueProviderFactory(codeModel, annotationClassfactory);
    this.creatorFactory = new CreatorFactory(codeModel, annotationClassfactory);
    this.equalsFactory = new EqualsFactory(codeModel, annotationClassfactory);
  }

  public void create(final Bean configuration) throws CreationException {
    try {
      final JDefinedClass bean = _class(configuration.name(), ClassType.CLASS);
      if (configuration.extend() != null) {
        bean._extends(_classByNames(configuration.extend()));
      }
      configuration.comment().consume(comment -> addTo(bean.javadoc(), comment));
      annotate(bean, configuration.annotations());
      if (configuration.isMutable()) {
        this.constructorFactory.constructor(configuration, bean, new ArrayList<JFieldVar>());
      }

      final Map<Member, JFieldVar> fieldsByMember = fields(bean, configuration);
      final Collection<JFieldVar> fields = fieldsByMember.values();
      this.constructorFactory.constructor(configuration, bean, fields);
      this.creatorFactory.creator(configuration, bean, fields);
      memberMethods(bean, fieldsByMember, configuration);
      this.equalsFactory.create(configuration, bean, fields);
    } catch (final JClassAlreadyExistsException exception) {
      throw new CreationException(exception.getLocalizedMessage(), exception);
    }
  }

  public Map<Member, JFieldVar> fields(final JDefinedClass instance, final Bean configuration)
      throws CreationException {
    final Map<Member, JFieldVar> result = new LinkedHashMap<>();
    for (final Member member : configuration.members()) {
      final JFieldVar field = this.memberFactory.create(
          instance,
          member.comment(),
          member.annotations(),
          member.type(),
          member.name(),
          member.value(),
          member.isImutable(),
          member.isNullable(),
          configuration.isPrimitivesEnabled(),
          configuration.isArrayNullable(),
          configuration.isCollectionNullable());
      result.put(member, field);
    }
    return result;
  }

  private void memberMethods(
      final JDefinedClass instance,
      final Map<Member, JFieldVar> fieldsByMember,
      final Bean configuration)
      throws CreationException {
    final Set<Entry<Member, JFieldVar>> entrySet = fieldsByMember.entrySet();
    for (final Entry<Member, JFieldVar> entry : entrySet) {
      final Member member = entry.getKey();
      final JFieldVar field = entry.getValue();
      createSetter(instance, configuration, member, field);
      createGetter(instance, configuration, member, field);
      createNamedValueProvider(instance, configuration, member, field);
      member.asObjectMethodFactory().create(getCodeModel(), instance, field);
      member.valueOfMethodFactory().create(getCodeModel(), instance, field);
    }
  }

  public void createNamedValueProvider(
      final JDefinedClass instance,
      final Bean configuration,
      final Member member,
      final JFieldVar field)
      throws CreationException {
    final NamedValueProvider namedValueProvider = configuration.namedValueProvider(member.name());
    if (namedValueProvider != null) {
      this.namedValueProviderFactory.create(instance, namedValueProvider, field);
    }
  }

  public void createGetter(
      final JDefinedClass instance,
      final Bean configuration,
      final Member member,
      final JFieldVar field)
      throws CreationException {
    final Getter getter = member.getter();
    if (getter.isEnabled()) {
      if (getter.isNamedValueGetterEnabled()) {
        final NamedValueProvider namedValueProvider = new NamedValueProvider(
            Builders.type(member.type().generics()[1]).build(),
            member.name(),
            false,
            new ArrayList<Annotation>(),
            MessageFormat.format("{0}Names", getter.name()), //$NON-NLS-1$
            new ArrayList<Annotation>(),
            getter.name());
        this.namedValueProviderFactory.create(instance, namedValueProvider, field);
      }
      this.getterFactory.create(
          instance,
          member.isNullable(),
          configuration.isCollectionNullable(),
          getter.annotations(),
          getter.comment(),
          field,
          getter.name());
    }
  }

  public void createSetter(
      final JDefinedClass instance,
      final Bean configuration,
      final Member member,
      final JFieldVar field)
      throws CreationException {
    final Setter setter = member.setter();
    final Argument argument = setter.arguments().iterator().next();
    if (configuration.isMutable() && setter.isEnabled()) {
      if (isInstanceOfMap(field.type())) {
        if (setter.isMultiValue()) {
          final List<Annotation> annotations = setter.isSingleValue() ? new ArrayList<>() : setter.annotations();
          this.setterFactory.create(
              instance, //
              field, //
              setter.name(), //
              member.isImutable(),
              member.isNullable(), //
              annotations, //
              setter.comment(), //
              _type(member.type().generics()[0]), //
              "name", //$NON-NLS-1$
              _type(member.type().generics()[1]), //
              "value"); //$NON-NLS-1$
        }
        if (setter.isSingleValue()) {
          final JVar parameter = this.setterFactory.create(
              instance,
              false,
              field,
              setter.name(),
              member.isImutable(),
              member.isNullable(), //
              configuration.isArrayNullable(),
              configuration.isCollectionNullable(),
              setter.annotations(),
              setter.comment());
          annotate(parameter, argument.annotations());
        }
      } else {
        final JVar parameter = this.setterFactory.create(
            instance,
            false,
            field,
            setter.name(),
            member.isImutable(),
            member.isNullable(), //
            configuration.isArrayNullable(),
            configuration.isCollectionNullable(),
            setter.annotations(),
            setter.comment());
        annotate(parameter, argument.annotations());
      }
    }
  }
}
