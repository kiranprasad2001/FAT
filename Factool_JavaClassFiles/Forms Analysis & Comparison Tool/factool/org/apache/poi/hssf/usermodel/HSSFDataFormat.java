// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import java.util.Vector;
import org.apache.poi.ss.usermodel.DataFormat;

public final class HSSFDataFormat implements DataFormat
{
    private static final String[] _builtinFormats;
    private final Vector<String> _formats;
    private final InternalWorkbook _workbook;
    private boolean _movedBuiltins;
    
    HSSFDataFormat(final InternalWorkbook workbook) {
        this._formats = new Vector<String>();
        this._movedBuiltins = false;
        this._workbook = workbook;
        for (final FormatRecord r : workbook.getFormats()) {
            this.ensureFormatsSize(r.getIndexCode());
            this._formats.set(r.getIndexCode(), r.getFormatString());
        }
    }
    
    public static List<String> getBuiltinFormats() {
        return Arrays.asList(HSSFDataFormat._builtinFormats);
    }
    
    public static short getBuiltinFormat(final String format) {
        return (short)BuiltinFormats.getBuiltinFormat(format);
    }
    
    @Override
    public short getFormat(final String pFormat) {
        String format;
        if (pFormat.toUpperCase().equals("TEXT")) {
            format = "@";
        }
        else {
            format = pFormat;
        }
        if (!this._movedBuiltins) {
            for (int i = 0; i < HSSFDataFormat._builtinFormats.length; ++i) {
                this.ensureFormatsSize(i);
                if (this._formats.get(i) == null) {
                    this._formats.set(i, HSSFDataFormat._builtinFormats[i]);
                }
            }
            this._movedBuiltins = true;
        }
        for (int i = 0; i < this._formats.size(); ++i) {
            if (format.equals(this._formats.get(i))) {
                return (short)i;
            }
        }
        final short index = this._workbook.getFormat(format, true);
        this.ensureFormatsSize(index);
        this._formats.set(index, format);
        return index;
    }
    
    @Override
    public String getFormat(final short index) {
        if (this._movedBuiltins) {
            return this._formats.get(index);
        }
        if (index == -1) {
            return null;
        }
        final String fmt = (this._formats.size() > index) ? this._formats.get(index) : null;
        if (HSSFDataFormat._builtinFormats.length <= index || HSSFDataFormat._builtinFormats[index] == null) {
            return fmt;
        }
        if (fmt != null) {
            return fmt;
        }
        return HSSFDataFormat._builtinFormats[index];
    }
    
    public static String getBuiltinFormat(final short index) {
        return BuiltinFormats.getBuiltinFormat(index);
    }
    
    public static int getNumberOfBuiltinBuiltinFormats() {
        return HSSFDataFormat._builtinFormats.length;
    }
    
    private void ensureFormatsSize(final int index) {
        if (this._formats.size() <= index) {
            this._formats.setSize(index + 1);
        }
    }
    
    static {
        _builtinFormats = BuiltinFormats.getAll();
    }
}
