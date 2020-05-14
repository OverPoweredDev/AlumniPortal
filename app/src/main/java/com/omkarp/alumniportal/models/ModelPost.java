package com.omkarp.alumniportal.models;

public class ModelPost {
    String title, description, timeStamp, userProfilePicture, username, postImage;
    int voteCount;

    public ModelPost() {
    }

    public ModelPost(String title, String description, String timeStamp, String userProfilePicture, String username, String postImage, int voteCount) {
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
        this.userProfilePicture = userProfilePicture;
        this.username = username;
        this.postImage = postImage;
        this.voteCount = voteCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
