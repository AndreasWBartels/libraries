/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.image.graphic;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;

public abstract class AbstractGraphics implements IGraphics {

  private final Graphics2D graphics;

  public AbstractGraphics(final Graphics2D graphics) {
    this.graphics = graphics;
  }

  @Override
  public void draw(final Shape s) {
    this.graphics.draw(s);
  }

  @Override
  public void drawString(final String str, final int x, final int y) {
    this.graphics.drawString(str, x, y);;
  }

  @Override
  public void drawString(final String str, final float x, final float y) {
    this.graphics.drawString(str, x, y);;
  }

  @Override
  public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
    this.graphics.drawString(iterator, x, y);;
  }

  @Override
  public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
    this.graphics.drawString(iterator, x, y);;
  }

  @Override
  public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
    this.graphics.drawGlyphVector(g, x, y);;
  }

  @Override
  public void fill(final Shape s) {
    this.graphics.fill(s);
  }

  @Override
  public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
    return this.graphics.hit(rect, s, onStroke);
  }

  @Override
  public GraphicsConfiguration getDeviceConfiguration() {
    return this.graphics.getDeviceConfiguration();
  }

  @Override
  public void setComposite(final Composite comp) {
    this.graphics.setComposite(comp);
  }

  @Override
  public void setPaint(final Paint paint) {
    this.graphics.setPaint(paint);
  }

  @Override
  public void setStroke(final Stroke s) {
    this.graphics.setStroke(s);
  }

  @Override
  public Object getRenderingHint(final Key hintKey) {
    return this.graphics.getRenderingHint(hintKey);
  }

  @Override
  public RenderingHints getRenderingHints() {
    return this.graphics.getRenderingHints();
  }

  @Override
  public void translate(final int x, final int y) {
    this.graphics.translate(x, y);
  }

  @Override
  public void translate(final double tx, final double ty) {
    this.graphics.translate(tx, ty);
  }

  @Override
  public void rotate(final double theta) {
    this.graphics.rotate(theta);
  }

  @Override
  public void rotate(final double theta, final double x, final double y) {
    this.graphics.rotate(theta, x, y);
  }

  @Override
  public void scale(final double sx, final double sy) {
    this.graphics.scale(sx, sy);
  }

  @Override
  public void shear(final double shx, final double shy) {
    this.graphics.shear(shx, shy);
  }

  @Override
  public void transform(final AffineTransform Tx) {
    this.graphics.transform(Tx);
  }

  @Override
  public void setTransform(final AffineTransform Tx) {
    this.graphics.setTransform(Tx);
  }

  @Override
  public AffineTransform getTransform() {
    return this.graphics.getTransform();
  }

  @Override
  public Paint getPaint() {
    return this.graphics.getPaint();
  }

  @Override
  public Composite getComposite() {
    return this.graphics.getComposite();
  }

  @Override
  public void setBackground(final Color color) {
    this.graphics.setBackground(color);
  }

  @Override
  public Color getBackground() {
    return this.graphics.getBackground();
  }

  @Override
  public Stroke getStroke() {
    return this.graphics.getStroke();
  }

  @Override
  public void clip(final Shape s) {
    this.graphics.clip(s);
  }

  @Override
  public FontRenderContext getFontRenderContext() {
    return this.graphics.getFontRenderContext();
  }

  @Override
  public IGraphics create() {
    return new Graphic2DDecorator((Graphics2D) this.graphics.create());
  }

  @Override
  public Color getColor() {
    return this.graphics.getColor();
  }

  @Override
  public void setColor(final Color c) {
    this.graphics.setColor(c);
  }

  @Override
  public void setPaintMode() {
    this.graphics.setPaintMode();;
  }

  @Override
  public void setXORMode(final Color c1) {
    this.graphics.setXORMode(c1);
  }

  @Override
  public Font getFont() {
    return this.graphics.getFont();
  }

  @Override
  public void setFont(final Font font) {
    this.graphics.setFont(font);
  }

  @Override
  public FontMetrics getFontMetrics(final Font f) {
    return this.graphics.getFontMetrics(f);
  }

  @Override
  public Rectangle getClipBounds() {
    return this.graphics.getClipBounds();
  }

  @Override
  public void clipRect(final int x, final int y, final int width, final int height) {
    this.graphics.clipRect(x, y, width, height);
  }

  @Override
  public void setClip(final int x, final int y, final int width, final int height) {
    this.graphics.setClip(x, y, width, height);
  }

  @Override
  public Shape getClip() {
    return getClip();
  }

  @Override
  public void setClip(final Shape clip) {
    this.graphics.setClip(clip);
  }

  @Override
  public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
    this.graphics.copyArea(x, y, width, height, dx, dy);
  }

  @Override
  public void drawLine(final int x1, final int y1, final int x2, final int y2) {
    this.graphics.drawLine(x1, y1, x2, y2);
  }

  @Override
  public void fillRect(final int x, final int y, final int width, final int height) {
    this.graphics.fillRect(x, y, width, height);
  }

  @Override
  public void clearRect(final int x, final int y, final int width, final int height) {
    this.graphics.clearRect(x, y, width, height);
  }

  @Override
  public void drawRoundRect(
      final int x,
      final int y,
      final int width,
      final int height,
      final int arcWidth,
      final int arcHeight) {
    this.graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
  }

  @Override
  public void fillRoundRect(
      final int x,
      final int y,
      final int width,
      final int height,
      final int arcWidth,
      final int arcHeight) {
    this.graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
  }

  @Override
  public void drawOval(final int x, final int y, final int width, final int height) {
    this.graphics.drawOval(x, y, width, height);
  }

  @Override
  public void fillOval(final int x, final int y, final int width, final int height) {
    this.graphics.fillOval(x, y, width, height);
  }

  @Override
  public void drawArc(
      final int x,
      final int y,
      final int width,
      final int height,
      final int startAngle,
      final int arcAngle) {
    this.graphics.drawArc(x, y, width, height, startAngle, arcAngle);
  }

  @Override
  public void fillArc(
      final int x,
      final int y,
      final int width,
      final int height,
      final int startAngle,
      final int arcAngle) {
    this.graphics.fillArc(x, y, width, height, startAngle, arcAngle);
  }

  @Override
  public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
    this.graphics.drawPolyline(xPoints, yPoints, nPoints);
  }

  @Override
  public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
    this.graphics.drawPolygon(xPoints, yPoints, nPoints);
  }

  @Override
  public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
    this.graphics.fillPolygon(xPoints, yPoints, nPoints);
  }

  @Override
  public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
    return this.graphics.drawImage(img, xform, obs);
  }

  @Override
  public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
    this.graphics.drawImage(img, op, x, y);
  }

  @Override
  public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
    this.graphics.drawRenderedImage(img, xform);
  }

  @Override
  public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
    this.graphics.drawRenderableImage(img, xform);
  }

  @Override
  public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
    return this.graphics.drawImage(img, x, y, observer);
  }

  @Override
  public boolean drawImage(
      final Image img,
      final int x,
      final int y,
      final int width,
      final int height,
      final ImageObserver observer) {
    return this.graphics.drawImage(img, x, y, width, height, observer);
  }

  @Override
  public boolean drawImage(
      final Image img,
      final int x,
      final int y,
      final Color bgcolor,
      final ImageObserver observer) {
    return this.graphics.drawImage(img, x, y, bgcolor, observer);
  }

  @Override
  public boolean drawImage(
      final Image img,
      final int x,
      final int y,
      final int width,
      final int height,
      final Color bgcolor,
      final ImageObserver observer) {
    return this.graphics.drawImage(img, x, y, width, height, bgcolor, observer);
  }

  @Override
  public boolean drawImage(
      final Image img,
      final int dx1,
      final int dy1,
      final int dx2,
      final int dy2,
      final int sx1,
      final int sy1,
      final int sx2,
      final int sy2,
      final ImageObserver observer) {
    return this.graphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
  }

  @Override
  public boolean drawImage(
      final Image img,
      final int dx1,
      final int dy1,
      final int dx2,
      final int dy2,
      final int sx1,
      final int sy1,
      final int sx2,
      final int sy2,
      final Color bgcolor,
      final ImageObserver observer) {
    return this.graphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
  }

}
