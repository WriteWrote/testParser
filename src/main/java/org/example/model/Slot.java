package org.example.model;

public class Slot {
//    private Integer id;

    private boolean isDenominator;
    private String startTime;
    private String endTime;
    private Integer weekDayNumber;

    public Slot(boolean isDenominator, String startTime, String endTime, Integer weekDayNumber) {
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

    public Integer getWeekDayNumber() {
        return weekDayNumber;
    }

    public void setWeekDayNumber(Integer weekDayNumber) {
        this.weekDayNumber = weekDayNumber;
    }
}
