package com.example.a368.ui.appointment_meeting;

public class TimePair {
    private int startTime;
    private int endTime;

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public TimePair(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
