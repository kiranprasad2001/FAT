// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Composite;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.awt.geom.RoundRectangle2D;
import java.util.Hashtable;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.font.GlyphVector;
import java.awt.geom.Arc2D;
import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.Map;
import java.awt.Color;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public final class EscherGraphics2d extends Graphics2D
{
    private EscherGraphics _escherGraphics;
    private BufferedImage _img;
    private AffineTransform _trans;
    private Stroke _stroke;
    private Paint _paint;
    private Shape _deviceclip;
    private POILogger logger;
    
    public EscherGraphics2d(final EscherGraphics escherGraphics) {
        this.logger = POILogFactory.getLogger(this.getClass());
        this._escherGraphics = escherGraphics;
        this.setImg(new BufferedImage(1, 1, 2));
        this.setColor(Color.black);
    }
    
    @Override
    public void addRenderingHints(final Map<?, ?> map) {
        this.getG2D().addRenderingHints(map);
    }
    
    @Override
    public void clearRect(final int i, final int j, final int k, final int l) {
        final Paint paint1 = this.getPaint();
        this.setColor(this.getBackground());
        this.fillRect(i, j, k, l);
        this.setPaint(paint1);
    }
    
    @Override
    public void clip(Shape shape) {
        if (this.getDeviceclip() != null) {
            final Area area = new Area(this.getClip());
            if (shape != null) {
                area.intersect(new Area(shape));
            }
            shape = area;
        }
        this.setClip(shape);
    }
    
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        this.clip(new Rectangle(x, y, width, height));
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        this.getG2D().copyArea(x, y, width, height, dx, dy);
    }
    
    @Override
    public Graphics create() {
        final EscherGraphics2d g2d = new EscherGraphics2d(this._escherGraphics);
        return g2d;
    }
    
    @Override
    public void dispose() {
        this.getEscherGraphics().dispose();
        this.getG2D().dispose();
        this.getImg().flush();
    }
    
    @Override
    public void draw(final Shape shape) {
        if (shape instanceof Line2D) {
            final Line2D shape2d = (Line2D)shape;
            int width = 0;
            if (this._stroke != null && this._stroke instanceof BasicStroke) {
                width = (int)((BasicStroke)this._stroke).getLineWidth() * 12700;
            }
            this.drawLine((int)shape2d.getX1(), (int)shape2d.getY1(), (int)shape2d.getX2(), (int)shape2d.getY2(), width);
        }
        else if (this.logger.check(5)) {
            this.logger.log(5, "draw not fully supported");
        }
    }
    
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        this.draw(new Arc2D.Float((float)x, (float)y, (float)width, (float)height, (float)startAngle, (float)arcAngle, 0));
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        this.fill(g.getOutline(x, y));
    }
    
    @Override
    public boolean drawImage(final Image image, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgColor, final ImageObserver imageobserver) {
        if (this.logger.check(5)) {
            this.logger.log(5, "drawImage() not supported");
        }
        return true;
    }
    
    @Override
    public boolean drawImage(final Image image, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver imageobserver) {
        if (this.logger.check(5)) {
            this.logger.log(5, "drawImage() not supported");
        }
        return this.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, imageobserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int dx1, final int dy1, final int dx2, final int dy2, final Color bgColor, final ImageObserver imageobserver) {
        if (this.logger.check(5)) {
            this.logger.log(5, "drawImage() not supported");
        }
        return true;
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        return this.drawImage(img, x, y, width, height, null, observer);
    }
    
    @Override
    public boolean drawImage(final Image image, final int x, final int y, final Color bgColor, final ImageObserver imageobserver) {
        return this.drawImage(image, x, y, image.getWidth(imageobserver), image.getHeight(imageobserver), bgColor, imageobserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int x, final int y, final ImageObserver imageobserver) {
        return this.drawImage(image, x, y, image.getWidth(imageobserver), image.getHeight(imageobserver), imageobserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final AffineTransform affinetransform, final ImageObserver imageobserver) {
        final AffineTransform affinetransform2 = (AffineTransform)this.getTrans().clone();
        this.getTrans().concatenate(affinetransform);
        this.drawImage(image, 0, 0, imageobserver);
        this.setTrans(affinetransform2);
        return true;
    }
    
    @Override
    public void drawImage(final BufferedImage bufferedimage, final BufferedImageOp op, final int x, final int y) {
        final BufferedImage img = op.filter(bufferedimage, null);
        this.drawImage(img, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, (float)x, (float)y), null);
    }
    
    public void drawLine(final int x1, final int y1, final int x2, final int y2, final int width) {
        this.getEscherGraphics().drawLine(x1, y1, x2, y2, width);
    }
    
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        int width = 0;
        if (this._stroke != null && this._stroke instanceof BasicStroke) {
            width = (int)((BasicStroke)this._stroke).getLineWidth() * 12700;
        }
        this.getEscherGraphics().drawLine(x1, y1, x2, y2, width);
    }
    
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        this.getEscherGraphics().drawOval(x, y, width, height);
    }
    
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        this.getEscherGraphics().drawPolygon(xPoints, yPoints, nPoints);
    }
    
    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        if (nPoints > 0) {
            final GeneralPath generalpath = new GeneralPath();
            generalpath.moveTo((float)xPoints[0], (float)yPoints[0]);
            for (int j = 1; j < nPoints; ++j) {
                generalpath.lineTo((float)xPoints[j], (float)yPoints[j]);
            }
            this.draw(generalpath);
        }
    }
    
    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        this._escherGraphics.drawRect(x, y, width, height);
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage renderableimage, final AffineTransform affinetransform) {
        this.drawRenderedImage(renderableimage.createDefaultRendering(), affinetransform);
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage renderedimage, final AffineTransform affinetransform) {
        final BufferedImage bufferedimage = new BufferedImage(renderedimage.getColorModel(), renderedimage.getData().createCompatibleWritableRaster(), false, null);
        bufferedimage.setData(renderedimage.getData());
        this.drawImage(bufferedimage, affinetransform, null);
    }
    
    @Override
    public void drawRoundRect(final int i, final int j, final int k, final int l, final int i1, final int j1) {
        this.draw(new RoundRectangle2D.Float((float)i, (float)j, (float)k, (float)l, (float)i1, (float)j1));
    }
    
    @Override
    public void drawString(final String string, final float x, final float y) {
        this.getEscherGraphics().drawString(string, (int)x, (int)y);
    }
    
    @Override
    public void drawString(final String string, final int x, final int y) {
        this.getEscherGraphics().drawString(string, x, y);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedcharacteriterator, final float x, final float y) {
        final TextLayout textlayout = new TextLayout(attributedcharacteriterator, this.getFontRenderContext());
        final Paint paint1 = this.getPaint();
        this.setColor(this.getColor());
        this.fill(textlayout.getOutline(AffineTransform.getTranslateInstance(x, y)));
        this.setPaint(paint1);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedcharacteriterator, final int x, final int y) {
        this.getEscherGraphics().drawString(attributedcharacteriterator, x, y);
    }
    
    @Override
    public void fill(final Shape shape) {
        if (this.logger.check(5)) {
            this.logger.log(5, "fill(Shape) not supported");
        }
    }
    
    @Override
    public void fillArc(final int i, final int j, final int k, final int l, final int i1, final int j1) {
        this.fill(new Arc2D.Float((float)i, (float)j, (float)k, (float)l, (float)i1, (float)j1, 2));
    }
    
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        this._escherGraphics.fillOval(x, y, width, height);
    }
    
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        this._escherGraphics.fillPolygon(xPoints, yPoints, nPoints);
    }
    
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        this.getEscherGraphics().fillRect(x, y, width, height);
    }
    
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        this.fill(new RoundRectangle2D.Float((float)x, (float)y, (float)width, (float)height, (float)arcWidth, (float)arcHeight));
    }
    
    @Override
    public Color getBackground() {
        return this.getEscherGraphics().getBackground();
    }
    
    @Override
    public Shape getClip() {
        try {
            return this.getTrans().createInverse().createTransformedShape(this.getDeviceclip());
        }
        catch (Exception _ex) {
            return null;
        }
    }
    
    @Override
    public Rectangle getClipBounds() {
        if (this.getDeviceclip() != null) {
            return this.getClip().getBounds();
        }
        return null;
    }
    
    @Override
    public Color getColor() {
        return this._escherGraphics.getColor();
    }
    
    @Override
    public Composite getComposite() {
        return this.getG2D().getComposite();
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.getG2D().getDeviceConfiguration();
    }
    
    @Override
    public Font getFont() {
        return this.getEscherGraphics().getFont();
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return this.getEscherGraphics().getFontMetrics(font);
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        this.getG2D().setTransform(this.getTrans());
        return this.getG2D().getFontRenderContext();
    }
    
    @Override
    public Paint getPaint() {
        return this._paint;
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key key) {
        return this.getG2D().getRenderingHint(key);
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this.getG2D().getRenderingHints();
    }
    
    @Override
    public Stroke getStroke() {
        return this._stroke;
    }
    
    @Override
    public AffineTransform getTransform() {
        return (AffineTransform)this.getTrans().clone();
    }
    
    @Override
    public boolean hit(final Rectangle rectangle, final Shape shape, final boolean flag) {
        this.getG2D().setTransform(this.getTrans());
        this.getG2D().setStroke(this.getStroke());
        this.getG2D().setClip(this.getClip());
        return this.getG2D().hit(rectangle, shape, flag);
    }
    
    @Override
    public void rotate(final double d) {
        this.getTrans().rotate(d);
    }
    
    @Override
    public void rotate(final double d, final double d1, final double d2) {
        this.getTrans().rotate(d, d1, d2);
    }
    
    @Override
    public void scale(final double d, final double d1) {
        this.getTrans().scale(d, d1);
    }
    
    @Override
    public void setBackground(final Color c) {
        this.getEscherGraphics().setBackground(c);
    }
    
    @Override
    public void setClip(final int i, final int j, final int k, final int l) {
        this.setClip(new Rectangle(i, j, k, l));
    }
    
    @Override
    public void setClip(final Shape shape) {
        this.setDeviceclip(this.getTrans().createTransformedShape(shape));
    }
    
    @Override
    public void setColor(final Color c) {
        this._escherGraphics.setColor(c);
    }
    
    @Override
    public void setComposite(final Composite composite) {
        this.getG2D().setComposite(composite);
    }
    
    @Override
    public void setFont(final Font font) {
        this.getEscherGraphics().setFont(font);
    }
    
    @Override
    public void setPaint(final Paint paint1) {
        if (paint1 != null) {
            this._paint = paint1;
            if (paint1 instanceof Color) {
                this.setColor((Color)paint1);
            }
        }
    }
    
    @Override
    public void setPaintMode() {
        this.getEscherGraphics().setPaintMode();
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key key, final Object obj) {
        this.getG2D().setRenderingHint(key, obj);
    }
    
    @Override
    public void setRenderingHints(final Map<?, ?> map) {
        this.getG2D().setRenderingHints(map);
    }
    
    @Override
    public void setStroke(final Stroke s) {
        this._stroke = s;
    }
    
    @Override
    public void setTransform(final AffineTransform affinetransform) {
        this.setTrans((AffineTransform)affinetransform.clone());
    }
    
    @Override
    public void setXORMode(final Color color1) {
        this.getEscherGraphics().setXORMode(color1);
    }
    
    @Override
    public void shear(final double d, final double d1) {
        this.getTrans().shear(d, d1);
    }
    
    @Override
    public void transform(final AffineTransform affinetransform) {
        this.getTrans().concatenate(affinetransform);
    }
    
    @Override
    public void translate(final double d, final double d1) {
        this.getTrans().translate(d, d1);
    }
    
    @Override
    public void translate(final int i, final int j) {
        this.getTrans().translate(i, j);
    }
    
    private EscherGraphics getEscherGraphics() {
        return this._escherGraphics;
    }
    
    private BufferedImage getImg() {
        return this._img;
    }
    
    private void setImg(final BufferedImage img) {
        this._img = img;
    }
    
    private Graphics2D getG2D() {
        return (Graphics2D)this._img.getGraphics();
    }
    
    private AffineTransform getTrans() {
        return this._trans;
    }
    
    private void setTrans(final AffineTransform trans) {
        this._trans = trans;
    }
    
    private Shape getDeviceclip() {
        return this._deviceclip;
    }
    
    private void setDeviceclip(final Shape deviceclip) {
        this._deviceclip = deviceclip;
    }
}
