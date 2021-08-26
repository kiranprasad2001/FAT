// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.GraphicsConfiguration;
import java.awt.Composite;
import java.awt.Color;
import java.text.AttributedCharacterIterator;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.ImageObserver;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import java.awt.image.BufferedImageOp;
import java.awt.font.GlyphVector;
import java.awt.Shape;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class DummyGraphics2d extends Graphics2D
{
    BufferedImage img;
    private Graphics2D g2D;
    
    public DummyGraphics2d() {
        this.img = new BufferedImage(1000, 1000, 2);
        this.g2D = (Graphics2D)this.img.getGraphics();
    }
    
    @Override
    public void addRenderingHints(final Map<?, ?> hints) {
        System.out.println("addRenderingHinds(Map):");
        System.out.println("  hints = " + hints);
        this.g2D.addRenderingHints(hints);
    }
    
    @Override
    public void clip(final Shape s) {
        System.out.println("clip(Shape):");
        System.out.println("  s = " + s);
        this.g2D.clip(s);
    }
    
    @Override
    public void draw(final Shape s) {
        System.out.println("draw(Shape):");
        System.out.println("s = " + s);
        this.g2D.draw(s);
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        System.out.println("drawGlyphVector(GlyphVector, float, float):");
        System.out.println("g = " + g);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawGlyphVector(g, x, y);
    }
    
    @Override
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        System.out.println("drawImage(BufferedImage, BufferedImageOp, x, y):");
        System.out.println("img = " + img);
        System.out.println("op = " + op);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawImage(img, op, x, y);
    }
    
    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        System.out.println("drawImage(Image,AfflineTransform,ImageObserver):");
        System.out.println("img = " + img);
        System.out.println("xform = " + xform);
        System.out.println("obs = " + obs);
        return this.g2D.drawImage(img, xform, obs);
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
        System.out.println("drawRenderableImage(RenderableImage, AfflineTransform):");
        System.out.println("img = " + img);
        System.out.println("xform = " + xform);
        this.g2D.drawRenderableImage(img, xform);
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
        System.out.println("drawRenderedImage(RenderedImage, AffineTransform):");
        System.out.println("img = " + img);
        System.out.println("xform = " + xform);
        this.g2D.drawRenderedImage(img, xform);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
        System.out.println("drawString(AttributedCharacterIterator):");
        System.out.println("iterator = " + iterator);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawString(iterator, x, y);
    }
    
    @Override
    public void drawString(final String s, final float x, final float y) {
        System.out.println("drawString(s,x,y):");
        System.out.println("s = " + s);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawString(s, x, y);
    }
    
    @Override
    public void fill(final Shape s) {
        System.out.println("fill(Shape):");
        System.out.println("s = " + s);
        this.g2D.fill(s);
    }
    
    @Override
    public Color getBackground() {
        System.out.println("getBackground():");
        return this.g2D.getBackground();
    }
    
    @Override
    public Composite getComposite() {
        System.out.println("getComposite():");
        return this.g2D.getComposite();
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        System.out.println("getDeviceConfiguration():");
        return this.g2D.getDeviceConfiguration();
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        System.out.println("getFontRenderContext():");
        return this.g2D.getFontRenderContext();
    }
    
    @Override
    public Paint getPaint() {
        System.out.println("getPaint():");
        return this.g2D.getPaint();
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key hintKey) {
        System.out.println("getRenderingHint(RenderingHints.Key):");
        System.out.println("hintKey = " + hintKey);
        return this.g2D.getRenderingHint(hintKey);
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        System.out.println("getRenderingHints():");
        return this.g2D.getRenderingHints();
    }
    
    @Override
    public Stroke getStroke() {
        System.out.println("getStroke():");
        return this.g2D.getStroke();
    }
    
    @Override
    public AffineTransform getTransform() {
        System.out.println("getTransform():");
        return this.g2D.getTransform();
    }
    
    @Override
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
        System.out.println("hit(Rectangle, Shape, onStroke):");
        System.out.println("rect = " + rect);
        System.out.println("s = " + s);
        System.out.println("onStroke = " + onStroke);
        return this.g2D.hit(rect, s, onStroke);
    }
    
    @Override
    public void rotate(final double theta) {
        System.out.println("rotate(theta):");
        System.out.println("theta = " + theta);
        this.g2D.rotate(theta);
    }
    
    @Override
    public void rotate(final double theta, final double x, final double y) {
        System.out.println("rotate(double,double,double):");
        System.out.println("theta = " + theta);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.rotate(theta, x, y);
    }
    
    @Override
    public void scale(final double sx, final double sy) {
        System.out.println("scale(double,double):");
        System.out.println("sx = " + sx);
        System.out.println("sy");
        this.g2D.scale(sx, sy);
    }
    
    @Override
    public void setBackground(final Color color) {
        System.out.println("setBackground(Color):");
        System.out.println("color = " + color);
        this.g2D.setBackground(color);
    }
    
    @Override
    public void setComposite(final Composite comp) {
        System.out.println("setComposite(Composite):");
        System.out.println("comp = " + comp);
        this.g2D.setComposite(comp);
    }
    
    @Override
    public void setPaint(final Paint paint) {
        System.out.println("setPain(Paint):");
        System.out.println("paint = " + paint);
        this.g2D.setPaint(paint);
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key hintKey, final Object hintValue) {
        System.out.println("setRenderingHint(RenderingHints.Key, Object):");
        System.out.println("hintKey = " + hintKey);
        System.out.println("hintValue = " + hintValue);
        this.g2D.setRenderingHint(hintKey, hintValue);
    }
    
    @Override
    public void setRenderingHints(final Map<?, ?> hints) {
        System.out.println("setRenderingHints(Map):");
        System.out.println("hints = " + hints);
        this.g2D.setRenderingHints(hints);
    }
    
    @Override
    public void setStroke(final Stroke s) {
        System.out.println("setStroke(Stoke):");
        System.out.println("s = " + s);
        this.g2D.setStroke(s);
    }
    
    @Override
    public void setTransform(final AffineTransform Tx) {
        System.out.println("setTransform():");
        System.out.println("Tx = " + Tx);
        this.g2D.setTransform(Tx);
    }
    
    @Override
    public void shear(final double shx, final double shy) {
        System.out.println("shear(shx, dhy):");
        System.out.println("shx = " + shx);
        System.out.println("shy = " + shy);
        this.g2D.shear(shx, shy);
    }
    
    @Override
    public void transform(final AffineTransform Tx) {
        System.out.println("transform(AffineTransform):");
        System.out.println("Tx = " + Tx);
        this.g2D.transform(Tx);
    }
    
    @Override
    public void translate(final double tx, final double ty) {
        System.out.println("translate(double, double):");
        System.out.println("tx = " + tx);
        System.out.println("ty = " + ty);
        this.g2D.translate(tx, ty);
    }
    
    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        System.out.println("clearRect(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.clearRect(x, y, width, height);
    }
    
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        System.out.println("clipRect(int, int, int, int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.clipRect(x, y, width, height);
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        System.out.println("copyArea(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.copyArea(x, y, width, height, dx, dy);
    }
    
    @Override
    public Graphics create() {
        System.out.println("create():");
        return this.g2D.create();
    }
    
    @Override
    public Graphics create(final int x, final int y, final int width, final int height) {
        System.out.println("create(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        return this.g2D.create(x, y, width, height);
    }
    
    @Override
    public void dispose() {
        System.out.println("dispose():");
        this.g2D.dispose();
    }
    
    @Override
    public void draw3DRect(final int x, final int y, final int width, final int height, final boolean raised) {
        System.out.println("draw3DRect(int,int,int,int,boolean):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("raised = " + raised);
        this.g2D.draw3DRect(x, y, width, height, raised);
    }
    
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        System.out.println("drawArc(int,int,int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("startAngle = " + startAngle);
        System.out.println("arcAngle = " + arcAngle);
        this.g2D.drawArc(x, y, width, height, startAngle, arcAngle);
    }
    
    @Override
    public void drawBytes(final byte[] data, final int offset, final int length, final int x, final int y) {
        System.out.println("drawBytes(byte[],int,int,int,int):");
        System.out.println("data = " + data);
        System.out.println("offset = " + offset);
        System.out.println("length = " + length);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawBytes(data, offset, length, x, y);
    }
    
    @Override
    public void drawChars(final char[] data, final int offset, final int length, final int x, final int y) {
        System.out.println("drawChars(data,int,int,int,int):");
        System.out.println("data = " + data.toString());
        System.out.println("offset = " + offset);
        System.out.println("length = " + length);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawChars(data, offset, length, x, y);
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,int,int,int,int,int,int,ImageObserver):");
        System.out.println("img = " + img);
        System.out.println("dx1 = " + dx1);
        System.out.println("dy1 = " + dy1);
        System.out.println("dx2 = " + dx2);
        System.out.println("dy2 = " + dy2);
        System.out.println("sx1 = " + sx1);
        System.out.println("sy1 = " + sy1);
        System.out.println("sx2 = " + sx2);
        System.out.println("sy2 = " + sy2);
        System.out.println("observer = " + observer);
        return this.g2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,int,int,int,int,int,int,Color,ImageObserver):");
        System.out.println("img = " + img);
        System.out.println("dx1 = " + dx1);
        System.out.println("dy1 = " + dy1);
        System.out.println("dx2 = " + dx2);
        System.out.println("dy2 = " + dy2);
        System.out.println("sx1 = " + sx1);
        System.out.println("sy1 = " + sy1);
        System.out.println("sx2 = " + sx2);
        System.out.println("sy2 = " + sy2);
        System.out.println("bgcolor = " + bgcolor);
        System.out.println("observer = " + observer);
        return this.g2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,Color,ImageObserver):");
        System.out.println("img = " + img);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("bgcolor = " + bgcolor);
        System.out.println("observer = " + observer);
        return this.g2D.drawImage(img, x, y, bgcolor, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,observer):");
        System.out.println("img = " + img);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("observer = " + observer);
        return this.g2D.drawImage(img, x, y, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,int,int,Color,ImageObserver):");
        System.out.println("img = " + img);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("bgcolor = " + bgcolor);
        System.out.println("observer = " + observer);
        return this.g2D.drawImage(img, x, y, width, height, bgcolor, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,width,height,observer):");
        System.out.println("img = " + img);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("observer = " + observer);
        return this.g2D.drawImage(img, x, y, width, height, observer);
    }
    
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        System.out.println("drawLine(int,int,int,int):");
        System.out.println("x1 = " + x1);
        System.out.println("y1 = " + y1);
        System.out.println("x2 = " + x2);
        System.out.println("y2 = " + y2);
        this.g2D.drawLine(x1, y1, x2, y2);
    }
    
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        System.out.println("drawOval(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.drawOval(x, y, width, height);
    }
    
    @Override
    public void drawPolygon(final Polygon p) {
        System.out.println("drawPolygon(Polygon):");
        System.out.println("p = " + p);
        this.g2D.drawPolygon(p);
    }
    
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        System.out.println("drawPolygon(int[],int[],int):");
        System.out.println("xPoints = " + xPoints);
        System.out.println("yPoints = " + yPoints);
        System.out.println("nPoints = " + nPoints);
        this.g2D.drawPolygon(xPoints, yPoints, nPoints);
    }
    
    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        System.out.println("drawPolyline(int[],int[],int):");
        System.out.println("xPoints = " + xPoints);
        System.out.println("yPoints = " + yPoints);
        System.out.println("nPoints = " + nPoints);
        this.g2D.drawPolyline(xPoints, yPoints, nPoints);
    }
    
    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        System.out.println("drawRect(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.drawRect(x, y, width, height);
    }
    
    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        System.out.println("drawRoundRect(int,int,int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("arcWidth = " + arcWidth);
        System.out.println("arcHeight = " + arcHeight);
        this.g2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        System.out.println("drawString(AttributedCharacterIterator,int,int):");
        System.out.println("iterator = " + iterator);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawString(iterator, x, y);
    }
    
    @Override
    public void drawString(final String str, final int x, final int y) {
        System.out.println("drawString(str,int,int):");
        System.out.println("str = " + str);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.drawString(str, x, y);
    }
    
    @Override
    public void fill3DRect(final int x, final int y, final int width, final int height, final boolean raised) {
        System.out.println("fill3DRect(int,int,int,int,boolean):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("raised = " + raised);
        this.g2D.fill3DRect(x, y, width, height, raised);
    }
    
    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        System.out.println("fillArc(int,int,int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("startAngle = " + startAngle);
        System.out.println("arcAngle = " + arcAngle);
        this.g2D.fillArc(x, y, width, height, startAngle, arcAngle);
    }
    
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        System.out.println("fillOval(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.fillOval(x, y, width, height);
    }
    
    @Override
    public void fillPolygon(final Polygon p) {
        System.out.println("fillPolygon(Polygon):");
        System.out.println("p = " + p);
        this.g2D.fillPolygon(p);
    }
    
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        System.out.println("fillPolygon(int[],int[],int):");
        System.out.println("xPoints = " + xPoints);
        System.out.println("yPoints = " + yPoints);
        System.out.println("nPoints = " + nPoints);
        this.g2D.fillPolygon(xPoints, yPoints, nPoints);
    }
    
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        System.out.println("fillRect(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.fillRect(x, y, width, height);
    }
    
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        System.out.println("fillRoundRect(int,int,int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    @Override
    public void finalize() {
        System.out.println("finalize():");
        this.g2D.finalize();
        super.finalize();
    }
    
    @Override
    public Shape getClip() {
        System.out.println("getClip():");
        return this.g2D.getClip();
    }
    
    @Override
    public Rectangle getClipBounds() {
        System.out.println("getClipBounds():");
        return this.g2D.getClipBounds();
    }
    
    @Override
    public Rectangle getClipBounds(final Rectangle r) {
        System.out.println("getClipBounds(Rectangle):");
        System.out.println("r = " + r);
        return this.g2D.getClipBounds(r);
    }
    
    @Override
    public Rectangle getClipRect() {
        System.out.println("getClipRect():");
        return this.g2D.getClipRect();
    }
    
    @Override
    public Color getColor() {
        System.out.println("getColor():");
        return this.g2D.getColor();
    }
    
    @Override
    public Font getFont() {
        System.out.println("getFont():");
        return this.g2D.getFont();
    }
    
    @Override
    public FontMetrics getFontMetrics() {
        System.out.println("getFontMetrics():");
        return this.g2D.getFontMetrics();
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        System.out.println("getFontMetrics():");
        return this.g2D.getFontMetrics(f);
    }
    
    @Override
    public boolean hitClip(final int x, final int y, final int width, final int height) {
        System.out.println("hitClip(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        return this.g2D.hitClip(x, y, width, height);
    }
    
    @Override
    public void setClip(final Shape clip) {
        System.out.println("setClip(Shape):");
        System.out.println("clip = " + clip);
        this.g2D.setClip(clip);
    }
    
    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        System.out.println("setClip(int,int,int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        this.g2D.setClip(x, y, width, height);
    }
    
    @Override
    public void setColor(final Color c) {
        System.out.println("setColor():");
        System.out.println("c = " + c);
        this.g2D.setColor(c);
    }
    
    @Override
    public void setFont(final Font font) {
        System.out.println("setFont(Font):");
        System.out.println("font = " + font);
        this.g2D.setFont(font);
    }
    
    @Override
    public void setPaintMode() {
        System.out.println("setPaintMode():");
        this.g2D.setPaintMode();
    }
    
    @Override
    public void setXORMode(final Color c1) {
        System.out.println("setXORMode(Color):");
        System.out.println("c1 = " + c1);
        this.g2D.setXORMode(c1);
    }
    
    @Override
    public String toString() {
        System.out.println("toString():");
        return this.g2D.toString();
    }
    
    @Override
    public void translate(final int x, final int y) {
        System.out.println("translate(int,int):");
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        this.g2D.translate(x, y);
    }
}
