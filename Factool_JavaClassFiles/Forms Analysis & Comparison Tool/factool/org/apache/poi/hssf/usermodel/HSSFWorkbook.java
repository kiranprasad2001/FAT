// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.Configurator;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.util.HexDump;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.apache.poi.hpsf.ClassID;
import java.util.Map;
import org.apache.poi.hssf.record.AbstractEscherHolderRecord;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBitmapBlip;
import org.apache.poi.ddf.EscherMetafileBlip;
import org.apache.poi.util.LittleEndian;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.model.DrawingManager2;
import org.apache.poi.ddf.EscherRecord;
import java.io.PrintWriter;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.poifs.filesystem.EntryUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.FilteringDirectoryNode;
import java.util.Collection;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.WorkbookUtil;
import java.util.Iterator;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.Record;
import java.io.InputStream;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.EncryptedDocumentException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.udf.IndexedUDFFinder;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Row;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.InternalWorkbook;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.POIDocument;

public final class HSSFWorkbook extends POIDocument implements Workbook
{
    private static final Pattern COMMA_PATTERN;
    private static final int MAX_STYLES = 4030;
    private static final int DEBUG = 1;
    public static final int INITIAL_CAPACITY;
    private InternalWorkbook workbook;
    protected List<HSSFSheet> _sheets;
    private ArrayList<HSSFName> names;
    private Hashtable<Short, HSSFFont> fonts;
    private boolean preserveNodes;
    private HSSFDataFormat formatter;
    private Row.MissingCellPolicy missingCellPolicy;
    private static POILogger log;
    private UDFFinder _udfFinder;
    private static final String[] WORKBOOK_DIR_ENTRY_NAMES;
    
    public static HSSFWorkbook create(final InternalWorkbook book) {
        return new HSSFWorkbook(book);
    }
    
    public HSSFWorkbook() {
        this(InternalWorkbook.createWorkbook());
    }
    
    private HSSFWorkbook(final InternalWorkbook book) {
        super((DirectoryNode)null);
        this.missingCellPolicy = HSSFRow.RETURN_NULL_AND_BLANK;
        this._udfFinder = new IndexedUDFFinder(new UDFFinder[] { UDFFinder.DEFAULT });
        this.workbook = book;
        this._sheets = new ArrayList<HSSFSheet>(HSSFWorkbook.INITIAL_CAPACITY);
        this.names = new ArrayList<HSSFName>(HSSFWorkbook.INITIAL_CAPACITY);
    }
    
    public HSSFWorkbook(final POIFSFileSystem fs) throws IOException {
        this(fs, true);
    }
    
    public HSSFWorkbook(final POIFSFileSystem fs, final boolean preserveNodes) throws IOException {
        this(fs.getRoot(), fs, preserveNodes);
    }
    
    public static String getWorkbookDirEntryName(final DirectoryNode directory) {
        int i = 0;
        while (i < HSSFWorkbook.WORKBOOK_DIR_ENTRY_NAMES.length) {
            final String wbName = HSSFWorkbook.WORKBOOK_DIR_ENTRY_NAMES[i];
            try {
                directory.getEntry(wbName);
                return wbName;
            }
            catch (FileNotFoundException e) {
                ++i;
                continue;
            }
            break;
        }
        try {
            directory.getEntry("EncryptedPackage");
            throw new EncryptedDocumentException("The supplied spreadsheet seems to be an Encrypted .xlsx file. It must be decrypted before use by XSSF, it cannot be used by HSSF");
        }
        catch (FileNotFoundException e2) {
            try {
                directory.getEntry("Book");
                throw new OldExcelFormatException("The supplied spreadsheet seems to be Excel 5.0/7.0 (BIFF5) format. POI only supports BIFF8 format (from Excel versions 97/2000/XP/2003)");
            }
            catch (FileNotFoundException e2) {
                throw new IllegalArgumentException("The supplied POIFSFileSystem does not contain a BIFF8 'Workbook' entry. Is it really an excel file?");
            }
        }
    }
    
    public HSSFWorkbook(final DirectoryNode directory, final POIFSFileSystem fs, final boolean preserveNodes) throws IOException {
        this(directory, preserveNodes);
    }
    
    public HSSFWorkbook(final DirectoryNode directory, final boolean preserveNodes) throws IOException {
        super(directory);
        this.missingCellPolicy = HSSFRow.RETURN_NULL_AND_BLANK;
        this._udfFinder = new IndexedUDFFinder(new UDFFinder[] { UDFFinder.DEFAULT });
        final String workbookName = getWorkbookDirEntryName(directory);
        if (!(this.preserveNodes = preserveNodes)) {
            this.directory = null;
        }
        this._sheets = new ArrayList<HSSFSheet>(HSSFWorkbook.INITIAL_CAPACITY);
        this.names = new ArrayList<HSSFName>(HSSFWorkbook.INITIAL_CAPACITY);
        final InputStream stream = directory.createDocumentInputStream(workbookName);
        final List<Record> records = RecordFactory.createRecords(stream);
        this.setPropertiesFromWorkbook(this.workbook = InternalWorkbook.createWorkbook(records));
        final int recOffset = this.workbook.getNumRecords();
        this.convertLabelRecords(records, recOffset);
        final RecordStream rs = new RecordStream(records, recOffset);
        while (rs.hasNext()) {
            try {
                final InternalSheet sheet = InternalSheet.createSheet(rs);
                this._sheets.add(new HSSFSheet(this, sheet));
            }
            catch (InternalSheet.UnsupportedBOFType eb) {
                HSSFWorkbook.log.log(5, "Unsupported BOF found of type " + eb.getType());
            }
        }
        for (int i = 0; i < this.workbook.getNumNames(); ++i) {
            final NameRecord nameRecord = this.workbook.getNameRecord(i);
            final HSSFName name = new HSSFName(this, nameRecord, this.workbook.getNameCommentRecord(nameRecord));
            this.names.add(name);
        }
    }
    
