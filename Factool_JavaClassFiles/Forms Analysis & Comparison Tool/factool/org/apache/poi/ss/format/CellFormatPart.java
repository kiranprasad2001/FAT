// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.Iterator;
import org.apache.poi.hssf.util.HSSFColor;
import java.util.Comparator;
import java.util.TreeMap;
import javax.swing.JLabel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.awt.Color;

public class CellFormatPart
{
    private final Color color;
    private CellFormatCondition condition;
    private final CellFormatter format;
    private final CellFormatType type;
    private static final Map<String, Color> NAMED_COLORS;
    public static final Pattern COLOR_PAT;
    public static final Pattern CONDITION_PAT;
    public static final Pattern SPECIFICATION_PAT;
    public static final Pattern FORMAT_PAT;
    public static final int COLOR_GROUP;
    public static final int CONDITION_OPERATOR_GROUP;
    public static final int CONDITION_VALUE_GROUP;
    public static final int SPECIFICATION_GROUP;
    
    public CellFormatPart(final String desc) {
        final Matcher m = CellFormatPart.FORMAT_PAT.matcher(desc);
        if (!m.matches()) {
            throw new IllegalArgumentException("Unrecognized format: " + CellFormatter.quote(desc));
        }
        this.color = getColor(m);
        this.condition = this.getCondition(m);
        this.type = this.getCellFormatType(m);
        this.format = this.getFormatter(m);
    }
    
    public boolean applies(final Object valueObject) {
        if (this.condition != null && valueObject instanceof Number) {
            final Number num = (Number)valueObject;
            return this.condition.pass(num.doubleValue());
        }
        if (valueObject == null) {
            throw new NullPointerException("valueObject");
        }
        return true;
    }
    
    private static int findGroup(final Pattern pat, final String str, final String marker) {
        final Matcher m = pat.matcher(str);
        if (!m.find()) {
            throw new IllegalArgumentException("Pattern \"" + pat.pattern() + "\" doesn't match \"" + str + "\"");
        }
        for (int i = 1; i <= m.groupCount(); ++i) {
            final String grp = m.group(i);
            if (grp != null && grp.equals(marker)) {
                return i;
            }
        }
        throw new IllegalArgumentException("\"" + marker + "\" not found in \"" + pat.pattern() + "\"");
    }
    
    private static Color getColor(final Matcher m) {
        final String cdesc = m.group(CellFormatPart.COLOR_GROUP);
        if (cdesc == null || cdesc.length() == 0) {
            return null;
        }
        final Color c = CellFormatPart.NAMED_COLORS.get(cdesc);
        if (c == null) {
            CellFormatter.logger.warning("Unknown color: " + CellFormatter.quote(cdesc));
        }
        return c;
    }
    
    private CellFormatCondition getCondition(final Matcher m) {
        final String mdesc = m.group(CellFormatPart.CONDITION_OPERATOR_GROUP);
        if (mdesc == null || mdesc.length() == 0) {
            return null;
        }
        return CellFormatCondition.getInstance(m.group(CellFormatPart.CONDITION_OPERATOR_GROUP), m.group(CellFormatPart.CONDITION_VALUE_GROUP));
    }
    
    private CellFormatType getCellFormatType(final Matcher matcher) {
        final String fdesc = matcher.group(CellFormatPart.SPECIFICATION_GROUP);
        return this.formatType(fdesc);
    }
    
    private CellFormatter getFormatter(final Matcher matcher) {
        final String fdesc = matcher.group(CellFormatPart.SPECIFICATION_GROUP);
        return this.type.formatter(fdesc);
    }
    
    private CellFormatType formatType(String fdesc) {
        fdesc = fdesc.trim();
        if (fdesc.equals("") || fdesc.equalsIgnoreCase("General")) {
            return CellFormatType.GENERAL;
        }
        final Matcher m = CellFormatPart.SPECIFICATION_PAT.matcher(fdesc);
        boolean couldBeDate = false;
        boolean seenZero = false;
        while (m.find()) {
            final String repl = m.group(0);
            if (repl.length() > 0) {
                switch (repl.charAt(0)) {
                    case '@': {
                        return CellFormatType.TEXT;
                    }
                    case 'D':
                    case 'Y':
                    case 'd':
                    case 'y': {
                        return CellFormatType.DATE;
                    }
                    case 'H':
                    case 'M':
                    case 'S':
                    case 'h':
                    case 'm':
                    case 's': {
                        couldBeDate = true;
                        continue;
                    }
                    case '0': {
                        seenZero = true;
                        continue;
                    }
                    case '[': {
                        return CellFormatType.ELAPSED;
                    }
                    case '#':
                    case '?': {
                        return CellFormatType.NUMBER;
                    }
                }
            }
        }
        if (couldBeDate) {
            return CellFormatType.DATE;
        }
        if (seenZero) {
            return CellFormatType.NUMBER;
        }
        return CellFormatType.TEXT;
    }
    
