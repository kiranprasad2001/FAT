// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

public enum CellFormatType
{
    GENERAL {
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellGeneralFormatter();
        }
        
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
    }, 
    NUMBER {
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellNumberFormatter(pattern);
        }
    }, 
    DATE {
        @Override
        boolean isSpecial(final char ch) {
            return ch == '\'' || (ch <= '\u007f' && Character.isLetter(ch));
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellDateFormatter(pattern);
        }
    }, 
    ELAPSED {
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellElapsedFormatter(pattern);
        }
    }, 
    TEXT {
        @Override
        boolean isSpecial(final char ch) {
            return false;
        }
        
        @Override
        CellFormatter formatter(final String pattern) {
            return new CellTextFormatter(pattern);
        }
    };
    
    abstract boolean isSpecial(final char p0);
    
    abstract CellFormatter formatter(final String p0);
}
