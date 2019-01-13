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

package net.anwiba.commons.injection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import net.anwiba.commons.annotation.Imitable;
import net.anwiba.commons.annotation.Injection;
import net.anwiba.commons.annotation.Named;
import net.anwiba.commons.injection.binding.NamedClassBinding;
import net.anwiba.commons.injection.impl.BindingFactory;
import net.anwiba.commons.reflection.CreationException;

@SuppressWarnings("nls")
public class InjectionValueProviderBuilderTest {

  public static interface IWow {
    IBar getBar();

    Iterable<IFoo> getFoos();
  };
  public static class Wow implements IWow {

    @Injection
    private final IBar bar = null;

    @Injection
    private final Iterable<IFoo> foos = null;

    @Override
    public Iterable<IFoo> getFoos() {
      return this.foos;
    }

    @Override
    public IBar getBar() {
      return this.bar;
    }

  };

  public static class Woww implements IWow {

    private final IBar bar;

    private final Iterable<IFoo> foos;

    public Woww(final IBar bar, final Collection<IFoo> foos) {
      this.bar = bar;
      this.foos = foos;
    }

    @Override
    public Iterable<IFoo> getFoos() {
      return this.foos;
    }

    @Override
    public IBar getBar() {
      return this.bar;
    }

  };

  public static interface IBar {
    Iterable<IFoo> getFoos();
  };

  public static class Bar implements IBar {

    @Injection
    private final Iterable<IFoo> foos = null;

    @Override
    public Iterable<IFoo> getFoos() {
      return this.foos;
    }

  };

  public static interface IMau {

    String getName();

    Integer getValue();

  };

  public static class Mau implements IMau {

    @Injection
    @Named
    private final Integer value = null;

    @Injection
    @Named("name")
    private final String string = null;

    @Override
    public Integer getValue() {
      return this.value;
    }

    @Override
    public String getName() {
      return this.string;
    }

  };

  public static class Pau implements IMau {

    private final Integer value;
    private final String string;

    @Injection
    public Pau(@Named("value") final Integer value, @Named("name") final String string) {
      this.value = value;
      this.string = string;
    }

    @Override
    public Integer getValue() {
      return this.value;
    }

    @Override
    public String getName() {
      return this.string;
    }

  };

  public static class Meu {

    private final Peu peu;

    public Meu(final Peu peu) {
      this.peu = peu;
    }

    public Peu getPeu() {
      return this.peu;
    }

  };

  public static class Peu {
  };

  public static interface IFoo {
  };

  public static interface IImitable {

    void any();

    default boolean that() {
      return true;
    };

  };

  public static class ImitableUser {

    private final IImitable imitable;

    @Injection
    public ImitableUser(@Imitable final IImitable imitable) {
      this.imitable = imitable;
    }

    public IImitable getImitable() {
      return this.imitable;
    }
  };

  public static interface IFoos extends IFoo {
  };

  public static class Foo implements IFoos {
  };
  public static class FooA implements IFoos {
  };
  public static class FooB implements IFoos {
  };
  public static class FooC implements IFoos {
  };

  IScope scope = new IScope() {
  };

  private final IBindingFactory bindingFactory = new BindingFactory();

  private <T> IBinding<T> binding(final Class<T> clazz) {
    return this.bindingFactory.create(clazz, null);
  }

