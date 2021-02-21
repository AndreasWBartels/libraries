/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.utilities.io.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.parameter.ParametersBuilder;
import net.anwiba.commons.utilities.string.StringUtilities;

public class Url implements IUrl {

  private final List<String> scheme;
  private final List<String> path;
  private final IParameters parameters;
  private final String fragment;
  private final IAuthority authority;

  public Url(
      final List<String> scheme,
      final IAuthority authority,
      final List<String> path,
      final List<IParameter> parameters,
      final String fragment) {
    this.scheme = scheme;
    this.authority = authority;
    this.path = path;
    this.parameters = new ParametersBuilder().add(parameters).build();
    this.fragment = fragment;
  }

  @Override
  public List<String> getScheme() {
    return this.scheme;
  }

  @Override
  public IAuthentication getAuthentication() {
    return Optional.of(this.authority).convert(a -> a.getAuthentication()).get();
  }

  @Override
  public IHost getHost() {
    return Optional.of(this.authority).convert(a -> a.getHost()).get();
  }

  @Override
  public List<String> getPath() {
    return this.path;
  }

  @Override
  public IParameters getQuery() {
    return this.parameters;
  }

  @Override
  public String getFragment() {
    return this.fragment;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(StringUtilities.concat(":", this.scheme));
    if (Objects.equals(this.scheme, List.of("file"))) {
      builder.append(":/");
    } else {
      builder.append("://");
    }
    if (this.authority != null) {
      final IAuthentication authentication = getAuthentication();
      if (authentication != null) {
        builder.append(authentication.getUsername());
        builder.append(":");
        builder.append(authentication.getPassword());
        builder.append("@");
      }
      final IHost host = getHost();
      if (host != null) {
        builder.append(host.getName());
        if (host.getPort() > -1) {
          builder.append(":");
          builder.append(host.getPort());
        }
      }
    }
    if (this.path.isEmpty()) {
      if (this.parameters.getNumberOfParameter() > 0) {
        builder.append("?");
        builder
            .append(
                StringUtilities
                    .concat(
                        "&",
                        IterableUtilities
                            .asList(this.parameters.parameters())
                            .stream()
                            .map(p -> p.getName() + "=" + p.getValue())
                            .collect(Collectors.toList())));
      } else {
        builder.append("/");
      }
    } else {
      builder.append(StringUtilities.concat("", this.path));
      if (this.parameters.getNumberOfParameter() > 0) {
        builder.append("?");
        builder
            .append(
                StringUtilities
                    .concat(
                        "&",
                        IterableUtilities
                            .asList(this.parameters.parameters())
                            .stream()
                            .map(p -> p.getName() + "=" + p.getValue())
                            .collect(Collectors.toList())));
      }
    }
    if (this.fragment != null) {
      builder.append("#");
      builder.append(this.fragment);
    }
    return builder.toString();
  }

  @Override
  public String getHostname() {
    return Optional.of(this.authority).convert(a -> a.getHost()).convert(h -> h.getName()).get();
  }

  @Override
  public int getPort() {
    return Optional
        .of(this.authority)
        .convert(a -> a.getHost())
        .convert(h -> Integer.valueOf(h.getPort()))
        .getOr(() -> Integer.valueOf(-1))
        .intValue();
  }

