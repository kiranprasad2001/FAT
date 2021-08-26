// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.POILogFactory;
import java.awt.Toolkit;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.text.AttributedCharacterIterator;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.RichTextString;
import java.awt.image.ImageObserver;
import java.awt.Image;
import org.apache.poi.util.POILogger;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;

public class EscherGraphics extends Graphics
{
    private HSSFShapeGroup escherGroup;
    private HSSFWorkbook workbook;
    private float verticalPointsPerPixel;
    private float verticalPixelsPerPoint;
    private Color foreground;
    private Color background;
    private Font font;
    private static POILogger logger;
    
    public EscherGraphics(final HSSFShapeGroup escherGroup, final HSSFWorkbook workbook, final Color forecolor, final float verticalPointsPerPixel) {
        this.verticalPointsPerPixel = 1.0f;
        this.background = Color.white;
        this.escherGroup = escherGroup;
        this.workbook = workbook;
        this.verticalPointsPerPixel = verticalPointsPerPixel;
        this.verticalPixelsPerPoint = 1.0f / verticalPointsPerPixel;
        this.font = new Font("Arial", 0, 10);
        this.foreground = forecolor;
    }
    
    EscherGraphics(final HSSFShapeGroup escherGroup, final HSSFWorkbook workbook, final Color foreground, final Font font, final float verticalPointsPerPixel) {
        this.verticalPointsPerPixel = 1.0f;
        this.background = Color.white;
        this.escherGroup = escherGroup;
        this.workbook = workbook;
        this.foreground = foreground;
        this.font = font;
        this.verticalPointsPerPixel = verticalPointsPerPixel;
        this.verticalPixelsPerPoint = 1.0f / verticalPointsPerPixel;
    }
    
    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        final Color color = this.foreground;
        this.setColor(this.background);
        this.fillRect(x, y, width, height);
        this.setColor(color);
    }
    
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "clipRect not supported");
        }
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "copyArea not supported");
        }
    }
    
    @Override
    public Graphics create() {
        final EscherGraphics g = new EscherGraphics(this.escherGroup, this.workbook, this.foreground, this.font, this.verticalPointsPerPixel);
        return g;
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "drawArc not supported");
        }
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "drawImage not supported");
        }
        return true;
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "drawImage not supported");
        }
        return true;
    }
    
    @Override
    public boolean drawImage(final Image image, final int i, final int j, final int k, final int l, final Color color, final ImageObserver imageobserver) {
        return this.drawImage(image, i, j, i + k, j + l, 0, 0, image.getWidth(imageobserver), image.getHeight(imageobserver), color, imageobserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int i, final int j, final int k, final int l, final ImageObserver imageobserver) {
        return this.drawImage(image, i, j, i + k, j + l, 0, 0, image.getWidth(imageobserver), image.getHeight(imageobserver), imageobserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int i, final int j, final Color color, final ImageObserver imageobserver) {
        return this.drawImage(image, i, j, image.getWidth(imageobserver), image.getHeight(imageobserver), color, imageobserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int i, final int j, final ImageObserver imageobserver) {
        return this.drawImage(image, i, j, image.getWidth(imageobserver), image.getHeight(imageobserver), imageobserver);
    }
    
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        this.drawLine(x1, y1, x2, y2, 0);
    }
    
    public void drawLine(final int x1, final int y1, final int x2, final int y2, final int width) {
        final HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x1, y1, x2, y2));
        shape.setShapeType(20);
        shape.setLineWidth(width);
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }
    
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        final HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(3);
        shape.setLineWidth(0);
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setNoFill(true);
    }
    
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int right = this.findBiggest(xPoints);
        final int bottom = this.findBiggest(yPoints);
        final int left = this.findSmallest(xPoints);
        final int top = this.findSmallest(yPoints);
        final HSSFPolygon shape = this.escherGroup.createPolygon(new HSSFChildAnchor(left, top, right, bottom));
        shape.setPolygonDrawArea(right - left, bottom - top);
        shape.setPoints(this.addToAll(xPoints, -left), this.addToAll(yPoints, -top));
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineWidth(0);
        shape.setNoFill(true);
    }
    
    private int[] addToAll(final int[] values, final int amount) {
        final int[] result = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = values[i] + amount;
        }
        return result;
    }
    
    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "drawPolyline not supported");
        }
    }
    
    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "drawRect not supported");
        }
    }
    
    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "drawRoundRect not supported");
        }
    }
    
    @Override
    public void drawString(final String str, final int x, int y) {
        if (str == null || str.equals("")) {
            return;
        }
        Font excelFont = this.font;
        if (this.font.getName().equals("SansSerif")) {
            excelFont = new Font("Arial", this.font.getStyle(), (int)(this.font.getSize() / this.verticalPixelsPerPoint));
        }
        else {
            excelFont = new Font(this.font.getName(), this.font.getStyle(), (int)(this.font.getSize() / this.verticalPixelsPerPoint));
        }
        final FontDetails d = StaticFontMetrics.getFontDetails(excelFont);
        final int width = d.getStringWidth(str) * 8 + 12;
        final int height = (int)(this.font.getSize() / this.verticalPixelsPerPoint + 6.0f) * 2;
        y -= (int)(this.font.getSize() / this.verticalPixelsPerPoint + 2.0f * this.verticalPixelsPerPoint);
        final HSSFTextbox textbox = this.escherGroup.createTextbox(new HSSFChildAnchor(x, y, x + width, y + height));
        textbox.setNoFill(true);
        textbox.setLineStyle(-1);
        final HSSFRichTextString s = new HSSFRichTextString(str);
        final HSSFFont hssfFont = this.matchFont(excelFont);
        s.applyFont(hssfFont);
        textbox.setString(s);
    }
    
    private HSSFFont matchFont(final Font font) {
        HSSFColor hssfColor = this.workbook.getCustomPalette().findColor((byte)this.foreground.getRed(), (byte)this.foreground.getGreen(), (byte)this.foreground.getBlue());
        if (hssfColor == null) {
            hssfColor = this.workbook.getCustomPalette().findSimilarColor((byte)this.foreground.getRed(), (byte)this.foreground.getGreen(), (byte)this.foreground.getBlue());
        }
        final boolean bold = (font.getStyle() & 0x1) != 0x0;
        final boolean italic = (font.getStyle() & 0x2) != 0x0;
        HSSFFont hssfFont = this.workbook.findFont((short)(bold ? 700 : 0), hssfColor.getIndex(), (short)(font.getSize() * 20), font.getName(), italic, false, (short)0, (byte)0);
        if (hssfFont == null) {
            hssfFont = this.workbook.createFont();
            hssfFont.setBoldweight((short)(bold ? 700 : 0));
            hssfFont.setColor(hssfColor.getIndex());
            hssfFont.setFontHeight((short)(font.getSize() * 20));
            hssfFont.setFontName(font.getName());
            hssfFont.setItalic(italic);
            hssfFont.setStrikeout(false);
            hssfFont.setTypeOffset((short)0);
            hssfFont.setUnderline((byte)0);
        }
        return hssfFont;
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "drawString not supported");
        }
    }
    
    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "fillArc not supported");
        }
    }
    
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        final HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(3);
        shape.setLineStyle(-1);
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setNoFill(false);
    }
    
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int right = this.findBiggest(xPoints);
        final int bottom = this.findBiggest(yPoints);
        final int left = this.findSmallest(xPoints);
        final int top = this.findSmallest(yPoints);
        final HSSFPolygon shape = this.escherGroup.createPolygon(new HSSFChildAnchor(left, top, right, bottom));
        shape.setPolygonDrawArea(right - left, bottom - top);
        shape.setPoints(this.addToAll(xPoints, -left), this.addToAll(yPoints, -top));
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }
    
    private int findBiggest(final int[] values) {
        int result = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; ++i) {
            if (values[i] > result) {
                result = values[i];
            }
        }
        return result;
    }
    
    private int findSmallest(final int[] values) {
        int result = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; ++i) {
            if (values[i] < result) {
                result = values[i];
            }
        }
        return result;
    }
    
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        final HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(1);
        shape.setLineStyle(-1);
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }
    
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "fillRoundRect not supported");
        }
    }
    
    @Override
    public Shape getClip() {
        return this.getClipBounds();
    }
    
    @Override
    public Rectangle getClipBounds() {
        return null;
    }
    
    @Override
    public Rectangle getClipRect() {
        return this.getClipBounds();
    }
    
    @Override
    public Color getColor() {
        return this.foreground;
    }
    
    @Override
    public Font getFont() {
        return this.font;
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return Toolkit.getDefaultToolkit().getFontMetrics(f);
    }
    
    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        this.setClip(new Rectangle(x, y, width, height));
    }
    
    @Override
    public void setClip(final Shape shape) {
    }
    
    @Override
    public void setColor(final Color color) {
        this.foreground = color;
    }
    
    @Override
    public void setFont(final Font f) {
        this.font = f;
    }
    
    @Override
    public void setPaintMode() {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "setPaintMode not supported");
        }
    }
    
    @Override
    public void setXORMode(final Color color) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "setXORMode not supported");
        }
    }
    
    @Override
    public void translate(final int x, final int y) {
        if (EscherGraphics.logger.check(5)) {
            EscherGraphics.logger.log(5, "translate not supported");
        }
    }
    
    public Color getBackground() {
        return this.background;
    }
    
    public void setBackground(final Color background) {
        this.background = background;
    }
    
    HSSFShapeGroup getEscherGraphics() {
        return this.escherGroup;
    }
    
    static {
        EscherGraphics.logger = POILogFactory.getLogger(EscherGraphics.class);
    }
}
