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

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.tools.generator.java.bean.configuration.Bean;
import net.anwiba.tools.generator.java.bean.configuration.Member;
import net.anwiba.tools.generator.java.bean.configuration.Setter;

public class BeanBuilderFactory extends AbstractSourceFactory {

  private final SetterFactory setterFactory;
  private final MemberFactory memberFactory;
  private final BuildMethodFactory buildMethodFactory;
  private final ConstructorFactory constructorFactory;

  public BeanBuilderFactory(final JCodeModel codeModel, final EnsurePredicateFactory ensurePredicateFactory) {
    super(codeModel);
    this.setterFactory = new SetterFactory(codeModel, ensurePredicateFactory);
    this.memberFactory = new MemberFactory(codeModel);
    this.buildMethodFactory = new BuildMethodFactory(codeModel);
    this.constructorFactory = new ConstructorFactory(codeModel, ensurePredicateFactory, true);
  }

  public void create(final Bean configuration) throws CreationException {
    try {
      final JDefinedClass beanBuilder = _class(configuration.name() + "Builder", ClassType.CLASS); //$NON-NLS-1$
      configuration.comment().consume(comment -> addTo(beanBuilder.javadoc(), comment));
      annotate(beanBuilder, configuration.annotations());
      final Map<Member, JFieldVar> fieldsByMember = fields(beanBuilder, configuration);
      final Collection<JFieldVar> fields = fieldsByMember.values();
      this.constructorFactory.constructor(configuration, beanBuilder, fields);
      this.buildMethodFactory.create(beanBuilder, configuration.name(), fields);
      setters(beanBuilder, fieldsByMember, configuration);
    } catch (final JClassAlreadyExistsException exception) {
      throw new CreationException(exception.getLocalizedMessage(), exception);
    }
  }

  private Iterable<JFieldVar> setters(
      final JDefinedClass instance,
      final Map<Member, JFieldVar> fieldsByMember,
      final Bean configuration) throws CreationException {
    final List<JFieldVar> result = new ArrayList<>();
    final Set<Entry<Member, JFieldVar>> entrySet = fieldsByMember.entrySet();
    for (final Entry<Member, JFieldVar> entry : entrySet) {
      final Member member = entry.getKey();
      final JFieldVar field = entry.getValue();
      result.add(field);
      if (!member.setter().isEnabled() || !member.isNullable()) {
        continue;
      }
      final Setter setter = member.setter();
      this.setterFactory.create(
          instance,
          true,
          field,
          setter.name(),
          false,
          member.isNullable(),
          configuration.isArrayNullable(),
          configuration.isCollectionNullable(),
          setter.annotations(),
          setter.comment());
    }
    return result;
  }

  public Map<Member, JFieldVar> fields(final JDefinedClass instance, final Bean configuration)
      throws CreationException {
    final Map<Member, JFieldVar> result = new LinkedHashMap<>();
    for (final Member member : configuration.members()) {
      if (!member.setter().isEnabled()) {
        continue;
      }
      final JFieldVar field = this.memberFactory.create(
          instance,
          member.comment(),
          member.annotations(),
          member.type(),
          member.name(),
          member.value(),
          false,
          member.isNullable(),
          configuration.isPrimitivesEnabled(),
          configuration.isArrayNullable(),
          configuration.isCollectionNullable());
      result.put(member, field);
    }
    return result;
  }

}