package com.example.a368.ui.friends;

public class FriendRequest {

    private int id;
    private String sender_name;
    private String sender_email;
    private String receiver_name;
    private String receiver_email;
    private String status;

    // Null constructor
    public FriendRequest () {

    }

    // Constructor with id (auto-incremented)
    public FriendRequest(int id, String sender_name, String sender_email, String receiver_name, String receiver_email, String status) {
        this.id = id;
        this.sender_name = sender_name;
        this.sender_email = sender_email;
        this.receiver_name = receiver_name;
        this.receiver_email = receiver_email;
        this.status = status;
    }

    // Constructor without id (auto-incremented)
    public FriendRequest(String sender_name, String sender_email, String receiver_name, String receiver_email, String status) {
        this.sender_name = sender_name;
        this.sender_email = sender_email;
        this.receiver_name = receiver_name;
        this.receiver_email = receiver_email;
        this.status = status;
    }

    // Getters and setters
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
    // End of getters and setters
}
