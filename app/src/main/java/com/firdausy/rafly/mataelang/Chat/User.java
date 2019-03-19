package com.firdausy.rafly.mataelang.Chat;

/*
 * Author Trian on 8/12/2018.
 */

public class User {

    private String displayName;
    private String current;
    private String email;
    private String uid;
    private String photoUrl;
    private String instanceId;



    public User() {
    }

    public User(String displayName, String email, String uid, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;

    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
