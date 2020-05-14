package com.omkarp.alumniportal.models;

public class ModelUser {

    public String email, name, phone, search, profilePicture, uid, yearPassedOut;

    public ModelUser() {

    }

    public ModelUser(String email, String name, String phone, String search, String profilePicture, String uid, String yearPassedOut) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.search = search;
        this.profilePicture = profilePicture;
        this.uid = uid;
        this.yearPassedOut = yearPassedOut;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getYearPassedOut() {
        return yearPassedOut;
    }

    public void setYearPassedOut(String yearPassedOut) {
        this.yearPassedOut = yearPassedOut;
    }
}
