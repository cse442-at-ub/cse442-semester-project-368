package com.example.a368.ui.appointment_meeting;

public class MeetingRequest {

    private int id;
    private String sender_name;
    private String sender_email;
    private String receiver_name;
    private String receiver_email;
    private String status;
    private String title;
    private String start_date;
    private String start_time;
    private String end_date;
    private String end_time;
    private String description;

    // NULL Constructor
    public MeetingRequest() {

    }

    // Constructor including id (auto-incremented) value
    public MeetingRequest(int id, String sender_name, String sender_email, String receiver_name,
                          String receiver_email, String status, String title, String start_date,
                          String start_time, String end_date, String end_time, String description) {
        this.id = id;
        this.sender_name = sender_name;
        this.sender_email = sender_email;
        this.receiver_name = receiver_name;
        this.receiver_email = receiver_email;
        this.status = status;
        this.title = title;
        this.start_date = start_date;
        this.start_time = start_time;
        this.end_date = end_date;
        this.end_time = end_time;
        this.description = description;
    }

    // Constructor excluding id (auto-incremented) value -used for inserting data
    public MeetingRequest(String sender_name, String sender_email, String receiver_name,
                          String receiver_email, String status, String title, String start_date,
                          String start_time, String end_date, String end_time, String description) {
        this.sender_name = sender_name;
        this.sender_email = sender_email;
        this.receiver_name = receiver_name;
        this.receiver_email = receiver_email;
        this.status = status;
        this.title = title;
        this.start_date = start_date;
        this.start_time = start_time;
        this.end_date = end_date;
        this.end_time = end_time;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_email() {
        return sender_email;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiver_email() {
        return receiver_email;
    }

    public void setReceiver_email(String receiver_email) {
        this.receiver_email = receiver_email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    // End of getters and setters
}
