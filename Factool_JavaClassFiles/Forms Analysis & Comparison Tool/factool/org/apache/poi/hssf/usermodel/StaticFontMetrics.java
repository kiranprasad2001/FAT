// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.awt.Font;
import java.util.Map;
import java.util.Properties;

final class StaticFontMetrics
{
    private static Properties fontMetricsProps;
    private static Map<String, FontDetails> fontDetailsMap;
    
    public static FontDetails getFontDetails(final Font font) {
        if (StaticFontMetrics.fontMetricsProps == null) {
            InputStream metricsIn = null;
            try {
                StaticFontMetrics.fontMetricsProps = new Properties();
                String propFileName = null;
                try {
                    propFileName = System.getProperty("font.metrics.filename");
                }
                catch (SecurityException ex) {}
                if (propFileName != null) {
                    final File file = new File(propFileName);
                    if (!file.exists()) {
                        throw new FileNotFoundException("font_metrics.properties not found at path " + file.getAbsolutePath());
                    }
                    metricsIn = new FileInputStream(file);
                }
                else {
                    metricsIn = FontDetails.class.getResourceAsStream("/font_metrics.properties");
                    if (metricsIn == null) {
                        throw new FileNotFoundException("font_metrics.properties not found in classpath");
                    }
                }
                StaticFontMetrics.fontMetricsProps.load(metricsIn);
            }
            catch (IOException e) {
                throw new RuntimeException("Could not load font metrics: " + e.getMessage());
            }
            finally {
                if (metricsIn != null) {
                    try {
                        metricsIn.close();
                    }
                    catch (IOException ex2) {}
                }
            }
        }
        String fontName = font.getName();
        String fontStyle = "";
        if (font.isPlain()) {
            fontStyle += "plain";
        }
        if (font.isBold()) {
            fontStyle += "bold";
        }
        if (font.isItalic()) {
            fontStyle += "italic";
        }
        if (StaticFontMetrics.fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName)) == null && StaticFontMetrics.fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName + "." + fontStyle)) != null) {
            fontName = fontName + "." + fontStyle;
        }
        if (StaticFontMetrics.fontDetailsMap.get(fontName) == null) {
            final FontDetails fontDetails = FontDetails.create(fontName, StaticFontMetrics.fontMetricsProps);
            StaticFontMetrics.fontDetailsMap.put(fontName, fontDetails);
            return fontDetails;
        }
        return StaticFontMetrics.fontDetailsMap.get(fontName);
    }
    
    static {
        StaticFontMetrics.fontDetailsMap = new HashMap<String, FontDetails>();
    }
}
