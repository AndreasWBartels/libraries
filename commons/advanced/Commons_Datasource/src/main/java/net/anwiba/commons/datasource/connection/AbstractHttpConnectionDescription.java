/*
 * #%L
 * *
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
package net.anwiba.commons.datasource.connection;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Objects;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.string.StringUtilities;

@SuppressWarnings("nls")
public abstract class AbstractHttpConnectionDescription extends AbstractConnectionDescription
    implements
    IHttpConnectionDescription {

  private static final long serialVersionUID = 1L;

  private final String host;
  private final int port;
  private final String path;
  private final String userName;
  private final String password;
  private final boolean sslEnabled;
  private final IParameters parameters;
  private final int hashCode;

  public AbstractHttpConnectionDescription(
      final String host,
      final int port,
      final String path,
      final String userName,
      final String password,
      final IParameters parameters,
      final boolean sslEnabled) {
    super(DataSourceType.SERVICE);
    this.host = host;
    this.port = port;
    this.path = path;
    this.userName = userName;
    this.password = password;
    this.parameters = parameters;
    this.sslEnabled = sslEnabled;
    this.hashCode = createHashCode();
  }

  @Override
  public String getHost() {
    return this.host;
  }

  @Override
  public String getPath() {
    return this.path;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  @Override
  public String getUserName() {
    return this.userName;
  }

  @Override
  public IParameters getParameters() {
    return this.parameters;
  }

  @Override
  public URI getURI() {
    try {
      return new URI(getUrl());
    } catch (final URISyntaxException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public String getUrl() {
    return MessageFormat.format(
        "{0}://{1}{2}{3}", //$NON-NLS-1$
        this.sslEnabled ? "https" : "http", //$NON-NLS-1$//$NON-NLS-2$
        this.host,
        (this.port <= 0 ? "" : ":" + this.port), //$NON-NLS-1$//$NON-NLS-2$
        (this.path == null ? "/" : this.path.startsWith("/") ? this.path : "/" + this.path)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public String getUrlString() {
    return MessageFormat.format(
        "{0}://{1}{2}{3}{4}", //$NON-NLS-1$
        this.sslEnabled ? "https" : "http", //$NON-NLS-1$//$NON-NLS-2$
        this.host,
        (this.port <= 0 ? "" : ":" + this.port), //$NON-NLS-1$//$NON-NLS-2$
        (this.path == null ? "/" : this.path.startsWith("/") ? this.path : "/" + this.path), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        getQueryString());
  }

  private String getQueryString() {
    if (this.parameters.getNumberOfParameter() == 0) {
      return ""; //$NON-NLS-1$
    }
    final StringBuilder builder = new StringBuilder();
    final int indexOfQueryStartSymbol = this.path == null ? -1 : this.path.indexOf("?");
    if (indexOfQueryStartSymbol == -1) {
      builder.append("?");
    } else if (indexOfQueryStartSymbol < this.path.length() - 1) {
      builder.append("&");
    }
    boolean flag = false;
    for (final IParameter parameter : this.parameters.parameters()) {
      if (flag) {
        builder.append("&");
      }
      builder.append(parameter.getName());
      builder.append("=");
      builder.append(parameter.getValue());
      flag = true;
    }
    return builder.toString();
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  protected int createHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.host == null) ? 0 : this.host.toUpperCase().hashCode());
    result = prime * result + ((this.parameters == null) ? 0 : this.parameters.hashCode());
    result = prime * result + ((this.password == null) ? 0 : this.password.toUpperCase().hashCode());
    result = prime * result + ((this.path == null) ? 0 : this.path.toUpperCase().hashCode());
    result = prime * result + this.port;
    result = prime * result + (this.sslEnabled ? 1231 : 1237);
    result = prime * result + ((this.userName == null) ? 0 : this.userName.toUpperCase().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof IHttpConnectionDescription)) {
      return false;
    }
    final IHttpConnectionDescription other = (IHttpConnectionDescription) obj;
    return Objects.equals(getDataSourceType(), other.getDataSourceType()) //
        && this.port == other.getPort()//
        && StringUtilities.trimedAndUpperCaseEquals(this.host, other.getHost())
        && StringUtilities.trimedAndUpperCaseEquals(this.path, other.getPath())
        && StringUtilities.trimedAndUpperCaseEquals(this.userName, other.getUserName())
        && StringUtilities.trimedAndUpperCaseEquals(this.password, other.getPassword())
        && Objects.equals(getParameters(), other.getParameters())
        && isSslEnabled() == other.isSslEnabled();
  }

  @Override
  public boolean isSslEnabled() {
    return this.sslEnabled;
  }

  public String toString(final String name) {
    if (getUserName() != null) {
      return MessageFormat.format(
          "{0}({1}@{2}{3}{4}{5})", //$NON-NLS-1$
          name, //
          getUserName(), //
          this.host,
          (this.port <= 0 ? "" : ":" + this.port), //$NON-NLS-1$//$NON-NLS-2$
          (this.path == null ? "/" : this.path.startsWith("/") ? this.path : "/" + this.path), //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
          getQueryString() //
      );
    }
    return MessageFormat.format(
        "{0}({1}{2}{3}{4})", //$NON-NLS-1$
        name, //
        this.host,
        (this.port <= 0 ? "" : ":" + this.port), //$NON-NLS-1$//$NON-NLS-2$
        (this.path == null ? "/" : this.path.startsWith("/") ? this.path : "/" + this.path), //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        getQueryString() //
    );
  }
}
