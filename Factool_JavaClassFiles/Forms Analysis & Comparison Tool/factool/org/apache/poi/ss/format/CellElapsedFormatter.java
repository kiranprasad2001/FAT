// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.regex.Matcher;
import java.util.Formatter;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;

public class CellElapsedFormatter extends CellFormatter
{
    private final List<TimeSpec> specs;
    private TimeSpec topmost;
    private final String printfFmt;
    private static final Pattern PERCENTS;
    private static final double HOUR__FACTOR = 0.041666666666666664;
    private static final double MIN__FACTOR = 6.944444444444444E-4;
    private static final double SEC__FACTOR = 1.1574074074074073E-5;
    
    public CellElapsedFormatter(final String pattern) {
        super(pattern);
        this.specs = new ArrayList<TimeSpec>();
        final StringBuffer desc = CellFormatPart.parseFormat(pattern, CellFormatType.ELAPSED, new ElapsedPartHandler());
        final ListIterator<TimeSpec> it = this.specs.listIterator(this.specs.size());
        while (it.hasPrevious()) {
            final TimeSpec spec = it.previous();
            desc.replace(spec.pos, spec.pos + spec.len, "%0" + spec.len + "d");
            if (spec.type != this.topmost.type) {
                spec.modBy = modFor(spec.type, spec.len);
            }
        }
        this.printfFmt = desc.toString();
    }
    
    private TimeSpec assignSpec(final char type, final int pos, final int len) {
        final TimeSpec spec = new TimeSpec(type, pos, len, factorFor(type, len));
        this.specs.add(spec);
        return spec;
    }
    
    private static double factorFor(final char type, final int len) {
        switch (type) {
            case 'h': {
                return 0.041666666666666664;
            }
            case 'm': {
                return 6.944444444444444E-4;
            }
            case 's': {
                return 1.1574074074074073E-5;
            }
            case '0': {
                return 1.1574074074074073E-5 / Math.pow(10.0, len);
            }
            default: {
                throw new IllegalArgumentException("Uknown elapsed time spec: " + type);
            }
        }
    }
    
    private static double modFor(final char type, final int len) {
        switch (type) {
            case 'h': {
                return 24.0;
            }
            case 'm': {
                return 60.0;
            }
            case 's': {
                return 60.0;
            }
            case '0': {
                return Math.pow(10.0, len);
            }
            default: {
                throw new IllegalArgumentException("Uknown elapsed time spec: " + type);
            }
        }
    }
    
    @Override
    public void formatValue(final StringBuffer toAppendTo, final Object value) {
        double elapsed = ((Number)value).doubleValue();
        if (elapsed < 0.0) {
            toAppendTo.append('-');
            elapsed = -elapsed;
        }
        final Object[] parts = new Long[this.specs.size()];
        for (int i = 0; i < this.specs.size(); ++i) {
            parts[i] = this.specs.get(i).valueFor(elapsed);
        }
        final Formatter formatter = new Formatter(toAppendTo);
        try {
            formatter.format(this.printfFmt, parts);
        }
        finally {
            formatter.close();
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        this.formatValue(toAppendTo, value);
    }
    
    static {
        PERCENTS = Pattern.compile("%");
    }
    
    private static class TimeSpec
    {
        final char type;
        final int pos;
        final int len;
        final double factor;
        double modBy;
        
        public TimeSpec(final char type, final int pos, final int len, final double factor) {
            this.type = type;
            this.pos = pos;
            this.len = len;
            this.factor = factor;
            this.modBy = 0.0;
        }
        
        public long valueFor(final double elapsed) {
            double val;
            if (this.modBy == 0.0) {
                val = elapsed / this.factor;
            }
            else {
                val = elapsed / this.factor % this.modBy;
            }
            if (this.type == '0') {
                return Math.round(val);
            }
            return (long)val;
        }
    }
    
    private class ElapsedPartHandler implements CellFormatPart.PartHandler
    {
        @Override
        public String handlePart(final Matcher m, String part, final CellFormatType type, final StringBuffer desc) {
            final int pos = desc.length();
            final char firstCh = part.charAt(0);
            switch (firstCh) {
                case '[': {
                    if (part.length() < 3) {
                        break;
                    }
                    if (CellElapsedFormatter.this.topmost != null) {
                        throw new IllegalArgumentException("Duplicate '[' times in format");
                    }
                    part = part.toLowerCase();
                    final int specLen = part.length() - 2;
                    CellElapsedFormatter.this.topmost = CellElapsedFormatter.this.assignSpec(part.charAt(1), pos, specLen);
                    return part.substring(1, 1 + specLen);
                }
                case '0':
                case 'h':
                case 'm':
                case 's': {
                    part = part.toLowerCase();
                    CellElapsedFormatter.this.assignSpec(part.charAt(0), pos, part.length());
                    return part;
                }
                case '\n': {
                    return "%n";
                }
                case '\"': {
                    part = part.substring(1, part.length() - 1);
                    break;
                }
                case '\\': {
                    part = part.substring(1);
                    break;
                }
                case '*': {
                    if (part.length() > 1) {
                        part = CellFormatPart.expandChar(part);
                        break;
                    }
                    break;
                }
                case '_': {
                    return null;
                }
            }
            return CellElapsedFormatter.PERCENTS.matcher(part).replaceAll("%%");
        }
    }
}
