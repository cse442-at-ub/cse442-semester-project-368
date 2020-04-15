package com.example.a368;


// This class is used to store global variables about the current user.
// get the email address of current user by calling: User.getInstance().getEmail()
public class User {
    private static User instance;
    private String email;
    private String name;

    private User(){}

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return this.email;
    }
    public void setName(String name) {this.name = name;}
    public String getName() {
        return this.name;
    }
    public static synchronized User getInstance(){
        if(instance==null){
            instance=new User();
        }
        return instance;
    }
}
