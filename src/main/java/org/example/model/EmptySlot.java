package org.example.model;

public class EmptySlot {
//    private Integer id;

    private boolean isDenominator;
    private String startTime;
    private String endTime;
    private String weekDayNumber;

    public EmptySlot(boolean isDenominator, String startTime, String endTime, String weekDayNumber) {
        this.isDenominator = isDenominator;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDayNumber = weekDayNumber;
    }

    public boolean isDenominator() {
        return isDenominator;
    }

    public void setDenominator(boolean denominator) {
        isDenominator = denominator;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getWeekDayNumber() {
        return weekDayNumber;
    }

    public void setWeekDayNumber(String weekDayNumber) {
        this.weekDayNumber = weekDayNumber;
    }
}