    public HSSFWorkbook(final InputStream s) throws IOException {
        this(s, true);
    }
    
    public HSSFWorkbook(final InputStream s, final boolean preserveNodes) throws IOException {
        this(new POIFSFileSystem(s), preserveNodes);
    }
    
    private void setPropertiesFromWorkbook(final InternalWorkbook book) {
        this.workbook = book;
    }
    
    private void convertLabelRecords(final List<Record> records, final int offset) {
        if (HSSFWorkbook.log.check(1)) {
            HSSFWorkbook.log.log(1, "convertLabelRecords called");
        }
        for (int k = offset; k < records.size(); ++k) {
            final Record rec = records.get(k);
            if (rec.getSid() == 516) {
                final LabelRecord oldrec = (LabelRecord)rec;
                records.remove(k);
                final LabelSSTRecord newrec = new LabelSSTRecord();
                final int stringid = this.workbook.addSSTString(new UnicodeString(oldrec.getValue()));
                newrec.setRow(oldrec.getRow());
                newrec.setColumn(oldrec.getColumn());
                newrec.setXFIndex(oldrec.getXFIndex());
                newrec.setSSTIndex(stringid);
                records.add(k, newrec);
            }
        }
        if (HSSFWorkbook.log.check(1)) {
            HSSFWorkbook.log.log(1, "convertLabelRecords exit");
        }
    }
    
