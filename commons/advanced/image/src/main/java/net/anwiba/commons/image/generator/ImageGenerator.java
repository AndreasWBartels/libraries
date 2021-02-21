/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.image.generator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageGenerator implements IImageGenerator {

  public static BufferedImage of(final int width, final int height, final long seed) {
    return new ImageGenerator(seed, width, height).createImage(width, height);
  }

  private final Random random;
  private final int minimumImageEdgeSize;
  private final int maximumImageEdgeSize;

  public ImageGenerator(
      final long seed,
      final int minimumImageEdgeSize,
      final int maximumImageEdgeSize) {
    this.minimumImageEdgeSize = minimumImageEdgeSize;
    this.maximumImageEdgeSize = maximumImageEdgeSize;
    if (seed == -1) {
      this.random = new Random();
      return;
    }
    this.random = new Random(seed);
  }

  @Override
  public BufferedImage createImage() {
    return createImage(randomSize(), randomSize());
  }

  private int randomSize() {
    return this.random.nextInt(this.maximumImageEdgeSize - this.minimumImageEdgeSize) + this.minimumImageEdgeSize;
  }

  @Override
  public BufferedImage createImage(final int width, final int height) {
    final int imageType = BufferedImage.TYPE_4BYTE_ABGR;
    return createImage(width, height, imageType);
  }

  @Override
  public BufferedImage createImage(
      final int width,
      final int height,
      final int imageType) {
    final BufferedImage image = new BufferedImage(width, height, imageType);
    paint((Graphics2D) image.getGraphics(), 0, 0, width, height);
    return image;
  }

  private void paint(
      final Graphics2D g2d,
      final double x,
      final double y,
      final double width,
      final double height) {
    for (int i = 0; i < 1000; i++) {
      fillRandomCircle(g2d, x, y, width, height);
    }

    for (int i = 0; i < 100; i++) {
      drawRandomString(g2d, x, y, width, height);
    }
  }

  private void drawRandomString(
      final Graphics2D g2d,
      final double x,
      final double y,
      final double width,
      final double height) {
    setRandomColor(g2d);
    setRandomFont(g2d);
    final double theta = this.random.nextDouble() * 2 * Math.PI;
    final AffineTransform originalTransform = (AffineTransform) g2d.getTransform().clone();
    g2d.translate(x + width / 2, y + height / 2);
    g2d.rotate(theta);
    final float x0 = (float) (this.random.nextDouble() * width / 2);
    final int value = (int) (this.random.nextDouble() * Integer.MAX_VALUE);
    g2d.drawString(String.valueOf(value), x0, 0);
    g2d.setTransform(originalTransform);
  }

  private void setRandomFont(final Graphics2D g2d) {
    final float fontSize = (float) (this.random.nextDouble() * 36);
    g2d.setFont(g2d.getFont().deriveFont(fontSize));
  }

  private void fillRandomCircle(
      final Graphics2D g2d,
      final double x,
      final double y,
      final double width,
      final double height) {
    setRandomColor(g2d);
    final double x1 = x + this.random.nextDouble() * width;
    final double x2 = x + this.random.nextDouble() * width;
    final double y1 = y + this.random.nextDouble() * height;
    final double y2 = y + this.random.nextDouble() * height;
    final double minX = Math.min(x1, x2);
    final double maxX = Math.max(x1, x2);
    final double minY = Math.min(y1, y2);
    final double maxY = Math.max(y1, y2);
    g2d.fill(new Ellipse2D.Double(minX, minY, maxX - minX, maxY - minY));
  }

  private void setRandomColor(final Graphics2D g2d) {
    final float r = (float) this.random.nextDouble();
    final float g = (float) this.random.nextDouble();
    final float b = (float) this.random.nextDouble();
    final float a = (float) this.random.nextDouble();
    g2d.setColor(new Color(r, g, b, a));
  }

}
