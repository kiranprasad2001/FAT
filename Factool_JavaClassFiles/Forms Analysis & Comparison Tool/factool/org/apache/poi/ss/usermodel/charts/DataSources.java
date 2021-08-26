// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Sheet;

public class DataSources
{
    private DataSources() {
    }
    
    public static <T> ChartDataSource<T> fromArray(final T[] elements) {
        return new ArrayDataSource<T>(elements);
    }
    
    public static ChartDataSource<Number> fromNumericCellRange(final Sheet sheet, final CellRangeAddress cellRangeAddress) {
        return new AbstractCellRangeDataSource<Number>(sheet, cellRangeAddress) {
            @Override
            public Number getPointAt(final int index) {
                final CellValue cellValue = this.getCellValueAt(index);
                if (cellValue != null && cellValue.getCellType() == 0) {
                    return cellValue.getNumberValue();
                }
                return null;
            }
            
            @Override
            public boolean isNumeric() {
                return true;
            }
        };
    }
    
    public static ChartDataSource<String> fromStringCellRange(final Sheet sheet, final CellRangeAddress cellRangeAddress) {
        return new AbstractCellRangeDataSource<String>(sheet, cellRangeAddress) {
            @Override
            public String getPointAt(final int index) {
                final CellValue cellValue = this.getCellValueAt(index);
                if (cellValue != null && cellValue.getCellType() == 1) {
                    return cellValue.getStringValue();
                }
                return null;
            }
            
            @Override
            public boolean isNumeric() {
                return false;
            }
        };
    }
    
    private static class ArrayDataSource<T> implements ChartDataSource<T>
    {
        private final T[] elements;
        
        public ArrayDataSource(final T[] elements) {
            this.elements = elements;
        }
        
        @Override
        public int getPointCount() {
            return this.elements.length;
        }
        
        @Override
        public T getPointAt(final int index) {
            return this.elements[index];
        }
        
        @Override
        public boolean isReference() {
            return false;
        }
        
        @Override
        public boolean isNumeric() {
            final Class<?> arrayComponentType = this.elements.getClass().getComponentType();
            return Number.class.isAssignableFrom(arrayComponentType);
        }
        
        @Override
        public String getFormulaString() {
            throw new UnsupportedOperationException("Literal data source can not be expressed by reference.");
        }
    }
    
    private abstract static class AbstractCellRangeDataSource<T> implements ChartDataSource<T>
    {
        private final Sheet sheet;
        private final CellRangeAddress cellRangeAddress;
        private final int numOfCells;
        private FormulaEvaluator evaluator;
        
        protected AbstractCellRangeDataSource(final Sheet sheet, final CellRangeAddress cellRangeAddress) {
            this.sheet = sheet;
            this.cellRangeAddress = cellRangeAddress.copy();
            this.numOfCells = this.cellRangeAddress.getNumberOfCells();
            this.evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        }
        
        @Override
        public int getPointCount() {
            return this.numOfCells;
        }
        
        @Override
        public boolean isReference() {
            return true;
        }
        
        @Override
        public String getFormulaString() {
            return this.cellRangeAddress.formatAsString(this.sheet.getSheetName(), true);
        }
        
        protected CellValue getCellValueAt(final int index) {
            if (index < 0 || index >= this.numOfCells) {
                throw new IndexOutOfBoundsException("Index must be between 0 and " + (this.numOfCells - 1) + " (inclusive), given: " + index);
            }
            final int firstRow = this.cellRangeAddress.getFirstRow();
            final int firstCol = this.cellRangeAddress.getFirstColumn();
            final int lastCol = this.cellRangeAddress.getLastColumn();
            final int width = lastCol - firstCol + 1;
            final int rowIndex = firstRow + index / width;
            final int cellIndex = firstCol + index % width;
            final Row row = this.sheet.getRow(rowIndex);
            return (row == null) ? null : this.evaluator.evaluate(row.getCell(cellIndex));
        }
    }
}