    @Override
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this.missingCellPolicy;
    }
    
    @Override
    public void setMissingCellPolicy(final Row.MissingCellPolicy missingCellPolicy) {
        this.missingCellPolicy = missingCellPolicy;
    }
    
    @Override
    public void setSheetOrder(final String sheetname, final int pos) {
        final int oldSheetIndex = this.getSheetIndex(sheetname);
        this._sheets.add(pos, this._sheets.remove(oldSheetIndex));
        this.workbook.setSheetOrder(sheetname, pos);
        final FormulaShifter shifter = FormulaShifter.createForSheetShift(oldSheetIndex, pos);
        for (final HSSFSheet sheet : this._sheets) {
            sheet.getSheet().updateFormulasAfterCellShift(shifter, -1);
        }
        this.workbook.updateNamesAfterCellShift(shifter);
        final int active = this.getActiveSheetIndex();
        if (active == oldSheetIndex) {
            this.setActiveSheet(pos);
        }
        else if (active >= oldSheetIndex || active >= pos) {
            if (active <= oldSheetIndex || active <= pos) {
                if (pos > oldSheetIndex) {
                    this.setActiveSheet(active - 1);
                }
                else {
                    this.setActiveSheet(active + 1);
                }
            }
        }
    }
    
    private void validateSheetIndex(final int index) {
        final int lastSheetIx = this._sheets.size() - 1;
        if (index < 0 || index > lastSheetIx) {
            String range = "(0.." + lastSheetIx + ")";
            if (lastSheetIx == -1) {
                range = "(no sheets)";
            }
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range " + range);
        }
    }
    
    @Override
    public void setSelectedTab(final int index) {
        this.validateSheetIndex(index);
        for (int nSheets = this._sheets.size(), i = 0; i < nSheets; ++i) {
            this.getSheetAt(i).setSelected(i == index);
        }
        this.workbook.getWindowOne().setNumSelectedTabs((short)1);
    }
    
    @Deprecated
    public void setSelectedTab(final short index) {
        this.setSelectedTab((int)index);
    }
    
    public void setSelectedTabs(final int[] indexes) {
        for (int i = 0; i < indexes.length; ++i) {
            this.validateSheetIndex(indexes[i]);
        }
        for (int nSheets = this._sheets.size(), j = 0; j < nSheets; ++j) {
            boolean bSelect = false;
            for (int k = 0; k < indexes.length; ++k) {
                if (indexes[k] == j) {
                    bSelect = true;
                    break;
                }
            }
            this.getSheetAt(j).setSelected(bSelect);
        }
        this.workbook.getWindowOne().setNumSelectedTabs((short)indexes.length);
    }
    
    @Override
    public void setActiveSheet(final int index) {
        this.validateSheetIndex(index);
        for (int nSheets = this._sheets.size(), i = 0; i < nSheets; ++i) {
            this.getSheetAt(i).setActive(i == index);
        }
        this.workbook.getWindowOne().setActiveSheetIndex(index);
    }
    
    @Override
    public int getActiveSheetIndex() {
        return this.workbook.getWindowOne().getActiveSheetIndex();
    }
    
    @Deprecated
    public short getSelectedTab() {
        return (short)this.getActiveSheetIndex();
    }
    
    @Override
    public void setFirstVisibleTab(final int index) {
        this.workbook.getWindowOne().setFirstVisibleTab(index);
    }
    
    @Deprecated
    public void setDisplayedTab(final short index) {
        this.setFirstVisibleTab(index);
    }
    
    @Override
    public int getFirstVisibleTab() {
        return this.workbook.getWindowOne().getFirstVisibleTab();
    }
    
    @Deprecated
    public short getDisplayedTab() {
        return (short)this.getFirstVisibleTab();
    }
    
    @Override
    public void setSheetName(final int sheetIx, final String name) {
        if (name == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        if (this.workbook.doesContainsSheetName(name, sheetIx)) {
            throw new IllegalArgumentException("The workbook already contains a sheet with this name");
        }
        this.validateSheetIndex(sheetIx);
        this.workbook.setSheetName(sheetIx, name);
    }
    
    @Override
    public String getSheetName(final int sheetIndex) {
        this.validateSheetIndex(sheetIndex);
        return this.workbook.getSheetName(sheetIndex);
    }
    
    @Override
    public boolean isHidden() {
        return this.workbook.getWindowOne().getHidden();
    }
    
    @Override
    public void setHidden(final boolean hiddenFlag) {
        this.workbook.getWindowOne().setHidden(hiddenFlag);
    }
    
    @Override
    public boolean isSheetHidden(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        return this.workbook.isSheetHidden(sheetIx);
    }
    
    @Override
    public boolean isSheetVeryHidden(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        return this.workbook.isSheetVeryHidden(sheetIx);
    }
    
    @Override
    public void setSheetHidden(final int sheetIx, final boolean hidden) {
        this.validateSheetIndex(sheetIx);
        this.workbook.setSheetHidden(sheetIx, hidden);
    }
    
    @Override
    public void setSheetHidden(final int sheetIx, final int hidden) {
        this.validateSheetIndex(sheetIx);
        WorkbookUtil.validateSheetState(hidden);
        this.workbook.setSheetHidden(sheetIx, hidden);
    }
    
    @Override
    public int getSheetIndex(final String name) {
        return this.workbook.getSheetIndex(name);
    }
    
    @Override
    public int getSheetIndex(final Sheet sheet) {
        for (int i = 0; i < this._sheets.size(); ++i) {
            if (this._sheets.get(i) == sheet) {
                return i;
            }
        }
        return -1;
    }
    
    @Deprecated
    public int getExternalSheetIndex(final int internalSheetIndex) {
        return this.workbook.checkExternSheet(internalSheetIndex);
    }
    
    @Deprecated
    public String findSheetNameFromExternSheet(final int externSheetIndex) {
        return this.workbook.findSheetFirstNameFromExternSheet(externSheetIndex);
    }
    
    @Deprecated
    public String resolveNameXText(final int refIndex, final int definedNameIndex) {
        return this.workbook.resolveNameXText(refIndex, definedNameIndex);
    }
    
    @Override
    public HSSFSheet createSheet() {
        final HSSFSheet sheet = new HSSFSheet(this);
        this._sheets.add(sheet);
        this.workbook.setSheetName(this._sheets.size() - 1, "Sheet" + (this._sheets.size() - 1));
        final boolean isOnlySheet = this._sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }
    
    @Override
    public HSSFSheet cloneSheet(final int sheetIndex) {
        this.validateSheetIndex(sheetIndex);
        final HSSFSheet srcSheet = this._sheets.get(sheetIndex);
        final String srcName = this.workbook.getSheetName(sheetIndex);
        final HSSFSheet clonedSheet = srcSheet.cloneSheet(this);
        clonedSheet.setSelected(false);
        clonedSheet.setActive(false);
        final String name = this.getUniqueSheetName(srcName);
        final int newSheetIndex = this._sheets.size();
        this._sheets.add(clonedSheet);
        this.workbook.setSheetName(newSheetIndex, name);
        final int filterDbNameIndex = this.findExistingBuiltinNameRecordIdx(sheetIndex, (byte)13);
        if (filterDbNameIndex != -1) {
            final NameRecord newNameRecord = this.workbook.cloneFilter(filterDbNameIndex, newSheetIndex);
            final HSSFName newName = new HSSFName(this, newNameRecord);
            this.names.add(newName);
        }
        return clonedSheet;
    }
    
    private String getUniqueSheetName(final String srcName) {
        int uniqueIndex = 2;
        String baseName = srcName;
        final int bracketPos = srcName.lastIndexOf(40);
        if (bracketPos > 0 && srcName.endsWith(")")) {
            final String suffix = srcName.substring(bracketPos + 1, srcName.length() - ")".length());
            try {
                uniqueIndex = Integer.parseInt(suffix.trim());
                ++uniqueIndex;
                baseName = srcName.substring(0, bracketPos).trim();
            }
            catch (NumberFormatException ex) {}
        }
        String name;
        do {
            final String index = Integer.toString(uniqueIndex++);
            if (baseName.length() + index.length() + 2 < 31) {
                name = baseName + " (" + index + ")";
            }
            else {
                name = baseName.substring(0, 31 - index.length() - 2) + "(" + index + ")";
            }
        } while (this.workbook.getSheetIndex(name) != -1);
        return name;
    }
    
    @Override
    public HSSFSheet createSheet(final String sheetname) {
        if (sheetname == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        if (this.workbook.doesContainsSheetName(sheetname, this._sheets.size())) {
            throw new IllegalArgumentException("The workbook already contains a sheet of this name");
        }
        final HSSFSheet sheet = new HSSFSheet(this);
        this.workbook.setSheetName(this._sheets.size(), sheetname);
        this._sheets.add(sheet);
        final boolean isOnlySheet = this._sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }
    
    @Override
    public int getNumberOfSheets() {
        return this._sheets.size();
    }
    
    @Deprecated
    public int getSheetIndexFromExternSheetIndex(final int externSheetNumber) {
        return this.workbook.getFirstSheetIndexFromExternSheetIndex(externSheetNumber);
    }
    
    private HSSFSheet[] getSheets() {
        final HSSFSheet[] result = new HSSFSheet[this._sheets.size()];
        this._sheets.toArray(result);
        return result;
    }
    
    @Override
    public HSSFSheet getSheetAt(final int index) {
        this.validateSheetIndex(index);
        return this._sheets.get(index);
    }
    
    @Override
    public HSSFSheet getSheet(final String name) {
        HSSFSheet retval = null;
        for (int k = 0; k < this._sheets.size(); ++k) {
            final String sheetname = this.workbook.getSheetName(k);
            if (sheetname.equalsIgnoreCase(name)) {
                retval = this._sheets.get(k);
            }
        }
        return retval;
    }
    
    @Override
    public void removeSheetAt(final int index) {
        this.validateSheetIndex(index);
        final boolean wasSelected = this.getSheetAt(index).isSelected();
        this._sheets.remove(index);
        this.workbook.removeSheet(index);
        final int nSheets = this._sheets.size();
        if (nSheets < 1) {
            return;
        }
        int newSheetIndex = index;
        if (newSheetIndex >= nSheets) {
            newSheetIndex = nSheets - 1;
        }
        if (wasSelected) {
            boolean someOtherSheetIsStillSelected = false;
            for (int i = 0; i < nSheets; ++i) {
                if (this.getSheetAt(i).isSelected()) {
                    someOtherSheetIsStillSelected = true;
                    break;
                }
            }
            if (!someOtherSheetIsStillSelected) {
                this.setSelectedTab(newSheetIndex);
            }
        }
        final int active = this.getActiveSheetIndex();
        if (active == index) {
            this.setActiveSheet(newSheetIndex);
        }
        else if (active > index) {
            this.setActiveSheet(active - 1);
        }
    }
    
    public void setBackupFlag(final boolean backupValue) {
        final BackupRecord backupRecord = this.workbook.getBackupRecord();
        backupRecord.setBackup((short)(backupValue ? 1 : 0));
    }
    
    public boolean getBackupFlag() {
        final BackupRecord backupRecord = this.workbook.getBackupRecord();
        return backupRecord.getBackup() != 0;
    }
    
    @Deprecated
    @Override
    public void setRepeatingRowsAndColumns(final int sheetIndex, final int startColumn, final int endColumn, final int startRow, final int endRow) {
        final HSSFSheet sheet = this.getSheetAt(sheetIndex);
        CellRangeAddress rows = null;
        CellRangeAddress cols = null;
        if (startRow != -1) {
            rows = new CellRangeAddress(startRow, endRow, -1, -1);
        }
        if (startColumn != -1) {
            cols = new CellRangeAddress(-1, -1, startColumn, endColumn);
        }
        sheet.setRepeatingRows(rows);
        sheet.setRepeatingColumns(cols);
    }
    
    int findExistingBuiltinNameRecordIdx(final int sheetIndex, final byte builtinCode) {
        for (int defNameIndex = 0; defNameIndex < this.names.size(); ++defNameIndex) {
            final NameRecord r = this.workbook.getNameRecord(defNameIndex);
            if (r == null) {
                throw new RuntimeException("Unable to find all defined names to iterate over");
            }
            if (r.isBuiltInName()) {
                if (r.getBuiltInName() == builtinCode) {
                    if (r.getSheetNumber() - 1 == sheetIndex) {
                        return defNameIndex;
                    }
                }
            }
        }
        return -1;
    }
    
    HSSFName createBuiltInName(final byte builtinCode, final int sheetIndex) {
        final NameRecord nameRecord = this.workbook.createBuiltInName(builtinCode, sheetIndex + 1);
        final HSSFName newName = new HSSFName(this, nameRecord, null);
        this.names.add(newName);
        return newName;
    }
    
    HSSFName getBuiltInName(final byte builtinCode, final int sheetIndex) {
        final int index = this.findExistingBuiltinNameRecordIdx(sheetIndex, builtinCode);
        if (index < 0) {
            return null;
        }
        return this.names.get(index);
    }
    
    @Override
    public HSSFFont createFont() {
        this.workbook.createNewFont();
        short fontindex = (short)(this.getNumberOfFonts() - 1);
        if (fontindex > 3) {
            ++fontindex;
        }
        if (fontindex == 32767) {
            throw new IllegalArgumentException("Maximum number of fonts was exceeded");
        }
        return this.getFontAt(fontindex);
    }
    
    @Override
    public HSSFFont findFont(final short boldWeight, final short color, final short fontHeight, final String name, final boolean italic, final boolean strikeout, final short typeOffset, final byte underline) {
        for (short i = 0; i <= this.getNumberOfFonts(); ++i) {
            if (i != 4) {
                final HSSFFont hssfFont = this.getFontAt(i);
                if (hssfFont.getBoldweight() == boldWeight && hssfFont.getColor() == color && hssfFont.getFontHeight() == fontHeight && hssfFont.getFontName().equals(name) && hssfFont.getItalic() == italic && hssfFont.getStrikeout() == strikeout && hssfFont.getTypeOffset() == typeOffset && hssfFont.getUnderline() == underline) {
                    return hssfFont;
                }
            }
        }
        return null;
    }
    
    @Override
    public short getNumberOfFonts() {
        return (short)this.workbook.getNumberOfFontRecords();
    }
    
    @Override
    public HSSFFont getFontAt(final short idx) {
        if (this.fonts == null) {
            this.fonts = new Hashtable<Short, HSSFFont>();
        }
        final Short sIdx = idx;
        if (this.fonts.containsKey(sIdx)) {
            return this.fonts.get(sIdx);
        }
        final FontRecord font = this.workbook.getFontRecordAt(idx);
        final HSSFFont retval = new HSSFFont(idx, font);
        this.fonts.put(sIdx, retval);
        return retval;
    }
    
    protected void resetFontCache() {
        this.fonts = new Hashtable<Short, HSSFFont>();
    }
    
    @Override
    public HSSFCellStyle createCellStyle() {
        if (this.workbook.getNumExFormats() == 4030) {
            throw new IllegalStateException("The maximum number of cell styles was exceeded. You can define up to 4000 styles in a .xls workbook");
        }
        final ExtendedFormatRecord xfr = this.workbook.createCellXF();
        final short index = (short)(this.getNumCellStyles() - 1);
        final HSSFCellStyle style = new HSSFCellStyle(index, xfr, this);
        return style;
    }
    
    @Override
    public short getNumCellStyles() {
        return (short)this.workbook.getNumExFormats();
    }
    
    @Override
    public HSSFCellStyle getCellStyleAt(final short idx) {
        final ExtendedFormatRecord xfr = this.workbook.getExFormatAt(idx);
        final HSSFCellStyle style = new HSSFCellStyle(idx, xfr, this);
        return style;
    }
    
    @Override
    public void close() throws IOException {
        if (this.directory != null && this.directory.getNFileSystem() != null) {
            this.directory.getNFileSystem().close();
            this.directory = null;
        }
    }
    
    @Override
    public void write(final OutputStream stream) throws IOException {
        final byte[] bytes = this.getBytes();
        final POIFSFileSystem fs = new POIFSFileSystem();
        final List<String> excepts = new ArrayList<String>(1);
        fs.createDocument(new ByteArrayInputStream(bytes), "Workbook");
        this.writeProperties(fs, excepts);
        if (this.preserveNodes) {
            excepts.addAll(Arrays.asList(HSSFWorkbook.WORKBOOK_DIR_ENTRY_NAMES));
            EntryUtils.copyNodes(new FilteringDirectoryNode(this.directory, excepts), new FilteringDirectoryNode(fs.getRoot(), excepts));
            fs.getRoot().setStorageClsid(this.directory.getStorageClsid());
        }
        fs.writeFilesystem(stream);
    }
    
    public byte[] getBytes() {
        if (HSSFWorkbook.log.check(1)) {
            HSSFWorkbook.log.log(1, "HSSFWorkbook.getBytes()");
        }
        final HSSFSheet[] sheets = this.getSheets();
        final int nSheets = sheets.length;
        this.workbook.preSerialize();
        for (int i = 0; i < nSheets; ++i) {
            sheets[i].getSheet().preSerialize();
            sheets[i].preSerialize();
        }
        int totalsize = this.workbook.getSize();
        final SheetRecordCollector[] srCollectors = new SheetRecordCollector[nSheets];
        for (int k = 0; k < nSheets; ++k) {
            this.workbook.setSheetBof(k, totalsize);
            final SheetRecordCollector src = new SheetRecordCollector();
            sheets[k].getSheet().visitContainedRecords(src, totalsize);
            totalsize += src.getTotalSize();
            srCollectors[k] = src;
        }
        final byte[] retval = new byte[totalsize];
        int pos = this.workbook.serialize(0, retval);
        for (int j = 0; j < nSheets; ++j) {
            final SheetRecordCollector src2 = srCollectors[j];
            final int serializedSize = src2.serialize(pos, retval);
            if (serializedSize != src2.getTotalSize()) {
                throw new IllegalStateException("Actual serialized sheet size (" + serializedSize + ") differs from pre-calculated size (" + src2.getTotalSize() + ") for sheet (" + j + ")");
            }
            pos += serializedSize;
        }
        return retval;
    }
    
    @Deprecated
    public int addSSTString(final String string) {
        return this.workbook.addSSTString(new UnicodeString(string));
    }
    
    @Deprecated
    public String getSSTString(final int index) {
        return this.workbook.getSSTString(index).getString();
    }
    
    InternalWorkbook getWorkbook() {
        return this.workbook;
    }
    
    @Override
    public int getNumberOfNames() {
        final int result = this.names.size();
        return result;
    }
    
    @Override
    public HSSFName getName(final String name) {
        final int nameIndex = this.getNameIndex(name);
        if (nameIndex < 0) {
            return null;
        }
        return this.names.get(nameIndex);
    }
    
    @Override
    public HSSFName getNameAt(final int nameIndex) {
        final int nNames = this.names.size();
        if (nNames < 1) {
            throw new IllegalStateException("There are no defined names in this workbook");
        }
        if (nameIndex < 0 || nameIndex > nNames) {
            throw new IllegalArgumentException("Specified name index " + nameIndex + " is outside the allowable range (0.." + (nNames - 1) + ").");
        }
        return this.names.get(nameIndex);
    }
    
    public NameRecord getNameRecord(final int nameIndex) {
        return this.getWorkbook().getNameRecord(nameIndex);
    }
    
    public String getNameName(final int index) {
        final String result = this.getNameAt(index).getNameName();
        return result;
    }
    
    @Override
    public void setPrintArea(final int sheetIndex, final String reference) {
        NameRecord name = this.workbook.getSpecificBuiltinRecord((byte)6, sheetIndex + 1);
        if (name == null) {
            name = this.workbook.createBuiltInName((byte)6, sheetIndex + 1);
        }
        final String[] parts = HSSFWorkbook.COMMA_PATTERN.split(reference);
        final StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < parts.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            SheetNameFormatter.appendFormat(sb, this.getSheetName(sheetIndex));
            sb.append("!");
            sb.append(parts[i]);
        }
        name.setNameDefinition(HSSFFormulaParser.parse(sb.toString(), this, 4, sheetIndex));
    }
    
    @Override
    public void setPrintArea(final int sheetIndex, final int startColumn, final int endColumn, final int startRow, final int endRow) {
        CellReference cell = new CellReference(startRow, startColumn, true, true);
        String reference = cell.formatAsString();
        cell = new CellReference(endRow, endColumn, true, true);
        reference = reference + ":" + cell.formatAsString();
        this.setPrintArea(sheetIndex, reference);
    }
    
    @Override
    public String getPrintArea(final int sheetIndex) {
        final NameRecord name = this.workbook.getSpecificBuiltinRecord((byte)6, sheetIndex + 1);
        if (name == null) {
            return null;
        }
        return HSSFFormulaParser.toFormulaString(this, name.getNameDefinition());
    }
    
    @Override
    public void removePrintArea(final int sheetIndex) {
        this.getWorkbook().removeBuiltinRecord((byte)6, sheetIndex + 1);
    }
    
    @Override
    public HSSFName createName() {
        final NameRecord nameRecord = this.workbook.createName();
        final HSSFName newName = new HSSFName(this, nameRecord);
        this.names.add(newName);
        return newName;
    }
    
    @Override
    public int getNameIndex(final String name) {
        for (int k = 0; k < this.names.size(); ++k) {
            final String nameName = this.getNameName(k);
            if (nameName.equalsIgnoreCase(name)) {
                return k;
            }
        }
        return -1;
    }
    
    int getNameIndex(final HSSFName name) {
        for (int k = 0; k < this.names.size(); ++k) {
            if (name == this.names.get(k)) {
                return k;
            }
        }
        return -1;
    }
    
    @Override
    public void removeName(final int index) {
        this.names.remove(index);
        this.workbook.removeName(index);
    }
    
    @Override
    public HSSFDataFormat createDataFormat() {
        if (this.formatter == null) {
            this.formatter = new HSSFDataFormat(this.workbook);
        }
        return this.formatter;
    }
    
    @Override
    public void removeName(final String name) {
        final int index = this.getNameIndex(name);
        this.removeName(index);
    }
    
    void removeName(final HSSFName name) {
        final int index = this.getNameIndex(name);
        this.removeName(index);
    }
    
    public HSSFPalette getCustomPalette() {
        return new HSSFPalette(this.workbook.getCustomPalette());
    }
    
    public void insertChartRecord() {
        final int loc = this.workbook.findFirstRecordLocBySid((short)252);
        final byte[] data = { 15, 0, 0, -16, 82, 0, 0, 0, 0, 0, 6, -16, 24, 0, 0, 0, 1, 8, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 51, 0, 11, -16, 18, 0, 0, 0, -65, 0, 8, 0, 8, 0, -127, 1, 9, 0, 0, 8, -64, 1, 64, 0, 0, 8, 64, 0, 30, -15, 16, 0, 0, 0, 13, 0, 0, 8, 12, 0, 0, 8, 23, 0, 0, 8, -9, 0, 0, 16 };
        final UnknownRecord r = new UnknownRecord(235, data);
        this.workbook.getRecords().add(loc, r);
    }
    
    public void dumpDrawingGroupRecords(final boolean fat) {
        final DrawingGroupRecord r = (DrawingGroupRecord)this.workbook.findFirstRecordBySid((short)235);
        r.decode();
        final List<EscherRecord> escherRecords = r.getEscherRecords();
        final PrintWriter w = new PrintWriter(System.out);
        for (final EscherRecord escherRecord : escherRecords) {
            if (fat) {
                System.out.println(escherRecord.toString());
            }
            else {
                escherRecord.display(w, 0);
            }
        }
        w.flush();
    }
    
    void initDrawings() {
        final DrawingManager2 mgr = this.workbook.findDrawingGroup();
        if (mgr != null) {
            for (int i = 0; i < this.getNumberOfSheets(); ++i) {
                this.getSheetAt(i).getDrawingPatriarch();
            }
        }
        else {
            this.workbook.createDrawingGroup();
        }
    }
    
    @Override
    public int addPicture(byte[] pictureData, final int format) {
        this.initDrawings();
        final byte[] uid = DigestUtils.md5(pictureData);
        EscherBlipRecord blipRecord = null;
        int blipSize = 0;
        short escherTag = 0;
        switch (format) {
            case 3: {
                if (LittleEndian.getInt(pictureData) == -1698247209) {
                    final byte[] picDataNoHeader = new byte[pictureData.length - 22];
                    System.arraycopy(pictureData, 22, picDataNoHeader, 0, pictureData.length - 22);
                    pictureData = picDataNoHeader;
                }
            }
            case 2: {
                final EscherMetafileBlip blipRecordMeta = (EscherMetafileBlip)(blipRecord = new EscherMetafileBlip());
                blipRecordMeta.setUID(uid);
                blipRecordMeta.setPictureData(pictureData);
                blipRecordMeta.setFilter((byte)(-2));
                blipSize = blipRecordMeta.getCompressedSize() + 58;
                escherTag = 0;
                break;
            }
            default: {
                final EscherBitmapBlip blipRecordBitmap = (EscherBitmapBlip)(blipRecord = new EscherBitmapBlip());
                blipRecordBitmap.setUID(uid);
                blipRecordBitmap.setMarker((byte)(-1));
                blipRecordBitmap.setPictureData(pictureData);
                blipSize = pictureData.length + 25;
                escherTag = 255;
                break;
            }
        }
        blipRecord.setRecordId((short)(-4072 + format));
        switch (format) {
            case 2: {
                blipRecord.setOptions((short)15680);
                break;
            }
            case 3: {
                blipRecord.setOptions((short)8544);
                break;
            }
            case 4: {
                blipRecord.setOptions((short)21536);
                break;
            }
            case 6: {
                blipRecord.setOptions((short)28160);
                break;
            }
            case 5: {
                blipRecord.setOptions((short)18080);
                break;
            }
            case 7: {
                blipRecord.setOptions((short)31360);
                break;
            }
        }
        final EscherBSERecord r = new EscherBSERecord();
        r.setRecordId((short)(-4089));
        r.setOptions((short)(0x2 | format << 4));
        r.setBlipTypeMacOS((byte)format);
        r.setBlipTypeWin32((byte)format);
        r.setUid(uid);
        r.setTag(escherTag);
        r.setSize(blipSize);
        r.setRef(0);
        r.setOffset(0);
        r.setBlipRecord(blipRecord);
        return this.workbook.addBSERecord(r);
    }
    
    @Override
    public List<HSSFPictureData> getAllPictures() {
        final List<HSSFPictureData> pictures = new ArrayList<HSSFPictureData>();
        for (final Record r : this.workbook.getRecords()) {
            if (r instanceof AbstractEscherHolderRecord) {
                ((AbstractEscherHolderRecord)r).decode();
                final List<EscherRecord> escherRecords = ((AbstractEscherHolderRecord)r).getEscherRecords();
                this.searchForPictures(escherRecords, pictures);
            }
        }
        return pictures;
    }
    
    private void searchForPictures(final List<EscherRecord> escherRecords, final List<HSSFPictureData> pictures) {
        for (final EscherRecord escherRecord : escherRecords) {
            if (escherRecord instanceof EscherBSERecord) {
                final EscherBlipRecord blip = ((EscherBSERecord)escherRecord).getBlipRecord();
                if (blip != null) {
                    final HSSFPictureData picture = new HSSFPictureData(blip);
                    pictures.add(picture);
                }
            }
            this.searchForPictures(escherRecord.getChildRecords(), pictures);
        }
    }
    
    protected static Map<String, ClassID> getOleMap() {
        final Map<String, ClassID> olemap = new HashMap<String, ClassID>();
        olemap.put("PowerPoint Document", ClassID.PPT_SHOW);
        for (final String str : HSSFWorkbook.WORKBOOK_DIR_ENTRY_NAMES) {
            olemap.put(str, ClassID.XLS_WORKBOOK);
        }
        return olemap;
    }
    
    public int addOlePackage(final POIFSFileSystem poiData, final String label, final String fileName, final String command) throws IOException {
        final DirectoryNode root = poiData.getRoot();
        final Map<String, ClassID> olemap = getOleMap();
        for (final Map.Entry<String, ClassID> entry : olemap.entrySet()) {
            if (root.hasEntry(entry.getKey())) {
                root.setStorageClsid(entry.getValue());
                break;
            }
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        poiData.writeFilesystem(bos);
        return this.addOlePackage(bos.toByteArray(), label, fileName, command);
    }
    
    public int addOlePackage(final byte[] oleData, final String label, final String fileName, final String command) throws IOException {
        if (this.directory == null) {
            this.directory = new POIFSFileSystem().getRoot();
            this.preserveNodes = true;
        }
        int storageId = 0;
        DirectoryEntry oleDir = null;
        do {
            final String storageStr = "MBD" + HexDump.toHex(++storageId);
            if (!this.directory.hasEntry(storageStr)) {
                oleDir = this.directory.createDirectory(storageStr);
                oleDir.setStorageClsid(ClassID.OLE10_PACKAGE);
            }
        } while (oleDir == null);
        final byte[] oleBytes = { 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        oleDir.createDocument("\u0001Ole", new ByteArrayInputStream(oleBytes));
        final Ole10Native oleNative = new Ole10Native(label, fileName, command, oleData);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        oleNative.writeOut(bos);
        oleDir.createDocument("\u0001Ole10Native", new ByteArrayInputStream(bos.toByteArray()));
        return storageId;
    }
    
    @Override
    public int linkExternalWorkbook(final String name, final Workbook workbook) {
        return this.workbook.linkExternalWorkbook(name, workbook);
    }
    
    public boolean isWriteProtected() {
        return this.workbook.isWriteProtected();
    }
    
    public void writeProtectWorkbook(final String password, final String username) {
        this.workbook.writeProtectWorkbook(password, username);
    }
    
    public void unwriteProtectWorkbook() {
        this.workbook.unwriteProtectWorkbook();
    }
    
    public List<HSSFObjectData> getAllEmbeddedObjects() {
        final List<HSSFObjectData> objects = new ArrayList<HSSFObjectData>();
        for (int i = 0; i < this.getNumberOfSheets(); ++i) {
            this.getAllEmbeddedObjects(this.getSheetAt(i), objects);
        }
        return objects;
    }
    
    private void getAllEmbeddedObjects(final HSSFSheet sheet, final List<HSSFObjectData> objects) {
        final HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
        if (null == patriarch) {
            return;
        }
        this.getAllEmbeddedObjects(patriarch, objects);
    }
    
    private void getAllEmbeddedObjects(final HSSFShapeContainer parent, final List<HSSFObjectData> objects) {
        for (final HSSFShape shape : parent.getChildren()) {
            if (shape instanceof HSSFObjectData) {
                objects.add((HSSFObjectData)shape);
            }
            else {
                if (!(shape instanceof HSSFShapeContainer)) {
                    continue;
                }
                this.getAllEmbeddedObjects((HSSFShapeContainer)shape, objects);
            }
        }
    }
    
    @Override
    public HSSFCreationHelper getCreationHelper() {
        return new HSSFCreationHelper(this);
    }
    
    UDFFinder getUDFFinder() {
        return this._udfFinder;
    }
    
    @Override
    public void addToolPack(final UDFFinder toopack) {
        final AggregatingUDFFinder udfs = (AggregatingUDFFinder)this._udfFinder;
        udfs.add(toopack);
    }
    
    @Override
    public void setForceFormulaRecalculation(final boolean value) {
        final InternalWorkbook iwb = this.getWorkbook();
        final RecalcIdRecord recalc = iwb.getRecalcId();
        recalc.setEngineId(0);
    }
    
    @Override
    public boolean getForceFormulaRecalculation() {
        final InternalWorkbook iwb = this.getWorkbook();
        final RecalcIdRecord recalc = (RecalcIdRecord)iwb.findFirstRecordBySid((short)449);
        return recalc != null && recalc.getEngineId() != 0;
    }
    
    public boolean changeExternalReference(final String oldUrl, final String newUrl) {
        return this.workbook.changeExternalReference(oldUrl, newUrl);
    }
    
    public DirectoryNode getRootDirectory() {
        return this.directory;
    }
    
    static {
        COMMA_PATTERN = Pattern.compile(",");
        INITIAL_CAPACITY = Configurator.getIntValue("HSSFWorkbook.SheetInitialCapacity", 3);
        HSSFWorkbook.log = POILogFactory.getLogger(HSSFWorkbook.class);
        WORKBOOK_DIR_ENTRY_NAMES = new String[] { "Workbook", "WORKBOOK", "BOOK" };
    }
    
    private static final class SheetRecordCollector implements RecordAggregate.RecordVisitor
    {
        private List<Record> _list;
        private int _totalSize;
        
        public SheetRecordCollector() {
            this._totalSize = 0;
            this._list = new ArrayList<Record>(128);
        }
        
        public int getTotalSize() {
            return this._totalSize;
        }
        
        @Override
        public void visitRecord(final Record r) {
            this._list.add(r);
            this._totalSize += r.getRecordSize();
        }
        
        public int serialize(final int offset, final byte[] data) {
            int result = 0;
            for (int nRecs = this._list.size(), i = 0; i < nRecs; ++i) {
                final Record rec = this._list.get(i);
                result += rec.serialize(offset + result, data);
            }
            return result;
        }
    }
}