  @Test
  public void createImitable() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(ImitableUser.class);
    final IInjectionValueProvider provider = builder.build();
    final ImitableUser actual = provider.get(binding(ImitableUser.class));
    final IImitable imitable = actual.getImitable();
    assertThat(imitable, notNullValue());
    assertThat(imitable.that(), equalTo(true));
    imitable.any();
    assertThat(provider.get(binding(IImitable.class)), nullValue());
  }

  @Test
  public void createMeu() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(Meu.class);
    final IInjectionValueProvider provider = builder.build();
    assertThat(provider.get(binding(Meu.class)), instanceOf(Meu.class));
    assertThat(provider.get(binding(Meu.class)).getPeu(), notNullValue());
    assertThat(provider.get(binding(Peu.class)), nullValue());
  }

  @Test
  public void setFoo() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(Foo.class, new Foo());
    builder.link(Foo.class, IFoo.class);
    final IInjectionValueProvider provider = builder.build();
    assertThat(provider.get(binding(IFoo.class)), instanceOf(Foo.class));
    assertThat(provider.get(binding(Foo.class)), instanceOf(Foo.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void setAddInjectFooThrowIllegalArgumentException() {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(IFoo.class, FooA.class);
    builder.add(IFoo.class, FooB.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSetInjectFooThrowIllegalArgumentException() {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.add(IFoo.class, FooB.class);
    builder.set(IFoo.class, FooA.class);
  }

  @Test(expected = IllegalStateException.class)
  public void addGetInjectFooThrowIllegalStateException() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.add(IFoo.class, FooB.class);
    builder.build().get(binding(IFoo.class));
  }

  @Test
  public void setGetAllInjectFoo() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(IFoo.class, FooB.class);
    final Collection<IFoo> foos = builder.build().getAll(binding(IFoo.class));
    assertThat(foos.size(), equalTo(1));
  }

  @Test
  public void injectMau() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(new NamedClassBinding<>(String.class, "name"), "Text");
    builder.set(new NamedClassBinding<>(Integer.class, "value"), Integer.valueOf(20));
    builder.set(IMau.class, Mau.class);
    final IInjectionValueProvider provider = builder.build();
    final IMau mau = provider.get(binding(IMau.class));
    assertThat(mau.getName(), equalTo("Text"));
    assertThat(mau.getValue(), equalTo(Integer.valueOf(20)));
  }

  @Test
  public void injectPau() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(new NamedClassBinding<>(String.class, "name"), "Text");
    builder.set(new NamedClassBinding<>(Integer.class, "value"), Integer.valueOf(20));
    builder.set(IMau.class, Pau.class);
    final IInjectionValueProvider provider = builder.build();
    final IMau mau = provider.get(binding(IMau.class));
    assertThat(mau.getName(), equalTo("Text"));
    assertThat(mau.getValue(), equalTo(Integer.valueOf(20)));
  }

  @Test(expected = CreationException.class)
  public void injectPauThrowException() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(IMau.class, Pau.class);
    builder.build();
  }

  @Test
  public void injectBarAndFooAndWow() throws CreationException {
    final InjectionValueProviderBuilder builder = new InjectionValueProviderBuilder(this.scope);
    builder.set(IWow.class, Wow.class);
    builder.set(IBar.class, Bar.class);
    builder.add(IFoos.class, FooA.class);
    builder.add(IFoos.class, FooB.class);
    builder.add(IFoos.class, FooC.class);
    builder.link(IFoos.class, IFoo.class);
    builder.set(Woww.class);
    final IInjectionValueProvider provider = builder.build();
    final Collection<IFoo> foos = provider.getAll(binding(IFoo.class));
    assertThat(foos.size(), equalTo(3));

    final Collection<IFoos> fooss = provider.getAll(binding(IFoos.class));
    assertThat(fooss.size(), equalTo(3));

    final IBar bar = provider.get(binding(IBar.class));
    final AtomicInteger barFooCounter = new AtomicInteger();
    bar.getFoos().forEach(f -> barFooCounter.incrementAndGet());
    assertThat(barFooCounter.get(), equalTo(3));

    final IWow wow = provider.get(binding(IWow.class));
    assertThat(wow, notNullValue());
    assertThat(wow.getBar(), notNullValue());
    assertThat(wow.getBar(), sameInstance(bar));
    final AtomicInteger wowFooCounter = new AtomicInteger();
    wow.getFoos().forEach(f -> wowFooCounter.incrementAndGet());
    assertThat(wowFooCounter.get(), equalTo(3));

    final IWow woww = provider.get(binding(Woww.class));
    assertThat(woww, notNullValue());
    assertThat(woww.getBar(), notNullValue());
    assertThat(woww.getBar(), sameInstance(bar));
    final AtomicInteger wowwFooCounter = new AtomicInteger();
    woww.getFoos().forEach(f -> wowwFooCounter.incrementAndGet());
    assertThat(wowwFooCounter.get(), equalTo(3));
  }
}
