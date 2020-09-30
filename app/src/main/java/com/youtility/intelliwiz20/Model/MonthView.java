package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 19/1/18.
 * represent user present or absent days in month view
 */

public class MonthView {
    private int dayNumber;
    private int dayType;
    private int presentDayType;
    private String dutyTypeName;
    private String dutyTypeShift;

    public String getDutyTypeShift() {
        return dutyTypeShift;
    }

    public void setDutyTypeShift(String dutyTypeShift) {
        this.dutyTypeShift = dutyTypeShift;
    }

    public String getDutyTypeName() {
        return dutyTypeName;
    }

    public void setDutyTypeName(String dutyTypeName) {
        this.dutyTypeName = dutyTypeName;
    }

    public int getPresentDayType() {
        return presentDayType;
    }

    public void setPresentDayType(int presentDayType) {
        this.presentDayType = presentDayType;
    }



    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getDayType() {
        return dayType;
    }

    public void setDayType(int dayType) {
        this.dayType = dayType;
    }
}
