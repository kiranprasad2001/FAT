// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface PrintSetup
{
    public static final short PRINTER_DEFAULT_PAPERSIZE = 0;
    public static final short LETTER_PAPERSIZE = 1;
    public static final short LETTER_SMALL_PAGESIZE = 2;
    public static final short TABLOID_PAPERSIZE = 3;
    public static final short LEDGER_PAPERSIZE = 4;
    public static final short LEGAL_PAPERSIZE = 5;
    public static final short STATEMENT_PAPERSIZE = 6;
    public static final short EXECUTIVE_PAPERSIZE = 7;
    public static final short A3_PAPERSIZE = 8;
    public static final short A4_PAPERSIZE = 9;
    public static final short A4_SMALL_PAPERSIZE = 10;
    public static final short A5_PAPERSIZE = 11;
    public static final short B4_PAPERSIZE = 12;
    public static final short B5_PAPERSIZE = 13;
    public static final short FOLIO8_PAPERSIZE = 14;
    public static final short QUARTO_PAPERSIZE = 15;
    public static final short TEN_BY_FOURTEEN_PAPERSIZE = 16;
    public static final short ELEVEN_BY_SEVENTEEN_PAPERSIZE = 17;
    public static final short NOTE8_PAPERSIZE = 18;
    public static final short ENVELOPE_9_PAPERSIZE = 19;
    public static final short ENVELOPE_10_PAPERSIZE = 20;
    public static final short ENVELOPE_DL_PAPERSIZE = 27;
    public static final short ENVELOPE_CS_PAPERSIZE = 28;
    public static final short ENVELOPE_C5_PAPERSIZE = 28;
    public static final short ENVELOPE_C3_PAPERSIZE = 29;
    public static final short ENVELOPE_C4_PAPERSIZE = 30;
    public static final short ENVELOPE_C6_PAPERSIZE = 31;
    public static final short ENVELOPE_MONARCH_PAPERSIZE = 37;
    public static final short A4_EXTRA_PAPERSIZE = 53;
    public static final short A4_TRANSVERSE_PAPERSIZE = 55;
    public static final short A4_PLUS_PAPERSIZE = 60;
    public static final short LETTER_ROTATED_PAPERSIZE = 75;
    public static final short A4_ROTATED_PAPERSIZE = 77;
    
    void setPaperSize(final short p0);
    
    void setScale(final short p0);
    
    void setPageStart(final short p0);
    
    void setFitWidth(final short p0);
    
    void setFitHeight(final short p0);
    
    void setLeftToRight(final boolean p0);
    
    void setLandscape(final boolean p0);
    
    void setValidSettings(final boolean p0);
    
    void setNoColor(final boolean p0);
    
    void setDraft(final boolean p0);
    
    void setNotes(final boolean p0);
    
    void setNoOrientation(final boolean p0);
    
    void setUsePage(final boolean p0);
    
    void setHResolution(final short p0);
    
    void setVResolution(final short p0);
    
    void setHeaderMargin(final double p0);
    
    void setFooterMargin(final double p0);
    
    void setCopies(final short p0);
    
    short getPaperSize();
    
    short getScale();
    
    short getPageStart();
    
    short getFitWidth();
    
    short getFitHeight();
    
    boolean getLeftToRight();
    
    boolean getLandscape();
    
    boolean getValidSettings();
    
    boolean getNoColor();
    
    boolean getDraft();
    
    boolean getNotes();
    
    boolean getNoOrientation();
    
    boolean getUsePage();
    
    short getHResolution();
    
    short getVResolution();
    
    double getHeaderMargin();
    
    double getFooterMargin();
    
    short getCopies();
}
