package com.example.a368.ui.today;

/*
Created by: Jiwon Choi
This is a schedule object for the user to save these data into the database.
It is integrating with Firebase, and saved as the object itself.
 */
public class Schedule {
    // Attributes for a single schedule
    private String name;
    private String start_time;
    private String start_date;
    private String end_time;
    private String end_date;
    private String description;

    // NULL Constructor
    public Schedule() {

    }

    // Constructor
    public Schedule(String name, String start_time, String start_date, String end_time, String end_date, String description) {
        this.name = name;
        this.start_time = start_time;
        this.start_date = start_date;
        this.end_time = end_time;
        this.end_date = end_date;
        this.description = description;
    }
    // end of constructor

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    // end of getters & setters
}
