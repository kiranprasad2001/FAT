// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;

public class WorkdayCalculator
{
    public static final WorkdayCalculator instance;
    
    private WorkdayCalculator() {
    }
    
    public int calculateWorkdays(final double start, final double end, final double[] holidays) {
        final int saturdaysPast = this.pastDaysOfWeek(start, end, 7);
        final int sundaysPast = this.pastDaysOfWeek(start, end, 1);
        final int nonWeekendHolidays = this.calculateNonWeekendHolidays(start, end, holidays);
        return (int)(end - start + 1.0) - saturdaysPast - sundaysPast - nonWeekendHolidays;
    }
    
    public Date calculateWorkdays(final double start, int workdays, final double[] holidays) {
        final Date startDate = DateUtil.getJavaDate(start);
        final int direction = (workdays < 0) ? -1 : 1;
        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate);
        double excelEndDate = DateUtil.getExcelDate(endDate.getTime());
        while (workdays != 0) {
            endDate.add(6, direction);
            excelEndDate += direction;
            if (endDate.get(7) != 7 && endDate.get(7) != 1 && !this.isHoliday(excelEndDate, holidays)) {
                workdays -= direction;
            }
        }
        return endDate.getTime();
    }
    
    protected int pastDaysOfWeek(final double start, final double end, final int dayOfWeek) {
        int pastDaysOfWeek = 0;
        for (int startDay = (int)Math.floor((start < end) ? start : end), endDay = (int)Math.floor((end > start) ? end : start); startDay <= endDay; ++startDay) {
            final Calendar today = Calendar.getInstance();
            today.setTime(DateUtil.getJavaDate(startDay));
            if (today.get(7) == dayOfWeek) {
                ++pastDaysOfWeek;
            }
        }
        return (start < end) ? pastDaysOfWeek : (-pastDaysOfWeek);
    }
    
    protected int calculateNonWeekendHolidays(final double start, final double end, final double[] holidays) {
        int nonWeekendHolidays = 0;
        final double startDay = (start < end) ? start : end;
        final double endDay = (end > start) ? end : start;
        for (int i = 0; i < holidays.length; ++i) {
            if (this.isInARange(startDay, endDay, holidays[i]) && !this.isWeekend(holidays[i])) {
                ++nonWeekendHolidays;
            }
        }
        return (start < end) ? nonWeekendHolidays : (-nonWeekendHolidays);
    }
    
    protected boolean isWeekend(final double aDate) {
        final Calendar date = Calendar.getInstance();
        date.setTime(DateUtil.getJavaDate(aDate));
        return date.get(7) == 7 || date.get(7) == 1;
    }
    
    protected boolean isHoliday(final double aDate, final double[] holidays) {
        for (int i = 0; i < holidays.length; ++i) {
            if (Math.round(holidays[i]) == Math.round(aDate)) {
                return true;
            }
        }
        return false;
    }
    
    protected int isNonWorkday(final double aDate, final double[] holidays) {
        return (this.isWeekend(aDate) || this.isHoliday(aDate, holidays)) ? 1 : 0;
    }
    
    protected boolean isInARange(final double start, final double end, final double aDate) {
        return aDate >= start && aDate <= end;
    }
    
    static {
        instance = new WorkdayCalculator();
    }
}