  @Override
  public String getPathString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(StringUtilities.concat("", this.path));
    return builder.toString();
  }

  @Override
  public String getUserName() {
    return Optional
        .of(this.authority)
        .convert(a -> a.getAuthentication())
        .convert(a -> a.getUsername())
        .get();
  }

  @Override
  public String getPassword() {
    return Optional
        .of(this.authority)
        .convert(a -> a.getAuthentication())
        .convert(a -> a.getPassword())
        .get();
  }

  @Override
  public String encoded() {
    final StringBuilder builder = new StringBuilder();
    builder.append(StringUtilities.concat(":", this.scheme));
    if (Objects.equals(this.scheme, List.of("file"))) {
      builder.append(":/");
    } else {
      builder.append("://");
    }
    if (this.authority != null) {
      final IAuthentication authentication = getAuthentication();
      if (authentication != null) {
        builder.append(authentication.getUsername());
        builder.append(":");
        builder.append(authentication.getPassword());
        builder.append("@");
      }
      final IHost host = getHost();
      if (host != null) {
        builder.append(host.getName());
        if (host.getPort() > -1) {
          builder.append(":");
          builder.append(host.getPort());
        }
      }
    }
    if (this.path.isEmpty()) {
      if (this.parameters.getNumberOfParameter() > 0) {
        builder.append("?");
        builder
            .append(
                StringUtilities
                    .concat(
                        "&",
                        IterableUtilities
                            .asList(this.parameters.parameters())
                            .stream()
                            .map(p -> encode(p.getName()) + "=" + encode(p.getValue()))
                            .collect(Collectors.toList())));
      } else {
        builder.append("/");
      }
    } else {
      builder
          .append(
              StringUtilities
                  .concat(
                      "",
                      this.path.stream().map(p -> encodePath(p)).collect(Collectors.toList())));
      if (this.parameters.getNumberOfParameter() > 0) {
        builder.append("?");
        builder
            .append(
                StringUtilities
                    .concat(
                        "&",
                        IterableUtilities
                            .asList(this.parameters.parameters())
                            .stream()
                            .map(p -> encode(p.getName()) + "=" + encode(p.getValue()))
                            .collect(Collectors.toList())));
      }
    }
    if (this.fragment != null) {
      builder.append("#");
      builder.append(this.fragment);
    }
    return builder.toString();
  }

  private String encodePath(final String string) {
    try {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < string.length(); i++) {
        char c = string.charAt(i);
        if (c != '/') {
          encode(builder, c);
        } else {
          builder.append(c);
        }
      }
      return builder.toString();
    } catch (UnsupportedEncodingException exception) {
      return string;
    }
  }

  private String encode(final String string) {
    if (string == null) {
      return null;
    }
    try {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < string.length(); i++) {
        char c = string.charAt(i);
        encode(builder, c);
      }
      return builder.toString();
    } catch (UnsupportedEncodingException exception) {
      return string;
    }
  }

  private void encode(final StringBuilder builder, final char c) throws UnsupportedEncodingException {
    if (c != ' ') {
      builder.append(URLEncoder.encode(String.valueOf(c), "UTF-8"));
    } else {
      builder.append("%20");
    }
  }

  @Override
  public String decoded() {
    final StringBuilder builder = new StringBuilder();
    builder.append(StringUtilities.concat(":", this.scheme));
    if (Objects.equals(this.scheme, List.of("file"))) {
      builder.append(":/");
    } else {
      builder.append("://");
    }
    if (this.authority != null) {
      final IAuthentication authentication = getAuthentication();
      if (authentication != null) {
        builder.append(authentication.getUsername());
        builder.append(":");
        builder.append(authentication.getPassword());
        builder.append("@");
      }
      final IHost host = getHost();
      if (host != null) {
        builder.append(host.getName());
        if (host.getPort() > -1) {
          builder.append(":");
          builder.append(host.getPort());
        }
      }
    }
    if (this.path.isEmpty()) {
      if (this.parameters.getNumberOfParameter() > 0) {
        builder.append("?");
        builder
            .append(
                StringUtilities
                    .concat(
                        "&",
                        IterableUtilities
                            .asList(this.parameters.parameters())
                            .stream()
                            .map(p -> p.getName() + "=" + p.getValue())
                            .collect(Collectors.toList())));
      } else {
        builder.append("/");
      }
    } else {
      builder
          .append(
              StringUtilities
                  .concat(
                      "",
                      this.path.stream().map(p -> p).collect(Collectors.toList())));
      if (this.parameters.getNumberOfParameter() > 0) {
        builder.append("?");
        builder
            .append(
                StringUtilities
                    .concat(
                        "&",
                        IterableUtilities
                            .asList(this.parameters.parameters())
                            .stream()
                            .map(p -> p.getName() + "=" + p.getValue())
                            .collect(Collectors.toList())));
      }
    }
    if (this.fragment != null) {
      builder.append("#");
      builder.append(this.fragment);
    }
    return builder.toString();
  }
}
