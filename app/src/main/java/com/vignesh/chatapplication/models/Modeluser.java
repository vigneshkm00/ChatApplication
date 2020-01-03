package com.vignesh.chatapplication.models;



public class Modeluser {
    String name;
    String email;
    String phone,image,uid;

    public Modeluser() {
    }

    public Modeluser(String name, String email, String phone, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.image = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
