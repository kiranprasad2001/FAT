// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.regex.Matcher;
import java.text.FieldPosition;
import java.util.Set;
import java.util.BitSet;
import java.util.Formatter;
import java.util.TreeSet;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedList;
import java.text.DecimalFormat;
import java.util.List;

public class CellNumberFormatter extends CellFormatter
{
    private final String desc;
    private String printfFmt;
    private double scale;
    private Special decimalPoint;
    private Special slash;
    private Special exponent;
    private Special numerator;
    private Special afterInteger;
    private Special afterFractional;
    private boolean integerCommas;
    private final List<Special> specials;
    private List<Special> integerSpecials;
    private List<Special> fractionalSpecials;
    private List<Special> numeratorSpecials;
    private List<Special> denominatorSpecials;
    private List<Special> exponentSpecials;
    private List<Special> exponentDigitSpecials;
    private int maxDenominator;
    private String numeratorFmt;
    private String denominatorFmt;
    private boolean improperFraction;
    private DecimalFormat decimalFmt;
    static final CellFormatter SIMPLE_NUMBER;
    private static final CellFormatter SIMPLE_INT;
    private static final CellFormatter SIMPLE_FLOAT;
    
    public CellNumberFormatter(final String format) {
        super(format);
        this.scale = 1.0;
        this.specials = new LinkedList<Special>();
        final NumPartHandler partHandler = new NumPartHandler();
        final StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.NUMBER, partHandler);
        if ((this.decimalPoint != null || this.exponent != null) && this.slash != null) {
            this.slash = null;
            this.numerator = null;
        }
        this.interpretCommas(descBuf);
        int fractionPartWidth = 0;
        int precision;
        if (this.decimalPoint == null) {
            precision = 0;
        }
        else {
            precision = this.interpretPrecision();
            fractionPartWidth = 1 + precision;
            if (precision == 0) {
                this.specials.remove(this.decimalPoint);
                this.decimalPoint = null;
            }
        }
        if (precision == 0) {
            this.fractionalSpecials = Collections.emptyList();
        }
        else {
            this.fractionalSpecials = this.specials.subList(this.specials.indexOf(this.decimalPoint) + 1, this.fractionalEnd());
        }
        if (this.exponent == null) {
            this.exponentSpecials = Collections.emptyList();
        }
        else {
            final int exponentPos = this.specials.indexOf(this.exponent);
            this.exponentSpecials = this.specialsFor(exponentPos, 2);
            this.exponentDigitSpecials = this.specialsFor(exponentPos + 2);
        }
        if (this.slash == null) {
            this.numeratorSpecials = Collections.emptyList();
            this.denominatorSpecials = Collections.emptyList();
        }
        else {
            if (this.numerator == null) {
                this.numeratorSpecials = Collections.emptyList();
            }
            else {
                this.numeratorSpecials = this.specialsFor(this.specials.indexOf(this.numerator));
            }
            this.denominatorSpecials = this.specialsFor(this.specials.indexOf(this.slash) + 1);
            if (this.denominatorSpecials.isEmpty()) {
                this.numeratorSpecials = Collections.emptyList();
            }
            else {
                this.maxDenominator = maxValue(this.denominatorSpecials);
                this.numeratorFmt = singleNumberFormat(this.numeratorSpecials);
                this.denominatorFmt = singleNumberFormat(this.denominatorSpecials);
            }
        }
        this.integerSpecials = this.specials.subList(0, this.integerEnd());
        if (this.exponent == null) {
            final StringBuffer fmtBuf = new StringBuffer("%");
            final int integerPartWidth = this.calculateIntegerPartWidth();
            final int totalWidth = integerPartWidth + fractionPartWidth;
            fmtBuf.append('0').append(totalWidth).append('.').append(precision);
            fmtBuf.append("f");
            this.printfFmt = fmtBuf.toString();
        }
        else {
            final StringBuffer fmtBuf = new StringBuffer();
            boolean first = true;
            final List<Special> specialList = this.integerSpecials;
            if (this.integerSpecials.size() == 1) {
                fmtBuf.append("0");
                first = false;
            }
            else {
                for (final Special s : specialList) {
                    if (isDigitFmt(s)) {
                        fmtBuf.append(first ? '#' : '0');
                        first = false;
                    }
                }
            }
            if (this.fractionalSpecials.size() > 0) {
                fmtBuf.append('.');
                for (final Special s : this.fractionalSpecials) {
                    if (isDigitFmt(s)) {
                        if (!first) {
                            fmtBuf.append('0');
                        }
                        first = false;
                    }
                }
            }
            fmtBuf.append('E');
            placeZeros(fmtBuf, this.exponentSpecials.subList(2, this.exponentSpecials.size()));
            this.decimalFmt = new DecimalFormat(fmtBuf.toString());
        }
        if (this.exponent != null) {
            this.scale = 1.0;
        }
        this.desc = descBuf.toString();
    }
    
    private static void placeZeros(final StringBuffer sb, final List<Special> specials) {
        for (final Special s : specials) {
            if (isDigitFmt(s)) {
                sb.append('0');
            }
        }
    }
    
    private static Special firstDigit(final List<Special> specials) {
        for (final Special s : specials) {
            if (isDigitFmt(s)) {
                return s;
            }
        }
        return null;
    }
    
    static StringMod insertMod(final Special special, final CharSequence toAdd, final int where) {
        return new StringMod(special, toAdd, where);
    }
    
    static StringMod deleteMod(final Special start, final boolean startInclusive, final Special end, final boolean endInclusive) {
        return new StringMod(start, startInclusive, end, endInclusive);
    }
    
    static StringMod replaceMod(final Special start, final boolean startInclusive, final Special end, final boolean endInclusive, final char withChar) {
        return new StringMod(start, startInclusive, end, endInclusive, withChar);
    }
    
    private static String singleNumberFormat(final List<Special> numSpecials) {
        return "%0" + numSpecials.size() + "d";
    }
    
    private static int maxValue(final List<Special> s) {
        return (int)Math.round(Math.pow(10.0, s.size()) - 1.0);
    }
    
    private List<Special> specialsFor(final int pos, final int takeFirst) {
        if (pos >= this.specials.size()) {
            return Collections.emptyList();
        }
        final ListIterator<Special> it = this.specials.listIterator(pos + takeFirst);
        Special last = it.next();
        int end = pos + takeFirst;
        while (it.hasNext()) {
            final Special s = it.next();
            if (!isDigitFmt(s)) {
                break;
            }
            if (s.pos - last.pos > 1) {
                break;
            }
            ++end;
            last = s;
        }
        return this.specials.subList(pos, end + 1);
    }
    
    private List<Special> specialsFor(final int pos) {
        return this.specialsFor(pos, 0);
    }
    
    private static boolean isDigitFmt(final Special s) {
        return s.ch == '0' || s.ch == '?' || s.ch == '#';
    }
    
    private Special previousNumber() {
        final ListIterator<Special> it = this.specials.listIterator(this.specials.size());
        while (it.hasPrevious()) {
            Special s = it.previous();
            if (isDigitFmt(s)) {
                Special numStart = s;
                Special last = s;
                while (it.hasPrevious()) {
                    s = it.previous();
                    if (last.pos - s.pos > 1) {
                        break;
                    }
                    if (!isDigitFmt(s)) {
                        break;
                    }
                    numStart = s;
                    last = s;
                }
                return numStart;
            }
        }
        return null;
    }
    
    private int calculateIntegerPartWidth() {
        final ListIterator<Special> it = this.specials.listIterator();
        int digitCount = 0;
        while (it.hasNext()) {
            final Special s = it.next();
            if (s == this.afterInteger) {
                break;
            }
            if (!isDigitFmt(s)) {
                continue;
            }
            ++digitCount;
        }
        return digitCount;
    }
    
    private int interpretPrecision() {
        if (this.decimalPoint == null) {
            return -1;
        }
        int precision = 0;
        final ListIterator<Special> it = this.specials.listIterator(this.specials.indexOf(this.decimalPoint));
        if (it.hasNext()) {
            it.next();
        }
        while (it.hasNext()) {
            final Special s = it.next();
            if (!isDigitFmt(s)) {
                break;
            }
            ++precision;
        }
        return precision;
    }
    
    private void interpretCommas(final StringBuffer sb) {
        ListIterator<Special> it = this.specials.listIterator(this.integerEnd());
        boolean stillScaling = true;
        this.integerCommas = false;
        while (it.hasPrevious()) {
            final Special s = it.previous();
            if (s.ch != ',') {
                stillScaling = false;
            }
            else if (stillScaling) {
                this.scale /= 1000.0;
            }
            else {
                this.integerCommas = true;
            }
        }
        if (this.decimalPoint != null) {
            it = this.specials.listIterator(this.fractionalEnd());
            while (it.hasPrevious()) {
                final Special s = it.previous();
                if (s.ch != ',') {
                    break;
                }
                this.scale /= 1000.0;
            }
        }
        it = this.specials.listIterator();
        int removed = 0;
        while (it.hasNext()) {
            final Special special;
            final Special s2 = special = it.next();
            special.pos -= removed;
            if (s2.ch == ',') {
                ++removed;
                it.remove();
                sb.deleteCharAt(s2.pos);
            }
        }
    }
    
    private int integerEnd() {
        if (this.decimalPoint != null) {
            this.afterInteger = this.decimalPoint;
        }
        else if (this.exponent != null) {
            this.afterInteger = this.exponent;
        }
        else if (this.numerator != null) {
            this.afterInteger = this.numerator;
        }
        else {
            this.afterInteger = null;
        }
        return (this.afterInteger == null) ? this.specials.size() : this.specials.indexOf(this.afterInteger);
    }
    
    private int fractionalEnd() {
        if (this.exponent != null) {
            this.afterFractional = this.exponent;
        }
        else if (this.numerator != null) {
            this.afterInteger = this.numerator;
        }
        else {
            this.afterFractional = null;
        }
        final int end = (this.afterFractional == null) ? this.specials.size() : this.specials.indexOf(this.afterFractional);
        return end;
    }
    
    @Override
    public void formatValue(final StringBuffer toAppendTo, final Object valueObject) {
        double value = ((Number)valueObject).doubleValue();
        value *= this.scale;
        final boolean negative = value < 0.0;
        if (negative) {
            value = -value;
        }
        double fractional = 0.0;
        if (this.slash != null) {
            if (this.improperFraction) {
                fractional = value;
                value = 0.0;
            }
            else {
                fractional = value % 1.0;
                value = (double)(long)value;
            }
        }
        final Set<StringMod> mods = new TreeSet<StringMod>();
        final StringBuffer output = new StringBuffer(this.desc);
        if (this.exponent != null) {
            this.writeScientific(value, output, mods);
        }
        else if (this.improperFraction) {
            this.writeFraction(value, null, fractional, output, mods);
        }
        else {
            final StringBuffer result = new StringBuffer();
            final Formatter f = new Formatter(result);
            try {
                f.format(CellNumberFormatter.LOCALE, this.printfFmt, value);
            }
            finally {
                f.close();
            }
            if (this.numerator == null) {
                this.writeFractional(result, output);
                this.writeInteger(result, output, this.integerSpecials, mods, this.integerCommas);
            }
            else {
                this.writeFraction(value, result, fractional, output, mods);
            }
        }
        final ListIterator<Special> it = this.specials.listIterator();
        final Iterator<StringMod> changes = mods.iterator();
        StringMod nextChange = changes.hasNext() ? changes.next() : null;
        int adjust = 0;
        final BitSet deletedChars = new BitSet();
        while (it.hasNext()) {
            final Special s = it.next();
            final int adjustedPos = s.pos + adjust;
            if (!deletedChars.get(s.pos) && output.charAt(adjustedPos) == '#') {
                output.deleteCharAt(adjustedPos);
                --adjust;
                deletedChars.set(s.pos);
            }
            while (nextChange != null && s == nextChange.special) {
                final int lenBefore = output.length();
                int modPos = s.pos + adjust;
                int posTweak = 0;
                switch (nextChange.op) {
                    case 2: {
                        if (nextChange.toAdd.equals(",") && deletedChars.get(s.pos)) {
                            break;
                        }
                        posTweak = 1;
                    }
                    case 1: {
                        output.insert(modPos + posTweak, nextChange.toAdd);
                        break;
                    }
                    case 3: {
                        int delPos = s.pos;
                        if (!nextChange.startInclusive) {
                            ++delPos;
                            ++modPos;
                        }
                        while (deletedChars.get(delPos)) {
                            ++delPos;
                            ++modPos;
                        }
                        int delEndPos = nextChange.end.pos;
                        if (nextChange.endInclusive) {
                            ++delEndPos;
                        }
                        final int modEndPos = delEndPos + adjust;
                        if (modPos < modEndPos) {
                            if ("".equals(nextChange.toAdd)) {
                                output.delete(modPos, modEndPos);
                            }
                            else {
                                final char fillCh = nextChange.toAdd.charAt(0);
                                for (int i = modPos; i < modEndPos; ++i) {
                                    output.setCharAt(i, fillCh);
                                }
                            }
                            deletedChars.set(delPos, delEndPos);
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown op: " + nextChange.op);
                    }
                }
                adjust += output.length() - lenBefore;
                if (changes.hasNext()) {
                    nextChange = changes.next();
                }
                else {
                    nextChange = null;
                }
            }
        }
        if (negative) {
            toAppendTo.append('-');
        }
        toAppendTo.append(output);
    }
    
    private void writeScientific(final double value, final StringBuffer output, final Set<StringMod> mods) {
        final StringBuffer result = new StringBuffer();
        final FieldPosition fractionPos = new FieldPosition(1);
        this.decimalFmt.format(value, result, fractionPos);
        this.writeInteger(result, output, this.integerSpecials, mods, this.integerCommas);
        this.writeFractional(result, output);
        final int ePos = fractionPos.getEndIndex();
        final int signPos = ePos + 1;
        char expSignRes = result.charAt(signPos);
        if (expSignRes != '-') {
            expSignRes = '+';
            result.insert(signPos, '+');
        }
        final ListIterator<Special> it = this.exponentSpecials.listIterator(1);
        final Special expSign = it.next();
        final char expSignFmt = expSign.ch;
        if (expSignRes == '-' || expSignFmt == '+') {
            mods.add(replaceMod(expSign, true, expSign, true, expSignRes));
        }
        else {
            mods.add(deleteMod(expSign, true, expSign, true));
        }
        final StringBuffer exponentNum = new StringBuffer(result.substring(signPos + 1));
        this.writeInteger(exponentNum, output, this.exponentDigitSpecials, mods, false);
    }
    
    private void writeFraction(final double value, final StringBuffer result, final double fractional, final StringBuffer output, final Set<StringMod> mods) {
        if (!this.improperFraction) {
            if (fractional == 0.0 && !hasChar('0', this.numeratorSpecials)) {
                this.writeInteger(result, output, this.integerSpecials, mods, false);
                final Special start = this.integerSpecials.get(this.integerSpecials.size() - 1);
                final Special end = this.denominatorSpecials.get(this.denominatorSpecials.size() - 1);
                if (hasChar('?', this.integerSpecials, this.numeratorSpecials, this.denominatorSpecials)) {
                    mods.add(replaceMod(start, false, end, true, ' '));
                }
                else {
                    mods.add(deleteMod(start, false, end, true));
                }
                return;
            }
            final boolean allZero = value == 0.0 && fractional == 0.0;
            final boolean willShowFraction = fractional != 0.0 || hasChar('0', this.numeratorSpecials);
            final boolean removeBecauseZero = allZero && (hasOnly('#', this.integerSpecials) || !hasChar('0', this.numeratorSpecials));
            final boolean removeBecauseFraction = !allZero && value == 0.0 && willShowFraction && !hasChar('0', this.integerSpecials);
            if (removeBecauseZero || removeBecauseFraction) {
                final Special start2 = this.integerSpecials.get(this.integerSpecials.size() - 1);
                if (hasChar('?', this.integerSpecials, this.numeratorSpecials)) {
                    mods.add(replaceMod(start2, true, this.numerator, false, ' '));
                }
                else {
                    mods.add(deleteMod(start2, true, this.numerator, false));
                }
            }
            else {
                this.writeInteger(result, output, this.integerSpecials, mods, false);
            }
        }
        try {
            int n;
            int d;
            if (fractional == 0.0 || (this.improperFraction && fractional % 1.0 == 0.0)) {
                n = (int)Math.round(fractional);
                d = 1;
            }
            else {
                final SimpleFraction frac = SimpleFraction.buildFractionMaxDenominator(fractional, this.maxDenominator);
                n = frac.getNumerator();
                d = frac.getDenominator();
            }
            if (this.improperFraction) {
                n += (int)Math.round(value * d);
            }
            this.writeSingleInteger(this.numeratorFmt, n, output, this.numeratorSpecials, mods);
            this.writeSingleInteger(this.denominatorFmt, d, output, this.denominatorSpecials, mods);
        }
        catch (RuntimeException ignored) {
            ignored.printStackTrace();
        }
    }
    
    private static boolean hasChar(final char ch, final List<Special>... numSpecials) {
        for (final List<Special> specials : numSpecials) {
            for (final Special s : specials) {
                if (s.ch == ch) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean hasOnly(final char ch, final List<Special>... numSpecials) {
        for (final List<Special> specials : numSpecials) {
            for (final Special s : specials) {
                if (s.ch != ch) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void writeSingleInteger(final String fmt, final int num, final StringBuffer output, final List<Special> numSpecials, final Set<StringMod> mods) {
        final StringBuffer sb = new StringBuffer();
        final Formatter formatter = new Formatter(sb);
        try {
            formatter.format(CellNumberFormatter.LOCALE, fmt, num);
        }
        finally {
            formatter.close();
        }
        this.writeInteger(sb, output, numSpecials, mods, false);
    }
    
    private void writeInteger(final StringBuffer result, final StringBuffer output, final List<Special> numSpecials, final Set<StringMod> mods, final boolean showCommas) {
        int pos = result.indexOf(".") - 1;
        if (pos < 0) {
            if (this.exponent != null && numSpecials == this.integerSpecials) {
                pos = result.indexOf("E") - 1;
            }
            else {
                pos = result.length() - 1;
            }
        }
        int strip;
        for (strip = 0; strip < pos; ++strip) {
            final char resultCh = result.charAt(strip);
            if (resultCh != '0' && resultCh != ',') {
                break;
            }
        }
        final ListIterator<Special> it = numSpecials.listIterator(numSpecials.size());
        boolean followWithComma = false;
        Special lastOutputIntegerDigit = null;
        int digit = 0;
        while (it.hasPrevious()) {
            char resultCh2;
            if (pos >= 0) {
                resultCh2 = result.charAt(pos);
            }
            else {
                resultCh2 = '0';
            }
            final Special s = it.previous();
            followWithComma = (showCommas && digit > 0 && digit % 3 == 0);
            boolean zeroStrip = false;
            if (resultCh2 != '0' || s.ch == '0' || s.ch == '?' || pos >= strip) {
                zeroStrip = (s.ch == '?' && pos < strip);
                output.setCharAt(s.pos, zeroStrip ? ' ' : resultCh2);
                lastOutputIntegerDigit = s;
            }
            if (followWithComma) {
                mods.add(insertMod(s, zeroStrip ? " " : ",", 2));
                followWithComma = false;
            }
            ++digit;
            --pos;
        }
        StringBuffer extraLeadingDigits = new StringBuffer();
        if (pos >= 0) {
            ++pos;
            extraLeadingDigits = new StringBuffer(result.substring(0, pos));
            if (showCommas) {
                while (pos > 0) {
                    if (digit > 0 && digit % 3 == 0) {
                        extraLeadingDigits.insert(pos, ',');
                    }
                    ++digit;
                    --pos;
                }
            }
            mods.add(insertMod(lastOutputIntegerDigit, extraLeadingDigits, 1));
        }
    }
    
    private void writeFractional(final StringBuffer result, final StringBuffer output) {
        if (this.fractionalSpecials.size() > 0) {
            int digit = result.indexOf(".") + 1;
            int strip;
            if (this.exponent != null) {
                strip = result.indexOf("e") - 1;
            }
            else {
                strip = result.length() - 1;
            }
            while (strip > digit && result.charAt(strip) == '0') {
                --strip;
            }
            final ListIterator<Special> it = this.fractionalSpecials.listIterator();
            while (it.hasNext()) {
                final Special s = it.next();
                final char resultCh = result.charAt(digit);
                if (resultCh != '0' || s.ch == '0' || digit < strip) {
                    output.setCharAt(s.pos, resultCh);
                }
                else if (s.ch == '?') {
                    output.setCharAt(s.pos, ' ');
                }
                ++digit;
            }
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        CellNumberFormatter.SIMPLE_NUMBER.formatValue(toAppendTo, value);
    }
    
    static {
        SIMPLE_NUMBER = new CellFormatter("General") {
            @Override
            public void formatValue(final StringBuffer toAppendTo, final Object value) {
                if (value == null) {
                    return;
                }
                if (value instanceof Number) {
                    final Number num = (Number)value;
                    if (num.doubleValue() % 1.0 == 0.0) {
                        CellNumberFormatter.SIMPLE_INT.formatValue(toAppendTo, value);
                    }
                    else {
                        CellNumberFormatter.SIMPLE_FLOAT.formatValue(toAppendTo, value);
                    }
                }
                else {
                    CellTextFormatter.SIMPLE_TEXT.formatValue(toAppendTo, value);
                }
            }
            
            @Override
            public void simpleValue(final StringBuffer toAppendTo, final Object value) {
                this.formatValue(toAppendTo, value);
            }
        };
        SIMPLE_INT = new CellNumberFormatter("#");
        SIMPLE_FLOAT = new CellNumberFormatter("#.#");
    }
    
    static class Special
    {
        final char ch;
        int pos;
        
        Special(final char ch, final int pos) {
            this.ch = ch;
            this.pos = pos;
        }
        
        @Override
        public String toString() {
            return "'" + this.ch + "' @ " + this.pos;
        }
    }
    
    static class StringMod implements Comparable<StringMod>
    {
        final Special special;
        final int op;
        CharSequence toAdd;
        Special end;
        boolean startInclusive;
        boolean endInclusive;
        public static final int BEFORE = 1;
        public static final int AFTER = 2;
        public static final int REPLACE = 3;
        
        private StringMod(final Special special, final CharSequence toAdd, final int op) {
            this.special = special;
            this.toAdd = toAdd;
            this.op = op;
        }
        
        public StringMod(final Special start, final boolean startInclusive, final Special end, final boolean endInclusive, final char toAdd) {
            this(start, startInclusive, end, endInclusive);
            this.toAdd = toAdd + "";
        }
        
        public StringMod(final Special start, final boolean startInclusive, final Special end, final boolean endInclusive) {
            this.special = start;
            this.startInclusive = startInclusive;
            this.end = end;
            this.endInclusive = endInclusive;
            this.op = 3;
            this.toAdd = "";
        }
        
        @Override
        public int compareTo(final StringMod that) {
            final int diff = this.special.pos - that.special.pos;
            if (diff != 0) {
                return diff;
            }
            return this.op - that.op;
        }
        
        @Override
        public boolean equals(final Object that) {
            try {
                return this.compareTo((StringMod)that) == 0;
            }
            catch (RuntimeException ignored) {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return this.special.hashCode() + this.op;
        }
    }
    
    private class NumPartHandler implements CellFormatPart.PartHandler
    {
        private char insertSignForExponent;
        
        @Override
        public String handlePart(final Matcher m, final String part, final CellFormatType type, final StringBuffer desc) {
            int pos = desc.length();
            final char firstCh = part.charAt(0);
            switch (firstCh) {
                case 'E':
                case 'e': {
                    if (CellNumberFormatter.this.exponent == null && CellNumberFormatter.this.specials.size() > 0) {
                        CellNumberFormatter.this.specials.add(CellNumberFormatter.this.exponent = new Special('.', pos));
                        this.insertSignForExponent = part.charAt(1);
                        return part.substring(0, 1);
                    }
                    break;
                }
                case '#':
                case '0':
                case '?': {
                    if (this.insertSignForExponent != '\0') {
                        CellNumberFormatter.this.specials.add(new Special(this.insertSignForExponent, pos));
                        desc.append(this.insertSignForExponent);
                        this.insertSignForExponent = '\0';
                        ++pos;
                    }
                    for (int i = 0; i < part.length(); ++i) {
                        final char ch = part.charAt(i);
                        CellNumberFormatter.this.specials.add(new Special(ch, pos + i));
                    }
                    break;
                }
                case '.': {
                    if (CellNumberFormatter.this.decimalPoint == null && CellNumberFormatter.this.specials.size() > 0) {
                        CellNumberFormatter.this.specials.add(CellNumberFormatter.this.decimalPoint = new Special('.', pos));
                        break;
                    }
                    break;
                }
                case '/': {
                    if (CellNumberFormatter.this.slash == null && CellNumberFormatter.this.specials.size() > 0) {
                        CellNumberFormatter.this.numerator = CellNumberFormatter.this.previousNumber();
                        if (CellNumberFormatter.this.numerator == firstDigit(CellNumberFormatter.this.specials)) {
                            CellNumberFormatter.this.improperFraction = true;
                        }
                        CellNumberFormatter.this.specials.add(CellNumberFormatter.this.slash = new Special('.', pos));
                        break;
                    }
                    break;
                }
                case '%': {
                    CellNumberFormatter.this.scale *= 100.0;
                    break;
                }
                default: {
                    return null;
                }
            }
            return part;
        }
    }
}
