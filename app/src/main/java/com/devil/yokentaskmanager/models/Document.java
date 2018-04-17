package com.devil.yokentaskmanager.models;

public class Document {
    private String name;
    private String userID;
    private String userName;
    private String downloadURL;
    private String type;

    public Document(String name, String userID, String userName, String downloadURL, String type, String timeStamp, String key) {
        this.name = name;
        this.userID = userID;
        this.userName = userName;
        this.downloadURL = downloadURL;
        this.type = type;
        this.timeStamp = timeStamp;
        this.key = key;
    }

    private String timeStamp;
    private String key;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getKey() {
        return key;
    }

}
