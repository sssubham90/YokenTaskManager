package com.devil.yokentaskmanager.models;

public class Task {
    private String ID,Title;

    public Task(String ID, String title) {
        this.ID = ID;
        Title = title;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }
}