    static String quoteSpecial(final String repl, final CellFormatType type) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repl.length(); ++i) {
            final char ch = repl.charAt(i);
            if (ch == '\'' && type.isSpecial('\'')) {
                sb.append('\0');
            }
            else {
                final boolean special = type.isSpecial(ch);
                if (special) {
                    sb.append("'");
                }
                sb.append(ch);
                if (special) {
                    sb.append("'");
                }
            }
        }
        return sb.toString();
    }
    
    public CellFormatResult apply(final Object value) {
        final boolean applies = this.applies(value);
        String text;
        Color textColor;
        if (applies) {
            text = this.format.format(value);
            textColor = this.color;
        }
        else {
            text = this.format.simpleFormat(value);
            textColor = null;
        }
        return new CellFormatResult(applies, text, textColor);
    }
    
    public CellFormatResult apply(final JLabel label, final Object value) {
        final CellFormatResult result = this.apply(value);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }
    
    CellFormatType getCellFormatType() {
        return this.type;
    }
    
    boolean hasCondition() {
        return this.condition != null;
    }
    
    public static StringBuffer parseFormat(final String fdesc, final CellFormatType type, final PartHandler partHandler) {
        final Matcher m = CellFormatPart.SPECIFICATION_PAT.matcher(fdesc);
        final StringBuffer fmt = new StringBuffer();
        while (m.find()) {
            final String part = group(m, 0);
            if (part.length() > 0) {
                String repl = partHandler.handlePart(m, part, type, fmt);
                if (repl == null) {
                    switch (part.charAt(0)) {
                        case '\"': {
                            repl = quoteSpecial(part.substring(1, part.length() - 1), type);
                            break;
                        }
                        case '\\': {
                            repl = quoteSpecial(part.substring(1), type);
                            break;
                        }
                        case '_': {
                            repl = " ";
                            break;
                        }
                        case '*': {
                            repl = expandChar(part);
                            break;
                        }
                        default: {
                            repl = part;
                            break;
                        }
                    }
                }
                m.appendReplacement(fmt, Matcher.quoteReplacement(repl));
            }
        }
        m.appendTail(fmt);
        if (type.isSpecial('\'')) {
            int pos = 0;
            while ((pos = fmt.indexOf("''", pos)) >= 0) {
                fmt.delete(pos, pos + 2);
            }
            pos = 0;
            while ((pos = fmt.indexOf("\u0000", pos)) >= 0) {
                fmt.replace(pos, pos + 1, "''");
            }
        }
        return fmt;
    }
    
    static String expandChar(final String part) {
        final char ch = part.charAt(1);
        final String repl = "" + ch + ch + ch;
        return repl;
    }
    
    public static String group(final Matcher m, final int g) {
        final String str = m.group(g);
        return (str == null) ? "" : str;
    }
    
    static {
        NAMED_COLORS = new TreeMap<String, Color>(String.CASE_INSENSITIVE_ORDER);
        final Map<Integer, HSSFColor> colors = HSSFColor.getIndexHash();
        for (final HSSFColor color : colors.values()) {
            final Class<? extends HSSFColor> type = color.getClass();
            final String name = type.getSimpleName();
            if (name.equals(name.toUpperCase())) {
                final short[] rgb = color.getTriplet();
                final Color c = new Color(rgb[0], rgb[1], rgb[2]);
                CellFormatPart.NAMED_COLORS.put(name, c);
                if (name.indexOf(95) > 0) {
                    CellFormatPart.NAMED_COLORS.put(name.replace('_', ' '), c);
                }
                if (name.indexOf("_PERCENT") <= 0) {
                    continue;
                }
                CellFormatPart.NAMED_COLORS.put(name.replace("_PERCENT", "%").replace('_', ' '), c);
            }
        }
        final String condition = "([<>=]=?|!=|<>)    # The operator\n  \\s*([0-9]+(?:\\.[0-9]*)?)\\s*  # The constant to test against\n";
        final String color2 = "\\[(black|blue|cyan|green|magenta|red|white|yellow|color [0-9]+)\\]";
        final String part = "\\\\.                 # Quoted single character\n|\"([^\\\\\"]|\\\\.)*\"         # Quoted string of characters (handles escaped quotes like \\\") \n|_.                             # Space as wide as a given character\n|\\*.                           # Repeating fill character\n|@                              # Text: cell text\n|([0?\\#](?:[0?\\#,]*))         # Number: digit + other digits and commas\n|e[-+]                          # Number: Scientific: Exponent\n|m{1,5}                         # Date: month or minute spec\n|d{1,4}                         # Date: day/date spec\n|y{2,4}                         # Date: year spec\n|h{1,2}                         # Date: hour spec\n|s{1,2}                         # Date: second spec\n|am?/pm?                        # Date: am/pm spec\n|\\[h{1,2}\\]                   # Elapsed time: hour spec\n|\\[m{1,2}\\]                   # Elapsed time: minute spec\n|\\[s{1,2}\\]                   # Elapsed time: second spec\n|[^;]                           # A character\n";
        final String format = "(?:" + color2 + ")?                  # Text color\n" + "(?:\\[" + condition + "\\])?                # Condition\n" + "((?:" + part + ")+)                        # Format spec\n";
        final int flags = 6;
        COLOR_PAT = Pattern.compile(color2, flags);
        CONDITION_PAT = Pattern.compile(condition, flags);
        SPECIFICATION_PAT = Pattern.compile(part, flags);
        FORMAT_PAT = Pattern.compile(format, flags);
        COLOR_GROUP = findGroup(CellFormatPart.FORMAT_PAT, "[Blue]@", "Blue");
        CONDITION_OPERATOR_GROUP = findGroup(CellFormatPart.FORMAT_PAT, "[>=1]@", ">=");
        CONDITION_VALUE_GROUP = findGroup(CellFormatPart.FORMAT_PAT, "[>=1]@", "1");
        SPECIFICATION_GROUP = findGroup(CellFormatPart.FORMAT_PAT, "[Blue][>1]\\a ?", "\\a ?");
    }
    
    interface PartHandler
    {
        String handlePart(final Matcher p0, final String p1, final CellFormatType p2, final StringBuffer p3);
    }
}
