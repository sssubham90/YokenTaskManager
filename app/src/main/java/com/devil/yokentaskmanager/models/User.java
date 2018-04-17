package com.devil.yokentaskmanager.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email, ID;
    private List<String> tasks;

    public User(String email, String ID) {
        this.email = email;
        this.ID = ID;
        tasks = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public String getID() {
        return ID;
    }

    public List<String> getTasks() {
        return tasks;
    }
}
