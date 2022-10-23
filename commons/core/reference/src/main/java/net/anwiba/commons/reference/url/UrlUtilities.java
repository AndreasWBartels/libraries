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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.reference.url;

import java.util.List;

import net.anwiba.commons.lang.parameter.IParameter;
import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.lang.parameter.Parameter;
import net.anwiba.commons.lang.stream.Streams;

public class UrlUtilities {

  public static IUrl decode(final IUrl url) {
    return new Url(
        decode(url.getScheme()),
        new Authority(decode(url.getAuthentication()), decode(url.getHost())),
        decode(url.getPath()),
        decode(url.getQuery()),
        decode(url.getFragment()));
  }

  private static List<IParameter> decode(final IParameters parameters) {
    return Streams.of(parameters.parameters()).convert(p -> decode(p)).asList();
  }

  private static IParameter decode(final IParameter parameter) {
    return Parameter.of(decode(parameter.getName()), decode(parameter.getValue()));
  }

  private static IAuthentication decode(final IAuthentication authentication) {
    return new Authentication(decode(authentication.getUsername()), decode(authentication.getPassword()));
  }

  private static List<String> decode(final List<String> list) {
    return list.stream().map(v -> decode(v)).toList();
  }

  private static String decode(final String v) {
    return v.replace(" ", "%20"); //$NON-NLS-1$//$NON-NLS-2$
  }

  public static IUrl encode(final IUrl url) {
    return new Url(
        encode(url.getScheme()),
        new Authority(encode(url.getAuthentication()), encode(url.getHost())),
        encode(url.getPath()),
        encode(url.getQuery()),
        encode(url.getFragment()));
  }

  private static IHost decode(final IHost host) {
    return new Host(decode(host.getName()), host.getPort());
  }

  private static IHost encode(final IHost host) {
    return new Host(encode(host.getName()), host.getPort());
  }

  private static List<IParameter> encode(final IParameters parameters) {
    return Streams.of(parameters.parameters()).convert(p -> encode(p)).asList();
  }

  private static IParameter encode(final IParameter parameter) {
    return Parameter.of(encode(parameter.getName()), encode(parameter.getValue()));
  }

  private static IAuthentication encode(final IAuthentication authentication) {
    return new Authentication(decode(authentication.getUsername()), decode(authentication.getPassword()));
  }

  private static List<String> encode(final List<String> list) {
    return list.stream().map(v -> decode(v)).toList();
  }

  private static String encode(final String v) {
    return v.replace("%20", " "); //$NON-NLS-1$//$NON-NLS-2$
  }

}
