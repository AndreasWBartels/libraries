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
package net.anwiba.spatial.coordinate;

public final class TargetEnvelopeCalculator {

  private final double envelopeScaleFactor;
  private final double minimumSizeScale;

  public TargetEnvelopeCalculator(final double targetEnvelopeFactor, final double notZoomInsideFactor) {
    this.envelopeScaleFactor = targetEnvelopeFactor;
    this.minimumSizeScale = notZoomInsideFactor;
  }

  private IEnvelope scale(final IEnvelope envelope, final double scaleFactor) {
    final double[] oldMin = envelope.getMinimum().getValues();
    final double[] oldMax = envelope.getMaximum().getValues();
    final double[] min = new double[oldMin.length];
    final double[] max = new double[oldMax.length];
    for (int i = 0; i < oldMin.length; i++) {
      final double oldDist = oldMax[i] - oldMin[i];
      final double avg = oldMin[i] + oldDist * 0.5;
      final double dist = oldDist * scaleFactor * 0.5;
      min[i] = avg - dist;
      max[i] = avg + dist;
    }
    return new Envelope(min, max, envelope.isMeasured());
  }

  public IEnvelope moveCenterTo(final IEnvelope envelope, final ICoordinate coordinate) {
    final double[] center = coordinate.getValues();
    final double[] oldMin = envelope.getMinimum().getValues();
    final double[] oldMax = envelope.getMaximum().getValues();
    final double[] min = new double[oldMin.length];
    final double[] max = new double[oldMax.length];
    for (int i = 0; i < 2; i++) {
      final double dist = (oldMax[i] - oldMin[i]) * 0.5;
      min[i] = center[i] - dist;
      max[i] = center[i] + dist;
    }
    return new Envelope(min, max, false);
  }

  public IEnvelope calculate(
      final IEnvelope currentEnvelope,
      final IEnvelope maximalEnvelope,
      final IEnvelope objectEnvelope,
      final boolean isMoveEnabled) {
    final IEnvelope targetEnvelope = scale(objectEnvelope, this.envelopeScaleFactor);
    if (currentEnvelope.contains(objectEnvelope)) {
      if (!isMoveEnabled
          && currentEnvelope.getWidth() * this.minimumSizeScale > objectEnvelope.getWidth()
          && currentEnvelope.getHeight() * this.minimumSizeScale > objectEnvelope.getHeight()) {
        return targetEnvelope;
      }
      return currentEnvelope;
    }
    if (isMoveEnabled) {
      return moveCenterTo(currentEnvelope, objectEnvelope.getCenterCoordinate());
    }
    if (!EnvelopeUtilities.isNullEnvelope(maximalEnvelope)
        && (targetEnvelope.getWidth() > maximalEnvelope.getWidth()
            || (targetEnvelope.getHeight() > maximalEnvelope.getWidth()) && maximalEnvelope.contains(objectEnvelope))) {
      return maximalEnvelope;
    }
    return targetEnvelope;
  }
}
