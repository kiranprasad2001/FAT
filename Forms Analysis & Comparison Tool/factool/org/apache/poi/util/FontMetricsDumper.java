// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.awt.FontMetrics;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Properties;

public class FontMetricsDumper
{
    public static void main(final String[] args) throws IOException {
        final Properties props = new Properties();
        final Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (int i = 0; i < allFonts.length; ++i) {
            final String fontName = allFonts[i].getFontName();
            final Font font = new Font(fontName, 1, 10);
            final FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
            final int fontHeight = fontMetrics.getHeight();
            props.setProperty("font." + fontName + ".height", fontHeight + "");
            final StringBuffer characters = new StringBuffer();
            for (char c = 'a'; c <= 'z'; ++c) {
                characters.append(c + ", ");
            }
            for (char c = 'A'; c <= 'Z'; ++c) {
                characters.append(c + ", ");
            }
            for (char c = '0'; c <= '9'; ++c) {
                characters.append(c + ", ");
            }
            final StringBuffer widths = new StringBuffer();
            for (char c2 = 'a'; c2 <= 'z'; ++c2) {
                widths.append(fontMetrics.getWidths()[c2] + ", ");
            }
            for (char c2 = 'A'; c2 <= 'Z'; ++c2) {
                widths.append(fontMetrics.getWidths()[c2] + ", ");
            }
            for (char c2 = '0'; c2 <= '9'; ++c2) {
                widths.append(fontMetrics.getWidths()[c2] + ", ");
            }
            props.setProperty("font." + fontName + ".characters", characters.toString());
            props.setProperty("font." + fontName + ".widths", widths.toString());
        }
        final FileOutputStream fileOut = new FileOutputStream("font_metrics.properties");
        try {
            props.store(fileOut, "Font Metrics");
        }
        finally {
            fileOut.close();
        }
    }
}
