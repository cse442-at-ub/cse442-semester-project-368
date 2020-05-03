package com.example.a368.ui.appointment_meeting;

import android.os.Parcel;
import android.os.Parcelable;

public class TimePair implements Parcelable {
    private int startTime;
    private int endTime;

    protected TimePair(Parcel in) {
        startTime = in.readInt();
        endTime = in.readInt();
    }

    public static final Creator<TimePair> CREATOR = new Creator<TimePair>() {
        @Override
        public TimePair createFromParcel(Parcel in) {
            return new TimePair(in);
        }

        @Override
        public TimePair[] newArray(int size) {
            return new TimePair[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startTime);
        dest.writeInt(endTime);
    }
}
