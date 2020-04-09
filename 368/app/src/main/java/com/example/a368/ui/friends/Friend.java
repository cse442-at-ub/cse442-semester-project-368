package com.example.a368.ui.friends;

public class Friend {

    private int id;
    private String name;
    private String email;

    // NULL Constructor
    public Friend() {

    }

    public Friend(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Friend(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
