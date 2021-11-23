/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.datasource.constaint;

import java.time.LocalDateTime;
import java.util.List;

import net.anwiba.commons.datasource.resource.IResourceDescription;
import net.anwiba.commons.utilities.time.UserDateTimeUtilities;

public class ResourceConstraint implements IResourceConstraint {

  private final IResourceDescription resourceDescription;
  private final ILicense constraint;
  private final LocalDateTime registered;
  private final String source;
  private final String usagePolicy;

  public ResourceConstraint(
      final IResourceDescription resourceDescription,
      final ILicense constraint,
      final String source) {
    this(resourceDescription, constraint, source, null, UserDateTimeUtilities.now());
  }

  public ResourceConstraint(
      final IResourceDescription resourceDescription,
      final ILicense constraint,
      final String source,
      final String usagePolicy,
      final LocalDateTime registered) {
    this.resourceDescription = resourceDescription;
    this.constraint = constraint;
    this.source = source;
    this.usagePolicy = usagePolicy;
    this.registered = registered;
  }

  @Override
  public ILicense getLicense() {
    return this.constraint;
  }

  @Override
  public IResourceDescription getResourceDescription() {
    return this.resourceDescription;
  }

  @Override
  public String getMaintainer() {
    return this.source;
  }

  @Override
  public String getUsagePolicy() {
    return this.usagePolicy;
  }

  @Override
  public LocalDateTime getRegistered() {
    return this.registered;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.constraint == null) ? 0 : this.constraint.hashCode());
    result = prime * result + ((this.resourceDescription == null) ? 0 : this.resourceDescription.hashCode());
    result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ResourceConstraint other = (ResourceConstraint) obj;
    if (this.constraint == null) {
      if (other.constraint != null) {
        return false;
      }
    } else if (!this.constraint.equals(other.constraint)) {
      return false;
    }
    if (this.resourceDescription == null) {
      if (other.resourceDescription != null) {
        return false;
      }
    } else if (!this.resourceDescription.equals(other.resourceDescription)) {
      return false;
    }
    if (this.source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!this.source.equals(other.source)) {
      return false;
    }
    return true;
  }

  public static IResourceConstraint of(
      final IResourceDescription resource,
      final String identifier,
      final String name,
      final String reference,
      final List<String> attributes, 
      final String source,
      final String usagePolicy,
      final LocalDateTime registered) {
    return new ResourceConstraint(resource, License.of(identifier, name, reference, attributes), source, usagePolicy, registered);
  }

  public static IResourceConstraint of(final IResourceDescription resource, final ILicense license) {
    return new ResourceConstraint(resource, license, null, null, UserDateTimeUtilities.now());
  }

  public static IResourceConstraint of(
      final IResourceDescription resource,
      final ILicense license,
      final String maintainer) {
    return new ResourceConstraint(resource, license, maintainer, null, UserDateTimeUtilities.now());
  }

  public static IResourceConstraint of(
      final IResourceDescription resource,
      final ILicense license,
      final String maintainer,
      final String usagePolicy) {
    return new ResourceConstraint(resource, license, maintainer, usagePolicy, UserDateTimeUtilities.now());
  }
}
