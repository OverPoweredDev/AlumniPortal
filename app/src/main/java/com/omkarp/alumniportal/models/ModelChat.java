package com.omkarp.alumniportal.models;

public class ModelChat {
    String message, reciever, sender, timeStamp;
    boolean isSeen;

    public ModelChat() {
    }

    public ModelChat(String message, String reciever, String sender, String timeStamp, boolean isSeen) {
        this.message = message;
        this.reciever = reciever;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
